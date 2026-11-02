import processing.core.PGraphics;

/**
 * Titik periksa pada objek dunia (meja, rak, jendela).
 *
 * Objeknya sendiri sudah digambar oleh TileMap, sehingga draw() sengaja kosong.
 * ExamineSpot menyediakan posisi, jangkauan, prompt, dialog awal, dan dialog
 * pengingat bila clue telah dicatat ke dalam buku kasus.
 */
public final class ExamineSpot implements Interactable {

    private final float x;
    private final float y;
    private final float radius;
    private final String label;
    private final Clue clue;
    private final Dialogue dialogue;
    private final Dialogue repeatDialogue;

    public ExamineSpot(float x, float y, float radius, String label, Dialogue dialogue) {
        this(x, y, radius, label, null, dialogue, null);
    }

    public ExamineSpot(float x, float y, float radius, String label, Clue clue,
                       Dialogue initialDialogue, Dialogue repeatDialogue) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius interaksi harus lebih dari nol.");
        }
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Label objek tidak boleh kosong.");
        }
        if (initialDialogue == null) {
            throw new IllegalArgumentException("Objek periksa membutuhkan dialog.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.label = label;
        this.clue = clue;
        this.dialogue = initialDialogue;
        this.repeatDialogue = repeatDialogue;
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
        return "PERIKSA";
    }

    public String label() {
        return label;
    }

    public Clue clue() {
        return clue;
    }

    public Dialogue repeatDialogue() {
        return repeatDialogue;
    }

    @Override
    public Dialogue createDialogue() {
        return dialogue;
    }

    @Override
    public Dialogue interact(InvestigationSystem investigation) {
        if (clue != null && investigation != null) {
            if (!investigation.isDiscovered(clue.id())) {
                investigation.discover(clue);
                return dialogue;
            } else if (repeatDialogue != null) {
                return repeatDialogue;
            }
        }
        return dialogue;
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        // Objek sudah digambar oleh TileMap.
    }
}
