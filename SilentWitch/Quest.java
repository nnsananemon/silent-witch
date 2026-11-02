import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model data Quest investigasi sesuai PRD Seksi 6.1.
 *
 * Menyimpan ID unik, judul, deskripsi naratif, status quest, prasyarat,
 * daftar sub-tujuan (QuestObjective), dan catatan dampak/hadiah penyelesaian.
 */
public final class Quest {

    public enum Status {
        LOCKED,
        ACTIVE,
        COMPLETED,
        FAILED
    }

    private final String id;
    private final String title;
    private final String description;
    private final String[] prerequisiteQuestIds;
    private final String[] prerequisiteClueIds;
    private final String rewardDescription;
    private final List<QuestObjective> objectives;
    private Status status;

    public Quest(String id, String title, String description,
                 String[] prerequisiteQuestIds, String[] prerequisiteClueIds,
                 String rewardDescription, List<QuestObjective> objectives) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID quest tidak boleh kosong.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Judul quest tidak boleh kosong.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Deskripsi quest tidak boleh kosong.");
        }
        this.id = id.trim();
        this.title = title.trim();
        this.description = description.trim();
        this.prerequisiteQuestIds = prerequisiteQuestIds == null ? new String[0] : prerequisiteQuestIds.clone();
        this.prerequisiteClueIds = prerequisiteClueIds == null ? new String[0] : prerequisiteClueIds.clone();
        this.rewardDescription = rewardDescription == null ? "" : rewardDescription.trim();
        this.objectives = new ArrayList<QuestObjective>();
        if (objectives != null) {
            for (QuestObjective obj : objectives) {
                if (obj != null) {
                    this.objectives.add(obj);
                }
            }
        }
        this.status = Status.LOCKED;
    }

    public Quest(String id, String title, String description, String rewardDescription) {
        this(id, title, description, new String[0], new String[0], rewardDescription, null);
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public Status status() {
        return status;
    }

    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status quest tidak boleh null.");
        }
        this.status = status;
    }

    public boolean isLocked() {
        return status == Status.LOCKED;
    }

    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }

    public boolean isFailed() {
        return status == Status.FAILED;
    }

    public void activate() {
        if (this.status != Status.COMPLETED) {
            this.status = Status.ACTIVE;
        }
    }

    public void complete() {
        this.status = Status.COMPLETED;
    }

    public String[] prerequisiteQuestIds() {
        return prerequisiteQuestIds.clone();
    }

    public String[] prerequisiteClueIds() {
        return prerequisiteClueIds.clone();
    }

    public String rewardDescription() {
        return rewardDescription;
    }

    public List<QuestObjective> objectives() {
        return Collections.unmodifiableList(objectives);
    }

    public void addObjective(QuestObjective objective) {
        if (objective == null) {
            throw new IllegalArgumentException("Objective tidak boleh null.");
        }
        for (QuestObjective existing : objectives) {
            if (existing.id().equals(objective.id())) {
                throw new IllegalArgumentException("Objective dengan ID '" + objective.id() + "' sudah ada di quest ini.");
            }
        }
        objectives.add(objective);
    }

    public QuestObjective objective(String objectiveId) {
        if (objectiveId == null) {
            return null;
        }
        for (QuestObjective obj : objectives) {
            if (obj.id().equals(objectiveId)) {
                return obj;
            }
        }
        return null;
    }

    public boolean isAllObjectivesComplete() {
        if (objectives.isEmpty()) {
            return false;
        }
        for (QuestObjective obj : objectives) {
            if (!obj.isComplete()) {
                return false;
            }
        }
        return true;
    }

    public boolean updateObjective(String objectiveId, int count) {
        QuestObjective obj = objective(objectiveId);
        if (obj != null) {
            obj.setProgress(count);
            if (isAllObjectivesComplete() && status == Status.ACTIVE) {
                this.status = Status.COMPLETED;
            }
            return true;
        }
        return false;
    }

    public boolean completeObjective(String objectiveId) {
        QuestObjective obj = objective(objectiveId);
        if (obj != null) {
            obj.setComplete(true);
            if (isAllObjectivesComplete() && status == Status.ACTIVE) {
                this.status = Status.COMPLETED;
            }
            return true;
        }
        return false;
    }

    public int completedObjectiveCount() {
        int count = 0;
        for (QuestObjective obj : objectives) {
            if (obj.isComplete()) {
                count++;
            }
        }
        return count;
    }

    public int totalObjectiveCount() {
        return objectives.size();
    }

    @Override
    public String toString() {
        return "Quest[" + id + " (" + status + ") " + completedObjectiveCount() + "/" + totalObjectiveCount() + "]";
    }
}
