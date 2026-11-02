import processing.core.PGraphics;

/**
 * Objek interaktif Gerbang Barat di Sayap Timur untuk kembali ke Aula Utara (Stage 07).
 */
public final class GerbangBarat implements Interactable {

    private final float x;
    private final float y;
    private final float radius;

    public GerbangBarat(float x, float y, float radius) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius GerbangBarat harus lebih dari nol.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public GerbangBarat(float x, float y) {
        this(x, y, 28f);
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
        return "KEMBALI KE AULA";
    }

    @Override
    public Dialogue createDialogue() {
        return new Dialogue("LORONG BARAT",
                "Lorong batu ini menghubungkan Sayap Timur kembali ke Aula Utara The Silent Manor.");
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        int px = Math.round(x - cameraX);
        int py = Math.round(y - cameraY);
        g.noStroke();
        g.fill(Palette.alpha(Palette.WARM, 35));
        g.ellipse(px + 4, py, 26, 32);
    }
}
