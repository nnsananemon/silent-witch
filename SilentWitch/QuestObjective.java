/**
 * Sub-tujuan spesifik dalam sebuah quest investigasi.
 */
public final class QuestObjective {

    private final String id;
    private final String description;
    private final int targetCount;
    private int currentCount;
    private boolean complete;

    public QuestObjective(String id, String description, int targetCount) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID objective tidak boleh kosong.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Deskripsi objective tidak boleh kosong.");
        }
        if (targetCount <= 0) {
            throw new IllegalArgumentException("Target count harus lebih dari nol.");
        }
        this.id = id.trim();
        this.description = description.trim();
        this.targetCount = targetCount;
        this.currentCount = 0;
        this.complete = false;
    }

    public QuestObjective(String id, String description) {
        this(id, description, 1);
    }

    public String id() {
        return id;
    }

    public String description() {
        return description;
    }

    public int targetCount() {
        return targetCount;
    }

    public int currentCount() {
        return currentCount;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setProgress(int count) {
        this.currentCount = Math.max(0, Math.min(targetCount, count));
        if (this.currentCount >= targetCount) {
            this.complete = true;
        }
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
        if (complete) {
            this.currentCount = targetCount;
        }
    }

    public String displayText() {
        if (targetCount > 1) {
            return description + " (" + currentCount + "/" + targetCount + ")";
        }
        return description + (complete ? " [SELESAI]" : "");
    }

    @Override
    public String toString() {
        return "Objective[" + id + ": " + displayText() + "]";
    }
}
