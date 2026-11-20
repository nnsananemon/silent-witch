import processing.core.PGraphics;

/**
 * Papan pengumuman di Ashen Village (Prolog).
 *
 * Menampilkan pengumuman desa tentang kematian Clara Thorne dan
 * permintaan bantuan penyelidikan. Saat diperiksa pertama kali,
 * Casebook memperkenalkan kategori Bukti, Kesaksian, dan Kronologi.
 */
public final class PapanPengumuman implements Interactable {

    private final float x;
    private final float y;
    private final float radius;
    private boolean examined = false;

    private final Dialogue initialDialogue;
    private final Dialogue repeatDialogue;

    public PapanPengumuman(float x, float y, float radius) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius PapanPengumuman harus lebih dari nol.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;

        this.initialDialogue = new Dialogue("PAPAN PENGUMUMAN DESA",
                "Papan kayu tua dipenuhi kertas pengumuman yang pudar.",
                "Satu kertas paling baru tertempel di tengah, tintanya masih gelap:",
                "\"KEPADA SIAPA SAJA YANG MEMBACA — Clara Thorne ditemukan tanpa nyawa\"",
                "\"di koridor Sayap Timur manor pada malam ke-tujuh bulan Ashen.\"",
                "\"Siapa pun yang memiliki informasi, segera melapor ke tetua desa.\"",
                "Casebook-mu mencatat informasi ini sebagai titik awal penyelidikan.",
                "Tiga kategori terbuka: BUKTI, KESAKSIAN, dan KRONOLOGI.");

        this.repeatDialogue = new Dialogue("PAPAN PENGUMUMAN DESA",
                "Pengumuman tentang kematian Clara Thorne.",
                "Informasi ini sudah tercatat di Casebook.");
    }

    public PapanPengumuman(float x, float y) {
        this(x, y, 34f);
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
        return "BACA PENGUMUMAN";
    }

    @Override
    public Dialogue createDialogue() {
        if (!examined) {
            examined = true;
            return initialDialogue;
        }
        return repeatDialogue;
    }

    public boolean isExamined() {
        return examined;
    }

    public void restoreExamined(boolean state) {
        this.examined = state;
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        // Papan sudah digambar oleh TileMap via tile 'N'.
    }
}
