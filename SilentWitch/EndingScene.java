import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Layar Hasil Investigasi dan Ending (PRD Seksi 6.2, 6.3, 11.1 point 7, dan AC-08).
 *
 * Menampilkan hasil akhir deduksi pemain:
 * - True Ending (The Truth): deduksi benar dan seluruh bukti terkumpul.
 * - Partial Ending (The Unfinished Case): bukti belum lengkap saat menutup kasus.
 * - Bad Deduction (The False Accusation): deduksi keliru atau bertentangan dengan bukti.
 *
 * Dilengkapi ringkasan alasan investigasi dan statistik kasus.
 */
public final class EndingScene extends Scene {

    private static final int STAR_COUNT = 30;
    private static final int MOTE_COUNT = 14;

    private final DeductionSystem.EndingType endingType;
    private final int discoveredCluesCount;
    private final int totalCluesCount;
    private final boolean puzzleSolved;

    private final float[] starX = new float[STAR_COUNT];
    private final float[] starY = new float[STAR_COUNT];
    private final float[] starPhase = new float[STAR_COUNT];

    private final float[] moteX = new float[MOTE_COUNT];
    private final float[] moteY = new float[MOTE_COUNT];
    private final float[] moteSpeed = new float[MOTE_COUNT];
    private final float[] motePhase = new float[MOTE_COUNT];

    private float time = 0f;
    private boolean buttonHovered = false;

    public EndingScene(GameManager game, DeductionSystem.EndingType endingType,
                       int discoveredCluesCount, int totalCluesCount, boolean puzzleSolved) {
        super(game);
        this.endingType = (endingType != null) ? endingType : DeductionSystem.EndingType.UNFINISHED_CASE;
        this.discoveredCluesCount = discoveredCluesCount;
        this.totalCluesCount = totalCluesCount;
        this.puzzleSolved = puzzleSolved;

        for (int i = 0; i < STAR_COUNT; i++) {
            starX[i] = Palette.hash(i * 7 + 3) * GameManager.VIRTUAL_W;
            starY[i] = 4f + Palette.hash(i * 7 + 5) * 80f;
            starPhase[i] = Palette.hash(i * 7 + 8) * 6.28318f;
        }

        for (int i = 0; i < MOTE_COUNT; i++) {
            moteX[i] = 10f + Palette.hash(i * 11 + 2) * 300f;
            moteY[i] = 90f + Palette.hash(i * 11 + 4) * 86f;
            moteSpeed[i] = 2.0f + Palette.hash(i * 11 + 6) * 5f;
            motePhase[i] = Palette.hash(i * 11 + 9) * 6.28318f;
        }
    }

    public DeductionSystem.EndingType endingType() {
        return endingType;
    }

    public int discoveredCluesCount() {
        return discoveredCluesCount;
    }

    @Override
    public void update(float dt) {
        time += dt;

        for (int i = 0; i < MOTE_COUNT; i++) {
            moteY[i] -= moteSpeed[i] * dt;
            if (moteY[i] < 88f) {
                moteY[i] = 180f;
            }
        }
    }

    @Override
    public void draw(PGraphics g) {
        drawBackdrop(g);
        PixelUI.vignette(g);

        int panelX = 14;
        int panelY = 8;
        int panelW = 292;
        int panelH = 164;

        PixelUI.panel(g, panelX, panelY, panelW, panelH);

        // Header Title
        int titleColor;
        String headerTitle;
        String subHeader;

        switch (endingType) {
            case TRUE_ENDING:
                titleColor = Palette.WARM;
                headerTitle = "ENDING I: THE TRUTH";
                subHeader = "KEBENARAN DI BALIK KESUNYIAN ASHEN VILLAGE";
                break;
            case UNFINISHED_CASE:
                titleColor = Palette.ACCENT;
                headerTitle = "ENDING II: THE UNFINISHED CASE";
                subHeader = "KASUS MENGGANTUNG TANPA KEPASTIAN HUKUM";
                break;
            case FALSE_ACCUSATION:
            default:
                titleColor = Palette.DANGER;
                headerTitle = "ENDING III: THE FALSE ACCUSATION";
                subHeader = "TUDUHAN KELIRU DAN HILANGNYA KEBENARAN";
                break;
        }

        PixelUI.centered(g, headerTitle, 160, panelY + 7, 2, titleColor);
        PixelUI.centered(g, subHeader, 160, panelY + 20, Palette.TEXT_DIM);
        PixelUI.divider(g, 160, panelY + 28, panelW - 24);

        // Narasi Hasil Kasus
        String narrativeText = getNarrativeText();
        PixelFont.drawWrapped(g, narrativeText, panelX + 10, panelY + 34, panelW - 20, 1, Palette.TEXT);

        // Ringkasan Alasan dan Bukti (PRD 6.3)
        int boxY = panelY + 98;
        int boxW = panelW - 20;
        int boxH = 34;
        PixelUI.panel(g, panelX + 10, boxY, boxW, boxH);

        PixelUI.text(g, "RANGKUMAN INVESTIGASI KASUS:", panelX + 16, boxY + 4, Palette.ACCENT);

        String statLine1 = "BUKTI: " + discoveredCluesCount + "/" + totalCluesCount
                + "   KOTAK BESI: " + (puzzleSolved ? "TERPECAHKAN" : "TERKUNCI")
                + "   QUEST: LENGKAP";
        PixelUI.text(g, statLine1, panelX + 16, boxY + 14, Palette.TEXT);

        String verdictText;
        int verdictColor;
        if (endingType == DeductionSystem.EndingType.TRUE_ENDING) {
            verdictText = "HASIL: GARRICK TERBUKTI BERSALAH - KEADILAN UNTUK CLARA DITEGAKKAN";
            verdictColor = Palette.WARM;
        } else if (endingType == DeductionSystem.EndingType.UNFINISHED_CASE) {
            verdictText = "HASIL: BUKTI KURANG MEMADAI - PELAKU SEBENARNYA BELUM TERDAKWA";
            verdictColor = Palette.TEXT_MUTED;
        } else {
            verdictText = "HASIL: SALAH MENUDUH PIHAK LAIN - GARRICK LOLOS DARI JERAT HUKUM";
            verdictColor = Palette.DANGER;
        }
        PixelUI.text(g, verdictText, panelX + 16, boxY + 24, verdictColor);

        // Tombol Kembali ke Menu
        int btnW = 160;
        int btnH = 14;
        int btnX = 160 - btnW / 2;
        int btnY = panelY + panelH - 18;

        PixelUI.button(g, btnX, btnY, btnW, btnH, "[ENTER] KEMBALI KE MENU UTAMA", buttonHovered, true);
    }

    private String getNarrativeText() {
        switch (endingType) {
            case TRUE_ENDING:
                return "Silent Witch membeberkan kronologi tak terbantahkan. Menyadari alibinya runtuh di hadapan bukti rekayasa jendela dan catatan jaga, Garrick mengakui perbuatannya di depan tetua desa.\n\nKematian Clara Thorne bukan kutukan gaib, melainkan keberanian seorang anak yang gugur demi menjaga warisan desa dari jerat kultus bayangan. Nama Clara dihormati dan keheningan damai kembali ke Ashen Village.";
            case UNFINISHED_CASE:
                return "Penyelidikan terpaksa ditutup sementara karena bukti yang terkumpul belum cukup kuat untuk membuktikan persekongkolan di pengadilan desa. Garrick menghilang tanpa jejak sebelum fajar menyingsing.\n\nKematian Clara Thorne tetap menjadi misteri yang menggantung. Kabut dingin Ashen Village masih menyelimuti lorong-lorong manor tua dalam ketidakpastian.";
            case FALSE_ACCUSATION:
            default:
                return "Tuduhan keliru dijatuhkan pada pihak yang tidak bersalah. Rekayasa alibi cungkilan jendela berhasil mengelabui penyelidikan, membiarkan pelaku sebenarnya bebas tanpa terjamah hukum.\n\nRelik terlarang Sayap Timur lenyap ke dalam bayang-bayang kultus, sementara kebenaran tentang pengorbanan Clara Thorne terkubur selamanya dalam kesunyian.";
        }
    }

    private void drawBackdrop(PGraphics g) {
        g.noStroke();

        for (int y = 0; y < 120; y++) {
            float t = y / 120f;
            g.fill(Palette.mix(Palette.NIGHT_TOP, Palette.NIGHT_BOTTOM, t));
            g.rect(0, y, GameManager.VIRTUAL_W, 1);
        }

        for (int i = 0; i < STAR_COUNT; i++) {
            int twinkle = (int) (80 + 70 * Math.sin(time * 1.5f + starPhase[i]));
            g.fill(Palette.alpha(Palette.MOON, Math.max(15, twinkle)));
            g.rect(starX[i], starY[i], 1, 1);
        }

        g.fill(0xFF090A10);
        g.rect(0, 118, GameManager.VIRTUAL_W, 62);

        // Motes kabut
        for (int i = 0; i < MOTE_COUNT; i++) {
            float x = moteX[i] + (float) Math.sin(time * 0.6f + motePhase[i]) * 4f;
            int alpha = 20 + (int) (16 * (0.5f + 0.5f * Math.sin(time * 1.4f + motePhase[i])));
            g.fill(Palette.alpha(Palette.FOG, alpha));
            g.rect(x, moteY[i], 1, 1);
        }
    }

    @Override
    public void keyPressed(char key, int keyCode) {
        if (key == '\n' || key == '\r' || key == ' ' || keyCode == 10 || keyCode == 13 || keyCode == 27) {
            game.showMenu();
        }
    }

    @Override
    public void mouseMoved(float x, float y) {
        int btnW = 160;
        int btnH = 14;
        int btnX = 160 - btnW / 2;
        int btnY = 172 - 18;
        buttonHovered = (x >= btnX && x <= btnX + btnW && y >= btnY && y <= btnY + btnH);
    }

    @Override
    public void mousePressed(float x, float y) {
        int btnW = 160;
        int btnH = 14;
        int btnX = 160 - btnW / 2;
        int btnY = 172 - 18;
        if (x >= btnX && x <= btnX + btnW && y >= btnY && y <= btnY + btnH) {
            game.showMenu();
        }
    }
}
