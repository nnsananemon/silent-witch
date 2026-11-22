/**
 * Palet warna bersama untuk seluruh scene.
 *
 * Warna disimpan sebagai ARGB 32-bit agar dapat langsung dipakai oleh
 * PGraphics tanpa bergantung pada instance PApplet.
 */
public final class Palette {

    public static final int VOID = 0xFF04050A;
    public static final int INK = 0xFF0A0C13;
    public static final int PANEL = 0xFF11141F;
    public static final int PANEL_LIGHT = 0xFF1A1D2C;
    public static final int PANEL_DARK = 0xFF0C0E17;
    public static final int BORDER = 0xFF453F5C;
    public static final int BORDER_DIM = 0xFF262238;

    public static final int TEXT = 0xFFDCD6C6;
    public static final int TEXT_DIM = 0xFF9791A6;
    public static final int TEXT_MUTED = 0xFF625D72;

    public static final int ACCENT = 0xFFE3B66C;
    public static final int ACCENT_DIM = 0xFF8B693C;
    public static final int MAGIC = 0xFFA88AD8;
    public static final int MAGIC_DIM = 0xFF4E3D6B;
    public static final int DANGER = 0xFFC97A7A;

    public static final int MOON = 0xFFE9E5D3;
    public static final int NIGHT_TOP = 0xFF070910;
    public static final int NIGHT_BOTTOM = 0xFF1B1C2E;
    public static final int FOG = 0xFF93A2BC;
    public static final int WARM = 0xFFE8A54B;
    public static final int WARM_DIM = 0xFF7A4E24;

    public static final int WOOD = 0xFF2A1E1C;
    public static final int WOOD_LIGHT = 0xFF3A2925;
    public static final int STONE_1 = 0xFF14151E;
    public static final int STONE_2 = 0xFF181A25;
    public static final int STONE_3 = 0xFF101119;
    public static final int RUG = 0xFF341823;

    // Hollow Woods (Bab 2) — Ebony Teal Gothic Forest
    public static final int TREE_BARK = 0xFF162024;
    public static final int TREE_BARK_LIGHT = 0xFF243038;
    public static final int LEAVES_DARK = 0xFF0A1318;
    public static final int LEAVES = 0xFF152630;
    public static final int LEAVES_LIGHT = 0xFF233A44;
    public static final int GRASS = 0xFF14232C;
    public static final int GRASS_LIGHT = 0xFF21323A;
    public static final int MOSS = 0xFF203A38;
    public static final int FOG_LIGHT = 0xFF98CED6;
    public static final int FOREST_PATH = 0xFF1C2A2E;
    public static final int FOREST_PATH_LIGHT = 0xFF2C3D42;
    public static final int SYMBOL_GLOW = 0xFF5DE0D2;

    // Lembah Berkabut (Bab 3)
    public static final int CLIFF = 0xFF1C1A22;
    public static final int CLIFF_LIGHT = 0xFF2A2730;
    public static final int CLIFF_DARK = 0xFF100F15;
    public static final int DIRT = 0xFF26201A;
    public static final int DIRT_LIGHT = 0xFF342C22;
    public static final int DIRT_DARK = 0xFF1A160F;
    public static final int SHALLOW_WATER = 0xFF141E2A;
    public static final int SHALLOW_WATER_LIGHT = 0xFF1E2E3E;
    public static final int SHALLOW_WATER_SURFACE = 0xFF2A4058;
    public static final int VINE = 0xFF1E3818;
    public static final int VINE_LIGHT = 0xFF2A4A22;
    public static final int RUINS = 0xFF201C28;
    public static final int RUINS_LIGHT = 0xFF302A38;
    public static final int RUINS_DARK = 0xFF141220;
    public static final int RUINS_MOSS = 0xFF1E2E1A;
    public static final int REST_GLOW = 0xFFB8A060;
    public static final int LOCKED_STONE = 0xFF2A1E30;
    public static final int LOCKED_SIGIL = 0xFF6E4A8A;

    // Ashen Village (Prolog)
    public static final int VILLAGE_ROAD = 0xFF282428;
    public static final int VILLAGE_ROAD_LIGHT = 0xFF343034;
    public static final int VILLAGE_WALL = 0xFF2A2220;
    public static final int VILLAGE_WALL_LIGHT = 0xFF3A322E;
    public static final int VILLAGE_DOOR = 0xFF3A2A1E;
    public static final int VILLAGE_LAMP = 0xFFD8C888;
    public static final int VILLAGE_LAMP_POST = 0xFF484048;
    public static final int NOTICE_BOARD = 0xFF3A3028;
    public static final int NOTICE_PAPER = 0xFFD0C8B0;

    // Chapel of Whispers (Bab 4)
    public static final int CHAPEL_STONE = 0xFF1E1A24;
    public static final int CHAPEL_STONE_LIGHT = 0xFF2C2634;
    public static final int CHAPEL_STONE_DARK = 0xFF12101A;
    public static final int CHAPEL_FLOOR = 0xFF221E28;
    public static final int CHAPEL_FLOOR_LIGHT = 0xFF2E2A36;
    public static final int CHAPEL_MOSS = 0xFF1C2E1A;
    public static final int PEW_WOOD = 0xFF2A1E16;
    public static final int PEW_WOOD_LIGHT = 0xFF3A2C22;
    public static final int ALTAR_STONE = 0xFF28223A;
    public static final int ALTAR_GLOW = 0xFF8A7AB8;
    public static final int STAINED_GLASS_R = 0xFF6A2838;
    public static final int STAINED_GLASS_B = 0xFF283868;
    public static final int STAINED_GLASS_G = 0xFF285038;
    public static final int CHAPEL_MORTAR = 0xFF0D0B12;
    public static final int CHAPEL_STONE_EDGE = 0xFF3A3244;
    public static final int CHAPEL_CARPET = 0xFF3A1626;
    public static final int CHAPEL_CARPET_LIGHT = 0xFF5A2438;
    public static final int CHAPEL_CARPET_GOLD = 0xFF8A6A32;
    public static final int CHAPEL_GLASS_LEAD = 0xFF0C0A12;
    public static final int CHAPEL_GLASS_GLOW = 0xFFB7A6D8;
    public static final int CHAPEL_CANDLE = 0xFFF0C878;

    // Underground Archive (Bab 5)
    public static final int ARCHIVE_WALL = 0xFF161220;
    public static final int ARCHIVE_WALL_LIGHT = 0xFF221C2E;
    public static final int ARCHIVE_FLOOR = 0xFF1A1824;
    public static final int ARCHIVE_FLOOR_LIGHT = 0xFF24222E;
    public static final int ARCHIVE_SHELF = 0xFF2C2218;
    public static final int ARCHIVE_SHELF_LIGHT = 0xFF3C3024;
    public static final int ARCHIVE_BOOK_1 = 0xFF6A2828;
    public static final int ARCHIVE_BOOK_2 = 0xFF28426A;
    public static final int ARCHIVE_BOOK_3 = 0xFF2A6A3A;
    public static final int ARCHIVE_TABLE = 0xFF2A221E;
    public static final int ARCHIVE_PAPER = 0xFFD8D0BA;
    public static final int MECHANISM = 0xFF383040;
    public static final int MECHANISM_LIGHT = 0xFF4A4258;

    private Palette() {
        // Kelas konstanta.
    }

    /** Menambahkan alpha 0..255 ke warna RGB/ARGB. */
    public static int alpha(int color, int alpha) {
        return ((alpha & 0xFF) << 24) | (color & 0x00FFFFFF);
    }

    /** Mencampur dua warna tanpa memakai objek PApplet. */
    public static int mix(int c1, int c2, float t) {
        if (t <= 0f) {
            return c1;
        }
        if (t >= 1f) {
            return c2;
        }
        int a1 = (c1 >> 24) & 0xFF;
        int r1 = (c1 >> 16) & 0xFF;
        int g1 = (c1 >> 8) & 0xFF;
        int b1 = c1 & 0xFF;
        int a2 = (c2 >> 24) & 0xFF;
        int r2 = (c2 >> 16) & 0xFF;
        int g2 = (c2 >> 8) & 0xFF;
        int b2 = c2 & 0xFF;
        int a = (int) (a1 + (a2 - a1) * t);
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /** Hash deterministik untuk variasi visual (bukan logika kasus). */
    public static float hash(int n) {
        double x = Math.sin(n * 12.9898 + 78.233) * 43758.5453;
        return (float) (x - Math.floor(x));
    }
}
