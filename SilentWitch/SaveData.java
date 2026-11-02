import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import processing.data.JSONArray;
import processing.data.JSONObject;

/**
 * Model data Save Game sesuai PRD Seksi 7.2.
 *
 * Menyimpan seluruh data progres investigasi penting secara terstruktur:
 * - Versi skema save (mendukung migrasi skema di masa depan)
 * - Timestamp penyimpanan
 * - ID Area dan posisi/arah hadap pemain
 * - Daftar ID clue yang telah ditemukan
 * - Status quest aktif dan seluruh sub-tujuan
 * - Status puzzle (status solved, jumlah salah, tingkat petunjuk)
 * - Flag dunia (seperti pembukaan gerbang sayap timur)
 */
public final class SaveData {

    public static final int CURRENT_SCHEMA_VERSION = 1;

    // --- Sub-model Quest Save ---
    public static final class ObjectiveSave {
        private final String id;
        private final int count;
        private final boolean complete;

        public ObjectiveSave(String id, int count, boolean complete) {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ObjectiveSave ID tidak boleh kosong.");
            }
            this.id = id.trim();
            this.count = Math.max(0, count);
            this.complete = complete;
        }

        public String id() {
            return id;
        }

        public int count() {
            return count;
        }

        public boolean isComplete() {
            return complete;
        }

        public JSONObject toJSON() {
            JSONObject obj = new JSONObject();
            obj.setString("id", id);
            obj.setInt("count", count);
            obj.setBoolean("complete", complete);
            return obj;
        }

        public static ObjectiveSave fromJSON(JSONObject obj) {
            if (obj == null) {
                throw new IllegalArgumentException("Data objective null.");
            }
            if (!obj.hasKey("id")) {
                throw new IllegalArgumentException("Objective tidak memiliki field 'id'.");
            }
            String id = obj.getString("id");
            int count = obj.getInt("count", 0);
            boolean complete = obj.getBoolean("complete", false);
            return new ObjectiveSave(id, count, complete);
        }
    }

    public static final class QuestSave {
        private final String id;
        private final String status;
        private final List<ObjectiveSave> objectives;

        public QuestSave(String id, String status, List<ObjectiveSave> objectives) {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("QuestSave ID tidak boleh kosong.");
            }
            this.id = id.trim();
            this.status = status == null ? "LOCKED" : status.trim().toUpperCase();
            this.objectives = new ArrayList<ObjectiveSave>();
            if (objectives != null) {
                this.objectives.addAll(objectives);
            }
        }

        public String id() {
            return id;
        }

        public String status() {
            return status;
        }

        public List<ObjectiveSave> objectives() {
            return Collections.unmodifiableList(objectives);
        }

        public JSONObject toJSON() {
            JSONObject json = new JSONObject();
            json.setString("id", id);
            json.setString("status", status);
            JSONArray objArr = new JSONArray();
            for (ObjectiveSave o : objectives) {
                objArr.append(o.toJSON());
            }
            json.setJSONArray("objectives", objArr);
            return json;
        }

        public static QuestSave fromJSON(JSONObject json) {
            if (json == null) {
                throw new IllegalArgumentException("Data quest save null.");
            }
            if (!json.hasKey("id")) {
                throw new IllegalArgumentException("Quest tidak memiliki field 'id'.");
            }
            String id = json.getString("id");
            String status = json.getString("status", "LOCKED");
            List<ObjectiveSave> list = new ArrayList<ObjectiveSave>();
            if (json.hasKey("objectives")) {
                JSONArray arr = json.getJSONArray("objectives");
                for (int i = 0; i < arr.size(); i++) {
                    list.add(ObjectiveSave.fromJSON(arr.getJSONObject(i)));
                }
            }
            return new QuestSave(id, status, list);
        }
    }

    // --- Sub-model Puzzle Save ---
    public static final class PuzzleSave {
        private final String id;
        private final boolean solved;
        private final int wrongAttempts;
        private final int hintLevel;

        public PuzzleSave(String id, boolean solved, int wrongAttempts, int hintLevel) {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("PuzzleSave ID tidak boleh kosong.");
            }
            this.id = id.trim();
            this.solved = solved;
            this.wrongAttempts = Math.max(0, wrongAttempts);
            this.hintLevel = Math.max(0, hintLevel);
        }

        public String id() {
            return id;
        }

        public boolean isSolved() {
            return solved;
        }

        public int wrongAttempts() {
            return wrongAttempts;
        }

        public int hintLevel() {
            return hintLevel;
        }

        public JSONObject toJSON() {
            JSONObject json = new JSONObject();
            json.setString("id", id);
            json.setBoolean("solved", solved);
            json.setInt("wrongAttempts", wrongAttempts);
            json.setInt("hintLevel", hintLevel);
            return json;
        }

        public static PuzzleSave fromJSON(JSONObject json) {
            if (json == null) {
                throw new IllegalArgumentException("Data puzzle save null.");
            }
            if (!json.hasKey("id")) {
                throw new IllegalArgumentException("Puzzle tidak memiliki field 'id'.");
            }
            String id = json.getString("id");
            boolean solved = json.getBoolean("solved", false);
            int wrongAttempts = json.getInt("wrongAttempts", 0);
            int hintLevel = json.getInt("hintLevel", 0);
            return new PuzzleSave(id, solved, wrongAttempts, hintLevel);
        }
    }

    // --- State Utama SaveData ---
    private final int schemaVersion;
    private final long timestamp;
    private final String areaId;
    private final float playerX;
    private final float playerY;
    private final Player.Facing playerFacing;
    private final List<String> discoveredClueIds;
    private final String activeQuestId;
    private final List<QuestSave> quests;
    private final List<PuzzleSave> puzzles;
    private final Map<String, Boolean> worldFlags;

    public SaveData(int schemaVersion, long timestamp, String areaId,
                    float playerX, float playerY, Player.Facing playerFacing,
                    List<String> discoveredClueIds, String activeQuestId,
                    List<QuestSave> quests, List<PuzzleSave> puzzles,
                    Map<String, Boolean> worldFlags) {
        if (schemaVersion <= 0) {
            throw new IllegalArgumentException("Versi skema harus lebih besar dari nol.");
        }
        if (areaId == null || areaId.trim().isEmpty()) {
            throw new IllegalArgumentException("Area ID tidak boleh kosong.");
        }
        this.schemaVersion = schemaVersion;
        this.timestamp = timestamp;
        this.areaId = areaId.trim();
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerFacing = playerFacing == null ? Player.Facing.DOWN : playerFacing;

        this.discoveredClueIds = new ArrayList<String>();
        if (discoveredClueIds != null) {
            for (String c : discoveredClueIds) {
                if (c != null && !c.trim().isEmpty() && !this.discoveredClueIds.contains(c.trim())) {
                    this.discoveredClueIds.add(c.trim());
                }
            }
        }

        this.activeQuestId = activeQuestId == null ? null : activeQuestId.trim();

        this.quests = new ArrayList<QuestSave>();
        if (quests != null) {
            this.quests.addAll(quests);
        }

        this.puzzles = new ArrayList<PuzzleSave>();
        if (puzzles != null) {
            this.puzzles.addAll(puzzles);
        }

        this.worldFlags = new LinkedHashMap<String, Boolean>();
        if (worldFlags != null) {
            this.worldFlags.putAll(worldFlags);
        }
    }

    public int schemaVersion() {
        return schemaVersion;
    }

    public long timestamp() {
        return timestamp;
    }

    public String areaId() {
        return areaId;
    }

    public float playerX() {
        return playerX;
    }

    public float playerY() {
        return playerY;
    }

    public Player.Facing playerFacing() {
        return playerFacing;
    }

    public List<String> discoveredClueIds() {
        return Collections.unmodifiableList(discoveredClueIds);
    }

    public String activeQuestId() {
        return activeQuestId;
    }

    public List<QuestSave> quests() {
        return Collections.unmodifiableList(quests);
    }

    public QuestSave quest(String questId) {
        if (questId == null) {
            return null;
        }
        for (QuestSave qs : quests) {
            if (qs.id().equals(questId)) {
                return qs;
            }
        }
        return null;
    }

    public List<PuzzleSave> puzzles() {
        return Collections.unmodifiableList(puzzles);
    }

    public PuzzleSave puzzle(String puzzleId) {
        if (puzzleId == null) {
            return null;
        }
        for (PuzzleSave ps : puzzles) {
            if (ps.id().equals(puzzleId)) {
                return ps;
            }
        }
        return null;
    }

    public Map<String, Boolean> worldFlags() {
        return Collections.unmodifiableMap(worldFlags);
    }

    public boolean isWorldFlag(String flagName, boolean defaultValue) {
        if (flagName == null || !worldFlags.containsKey(flagName)) {
            return defaultValue;
        }
        Boolean val = worldFlags.get(flagName);
        return val == null ? defaultValue : val.booleanValue();
    }

    // --- Serialisasi JSON ---

    public JSONObject toJSON() {
        JSONObject root = new JSONObject();
        root.setInt("schemaVersion", schemaVersion);
        root.setLong("timestamp", timestamp);
        root.setString("areaId", areaId);

        JSONObject playerObj = new JSONObject();
        playerObj.setFloat("x", playerX);
        playerObj.setFloat("y", playerY);
        playerObj.setString("facing", playerFacing.name());
        root.setJSONObject("player", playerObj);

        JSONArray cluesArr = new JSONArray();
        for (String c : discoveredClueIds) {
            cluesArr.append(c);
        }
        root.setJSONArray("clues", cluesArr);

        JSONObject questsObj = new JSONObject();
        if (activeQuestId != null) {
            questsObj.setString("activeQuestId", activeQuestId);
        }
        JSONArray questList = new JSONArray();
        for (QuestSave q : quests) {
            questList.append(q.toJSON());
        }
        questsObj.setJSONArray("list", questList);
        root.setJSONObject("quests", questsObj);

        JSONArray puzzlesArr = new JSONArray();
        for (PuzzleSave p : puzzles) {
            puzzlesArr.append(p.toJSON());
        }
        root.setJSONArray("puzzles", puzzlesArr);

        JSONObject flagsObj = new JSONObject();
        for (Map.Entry<String, Boolean> entry : worldFlags.entrySet()) {
            flagsObj.setBoolean(entry.getKey(), entry.getValue());
        }
        root.setJSONObject("worldFlags", flagsObj);

        return root;
    }

    public static SaveData fromJSON(JSONObject root) {
        if (root == null) {
            throw new IllegalArgumentException("Data root JSON null.");
        }

        if (!root.hasKey("schemaVersion")) {
            throw new IllegalArgumentException("Format data save rusak: field 'schemaVersion' tidak ditemukan.");
        }
        int version = root.getInt("schemaVersion");
        if (version != CURRENT_SCHEMA_VERSION) {
            throw new IllegalArgumentException("Versi skema save tidak didukung: v" + version + " (mendukung: v" + CURRENT_SCHEMA_VERSION + ").");
        }

        long timestamp = root.hasKey("timestamp") ? root.getLong("timestamp") : System.currentTimeMillis();

        if (!root.hasKey("areaId")) {
            throw new IllegalArgumentException("Format data save rusak: field 'areaId' tidak ditemukan.");
        }
        String areaId = root.getString("areaId");

        if (!root.hasKey("player")) {
            throw new IllegalArgumentException("Format data save rusak: field 'player' tidak ditemukan.");
        }
        JSONObject playerObj = root.getJSONObject("player");
        if (!playerObj.hasKey("x") || !playerObj.hasKey("y")) {
            throw new IllegalArgumentException("Format data save rusak: koordinat pemain tidak lengkap.");
        }
        float playerX = playerObj.getFloat("x");
        float playerY = playerObj.getFloat("y");
        String facingStr = playerObj.getString("facing", "DOWN");
        Player.Facing facing;
        try {
            facing = Player.Facing.valueOf(facingStr);
        } catch (IllegalArgumentException ex) {
            facing = Player.Facing.DOWN;
        }

        List<String> clues = new ArrayList<String>();
        if (root.hasKey("clues")) {
            JSONArray cluesArr = root.getJSONArray("clues");
            for (int i = 0; i < cluesArr.size(); i++) {
                clues.add(cluesArr.getString(i));
            }
        }

        String activeQuestId = null;
        List<QuestSave> quests = new ArrayList<QuestSave>();
        if (root.hasKey("quests")) {
            JSONObject questsObj = root.getJSONObject("quests");
            if (questsObj.hasKey("activeQuestId")) {
                activeQuestId = questsObj.getString("activeQuestId");
            }
            if (questsObj.hasKey("list")) {
                JSONArray questList = questsObj.getJSONArray("list");
                for (int i = 0; i < questList.size(); i++) {
                    quests.add(QuestSave.fromJSON(questList.getJSONObject(i)));
                }
            }
        }

        List<PuzzleSave> puzzles = new ArrayList<PuzzleSave>();
        if (root.hasKey("puzzles")) {
            JSONArray puzzlesArr = root.getJSONArray("puzzles");
            for (int i = 0; i < puzzlesArr.size(); i++) {
                puzzles.add(PuzzleSave.fromJSON(puzzlesArr.getJSONObject(i)));
            }
        }

        Map<String, Boolean> flags = new LinkedHashMap<String, Boolean>();
        if (root.hasKey("worldFlags")) {
            JSONObject flagsObj = root.getJSONObject("worldFlags");
            for (Object k : flagsObj.keys()) {
                String key = (String) k;
                flags.put(key, flagsObj.getBoolean(key));
            }
        }

        return new SaveData(version, timestamp, areaId, playerX, playerY, facing, clues, activeQuestId, quests, puzzles, flags);
    }
}
