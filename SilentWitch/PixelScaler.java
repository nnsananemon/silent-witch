import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitas: menyiapkan gambar latar pixel art untuk area game.
 *
 * Berbeda dengan {@link BackgroundBuilder} yang memakai penskalaan rata-rata
 * (cocok untuk render besar yang berbasis block), util ini cocok untuk
 * pixel art yang detail:
 *
 * 1) Sumber di-crop dulu ke rasio aspek peta tujuan, jadi gambar TIDAK
 *    gepeng (terhimpit). Area yang dipakai bisa digeser lewat argumen
 *    "geser" (0.0 = kiri, 0.5 = tengah, 1.0 = kanan).
 * 2) Penurunan ukuran memakai mode-color per blok: warna yang paling sering
 *    muncul di dalam blok sumber dipakai apa adanya, sehingga bidang datar
 *    tetap tajam (tidak blur) seperti pixel art aslinya. Kalau tidak ada
 *    warna dominan, rata-rata dipakai supaya gradasi tetap halus.
 *
 * Pemakaian:
 *   java -cp . PixelScaler &lt;sumber.png&gt; &lt;keluaran.png&gt; &lt;lebar&gt; &lt;tinggi&gt; [geser]
 */
public final class PixelScaler {

    private PixelScaler() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Pemakaian: PixelScaler <sumber> <keluaran> <lebar> <tinggi> [geser]");
            return;
        }
        File src = new File(args[0]);
        File dst = new File(args[1]);
        int targetW = Integer.parseInt(args[2]);
        int targetH = Integer.parseInt(args[3]);
        double shift = args.length > 4 ? Double.parseDouble(args[4]) : 0.5;

        BufferedImage img = ImageIO.read(src);
        if (img == null) {
            System.out.println("Gagal membaca: " + src.getAbsolutePath());
            return;
        }

        int sw = img.getWidth();
        int sh = img.getHeight();
        double srcAspect = (double) sw / sh;
        double dstAspect = (double) targetW / targetH;

        int cw = sw;
        int ch = sh;
        int ox = 0;
        int oy = 0;

        if (srcAspect > dstAspect) {
            // sumber lebih lebar -> potong lebar, tinggi penuh
            cw = (int) Math.round(sh * dstAspect);
            ox = (int) Math.round((sw - cw) * shift);
        } else if (srcAspect < dstAspect) {
            // sumber lebih tinggi -> potong tinggi, lebar penuh
            ch = (int) Math.round(sw / dstAspect);
            oy = (int) Math.round((sh - ch) * shift);
        }
        System.out.println("Crop: " + cw + "x" + ch + " @ (" + ox + "," + oy + ") dari " + sw + "x" + sh);

        BufferedImage scaled = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
        double scaleX = (double) cw / targetW;
        double scaleY = (double) ch / targetH;

        for (int y = 0; y < targetH; y++) {
            int y0 = oy + (int) Math.floor(y * scaleY);
            int y1 = oy + (int) Math.ceil((y + 1) * scaleY);
            if (y1 <= y0) y1 = y0 + 1;
            if (y1 > oy + ch) y1 = oy + ch;
            for (int x = 0; x < targetW; x++) {
                int x0 = ox + (int) Math.floor(x * scaleX);
                int x1 = ox + (int) Math.ceil((x + 1) * scaleX);
                if (x1 <= x0) x1 = x0 + 1;
                if (x1 > ox + cw) x1 = ox + cw;

                Map<Integer, Integer> freq = new HashMap<>();
                long r = 0, g = 0, b = 0;
                int n = 0;
                for (int sy = y0; sy < y1; sy++) {
                    for (int sx = x0; sx < x1; sx++) {
                        int c = img.getRGB(sx, sy) & 0xFFFFFF;
                        freq.merge(c, 1, Integer::sum);
                        r += (c >> 16) & 0xFF;
                        g += (c >> 8) & 0xFF;
                        b += c & 0xFF;
                        n++;
                    }
                }

                int best = 0;
                int bestCount = -1;
                for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
                    if (e.getValue() > bestCount) {
                        bestCount = e.getValue();
                        best = e.getKey();
                    }
                }

                int rr, gg, bb;
                if (n > 0 && bestCount > n * 0.35) {
                    rr = (best >> 16) & 0xFF;
                    gg = (best >> 8) & 0xFF;
                    bb = best & 0xFF;
                } else {
                    rr = (int) (r / Math.max(1, n));
                    gg = (int) (g / Math.max(1, n));
                    bb = (int) (b / Math.max(1, n));
                }
                scaled.setRGB(x, y, (rr << 16) | (gg << 8) | bb);
            }
        }

        File parent = dst.getParentFile();
        if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
            System.out.println("Gagal membuat folder " + parent);
            return;
        }
        ImageIO.write(scaled, "png", dst);
        System.out.println("Background: " + dst.getAbsolutePath()
                + "  " + targetW + "x" + targetH
                + "  (dari " + sw + "x" + sh + ")");
    }
}
