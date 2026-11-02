import processing.core.PGraphics;

/**
 * Objek interaktif Kotak Besi Aula (Stage 05).
 *
 * Mengunci mekanisme gerbang aula timur dengan puzzle logika 3-dial angka.
 * Bila belum terpecahkan, interaksi membuka antarmuka PuzzleSystem.
 * Bila sudah terpecahkan, interaksi menampilkan dialog bahwa kotak sudah terbuka.
 */
public final class KotakBesi implements Interactable {

    private final float x;
    private final float y;
    private final float radius;
    private final Puzzle puzzle;

    public KotakBesi(float x, float y, float radius, Puzzle puzzle) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius interaksi kotak besi harus lebih dari nol.");
        }
        if (puzzle == null) {
            throw new IllegalArgumentException("Kotak besi membutuhkan puzzle.");
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
        return puzzle.isSolved() ? "PERIKSA KOTAK" : "BUKA KOTAK";
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
            return new Dialogue("KOTAK BESI AULA",
                    "Kotak besi kuno ini sudah terbuka dengan kombinasi 2 - 4 - 9.",
                    "Kait mekanis di dalamnya telah terlepas, membuka pengunci gerbang sayap timur.");
        }
        return new Dialogue("KOTAK BESI AULA",
                "Kotak besi berat dengan plakat kuno dan 3 roda pemutar angka (0-9).",
                "Mekanisme kaitnya menahan tuas gerbang sayap timur.");
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

        // Bayangan peti di lantai
        g.fill(Palette.alpha(0x000000, 100));
        g.rect(px - 9, py + 2, 18, 5);

        // Badan peti besi (16x12 px)
        g.fill(0xFF22242E);
        g.rect(px - 8, py - 6, 16, 11);

        // Pinggiran & rusuk logam gelap
        g.fill(0xFF383C4C);
        g.rect(px - 8, py - 6, 16, 1);
        g.rect(px - 8, py + 4, 16, 1);
        g.rect(px - 8, py - 6, 1, 11);
        g.rect(px + 7, py - 6, 1, 11);

        // Plat kunci di tengah
        if (puzzle.isSolved()) {
            // Tutup peti sedikit terbuka dengan kilau emas
            g.fill(Palette.ACCENT);
            g.rect(px - 3, py - 4, 6, 3);
            g.fill(Palette.WARM);
            g.rect(px - 2, py - 3, 4, 1);
        } else {
            // Plat kunci dengan 3 dial kecil ungu misterius
            g.fill(0xFF504668);
            g.rect(px - 4, py - 2, 8, 4);
            g.fill(Palette.MAGIC);
            g.rect(px - 3, py - 1, 2, 2);
            g.rect(px, py - 1, 1, 2);
            g.rect(px + 2, py - 1, 2, 2);
        }
    }
}
