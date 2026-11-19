import processing.core.PGraphics;
import processing.core.PImage;

/**
 * Peta tile sederhana untuk Stage 02.
 *
 * Peta disimpan sebagai grid karakter agar data area mudah dibaca dan diubah.
 * Tidak ada file aset eksternal: setiap tile digambar secara prosedural dengan
 * placeholder buatan sendiri (lihat ASSET_CREDITS.md).
 *
 * Legenda karakter:
 *   '#' dinding batu (solid)      'W' dinding dengan jendela (solid)
 *   '.' lantai kayu               ',' karpet (bisa dilewati)
 *   'B' rak buku (solid)          'T' meja (solid)
 *   'P' pilar (solid)             'S' titik spawn (lantai/jalur)
 *   't' batang pohon (solid)      'l' kanopi daun (solid)
 *   'g' rumput (lewat)            'r' batu (solid)
 *   '~' zona kabut (lewat)        'F' jalur hutan (lewat)
 *   'E' penanda keluar (lewat)    'X' batu simbol (solid)
 *   'G' gerbang (solid/lewat)
 *   't' batang pohon (solid)      'l' kanopi daun (solid)
 *   'g' rumput (walkable)         'r' batu (solid)
 *   '~' zona kabut (walkable)     'F' jalan hutan (walkable)
 *   'E' penanda keluar timur      'X' batu simbol (solid)
 *
 *   Lembah Berkabut (Bab 3):
 *   'v' tanaman rambat (walkable)  'C' dinding tebing (solid)
 *   'd' tanah lembah (walkable)    'w' air dangkal (walkable)
 *   'R' batu reruntuhan (solid)    'Z' titik istirahat (walkable)
 *   'K' jalur terkunci (solid sampai sigil fragment diperoleh)
 *
 *   Ashen Village (Prolog):
 *   'Y' jalan desa/batu cobble (walkable) 'V' dinding bangunan desa (solid)
 *   'D' pintu bangunan (walkable)          'N' papan pengumuman (solid)
 *   'L' tiang lampu (solid)
 *
 *   Chapel of Whispers (Bab 4):
 *   'H' dinding batu kapel (solid)     'n' bangku rusak (solid)
 *   'a' batu altar (solid)             'i' fragmen kaca patri (solid)
 *   'o' lantai kapel (walkable)        'm' lantai kapel berlumut (walkable)
 */
public final class TileMap {

    public static final int TILE = 16;

    // Aula uji The Silent Manor. Ukuran 26x16 tile (416x256 piksel), lebih
    // besar dari viewport 320x180 sehingga kamera ikut bergerak.
    // Gambar latar: bg_manor_hall.png (aula kastel gelap dengan rak buku,
    // jendela pecah, dan kait rantai pengunci pintu di kanan bawah).
    private static final String[] MANOR_HALL = {
            "##########################",
            "##########################",
            "##########################",
            "##########################",
            "##########################",
            "##BBBBBB#########BBBBBB###",
            "##BBBBBB#########BBBBBB###",
            "##BBBBBB#########BBBBBB###",
            "##BBBBBB.........BBBBBB.##",
            "#.......................##",
            "#.......,,,,,,,,,,......G#",
            "#.......,,,,,,,,,,......G#",
            "#.......,,,,,,,,,,......##",
            "#...........S...........##",
            "#.......................##",
            "##########################"
    };

    // Sayap Timur The Silent Manor (Arsip & Altar Rekonstruksi).
    // Ukuran 26x16 tile (416x256 piksel).
    private static final String[] MANOR_EAST_WING = {
            "####WW####WW####WW########",
            "#........................#",
            "#..BBBB..........BBBB....#",
            "#..BBBB....AA....BBBB....#",
            "#..........AA............#",
            "#....PP..........PP......#",
            "#G.S......,,,,......MM...#",
            "#G........,,,,......MM...#",
            "#.........,,,,...........#",
            "#....PP..........PP......#",
            "#........................#",
            "#..BBBB..........BBBB....#",
            "#..BBBB..........BBBB....#",
            "#........................#",
            "#........................#",
            "##########################"
    };

    // Hollow Woods — hutan berkabut di luar manor (Bab 2).
    // Ukuran 40x16 tile (640x256 piksel), area side-scrolling.
    //
    // Legenda tambahan:
    //   't' batang pohon (solid)       'l' kanopi daun (solid)
    //   'g' rumput (walkable)          'r' batu (solid)
    //   '~' zona kabut (walkable)      'F' jalan hutan (walkable)
    //   'E' penanda keluar timur       'X' batu simbol (solid)
    private static final String[] HOLLOW_WOODS = {
            // 40 kolom x 16 baris = 640x256 piksel
            "llllllllllllllllllllllllllllllllllllllll",
            "ltttllltttllllltttlllltttlllltttlllltttl",
            "lggggggggggggggggggggggggggggggggggggggl",
            "lggggggggFFFFFggggggggggggFFgggggggggggl",
            "FSFFFFFFFFFFFFFFFFFFFgFFFFFFFFFFF~~E~~gl",
            "lgggggggggggggggggggggggggggggggggg~gggl",
            "lgggggggggggrrgggggggggggggggggggggggggl",
            "llltttllgggggrrgggggggggggggggggggg~gggl",
            "lllgggllgg~ggggg~ggg~gggg~ggggg~gggg~ggl",
            "lggggggggg~ggXgg~gggg~ggg~~ggggg~ggggggl",
            "lggggggggg~gggg~ggggggggg~gggggggggggggl",
            "lggggggggggggggggggggggggggggggggggggggl",
            "lggggggggggggggggggggggggggggggggggggggl",
            "ltttllltttllllltttlllltttlllltttlllltttl",
            "llllllllllllllllllllllllllllllllllllllll",
            "llllllllllllllllllllllllllllllllllllllll"
    };

    // Jalur Tebing — jalur tebing berkabut (Bab 3A).
    // Ukuran 40x16 tile (640x256 piksel).
    // Entry dari kiri (Hollow Woods), exit ke kanan (Dasar Lembah).
    private static final String[] VALLEY_CLIFF_PATH = {
            "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCvCCC",
            "CCCCCCCCCCvCCCCCCCCCCCCCCCCCvCCCCCCCvCCC",
            "CCCCvCCCCCvCCCCCCddddddCCCCCvCCCCCCCCCCC",
            "CCCCvCCCddddddCCCddXddCCCCddddddCCCCCCCC",
            "GSFdddddddd~~dddddddddddddddddddddddddGC",
            "CCCCCCCddddddCCCCCCCCCCdddddddddddddddGC",
            "CCCCCCCCCCCCCCCCCCCCCCCCCCddddddddddCCCC",
            "CCCCCCCvCCCCCCCCCCCCCCCCCCCCddddddddCCCC",
            "CCCCCCCvCCCCCCCCddddddddddddCCCCCCCCCCCC",
            "CCCCCCCCCCCCCCCCddddddddddddCCCCCCCCCCCC",
            "CCCCCCCCCCCCCCCCddXdddddXddCCCCCCCCCCCCC",
            "CCCCCvCCCCCCCCCCddddddddddddCCCCCCCCCCCC",
            "CCCCCvCCCCCCCCCCCCCCddddCCCCCCCCCCCCCCCC",
            "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC",
            "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCvCCC",
            "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"
    };

    // Dasar Lembah — dasar lembah berkabut dengan sungai (Bab 3B).
    // Ukuran 40x16 tile (640x256 piksel).
    // Entry dari kiri (Jalur Tebing), exit ke kanan (Lintasan Reruntuhan).
    private static final String[] VALLEY_FLOOR = {
            "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCvC",
            "CvCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCRRCCCvC",
            "CvCCCCddddddddddddddddddddddddRRddddCCCC",
            "CCCdddddddddwwddddddddddddddddddddddCCCC",
            "GSFddddddwwwwwwdddd~~dd~~ddddddddddddGCC",
            "CCCCdddddddwwwddddd~~dd~~dddRddddddddGCC",
            "CCCCCCddddddddddddddddddddddRdddddddCCCC",
            "CCCCCCCdddddddddddddZddddddddddddddCCCCC",
            "CCCCCCCCdddddddddddddddddddddddddCCCCCCC",
            "CCCCCCCCCdddddddwwddddddddddddddCCCCCCCC",
            "CCCCCCCCCCdddddwwwwddddddXdddddCCCCCCCCC",
            "CCCCCCCCCCCdddddwwdddRddddddddCCCCCCCCCC",
            "CCCvCCCCCCCCddddddddddddddddCCCCCCCCCCCC",
            "CCCvCCCCCCCCCCCCddddddddCCCCCCCCCCCCCCCC",
            "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC",
            "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"
    };

    // Lintasan Reruntuhan — reruntuhan kuno (Bab 3C).
    // Ukuran 40x16 tile (640x256 piksel).
    // Entry dari kiri (Dasar Lembah), exit ke kanan (Chapel of Whispers).
    private static final String[] VALLEY_RUINS = {
            "CCCCCCCCCCCCCCCCCCCCCCCCCCCRRRRRCCCCCCvC",
            "CvCCCCCCCCRRRCCCCCCCCCCCCCCRddddRCCCCCvC",
            "CvCCCCCCCCRddRCCCCddddddddCRddddRCCCCCCC",
            "CCCCCCddddRddRddddddddddddddddddddddCCCC",
            "GSFddddddddddddddddddddddddddddddddddKCC",
            "CCCCCCddddRddddddddddddddddddddddddddKCC",
            "CCCCCCCCCCRRRddddddddddddddddRddddddCCCC",
            "CCCCCCCCCCCCCCddddddddddddddRRRddddCCCCC",
            "CCCCCCCCCCCCCCddddddddddddddddddddCCCCCC",
            "CCCCCCCCCCCCddddddddXdddddddddddCCCCCCCC",
            "CCCCCCCCCCCCdddRRRddddddRRdddddCCCCCCCCC",
            "CCCvCCCCCCCCddRdddRddddRddRdddCCCCCCCCCC",
            "CCCvCCCCCCCCCCRdZdRddddRddRCCCCCCCCCCCCC",
            "CCCCCCCCCCCCCCRRRRRCCCCRRRRCCCCCCCCCCCCC",
            "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCvCC",
            "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"
    };

    // Chapel of Whispers — kapel reruntuhan gotik (Bab 4).
    // Ukuran 40x16 tile (640x256 piksel).
    // Entry dari kiri (Valley Ruins), exit ke kanan (Underground Archive).
    private static final String[] CHAPEL_WHISPERS = {
            "HHHHiHHHHiHHHHHHHHHHHHHHiHHHHHHHiHHHHHHH",
            "HooooooooooooHHoooooooooooooHoooooooooHH",
            "HonoonnoonoooHHonoonnoonoooHooooooooooHH",
            "HoooooooooooooooooooooooooooHooaooooooGH",
            "GSooommooooommooooommoooooooooaoooooodGH",
            "HoooooooooooooooooooooooooooooaoooooodHH",
            "HoooooooooooooooooooooooooooHooaooooooHH",
            "HonoonnoonoooHHonoonnoonoooHooooooooooHH",
            "HooooooooooooHHooooXooooooHHooZoooooooHH",
            "HHHHHHHHoooHHHHHHHHHHHHHHHHHHHHHoooHHHHH",
            "HHHHHHHHoooHHHHHHHHHHHHHHHHHHHHHoooHHHHH",
            "HHHHHooooooHHHHHHHHHHHHHHHHHHHoooooooHHH",
            "HHHHHoXooooHHHHHHHHHHHHHHHHHHHoooooooHHH",
            "HHHHHooooooHHHHHHHHHHHHHHHHHHHoooooooHHH",
            "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH",
            "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
    };

    // Ashen Village — desa berkabut tempat prolog dimulai.
    // Ukuran 40x16 tile (640x256 piksel).
    // Spawn di kiri, exit ke kanan (G gate) menuju Manor.
    //
    // Legenda tambahan:
    //   'Y' jalan desa cobblestone (walkable) 'V' dinding bangunan desa (solid)
    //   'D' pintu bangunan (walkable)          'N' papan pengumuman (solid)
    //   'L' tiang lampu (solid)
    private static final String[] ASHEN_VILLAGE = {
            "VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV",
            "VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV",
            "VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV",
            "VVVVVVVVVVVVVVVVVVVVXXVVVVVVVVVVVVVVVVVV",
            "VVVVVVVVVVVVVVVVVVVVXXVVVVVVVVVVVVVVVVVV",
            "ggggggggggggggggggNggggggggggggggggggggg",
            "gggLggggggLggggggggggggggLgggggggggggggg",
            "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY",
            "YYSYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYGGYYY",
            "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY",
            "VVVVVVVVggggggVVVVVVVggggggggggggggggVVV",
            "VVVVVVVVggggggVVVVVVVgggggVVVggggggggVVV",
            "VVVVVVVVggggggVVVVVVVgggggVVVggggggggVVV",
            "VVVVVVVVggggggVVVVVVVgggggVVVggggggggVVV",
            "VVVVVVVVggggggVVVVVVVgggggVVVggggggggVVV",
            "VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV"
    };

    // Underground Archive — arsip bawah tanah kapel (Bab 5).
    // Ukuran 40x16 tile (640x256 piksel).
    // Entry dari kiri (Chapel of Whispers), exit ke kanan (lorong rahasia ke Manor).
    //
    // Legenda tambahan:
    //   'U' dinding batu arsip (solid)        'b' rak buku arsip (solid)
    //   'q' meja arsip & dokumen (solid)      'e' panel mekanisme batu (solid)
    //   'p' lantai batu arsip (walkable)      'c' lantai koridor sempit (walkable)
    private static final String[] UNDERGROUND_ARCHIVE = {
            "UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU",
            "UbbbbUUUUbbbbUUUUUUUUUUUUUUUUUUUUUUUUUUU",
            "UppppUUUUppppUUUeUUUeeUUUppppppppppppUUU",
            "UppppUUUUppppUUUppppUUUUUppppppppppppUUU",
            "GSppppppppppppppqqqqpppppppppppppppppGGU",
            "UppppUUUUppppUUUppppUUUUUppppppppppppGGU",
            "UppppUUUUppppUUUUUUUUUUUUpqqppppqqpppUUU",
            "UUccUUUUUUccUUUUUUUUUUUUUppppppppppppUUU",
            "UUccUUUUUUccUUUUUUUUUUUUUUUUUUccUUUUUUUU",
            "UppppppppppppUUUbbbbUUUUbbbbUUccUUUUUUUU",
            "UpqqppppqqpppUUUppppUUUUppppUUccUUUUUUUU",
            "UppppppppppppUUUppppUUUUppppUUccUUUUUUUU",
            "UppppppppppppUUUppppUUUUppppUUppppppppXU",
            "UUUUUUZZUUUUUUUUpqqpUUUUpqqpUUppppZpppXU",
            "UUUUUUUUUUUUUUUUppppUUUUppppUUppppppppUU",
            "UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU"
    };

    private final char[][] tiles;
    private final int cols;
    private final int rows;
    private int spawnCol = -1;
    private int spawnRow = -1;
    private boolean gateOpen = false;
    private Art.Theme theme = Art.Theme.MANOR;

    /**
     * @throws IllegalArgumentException bila layout kosong, tidak berbentuk
     *                                  persegi, atau tidak memiliki spawn.
     */
    public TileMap(String[] layout) {
        if (layout == null || layout.length == 0) {
            throw new IllegalArgumentException("Layout peta kosong.");
        }

        rows = layout.length;
        cols = layout[0].length();
        if (cols == 0) {
            throw new IllegalArgumentException("Baris pertama layout peta kosong.");
        }

        tiles = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            String line = layout[r];
            if (line == null || line.length() != cols) {
                throw new IllegalArgumentException("Baris peta " + r
                        + " memiliki panjang " + (line == null ? "null" : line.length())
                        + ", seharusnya " + cols + ".");
            }
            for (int c = 0; c < cols; c++) {
                char ch = line.charAt(c);
                tiles[r][c] = ch;
                if (ch == 'S') {
                    if (spawnCol >= 0) {
                        throw new IllegalArgumentException("Layout peta memiliki lebih dari satu spawn.");
                    }
                    spawnCol = c;
                    spawnRow = r;
                }
            }
        }

        if (spawnCol < 0) {
            throw new IllegalArgumentException("Layout peta tidak memiliki spawn 'S'.");
        }
    }

    public static TileMap manorHall() {
        TileMap map = themed(new TileMap(MANOR_HALL), Art.Theme.MANOR);
        map.setBackground(Art.loadBackground("bg_manor_hall.png"));
        // Aula Manor pakai gambar latar — tidak perlu sprite pintu besi
        // sama sekali (baik tertutup maupun terbuka).
        map.setDrawDoors(false);
        return map;
    }

    public static TileMap manorEastWing() {
        TileMap wing = themed(new TileMap(MANOR_EAST_WING), Art.Theme.MANOR);
        wing.setGateOpen(true);
        return wing;
    }

    public static TileMap hollowWoods() {
        TileMap woods = themed(new TileMap(HOLLOW_WOODS), Art.Theme.FOREST);
        woods.setGateOpen(true);
        woods.setDrawDoors(false);
        return woods;
    }

    public static TileMap valleyCliffPath() {
        TileMap map = themed(new TileMap(VALLEY_CLIFF_PATH), Art.Theme.CAVE);
        map.setGateOpen(true);
        return map;
    }

    public static TileMap valleyFloor() {
        TileMap map = themed(new TileMap(VALLEY_FLOOR), Art.Theme.CAVE);
        map.setGateOpen(true);
        return map;
    }

    public static TileMap valleyRuins() {
        TileMap map = themed(new TileMap(VALLEY_RUINS), Art.Theme.CAVE);
        map.setGateOpen(true);
        return map;
    }

    /**
     * Gambar latar penuh peta. Kalau ada, gambar ini menggantikan render tile
     * lantai/dinding (bukan tile objek, yang tetap digambar di atasnya).
     */
    private PImage background;
    private boolean drawDoors = true;
    private boolean drawClosedGateDoor = true;
    private boolean drawOpenGateDoor = true;

    /** memasang gambar latar peta (null = pakai tile prosedural). */
    public void setBackground(PImage image) {
        this.background = image;
    }

    /**
     * Matikan gambar pintu besi bila gambar latar sudah memuat pintu sendiri
     * (mis. lengkung gerbang di Ashen Village).
     */
    public void setDrawDoors(boolean draw) {
        this.drawDoors = draw;
    }

    /**
     * Mengatur sprite pintu besi mana yang digambar untuk tile gerbang 'G'.
     * Dipakai Aula Manor untuk membuang pintu pagar tertutup yang lama,
     * sementara tetap memakai sprite lorong terbuka sebagai jalan masuk.
     */
    public void setGateDoorVisibility(boolean closed, boolean open) {
        this.drawClosedGateDoor = closed;
        this.drawOpenGateDoor = open;
    }

    /** True bila peta memakai gambar latar, bukan tile prosedural. */
    public boolean hasBackground() {
        return background != null;
    }

    public static TileMap ashenVillage() {
        TileMap map = themed(new TileMap(ASHEN_VILLAGE), Art.Theme.VILLAGE);
        // Gerbang desa tetap terkunci sampai prologue selesai (lihat
        // GameplayScene.syncVillageGate).
        map.setBackground(Art.loadBackground("bg_ashen_village.png"));
        // Gambar latar sudah memuat lengkung gerbang, jadi pintu besi tidak
        // digambar lagi di atasnya.
        map.setDrawDoors(false);
        return map;
    }

    public static TileMap chapelWhispers() {
        TileMap map = themed(new TileMap(CHAPEL_WHISPERS), Art.Theme.CHAPEL);
        map.setGateOpen(true);
        return map;
    }

    public static TileMap undergroundArchive() {
        TileMap map = themed(new TileMap(UNDERGROUND_ARCHIVE), Art.Theme.ARCHIVE);
        map.setGateOpen(true);
        return map;
    }

    private static TileMap themed(TileMap map, Art.Theme theme) {
        map.theme = theme;
        return map;
    }

    /** Tema visual peta (menentukan tekstur lantai dan dinding). */
    public Art.Theme theme() {
        return theme;
    }

    public int cols() {
        return cols;
    }

    public int rows() {
        return rows;
    }

    public int pixelWidth() {
        return cols * TILE;
    }

    public int pixelHeight() {
        return rows * TILE;
    }

    public float spawnX() {
        return spawnCol * TILE + TILE / 2f;
    }

    public float spawnY() {
        return spawnRow * TILE + TILE / 2f;
    }

    public char tileAt(int col, int row) {
        if (col < 0 || col >= cols || row < 0 || row >= rows) {
            return '#';
        }
        return tiles[row][col];
    }

    public boolean isSolid(int col, int row) {
        char ch = tileAt(col, row);
        if (ch == 'G') {
            return !gateOpen;
        }
        if (ch == 'K') {
            return !gateOpen;
        }
        return ch == '#' || ch == 'W' || ch == 'B' || ch == 'T' || ch == 'P' || ch == 'A' || ch == 'M'
                || ch == 't' || ch == 'l' || ch == 'r' || ch == 'X'
                || ch == 'C' || ch == 'R'
                || ch == 'H' || ch == 'n' || ch == 'a' || ch == 'i'
                || ch == 'V' || ch == 'N' || ch == 'L'
                || ch == 'U' || ch == 'b' || ch == 'q' || ch == 'e';
    }

    public boolean isGateOpen() {
        return gateOpen;
    }

    public void setGateOpen(boolean open) {
        this.gateOpen = open;
    }

    /**
     * Mengecek tabrakan antara kotak dunia dan tile solid.
     * Koordinat di luar peta dianggap solid agar pemain tidak bisa keluar area.
     */
    public boolean rectCollides(float x, float y, float w, float h) {
        if (w <= 0f || h <= 0f) {
            return true;
        }
        int c0 = (int) Math.floor(x / TILE);
        int c1 = (int) Math.floor((x + w - 0.001f) / TILE);
        int r0 = (int) Math.floor(y / TILE);
        int r1 = (int) Math.floor((y + h - 0.001f) / TILE);

        if (c0 < 0 || r0 < 0 || c1 >= cols || r1 >= rows) {
            return true;
        }
        for (int r = r0; r <= r1; r++) {
            for (int c = c0; c <= c1; c++) {
                if (isSolid(c, r)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Menggambar seluruh tile peta tanpa culling kamera.
     * Dipakai oleh AssetExport untuk menyimpan peta penuh sebagai PNG.
     */
    public void drawAll(PGraphics g) {
        if (background != null) {
            g.image(background, 0, 0, background.width, background.height);
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (background != null && !isOverlayTile(tiles[r][c])) {
                    continue;
                }
                drawTile(g, tiles[r][c], c, r, 0, 0);
            }
        }
        drawGates(g, 0, 0);
    }

    /** Menggambar tile yang terlihat pada kamera. */
    public void draw(PGraphics g, int cameraX, int cameraY) {
        int firstCol = Math.max(0, cameraX / TILE);
        int firstRow = Math.max(0, cameraY / TILE);
        int lastCol = Math.min(cols - 1, (cameraX + GameManager.VIRTUAL_W) / TILE);
        int lastRow = Math.min(rows - 1, (cameraY + GameManager.VIRTUAL_H) / TILE);

        if (background != null) {
            g.image(background, -cameraX, -cameraY, background.width, background.height);
            for (int r = firstRow; r <= lastRow; r++) {
                for (int c = firstCol; c <= lastCol; c++) {
                    char ch = tiles[r][c];
                    if (!isOverlayTile(ch)) {
                        continue;
                    }
                    drawTile(g, ch, c, r, cameraX, cameraY);
                }
            }
            drawGates(g, cameraX, cameraY);
            return;
        }

        for (int r = firstRow; r <= lastRow; r++) {
            for (int c = firstCol; c <= lastCol; c++) {
                drawTile(g, tiles[r][c], c, r, cameraX, cameraY);
            }
        }
        drawGates(g, cameraX, cameraY);
    }

    /**
     * Tile yang tetap digambar di atas gambar latar (objek, bukan terrain).
     * Saat gambar latar dipakai, lantai/dinding/rumput/gerbang sudah ada di
     * gambar, jadi tidak ada tile yang digambar ulang — gerbang Aula Manor
     * memakai rantai di ujung kanan gambar, bukan aset gerbang prosedural.
     */
    private boolean isOverlayTile(char ch) {
        return ch == 'K';
    }

    private void drawTile(PGraphics g, char ch, int col, int row, int camX, int camY) {
        int x = col * TILE - camX;
        int y = row * TILE - camY;

        if (Art.ENABLED) {
            Art.drawTile(g, ch, x, y, theme, col, row);
            return;
        }

        switch (ch) {
            case '#':
                drawWall(g, x, y, col, row, false);
                break;
            case 'W':
                drawWall(g, x, y, col, row, true);
                break;
            case ',':
                drawRug(g, x, y, col, row);
                break;
            case 'B':
                drawFloor(g, x, y, col, row);
                drawBookshelf(g, x, y, col, row);
                break;
            case 'T':
                drawFloor(g, x, y, col, row);
                drawTable(g, x, y, col, row);
                break;
            case 'P':
                drawFloor(g, x, y, col, row);
                drawPillar(g, x, y, col, row);
                break;
            case 'G':
                drawGate(g, x, y, col, row);
                break;
            case 'A':
                drawFloor(g, x, y, col, row);
                drawAltar(g, x, y, col, row);
                break;
            case 'M':
                drawFloor(g, x, y, col, row);
                drawMemorial(g, x, y, col, row);
                break;
            case 't':
                drawTreeTrunk(g, x, y, col, row);
                break;
            case 'l':
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
                drawFog(g, x, y, col, row);
                break;
            case 'F':
                drawForestPath(g, x, y, col, row);
                break;
            case 'E':
                drawFog(g, x, y, col, row);
                break;
            case 'X':
                if (isValleyTile(col + 1, row) || isValleyTile(col - 1, row)
                        || isValleyTile(col, row + 1) || isValleyTile(col, row - 1)) {
                    drawDirtGround(g, x, y, col, row);
                } else if (isChapelTile(col + 1, row) || isChapelTile(col - 1, row)) {
                    drawChapelFloor(g, x, y, col, row);
                } else if (isArchiveTile(col + 1, row) || isArchiveTile(col - 1, row)) {
                    drawArchiveFloor(g, x, y, col, row);
                } else if (isVillageTile(col + 1, row) || isVillageTile(col - 1, row)) {
                    drawVillageRoad(g, x, y, col, row);
                } else {
                    drawGrass(g, x, y, col, row);
                }
                drawSymbolStone(g, x, y, col, row);
                break;
            case 'v':
                drawVine(g, x, y, col, row);
                break;
            case 'C':
                drawCliffWall(g, x, y, col, row);
                break;
            case 'd':
                drawDirtGround(g, x, y, col, row);
                break;
            case 'w':
                drawShallowWater(g, x, y, col, row);
                break;
            case 'R':
                drawDirtGround(g, x, y, col, row);
                drawRuinsBlock(g, x, y, col, row);
                break;
            case 'Z':
                if (isChapelTile(col + 1, row) || isChapelTile(col - 1, row)) {
                    drawChapelFloor(g, x, y, col, row);
                } else if (isArchiveTile(col + 1, row) || isArchiveTile(col - 1, row)) {
                    drawArchiveFloor(g, x, y, col, row);
                } else {
                    drawDirtGround(g, x, y, col, row);
                }
                drawRestPoint(g, x, y, col, row);
                break;
            case 'K':
                drawLockedPath(g, x, y, col, row);
                break;
            case 'H':
                drawChapelWall(g, x, y, col, row);
                break;
            case 'n':
                drawChapelFloor(g, x, y, col, row);
                drawChapelPew(g, x, y, col, row);
                break;
            case 'a':
                drawChapelFloor(g, x, y, col, row);
                drawChapelAltar(g, x, y, col, row);
                break;
            case 'i':
                drawChapelStainedGlass(g, x, y, col, row);
                break;
            case 'o':
                drawChapelFloor(g, x, y, col, row);
                break;
            case 'm':
                drawChapelMossFloor(g, x, y, col, row);
                break;
            case 'Y':
                drawVillageRoad(g, x, y, col, row);
                break;
            case 'V':
                drawVillageWall(g, x, y, col, row);
                break;
            case 'D':
                drawVillageDoor(g, x, y, col, row);
                break;
            case 'N':
                drawGrass(g, x, y, col, row);
                drawNoticeBoard(g, x, y, col, row);
                break;
            case 'L':
                drawGrass(g, x, y, col, row);
                drawLampPost(g, x, y, col, row);
                break;
            case 'U':
                drawArchiveWall(g, x, y, col, row);
                break;
            case 'b':
                drawArchiveFloor(g, x, y, col, row);
                drawArchiveBookshelf(g, x, y, col, row);
                break;
            case 'q':
                drawArchiveFloor(g, x, y, col, row);
                drawArchiveTable(g, x, y, col, row);
                break;
            case 'e':
                drawArchiveFloor(g, x, y, col, row);
                drawArchiveMechanism(g, x, y, col, row);
                break;
            case 'p':
                drawArchiveFloor(g, x, y, col, row);
                break;
            case 'c':
                drawCorridorFloor(g, x, y, col, row);
                break;
            case '.':
            case 'S':
            default:
                if (ch == 'S' && isValleyTile(col + 1, row)) {
                    drawDirtGround(g, x, y, col, row);
                } else if (ch == 'S' && isForestTile(col + 1, row)) {
                    drawForestPath(g, x, y, col, row);
                } else if (ch == 'S' && isChapelTile(col + 1, row)) {
                    drawChapelFloor(g, x, y, col, row);
                } else if (ch == 'S' && isArchiveTile(col + 1, row)) {
                    drawArchiveFloor(g, x, y, col, row);
                } else if (ch == 'S' && isVillageTile(col + 1, row)) {
                    drawVillageRoad(g, x, y, col, row);
                } else {
                    drawFloor(g, x, y, col, row);
                }
                break;
        }
    }

    private void drawFloor(PGraphics g, int x, int y, int col, int row) {
        // Lantai kayu dengan kontras rendah: hindari grid terang yang membuat
        // lantai terbaca seperti dinding bata.
        g.noStroke();
        g.fill(0xFF2B2226);
        g.rect(x, y, TILE, TILE);

        if (row % 3 == 0) {
            g.fill(0xFF241D21);
            g.rect(x, y, TILE, 1);
        }

        if (col % 4 == 0 && (row & 1) == 0) {
            g.fill(0xFF241D21);
            g.rect(x + 9, y + 1, 1, TILE - 1);
        }

        if ((col * 5 + row * 3) % 7 == 0) {
            g.fill(0xFF33292D);
            g.rect(x + 3, y + 6, 9, 1);
            g.rect(x + 6, y + 11, 5, 1);
        }

        // Bayangan dan papan alas di bawah dinding utara.
        if (isWall(col, row - 1)) {
            g.fill(Palette.WOOD_LIGHT);
            g.rect(x, y + 1, TILE, 2);
            g.fill(Palette.alpha(0x000000, 100));
            g.rect(x, y + 3, TILE, 2);
        }
    }

    private boolean isWall(int col, int row) {
        char ch = tileAt(col, row);
        return ch == '#' || ch == 'W';
    }

    private void drawWall(PGraphics g, int x, int y, int col, int row, boolean window) {
        int variation = (int) (Palette.hash(col * 17 + row * 31) * 3);
        g.noStroke();
        g.fill(variation == 0 ? Palette.STONE_1 : (variation == 1 ? Palette.STONE_2 : 0xFF11121B));
        g.rect(x, y, TILE, TILE);

        // Pola bata.
        g.fill(0xFF0B0C12);
        g.rect(x, y + 7, TILE, 1);
        g.rect(x, y + 15, TILE, 1);
        int offset = ((row & 1) == 0) ? 0 : 8;
        g.rect(x + offset, y, 1, 7);
        g.rect(x + (offset + 8) % TILE, y + 8, 1, 7);

        g.fill(0xFF1A1C27);
        g.rect(x, y, TILE, 1);
        g.fill(0xFF0A0B10);
        g.rect(x, y, 1, TILE);

        if (window) {
            drawWindow(g, x, y);
        }
    }

    private void drawWindow(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(0xFF0A0B12);
        g.rect(x + 3, y + 2, 10, 12);
        g.fill(0xFF2A2C38);
        g.rect(x + 4, y + 3, 8, 10);
        g.fill(0xFF0B1020);
        g.rect(x + 5, y + 4, 6, 8);

        g.fill(Palette.alpha(Palette.MOON, 90));
        g.rect(x + 6, y + 5, 2, 2);
        g.fill(Palette.alpha(Palette.MOON, 40));
        g.rect(x + 9, y + 7, 1, 1);

        g.fill(0xFF2A2C38);
        g.rect(x + 7, y + 4, 1, 8);
        g.rect(x + 5, y + 7, 6, 1);
    }

    private void drawRug(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.RUG);
        g.rect(x, y, TILE, TILE);
        g.fill(0xFF4A2430);
        g.rect(x, y, TILE, 1);
        g.rect(x, y + TILE - 1, TILE, 1);
        g.rect(x, y, 1, TILE);
        g.rect(x + TILE - 1, y, 1, TILE);

        // Motif berlian kecil.
        g.fill(0xFF5C2E3A);
        int cx = x + 8;
        int cy = y + 8;
        g.triangle(cx, cy - 4, cx + 4, cy, cx, cy + 4);
        g.triangle(cx, cy - 4, cx - 4, cy, cx, cy + 4);

        if (((col + row) & 1) == 0) {
            g.fill(0xFF6B3947);
            g.rect(cx - 1, cy - 1, 2, 2);
        }
    }

    private void drawBookshelf(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(0xFF0A090D);
        g.rect(x, y, TILE, TILE);
        g.fill(Palette.WOOD);
        g.rect(x + 1, y + 1, TILE - 2, TILE - 2);
        g.fill(Palette.WOOD_LIGHT);
        g.rect(x + 2, y + 2, TILE - 4, 1);

        int color = BOOK_COLORS[(int) (Palette.hash(col * 13 + row * 7) * BOOK_COLORS.length)];
        int top = y + 4 + (int) (Palette.hash(col * 3 + row * 11) * 3);
        for (int i = 0; i < 4; i++) {
            g.fill(Palette.alpha(color, 220 - i * 12));
            g.rect(x + 2 + i * 3, top, 2, y + 12 - top);
        }
        g.fill(0xFF120D0C);
        g.rect(x + 2, y + 12, TILE - 4, 1);

        g.fill(Palette.alpha(0x000000, 90));
        g.rect(x + 1, y + TILE - 2, TILE - 2, 1);
    }

    private void drawTable(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.alpha(0x000000, 110));
        g.rect(x + 1, y + 4, 14, 12);

        // Permukaan meja.
        g.fill(0xFF3A2925);
        g.rect(x, y + 2, TILE, 10);
        g.fill(0xFF523B31);
        g.rect(x + 1, y + 3, TILE - 2, 2);
        g.fill(0xFF2A1C18);
        g.rect(x, y + 9, TILE, 3);

        // Kaki meja.
        g.fill(0xFF1D1512);
        g.rect(x + 2, y + 12, 2, 4);
        g.rect(x + TILE - 4, y + 12, 2, 4);

        // Lilin kecil sebagai penanda furnitur, bukan objek interaktif.
        if (((col + row) & 1) == 0) {
            g.fill(0xFFB8A88A);
            g.rect(x + 7, y + 4, 2, 4);
            g.fill(Palette.WARM);
            g.rect(x + 7, y + 2, 1, 2);
        }
    }

    private void drawPillar(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.alpha(0x000000, 120));
        g.rect(x + 3, y + 3, TILE - 4, TILE - 3);

        // Badan pilar batu.
        g.fill(0xFF2B2C36);
        g.rect(x + 2, y, TILE - 4, TILE);
        g.fill(0xFF3D3F4C);
        g.rect(x + 3, y + 1, 3, TILE - 2);
        g.fill(0xFF1B1C24);
        g.rect(x + TILE - 7, y + 1, 3, TILE - 2);

        // Kapital dan alas.
        g.fill(0xFF474956);
        g.rect(x, y, TILE, 3);
        g.fill(0xFF3A3C48);
        g.rect(x, y + 3, TILE, 1);
        g.fill(0xFF23242D);
        g.rect(x, y + TILE - 3, TILE, 3);
    }

    private void drawGate(PGraphics g, int x, int y, int col, int row) {
        // Context-aware: gunakan tile dasar sesuai area.
        // Aset pintu besi digambar terpisah lewat drawGates() supaya tidak
        // tertimpa tile baris berikutnya (sprite pintu setinggi 2 tile).
        if (isValleyTile(col + 1, row) || isValleyTile(col - 1, row)) {
            drawDirtGround(g, x, y, col, row);
        } else if (isChapelTile(col + 1, row) || isChapelTile(col - 1, row)) {
            drawChapelFloor(g, x, y, col, row);
        } else if (isArchiveTile(col + 1, row) || isArchiveTile(col - 1, row)) {
            drawArchiveFloor(g, x, y, col, row);
        } else if (isVillageTile(col + 1, row) || isVillageTile(col - 1, row)) {
            drawVillageRoad(g, x, y, col, row);
        } else if (isForestTile(col + 1, row) || isForestTile(col - 1, row)) {
            drawGrass(g, x, y, col, row);
        } else {
            drawFloor(g, x, y, col, row);
        }
    }

    /**
     * Menggambar pintu besi untuk semua gerbang di peta.
     *
     * Sprite pintu setinggi 2 tile (16x32), jadi hanya tile teratas dari
     * setiap deretan vertikal 'G' yang menggambarnya. Gambar selalu dipasang
     * di atas seluruh tile supaya tidak tertimpa baris di bawahnya.
     */
    private void drawGates(PGraphics g, int camX, int camY) {
        if (!drawDoors) {
            return;
        }
        if (gateOpen ? !drawOpenGateDoor : !drawClosedGateDoor) {
            return;
        }
        PImage door = Art.loadImage(gateOpen ? "door_iron_open.png" : "door_iron.png");
        if (door == null) {
            return;
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (tiles[r][c] != 'G') {
                    continue;
                }
                // Hanya baris teratas deretan 'G' vertikal yang menggambar,
                // supaya sprite 2 tile tidak bertumpuk.
                if (r > 0 && tiles[r - 1][c] == 'G') {
                    continue;
                }
                g.image(door, c * TILE - camX, r * TILE - camY);
            }
        }
    }

    private void drawAltar(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.alpha(0x000000, 120));
        g.rect(x + 1, y + 4, 14, 12);

        // Dasar batu altar kuno
        g.fill(0xFF2C2434);
        g.rect(x + 1, y + 2, TILE - 2, 12);
        g.fill(0xFF40364C);
        g.rect(x + 2, y + 3, TILE - 4, 2);

        // Kain penutup beludru ungu altar
        g.fill(0xFF442756);
        g.rect(x + 1, y + 6, TILE - 2, 6);
        g.fill(0xFF5E3977);
        g.rect(x + 2, y + 7, TILE - 4, 1);

        // Lilin investigasi altar
        if ((col & 1) == 0) {
            g.fill(0xFFDCD2C4);
            g.rect(x + 3, y + 1, 2, 5);
            g.fill(Palette.WARM);
            g.rect(x + 3, y - 1, 2, 2);
        } else {
            g.fill(0xFFDCD2C4);
            g.rect(x + 11, y + 1, 2, 5);
            g.fill(Palette.WARM);
            g.rect(x + 11, y - 1, 2, 2);
        }

        // Simbol rune investigasi
        g.fill(0xFF8F76C4);
        g.rect(x + 7, y + 9, 2, 2);
    }

    private void drawMemorial(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.alpha(0x000000, 110));
        g.rect(x + 2, y + 5, 12, 11);

        // Meja arsip memorial
        g.fill(0xFF2D1F1C);
        g.rect(x + 1, y + 2, TILE - 2, 11);
        g.fill(0xFF45302A);
        g.rect(x + 2, y + 3, TILE - 4, 2);

        // Berkas arsip Clara Thorne
        g.fill(0xFFD6C8B0);
        g.rect(x + 3, y + 5, 10, 6);
        g.fill(0xFF6E5F4A);
        g.rect(x + 4, y + 7, 8, 1);
        g.rect(x + 4, y + 9, 6, 1);

        // Bunga putih tanda kehormatan
        g.fill(0xFFF7F2E7);
        g.rect(x + 4, y + 3, 2, 2);
        g.fill(0xFF7FA872);
        g.rect(x + 3, y + 4, 1, 2);
    }

    private void drawTreeTrunk(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.TREE_BARK);
        g.rect(x, y, TILE, TILE);

        // Tekstur kulit kayu vertikal
        int v = (int) (Palette.hash(col * 19 + row * 37) * 4);
        g.fill(Palette.TREE_BARK_LIGHT);
        g.rect(x + 3 + v, y, 2, TILE);
        g.rect(x + 10 - v, y, 1, TILE);

        // Knot / mata kayu
        if ((col * 7 + row * 11) % 5 == 0) {
            g.fill(0xFF1E1209);
            g.rect(x + 6, y + 5, 3, 3);
            g.fill(Palette.TREE_BARK_LIGHT);
            g.rect(x + 7, y + 6, 1, 1);
        }

        // Garis horizontal retak
        g.fill(0xFF1E1209);
        g.rect(x, y + 7 + (v & 1), TILE, 1);
    }

    private void drawLeaves(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        int variation = (int) (Palette.hash(col * 23 + row * 41) * 3);
        g.fill(variation == 0 ? Palette.LEAVES_DARK : (variation == 1 ? Palette.LEAVES : 0xFF142A18));
        g.rect(x, y, TILE, TILE);

        // Variasi daun dan celah cahaya
        g.fill(Palette.LEAVES_LIGHT);
        int lx = (int) (Palette.hash(col * 11 + row * 29) * 10);
        int ly = (int) (Palette.hash(col * 7 + row * 13) * 10);
        g.rect(x + lx, y + ly, 3, 2);
        g.rect(x + (lx + 7) % 14, y + (ly + 6) % 12, 2, 3);

        // Celah gelap antar daun
        g.fill(Palette.LEAVES_DARK);
        g.rect(x + (lx + 4) % 14 + 1, y + (ly + 3) % 12, 1, 2);
    }

    private void drawGrass(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.GRASS);
        g.rect(x, y, TILE, TILE);

        // Variasi rerumputan
        int v = (int) (Palette.hash(col * 13 + row * 17) * 4);
        g.fill(Palette.GRASS_LIGHT);
        g.rect(x + 2 + v, y + 4, 1, 3);
        g.rect(x + 8 - v, y + 8, 1, 2);
        g.rect(x + 12, y + 2 + v, 1, 3);

        // Tanah gelap di antara rumput
        if ((col + row) % 3 == 0) {
            g.fill(0xFF151F15);
            g.rect(x + 5, y + 10, 4, 1);
        }

        // Bayangan dari kanopi di atas
        if (isCanopy(col, row - 1)) {
            g.fill(Palette.alpha(0x000000, 50));
            g.rect(x, y, TILE, 4);
        }
    }

    private void drawRock(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();

        // Bayangan batu
        g.fill(Palette.alpha(0x000000, 80));
        g.rect(x + 2, y + 5, 13, 11);

        // Badan batu
        g.fill(0xFF2C2E38);
        g.rect(x + 1, y + 3, 14, 11);
        g.fill(0xFF383A46);
        g.rect(x + 2, y + 4, 12, 3);
        g.fill(0xFF202228);
        g.rect(x + 2, y + 10, 12, 3);

        // Lumut pada batu
        g.fill(Palette.MOSS);
        g.rect(x + 2, y + 6, 3, 2);
        g.rect(x + 10, y + 8, 3, 2);
    }

    private void drawFog(PGraphics g, int x, int y, int col, int row) {
        if (isValleyTile(col + 1, row) || isValleyTile(col - 1, row)
                || isValleyTile(col, row + 1) || isValleyTile(col, row - 1)) {
            drawDirtGround(g, x, y, col, row);
        } else {
            drawGrass(g, x, y, col, row);
        }
        g.noStroke();

        // Lapisan kabut tembus pandang
        int fogAlpha = 30 + (int) (Palette.hash(col * 29 + row * 43) * 25);
        g.fill(Palette.alpha(Palette.FOG_LIGHT, fogAlpha));
        g.rect(x, y, TILE, TILE);

        // Gumpalan kabut individual
        int fx = (int) (Palette.hash(col * 7 + row * 19) * 8);
        int fy = (int) (Palette.hash(col * 3 + row * 23) * 8);
        g.fill(Palette.alpha(Palette.FOG_LIGHT, fogAlpha + 15));
        g.rect(x + fx, y + fy, 6, 4);
        g.rect(x + (fx + 9) % 12, y + (fy + 7) % 11, 4, 3);
    }

    private void drawForestPath(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.FOREST_PATH);
        g.rect(x, y, TILE, TILE);

        // Variasi tekstur tanah setapak
        int v = (int) (Palette.hash(col * 17 + row * 31) * 3);
        g.fill(Palette.FOREST_PATH_LIGHT);
        g.rect(x + 2 + v * 2, y + 6, 5, 1);
        g.rect(x + 7, y + 11 - v, 4, 1);

        // Kerikil kecil
        if ((col * 3 + row * 7) % 5 == 0) {
            g.fill(0xFF3D352C);
            g.rect(x + 4, y + 3, 2, 2);
            g.rect(x + 10, y + 9, 2, 1);
        }

        // Tepi rumput di sisi jalan
        boolean grassAbove = isGrassLike(col, row - 1);
        boolean grassBelow = isGrassLike(col, row + 1);
        if (grassAbove) {
            g.fill(Palette.GRASS);
            g.rect(x, y, TILE, 2);
        }
        if (grassBelow) {
            g.fill(Palette.GRASS);
            g.rect(x, y + TILE - 2, TILE, 2);
        }
    }

    private void drawSymbolStone(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();

        // Bayangan batu
        g.fill(Palette.alpha(0x000000, 100));
        g.rect(x + 1, y + 5, 14, 11);

        // Badan batu simbol
        g.fill(0xFF2A2C36);
        g.rect(x + 2, y + 2, 12, 12);
        g.fill(0xFF353842);
        g.rect(x + 3, y + 3, 10, 3);
        g.fill(0xFF1E2028);
        g.rect(x + 3, y + 10, 10, 3);

        // Simbol ukiran rune
        g.fill(Palette.alpha(Palette.SYMBOL_GLOW, 90));
        g.rect(x + 6, y + 4, 4, 1);
        g.rect(x + 5, y + 6, 1, 3);
        g.rect(x + 10, y + 6, 1, 3);
        g.rect(x + 7, y + 9, 2, 1);
        g.fill(Palette.alpha(Palette.SYMBOL_GLOW, 60));
        g.rect(x + 7, y + 6, 2, 2);
    }

    private void drawCliffWall(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        int v = (int) (Palette.hash(col * 23 + row * 37) * 3);
        g.fill(v == 0 ? Palette.CLIFF : (v == 1 ? Palette.CLIFF_LIGHT : Palette.CLIFF_DARK));
        g.rect(x, y, TILE, TILE);

        // Strata horizontal — lapisan batuan
        g.fill(Palette.CLIFF_DARK);
        int sy = 3 + (int) (Palette.hash(col * 11 + row * 19) * 4);
        g.rect(x, y + sy, TILE, 1);
        g.rect(x, y + sy + 6, TILE, 1);

        // Celah vertikal
        int cx = (int) (Palette.hash(col * 7 + row * 29) * 10);
        g.fill(0xFF0A0910);
        g.rect(x + cx + 2, y, 1, sy);

        // Highlight tepi atas
        g.fill(Palette.CLIFF_LIGHT);
        g.rect(x, y, TILE, 1);
        g.fill(Palette.CLIFF_DARK);
        g.rect(x, y + TILE - 1, TILE, 1);
    }

    private void drawDirtGround(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.DIRT);
        g.rect(x, y, TILE, TILE);

        // Variasi tekstur tanah
        int v = (int) (Palette.hash(col * 17 + row * 23) * 4);
        g.fill(Palette.DIRT_LIGHT);
        g.rect(x + 3 + v, y + 5, 2, 1);
        g.rect(x + 9 - v, y + 10, 3, 1);

        // Kerikil kecil
        if ((col * 5 + row * 3) % 7 == 0) {
            g.fill(Palette.DIRT_DARK);
            g.rect(x + 6, y + 3, 2, 2);
            g.rect(x + 11, y + 8, 2, 1);
        }

        // Bayangan dari tebing di atas
        if (isCliff(col, row - 1)) {
            g.fill(Palette.alpha(0x000000, 60));
            g.rect(x, y, TILE, 5);
        }
    }

    private void drawShallowWater(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.SHALLOW_WATER);
        g.rect(x, y, TILE, TILE);

        // Riak air
        int v = (int) (Palette.hash(col * 13 + row * 31) * 3);
        g.fill(Palette.SHALLOW_WATER_LIGHT);
        g.rect(x + 1 + v * 2, y + 4, 6, 1);
        g.rect(x + 5, y + 9 - v, 5, 1);

        // Kilau permukaan
        g.fill(Palette.alpha(Palette.SHALLOW_WATER_SURFACE, 80));
        int sx = (int) (Palette.hash(col * 7 + row * 17) * 8);
        g.rect(x + sx + 2, y + 3, 2, 1);
        g.rect(x + (sx + 6) % 12, y + 11, 3, 1);

        // Tepi lumpur
        if (isDirt(col, row - 1)) {
            g.fill(Palette.DIRT_DARK);
            g.rect(x, y, TILE, 2);
        }
        if (isDirt(col, row + 1)) {
            g.fill(Palette.DIRT_DARK);
            g.rect(x, y + TILE - 2, TILE, 2);
        }
    }

    private void drawVine(PGraphics g, int x, int y, int col, int row) {
        // Latar belakang: tebing di belakang tanaman rambat
        drawCliffWall(g, x, y, col, row);
        g.noStroke();

        // Sulur utama
        int v = (int) (Palette.hash(col * 19 + row * 29) * 3);
        g.fill(Palette.VINE);
        g.rect(x + 5 + v, y, 2, TILE);
        g.rect(x + 10 - v, y, 2, TILE);

        // Daun-daun kecil
        g.fill(Palette.VINE_LIGHT);
        int lx = (int) (Palette.hash(col * 11 + row * 23) * 6);
        g.rect(x + 3 + lx, y + 3, 3, 2);
        g.rect(x + 7 - lx / 2, y + 9, 3, 2);
        g.rect(x + lx + 1, y + 13, 2, 2);

        // Sulur melengkung
        g.fill(Palette.VINE);
        g.rect(x + 4 + v, y + 6, 1, 3);
        g.rect(x + 11 - v, y + 4, 1, 2);
    }

    private void drawRuinsBlock(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();

        // Bayangan
        g.fill(Palette.alpha(0x000000, 100));
        g.rect(x + 2, y + 4, 13, 12);

        // Badan batu reruntuhan
        g.fill(Palette.RUINS);
        g.rect(x + 1, y + 2, 14, 12);
        g.fill(Palette.RUINS_LIGHT);
        g.rect(x + 2, y + 3, 12, 3);
        g.fill(Palette.RUINS_DARK);
        g.rect(x + 2, y + 10, 12, 3);

        // Retakan
        g.fill(0xFF0A0812);
        int cx = (int) (Palette.hash(col * 13 + row * 41) * 6);
        g.rect(x + 4 + cx, y + 4, 1, 5);
        g.rect(x + 4 + cx, y + 7, 3, 1);

        // Lumut pada reruntuhan
        g.fill(Palette.RUINS_MOSS);
        g.rect(x + 2, y + 11, 4, 2);
        if ((col + row) % 3 == 0) {
            g.rect(x + 9, y + 5, 3, 2);
        }
    }

    private void drawRestPoint(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();

        // Lingkaran batu dasar
        g.fill(Palette.RUINS_LIGHT);
        g.rect(x + 3, y + 4, 10, 10);
        g.fill(Palette.RUINS);
        g.rect(x + 4, y + 5, 8, 8);

        // Cahaya hangat api kecil
        g.fill(Palette.alpha(Palette.REST_GLOW, 40));
        g.rect(x + 2, y + 2, 12, 12);

        // Api kecil di tengah
        g.fill(Palette.WARM);
        g.rect(x + 7, y + 6, 2, 4);
        g.fill(0xFFE8C46C);
        g.rect(x + 7, y + 5, 2, 2);
        g.fill(Palette.DANGER);
        g.rect(x + 6, y + 8, 1, 2);
        g.rect(x + 9, y + 8, 1, 2);

        // Glow di sekitar
        g.fill(Palette.alpha(Palette.REST_GLOW, 20));
        g.rect(x, y, TILE, TILE);
    }

    private void drawLockedPath(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();

        if (!gateOpen) {
            // Pintu batu tersegel
            g.fill(Palette.LOCKED_STONE);
            g.rect(x, y, TILE, TILE);

            // Pola bata besar
            g.fill(0xFF1E1826);
            g.rect(x, y + 7, TILE, 1);
            g.rect(x + 7, y, 1, 7);
            g.rect(x + 11, y + 8, 1, 8);

            // Frame batu
            g.fill(Palette.RUINS_DARK);
            g.rect(x, y, TILE, 2);
            g.rect(x, y + TILE - 2, TILE, 2);
            g.rect(x, y, 2, TILE);
            g.rect(x + TILE - 2, y, 2, TILE);

            // Sigil terkunci bercahaya
            g.fill(Palette.alpha(Palette.LOCKED_SIGIL, 120));
            g.rect(x + 5, y + 4, 6, 1);
            g.rect(x + 7, y + 5, 2, 4);
            g.rect(x + 5, y + 9, 6, 1);
            g.rect(x + 5, y + 5, 1, 2);
            g.rect(x + 10, y + 7, 1, 2);

            // Pulse redup
            g.fill(Palette.alpha(Palette.LOCKED_SIGIL, 30));
            g.rect(x + 3, y + 2, 10, 12);
        } else {
            // Pintu terbuka: celah gelap
            drawDirtGround(g, x, y, col, row);
            g.fill(Palette.RUINS_DARK);
            g.rect(x, y, 2, TILE);
            g.rect(x + TILE - 2, y, 2, TILE);
            g.fill(Palette.alpha(Palette.MAGIC, 10));
            g.rect(x + 3, y + 2, TILE - 6, TILE - 4);
        }
    }

    private boolean isCliff(int col, int row) {
        char ch = tileAt(col, row);
        return ch == 'C' || ch == 'v';
    }

    private boolean isDirt(int col, int row) {
        char ch = tileAt(col, row);
        return ch == 'd' || ch == 'Z' || ch == 'R';
    }

    private boolean isValleyTile(int col, int row) {
        char ch = tileAt(col, row);
        return ch == 'C' || ch == 'v' || ch == 'd' || ch == 'w'
                || ch == 'R' || ch == 'Z' || ch == 'K';
    }

    private boolean isCanopy(int col, int row) {
        char ch = tileAt(col, row);
        return ch == 'l' || ch == 't';
    }

    private boolean isGrassLike(int col, int row) {
        char ch = tileAt(col, row);
        return ch == 'g' || ch == '~' || ch == 'r';
    }

    private boolean isForestTile(int col, int row) {
        char ch = tileAt(col, row);
        return ch == 'g' || ch == 'F' || ch == '~' || ch == 'E' || ch == 't'
                || ch == 'l' || ch == 'r' || ch == 'X' || ch == 'S';
    }

    // --- Chapel Drawing Methods (Bab 4) ---
    private void drawChapelWall(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.CHAPEL_STONE);
        g.rect(x, y, TILE, TILE);
        g.fill(Palette.CHAPEL_STONE_DARK);
        g.rect(x, y + 7, TILE, 1);
        g.rect(x + (col % 2 == 0 ? 5 : 11), y, 1, 7);
        g.rect(x + (col % 2 == 0 ? 11 : 5), y + 8, 1, 8);
        g.fill(Palette.CHAPEL_STONE_LIGHT);
        g.rect(x, y, TILE, 1);
    }

    private void drawChapelFloor(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.CHAPEL_FLOOR);
        g.rect(x, y, TILE, TILE);
        if ((col + row) % 2 == 0) {
            g.fill(Palette.CHAPEL_FLOOR_LIGHT);
            g.rect(x + 2, y + 2, 4, 4);
        }
    }

    private void drawChapelMossFloor(PGraphics g, int x, int y, int col, int row) {
        drawChapelFloor(g, x, y, col, row);
        g.fill(Palette.CHAPEL_MOSS);
        g.rect(x + 2, y + 3, 5, 4);
        g.rect(x + 9, y + 8, 4, 5);
    }

    private void drawChapelPew(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.PEW_WOOD);
        g.rect(x + 1, y + 3, 14, 10);
        g.fill(Palette.PEW_WOOD_LIGHT);
        g.rect(x + 2, y + 3, 12, 2);
        g.rect(x + 1, y + 5, 2, 8);
        g.rect(x + 13, y + 5, 2, 8);
    }

    private void drawChapelAltar(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.ALTAR_STONE);
        g.rect(x + 2, y + 3, 12, 11);
        g.fill(Palette.alpha(Palette.ALTAR_GLOW, 80));
        g.rect(x + 4, y + 5, 8, 4);
        g.fill(Palette.ALTAR_GLOW);
        g.rect(x + 7, y + 6, 2, 2);
    }

    private void drawChapelStainedGlass(PGraphics g, int x, int y, int col, int row) {
        drawChapelWall(g, x, y, col, row);
        g.fill(0xFF100E14);
        g.rect(x + 3, y + 2, 10, 12);
        int glassColor = (col % 3 == 0) ? Palette.STAINED_GLASS_R : (col % 3 == 1 ? Palette.STAINED_GLASS_B : Palette.STAINED_GLASS_G);
        g.fill(Palette.alpha(glassColor, 180));
        g.rect(x + 4, y + 3, 8, 10);
        g.fill(0xFF18141E);
        g.rect(x + 7, y + 3, 2, 10);
        g.rect(x + 4, y + 7, 8, 2);
    }

    // --- Village Drawing Methods (Prolog) ---
    private void drawVillageRoad(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.VILLAGE_ROAD);
        g.rect(x, y, TILE, TILE);
        if ((col * 7 + row * 13) % 4 == 0) {
            g.fill(Palette.VILLAGE_ROAD_LIGHT);
            g.rect(x + 3, y + 3, 4, 3);
            g.rect(x + 9, y + 8, 5, 4);
        }
    }

    private void drawVillageWall(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.VILLAGE_WALL);
        g.rect(x, y, TILE, TILE);
        g.fill(Palette.VILLAGE_WALL_LIGHT);
        g.rect(x, y + 4, TILE, 1);
        g.rect(x, y + 10, TILE, 1);
        g.fill(0xFF1C1614);
        g.rect(x + (row % 2 == 0 ? 6 : 12), y, 1, 4);
        g.rect(x + (row % 2 == 0 ? 12 : 6), y + 5, 1, 5);
    }

    private void drawVillageDoor(PGraphics g, int x, int y, int col, int row) {
        drawVillageWall(g, x, y, col, row);
        g.fill(Palette.VILLAGE_DOOR);
        g.rect(x + 3, y + 2, 10, 14);
        g.fill(Palette.ACCENT_DIM);
        g.rect(x + 10, y + 8, 2, 2);
    }

    private void drawNoticeBoard(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.NOTICE_BOARD);
        g.rect(x + 2, y + 2, 12, 10);
        g.rect(x + 4, y + 12, 2, 4);
        g.rect(x + 10, y + 12, 2, 4);
        g.fill(Palette.NOTICE_PAPER);
        g.rect(x + 4, y + 4, 4, 5);
        g.rect(x + 9, y + 5, 4, 4);
    }

    private void drawLampPost(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.VILLAGE_LAMP_POST);
        g.rect(x + 7, y + 4, 2, 12);
        g.fill(Palette.alpha(Palette.VILLAGE_LAMP, 50));
        g.rect(x + 4, y + 1, 8, 8);
        g.fill(Palette.VILLAGE_LAMP);
        g.rect(x + 6, y + 3, 4, 4);
    }

    // --- Archive Drawing Methods (Bab 5) ---
    private void drawArchiveWall(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.ARCHIVE_WALL);
        g.rect(x, y, TILE, TILE);
        g.fill(Palette.ARCHIVE_WALL_LIGHT);
        g.rect(x, y, TILE, 1);
        g.rect(x + 7, y, 1, 7);
        g.rect(x + 11, y + 8, 1, 8);
    }

    private void drawArchiveFloor(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.ARCHIVE_FLOOR);
        g.rect(x, y, TILE, TILE);
        if ((col + row) % 3 == 0) {
            g.fill(Palette.ARCHIVE_FLOOR_LIGHT);
            g.rect(x + 1, y + 1, 5, 5);
        }
    }

    private void drawCorridorFloor(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(0xFF14121C);
        g.rect(x, y, TILE, TILE);
        g.fill(0xFF1E1A26);
        g.rect(x + 3, y + 3, 10, 10);
    }

    private void drawArchiveBookshelf(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.ARCHIVE_SHELF);
        g.rect(x + 1, y, 14, TILE);
        g.fill(Palette.ARCHIVE_SHELF_LIGHT);
        g.rect(x + 1, y + 7, 14, 1);
        g.rect(x + 1, y + 15, 14, 1);
        g.fill(Palette.ARCHIVE_BOOK_1);
        g.rect(x + 3, y + 2, 2, 5);
        g.rect(x + 9, y + 9, 2, 6);
        g.fill(Palette.ARCHIVE_BOOK_2);
        g.rect(x + 6, y + 1, 3, 6);
        g.fill(Palette.ARCHIVE_BOOK_3);
        g.rect(x + 10, y + 2, 2, 5);
        g.rect(x + 4, y + 10, 3, 5);
    }

    private void drawArchiveTable(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.ARCHIVE_TABLE);
        g.rect(x + 2, y + 4, 12, 10);
        g.fill(Palette.ARCHIVE_PAPER);
        g.rect(x + 4, y + 6, 4, 5);
        g.rect(x + 9, y + 7, 3, 4);
    }

    private void drawArchiveMechanism(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.MECHANISM);
        g.rect(x + 2, y + 2, 12, 12);
        g.fill(Palette.MECHANISM_LIGHT);
        g.rect(x + 4, y + 4, 8, 8);
        g.fill(Palette.alpha(Palette.MAGIC, 80));
        g.rect(x + 6, y + 6, 4, 4);
    }

    // --- Area Helpers ---
    private boolean isChapelTile(int col, int row) {
        char ch = tileAt(col, row);
        return ch == 'H' || ch == 'n' || ch == 'a' || ch == 'i' || ch == 'o' || ch == 'm';
    }

    private boolean isVillageTile(int col, int row) {
        char ch = tileAt(col, row);
        return ch == 'Y' || ch == 'V' || ch == 'D' || ch == 'N' || ch == 'L';
    }

    private boolean isArchiveTile(int col, int row) {
        char ch = tileAt(col, row);
        return ch == 'U' || ch == 'b' || ch == 'q' || ch == 'e' || ch == 'p' || ch == 'c';
    }

    private static final int[] BOOK_COLORS = {
            0xFF6B4B4B,
            0xFF4C5A6B,
            0xFF5C4E6B,
            0xFF6B6042,
            0xFF465A52,
            0xFF5A4657
    };
}
