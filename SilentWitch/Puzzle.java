/**
 * Model data dan validasi Puzzle logika sesuai PRD Seksi 5.
 *
 * Mendukung jenis puzzle deduksi kondisi (PRD 5.1 type 2), sistem petunjuk
 * bertingkat 3 tahap (PRD 5.3), validasi jawaban dengan umpan balik edukatif,
 * dan perekaman status penyelesaian tanpa merusak progres saat salah (AC-05).
 */
public final class Puzzle {

    public static final class ValidationResult {
        private final boolean success;
        private final String message;

        public ValidationResult(boolean success, String message) {
            this.success = success;
            this.message = message == null ? "" : message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String message() {
            return message;
        }

        @Override
        public String toString() {
            return (success ? "[SUKSES] " : "[GAGAL] ") + message;
        }
    }

    private final String id;
    private final String title;
    private final String instruction;
    private final int[] solution;
    private final String[] hints;
    private final String explanation;

    private boolean solved;
    private int wrongAttempts;
    private int revealedHintLevel; // 0: belum minta, 1..hints.length

    public Puzzle(String id, String title, String instruction, int[] solution,
                  String[] hints, String explanation) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID puzzle tidak boleh kosong.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Judul puzzle tidak boleh kosong.");
        }
        if (instruction == null || instruction.trim().isEmpty()) {
            throw new IllegalArgumentException("Instruksi puzzle tidak boleh kosong.");
        }
        if (solution == null || solution.length == 0) {
            throw new IllegalArgumentException("Solusi puzzle tidak boleh kosong.");
        }
        if (hints == null || hints.length < 3) {
            throw new IllegalArgumentException("Puzzle harus memiliki minimal 3 tingkatan hint (PRD 5.3).");
        }
        for (int i = 0; i < hints.length; i++) {
            if (hints[i] == null || hints[i].trim().isEmpty()) {
                throw new IllegalArgumentException("Hint ke-" + (i + 1) + " tidak boleh kosong.");
            }
        }
        if (explanation == null || explanation.trim().isEmpty()) {
            throw new IllegalArgumentException("Penjelasan solusi puzzle tidak boleh kosong.");
        }

        this.id = id.trim();
        this.title = title.trim();
        this.instruction = instruction.trim();
        this.solution = solution.clone();
        this.hints = hints.clone();
        this.explanation = explanation.trim();
        this.solved = false;
        this.wrongAttempts = 0;
        this.revealedHintLevel = 0;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String instruction() {
        return instruction;
    }

    public int dialCount() {
        return solution.length;
    }

    public int[] solution() {
        return solution.clone();
    }

    public String explanation() {
        return explanation;
    }

    public boolean isSolved() {
        return solved;
    }

    public int wrongAttempts() {
        return wrongAttempts;
    }

    public int revealedHintLevel() {
        return revealedHintLevel;
    }

    public int maxHintLevel() {
        return hints.length;
    }

    /**
     * Meminta petunjuk tingkat berikutnya secara bertahap (PRD 5.3).
     * Hint tidak mengubah solusi atau status penyelesaian (AC-05).
     *
     * @return teks hint yang aktif setelah permintaan.
     */
    public String requestNextHint() {
        if (revealedHintLevel < hints.length) {
            revealedHintLevel++;
        }
        return currentHint();
    }

    public String currentHint() {
        if (revealedHintLevel <= 0) {
            return null;
        }
        return hints[revealedHintLevel - 1];
    }

    public String hintAt(int level) {
        if (level < 1 || level > hints.length) {
            throw new IndexOutOfBoundsException("Tingkat hint tidak valid: " + level);
        }
        return hints[level - 1];
    }

    /**
     * Memvalidasi kombinasi input yang diberikan pemain.
     *
     * Jika benar: mengubah solved = true dan mengembalikan penjelasan penyelesaian.
     * Jika salah: menambah wrongAttempts dan mengembalikan umpan balik edukatif
     *             tanpa merusak progres (AC-05).
     */
    public ValidationResult validate(int[] input) {
        if (input == null || input.length != solution.length) {
            return new ValidationResult(false, "Jumlah digit input tidak sesuai format puzzle.");
        }

        boolean allMatch = true;
        for (int i = 0; i < solution.length; i++) {
            if (input[i] != solution[i]) {
                allMatch = false;
                break;
            }
        }

        if (allMatch) {
            this.solved = true;
            return new ValidationResult(true, explanation);
        } else {
            this.wrongAttempts++;
            String feedback = evaluateConditions(input);
            return new ValidationResult(false, feedback);
        }
    }

    /** Evaluasi kondisi logika numerik spesifik kotak besi (PRD 5.1 type 2). */
    private String evaluateConditions(int[] input) {
        if (input.length == 3) {
            int d0 = input[0];
            int d1 = input[1];
            int d2 = input[2];

            if (d0 != 2) {
                return "Digit I salah: harus bilangan prima genap terkecil.";
            }
            if (d1 != 4) {
                return "Digit II salah: harus bernilai dua kali lipat Digit I (2 x 2 = 4).";
            }
            if (d2 != 9) {
                return "Digit III salah: harus berupa kuadrat dari angka 3 (3 x 3 = 9).";
            }
            if (d0 + d1 + d2 != 15) {
                return "Jumlah ketiga digit harus tepat sama dengan 15.";
            }
        }
        return "Kombinasi salah. Roda pemutar berderak dan kembali terkunci. Periksa kembali ketiga aturan kondisi.";
    }

    public void forceSolve() {
        this.solved = true;
    }

    public void restoreState(boolean solved, int wrongAttempts, int revealedHintLevel) {
        this.solved = solved;
        this.wrongAttempts = Math.max(0, wrongAttempts);
        this.revealedHintLevel = Math.max(0, Math.min(hints.length, revealedHintLevel));
    }

    @Override
    public String toString() {
        return "Puzzle[" + id + " - " + title + " (solved=" + solved + ", attempts=" + wrongAttempts + ")]";
    }
}
