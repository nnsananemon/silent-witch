import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

/**
 * Membangun sprite sheet horizontal dari kumpulan frame PNG berukuran besar.
 *
 * Dipakai untuk merapikan paket "Asset Terbaru" (mis. Golden Knight 900x500)
 * menjadi sheet pixel-art kecil dengan tinggi karakter yang seragam.
 *
 * Pemakaian:
 *   java -cp . SpriteSheetBuilder <folderFrame> <keluaran.png> [tinggiKarakter]
 *
 * Keluaran: satu strip horizontal, tiap sel berukuran (cellW x cellH) dengan
 * kaki karakter menempel di dasar sel dan badan tepat di tengah sel. Titik
 * jangkar yang dipakai game: (cellW / 2, cellH).
 */
public final class SpriteSheetBuilder {

    private SpriteSheetBuilder() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Pemakaian: SpriteSheetBuilder <folderFrame> <keluaran.png> [tinggiKarakter]");
            return;
        }
        File dir = new File(args[0]);
        File outFile = new File(args[1]);
        int targetH = args.length > 2 ? Integer.parseInt(args[2]) : 24;

        File[] files = dir.listFiles((d, n) -> n.toLowerCase().endsWith(".png"));
        if (files == null || files.length == 0) {
            System.out.println("Tidak ada PNG di " + dir);
            return;
        }
        Arrays.sort(files);

        BufferedImage[] frames = new BufferedImage[files.length];
        int ux0 = Integer.MAX_VALUE, uy0 = Integer.MAX_VALUE, ux1 = -1, uy1 = -1;
        for (int i = 0; i < files.length; i++) {
            frames[i] = ImageIO.read(files[i]);
            int[] b = bbox(frames[i]);
            if (b == null) {
                System.out.println("Frame kosong: " + files[i].getName());
                return;
            }
            ux0 = Math.min(ux0, b[0]);
            uy0 = Math.min(uy0, b[1]);
            ux1 = Math.max(ux1, b[2]);
            uy1 = Math.max(uy1, b[3]);
        }

        int unionW = ux1 - ux0 + 1;
        int unionH = uy1 - uy0 + 1;
        float scale = targetH / (float) unionH;

        // Jangkar horizontal: pusat massa piksel di 25% baris terbawah
        // (kaki/badan), supaya pedang di kanan tidak menggeser karakter.
        int feetTop = uy1 - Math.max(4, unionH / 4);
        long sumX = 0, count = 0;
        for (BufferedImage img : frames) {
            for (int y = feetTop; y <= uy1; y++) {
                if (y >= img.getHeight()) continue;
                for (int x = 0; x < img.getWidth(); x++) {
                    if (((img.getRGB(x, y) >>> 24) & 0xFF) > 16) {
                        sumX += x;
                        count++;
                    }
                }
            }
        }
        int anchorX = count == 0 ? (ux0 + ux1) / 2 : (int) (sumX / count);

        int leftExtent = Math.max(1, Math.round((anchorX - ux0) * scale));
        int rightExtent = Math.max(1, Math.round((ux1 - anchorX) * scale));
        int half = Math.max(leftExtent, rightExtent);
        int cellW = half * 2 + 2;
        int cellH = Math.max(4, Math.round(unionH * scale)) + 2;

        BufferedImage strip = new BufferedImage(cellW * frames.length, cellH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = strip.createGraphics();
        for (int i = 0; i < frames.length; i++) {
            BufferedImage cell = renderCell(frames[i], ux0, uy0, unionW, unionH,
                    scale, cellW, cellH, anchorX);
            g.drawImage(cell, i * cellW, 0, null);
        }
        g.dispose();

        File parent = outFile.getParentFile();
        if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
            System.out.println("Gagal membuat folder " + parent);
            return;
        }
        ImageIO.write(strip, "png", outFile);

        System.out.println("Sheet: " + outFile.getAbsolutePath());
        System.out.println("  frame=" + frames.length + " cell=" + cellW + "x" + cellH
                + " total=" + strip.getWidth() + "x" + strip.getHeight());
        System.out.println("  sumber union=" + unionW + "x" + unionH + " anchorX=" + anchorX
                + " scale=" + String.format("%.4f", scale));
    }

    /** Potong bingkai union, perkecil dengan rata-rata area, tempel ke sel. */
    private static BufferedImage renderCell(BufferedImage src, int ux0, int uy0,
                                            int unionW, int unionH, float scale,
                                            int cellW, int cellH, int anchorX) {
        BufferedImage cell = new BufferedImage(cellW, cellH, BufferedImage.TYPE_INT_ARGB);
        int dstW = Math.max(1, Math.round(unionW * scale));
        int dstH = Math.max(1, Math.round(unionH * scale));
        int dstX = Math.round(cellW / 2f - (anchorX - ux0) * scale);
        int dstY = cellH - dstH;

        for (int dy = 0; dy < dstH; dy++) {
            int y0 = uy0 + (int) Math.floor(dy / scale);
            int y1 = uy0 + (int) Math.floor((dy + 1) / scale);
            if (y1 <= y0) y1 = y0 + 1;
            for (int dx = 0; dx < dstW; dx++) {
                int x0 = ux0 + (int) Math.floor(dx / scale);
                int x1 = ux0 + (int) Math.floor((dx + 1) / scale);
                if (x1 <= x0) x1 = x0 + 1;

                long a = 0, r = 0, gr = 0, b = 0, n = 0;
                for (int sy = y0; sy < y1; sy++) {
                    if (sy < 0 || sy >= src.getHeight()) continue;
                    for (int sx = x0; sx < x1; sx++) {
                        if (sx < 0 || sx >= src.getWidth()) continue;
                        int c = src.getRGB(sx, sy);
                        int ca = (c >>> 24) & 0xFF;
                        if (ca == 0) {
                            n++;
                            continue;
                        }
                        a += ca;
                        r += ((c >> 16) & 0xFF) * ca;
                        gr += ((c >> 8) & 0xFF) * ca;
                        b += (c & 0xFF) * ca;
                        n++;
                    }
                }
                if (n == 0 || a == 0) continue;
                int outA = (int) (a / n);
                int outR = (int) (r / a);
                int outG = (int) (gr / a);
                int outB = (int) (b / a);
                cell.setRGB(dstX + dx, dstY + dy, (outA << 24) | (outR << 16) | (outG << 8) | outB);
            }
        }
        return cell;
    }

    private static int[] bbox(BufferedImage img) {
        int x0 = Integer.MAX_VALUE, y0 = Integer.MAX_VALUE, x1 = -1, y1 = -1;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (((img.getRGB(x, y) >>> 24) & 0xFF) > 16) {
                    if (x < x0) x0 = x;
                    if (y < y0) y0 = y;
                    if (x > x1) x1 = x;
                    if (y > y1) y1 = y;
                }
            }
        }
        return x1 < 0 ? null : new int[]{x0, y0, x1, y1};
    }
}
