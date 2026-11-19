import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utilitas: membuat sprite "pintu besi" (iron door) untuk gerbang antar area.
 *
 * Sprite 16x32 piksel (1 tile x 2 tile) dengan gaya gotik gelap yang
 * selaras dengan latar kastil (Aula Manor). Dua varian dihasilkan:
 *
 *   door_iron.png      - pintu tertutup (teralis besi + kunci zamrud)
 *   door_iron_open.png  - pintu terbuka (lorong gelap ke area sebelah)
 *
 * Pemakaian:
 *   java -cp . DoorBuilder <folderTujuan>
 */
public final class DoorBuilder {

    private static final int W = 16;
    private static final int H = 32;

    // Palet selaras dengan bg_manor_hall.png
    private static final int STONE_DARK = 0xFF1C1C24;
    private static final int STONE_MID = 0xFF2A2A34;
    private static final int IRON_DARK = 0xFF2C2E3A;
    private static final int IRON_MID = 0xFF3E4252;
    private static final int IRON_LIGHT = 0xFF585E72;
    private static final int IRON_HI = 0xFF7E8599;
    private static final int GAP = 0xFF0E0E14;

    private DoorBuilder() {
    }

    public static void main(String[] args) throws Exception {
        File dir = new File(args.length > 0 ? args[0] : "data/art");
        if (!dir.isDirectory() && !dir.mkdirs()) {
            System.out.println("Gagal membuat folder " + dir);
            return;
        }
        write(closedDoor(), new File(dir, "door_iron.png"));
        write(openDoor(), new File(dir, "door_iron_open.png"));
        System.out.println("Pintu besi dibuat di " + dir.getAbsolutePath());
    }

    /** Lengkungan gotik: 1 = bukaan, 0 = batu. */
    private static boolean arch(int x, int y) {
        // Lengkungan runcing: lebar penuh di bawah y=8, menyempit ke atas.
        int half = 7;
        if (y >= 9) {
            return x >= 2 && x <= 13;
        }
        int t = 8 - y;                       // 0..8 dari atas
        int shrink = (t * t) / 8;             //_runcing_
        half -= shrink;
        int cx = 7;
        return Math.abs(x - cx) <= half && x >= 2 && x <= 13;
    }

    private static BufferedImage closedDoor() {
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                int c = 0x00000000;
                boolean inArch = arch(x, y);

                if (!inArch) {
                    // Dinding batu mengelilingi bukaan
                    c = ((x + y) % 7 == 0) ? STONE_MID : STONE_DARK;
                } else if (y >= 29) {
                    // Ambang batu lantai
                    c = ((x % 4 == 0) ? STONE_MID : STONE_DARK);
                } else if (x <= 2 || x >= 13) {
                    // Rangka besi sisi
                    c = (x == 2 || x == 13) ? IRON_MID : IRON_DARK;
                } else {
                    // Rongga gelap di balik teralis
                    c = GAP;
                }

                // Teralis besi vertikal
                if (inArch && y >= 7 && y < 29 && (x == 4 || x == 7 || x == 10)) {
                    c = IRON_MID;
                    if (x == 4 || x == 10) c = IRON_LIGHT;
                }
                // Palang horizontal
                if (inArch && (y == 12 || y == 21)) {
                    c = IRON_MID;
                    if (x == 4 || x == 10) c = IRON_LIGHT;
                }
                // Paku/rivet
                if (inArch && (y == 12 || y == 21) && (x == 7)) {
                    c = IRON_HI;
                }
                // Plat kunci besi gelap di tengah (bukan zamrud, supaya tidak
                // bentrok dengan kait pengunci beruas zamrud di Aula Manor).
                if (inArch && y == 16 && x == 7) {
                    c = IRON_LIGHT;
                }
                if (inArch && y == 15 && x == 7) {
                    c = IRON_HI;
                }
                img.setRGB(x, y, c);
            }
        }
        return img;
    }

    private static BufferedImage openDoor() {
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                int c = 0x00000000;
                boolean inArch = arch(x, y);

                if (!inArch) {
                    c = ((x + y) % 7 == 0) ? STONE_MID : STONE_DARK;
                } else if (y >= 29) {
                    c = ((x % 4 == 0) ? STONE_MID : STONE_DARK);
                } else if (x <= 2 || x >= 13) {
                    c = (x == 2 || x == 13) ? IRON_MID : IRON_DARK;
                } else {
                    // Lorong gelap yang terbuka
                    c = GAP;
                    // Cahaya redup dari sisi seberang di bawah
                    if (y >= 25 && (x == 5 || x == 6 || x == 7 || x == 8)) {
                        c = 0xFF1C2030;
                    }
                }
                // Teralis terangkat: hanya sisa di paling atas lengkungan
                if (inArch && y >= 7 && y <= 9 && (x == 4 || x == 7 || x == 10)) {
                    c = IRON_MID;
                }
                img.setRGB(x, y, c);
            }
        }
        return img;
    }

    private static void write(BufferedImage img, File file) throws Exception {
        ImageIO.write(img, "png", file);
        System.out.println("ok " + file.getAbsolutePath() + " (" + img.getWidth() + "x" + img.getHeight() + ")");
    }
}
