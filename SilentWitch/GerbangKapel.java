import processing.core.PGraphics;

/**
 * Gerbang keluar dari Chapel of Whispers ke Underground Archive (Bab 4).
 *
 * Sebuah pintu batu berat yang mengarah ke tangga turun menuju arsip
 * bawah tanah. Terbuka setelah pemain menyelesaikan puzzle simbol
 * atau menemukan mekanisme pembukanya.
 */
public final class GerbangKapel implements Interactable {

    private final float x;
    private final float y;
    private final float radius;
    private final String targetLabel;
    private boolean unlocked = false;

    public GerbangKapel(float x, float y, float radius, String targetLabel) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius GerbangKapel harus lebih dari nol.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.targetLabel = targetLabel;
    }

    public GerbangKapel(float x, float y, String targetLabel) {
        this(x, y, 28f, targetLabel);
    }

    public GerbangKapel(float x, float y) {
        this(x, y, 28f, "ARSIP BAWAH TANAH");
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

    public boolean isUnlocked() {
        return unlocked;
    }

    public void unlock() {
        this.unlocked = true;
    }

    public void restoreUnlocked(boolean state) {
        this.unlocked = state;
    }

    @Override
    public String prompt() {
        return unlocked ? "LANJUT KE " + targetLabel : "PINTU TERKUNCI";
    }

    @Override
    public Dialogue createDialogue() {
        if (!unlocked) {
            return new Dialogue("PINTU BATU TERKUNCI",
                    "Pintu batu berat dengan ukiran simbol di permukaannya.",
                    "Mekanisme penguncinya terhubung dengan pola simbol di kapel.",
                    "Periksa simbol-simbol di area kapel untuk menemukan kuncinya.");
        }
        return new Dialogue("PINTU BATU TERBUKA",
                "Pintu batu bergeser, memperlihatkan tangga batu yang mengarah ke bawah.",
                "Udara dingin dan bau kertas tua menguar dari kegelapan.");
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        int sx = Math.round(x - cameraX);
        int sy = Math.round(y - cameraY);

        if (!unlocked) {
            // Cahaya redup pada pintu terkunci
            g.noStroke();
            g.fill(Palette.alpha(Palette.MAGIC_DIM, 12));
            g.rect(sx - 8, sy - 8, 16, 16);
        } else {
            // Cahaya terbuka
            g.noStroke();
            g.fill(Palette.alpha(Palette.MAGIC, 18));
            g.rect(sx - 8, sy - 8, 16, 16);
            g.fill(Palette.alpha(Palette.MAGIC, 10));
            g.rect(sx - 5, sy - 5, 10, 10);
        }
    }
}
