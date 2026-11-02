import processing.core.PGraphics;

/**
 * Objek interaktif Altar Rekonstruksi Kasus di Sayap Timur (Stage 07).
 *
 * Tempat pemain menyusun kesimpulan deduksi kanonis berdasarkan bukti-bukti
 * yang telah dikumpulkan di Buku Kasus. Membuka antarmuka modal DeductionSystem.
 */
public final class AltarDeduksi implements Interactable {

    private final float x;
    private final float y;
    private final float radius;

    public AltarDeduksi(float x, float y, float radius) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius AltarDeduksi harus lebih dari nol.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public AltarDeduksi(float x, float y) {
        this(x, y, 32f);
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
        return "REKONSTRUKSI KASUS";
    }

    @Override
    public Dialogue createDialogue() {
        return new Dialogue("ALTAR REKONSTRUKSI KASUS",
                "Altar batu kuno berbalut beludru ungu tersinari cahaya temaram lilin.",
                "Di sinilah seluruh kepingan bukti investigasi The Silent Manor disatukan untuk menarik kesimpulan akhir.",
                "Tekan E untuk membuka jendela Rekonstruksi Deduksi Kasus.");
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        // Tile altar sudah digambar oleh TileMap.
        // Kilau temaram di atas altar
        int px = Math.round(x - cameraX);
        int py = Math.round(y - cameraY);
        g.noStroke();
        g.fill(Palette.alpha(0x9F8CD4, 32));
        g.ellipse(px, py, 36, 28);
    }
}
