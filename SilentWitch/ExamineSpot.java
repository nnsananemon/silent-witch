import processing.core.PGraphics;

/**
 * Titik periksa pada objek dunia (meja, rak, jendela).
 *
 * Objeknya sendiri sudah digambar oleh TileMap, sehingga draw() sengaja kosong.
 * ExamineSpot hanya menyediakan posisi, jangkauan, prompt, dan dialog.
 */
public final class ExamineSpot implements Interactable {

    private final float x;
    private final float y;
    private final float radius;
    private final String label;
    private final Dialogue dialogue;

    public ExamineSpot(float x, float y, float radius, String label, Dialogue dialogue) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius interaksi harus lebih dari nol.");
        }
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Label objek tidak boleh kosong.");
        }
        if (dialogue == null) {
            throw new IllegalArgumentException("Objek periksa membutuhkan dialog.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.label = label;
        this.dialogue = dialogue;
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

    @Override
    public Dialogue createDialogue() {
        return dialogue;
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        // Objek sudah digambar oleh TileMap.
    }
}
