import processing.core.PGraphics;

/**
 * Obor genggam yang ditemukan di pintu masuk Jalur Tebing (Bab 3).
 *
 * Sebelum diambil, obor ditancapkan di tanah dekat pintu masuk dan menjadi
 * satu-satunya sumber cahaya di lorong gelap. Setelah diambil, pemain
 * menggenggamnya dan cahaya mengikuti pergerakan pemain (diatur GameplayScene).
 */
public final class Obor implements Interactable {

    private final float x;
    private final float y;
    private final float radius;
    private boolean pickedUp = false;

    public Obor(float x, float y, float radius) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius Obor harus lebih dari nol.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public Obor(float x, float y) {
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
        return pickedUp ? "OBOR DIAMBIL" : "AMBIL OBOR";
    }

    /** True bila obor sudah diambil pemain. */
    public boolean isPickedUp() {
        return pickedUp;
    }

    /** Dipakai saat memulihkan state dari save. */
    public void restorePickedUp(boolean value) {
        this.pickedUp = value;
    }

    @Override
    public Dialogue createDialogue() {
        return new Dialogue("OBOR KAYU",
                "Sebuah obor kayu dengan kain pembungkus hangus tertancap di tanah dekat pintu masuk.",
                "Api kecilnya masih menyala, cukup untuk menyingkap dinding batu di sekitarmu.",
                "Kau mencabut obornya. Kehangatannya langsung terasa di ujung jari.",
                "Tanpa cahaya ini, lorong reruntuhan di depan hanya akan menjadi kegelapan total.");
    }

    @Override
    public Dialogue interact(InvestigationSystem investigation) {
        boolean wasPicked = pickedUp;
        pickedUp = true;
        if (wasPicked) {
            return new Dialogue("OBOR KAYU",
                    "Obor itu sudah ada di tanganmu. Cahayanya menerangi langkahmu.");
        }
        return createDialogue();
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        if (pickedUp) {
            return;
        }
        int px = Math.round(x - cameraX);
        int py = Math.round(y - cameraY);
        float animTime = (System.currentTimeMillis() % 100000) / 1000f;
        float flicker = 1f + 0.08f * (float) Math.sin(animTime * 9f);

        g.noStroke();
        g.fill(Palette.alpha(Palette.REST_GLOW, 16));
        g.ellipse(px, py - 10f, 40f * flicker, 34f * flicker);
        g.fill(Palette.alpha(Palette.WARM, 30));
        g.ellipse(px, py - 12f, 22f * flicker, 20f * flicker);

        Art.drawTorch(g, px, py, animTime, 1f, false);
    }
}
