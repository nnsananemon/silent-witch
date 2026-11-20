import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lapisan visual berbasis gambar untuk Silent Witch.
 *
 * Semua sprite dan tekstur tile berasal dari paket gambar di data/art.
 * Gambar dimuat lewat ImageIO agar tetap bisa dipakai saat sketch dirender
 * offscreen (test, preview, AssetExport) tanpa membutuhkan jendela Processing.
 */
public final class Art {

    /**
     * Saklar lapisan tile/properti berbasis gambar (latar, NPC, musuh, prop).
     *
     * Dimatikan karena paket "Asset Terbaru" belum punya tekstur lantai/dinding
     * yang layak: lantai dan dinding memakai potongan tekstur yang sama, jadi
     * latar kehilangan kontras (desa/hutan jadi bintik, kapel/arsip jadi tembok
     * penuh). Karakter utama punya saklar sendiri di {@link #HERO_SPRITE}.
     */
    public static final boolean ENABLED = false;

    /**
     * Karakter utama memakai sprite Golden Knight dari paket "Asset Terbaru".
     *
     * Sheet dibuat ulang lewat {@code SpriteSheetBuilder} (bukan sheet 48x32
     * lama yang pedangnya terpotong di batas sel). Latar, NPC, musuh, dan
     * properti tetap memakai renderer prosedural sampai asetnya dirapikan.
     */
    public static final boolean HERO_SPRITE = true;

    /** Mengaktifkan sprite NPC dari data/art (Librarian Elder, Villager Boy, Villager Girl). */
    public static final boolean NPC_SPRITES = true;

    /** Mengaktifkan sprite penyihir gothic untuk mini-boss Penjaga Jejak. */
    public static final boolean WIZARD_SPRITE = true;

    /** Mengaktifkan sprite flameling untuk musuh reguler Bayangan Hutan. */
    public static final boolean FLAMELING_SPRITE = true;

    /** Jumlah pose badan (idle/walk/run x 3 arah: down/up/samping). */
    private static final int HERO_BODY_FRAMES = 9;

    /** Jumlah frame animasi tebasan. */
    private static final int HERO_ATTACK_FRAMES = 5;

    /** Ukuran satu sel pada sheet wizard gothic (15 frame: 5 idle + 10 fire). */
    private static final int WIZARD_FRAME_W = 81;
    private static final int WIZARD_FRAME_H = 66;
    private static final int WIZARD_IDLE_COUNT = 5;

    /** Ukuran satu sel pada sheet flamelings (3 wujud: kecil, sedang, besar). */
    private static final int FLAMELING_CELL = 128;

    public enum Theme {
        MANOR, FOREST, VILLAGE, CAVE, CHAPEL, ARCHIVE
    }

    private static final int T = 16;

    private static PImage erodaxia;
    private static PImage cave;
    private static PImage flamelings;
    private static PImage bricks;

    private static PImage heroBodySheet;
    private static PImage heroAttackSheet;
    private static PImage heroPortrait;

    private static PImage wizardSheet;
    private static PImage[] wizardFrames;

    /** Tiga wujud flameling (kecil, sedang, besar) setelah latar biru dibuang. */
    private static PImage[] flamelingFrames;

    private static PImage[] heroBodyFrames;
    private static PImage[] heroAttackFrames;
    private static int heroBodyW;
    private static int heroBodyH;
    private static int heroAttackW;
    private static int heroAttackH;
    private static PImage[] spiritFireFrames;
    private static PImage[] shadowFlameFrames;

    private static PImage[] brickFloor;
    private static PImage[] brickWall;
    private static PImage[] stoneFloor;
    private static PImage[] darkStoneFloor;
    private static PImage[] archiveFloor;
    private static PImage[] stoneWall;
    private static PImage[] dirtFloor;
    private static PImage[] grassFloor;
    private static PImage[] forestFloor;
    private static PImage[] leafWall;
    private static PImage[] canopy;
    private static PImage[] caveFloor;
    private static PImage[] caveWall;
    private static PImage waterTile;
    private static PImage lavaTile;
    private static PImage rockTile;
    private static PImage[] rockVariants;
    private static PImage trunkTile;
    private static PImage trunkDark;

    private static final Map<String, PImage> SPRITES = new HashMap<>();
    private static final Map<String, PImage> ICONS = new HashMap<>();
    private static final Map<String, PImage> BACKGROUNDS = new HashMap<>();
    private static boolean loaded = false;

    private Art() {
    }

    // ------------------------------------------------------------------
    // Pemuatan gambar.
    // ------------------------------------------------------------------

    private static synchronized void ensureLoaded() {
        if (loaded) {
            return;
        }
        loaded = true;

        erodaxia = load("erodaxia.png");
        cave = load("cave.png");
        flamelings = load("flamelings.png");
        bricks = load("bricks.png");

        heroBodySheet = load("hero_knight_body.png");
        heroAttackSheet = load("hero_knight_attack.png");
        heroPortrait = load("hero_knight_portrait.png");
        if (heroBodySheet != null) {
            heroBodyW = heroBodySheet.width / HERO_BODY_FRAMES;
            heroBodyH = heroBodySheet.height;
            heroBodyFrames = new PImage[HERO_BODY_FRAMES];
            for (int i = 0; i < HERO_BODY_FRAMES; i++) {
                heroBodyFrames[i] = cut(heroBodySheet, i * heroBodyW, 0, heroBodyW, heroBodyH);
            }
        }
        if (heroAttackSheet != null) {
            heroAttackW = heroAttackSheet.width / HERO_ATTACK_FRAMES;
            heroAttackH = heroAttackSheet.height;
            heroAttackFrames = new PImage[HERO_ATTACK_FRAMES];
            for (int i = 0; i < HERO_ATTACK_FRAMES; i++) {
                heroAttackFrames[i] = cut(heroAttackSheet, i * heroAttackW, 0, heroAttackW, heroAttackH);
            }
        }

        wizardSheet = load("wizard.png");
        if (wizardSheet != null) {
            int total = wizardSheet.width / WIZARD_FRAME_W;
            wizardFrames = new PImage[total];
            for (int i = 0; i < total; i++) {
                wizardFrames[i] = cut(wizardSheet, i * WIZARD_FRAME_W, 0,
                        WIZARD_FRAME_W, WIZARD_FRAME_H);
            }
        }

        if (flamelings != null) {
            // Sheet flamelings berisi tiga wujud terpisah (128x128 per sel) di atas
            // latar biru rata. Buang latar per sel, lalu pangkas ruang kosongnya.
            int count = Math.min(3, flamelings.width / FLAMELING_CELL);
            flamelingFrames = new PImage[count];
            for (int i = 0; i < count; i++) {
                PImage region = flamelings.get(i * FLAMELING_CELL, 0,
                        FLAMELING_CELL, FLAMELING_CELL);
                region.loadPixels();
                int bg = region.pixels.length > 0 ? region.pixels[0] : 0;
                for (int p = 0; p < region.pixels.length; p++) {
                    int c = region.pixels[p];
                    if (((c >>> 24) & 0xFF) == 0) {
                        continue;
                    }
                    int dr = Math.abs(((c >> 16) & 0xFF) - ((bg >> 16) & 0xFF));
                    int dg = Math.abs(((c >> 8) & 0xFF) - ((bg >> 8) & 0xFF));
                    int db = Math.abs((c & 0xFF) - (bg & 0xFF));
                    if (dr < 14 && dg < 14 && db < 14) {
                        region.pixels[p] = 0;
                    }
                }
                region.updatePixels();
                flamelingFrames[i] = trim(region);
            }

            if (flamelingFrames.length > 0) {
                // Api di Rest Point memakai wujud kecil dengan variasi kecerahan
                // sebagai pengganti animasi 12 frame lama.
                PImage small = flamelingFrames[0];
                spiritFireFrames = new PImage[]{
                        small, shade(small, 0.86f), small, shade(small, 0.70f)
                };
                shadowFlameFrames = new PImage[]{
                        shade(small, 0.82f), shade(small, 0.68f), shade(small, 0.90f)
                };
            }
        }

        if (erodaxia == null || cave == null || bricks == null) {
            return;
        }

        // 8 varian bata beragam dari scraps_bricks.png
        int numBricks = Math.min(8, bricks.width / 16);
        brickFloor = new PImage[numBricks];
        brickWall = new PImage[numBricks];
        for (int i = 0; i < numBricks; i++) {
            brickFloor[i] = cut(bricks, i * 16, 0);
            brickWall[i] = shade(brickFloor[i], 0.55f);
        }

        stoneFloor = new PImage[]{
                cut(erodaxia, 30, 275),
                cut(erodaxia, 50, 250),
                cut(erodaxia, 30, 285)
        };
        darkStoneFloor = new PImage[]{
                shade(stoneFloor[0], 0.66f),
                shade(stoneFloor[1], 0.66f),
                shade(stoneFloor[2], 0.66f)
        };
        archiveFloor = new PImage[]{
                shade(brickFloor[0], 0.58f),
                shade(brickFloor[1], 0.58f),
                shade(brickFloor[2], 0.58f)
        };
        stoneWall = new PImage[]{
                cut(erodaxia, 30, 275),
                cut(erodaxia, 50, 250),
                cut(erodaxia, 30, 285)
        };

        dirtFloor = new PImage[]{
                cut(erodaxia, 8, 55),
                cut(erodaxia, 40, 55),
                cut(erodaxia, 80, 55),
                cut(erodaxia, 100, 55)
        };
        grassFloor = new PImage[]{
                cut(erodaxia, 10, 100),
                cut(erodaxia, 40, 100),
                cut(erodaxia, 90, 100)
        };
        forestFloor = new PImage[]{
                cut(erodaxia, 100, 150),
                cut(erodaxia, 120, 170),
                shade(cut(erodaxia, 100, 150), 0.80f)
        };
        leafWall = new PImage[]{
                shade(cut(erodaxia, 140, 50), 0.72f),
                shade(cut(erodaxia, 160, 80), 0.72f),
                shade(cut(erodaxia, 140, 50), 0.55f)
        };
        canopy = new PImage[]{
                shade(cut(erodaxia, 140, 50), 0.92f),
                shade(cut(erodaxia, 160, 80), 0.92f),
                shade(cut(erodaxia, 140, 50), 0.78f)
        };
        caveFloor = new PImage[]{
                shade(cut(cave, 196, 16), 0.55f),
                shade(cut(cave, 212, 16), 0.55f),
                shade(cut(cave, 196, 16), 0.46f),
                shade(cut(cave, 228, 16), 0.52f),
                shade(cut(cave, 196, 32), 0.50f)
        };
        caveWall = new PImage[]{
                cut(cave, 196, 16),
                cut(cave, 212, 16),
                cut(cave, 212, 144),
                cut(cave, 228, 16),
                cut(cave, 196, 32)
        };
        waterTile = cut(cave, 144, 240);
        lavaTile = cut(cave, 10, 310);
        // Bongkahan batu berlumut dari paket Erodaxia, dicondongkan ke abu-abu
        // agar terpisah jelas dari rumput/daun di sekitarnya.
        rockTile = stoneTone(cut(erodaxia, 48, 248, 32, 32), 0.32f);
        rockVariants = new PImage[]{
                rockTile,
                shade(rockTile, 0.82f),
                shade(rockTile, 1.18f)
        };
        // Tekstur kayu gelap untuk batang pohon.
        trunkTile = shade(cut(erodaxia, 8, 55), 0.55f);
        trunkDark = shade(trunkTile, 0.68f);
    }

    private static File dataFile(String name) {
        return new File(dataRoot(), name);
    }

    /**
     * Folder sketch yang dioper oleh Processing (dari {@code app.sketchPath()}).
     *
     * Wajib diisi lewat {@link #attach(PApplet)} karena Processing IDE
     * menjalankan sketch dari folder build sementara di %TEMP% yang tidak
     * memuat folder {@code data/}. Tanpa ini, "data/art" relatif terhadap
     * folder kerja selalu kosong dan sprite karakter utama tidak pernah
     * termuat.
     */
    private static File sketchRoot;

    /**
     * Menyuntikkan lokasi folder sketch ke pemuat aset.
     *
     * Dipanggil sekali dari {@code setup()} sebelum scene pertama digambar.
     */
    public static void attach(PApplet app) {
        if (app == null) {
            return;
        }
        try {
            String path = app.sketchPath();
            if (path != null && path.trim().length() > 0) {
                sketchRoot = new File(path.trim());
            }
        } catch (Throwable ignored) {
            // Tetap lanjut dengan pencarian lewat folder kerja.
        }
    }

    /** Folder sketch yang dipakai untuk mencari data/art (null bila belum diisi). */
    public static File sketchRoot() {
        return sketchRoot;
    }

    private static File dataRoot() {
        for (File dir : candidateRoots()) {
            if (dir.isDirectory()) {
                return dir;
            }
        }
        return new File("data/art");
    }

    /** Daftar kandidat folder aset, dari yang paling tepercaya. */
    private static List<File> candidateRoots() {
        List<File> dirs = new ArrayList<File>();

        // 1. Folder sketch yang benar-benar diberikan Processing.
        addArtDirs(dirs, sketchRoot);

        // 2. Properti sketch.path (dipakai saat export/CLI).
        addArtDirs(dirs, fileFromProperty("sketch.path"));
        addArtDirs(dirs, fileFromProperty("processing.sketch.path"));

        // 3. Folder kerja saat ini dan folder induknya (menutup CLI/test).
        addArtDirs(dirs, new File("").getAbsoluteFile());
        addArtDirs(dirs, fileFromProperty("user.dir"));

        // 4. Folder instalasi project di Desktop (fallback absolut andal).
        File desktopDir = new File(System.getProperty("user.home"), "Desktop/silent-witch/SilentWitch");
        addArtDirs(dirs, desktopDir);

        // 5. Folder tempat class berada (menutup build Processing IDE).
        addArtDirs(dirs, codeSourceDir());

        return dirs;
    }

    /**
     * Menambahkan folder "data/art" milik base beserta beberapa induknya.
     *
     * Naik satu tingkat beberapa kali supaya jalan dari subfolder mana pun di
     * dalam project tetap menemukan aset.
     */
    private static void addArtDirs(List<File> dirs, File base) {
        if (base == null) {
            return;
        }
        File dir = base.getAbsoluteFile();
        for (int i = 0; i < 8 && dir != null; i++) {
            dirs.add(new File(dir, "data/art"));
            dirs.add(new File(dir, "art"));
            dir = dir.getParentFile();
        }
    }

    private static File fileFromProperty(String key) {
        try {
            String value = System.getProperty(key);
            if (value != null && value.trim().length() > 0) {
                return new File(value.trim());
            }
        } catch (Throwable ignored) {
            // Properti tidak tersedia.
        }
        return null;
    }

    private static File codeSourceDir() {
        try {
            File code = new File(Art.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            return code.isDirectory() ? code : code.getParentFile();
        } catch (Exception ignored) {
            return null;
        }
    }

    private static PImage load(String name) {
        File file = dataFile(name);
        try {
            BufferedImage src = ImageIO.read(file);
            if (src == null) {
                System.err.println("Art: gagal membaca " + file.getAbsolutePath());
                return null;
            }
            int w = src.getWidth();
            int h = src.getHeight();
            PImage img = new PImage(w, h, PApplet.ARGB);
            img.loadPixels();
            src.getRGB(0, 0, w, h, img.pixels, 0, w);
            img.updatePixels();
            return img;
        } catch (Exception e) {
            System.err.println("Art: " + file.getAbsolutePath() + " -> " + e.getMessage());
            return null;
        }
    }

    private static PImage cut(PImage src, int x, int y) {
        return cut(src, x, y, T, T);
    }

    private static PImage cut(PImage src, int x, int y, int w, int h) {
        PImage out = src.get(x, y, w, h);
        out.loadPixels();
        return out;
    }

    private static PImage shade(PImage src, float factor) {
        PImage out = src.get();
        out.loadPixels();
        for (int i = 0; i < out.pixels.length; i++) {
            int c = out.pixels[i];
            int a = (c >>> 24) & 0xFF;
            int r = (int) (((c >> 16) & 0xFF) * factor);
            int g = (int) (((c >> 8) & 0xFF) * factor);
            int b = (int) ((c & 0xFF) * factor);
            out.pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
        out.updatePixels();
        return out;
    }

    /** Mengurangi saturasi warna: 0 = abu-abu penuh, 1 = warna asli. */
    private static PImage stoneTone(PImage src, float sat) {
        PImage out = src.get();
        out.loadPixels();
        for (int i = 0; i < out.pixels.length; i++) {
            int c = out.pixels[i];
            int a = (c >>> 24) & 0xFF;
            int r = (c >> 16) & 0xFF;
            int g = (c >> 8) & 0xFF;
            int b = c & 0xFF;
            int lum = (r * 30 + g * 59 + b * 11) / 100;
            r = Math.min(255, lum + (int) ((r - lum) * sat));
            g = Math.min(255, lum + (int) ((g - lum) * sat));
            b = Math.min(255, lum + (int) ((b - lum) * sat));
            out.pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
        out.updatePixels();
        return out;
    }

    private static PImage pick(PImage[] set, int col, int row) {
        if (set == null || set.length == 0) {
            return null;
        }
        return set[Math.floorMod(col * 7 + row * 13, set.length)];
    }

    private static void drawTiled(PGraphics g, PImage[] set, int x, int y, int col, int row) {
        PImage img = pick(set, col, row);
        if (img != null) {
            g.image(img, x, y, T, T);
        }
    }

    // ------------------------------------------------------------------
    // Tile peta.
    // ------------------------------------------------------------------

    public static void drawTile(PGraphics g, char ch, int x, int y, Theme theme,
                                int col, int row) {
        ensureLoaded();
        g.noSmooth();

        // Dasar opak: tekstur sumber punya area transparan, jadi tanpa ini
        // akan terlihat lubang putih/bolong di dalam peta.
        g.noStroke();
        g.fill(baseColor(theme));
        g.rect(x, y, T, T);

        switch (ch) {
            case '#':
                drawTiled(g, wallSet(theme), x, y, col, row);
                break;
            case 'W':
                drawTiled(g, wallSet(theme), x, y, col, row);
                drawWindow(g, x, y);
                break;
            case ',':
                drawRug(g, x, y, col, row);
                break;
            case 'B':
            case 'b':
                drawFloor(g, theme, x, y, col, row);
                drawShelf(g, x, y);
                break;
            case 'T':
            case 'q':
                drawFloor(g, theme, x, y, col, row);
                drawTable(g, x, y);
                break;
            case 'P':
                drawFloor(g, theme, x, y, col, row);
                drawPillar(g, x, y, col, row);
                break;
            case 'G':
            case 'K':
                drawFloor(g, theme, x, y, col, row);
                drawGate(g, x, y);
                break;
            case 'A':
            case 'a':
                drawFloor(g, theme, x, y, col, row);
                drawAltarProp(g, x, y);
                break;
            case 'M':
                drawFloor(g, theme, x, y, col, row);
                drawMemorial(g, x, y);
                break;
            case 't':
                drawFloor(g, theme, x, y, col, row);
                drawTrunk(g, x, y, col, row);
                break;
            case 'l':
                drawFloor(g, theme, x, y, col, row);
                drawLeaves(g, x, y, col, row);
                break;
            case 'g':
                drawGrass(g, x, y, col, row);
                break;
            case 'r':
                drawGrass(g, x, y, col, row);
                drawRock(g, x, y, col, row);
                break;
            case '~':
                drawFloor(g, theme, x, y, col, row);
                drawFog(g, x, y, col, row);
                break;
            case 'F':
                drawPath(g, theme, x, y, col, row);
                break;
            case 'E':
                drawFloor(g, theme, x, y, col, row);
                drawFog(g, x, y, col, row);
                break;
            case 'X':
                drawFloor(g, theme, x, y, col, row);
                drawRuneStone(g, x, y);
                break;
            case 'v':
                drawFloor(g, theme, x, y, col, row);
                drawLeaves(g, x, y, col, row);
                break;
            case 'C':
                drawTiled(g, caveWall, x, y, col, row);
                break;
            case 'd':
                drawPath(g, theme, x, y, col, row);
                break;
            case 'w':
                drawWater(g, x, y, col, row);
                break;
            case 'R':
                drawTiled(g, caveWall, x, y, col, row);
                drawRock(g, x, y, col, row);
                break;
            case 'Z':
                drawFloor(g, theme, x, y, col, row);
                drawFire(g, x + 1, y + 3, 15, 18);
                break;
            case 'H':
                drawTiled(g, stoneWall, x, y, col, row);
                break;
            case 'n':
                drawTiled(g, stoneFloor, x, y, col, row);
                drawPew(g, x, y);
                break;
            case 'o':
                drawTiled(g, stoneFloor, x, y, col, row);
                break;
            case 'm':
                drawTiled(g, stoneFloor, x, y, col, row);
                moss(g, x, y);
                break;
            case 'i':
                drawTiled(g, stoneWall, x, y, col, row);
                drawStainedGlass(g, x, y);
                break;
            case 'Y':
                drawPath(g, theme, x, y, col, row);
                break;
            case 'V':
                drawTiled(g, stoneWall, x, y, col, row);
                break;
            case 'D':
                drawTiled(g, stoneWall, x, y, col, row);
                drawDoor(g, x, y);
                break;
            case 'N':
                drawGrass(g, x, y, col, row);
                drawNoticeBoard(g, x, y);
                break;
            case 'L':
                drawGrass(g, x, y, col, row);
                drawLamp(g, x, y);
                break;
            case 'U':
                drawTiled(g, brickWall, x, y, col, row);
                break;
            case 'p':
            case 'c':
                drawTiled(g, theme == Theme.ARCHIVE ? archiveFloor : brickFloor, x, y, col, row);
                break;
            case 'e':
                drawTiled(g, theme == Theme.ARCHIVE ? archiveFloor : brickFloor, x, y, col, row);
                drawIcon(g, "upg_hammer", x + 2, y + 2, 12, 12);
                break;
            case '.':
            case 'S':
            default:
                drawFloor(g, theme, x, y, col, row);
                break;
        }
    }

    private static int baseColor(Theme theme) {
        switch (theme) {
            case FOREST:
                return 0xFF0E1822;
            case VILLAGE:
                return 0xFF3A3220;
            case CAVE:
                return 0xFF241B15;
            case CHAPEL:
                return 0xFF1E1E24;
            case ARCHIVE:
                return 0xFF17121C;
            case MANOR:
            default:
                return 0xFF1C1424;
        }
    }

    private static PImage[] wallSet(Theme theme) {
        switch (theme) {
            case FOREST:
                return leafWall;
            case CAVE:
                return caveWall;
            case CHAPEL:
            case VILLAGE:
                return stoneWall;
            case ARCHIVE:
            case MANOR:
            default:
                return brickWall;
        }
    }

    private static void drawFloor(PGraphics g, Theme theme, int x, int y, int col, int row) {
        switch (theme) {
            case FOREST:
                drawTiled(g, forestFloor, x, y, col, row);
                break;
            case CAVE:
                drawTiled(g, caveFloor, x, y, col, row);
                break;
            case VILLAGE:
                drawTiled(g, dirtFloor, x, y, col, row);
                break;
            case CHAPEL:
                drawSlabs(g, darkStoneFloor, x, y, col, row, 0xFF24242A);
                break;
            case ARCHIVE:
                drawSlabs(g, archiveFloor, x, y, col, row, 0xFF221A22);
                break;
            case MANOR:
            default:
                drawSlabs(g, darkStoneFloor, x, y, col, row, 0xFF241E26);
                break;
        }
    }

    /**
     * Lantai lempeng batu: dasar gelap, tekstur batu tipis, lalu sambungan tegas.
     * Grid-nya membuat lantai tidak lagi terbaca seperti dinding bata.
     */
    private static void drawSlabs(PGraphics g, PImage[] set, int x, int y,
                                  int col, int row, int base) {
        g.noStroke();
        g.fill(base);
        g.rect(x, y, T, T);
        PImage img = pick(set, col, row);
        if (img != null) {
            g.tint(255, 255, 255, 58);
            g.noSmooth();
            g.image(img, x, y, T, T);
            g.noTint();
        }
        g.noStroke();
        g.fill(0, 0, 0, 120);
        g.rect(x, y, T, 1);
        g.rect(x, y, 1, T);
        g.fill(255, 255, 255, 18);
        g.rect(x + 1, y + 1, T - 1, 1);
        g.rect(x + 1, y + 1, 1, T - 1);
    }

    /** Lantai jalan: tanah di luar ruangan, batu gelap di gua/reruntuhan. */
    private static void drawPath(PGraphics g, Theme theme, int x, int y, int col, int row) {
        if (theme == Theme.CAVE) {
            drawTiled(g, caveFloor, x, y, col, row);
        } else if (theme == Theme.FOREST || theme == Theme.VILLAGE) {
            drawTiled(g, dirtFloor, x, y, col, row);
        } else {
            drawFloor(g, theme, x, y, col, row);
        }
    }

    /** Rumput: dasar daun opak, lalu helai rumput transparan di atasnya. */
    private static void drawGrass(PGraphics g, int x, int y, int col, int row) {
        drawTiled(g, forestFloor, x, y, col, row);
        PImage tuft = pick(grassFloor, col, row);
        if (tuft != null) {
            g.image(tuft, x, y, T, T);
        }
    }

    private static void drawRug(PGraphics g, int x, int y, int col, int row) {
        PImage rug = pick(brickFloor, col, row);
        if (rug != null) {
            g.image(rug, x, y, T, T);
            g.noStroke();
            g.fill(90, 18, 34, 130);
            g.rect(x, y, T, T);
            g.fill(190, 70, 70, 90);
            g.rect(x + 3, y + 3, 2, 2);
            g.rect(x + 11, y + 11, 2, 2);
        }
    }

    private static void drawWindow(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(8, 12, 18);
        g.rect(x + 4, y + 3, 8, 9);
        g.fill(64, 160, 168);
        g.rect(x + 5, y + 4, 6, 7);
        g.fill(180, 236, 240);
        g.rect(x + 5, y + 4, 2, 3);
        g.fill(8, 12, 18);
        g.rect(x + 7, y + 4, 1, 7);
        g.rect(x + 5, y + 7, 6, 1);
    }

    private static void drawShelf(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(35, 20, 16);
        g.rect(x + 1, y, 14, 15);
        g.fill(92, 56, 38);
        g.rect(x + 1, y + 1, 14, 2);
        g.rect(x + 1, y + 7, 14, 2);
        g.rect(x + 1, y + 13, 14, 2);
        int[] books = {0xFFB24A3E, 0xFF3E7AB2, 0xFFC9A24A, 0xFF6B8E4E};
        for (int i = 0; i < 4; i++) {
            g.fill(books[i]);
            g.rect(x + 3 + i * 3, y + 3, 2, 4);
            g.rect(x + 3 + i * 3, y + 9, 2, 4);
        }
    }

    private static void drawTable(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(30, 18, 12, 110);
        g.rect(x + 1, y + 13, 14, 3);
        g.fill(38, 24, 16);
        g.rect(x + 2, y + 12, 3, 4);
        g.rect(x + 11, y + 12, 3, 4);
        g.fill(92, 58, 36);
        g.rect(x + 1, y + 4, 14, 8);
        g.fill(140, 92, 56);
        g.rect(x + 1, y + 4, 14, 2);
        g.fill(70, 42, 26);
        g.rect(x + 2, y + 10, 12, 1);
    }

    private static void drawPillar(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(0, 0, 0, 90);
        g.rect(x + 3, y + 13, 10, 3);
        PImage stone = pick(stoneWall, col, row);
        if (stone != null) {
            g.image(stone, x + 5, y, 7, 15);
        }
        g.fill(150, 144, 120, 70);
        g.rect(x + 5, y, 2, 15);
    }

    private static void drawGate(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(6, 6, 10);
        g.rect(x + 2, y + 2, 12, 14);
        g.fill(92, 74, 40);
        g.rect(x + 1, y, 14, 3);
        g.rect(x + 1, y + 13, 14, 3);
        g.rect(x + 1, y + 2, 2, 12);
        g.rect(x + 13, y + 2, 2, 12);
        g.fill(56, 46, 30);
        for (int i = 0; i < 4; i++) {
            g.rect(x + 4 + i * 3, y + 4, 1, 9);
        }
        g.fill(200, 168, 96);
        g.rect(x + 7, y + 7, 2, 2);
    }

    private static void drawAltarProp(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(0, 0, 0, 80);
        g.rect(x + 2, y + 13, 12, 3);
        g.fill(74, 70, 96);
        g.rect(x + 2, y + 5, 12, 9);
        g.fill(112, 106, 138);
        g.rect(x + 2, y + 5, 12, 2);
        g.fill(196, 154, 78);
        g.rect(x + 7, y + 2, 2, 4);
        g.fill(94, 210, 198, 90);
        g.rect(x + 4, y + 1, 8, 2);
    }

    private static void drawMemorial(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(0, 0, 0, 80);
        g.rect(x + 3, y + 13, 10, 3);
        g.fill(58, 58, 72);
        g.rect(x + 4, y + 4, 8, 10);
        g.fill(88, 86, 106);
        g.rect(x + 4, y + 4, 8, 2);
        g.fill(180, 156, 96);
        g.rect(x + 7, y + 7, 2, 4);
    }

    private static void drawTrunk(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        // Rimbun daun memenuhi petak, dengan serpihan batang di bagian bawah.
        drawTiled(g, canopy, x, y, col, row);
        g.fill(0, 0, 0, 80);
        g.rect(x, y + 12, 16, 4);
        if (trunkDark != null) {
            g.image(trunkDark, x + 5, y + 10, 6, 6);
        }
        g.fill(0, 0, 0, 70);
        g.rect(x + 11, y + 10, 1, 6);
    }

    private static void drawLeaves(PGraphics g, int x, int y, int col, int row) {
        drawTiled(g, leafWall, x, y, col, row);
        g.noStroke();
        g.fill(255, 255, 255, 12);
        g.rect(x, y, 16, 3);
    }

    private static void drawRock(PGraphics g, int x, int y, int col, int row) {
        PImage stone = pick(rockVariants, col, row);
        if (stone == null) {
            return;
        }
        g.noStroke();
        g.fill(0, 0, 0, 110);
        g.ellipse(x + 8, y + 15, 14, 4);

        // Siluet gelap satu piksel lebih besar supaya batu terpisah dari tanah.
        g.fill(16, 20, 16);
        g.rect(x, y + 7, 16, 9);
        g.rect(x + 2, y + 3, 12, 5);
        g.rect(x + 4, y, 8, 4);

        g.image(stone, x + 1, y + 8, 14, 8);
        g.image(stone, x + 3, y + 4, 10, 5);
        g.image(stone, x + 5, y + 1, 6, 4);

        g.fill(226, 232, 224, 70);
        g.rect(x + 4, y + 4, 3, 1);
        g.rect(x + 7, y + 1, 2, 1);
    }

    private static void drawWater(PGraphics g, int x, int y, int col, int row) {
        if (waterTile != null) {
            g.image(waterTile, x, y, T, T);
        }
        g.noStroke();
        g.fill(70, 190, 195, 70);
        int shift = Math.floorMod(col * 3 + row * 5, 7);
        g.rect(x + shift, y + 3, 5, 1);
        g.rect(x + 9 - shift / 2, y + 8, 4, 1);
        g.rect(x + shift / 2, y + 12, 6, 1);
        g.fill(180, 240, 242, 60);
        g.rect(x + shift + 1, y + 3, 2, 1);
        g.rect(x + 9 - shift / 2, y + 8, 1, 1);
    }

    private static void drawFog(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        int drift = Math.floorMod(col * 3 + row * 7, 6);
        g.fill(80, 152, 158, 34);
        g.rect(x, y, 16, 16);
        g.fill(170, 220, 224, 22);
        g.rect(x + drift, y + 3, 10, 3);
        g.rect(x + (drift + 4) % 8, y + 9, 12, 2);
    }

    private static void drawRuneStone(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(0, 0, 0, 90);
        g.rect(x + 2, y + 13, 12, 3);
        drawIcon(g, "upg_shield", x + 2, y + 2, 12, 13);
    }

    /** Api kecil untuk lampu dan tungku: digambar langsung agar tajam di ukuran mungil. */
    public static void drawFire(PGraphics g, int x, int y, int w, int h) {
        g.noStroke();
        int cx = x + w / 2;
        g.fill(255, 150, 40, 90);
        g.ellipse(cx, y + h * 0.72f, w * 1.5f, h * 0.5f);
        g.fill(220, 70, 30);
        g.triangle(cx - w * 0.42f, y + h, cx + w * 0.42f, y + h, cx, y + h * 0.15f);
        g.fill(255, 170, 50);
        g.triangle(cx - w * 0.28f, y + h, cx + w * 0.28f, y + h, cx, y + h * 0.38f);
        g.fill(255, 238, 170);
        g.triangle(cx - w * 0.14f, y + h * 0.92f, cx + w * 0.14f, y + h * 0.92f, cx, y + h * 0.55f);
    }

    /** Roh api dari paket flamelings, dipakai di titik peristirahatan. */
    public static void drawSpiritFire(PGraphics g, int x, int y, int w, int h, float animTime) {
        ensureLoaded();
        if (spiritFireFrames != null && spiritFireFrames.length > 0) {
            int idx = Math.floorMod((int) (animTime * 8f), spiritFireFrames.length);
            PImage fire = spiritFireFrames[idx];
            if (fire != null) {
                g.noSmooth();
                g.image(fire, x, y, w, h);
                return;
            }
        }
        drawFire(g, x, y, w, h);
    }

    public static void drawSpiritFire(PGraphics g, int x, int y, int w, int h) {
        drawSpiritFire(g, x, y, w, h, (System.currentTimeMillis() % 100000) / 1000f);
    }

    private static void drawPew(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(28, 16, 12, 110);
        g.rect(x + 1, y + 13, 14, 3);
        g.fill(66, 38, 26);
        g.rect(x + 1, y + 6, 14, 7);
        g.fill(104, 62, 40);
        g.rect(x + 1, y + 6, 14, 2);
        g.fill(40, 22, 16);
        g.rect(x + 2, y + 13, 2, 3);
        g.rect(x + 12, y + 13, 2, 3);
    }

    private static void moss(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(60, 120, 70, 70);
        g.rect(x, y, 16, 16);
        g.fill(110, 170, 90, 60);
        g.rect(x + 2, y + 2, 5, 3);
        g.rect(x + 9, y + 8, 4, 4);
    }

    private static void drawStainedGlass(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(10, 12, 20);
        g.rect(x + 4, y + 2, 8, 11);
        g.fill(48, 170, 178);
        g.rect(x + 5, y + 3, 3, 4);
        g.fill(214, 170, 68);
        g.rect(x + 8, y + 3, 3, 4);
        g.fill(150, 58, 130);
        g.rect(x + 5, y + 7, 3, 5);
        g.fill(48, 170, 178);
        g.rect(x + 8, y + 7, 3, 5);
        g.fill(10, 12, 20);
        g.rect(x + 7, y + 3, 1, 9);
    }

    private static void drawDoor(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(8, 7, 10);
        g.rect(x + 4, y + 3, 8, 12);
        g.fill(58, 36, 24);
        g.rect(x + 5, y + 4, 6, 11);
        g.fill(140, 108, 62);
        g.rect(x + 9, y + 9, 1, 1);
    }

    private static void drawNoticeBoard(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(0, 0, 0, 100);
        g.rect(x + 2, y + 13, 12, 3);
        g.fill(52, 34, 22);
        g.rect(x + 3, y + 11, 2, 4);
        g.rect(x + 11, y + 11, 2, 4);
        g.fill(94, 62, 38);
        g.rect(x + 1, y + 3, 14, 9);
        g.fill(190, 178, 148);
        g.rect(x + 3, y + 5, 5, 5);
        g.rect(x + 9, y + 5, 4, 3);
        g.fill(70, 44, 28);
        g.rect(x + 3, y + 4, 5, 1);
        g.rect(x + 9, y + 4, 4, 1);
    }

    private static void drawLamp(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(0, 0, 0, 90);
        g.rect(x + 6, y + 15, 5, 2);
        g.fill(44, 40, 34);
        g.rect(x + 7, y + 6, 2, 9);
        g.rect(x + 5, y + 5, 6, 2);
        drawFire(g, x + 4, y - 2, 8, 10);
    }

    // ------------------------------------------------------------------
    // Sprite karakter & properti.
    // ------------------------------------------------------------------

    public static void drawShadow(PGraphics g, int px, int py, int w) {
        g.noStroke();
        g.fill(0, 0, 0, 100);
        g.rect(px - w / 2, py - 1, w, 3);
    }

    public static void drawSprite(PGraphics g, String name, int px, int py,
                                  boolean flip, int tintColor, int alpha) {
        ensureLoaded();
        PImage img = sprite(name);
        if (img == null) {
            return;
        }
        float w = img.width;
        float h = img.height;
        g.noSmooth();
        g.tint((tintColor >> 16) & 0xFF, (tintColor >> 8) & 0xFF, tintColor & 0xFF, alpha);

        if (flip) {
            g.pushMatrix();
            g.translate(px, 0);
            g.scale(-1f, 1f);
            g.image(img, -w / 2f, py - h, w, h);
            g.popMatrix();
        } else {
            g.image(img, px - w / 2f, py - h, w, h);
        }
        g.noTint();
    }

    /** True bila sheet Dark Knight berhasil dimuat dan boleh dipakai. */
    public static boolean heroSpriteReady() {
        ensureLoaded();
        return HERO_SPRITE && heroBodyFrames != null;
    }

    /** True bila sheet wizard gothic berhasil dimuat dan boleh dipakai. */
    public static boolean wizardSpriteReady() {
        ensureLoaded();
        return WIZARD_SPRITE && wizardFrames != null && wizardFrames.length > 0;
    }

    /** True bila sheet flamelings berhasil dimuat dan boleh dipakai. */
    public static boolean flamelingSpriteReady() {
        ensureLoaded();
        return FLAMELING_SPRITE && flamelingFrames != null && flamelingFrames.length > 0;
    }

    /**
     * Menggambar penyihir gothic (mini-boss Penjaga Jejak).
     *
     * Selalu memakai sprite profil samping (frames 11-14) yang menghadap kiri,
     * bukan menghadap ke depan layar.
     *
     * @param frameIndex indeks absolut pada sheet: 11-14 frame menyamping (side view).
     * @param flip       cermin horizontal; art menyamping dasar sudah menghadap kiri.
     */
    public static void drawWizard(PGraphics g, int px, int py, boolean flip,
                                  int frameIndex, int alpha, boolean hurt) {
        drawWizard(g, px, py, flip, frameIndex, alpha, hurt, 0f);
    }

    public static void drawWizard(PGraphics g, int px, int py, boolean flip,
                                  int frameIndex, int alpha, boolean hurt, float animTime) {
        ensureLoaded();
        if (wizardFrames == null || wizardFrames.length == 0) {
            return;
        }

        // Pastikan hanya menggunakan frame profil samping (11-14), bukan badan hadap depan (0-10).
        int idx = frameIndex;
        if (idx < 11) {
            idx = 14;
        }
        idx = Math.max(0, Math.min(idx, wizardFrames.length - 1));
        PImage frame = wizardFrames[idx];
        if (frame == null) {
            return;
        }

        // Ukuran disesuaikan agar tinggi karakter proporsional dengan pemain (~30 px).
        float drawH = 46f;
        float drawW = (WIZARD_FRAME_W / (float) WIZARD_FRAME_H) * drawH; // ~56.45 px

        // Efek melayang/hovering halus khas penyihir gelap
        float bob = animTime > 0f ? (float) (Math.sin(animTime * 4f) * 1.5f) : 0f;
        float topY = py - drawH + 1f + bob;

        // Bayangan di tanah (tetap di permukaan lantai) dan aura magis ungu
        g.noStroke();
        g.fill(0, 0, 0, (int) (95 * (alpha / 255f)));
        g.ellipse(px, py + 1, 18, 5);
        g.fill(120, 40, 150, (int) (26 * (alpha / 255f)));
        g.ellipse(px, py - 15 + bob * 0.5f, 26, 34);

        g.noSmooth();
        if (hurt) {
            g.tint(255, 225, 235, alpha);
        } else if (alpha < 255) {
            g.tint(255, 255, 255, alpha);
        }

        if (flip) {
            g.pushMatrix();
            g.translate(px, 0);
            g.scale(-1f, 1f);
            g.image(frame, -drawW / 2f, topY, drawW, drawH);
            g.popMatrix();
        } else {
            g.image(frame, px - drawW / 2f, topY, drawW, drawH);
        }
        g.noTint();
    }

    /**
     * Menggambar flameling (musuh reguler Bayangan Hutan).
     *
     * @param stage 0 = wujud kecil (tenang), 1 = sedang (mengejar), 2 = besar (telegraf).
     */
    public static void drawFlameling(PGraphics g, int px, int py, int stage,
                                     boolean flip, int alpha, boolean hurt, float animTime) {
        drawFlameling(g, px, py, stage, flip, alpha, hurt, animTime, 0);
    }

    public static void drawFlameling(PGraphics g, int px, int py, int stage,
                                     boolean flip, int alpha, boolean hurt,
                                     float animTime, int extraBob) {
        ensureLoaded();
        if (flamelingFrames == null || flamelingFrames.length == 0) {
            return;
        }

        stage = Math.max(0, Math.min(stage, flamelingFrames.length - 1));
        PImage img = flamelingFrames[stage];
        if (img == null) {
            return;
        }

        // Tinggi target di dunia: kecil 30 px, sedang 48 px, besar 58 px.
        float targetH = stage == 0 ? 30f : (stage == 1 ? 48f : 58f);
        float scale = targetH / img.height;
        float w = img.width * scale;

        float pulse = 1f + 0.035f * (float) Math.sin(animTime * 9f + stage);
        int bob = ((int) (animTime * 5f) & 1) + extraBob;

        // Pendar api di belakang tubuh.
        g.noStroke();
        g.fill(255, 126, 28, (int) (20 * (alpha / 255f)));
        g.ellipse(px, py - targetH * 0.45f + bob, w * 1.35f, targetH * 0.95f);
        g.fill(255, 186, 70, (int) (16 * (alpha / 255f)));
        g.ellipse(px, py - targetH * 0.35f + bob, w * 0.85f, targetH * 0.7f);

        // Bayangan di tanah.
        g.fill(0, 0, 0, (int) (85 * (alpha / 255f)));
        g.rect(px - w * 0.32f, py - 1, w * 0.64f, 3);

        g.noSmooth();
        if (hurt) {
            g.tint(255, 235, 235, alpha);
        } else if (alpha < 255) {
            g.tint(255, 255, 255, alpha);
        }

        float drawW = w * pulse;
        float drawH = targetH * pulse;
        float topY = py - drawH + bob;

        if (flip) {
            g.pushMatrix();
            g.translate(px, 0);
            g.scale(-1f, 1f);
            g.image(img, -drawW / 2f, topY, drawW, drawH);
            g.popMatrix();
        } else {
            g.image(img, px - drawW / 2f, topY, drawW, drawH);
        }
        g.noTint();
    }

    public static void drawHero(PGraphics g, int px, int py, Player.Facing facing,
                                boolean moving, float animTime, boolean attacking, int alpha) {
        drawHero(g, px, py, facing, moving, animTime, attacking, 0f, alpha);
    }

    /**
     * Indeks arah pada sheet badan.
     *
     * Paket "Dark Knight" hanya menyediakan satu art samping (menghadap kiri);
     * berkas "_right" di paket itu masih menghadap kiri, jadi art yang sama
     * dipakai untuk kedua arah dan dicerminkan saat menghadap kanan.
     */
    private static int heroDirIndex(Player.Facing facing) {
        switch (facing) {
            case UP:
                return 1;
            case LEFT:
            case RIGHT:
                return 2;
            default:
                return 0;
        }
    }

    /** Potret tokoh utama (dipakai di pause menu). Mengembalikan true bila tampil. */
    public static boolean drawHeroPortrait(PGraphics g, int x, int bottomY) {
        ensureLoaded();
        if (heroPortrait == null) {
            return false;
        }
        g.noSmooth();
        g.image(heroPortrait, x, bottomY - heroPortrait.height);
        return true;
    }

    /**
     * Menggambar karakter utama (Dark Knight).
     *
     * Pose badan memakai paket "knight_game_assets": idle/walk/run untuk arah
     * depan, belakang, dan samping. Art samping pada paket menghadap kiri,
     * jadi dicerminkan otomatis saat pemain menghadap kanan (art "_right" di
     * paket itu masih menghadap kiri dan tidak dipakai).
     * Serangan memakai 5 frame tebasan yang sudah membawa efek sabit.
     *
     * Paket ini hanya menyediakan 1 pose per arah, jadi langkah kaki disiasati
     * sendiri: baris paling bawah sprite digeser 1 piksel ke kiri-kanan
     * bergantian (gait 2 frame) saat berjalan, sementara saat diam karakter
     * diam di tanah supaya tidak terlihat melayang.
     *
     * @param swingProgress 0..1 selama-menyerang, menentukan frame tebasan.
     */
    public static void drawHero(PGraphics g, int px, int py, Player.Facing facing,
                                boolean moving, float animTime, boolean attacking,
                                float swingProgress, int alpha) {
        drawShadow(g, px, py, 16);

        ensureLoaded();
        if (heroBodyFrames == null) {
            return;
        }

        PImage frame;
        int cellW;
        int cellH;
        boolean flip = facing == Player.Facing.RIGHT;
        float bob = 0f;

        if (attacking && heroAttackFrames != null) {
            // Frame tebasan dari paket (menghadap kiri, dicerminkan ke kanan).
            int idx = (int) (Math.max(0f, Math.min(0.999f, swingProgress))
                    * heroAttackFrames.length);
            frame = heroAttackFrames[idx];
            cellW = heroAttackW;
            cellH = heroAttackH;
        } else if (attacking) {
            // Cadangan kalau art tebasan paket tidak tersedia.
            int idx = 3 + heroDirIndex(facing);
            if (idx >= heroBodyFrames.length) {
                idx = heroBodyFrames.length - 1;
            }
            frame = heroBodyFrames[idx];
            cellW = heroBodyW;
            cellH = heroBodyH;
        } else {
            int action = moving ? 1 : 0;
            int idx = action * 3 + heroDirIndex(facing);
            if (idx >= heroBodyFrames.length) {
                idx = heroBodyFrames.length - 1;
            }
            frame = heroBodyFrames[idx];
            cellW = heroBodyW;
            cellH = heroBodyH;
            if (moving) {
                // Gait 2 frame: selang-seling pose jalan dan pose lari (kaki
                // terlihat melangkah) + badan terangkat 1 piksel saat kaki lewat.
                boolean contact = ((int) (animTime * 8f) & 1) == 0;
                frame = heroBodyFrames[(contact ? 3 : 6) + heroDirIndex(facing)];
                bob = contact ? 0f : -1f;
            }
        }

        if (frame == null) {
            return;
        }

        g.noSmooth();
        // Punggung menghadap kamera saat berjalan ke atas: digelapkan sedikit
        // supaya arah hadap tetap terbaca walau sprite hanya punya sisi kiri/kanan.
        if (facing == Player.Facing.UP) {
            g.tint(150, 150, 170, alpha);
        } else if (alpha < 255) {
            g.tint(255, 255, 255, alpha);
        }

        // +1 piksel: sel sprite punya 2 piksel bantalan bawah, jadi tanpa ini
        // alas kaki melayang 1 piksel di atas garis tanah.
        float dy = py - cellH + 1 + bob;

        if (flip) {
            g.pushMatrix();
            g.translate(px, 0);
            g.scale(-1f, 1f);
            g.image(frame, -cellW / 2f, dy);
            g.popMatrix();
        } else {
            g.image(frame, px - cellW / 2f, dy);
        }

        if (attacking) {
            // Sabit prosedural digambar di DEPAN tokoh, searah tebasan.
            // (Jejak sabit bawaan art paket ada di belakang, jadi efek utama
            // harus digambar sendiri di sisi depan.)
            drawSwordSlash(g, px, py, facing);
        }

        if (alpha < 255 || facing == Player.Facing.UP) {
            g.noTint();
        }
    }

    /**
     * Busur tebasan sabit: titik-titik sepanjang busur lingkaran, dengan inti
     * putih dan halo emas tipis, mengikuti arah hadap pemain. Busur selalu
     * digambar di sisi depan tokoh (arah tebasan).
     */
    private static void drawSwordSlash(PGraphics g, int px, int py, Player.Facing facing) {
        g.noStroke();
        int dirX = facing == Player.Facing.LEFT ? -1 : (facing == Player.Facing.RIGHT ? 1 : 0);
        int dirY = facing == Player.Facing.UP ? -1 : (facing == Player.Facing.DOWN ? 1 : 0);
        boolean horizontal = dirX != 0;

        int cx = px + dirX * 3;
        int cy = py - 12 + dirY * 3;
        int radius = 12;

        // Busur 130 derajat; arah samping memutar busur ke kiri/kanan,
        // arah atas/bawah memutarnya ke atas/bawah.
        for (int i = -16; i <= 16; i++) {
            double t = (i / 16.0) * 1.13;
            double s = Math.sin(t);
            double c = Math.cos(t);
            int x = cx + (int) Math.round((horizontal ? dirX * radius * c : radius * s));
            int y = cy + (int) Math.round((horizontal ? radius * s : dirY * radius * c));

            g.fill(255, 200, 78, 55);
            g.rect(x - 1, y - 1, 3, 3);
            g.fill(255, 250, 216, 235);
            g.rect(x, y, 2, 2);
        }

        // Percikan di ujung tebasan.
        g.fill(255, 244, 170, 220);
        if (horizontal) {
            g.rect(cx + dirX * 2, cy - 14, 1, 2);
            g.rect(cx + dirX * 2, cy + 12, 1, 2);
        } else {
            g.rect(cx - 14, cy + dirY * 2, 2, 1);
            g.rect(cx + 12, cy + dirY * 2, 2, 1);
        }
    }

    public static void drawHero(PGraphics g, int px, int py, Player.Facing facing,
                                boolean attacking, int alpha) {
        drawHero(g, px, py, facing, false, 0f, attacking, alpha);
    }

    private static PImage[] elderSprites = null;
    private static PImage[] boySprites = null;
    private static PImage[] girlSprites = null;

    public static void ensureNpcSpritesLoaded() {
        if (elderSprites == null) {
            elderSprites = new PImage[4];
            elderSprites[0] = loadImage("elder_front.png");
            elderSprites[1] = loadImage("elder_left.png");
            elderSprites[2] = loadImage("elder_back.png");
            elderSprites[3] = loadImage("elder_right.png");
        }
        if (boySprites == null) {
            boySprites = new PImage[4];
            boySprites[0] = loadImage("boy_front.png");
            boySprites[1] = loadImage("boy_left.png");
            boySprites[2] = loadImage("boy_back.png");
            boySprites[3] = loadImage("boy_right.png");
        }
        if (girlSprites == null) {
            girlSprites = new PImage[4];
            girlSprites[0] = loadImage("girl_front.png");
            girlSprites[1] = loadImage("girl_left.png");
            girlSprites[2] = loadImage("girl_back.png");
            girlSprites[3] = loadImage("girl_right.png");
        }
    }

    public static void drawAnimatedNpc(PGraphics g, int px, int py, String kind, int dir, float bob) {
        ensureNpcSpritesLoaded();
        PImage[] sprites = null;
        float h = 30f;
        if ("librarian".equalsIgnoreCase(kind) || "guard".equalsIgnoreCase(kind) || "elder".equalsIgnoreCase(kind)
                || "elder_librarian".equalsIgnoreCase(kind)) {
            sprites = elderSprites;
            h = (sprites != null && sprites[0] != null) ? sprites[0].height : 32f;
        } else if ("boy".equalsIgnoreCase(kind) || "tetua".equalsIgnoreCase(kind) || "tetua_desa".equalsIgnoreCase(kind)) {
            sprites = boySprites;
            h = (sprites != null && sprites[0] != null) ? sprites[0].height : 25f;
        } else if ("girl".equalsIgnoreCase(kind) || "saksi".equalsIgnoreCase(kind) || "saksi_desa".equalsIgnoreCase(kind)) {
            sprites = girlSprites;
            h = (sprites != null && sprites[0] != null) ? sprites[0].height : 25f;
        }

        if (sprites == null || sprites[0] == null) {
            drawShadow(g, px, py, 14);
            drawSprite(g, kind, px, py, false, 0xFFFFFF, 255);
            return;
        }

        drawShadow(g, px, py, 14);

        PImage im = (dir >= 0 && dir < sprites.length) ? sprites[dir] : null;
        boolean flip = false;
        if (im == null) {
            if (dir == 3 && sprites.length > 1 && sprites[1] != null) {
                // If DIR_RIGHT is missing, mirror DIR_LEFT
                im = sprites[1];
                flip = true;
            } else if (dir == 1 && sprites.length > 3 && sprites[3] != null) {
                // If DIR_LEFT is missing, mirror DIR_RIGHT
                im = sprites[3];
                flip = true;
            } else {
                // Missing sideways or up sprite: fallback to DIR_DOWN, mirror when facing left
                im = sprites[0];
                flip = (dir == 1); // DIR_LEFT
            }
        }

        if (im == null) {
            return;
        }

        float w = Math.round(h * im.width / (float) im.height);
        float renderY = py + bob;

        g.pushMatrix();
        g.translate(px, renderY);
        if (flip) {
            g.scale(-1f, 1f);
        }
        g.image(im, -w / 2f, -h, w, h);
        g.popMatrix();
    }

    public static void drawNpc(PGraphics g, int px, int py, String kind) {
        ensureNpcSpritesLoaded();
        if ("librarian".equalsIgnoreCase(kind) || "guard".equalsIgnoreCase(kind) || "elder".equalsIgnoreCase(kind)
                || "boy".equalsIgnoreCase(kind) || "tetua".equalsIgnoreCase(kind) || "tetua_desa".equalsIgnoreCase(kind)
                || "girl".equalsIgnoreCase(kind) || "saksi".equalsIgnoreCase(kind) || "saksi_desa".equalsIgnoreCase(kind)) {
            String mapped = ("boy".equalsIgnoreCase(kind) || "tetua".equalsIgnoreCase(kind) || "tetua_desa".equalsIgnoreCase(kind)) ? "boy"
                    : ("girl".equalsIgnoreCase(kind) || "saksi".equalsIgnoreCase(kind) || "saksi_desa".equalsIgnoreCase(kind)) ? "girl"
                    : "librarian";
            drawAnimatedNpc(g, px, py, mapped, 0, 0f);
            return;
        }

        drawShadow(g, px, py, 14);
        drawSprite(g, kind, px, py, false, 0xFFFFFF, 255);
        if ("guard".equals(kind)) {
            // Tombak upacara dan perisai penjaga aula
            drawIcon(g, "upg_spear", px + 6, py - 28, 8, 22);
            drawIcon(g, "upg_shieldSmall", px - 12, py - 20, 10, 12);
        } else if ("elder".equals(kind)) {
            // Tongkat kayu panjang di sisi kanan sesepuh.
            drawIcon(g, "upg_wand", px + 8, py - 26, 7, 20);
        }
    }

    public static void drawEnemy(PGraphics g, int px, int py, String kind,
                                 boolean hurt, int alpha, boolean flip, float animTime) {
        drawShadow(g, px, py, "orc".equals(kind) ? 24 : 14);

        if ("slime".equals(kind)) {
            // Bayangan Hutan: animasi roh bayangan api dari flamelings
            ensureLoaded();
            PImage[] frames = shadowFlameFrames;
            PImage img = null;
            if (frames != null && frames.length > 0) {
                int idx = Math.floorMod((int) (animTime * 8f), frames.length);
                img = frames[idx];
            }
            if (img == null) {
                img = sprite("slime");
            }
            if (img != null) {
                float w = img.width;
                float h = img.height;
                g.noSmooth();
                g.tint(160, 130, 220, alpha);
                if (flip) {
                    g.pushMatrix();
                    g.translate(px, 0);
                    g.scale(-1f, 1f);
                    g.image(img, -w / 2f, py - h, w, h);
                    g.popMatrix();
                } else {
                    g.image(img, px - w / 2f, py - h, w, h);
                }
                g.noTint();

                // Inti roh bayangan bersinar
                g.noStroke();
                g.fill(120, 230, 255, 120 + ((int) (animTime * 10f) & 1) * 60);
                g.ellipse(px + (flip ? -1 : 1), py - 14, 5, 5);
            }
        } else {
            drawSprite(g, kind, px, py, flip, 0xFFFFFF, alpha);
        }

        if (hurt) {
            g.blendMode(PApplet.ADD);
            drawSprite(g, kind, px, py, flip, 0x808080, alpha);
            g.blendMode(PApplet.BLEND);
        }
    }

    public static void drawEnemy(PGraphics g, int px, int py, String kind,
                                 boolean hurt, int alpha, boolean flip) {
        drawEnemy(g, px, py, kind, hurt, alpha, flip, (System.currentTimeMillis() % 100000) / 1000f);
    }

    public static void drawIcon(PGraphics g, String name, int x, int y, int w, int h) {
        ensureLoaded();
        PImage img = icon(name);
        if (img != null) {
            g.noSmooth();
            g.image(img, x, y, w, h);
        }
    }

    /**
     * Memuat gambar latar penuh peta (mis. 640x256) dari data/art.
     *
     * Mengembalikan null bila berkas tidak ada, supaya pemanggil bisa
     * kembali ke renderer tile prosedural.
     */
    public static PImage loadBackground(String name) {
        ensureLoaded();
        PImage cached = BACKGROUNDS.get(name);
        if (cached != null) {
            return cached;
        }
        PImage img = load(name);
        if (img != null) {
            BACKGROUNDS.put(name, img);
        }
        return img;
    }

    /**
     * Memuat gambar bebas (mis. sprite pintu besi) dari folder data/art.
     *
     * Mengembalikan null bila berkas tidak ada, supaya pemanggil bisa
     * kembali ke renderer prosedural.
     */
    public static PImage loadImage(String name) {
        return loadBackground(name);
    }

    private static PImage sprite(String name) {
        ensureLoaded();
        PImage cached = SPRITES.get(name);
        if (cached != null) {
            return cached;
        }
        if (erodaxia == null) {
            return null;
        }
        PImage img;
        switch (name) {
            case "hero":
                img = cut(erodaxia, 332, 319, 23, 30);
                break;
            case "hero2":
                img = cut(erodaxia, 360, 318, 23, 31);
                break;
            case "guard":
                img = robedNpc(0x353F50, 0x546278, false, true);
                break;
            case "elder":
                img = robedNpc(0x3A3E50, 0x464A5E, true, false);
                break;
            case "saksi":
                img = robedNpc(0x4A2632, 0x603442, false, false);
                break;
            case "slime":
                img = cut(erodaxia, 347, 16, 32, 26);
                break;
            case "orc":
                img = cut(erodaxia, 385, 195, 46, 51);
                break;
            case "fire":
                img = fireSprite();
                break;
            case "kotak":
                img = ironChestSprite();
                break;
            case "stone":
                img = runeMonolithSprite();
                break;
            default:
                img = null;
                break;
        }
        if (img != null) {
            SPRITES.put(name, img);
        }
        return img;
    }

    /**
     * Menyusun NPC berjubah dari sprite manusia polos: bagian tubuh diwarnai
     * kain, lalu ditambahkan tudung (atau helm untuk penjaga) dan janggut.
     */
    private static PImage robedNpc(int body, int hood, boolean beard, boolean helmet) {
        PImage out = cut(erodaxia, 207, 172, 20, 34).get();
        out.loadPixels();
        final int W = 20;

        for (int y = 0; y < 34; y++) {
            for (int x = 0; x < W; x++) {
                int i = y * W + x;
                int c = out.pixels[i];
                int a = (c >>> 24) & 0xFF;
                int r = (c >> 16) & 0xFF;
                int g = (c >> 8) & 0xFF;
                int b = c & 0xFF;
                if (a < 20) {
                    out.pixels[i] = 0;
                    continue;
                }
                boolean skin = r > 150 && g > 95 && b > 55 && r > g && g >= b;
                boolean cloth = r > g && g > b && r < 190;
                if (y < 11) {
                    if (cloth) {
                        out.pixels[i] = 0xFF000000 | hood;
                    }
                } else if (skin || cloth) {
                    float lum = (r * 0.30f + g * 0.59f + b * 0.11f) / 200f;
                    lum = Math.max(0.35f, Math.min(1.10f, lum));
                    out.pixels[i] = 0xFF000000 | scaleColor(body, lum);
                }
            }
        }

        for (int y = 0; y < 11; y++) {
            for (int x = 0; x < W; x++) {
                int i = y * W + x;
                if (((out.pixels[i] >>> 24) & 0xFF) < 20) {
                    continue;
                }
                boolean in;
                if (y == 0) {
                    in = x >= 6 && x <= 13;
                } else if (y == 1) {
                    in = x >= 5 && x <= 14;
                } else if (y == 2) {
                    in = x >= 4 && x <= 15;
                } else if (y <= 4) {
                    in = (x >= 4 && x <= 6) || (x >= 13 && x <= 15);
                } else if (y <= 6) {
                    in = (x >= 3 && x <= 5) || (x >= 14 && x <= 16);
                } else {
                    in = (x >= 2 && x <= 4) || (x >= 15 && x <= 17);
                }
                if (in) {
                    float lum = y <= 1 ? 1.22f : (y >= 7 ? 0.78f : 1.0f);
                    out.pixels[i] = 0xFF000000 | scaleColor(hood, lum);
                }
            }
        }

        if (beard) {
            for (int y = 9; y < 13; y++) {
                int x0 = y == 12 ? 7 : 6;
                int x1 = y == 12 ? 11 : 12;
                for (int x = x0; x <= x1; x++) {
                    int i = y * W + x;
                    if (((out.pixels[i] >>> 24) & 0xFF) < 20) {
                        continue;
                    }
                    out.pixels[i] = 0xFFD2CEC0;
                }
            }
        }

        if (helmet) {
            for (int y = 1; y < 4; y++) {
                for (int x = 5; x <= 14; x++) {
                    int i = y * W + x;
                    if (((out.pixels[i] >>> 24) & 0xFF) < 20) {
                        continue;
                    }
                    float lum = y == 1 ? 1.15f : (y == 3 ? 0.72f : 1.0f);
                    out.pixels[i] = 0xFF000000 | scaleColor(hood, lum);
                }
            }
        }

        out.updatePixels();
        return out;
    }

    private static int scaleColor(int rgb, float factor) {
        int r = Math.min(255, (int) (((rgb >> 16) & 0xFF) * factor));
        int g = Math.min(255, (int) (((rgb >> 8) & 0xFF) * factor));
        int b = Math.min(255, (int) ((rgb & 0xFF) * factor));
        return (r << 16) | (g << 8) | b;
    }

    private static PImage icon(String name) {
        PImage cached = ICONS.get(name);
        if (cached != null) {
            return cached;
        }
        PImage img = loadIcon(name);
        if (img != null) {
            ICONS.put(name, img);
        }
        return img;
    }

    private static PImage loadIcon(String name) {
        return load(name + ".png");
    }

    /** Mengambil makhluk api terkecil dari sheet flamelings dan menghapus latar biru. */
    private static PImage fireSprite() {
        if (flamelings == null) {
            return null;
        }
        PImage keyed = flamelings.get();
        keyed.loadPixels();
        for (int i = 0; i < keyed.pixels.length; i++) {
            int c = keyed.pixels[i];
            int r = (c >> 16) & 0xFF;
            int g = (c >> 8) & 0xFF;
            int b = c & 0xFF;
            if (Math.abs(r - 32) < 55 && Math.abs(g - 138) < 70 && Math.abs(b - 171) < 60) {
                keyed.pixels[i] = 0;
            }
        }
        keyed.updatePixels();

        PImage region = keyed.get(0, 0, 128, 128);
        return trim(region);
    }

    /** Memangkas area transparan di sekeliling sprite. */
    private static PImage trim(PImage src) {
        src.loadPixels();
        int minX = src.width;
        int minY = src.height;
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < src.height; y++) {
            for (int x = 0; x < src.width; x++) {
                int a = (src.pixels[y * src.width + x] >>> 24) & 0xFF;
                if (a > 16) {
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }
        if (maxX < 0) {
            return src;
        }
        int pad = 1;
        minX = Math.max(0, minX - pad);
        minY = Math.max(0, minY - pad);
        maxX = Math.min(src.width - 1, maxX + pad);
        maxY = Math.min(src.height - 1, maxY + pad);
        return src.get(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private static PImage ironChestSprite() {
        PImage out = new PImage(22, 18, PApplet.ARGB);
        out.loadPixels();
        for (int y = 0; y < 18; y++) {
            for (int x = 0; x < 22; x++) {
                int c = 0;
                if (x >= 1 && x <= 20 && y >= 2 && y <= 16) {
                    boolean border = (x == 1 || x == 20 || y == 2 || y == 16);
                    boolean band = (x >= 3 && x <= 5) || (x >= 16 && x <= 18) || (y == 8 || y == 9);
                    boolean lockPlate = (x >= 9 && x <= 12 && y >= 7 && y <= 11);
                    if (border) {
                        c = 0xFF141820;
                    } else if (lockPlate) {
                        c = (y == 9 && (x == 10 || x == 11)) ? 0xFF15181E : 0xFFD4AF37;
                    } else if (band) {
                        c = (y == 2 || y == 8 || x == 3 || x == 16) ? 0xFF626E80 : 0xFF3E4756;
                    } else {
                        c = (y < 8) ? 0xFF3A2C22 : 0xFF2B2018;
                    }
                }
                out.pixels[y * 22 + x] = c;
            }
        }
        out.updatePixels();
        return out;
    }

    private static PImage runeMonolithSprite() {
        PImage out = new PImage(20, 24, PApplet.ARGB);
        out.loadPixels();
        for (int y = 0; y < 24; y++) {
            int halfW = (y < 4) ? (y + 4) : ((y < 20) ? 8 : 9);
            for (int x = 0; x < 20; x++) {
                int dx = Math.abs(x - 10);
                if (dx <= halfW && y >= 2 && y <= 22) {
                    boolean edge = (dx == halfW || y == 2 || y == 22);
                    if (edge) {
                        out.pixels[y * 20 + x] = 0xFF181C24;
                    } else {
                        boolean rune = (dx == 0 && y >= 7 && y <= 15) || (y == 11 && dx <= 3) || (y == 8 && dx <= 2);
                        if (rune) {
                            out.pixels[y * 20 + x] = 0xFF6DE0D8;
                        } else {
                            float lum = 0.5f + (22 - y) * 0.02f - dx * 0.03f;
                            int v = Math.min(255, (int) (80 * lum));
                            out.pixels[y * 20 + x] = 0xFF000000 | (v << 16) | ((v + 10) << 8) | (v + 15);
                        }
                    }
                } else {
                    out.pixels[y * 20 + x] = 0;
                }
            }
        }
        out.updatePixels();
        return out;
    }
}
