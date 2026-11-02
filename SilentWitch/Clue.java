import java.util.Arrays;

/**
 * Model data clue (bukti/petunjuk investigasi) sesuai PRD Seksi 4.2.
 *
 * Setiap clue memiliki ID unik dan stabil yang tidak berubah setelah dipakai
 * oleh dialog, puzzle, quest, atau save data.
 */
public final class Clue {

    private final String id;
    private final String title;
    private final String description;
    private final String source;
    private final String discoveryCondition;
    private final String[] relatedClueIds;
    private final String[] relatedQuestIds;
    private final String timelineTimestamp;
    private final String reliability;

    /**
     * Konstruktor lengkap untuk clue investigasi.
     *
     * @throws IllegalArgumentException bila ada field wajib bernilai null atau kosong.
     */
    public Clue(String id, String title, String description, String source,
                String discoveryCondition, String[] relatedClueIds,
                String[] relatedQuestIds, String timelineTimestamp,
                String reliability) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID clue tidak boleh kosong.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Judul clue tidak boleh kosong.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Deskripsi clue tidak boleh kosong.");
        }
        if (source == null || source.trim().isEmpty()) {
            throw new IllegalArgumentException("Sumber clue tidak boleh kosong.");
        }
        if (discoveryCondition == null || discoveryCondition.trim().isEmpty()) {
            throw new IllegalArgumentException("Kondisi penemuan clue tidak boleh kosong.");
        }
        if (reliability == null || reliability.trim().isEmpty()) {
            throw new IllegalArgumentException("Tingkat keandalan (reliability) clue tidak boleh kosong.");
        }

        this.id = id.trim();
        this.title = title.trim();
        this.description = description.trim();
        this.source = source.trim();
        this.discoveryCondition = discoveryCondition.trim();
        this.relatedClueIds = relatedClueIds == null ? new String[0] : copyArray(relatedClueIds);
        this.relatedQuestIds = relatedQuestIds == null ? new String[0] : copyArray(relatedQuestIds);
        this.timelineTimestamp = (timelineTimestamp == null || timelineTimestamp.trim().isEmpty())
                ? "-" : timelineTimestamp.trim();
        this.reliability = reliability.trim();
    }

    /**
     * Konstruktor praktis untuk clue tanpa relasi quest eksplisit.
     */
    public Clue(String id, String title, String description, String source,
                String discoveryCondition, String timelineTimestamp,
                String reliability) {
        this(id, title, description, source, discoveryCondition,
                new String[0], new String[0], timelineTimestamp, reliability);
    }

    private static String[] copyArray(String[] src) {
        String[] copy = new String[src.length];
        for (int i = 0; i < src.length; i++) {
            copy[i] = src[i] == null ? "" : src[i].trim();
        }
        return copy;
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

    public String source() {
        return source;
    }

    public String discoveryCondition() {
        return discoveryCondition;
    }

    public String[] relatedClueIds() {
        return relatedClueIds.clone();
    }

    public String[] relatedQuestIds() {
        return relatedQuestIds.clone();
    }

    public String timelineTimestamp() {
        return timelineTimestamp;
    }

    public String reliability() {
        return reliability;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Clue other = (Clue) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Clue[" + id + ": " + title + "]";
    }
}
