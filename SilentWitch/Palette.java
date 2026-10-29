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
