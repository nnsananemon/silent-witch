import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tool offline: slice Lembar Sprite Petualang (latar hitam solid, 4 kolom x 2 baris)
 * menjadi sprite NPC 4-arah untuk data/art.
 *
 * Mode:
 *   probe <sheet>                  - analisa warna gelap & skala blok pixel art
 *   crop  <sheet> <outDir>         - simpan 8 sel mentah (untuk verifikasi arah)
 *   build <sheet> <outDir> <h> <boyMap> <girlMap>
 *        boyMap/girlMap = indeks kolom urut front,left,back,right (contoh "0,1,2,3")
 */
public class SheetTool {

    static final int COLS = 4;
    static final int ROWS = 2;
    static final String[] COL_NAMES = {"front", "left", "back", "right"};
    static final String[] ROW_NAMES = {"boy", "girl"};

    public static void main(String[] args) throws Exception {
        String mode = args[0];
        BufferedImage sheet = ImageIO.read(new File(args[1]));
        int cw = sheet.getWidth() / COLS;
        int ch = sheet.getHeight() / ROWS;
        System.out.println("sheet " + sheet.getWidth() + "x" + sheet.getHeight()
                + " cell " + cw + "x" + ch);

        if ("probe".equals(mode)) {
            probe(sheet, cw, ch);
        } else if ("crop".equals(mode)) {
            crop(sheet, cw, ch, new File(args[2]));
        } else if ("build".equals(mode)) {
            File outDir = new File(args[2]);
            int targetH = Integer.parseInt(args[3]);
            int[] boyMap = parseMap(args[4]);
            int[] girlMap = parseMap(args[5]);
            boolean grade = args.length > 6 && "grade".equalsIgnoreCase(args[6]);
            build(sheet, cw, ch, outDir, targetH, boyMap, girlMap, grade);
        } else if ("build-sage".equals(mode)) {
            File outDir = new File(args[2]);
            int targetH = Integer.parseInt(args[3]);
            buildSage(sheet, outDir, targetH);
        } else {
            System.err.println("mode tidak dikenal: " + mode);
            System.exit(1);
        }
    }

    static int[] parseMap(String s) {
        String[] parts = s.split(",");
        int[] m = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            m[i] = Integer.parseInt(parts[i].trim());
        }
        if (m.length != 4) {
            throw new IllegalArgumentException("map harus 4 indeks kolom");
        }
        return m;
    }

    // ------------------------------------------------------------------
    // probe
    // ------------------------------------------------------------------

    static void probe(BufferedImage sheet, int cw, int ch) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int x0 = col * cw, y0 = row * ch;
                // histogram warna gelap
                HashMap<Integer, Integer> dark = new HashMap<Integer, Integer>();
                int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = -1, maxY = -1;
                for (int y = 0; y < ch; y++) {
                    for (int x = 0; x < cw; x++) {
                        int c = sheet.getRGB(x0 + x, y0 + y);
                        int r = (c >> 16) & 0xFF, g = (c >> 8) & 0xFF, b = c & 0xFF;
                        int lum = (r + g + b) / 3;
                        if (lum < 60) {
                            Integer k = c & 0xFFFFFF;
                            Integer v = dark.get(k);
                            dark.put(k, v == null ? 1 : v + 1);
                        }
                        if (Math.max(r, Math.max(g, b)) > 8) {
                            if (x < minX) minX = x;
                            if (x > maxX) maxX = x;
                            if (y < minY) minY = y;
                            if (y > maxY) maxY = y;
                        }
                    }
                }
                System.out.println("cell[" + row + "][" + col + "] contentBbox=(" + minX + ","
                        + minY + ")-(" + maxX + "," + maxY + ") " + (maxX - minX + 1) + "x"
                        + (maxY - minY + 1));
                if (row == 0 && col == 0 || row == 1 && col == 0) {
                    List<Map.Entry<Integer, Integer>> top =
                            new ArrayList<Map.Entry<Integer, Integer>>(dark.entrySet());
                    Collections.sort(top, new Comparator<Map.Entry<Integer, Integer>>() {
                        public int compare(Map.Entry<Integer, Integer> a, Map.Entry<Integer, Integer> b) {
                            return b.getValue() - a.getValue();
                        }
                    });
                    System.out.println("  warna gelap teratas:");
                    for (int i = 0; i < Math.min(15, top.size()); i++) {
                        int rgb = top.get(i).getKey();
                        System.out.println(String.format("    #%06X count=%d", rgb, top.get(i).getValue()));
                    }
                    // histogram run-length lintasan isi (indikasi ukuran blok pixel art)
                    HashMap<Integer, Integer> runs = new HashMap<Integer, Integer>();
                    for (int y = minY + 5; y <= maxY - 5; y += 3) {
                        int run = 1;
                        int prev = sheet.getRGB(x0 + minX, y0 + y);
                        for (int x = minX + 1; x <= maxX; x++) {
                            int cur = sheet.getRGB(x0 + x, y0 + y);
                            if (cur == prev && run < 200) {
                                run++;
                            } else {
                                if (run >= 2) {
                                    Integer v = runs.get(run);
                                    runs.put(run, v == null ? 1 : v + 1);
                                }
                                run = 1;
                                prev = cur;
                            }
                        }
                    }
                    List<Map.Entry<Integer, Integer>> rr =
                            new ArrayList<Map.Entry<Integer, Integer>>(runs.entrySet());
                    Collections.sort(rr, new Comparator<Map.Entry<Integer, Integer>>() {
                        public int compare(Map.Entry<Integer, Integer> a, Map.Entry<Integer, Integer> b) {
                            return b.getValue() - a.getValue();
                        }
                    });
                    System.out.println("  run-length teratas (indikasi skala blok):");
                    for (int i = 0; i < Math.min(10, rr.size()); i++) {
                        System.out.println("    len=" + rr.get(i).getKey()
                                + " count=" + rr.get(i).getValue());
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // crop (verifikasi arah)
    // ------------------------------------------------------------------

    static void crop(BufferedImage sheet, int cw, int ch, File outDir) throws Exception {
        if (!outDir.isDirectory()) {
            outDir.mkdirs();
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                BufferedImage cell = sheet.getSubimage(col * cw, row * ch, cw, ch);
                File out = new File(outDir, ROW_NAMES[row] + "_col" + col + ".png");
                ImageIO.write(cell, "png", out);
                System.out.println("saved " + out.getAbsolutePath());
            }
        }
    }

    // ------------------------------------------------------------------
    // build
    // ------------------------------------------------------------------

    static void build(BufferedImage sheet, int cw, int ch, File outDir,
                      int targetH, int[] boyMap, int[] girlMap, boolean grade) throws Exception {
        if (!outDir.isDirectory()) {
            outDir.mkdirs();
        }
        int[][] maps = {boyMap, girlMap};
        for (int row = 0; row < ROWS; row++) {
            // 1. Hapus latar tiap sel arah milik baris ini
            BufferedImage[] cells = new BufferedImage[4];
            for (int dir = 0; dir < 4; dir++) {
                int col = maps[row][dir];
                BufferedImage cell = sheet.getSubimage(col * cw, row * ch, cw, ch);
                cells[dir] = removeBackground(cell, 8);
            }

            // 2. Y-range gabungan supaya kepala & kaki sejajar di keempat arah
            int y0 = Integer.MAX_VALUE, y1 = -1;
            for (int dir = 0; dir < 4; dir++) {
                int[] yb = contentRows(cells[dir]);
                if (yb == null) {
                    System.err.println("cell kosong: " + ROW_NAMES[row] + " arah " + COL_NAMES[dir]);
                    continue;
                }
                if (yb[0] < y0) y0 = yb[0];
                if (yb[1] > y1) y1 = yb[1];
            }
            if (y1 < 0) {
                continue;
            }

            // 3. Crop per-arah (X bebas agar tiap arah terpusat), downscale, grade
            for (int dir = 0; dir < 4; dir++) {
                int[] xb = contentCols(cells[dir], y0, y1);
                if (xb == null) {
                    System.err.println("cell kosong: " + ROW_NAMES[row] + " arah " + COL_NAMES[dir]);
                    continue;
                }
                BufferedImage cropped = cells[dir].getSubimage(
                        xb[0], y0, xb[1] - xb[0] + 1, y1 - y0 + 1);
                BufferedImage out = downscale(cropped, targetH);
                if (grade) {
                    out = NpcSpriteBuilder.gradeVillager(out, row == 0);
                }
                File f = new File(outDir, ROW_NAMES[row] + "_" + COL_NAMES[dir] + ".png");
                ImageIO.write(out, "png", f);
                System.out.println("built " + f.getName() + " " + out.getWidth() + "x"
                        + out.getHeight() + " (crop " + cropped.getWidth() + "x"
                        + cropped.getHeight() + (grade ? ", graded" : "") + ")");
            }
        }
        preview(outDir);
    }

    /** Memotong lembar sprite Sage Istana Gothic 1x4 kolom menjadi 4-arah (elder_front/left/back/right.png). */
    static void buildSage(BufferedImage sheet, File outDir, int targetH) throws Exception {
        int[][] xRanges = {
            {127, 407},
            {512, 734},
            {853, 1098},
            {1209, 1421}
        };
        // 0: front, 1: right, 2: back, 3: left
        // Urutan arah: front (0), left (3), back (2), right (1)
        int[] dirMap = {0, 3, 2, 1};
        String[] dirNames = {"front", "left", "back", "right"};

        BufferedImage[] cuts = new BufferedImage[4];
        for (int i = 0; i < 4; i++) {
            int x0 = Math.max(0, xRanges[i][0] - 10);
            int x1 = Math.min(sheet.getWidth() - 1, xRanges[i][1] + 10);
            int w = x1 - x0 + 1;
            BufferedImage sub = sheet.getSubimage(x0, 0, w, sheet.getHeight());
            cuts[i] = removeBackground(sub, 10);
        }

        int y0 = Integer.MAX_VALUE, y1 = -1;
        for (int i = 0; i < 4; i++) {
            int[] yb = contentRows(cuts[i]);
            if (yb != null) {
                if (yb[0] < y0) y0 = yb[0];
                if (yb[1] > y1) y1 = yb[1];
            }
        }

        for (int d = 0; d < 4; d++) {
            int figIdx = dirMap[d];
            int[] xb = contentCols(cuts[figIdx], y0, y1);
            BufferedImage cropped = cuts[figIdx].getSubimage(xb[0], y0, xb[1] - xb[0] + 1, y1 - y0 + 1);
            BufferedImage out = downscale(cropped, targetH);
            File f = new File(outDir, "elder_" + dirNames[d] + ".png");
            ImageIO.write(out, "png", f);
            System.out.printf("built %s %dx%d (crop %dx%d)%n",
                    f.getName(), out.getWidth(), out.getHeight(), cropped.getWidth(), cropped.getHeight());
        }
    }

    /** Baris [atas,bawah] yang punya konten (alpha > 0). */
    static int[] contentRows(BufferedImage src) {
        int w = src.getWidth(), h = src.getHeight();
        int top = -1, bot = -1;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (((src.getRGB(x, y) >>> 24) & 0xFF) > 0) {
                    if (top < 0) top = y;
                    bot = y;
                    break;
                }
            }
        }
        return top < 0 ? null : new int[]{top, bot};
    }

    /** Kolom [kiri,kanan] yang punya konten di rentang baris y0..y1. */
    static int[] contentCols(BufferedImage src, int y0, int y1) {
        int w = src.getWidth();
        int left = -1, right = -1;
        for (int x = 0; x < w; x++) {
            for (int y = y0; y <= y1; y++) {
                if (((src.getRGB(x, y) >>> 24) & 0xFF) > 0) {
                    if (left < 0) left = x;
                    right = x;
                    break;
                }
            }
        }
        return left < 0 ? null : new int[]{left, right};
    }

    /** Flood-fill latar hitam dari tepi sel -> transparan. */
    static BufferedImage removeBackground(BufferedImage src, int threshold) {
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = src.getRGB(0, 0, w, h, null, 0, w);
        out.setRGB(0, 0, w, h, pixels, 0, w);
        boolean[][] removed = new boolean[h][w];
        ArrayDeque<int[]> q = new ArrayDeque<int[]>();
        for (int x = 0; x < w; x++) {
            q.add(new int[]{x, 0});
            q.add(new int[]{x, h - 1});
        }
        for (int y = 0; y < h; y++) {
            q.add(new int[]{0, y});
            q.add(new int[]{w - 1, y});
        }
        while (!q.isEmpty()) {
            int[] p = q.poll();
            int x = p[0], y = p[1];
            if (x < 0 || y < 0 || x >= w || y >= h || removed[y][x]) {
                continue;
            }
            int c = out.getRGB(x, y);
            int r = (c >> 16) & 0xFF, g = (c >> 8) & 0xFF, b = c & 0xFF;
            if (Math.max(r, Math.max(g, b)) > threshold) {
                continue;
            }
            removed[y][x] = true;
            out.setRGB(x, y, 0);
            q.add(new int[]{x + 1, y});
            q.add(new int[]{x - 1, y});
            q.add(new int[]{x, y + 1});
            q.add(new int[]{x, y - 1});
        }
        return out;
    }

    /** Potong ke bounding box konten (alpha > 0). */
    static BufferedImage trim(BufferedImage src) {
        int w = src.getWidth(), h = src.getHeight();
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = -1, maxY = -1;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (((src.getRGB(x, y) >>> 24) & 0xFF) > 0) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }
        if (maxX < 0) {
            return null;
        }
        return src.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    /**
     * Downscale ke tinggi target dengan filter area-weighted (box filter)
     * presisi subpixel, diikuti kontras & penajaman outline/mata untuk pixel
     * art tajam, bersih, dan berkarakter (menghilangkan efek blur/becek).
     */
    static BufferedImage downscale(BufferedImage src, int targetH) {
        int sw = src.getWidth(), sh = src.getHeight();
        int targetW = Math.max(1, Math.round(targetH * sw / (float) sh));
        BufferedImage dst = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);

        float blockX = sw / (float) targetW;
        float blockY = sh / (float) targetH;

        for (int dy = 0; dy < targetH; dy++) {
            float sy0 = dy * blockY;
            float sy1 = (dy + 1) * blockY;
            int iy0 = (int) sy0;
            int iy1 = Math.min(sh, (int) Math.ceil(sy1));

            for (int dx = 0; dx < targetW; dx++) {
                float sx0 = dx * blockX;
                float sx1 = (dx + 1) * blockX;
                int ix0 = (int) sx0;
                int ix1 = Math.min(sw, (int) Math.ceil(sx1));

                double totalWeight = 0, sumA = 0, sumR = 0, sumG = 0, sumB = 0;
                for (int y = iy0; y < iy1; y++) {
                    float wy = Math.min(y + 1, sy1) - Math.max(y, sy0);
                    if (wy <= 0) continue;
                    for (int x = ix0; x < ix1; x++) {
                        float wx = Math.min(x + 1, sx1) - Math.max(x, sx0);
                        if (wx <= 0) continue;
                        float w = wx * wy;
                        int c = src.getRGB(x, y);
                        int a = (c >>> 24) & 0xFF;
                        totalWeight += w;
                        sumA += a * w;
                        if (a > 0) {
                            sumR += ((c >> 16) & 0xFF) * w;
                            sumG += ((c >> 8) & 0xFF) * w;
                            sumB += (c & 0xFF) * w;
                        }
                    }
                }
                if (totalWeight > 0) {
                    int a = (int) Math.round(sumA / totalWeight);
                    if (a < 75) {
                        dst.setRGB(dx, dy, 0);
                    } else {
                        double cw = sumA / 255.0;
                        int r = cw > 0 ? (int) Math.round(sumR / cw) : 0;
                        int g = cw > 0 ? (int) Math.round(sumG / cw) : 0;
                        int b = cw > 0 ? (int) Math.round(sumB / cw) : 0;
                        dst.setRGB(dx, dy, (0xFF << 24) | (clamp(r) << 16) | (clamp(g) << 8) | clamp(b));
                    }
                }
            }
        }

        // Penajaman kontras: mata/outline gelap tegas, kulit/kain cerah jelas
        BufferedImage out = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < targetH; y++) {
            for (int x = 0; x < targetW; x++) {
                int c = dst.getRGB(x, y);
                int a = (c >>> 24) & 0xFF;
                if (a == 0) continue;
                int r = (c >> 16) & 0xFF, g = (c >> 8) & 0xFF, b = c & 0xFF;
                float lum = (r * 0.299f + g * 0.587f + b * 0.114f) / 255f;

                int nr, ng, nb;
                if (lum < 0.22f) {
                    // Garis tepi dan mata hitam tegas
                    float f = lum / 0.22f;
                    nr = Math.round(r * 0.70f * f);
                    ng = Math.round(g * 0.70f * f);
                    nb = Math.round(b * 0.70f * f);
                } else if (lum > 0.60f) {
                    // Kulit dan baju cerah/bersih
                    float f = 1.0f + (lum - 0.60f) * 0.30f;
                    nr = Math.min(255, Math.round(r * f));
                    ng = Math.min(255, Math.round(g * f));
                    nb = Math.min(255, Math.round(b * f));
                } else {
                    float f = 1.08f;
                    nr = Math.min(255, Math.round(r * f));
                    ng = Math.min(255, Math.round(g * f));
                    nb = Math.min(255, Math.round(b * f));
                }
                out.setRGB(x, y, (0xFF << 24) | (clamp(nr) << 16) | (clamp(ng) << 8) | clamp(nb));
            }
        }
        return out;
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    /** Preview 4 arah x 2 karakter, diperbesar 6x dengan nearest-neighbor. */
    static void preview(File outDir) throws Exception {
        int scale = 6;
        int cellW = 40 * scale, cellH = 40 * scale;
        BufferedImage strip = new BufferedImage(cellW * 4, cellH * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = strip.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setColor(new java.awt.Color(0x3A3220));
        g.fillRect(0, 0, strip.getWidth(), strip.getHeight());
        for (int row = 0; row < 2; row++) {
            for (int dir = 0; dir < 4; dir++) {
                File f = new File(outDir, ROW_NAMES[row] + "_" + COL_NAMES[dir] + ".png");
                if (!f.isFile()) {
                    continue;
                }
                BufferedImage im = ImageIO.read(f);
                float s = Math.min((cellW - 8) / (float) im.getWidth(),
                        (cellH - 8) / (float) im.getHeight());
                int dw = Math.round(im.getWidth() * s);
                int dh = Math.round(im.getHeight() * s);
                int dx = dir * cellW + (cellW - dw) / 2;
                int dy = row * cellH + (cellH - dh) / 2;
                g.drawImage(im, dx, dy, dw, dh, null);
            }
        }
        g.dispose();
        File out = new File(outDir, "_preview_strip.png");
        ImageIO.write(strip, "png", out);
        System.out.println("preview " + out.getAbsolutePath());
    }
}
