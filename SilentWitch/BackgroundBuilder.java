import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utilitas: mengubah gambar besar (mis. render 1983x793) menjadi ukuran peta
 * game (mis. 640x256) dengan penskalaan rata-rata area, supaya detail tetap
 * terbaca sebagai pixel art dan tidak muncul garis interpolasi.
 *
 * Pemakaian:
 *   java -cp . BackgroundBuilder <sumber.png> <keluaran.png> <lebar> <tinggi>
 */
public final class BackgroundBuilder {

    private BackgroundBuilder() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Pemakaian: BackgroundBuilder <sumber> <keluaran> <lebar> <tinggi>");
            return;
        }
        File src = new File(args[0]);
        File out = new File(args[1]);
        int targetW = Integer.parseInt(args[2]);
        int targetH = Integer.parseInt(args[3]);

        BufferedImage img = ImageIO.read(src);
        if (img == null) {
            System.out.println("Gagal membaca: " + src.getAbsolutePath());
            return;
        }

        BufferedImage scaled = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
        float scaleX = (float) img.getWidth() / targetW;
        float scaleY = (float) img.getHeight() / targetH;

        for (int y = 0; y < targetH; y++) {
            int y0 = (int) Math.floor(y * scaleY);
            int y1 = (int) Math.ceil((y + 1) * scaleY);
            if (y1 <= y0) y1 = y0 + 1;
            if (y1 > img.getHeight()) y1 = img.getHeight();
            for (int x = 0; x < targetW; x++) {
                int x0 = (int) Math.floor(x * scaleX);
                int x1 = (int) Math.ceil((x + 1) * scaleX);
                if (x1 <= x0) x1 = x0 + 1;
                if (x1 > img.getWidth()) x1 = img.getWidth();

                long r = 0, gr = 0, b = 0;
                int n = 0;
                for (int sy = y0; sy < y1; sy++) {
                    for (int sx = x0; sx < x1; sx++) {
                        int c = img.getRGB(sx, sy);
                        r += (c >> 16) & 0xFF;
                        gr += (c >> 8) & 0xFF;
                        b += c & 0xFF;
                        n++;
                    }
                }
                if (n == 0) n = 1;
                int rr = (int) (r / n);
                int gg = (int) (gr / n);
                int bb = (int) (b / n);
                scaled.setRGB(x, y, (rr << 16) | (gg << 8) | bb);
            }
        }

        File parent = out.getParentFile();
        if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
            System.out.println("Gagal membuat folder " + parent);
            return;
        }
        ImageIO.write(scaled, "png", out);
        System.out.println("Background: " + out.getAbsolutePath()
                + "  " + targetW + "x" + targetH
                + "  (dari " + img.getWidth() + "x" + img.getHeight() + ")");
    }
}
