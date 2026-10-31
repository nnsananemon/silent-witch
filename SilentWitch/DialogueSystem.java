import processing.core.PGraphics;

/**
 * Panel dialog di bagian bawah layar.
 *
 * Kelas ini hanya menangani state dialog (baris aktif, buka/tutup) dan
 * penggambaran panel. Input tetap diteruskan oleh GameplayScene agar gerakan
 * pemain dapat dikunci saat dialog terbuka.
 */
public final class DialogueSystem {

    public static final int PANEL_X = 8;
    public static final int PANEL_W = 304;
    public static final int PANEL_H = 56;
    public static final int PANEL_Y = GameManager.VIRTUAL_H - 12 - PANEL_H - 4;

    private Dialogue dialogue;
    private int lineIndex;

    public boolean isOpen() {
        return dialogue != null;
    }

    public Dialogue dialogue() {
        return dialogue;
    }

    public int lineIndex() {
        return lineIndex;
    }

    public String speaker() {
        return dialogue == null ? "" : dialogue.speaker();
    }

    public String currentLine() {
        return dialogue == null ? "" : dialogue.line(lineIndex);
    }

    public boolean isLastLine() {
        return dialogue == null || lineIndex >= dialogue.lineCount() - 1;
    }

    /** Membuka dialog baru dan kembali ke baris pertama. */
    public void open(Dialogue next) {
        if (next == null) {
            throw new IllegalArgumentException("Dialog yang dibuka tidak boleh null.");
        }
        dialogue = next;
        lineIndex = 0;
    }

    /**
     * Maju ke baris berikutnya. Bila sudah di baris terakhir, dialog ditutup.
     *
     * @return true bila dialog masih terbuka setelah pemanggilan.
     */
    public boolean advance() {
        if (dialogue == null) {
            return false;
        }
        if (lineIndex + 1 < dialogue.lineCount()) {
            lineIndex++;
            return true;
        }
        close();
        return false;
    }

    public void close() {
        dialogue = null;
        lineIndex = 0;
    }

    public void draw(PGraphics g) {
        if (dialogue == null) {
            return;
        }

        PixelUI.dim(g, 70);
        PixelUI.panel(g, PANEL_X, PANEL_Y, PANEL_W, PANEL_H);

        // Penanda pembicara.
        g.noStroke();
        g.fill(Palette.ACCENT);
        g.rect(PANEL_X + 8, PANEL_Y + 7, 2, 7);
        PixelUI.text(g, dialogue.speaker(), PANEL_X + 14, PANEL_Y + 7, Palette.ACCENT);

        PixelFont.drawRight(g, (lineIndex + 1) + "/" + dialogue.lineCount(),
                PANEL_X + PANEL_W - 8, PANEL_Y + 7, 1, Palette.TEXT_MUTED);

        PixelUI.divider(g, PANEL_X + PANEL_W / 2f, PANEL_Y + 18, PANEL_W - 24);

        PixelFont.drawWrapped(g, dialogue.line(lineIndex), PANEL_X + 10, PANEL_Y + 23,
                PANEL_W - 20, 1, Palette.TEXT);
    }
}
