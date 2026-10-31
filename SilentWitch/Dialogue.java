/**
 * Data dialog: satu pembicara dan satu atau lebih baris teks.
 *
 * Objek ini hanya menyimpan konten. Posisi baris aktif dan tampilan panel
 * ditangani DialogueSystem.
 */
public final class Dialogue {

    private final String speaker;
    private final String[] lines;

    /**
     * @throws IllegalArgumentException bila pembicara kosong atau tidak ada
     *                                  baris dialog yang valid.
     */
    public Dialogue(String speaker, String... lines) {
        if (speaker == null || speaker.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama pembicara dialog tidak boleh kosong.");
        }
        if (lines == null || lines.length == 0) {
            throw new IllegalArgumentException("Dialog harus memiliki minimal satu baris.");
        }
        for (int i = 0; i < lines.length; i++) {
            if (lines[i] == null || lines[i].trim().isEmpty()) {
                throw new IllegalArgumentException("Baris dialog " + i + " kosong.");
            }
        }
        this.speaker = speaker;
        this.lines = lines.clone();
    }

    public String speaker() {
        return speaker;
    }

    public int lineCount() {
        return lines.length;
    }

    public String line(int index) {
        if (index < 0 || index >= lines.length) {
            throw new IndexOutOfBoundsException("Baris dialog " + index + " tidak ada.");
        }
        return lines[index];
    }
}
