import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Antarmuka interaktif dan pengelola Puzzle Screen sesuai PRD Seksi 5, 10.4, dan 11.1.
 *
 * Mengatur tampilan modal puzzle 320x180, navigasi dial angka (keyboard & mouse),
 * penanganan permintaan petunjuk (hint), evaluasi kombinasi jawaban, serta umpan balik
 * edukatif yang jelas tanpa merusak progres saat salah (AC-05).
 */
public final class PuzzleSystem {

    public interface OnSolvedListener {
        void onPuzzleSolved(Puzzle puzzle);
    }

    private static final int FRAME_X = 10;
    private static final int FRAME_Y = 8;
    private static final int FRAME_W = 300;
    private static final int FRAME_H = 164;

    private Puzzle puzzle = null;
    private boolean open = false;
    private int[] dials = new int[3];
    private int selectedDial = 0;

    private String feedbackMessage = null;
    private boolean lastResultSuccess = false;
    private boolean isError = false;

    private int hoveredDial = -1;
    private int hoveredArrow = 0; // -1: up, 1: down
    private int hoveredAction = -1; // 0: submit, 1: hint, 2: close
    private boolean hoveredClose = false;

    private OnSolvedListener solvedListener = null;

    public PuzzleSystem() {
    }

    public void setOnSolvedListener(OnSolvedListener listener) {
        this.solvedListener = listener;
    }

    public boolean isOpen() {
        return open;
    }

    public Puzzle puzzle() {
        return puzzle;
    }

    public int selectedDial() {
        return selectedDial;
    }

    public int dialValue(int index) {
        if (index >= 0 && index < dials.length) {
            return dials[index];
        }
        return -1;
    }

    public int[] currentDials() {
        return dials.clone();
    }

    public void setDialValue(int index, int value) {
        if (index >= 0 && index < dials.length) {
            dials[index] = Math.max(0, Math.min(9, value));
        }
    }

    public String feedbackMessage() {
        return feedbackMessage;
    }

    public boolean isLastResultSuccess() {
        return lastResultSuccess;
    }

    public void open(Puzzle puzzle) {
        if (puzzle == null) {
            throw new IllegalArgumentException("Puzzle tidak boleh null.");
        }
        this.puzzle = puzzle;
        this.open = true;
        this.selectedDial = 0;

        if (dials.length != puzzle.dialCount()) {
            dials = new int[puzzle.dialCount()];
        }

        if (puzzle.isSolved()) {
            int[] sol = puzzle.solution();
            for (int i = 0; i < sol.length && i < dials.length; i++) {
                dials[i] = sol[i];
            }
            feedbackMessage = "KOTAK BESI SUDAH TERBUKA: " + puzzle.explanation();
            lastResultSuccess = true;
            isError = false;
        } else {
            feedbackMessage = "PILIH DIAL (A/D), UBAH ANGKA (W/S), LALU TEKAN ENTER UNTUK MENGUJI.";
            lastResultSuccess = false;
            isError = false;
        }
    }

    public void close() {
        this.open = false;
    }

    public void submit() {
        if (puzzle == null) {
            return;
        }

        if (puzzle.isSolved()) {
            feedbackMessage = "KOTAK BESI SUDAH TERBUKA: " + puzzle.explanation();
            lastResultSuccess = true;
            isError = false;
            return;
        }

        Puzzle.ValidationResult res = puzzle.validate(dials);
        if (res.isSuccess()) {
            lastResultSuccess = true;
            isError = false;
            feedbackMessage = res.message();
            if (solvedListener != null) {
                solvedListener.onPuzzleSolved(puzzle);
            }
        } else {
            lastResultSuccess = false;
            isError = true;
            feedbackMessage = res.message();
        }
    }

    public void requestHint() {
        if (puzzle == null) {
            return;
        }
        String hint = puzzle.requestNextHint();
        isError = false;
        feedbackMessage = "[PETUNJUK " + puzzle.revealedHintLevel() + "/" + puzzle.maxHintLevel() + "] " + hint;
    }

    // --- Input Keyboard ---

    public void keyPressed(char key, int keyCode) {
        if (!open) {
            return;
        }

        // 1. Tombol ESC untuk menutup
        if (key == 27 || keyCode == 27) {
            close();
            return;
        }

        // 2. Tombol Navigasi Dial (A / D atau Kiri / Kanan)
        if (key == 'a' || key == 'A' || keyCode == PConstants.LEFT) {
            selectedDial = (selectedDial - 1 + dials.length) % dials.length;
            return;
        }
        if (key == 'd' || key == 'D' || keyCode == PConstants.RIGHT) {
            selectedDial = (selectedDial + 1) % dials.length;
            return;
        }

        // 3. Tombol Ubah Nilai Dial (W / S atau Atas / Bawah)
        if (key == 'w' || key == 'W' || keyCode == PConstants.UP) {
            dials[selectedDial] = (dials[selectedDial] + 1) % 10;
            return;
        }
        if (key == 's' || key == 'S' || keyCode == PConstants.DOWN) {
            dials[selectedDial] = (dials[selectedDial] - 1 + 10) % 10;
            return;
        }

        // 4. Tombol Angka Langsung '0'..'9'
        if (key >= '0' && key <= '9') {
            dials[selectedDial] = key - '0';
            return;
        }

        // 5. Tombol Uji / Submit (Enter / Spasi)
        if (key == 10 || key == 13 || keyCode == 10 || keyCode == 13 || key == ' ' || keyCode == 32) {
            submit();
            return;
        }

        // 6. Tombol Minta Hint (H / T)
        if (key == 'h' || key == 'H' || key == 't' || key == 'T') {
            requestHint();
            return;
        }
    }

    // --- Input Mouse ---

    public void mouseMoved(float x, float y) {
        if (!open) {
            return;
        }

        hoveredDial = -1;
        hoveredArrow = 0;
        hoveredAction = -1;
        hoveredClose = false;

        // Tombol [X]
        if (x >= FRAME_X + FRAME_W - 24 && x <= FRAME_X + FRAME_W - 6 && y >= 11 && y <= 23) {
            hoveredClose = true;
            return;
        }

        // Dial buttons
        int dialW = 32;
        int spacing = 24;
        int totalW = dials.length * dialW + (dials.length - 1) * spacing;
        int startX = (GameManager.VIRTUAL_W - totalW) / 2;

        for (int i = 0; i < dials.length; i++) {
            int dx = startX + i * (dialW + spacing);
            if (x >= dx && x <= dx + dialW) {
                if (y >= 71 && y <= 80) {
                    hoveredDial = i;
                    hoveredArrow = -1; // UP
                    return;
                } else if (y >= 81 && y <= 97) {
                    hoveredDial = i;
                    hoveredArrow = 0; // BOX
                    return;
                } else if (y >= 98 && y <= 107) {
                    hoveredDial = i;
                    hoveredArrow = 1; // DOWN
                    return;
                }
            }
        }

        // Action buttons
        if (y >= 115 && y <= 128) {
            if (x >= 22 && x <= 102) {
                hoveredAction = 0; // BUKA
            } else if (x >= 108 && x <= 212) {
                hoveredAction = 1; // PETUNJUK
            } else if (x >= 218 && x <= 298) {
                hoveredAction = 2; // KEMBALI
            }
        }
    }

    public void mousePressed(float x, float y) {
        if (!open) {
            return;
        }

        // Klik di luar panel menutup modal
        if (x < FRAME_X || x > FRAME_X + FRAME_W || y < FRAME_Y || y > FRAME_Y + FRAME_H) {
            close();
            return;
        }

        // Klik tombol [X]
        if (hoveredClose) {
            close();
            return;
        }

        // Klik Dial
        if (hoveredDial >= 0) {
            selectedDial = hoveredDial;
            if (hoveredArrow == -1) {
                dials[selectedDial] = (dials[selectedDial] + 1) % 10;
            } else if (hoveredArrow == 1) {
                dials[selectedDial] = (dials[selectedDial] - 1 + 10) % 10;
            }
            return;
        }

        // Klik Action buttons
        if (hoveredAction == 0) {
            submit();
        } else if (hoveredAction == 1) {
            requestHint();
        } else if (hoveredAction == 2) {
            close();
        }
    }

    // --- Rendering Virtual 320x180 ---

    public void draw(PGraphics g) {
        if (!open || puzzle == null) {
            return;
        }

        // Dim background overlay
        g.noStroke();
        g.fill(Palette.alpha(Palette.INK, 215));
        g.rect(0, 0, GameManager.VIRTUAL_W, GameManager.VIRTUAL_H);

        // Frame utama modal
        PixelUI.panel(g, FRAME_X, FRAME_Y, FRAME_W, FRAME_H);

        // 1. Header
        String title = "TEKA-TEKI: " + puzzle.title();
        PixelUI.text(g, title, FRAME_X + 8, FRAME_Y + 7, Palette.ACCENT);

        String statusBadge = puzzle.isSolved() ? "[TERBUKA]" : "[TERKUNCI]";
        int statusColor = puzzle.isSolved() ? Palette.ACCENT : Palette.TEXT_MUTED;
        PixelFont.drawRight(g, statusBadge, FRAME_X + FRAME_W - 32, FRAME_Y + 7, 1, statusColor);

        PixelUI.button(g, FRAME_X + FRAME_W - 24, 11, 16, 12, "[X]", hoveredClose, true);
        PixelUI.divider(g, FRAME_X + FRAME_W / 2f, 26, FRAME_W - 16);

        // 2. Sub-panel Aturan / Instruksi
        drawInstructionBox(g);

        // 3. Dial Angka
        drawDials(g);

        // 4. Tombol Aksi
        drawActionButtons(g);

        // 5. Sub-panel Feedback
        drawFeedbackBox(g);
    }

    private void drawInstructionBox(PGraphics g) {
        int inX = 18;
        int inY = 29;
        int inW = 284;
        int inH = 39;

        g.noStroke();
        g.fill(Palette.PANEL_DARK);
        g.rect(inX, inY, inW, inH);
        g.fill(Palette.BORDER_DIM);
        g.rect(inX, inY, inW, 1);
        g.rect(inX, inY + inH - 1, inW, 1);
        g.rect(inX, inY, 1, inH);
        g.rect(inX + inW - 1, inY, 1, inH);

        PixelUI.text(g, "PETUNJUK PLAKAT KOTAK BESI [TOTAL DIGIT = 15]:", inX + 6, inY + 4, Palette.ACCENT);
        PixelUI.text(g, "1. DIGIT I   : PRIMA GENAP TERKECIL", inX + 12, inY + 13, Palette.TEXT_DIM);
        PixelUI.text(g, "2. DIGIT II  : DUA KALI LIPAT DIGIT I", inX + 12, inY + 21, Palette.TEXT_DIM);
        PixelUI.text(g, "3. DIGIT III : KUADRAT DARI ANGKA 3 (3X3)", inX + 12, inY + 29, Palette.TEXT_DIM);
    }

    private void drawDials(PGraphics g) {
        int dialW = 32;
        int spacing = 24;
        int totalW = dials.length * dialW + (dials.length - 1) * spacing;
        int startX = (GameManager.VIRTUAL_W - totalW) / 2;

        for (int i = 0; i < dials.length; i++) {
            int dx = startX + i * (dialW + spacing);
            boolean isSel = (selectedDial == i);
            boolean isHovBox = (hoveredDial == i && hoveredArrow == 0);
            boolean isHovUp = (hoveredDial == i && hoveredArrow == -1);
            boolean isHovDown = (hoveredDial == i && hoveredArrow == 1);

            // Tombol Up [^]
            PixelUI.button(g, dx, 71, dialW, 9, "[^]", isHovUp, true);

            // Kotak Angka
            g.noStroke();
            g.fill(isSel ? Palette.PANEL_LIGHT : (isHovBox ? Palette.PANEL : Palette.PANEL_DARK));
            g.rect(dx, 81, dialW, 16);

            g.fill(isSel ? Palette.ACCENT : Palette.BORDER_DIM);
            g.rect(dx, 81, dialW, 1);
            g.rect(dx, 96, dialW, 1);
            g.rect(dx, 81, 1, 16);
            g.rect(dx + dialW - 1, 81, 1, 16);

            // Angka Dial (skala 2 agar besar dan jelas terbaca)
            String numStr = String.valueOf(dials[i]);
            int numColor = isSel ? Palette.ACCENT : Palette.TEXT;
            PixelFont.draw(g, numStr, dx + 11, 83, 2, numColor);

            // Tombol Down [v]
            PixelUI.button(g, dx, 98, dialW, 9, "[v]", isHovDown, true);

            // Label Dial
            String label = "DIGIT " + (i == 0 ? "I" : (i == 1 ? "II" : "III"));
            PixelUI.centered(g, label, dx + dialW / 2, 109, Palette.TEXT_MUTED);
        }
    }

    private void drawActionButtons(PGraphics g) {
        boolean sel0 = (hoveredAction == 0);
        PixelUI.button(g, 22, 116, 80, 12, "[ENTER] BUKA", sel0, true);

        boolean sel1 = (hoveredAction == 1);
        String hintLabel = "[H] PETUNJUK (" + (puzzle != null ? puzzle.revealedHintLevel() : 0) + "/3)";
        PixelUI.button(g, 108, 116, 104, 12, hintLabel, sel1, true);

        boolean sel2 = (hoveredAction == 2);
        PixelUI.button(g, 218, 116, 80, 12, "[ESC] KEMBALI", sel2, true);
    }

    private void drawFeedbackBox(PGraphics g) {
        int fbX = 18;
        int fbY = 131;
        int fbW = 284;
        int fbH = 34;

        g.noStroke();
        g.fill(Palette.PANEL_DARK);
        g.rect(fbX, fbY, fbW, fbH);

        int borderColor = lastResultSuccess ? Palette.ACCENT : (isError ? Palette.DANGER : Palette.BORDER_DIM);
        g.fill(borderColor);
        g.rect(fbX, fbY, fbW, 1);
        g.rect(fbX, fbY + fbH - 1, fbW, 1);
        g.rect(fbX, fbY, 1, fbH);
        g.rect(fbX + fbW - 1, fbY, 1, fbH);

        String msg = feedbackMessage != null ? feedbackMessage : "";
        int textColor = lastResultSuccess ? Palette.ACCENT : (isError ? Palette.DANGER : Palette.TEXT_DIM);
        PixelFont.drawWrapped(g, msg, fbX + 6, fbY + 4, fbW - 12, 1, textColor);
    }
}
