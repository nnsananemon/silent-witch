import processing.core.PGraphics;

/**
 * Gerbang keluar dari Underground Archive kembali ke Manor (Bab 5→6).
 *
 * Setelah menyelesaikan arsip bawah tanah, gerbang ini membuka
 * jalan pintas kembali ke manor. Pemain tidak perlu backtrack
 * melalui seluruh lembah dan hutan.
 */
public final class GerbangArsip implements Interactable {

    private final float x;
    private final float y;
    private final float radius;

    public GerbangArsip(float x, float y, float radius) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius GerbangArsip harus lebih dari nol.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public GerbangArsip(float x, float y) {
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
        return "KEMBALI KE MANOR";
    }

    @Override
    public Dialogue createDialogue() {
        return new Dialogue("LORONG RAHASIA",
                "Lorong sempit ini mengarah ke atas, melewati batu-batu tua.",
                "Kau bisa merasakan udara manor dari celah di ujungnya.",
                "Jalan ini lebih pendek daripada kembali melalui lembah.");
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        int sx = Math.round(x - cameraX);
        int sy = Math.round(y - cameraY);

        g.noStroke();
        g.fill(Palette.alpha(Palette.WARM_DIM, 15));
        g.rect(sx - 8, sy - 8, 16, 16);
        g.fill(Palette.alpha(Palette.WARM, 8));
        g.rect(sx - 4, sy - 4, 8, 8);
    }
}
