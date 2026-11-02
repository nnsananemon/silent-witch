import java.util.List;

import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Sistem Rekonstruksi Deduksi Akhir sesuai PRD Seksi 6.2, 6.3, dan Silent_Witch_Canon.md.
 *
 * Pemain menyusun 3 kesimpulan deduksi di Altar Sayap Timur berdasarkan bukti
 * investigasi yang dikumpulkan di Buku Kasus:
 * 1. Menentukan identitas pelaku sebenarnya.
 * 2. Menentukan bukti kunci yang mematahkan alibi palsu.
 * 3. Menentukan motif konspirasi relik.
 *
 * Hasil evaluasi menghasilkan salah satu dari 3 variasi ending kanonis:
 * - The Truth (True Ending)
 * - The Unfinished Case (Partial Ending)
 * - The False Accusation (Bad Deduction)
 */
public final class DeductionSystem {

    public enum EndingType {
        TRUE_ENDING("THE TRUTH", "KEBENARAN DI BALIK KESUNYIAN"),
        UNFINISHED_CASE("THE UNFINISHED CASE", "KASUS MENGGANTUNG"),
        FALSE_ACCUSATION("THE FALSE ACCUSATION", "TUDUHAN KELIRU");

        private final String title;
        private final String subtitle;

        EndingType(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }

        public String title() {
            return title;
        }

        public String subtitle() {
            return subtitle;
        }
    }

    public interface OnDeductionComplete {
        void onComplete(EndingType endingType);
    }

    private static final int FRAME_X = 10;
    private static final int FRAME_Y = 8;
    private static final int FRAME_W = 300;
    private static final int FRAME_H = 164;

    private static final String[] QUESTIONS = {
            "SIAPA PELAKU SEBENARNYA DI BALIK KEMATIAN CLARA THORNE?",
            "BUKTI APA YANG MEMATAHKAN ALIBI PALSU PELAKU?",
            "APA MOTIF UTAMA DI BALIK TINDAK KEJAHATAN INI?"
    };

    private static final String[][] OPTIONS = {
            {
                    "GARRICK (PENJAGA MALAM AULA)",
                    "PENYUSUP ASING DARI HUTAN BERKABUT",
                    "WARGA DESA YANG KETAKUTAN"
            },
            {
                    "CUNGKILAN JENDELA DIREKAYASA & JADWAL JAGA DIMANIPULASI",
                    "SUARA LANGKAH KAKI YANG TIDAK MENINGGALKAN JEJAK",
                    "PINTU GERBANG YANG BERDERAK DI TENGAH MALAM"
            },
            {
                    "BERSEKONGKOL MENCURI RELIK SAYAP TIMUR DENGAN KULTUS",
                    "MELINDUNGI AULA DARI ANCAMAN KABUT MALAM",
                    "PERTIKAIAN TIDAK SENGAJA TANPA MOTIF TERTENTU"
            }
    };

    private boolean open = false;
    private int currentStep = 0;
    private int selectedOption = 0;
    private int hoveredOption = -1;
    private final int[] chosenAnswers = new int[3];

    private int discoveredCluesCount = 0;
    private OnDeductionComplete onCompleteListener;

    public DeductionSystem() {
    }

    public void setOnCompleteListener(OnDeductionComplete listener) {
        this.onCompleteListener = listener;
    }

    public boolean isOpen() {
        return open;
    }

    public int currentStep() {
        return currentStep;
    }

    public int selectedOption() {
        return selectedOption;
    }

    public int chosenAnswer(int step) {
        if (step >= 0 && step < chosenAnswers.length) {
            return chosenAnswers[step];
        }
        return -1;
    }

    public void open(int discoveredCluesCount) {
        this.open = true;
        this.currentStep = 0;
        this.selectedOption = 0;
        this.hoveredOption = -1;
        this.discoveredCluesCount = discoveredCluesCount;
        for (int i = 0; i < chosenAnswers.length; i++) {
            chosenAnswers[i] = -1;
        }
    }

    public void close() {
        this.open = false;
    }

    public void triggerPartialEnding() {
        close();
        if (onCompleteListener != null) {
            onCompleteListener.onComplete(EndingType.UNFINISHED_CASE);
        }
    }

    public void submitOption(int optionIndex) {
        if (!open || currentStep >= QUESTIONS.length) {
            return;
        }
        chosenAnswers[currentStep] = optionIndex;
        if (currentStep < QUESTIONS.length - 1) {
            currentStep++;
            selectedOption = 0;
            hoveredOption = -1;
        } else {
            // Evaluasi deduksi akhir
            EndingType result = evaluateEnding();
            close();
            if (onCompleteListener != null) {
                onCompleteListener.onComplete(result);
            }
        }
    }

    public EndingType evaluateEnding() {
        // Syarat True Ending:
        // 1. Minimal 4 bukti aula ditemukan
        // 2. Ketiga pertanyaan deduksi dijawab dengan pilihan 0 (jawaban kanonis yang benar)
        boolean allCorrect = (chosenAnswers[0] == 0 && chosenAnswers[1] == 0 && chosenAnswers[2] == 0);
        if (allCorrect && discoveredCluesCount >= 4) {
            return EndingType.TRUE_ENDING;
        } else if (discoveredCluesCount < 4) {
            return EndingType.UNFINISHED_CASE;
        } else {
            return EndingType.FALSE_ACCUSATION;
        }
    }

    public void keyPressed(char key, int keyCode) {
        if (!open) {
            return;
        }

        if (keyCode == 27 || key == 27) { // ESC
            close();
            return;
        }

        if (keyCode == PConstants.UP || key == 'w' || key == 'W') {
            selectedOption = (selectedOption - 1 + OPTIONS[currentStep].length) % OPTIONS[currentStep].length;
            hoveredOption = -1;
            return;
        }

        if (keyCode == PConstants.DOWN || key == 's' || key == 'S') {
            selectedOption = (selectedOption + 1) % OPTIONS[currentStep].length;
            hoveredOption = -1;
            return;
        }

        if (key == '\n' || key == '\r' || key == ' ' || keyCode == 10 || keyCode == 13) {
            submitOption(selectedOption);
            return;
        }

        // Shortcut angka 1, 2, 3
        if (key >= '1' && key <= '3') {
            int idx = key - '1';
            if (idx < OPTIONS[currentStep].length) {
                submitOption(idx);
            }
        }
    }

    public void mouseMoved(float x, float y) {
        if (!open) {
            return;
        }
        hoveredOption = optionAt(x, y);
    }

    public void mousePressed(float x, float y) {
        if (!open) {
            return;
        }

        // Klik tombol tutup [X] di kanan atas
        if (x >= FRAME_X + FRAME_W - 18 && x <= FRAME_X + FRAME_W - 4 && y >= FRAME_Y + 4 && y <= FRAME_Y + 16) {
            close();
            return;
        }

        int clicked = optionAt(x, y);
        if (clicked >= 0) {
            submitOption(clicked);
            return;
        }

        // Klik di luar jendela modal menutup modal
        if (x < FRAME_X || x > FRAME_X + FRAME_W || y < FRAME_Y || y > FRAME_Y + FRAME_H) {
            close();
        }
    }

    private int optionAt(float x, float y) {
        int optX = FRAME_X + 16;
        int optW = FRAME_W - 32;
        int startY = FRAME_Y + 62;
        int optH = 22;
        int gap = 6;

        for (int i = 0; i < OPTIONS[currentStep].length; i++) {
            int curY = startY + i * (optH + gap);
            if (x >= optX && x <= optX + optW && y >= curY && y <= curY + optH) {
                return i;
            }
        }
        return -1;
    }

    public void draw(PGraphics g) {
        if (!open) {
            return;
        }

        PixelUI.dim(g, 205);
        PixelUI.panel(g, FRAME_X, FRAME_Y, FRAME_W, FRAME_H);

        // Header
        PixelUI.centered(g, "REKONSTRUKSI DEDUKSI KASUS", FRAME_X + FRAME_W / 2, FRAME_Y + 7, 2, Palette.ACCENT);
        PixelUI.divider(g, FRAME_X + FRAME_W / 2, FRAME_Y + 22, FRAME_W - 24);

        // Tombol Close [X]
        PixelUI.button(g, FRAME_X + FRAME_W - 16, FRAME_Y + 5, 11, 10, "X", false, true);

        // Indikator langkah
        String stepText = "TAHAP DEDUKSI " + (currentStep + 1) + " DARI 3";
        PixelUI.centered(g, stepText, FRAME_X + FRAME_W / 2, FRAME_Y + 26, Palette.ACCENT_DIM);

        // Pertanyaan aktif
        String question = QUESTIONS[currentStep];
        PixelFont.drawWrapped(g, question, FRAME_X + 14, FRAME_Y + 38, FRAME_W - 28, 1, Palette.TEXT);

        // Daftar Pilihan
        int optX = FRAME_X + 14;
        int optW = FRAME_W - 28;
        int startY = FRAME_Y + 62;
        int optH = 22;
        int gap = 6;

        for (int i = 0; i < OPTIONS[currentStep].length; i++) {
            boolean isSelected = (i == selectedOption || i == hoveredOption);
            int curY = startY + i * (optH + gap);

            // Kotak opsi
            PixelUI.button(g, optX, curY, optW, optH, "", isSelected, true);

            String prefix = "[" + (i + 1) + "] ";
            int labelColor = isSelected ? Palette.ACCENT : Palette.TEXT;
            PixelUI.text(g, prefix, optX + 6, curY + 4, Palette.ACCENT_DIM);
            PixelFont.drawWrapped(g, OPTIONS[currentStep][i], optX + 26, curY + 4, optW - 32, 1, labelColor);

            if (isSelected) {
                PixelUI.text(g, ">", optX + 1, curY + 7, Palette.ACCENT);
            }
        }

        // Footer petunjuk
        int footY = FRAME_Y + FRAME_H - 14;
        PixelUI.divider(g, FRAME_X + FRAME_W / 2, footY - 2, FRAME_W - 24);
        PixelUI.centered(g, "W/S ATAU PANAH: PILIH   ENTER/KLIK: KONFIRMASI   ESC: BATAL",
                FRAME_X + FRAME_W / 2, footY + 2, Palette.TEXT_MUTED);
    }
}
