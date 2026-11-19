import processing.core.PGraphics;

/**
 * Gerbang transisi antar segmen di Lembah Berkabut (Bab 3).
 *
 * Menandai titik perpindahan dari satu area lembah ke area lembah berikutnya.
 * Tidak memiliki state terkunci — jalan selalu terbuka sebagai pemicu transisi.
 */
public final class GerbangLembah implements Interactable {

    private final float x;
    private final float y;
    private final float radius;
    private final String targetLabel;

    public GerbangLembah(float x, float y, float radius, String targetLabel) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius GerbangLembah harus lebih dari nol.");
        }
        if (targetLabel == null || targetLabel.trim().isEmpty()) {
            throw new IllegalArgumentException("Label tujuan GerbangLembah tidak boleh kosong.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.targetLabel = targetLabel;
    }

    public GerbangLembah(float x, float y, String targetLabel) {
        this(x, y, 28f, targetLabel);
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

    public String targetLabel() {
        return targetLabel;
    }

    @Override
    public String prompt() {
        return "LANJUT KE " + targetLabel;
    }

    @Override
    public Dialogue createDialogue() {
        return new Dialogue("JALAN LEMBAH",
                "Jalan setapak berlumut menyempit di antara dinding batu yang menjulang.",
                "Kabut tipis bergerak pelan, memperlihatkan jalur menuju " + targetLabel + ".",
                "Udara terasa lebih berat di sini — tanda bahwa kau semakin jauh masuk ke dalam lembah.");
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        int px = Math.round(x - cameraX);
        int py = Math.round(y - cameraY);
        g.noStroke();

        // Cahaya sihir redup menandai jalan
        g.fill(Palette.alpha(Palette.MAGIC_DIM, 20));
        g.ellipse(px, py, 32, 38);
        g.fill(Palette.alpha(Palette.MAGIC_DIM, 12));
        g.ellipse(px, py - 4, 20, 24);
    }
}
