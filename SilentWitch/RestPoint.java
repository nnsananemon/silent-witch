import processing.core.PGraphics;

/**
 * Titik istirahat di dunia permainan.
 *
 * Mendukung beberapa gaya visual sesuai tema area:
 * - BRAZIER: Tungku perapian batu berukir dungeon masonry (semua area gua/lembah).
 * - CANDLE: Lilin altar kapel (cahaya lembut di chapel of whispers).
 * - CAMPFIRE: Lingkaran batu api unggun tradisional (hutan & default).
 *
 * Saat berinteraksi, pemain menerima dialog tenang tentang suasana
 * dan Casebook menyajikan ringkasan tujuan pencarian saat ini.
 *
 * Tidak memiliki state — selalu tersedia untuk digunakan.
 */
public final class RestPoint implements Interactable {

    public enum Style {
        BRAZIER,
        CANDLE,
        CAMPFIRE
    }

    private final float x;
    private final float y;
    private final float radius;
    private final Style style;

    public RestPoint(float x, float y, float radius, Style style) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius RestPoint harus lebih dari nol.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.style = style != null ? style : Style.CAMPFIRE;
    }

    public RestPoint(float x, float y, Style style) {
        this(x, y, 26f, style);
    }

    public RestPoint(float x, float y, float radius) {
        this(x, y, radius, Style.CAMPFIRE);
    }

    public RestPoint(float x, float y) {
        this(x, y, 26f, Style.CAMPFIRE);
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public float radius() {
        return radius;
    }

    public Style style() {
        return style;
    }

    /**
     * Mengembalikan kotak penghalang fisik [centerX, centerY, width, height]
     * yang dipakai Player.addBlocker. Geometri dihitung dari gaya visual
     * sehingga blocker selalu menempel tepat pada bagian yang solid — tidak
     * lebih besar (koordinat lama) dan tidak lebih kecil dari yang terlihat.
     *
     * - BRAZIER: nyala api + tapak masonry, total 16x22 di atas y titik istirahat.
     * - CANDLE : lilin kecil di altar; cukup penghalang 10x12.
     * - CAMPFIRE: lingkaran batu api unggun.
     */
    public float[] blockerRect() {
        switch (style) {
            case BRAZIER:
                // Sprite 16x32 digambar dengan dasar di (x, y):
                //   nyala api  -> y-22 .. y-16
                //   tapak batu -> y-16 .. y
                // Blocker harus menutup keduanya, bukan hanya tapaknya,
                // supaya pemain tidak bisa menembus nyala api dari atas/samping.
                return new float[]{x, y - 11f, 16f, 22f};
            case CANDLE:
                return new float[]{x, y - 4f, 10f, 12f};
            case CAMPFIRE:
            default:
                return new float[]{x, y - 6f, 20f, 18f};
        }
    }

    @Override
    public String prompt() {
        return "ISTIRAHAT";
    }

    @Override
    public Dialogue createDialogue() {
        if (style == Style.BRAZIER) {
            return new Dialogue("BRAZIER BATU KUNO",
                    "Tungku perapian batu dengan ukiran masonry kuno berdiri kokoh di atas lantai.",
                    "Nyala apinya berkobar tenang di dalam wadah perunggu, memancarkan kehangatan stabil.",
                    "Kau duduk beristirahat di dekat tungku batu ini. Hawa dingin lembah perlahan sirna.",
                    "Napasmu teratur kembali. Pikiranmu jernih dan tenagamu pulih sepenuhnya.");
        }
        if (style == Style.CANDLE) {
            return new Dialogue("LILIN ALTAR RERUNTUHAN",
                    "Lilin-lilin di atas altar batu menyala hening di tengah kesunyian kapel.",
                    "Aroma lilin tua dan debu dupa menenangkan jiwamu yang lelah.",
                    "Kau duduk bersimpuh sejenak di dekat altar, mengumpulkan kembali ketenangan batin.",
                    "Kekuatanmu pulih untuk melangkah lebih dalam ke arsip bawah tanah.");
        }
        return new Dialogue("LINGKARAN BATU",
                "Batu-batu kecil tersusun melingkar di tanah lembab, mengelilingi bara api yang masih menyala.",
                "Kehangatan samar meresap dari bara, mengusir dingin kabut sejenak.",
                "Kau duduk sejenak di tepi lingkaran batu. Suara hutan terasa jauh di sini.",
                "Napasmu melambat. Pikiran kembali jernih — tujuanmu menanti di kedalaman lembah.");
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        int px = Math.round(x - cameraX);
        int py = Math.round(y - cameraY);
        float animTime = (System.currentTimeMillis() % 100000) / 1000f;

        if (style == Style.BRAZIER) {
            if (Art.brazierSpriteReady()) {
                Art.drawBrazier(g, px, py, animTime);
                return;
            }
        } else if (style == Style.CANDLE) {
            // Lilin di altar sudah tergambar di latar chapel; berikan kedip cahaya lembut
            float flicker = 1f + 0.08f * (float) Math.sin(animTime * 6.3f);
            g.noStroke();
            g.fill(255, 200, 100, 14);
            g.ellipse(px, py - 4, 28 * flicker, 20 * flicker);
            g.fill(255, 220, 140, 22);
            g.ellipse(px, py - 4, 16 * flicker, 12 * flicker);
            return;
        }

        // Default CAMPFIRE / spirit fire:
        if (Art.campfireSpriteReady()) {
            Art.drawCampfire(g, px, py, animTime);
            return;
        }

        g.noStroke();
        g.fill(Palette.alpha(Palette.REST_GLOW, 14));
        g.ellipse(px, py, 34, 34);
        g.fill(Palette.alpha(Palette.REST_GLOW, 28));
        g.ellipse(px, py, 20, 20);
        g.fill(Palette.alpha(Palette.WARM, 45));
        g.ellipse(px, py, 8, 8);
        g.fill(Palette.alpha(Palette.WARM, 70));
        g.rect(px - 2, py - 2, 4, 4);
    }
}
