import processing.core.PGraphics;

/**
 * Titik istirahat di Lembah Berkabut (Bab 3).
 *
 * Berupa lingkaran batu kecil dengan api hangat di tengahnya.
 * Saat berinteraksi, pemain menerima dialog tenang tentang suasana
 * dan Casebook menyajikan ringkasan tujuan pencarian saat ini.
 *
 * Tidak memiliki state — selalu tersedia untuk digunakan.
 */
public final class RestPoint implements Interactable {

    private final float x;
    private final float y;
    private final float radius;

    public RestPoint(float x, float y, float radius) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius RestPoint harus lebih dari nol.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public RestPoint(float x, float y) {
        this(x, y, 24f);
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

    @Override
    public String prompt() {
        return "ISTIRAHAT";
    }

    @Override
    public Dialogue createDialogue() {
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

        // Nyala api sungguhan (6 frame + kedip + percikan) dari aset pengguna.
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
