import processing.core.PGraphics;

/**
 * Objek interaktif Kait Pengunci Aula (Stage 05).
 *
 * Mengunci gerbang aula timur dengan puzzle logika 3-dial angka.
 * Bila belum terpecahkan, interaksi membuka antarmuka PuzzleSystem.
 * Bila sudah terpecahkan, interaksi menampilkan dialog bahwa kait sudah terbuka.
 */
public final class KotakBesi implements Interactable {

    private final float x;
    private final float y;
    private final float radius;
    private final Puzzle puzzle;

    public KotakBesi(float x, float y, float radius, Puzzle puzzle) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius interaksi kait pengunci harus lebih dari nol.");
        }
        if (puzzle == null) {
            throw new IllegalArgumentException("Kait pengunci membutuhkan puzzle.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.puzzle = puzzle;
    }

    public KotakBesi(float x, float y, Puzzle puzzle) {
        this(x, y, 28f, puzzle);
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
        return puzzle.isSolved() ? "PERIKSA KAIT" : "BUKA KAIT";
    }

    public Puzzle puzzle() {
        return puzzle;
    }

    public boolean isSolved() {
        return puzzle.isSolved();
    }

    @Override
    public Dialogue createDialogue() {
        if (puzzle.isSolved()) {
            return new Dialogue("KAIT PENGUNCI",
                    "Kait pengunci ini sudah terbuka dengan kombinasi 2 - 4 - 9.",
                    "Rantainya mengendur dan gerbang sayap timur kini lepas.");
        }
        return new Dialogue("KAIT PENGUNCI",
                "Kait logam beruas zamrud mengikat rantai gerbang di ujung aula.",
                "Tiga roda angka kecil terpasang pada kait: mekanisme yang mengunci gerbang.");
    }

    @Override
    public Dialogue interact(InvestigationSystem investigation) {
        return createDialogue();
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        // Tidak ada penanda tambahan di dunia. Latar Aula Manor sudah memuat
        // kait dan rantai aslinya, jadi objek ini hanya menangani interaksi.
    }
}

