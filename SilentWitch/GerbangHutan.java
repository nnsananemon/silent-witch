import processing.core.PGraphics;

/**
 * Objek interaktif pintu keluar timur di Hollow Woods (Bab 2).
 *
 * Berada di sisi timur hutan, menandai jalan menuju lembah yang lebih dalam.
 * Saat ini jalur terhalang kabut tebal sampai pemain memeriksa Batu Simbol
 * di tengah hutan. Setelah batu diperiksa, jalan terbuka untuk transisi
 * ke area berikutnya.
 */
public final class GerbangHutan implements Interactable {

    private final float x;
    private final float y;
    private final float radius;
    private boolean fogCleared = false;

    public GerbangHutan(float x, float y, float radius) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius GerbangHutan harus lebih dari nol.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public GerbangHutan(float x, float y) {
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

    public boolean isFogCleared() {
        return fogCleared;
    }

    public void clearFog() {
        fogCleared = true;
    }

    @Override
    public String prompt() {
        return fogCleared ? "LANJUTKAN KE LEMBAH" : "PERIKSA JALAN";
    }

    @Override
    public Dialogue createDialogue() {
        if (fogCleared) {
            return new Dialogue("GERBANG HUTAN TIMUR",
                    "Kabut perlahan menyingkap jalan setapak sempit menuju lembah.",
                    "Sigil Jejak kuno beresonansi dengan udara lembah di balik gerbang pohon.",
                    "Jalan menyempit di antara akar-akar raksasa, menuju tebing lembah.");
        }
        return new Dialogue("GERBANG HUTAN TIMUR",
                "Kabut putih tebal menghalangi jalan di depan.",
                "Di balik kabut samar terlihat siluet pepohonan rapat dan aura bayangan dingin.",
                "Kau merasakan kehadiran Penjaga Jejak yang menjaga batas wilayah ini...",
                "Kalahkan penjaga bayangan atau periksa batu simbol kuno untuk menembus kabut.");
    }

    @Override
    public Dialogue interact(InvestigationSystem investigation) {
        return createDialogue();
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        int px = Math.round(x - cameraX);
        int py = Math.round(y - cameraY);
        g.noStroke();

        if (fogCleared) {
            // Cahaya redup dari jalur lembah yang terbuka
            g.fill(Palette.alpha(Palette.MAGIC, 18));
            g.ellipse(px, py, 30, 36);
            g.fill(Palette.alpha(Palette.MAGIC, 10));
            g.ellipse(px, py - 4, 18, 22);
        } else {
            // Kabut tebal berputar menghalangi jalan
            g.fill(Palette.alpha(Palette.FOG, 45));
            g.ellipse(px, py, 36, 40);
            g.fill(Palette.alpha(Palette.FOG, 30));
            g.ellipse(px + 6, py - 4, 24, 28);
            g.fill(Palette.alpha(Palette.FOG, 20));
            g.ellipse(px - 4, py + 6, 20, 22);
        }
    }
}
