import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sistem Quest sesuai PRD Seksi 6.1 dan 10.4.
 *
 * Mengelola pendaftaran quest, aktivasi, evaluasi tujuan, pembaruan progres,
 * dan notifikasi perubahan status quest.
 */
public final class QuestSystem {

    private static final float NOTIF_DURATION = 3.5f;

    private final Map<String, Quest> registeredQuests = new LinkedHashMap<String, Quest>();
    private final List<String> questOrder = new ArrayList<String>();
    private String activeQuestId = null;

    private String lastNotification = null;
    private float notificationTimer = 0f;

    public QuestSystem() {
    }

    public void registerQuest(Quest quest) {
        if (quest == null) {
            throw new IllegalArgumentException("Quest yang didaftarkan tidak boleh null.");
        }
        if (registeredQuests.containsKey(quest.id())) {
            throw new IllegalArgumentException("Quest dengan ID '" + quest.id() + "' sudah terdaftar.");
        }
        registeredQuests.put(quest.id(), quest);
        questOrder.add(quest.id());
        if (activeQuestId == null && quest.isActive()) {
            activeQuestId = quest.id();
        }
    }

    public boolean isRegistered(String questId) {
        return questId != null && registeredQuests.containsKey(questId);
    }

    public Quest quest(String id) {
        return registeredQuests.get(id);
    }

    public List<Quest> allQuests() {
        List<Quest> list = new ArrayList<Quest>();
        for (String id : questOrder) {
            list.add(registeredQuests.get(id));
        }
        return Collections.unmodifiableList(list);
    }

    public int totalQuestCount() {
        return questOrder.size();
    }

    public void setActiveQuest(String id) {
        if (id != null && !registeredQuests.containsKey(id)) {
            throw new IllegalArgumentException("Quest ID '" + id + "' belum terdaftar di sistem.");
        }
        this.activeQuestId = id;
    }

    public String activeQuestId() {
        return activeQuestId;
    }

    public Quest activeQuest() {
        if (activeQuestId == null) {
            return null;
        }
        return registeredQuests.get(activeQuestId);
    }

    public boolean isQuestCompleted(String questId) {
        Quest q = registeredQuests.get(questId);
        return q != null && q.isCompleted();
    }

    public void activateQuest(String id) {
        Quest q = registeredQuests.get(id);
        if (q != null) {
            q.activate();
            this.activeQuestId = id;
            setNotification("QUEST BARU: " + q.title());
        }
    }

    public void restoreQuestState(String questId, String statusStr, List<SaveData.ObjectiveSave> objectiveSaves) {
        Quest q = registeredQuests.get(questId);
        if (q == null) {
            return;
        }
        if (statusStr != null) {
            try {
                q.setStatus(Quest.Status.valueOf(statusStr));
            } catch (Exception ignored) {
            }
        }
        if (objectiveSaves != null) {
            for (SaveData.ObjectiveSave os : objectiveSaves) {
                QuestObjective obj = q.objective(os.id());
                if (obj != null) {
                    obj.setProgress(os.count());
                    obj.setComplete(os.isComplete());
                }
            }
        }
    }

    public boolean completeObjective(String questId, String objectiveId) {
        Quest q = registeredQuests.get(questId);
        if (q != null) {
            QuestObjective obj = q.objective(objectiveId);
            boolean wasComplete = (obj != null && obj.isComplete());
            boolean success = q.completeObjective(objectiveId);
            if (success && !wasComplete) {
                if (q.isCompleted()) {
                    setNotification("QUEST SELESAI: " + q.title());
                } else {
                    setNotification("TUJUAN TERCAPAI: " + (obj != null ? obj.description() : objectiveId));
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateObjective(String questId, String objectiveId, int count) {
        Quest q = registeredQuests.get(questId);
        if (q != null) {
            boolean wasComplete = q.isCompleted();
            boolean success = q.updateObjective(objectiveId, count);
            if (success && !wasComplete && q.isCompleted()) {
                setNotification("QUEST SELESAI: " + q.title());
            }
            return success;
        }
        return false;
    }

    /** Dipanggil saat ada clue baru ditemukan untuk memperbarui objective clue jika ada. */
    public void onClueDiscovered(int currentClueCount) {
        Quest q = activeQuest();
        if (q != null && q.isActive()) {
            QuestObjective obj = q.objective("obj_kumpulkan_bukti");
            if (obj != null && !obj.isComplete()) {
                updateObjective(q.id(), "obj_kumpulkan_bukti", currentClueCount);
            }
        }
    }

    /** Dipanggil saat puzzle berhasil diselesaikan. */
    public void onPuzzleSolved(String puzzleId) {
        Quest q = activeQuest();
        if (q != null && q.isActive()) {
            QuestObjective obj = q.objective("obj_pecahkan_puzzle");
            if (obj != null && !obj.isComplete()) {
                completeObjective(q.id(), "obj_pecahkan_puzzle");
            }
        }
    }

    /** Dipanggil saat gerbang berhasil dibuka. */
    public void onGateUnlocked() {
        Quest q = activeQuest();
        if (q != null && q.isActive()) {
            QuestObjective obj = q.objective("obj_buka_gerbang");
            if (obj != null && !obj.isComplete()) {
                completeObjective(q.id(), "obj_buka_gerbang");
            }
        }
    }

    public void update(float dt) {
        if (notificationTimer > 0f) {
            notificationTimer -= dt;
            if (notificationTimer <= 0f) {
                notificationTimer = 0f;
                lastNotification = null;
            }
        }
    }

    public void setNotification(String message) {
        if (message != null && !message.trim().isEmpty()) {
            this.lastNotification = message.trim();
            this.notificationTimer = NOTIF_DURATION;
        }
    }

    public String lastNotification() {
        return lastNotification;
    }

    public boolean hasActiveNotification() {
        return notificationTimer > 0f && lastNotification != null;
    }

    public void clearNotification() {
        notificationTimer = 0f;
        lastNotification = null;
    }

    public String hudSummary() {
        Quest q = activeQuest();
        if (q == null) {
            return "QUEST: -";
        }
        if (q.isCompleted()) {
            return "QUEST: SELESAI";
        }
        return "QUEST: " + q.completedObjectiveCount() + "/" + q.totalObjectiveCount();
    }

    public String fullTitleSummary() {
        Quest q = activeQuest();
        if (q == null) {
            return "QUEST: -";
        }
        if (q.isCompleted()) {
            return "QUEST: " + q.title() + " [SELESAI]";
        }
        return "QUEST: " + q.title() + " (" + q.completedObjectiveCount() + "/" + q.totalObjectiveCount() + ")";
    }
}
