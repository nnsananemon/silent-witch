import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utilitas untuk mem-build dan menyelaraskan sprite NPC dari folder sumber
 * (C:\Users\runneradmin\Desktop\npc_talk\data\sprites) ke dalam folder aset game
 * (data/art) dengan skala pixel art native dan grading palet yang menyatu
 * dengan atmosfer latar belakang game (The Silent Manor & Ashen Village).
 */
public final class NpcSpriteBuilder {

    private NpcSpriteBuilder() {}

    public static BufferedImage downscaleToPixelArt(BufferedImage src, int targetH) {
        int targetW = Math.round(targetH * src.getWidth() / (float) src.getHeight());
        BufferedImage scaled = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(src, 0, 0, targetW, targetH, null);
        g2.dispose();

        // Bersihkan alpha threshold agar menjadi pixel art tajam (tanpa halo buram)
        for (int y = 0; y < targetH; y++) {
            for (int x = 0; x < targetW; x++) {
                int c = scaled.getRGB(x, y);
                int a = (c >>> 24) & 0xFF;
                if (a < 70) {
                    scaled.setRGB(x, y, 0);
                } else {
                    scaled.setRGB(x, y, (0xFF << 24) | (c & 0x00FFFFFF));
                }
            }
        }
        return scaled;
    }

    /**
     * Color-grade Kakek Pustakawan agar menyatu dengan pencahayaan dan palet Aula Manor:
     * - Aula Manor: gothic slate/purple gelap dengan cahaya lilin hangat.
     * - Rambut/janggut: perak kelabu akademisi yang bermartabat (bukan putih neon mencolok).
     * - Jubah: beludru navy/indigo gelap sarjana dengan sulaman emas antik.
     * - Buku/naskah: kulit tua dan perkamen kusam.
     */
    public static BufferedImage gradeLibrarian(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int c = src.getRGB(x, y);
                int a = (c >>> 24) & 0xFF;
                if (a == 0) {
                    out.setRGB(x, y, 0);
                    continue;
                }
                int r = (c >> 16) & 0xFF;
                int g = (c >> 8) & 0xFF;
                int b = c & 0xFF;

                float lum = (r * 0.299f + g * 0.587f + b * 0.114f) / 255f;
                float sat = max(r, g, b) == 0 ? 0 : (max(r, g, b) - min(r, g, b)) / (float) max(r, g, b);

                int nr, ng, nb;

                // 1. Garis tepi gelap (outline gothic bernuansa ungu slate)
                if (lum < 0.20f) {
                    nr = Math.round(r * 0.38f + 16);
                    ng = Math.round(g * 0.35f + 12);
                    nb = Math.round(b * 0.40f + 22);
                }
                // 2. Rambut & janggut putih: remap ke perak kelabu sarjana + ambient hangat lilin
                else if (sat < 0.18f && lum > 0.42f) {
                    float factor = 0.54f + lum * 0.28f;
                    nr = Math.round(208 * factor);
                    ng = Math.round(196 * factor);
                    nb = Math.round(202 * factor);
                }
                // 3. Kulit wajah / tangan: warna kulit hangat diterangi temaram lilin
                else if (r > g && g > b && sat > 0.18f && lum > 0.32f && lum < 0.88f && (r - b) > 28) {
                    float factor = 0.62f + lum * 0.26f;
                    nr = Math.round(202 * factor);
                    ng = Math.round(156 * factor);
                    nb = Math.round(132 * factor);
                }
                // 4. Jubah: beludru indigo/navy aula manor
                else if (b > r - 12 && (b > g || r > g)) {
                    float factor = 0.42f + lum * 0.40f;
                    nr = Math.round(58 * factor);
                    ng = Math.round(68 * factor);
                    nb = Math.round(102 * factor);
                }
                // 5. Sulaman emas / buku naskah kulit
                else if (r > b && g > b && r > 95) {
                    float factor = 0.52f + lum * 0.34f;
                    nr = Math.round(186 * factor);
                    ng = Math.round(148 * factor);
                    nb = Math.round(76 * factor);
                }
                // 6. Bagian lainnya dengan ambient tinting manor
                else {
                    float factor = 0.64f;
                    nr = Math.round(r * factor * 0.94f + 14);
                    ng = Math.round(g * factor * 0.90f + 10);
                    nb = Math.round(b * factor * 1.06f + 18);
                }

                // Falloff vertikal: bagian bawah tubuh sedikit lebih gelap menyatu dengan bayangan lantai
                float verticalShade = 1.02f - (y / (float) h) * 0.18f;
                nr = clamp((int) (nr * verticalShade));
                ng = clamp((int) (ng * verticalShade));
                nb = clamp((int) (nb * verticalShade));

                out.setRGB(x, y, (0xFF << 24) | (nr << 16) | (ng << 8) | nb);
            }
        }
        return out;
    }

    /**
     * Color-grade warga desa agar menyatu dengan latar Ashen Village:
     * - Ashen Village: malam berkabut, jalan batu basah, dan obor desa.
     * - Pakaian putih/linen: kain tenun pedesaan kusam bernuansa malam berkabut (bukan putih bersih siang hari).
     * - Rompi/gaun: cokelat tanah pedesaan yang alami.
     */
    public static BufferedImage gradeVillager(BufferedImage src, boolean isBoy) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int c = src.getRGB(x, y);
                int a = (c >>> 24) & 0xFF;
                if (a == 0) {
                    out.setRGB(x, y, 0);
                    continue;
                }
                int r = (c >> 16) & 0xFF;
                int g = (c >> 8) & 0xFF;
                int b = c & 0xFF;

                float lum = (r * 0.299f + g * 0.587f + b * 0.114f) / 255f;
                float sat = max(r, g, b) == 0 ? 0 : (max(r, g, b) - min(r, g, b)) / (float) max(r, g, b);

                int nr, ng, nb;

                // 1. Garis tepi gelap desa berkabut
                if (lum < 0.20f) {
                    nr = Math.round(r * 0.38f + 14);
                    ng = Math.round(g * 0.38f + 14);
                    nb = Math.round(b * 0.40f + 18);
                }
                // 2. Kain putih (kemeja pemuda / tudung & celemek gadis): linen desa kusam di bawah langit malam
                else if (sat < 0.22f && lum > 0.46f) {
                    float factor = 0.50f + lum * 0.24f;
                    nr = Math.round(176 * factor);
                    ng = Math.round(170 * factor);
                    nb = Math.round(168 * factor);
                }
                // 3. Kulit: pencahayaan lembut malam berkabut
                else if (r > g && g > b && sat > 0.18f && lum > 0.30f && lum < 0.88f && (r - b) > 22) {
                    float factor = 0.58f + lum * 0.25f;
                    nr = Math.round(194 * factor);
                    ng = Math.round(152 * factor);
                    nb = Math.round(128 * factor);
                }
                // 4. Rompi / rambut / gaun: warna tanah dan kayu desa
                else if (r > b && g > b) {
                    float factor = 0.50f + lum * 0.28f;
                    nr = Math.round(r * factor);
                    ng = Math.round(g * factor);
                    nb = Math.round(b * factor);
                }
                // 5. Bagian lainnya
                else {
                    float factor = 0.60f;
                    nr = Math.round(r * factor + 10);
                    ng = Math.round(g * factor + 10);
                    nb = Math.round(b * factor + 14);
                }

                // Falloff vertikal
                float verticalShade = 1.02f - (y / (float) h) * 0.16f;
                nr = clamp((int) (nr * verticalShade));
                ng = clamp((int) (ng * verticalShade));
                nb = clamp((int) (nb * verticalShade));

                out.setRGB(x, y, (0xFF << 24) | (nr << 16) | (ng << 8) | nb);
            }
        }
        return out;
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
    private static int max(int a, int b, int c) {
        return Math.max(a, Math.max(b, c));
    }
    private static int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }

    public static void main(String[] args) throws Exception {
        File srcDir = new File(args.length > 0 ? args[0] : "C:/Users/runneradmin/Desktop/npc_talk/data/sprites");
        File dstDir = new File(args.length > 1 ? args[1] : "data/art");

        if (!srcDir.isDirectory()) {
            System.err.println("Folder sumber tidak ditemukan: " + srcDir.getAbsolutePath());
            return;
        }
        if (!dstDir.isDirectory()) {
            dstDir.mkdirs();
        }

        System.out.println("Building and color-grading NPC sprites...");
        System.out.println("Source: " + srcDir.getAbsolutePath());
        System.out.println("Destination: " + dstDir.getAbsolutePath());

        // 1. Elder (4 directions, target height = 32)
        String[] elderFiles = {"elder_front.png", "elder_left.png", "elder_back.png", "elder_right.png"};
        for (String fn : elderFiles) {
            File f = new File(srcDir, fn);
            if (!f.exists()) {
                System.err.println("File tidak ditemukan: " + f.getAbsolutePath());
                continue;
            }
            BufferedImage raw = ImageIO.read(f);
            BufferedImage pixelArt = downscaleToPixelArt(raw, 32);
            BufferedImage graded = gradeLibrarian(pixelArt);
            File out = new File(dstDir, fn);
            ImageIO.write(graded, "png", out);
            System.out.println("  Built: " + out.getName() + " (" + graded.getWidth() + "x" + graded.getHeight() + ")");
        }

        // 2. Boy (target height = 29)
        File fBoy = new File(srcDir, "boy_front.png");
        if (fBoy.exists()) {
            BufferedImage raw = ImageIO.read(fBoy);
            BufferedImage pixelArt = downscaleToPixelArt(raw, 29);
            BufferedImage graded = gradeVillager(pixelArt, true);
            File out = new File(dstDir, "boy_front.png");
            ImageIO.write(graded, "png", out);
            System.out.println("  Built: " + out.getName() + " (" + graded.getWidth() + "x" + graded.getHeight() + ")");
        }

        // 3. Girl (target height = 29)
        File fGirl = new File(srcDir, "girl_front.png");
        if (fGirl.exists()) {
            BufferedImage raw = ImageIO.read(fGirl);
            BufferedImage pixelArt = downscaleToPixelArt(raw, 29);
            BufferedImage graded = gradeVillager(pixelArt, false);
            File out = new File(dstDir, "girl_front.png");
            ImageIO.write(graded, "png", out);
            System.out.println("  Built: " + out.getName() + " (" + graded.getWidth() + "x" + graded.getHeight() + ")");
        }

        System.out.println("Selesai mem-build seluruh sprite NPC yang menyatu dengan latar belakang!");
    }
}
