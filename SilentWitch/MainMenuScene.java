import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Main Menu bergaya sinematik: judul besar rata kiri, paragraf pengantar,
 * kolom nama penyihir, dan tombol pil. Latar tetap gothic (bulan, kabut,
 * siluet manor) agar selaras dengan cerita.
 *
 * Mendukung keyboard dan mouse. "Lanjutkan" aktif bila save valid tersedia.
 * Save rusak ditangani tanpa menghapus file asli (AC-07 / NFR-06), dan
 * "Mulai Baru" meminta konfirmasi sebelum menimpa save lama (PRD 7.1).
 */
public final class MainMenuScene extends Scene {

    // Dua aksi utama di kolom kiri.
    private static final String[] ITEMS = {
            "MULAI PERJALANAN",
            "LANJUTKAN"
    };
    private static final int CONTINUE_INDEX = 1;

    private static final int COL_X = 18;
    private static final int BTN_W = 156;
    private static final int BTN_PRIMARY_Y = 124;
    private static final int BTN_PRIMARY_H = 16;
    private static final int BTN_SECOND_Y = 144;
    private static final int BTN_SECOND_H = 14;
    private static final int NAME_Y = 92;
    private static final int NAME_H = 15;
    private static final int NAME_W = 190;

    private static final int QUIT_X = 254;
    private static final int QUIT_Y = 164;
    private static final int QUIT_W = 50;
    private static final int QUIT_H = 13;

    private static final int STAR_COUNT = 36;
    private static final int MOTE_COUNT = 34;
    private static final int FIREFLY_COUNT = 7;
    private static final int MAX_NAME_LEN = 14;

    private final float[] starX = new float[STAR_COUNT];
    private final float[] starY = new float[STAR_COUNT];
    private final float[] starPhase = new float[STAR_COUNT];

    private final float[] moteBaseX = new float[MOTE_COUNT];
    private final float[] moteBaseY = new float[MOTE_COUNT];
    private final float[] moteSpeed = new float[MOTE_COUNT];
    private final float[] motePhase = new float[MOTE_COUNT];

    private int selected = 0;
    private float time = 0f;

    private boolean canContinue = false;
    private String saveErrorMessage = null;
    private boolean confirmNewGameOpen = false;
    private int hoveredConfirm = -1; // 0: YA, 1: BATAL

    private String notice = "";
    private float noticeTime = 0f;
    private int hovered = -1;
    private boolean hoverQuit = false;

    private String nameInput;
    private boolean nameEditing = false;

    public MainMenuScene(GameManager game) {
        super(game);

        for (int i = 0; i < STAR_COUNT; i++) {
            starX[i] = Palette.hash(i * 5 + 1) * GameManager.VIRTUAL_W;
            starY[i] = 4f + Palette.hash(i * 5 + 2) * 74f;
            starPhase[i] = Palette.hash(i * 5 + 3) * 6.28318f;
        }

        for (int i = 0; i < MOTE_COUNT; i++) {
            moteBaseX[i] = 8f + Palette.hash(i * 7 + 11) * 304f;
            moteBaseY[i] = 92f + Palette.hash(i * 7 + 12) * 86f;
            moteSpeed[i] = 5f + Palette.hash(i * 7 + 13) * 11f;
            motePhase[i] = Palette.hash(i * 7 + 14) * 6.28318f;
        }

        String existing = (game != null) ? game.playerName() : null;
        nameInput = (existing != null && !existing.trim().isEmpty()
                && !GameManager.HERO_NAME_DEFAULT.equalsIgnoreCase(existing.trim())
                && !"PENYIHIR".equalsIgnoreCase(existing.trim())) ? existing.trim() : "";
        refreshSaveStatus();
    }

    @Override
    public void onEnter() {
        refreshSaveStatus();
    }

    public void refreshSaveStatus() {
        if (game != null && game.saveManager() != null) {
            SaveManager.LoadResult res = game.saveManager().load();
            if (res.isSuccess()) {
                canContinue = true;
                saveErrorMessage = null;
            } else if (res.isCorrupt()) {
                canContinue = false;
                saveErrorMessage = res.errorMessage();
            } else {
                canContinue = false;
                saveErrorMessage = null;
            }
        } else {
            canContinue = false;
            saveErrorMessage = null;
        }
        confirmNewGameOpen = false;
    }

    public boolean canContinue() {
        return canContinue;
    }

    public String saveErrorMessage() {
        return saveErrorMessage;
    }

    public boolean isConfirmNewGameOpen() {
        return confirmNewGameOpen;
    }

    public int selectedIndex() {
        return selected;
    }

    public String playerNameInput() {
        return effectiveName();
    }

    public boolean isNameEditing() {
        return nameEditing;
    }

    @Override
    public void update(float dt) {
        time += dt;

        if (noticeTime > 0f) {
            noticeTime = Math.max(0f, noticeTime - dt);
        }

        for (int i = 0; i < MOTE_COUNT; i++) {
            moteBaseY[i] -= moteSpeed[i] * dt;
            if (moteBaseY[i] < 92f) {
                moteBaseY[i] = 180f;
            }
        }
    }

    @Override
    public void draw(PGraphics g) {
        drawBackdrop(g);
        PixelUI.vignette(g);
        drawNameColumn(g);
        drawMenu(g);
        drawQuitPill(g);

        if (confirmNewGameOpen) {
            drawConfirmNewGame(g);
        }
    }

    // --- Latar gothic bergaya sinematik ---

    private void drawBackdrop(PGraphics g) {
        g.noStroke();

        for (int y = 0; y < 142; y++) {
            float t = y / 141f;
            g.fill(Palette.mix(Palette.NIGHT_TOP, 0xFF242742, t * 0.85f));
            g.rect(0, y, GameManager.VIRTUAL_W, 1);
        }

        for (int i = 0; i < STAR_COUNT; i++) {
            int twinkle = (int) (90 + 80 * Math.sin(time * 1.4f + starPhase[i]));
            g.fill(Palette.alpha(Palette.MOON, Math.max(18, twinkle)));
            g.rect(starX[i], starY[i], 1, 1);
        }

        drawCloudBank(g);
        drawMoon(g);
        drawShootingStar(g);
        drawSkyline(g);
        drawHillAndWitch(g);

        g.fill(0xFF090A10);
        g.rect(0, 141, GameManager.VIRTUAL_W, 39);

        drawFog(g);
        drawFireflies(g);
        drawMotes(g);
    }

    /** Bank awan di langit kanan; bergerak pelan agar latar terasa hidup. */
    private void drawCloudBank(PGraphics g) {
        g.noStroke();
        float span = 300f;

        // Lapisan jauh: lambat dan redup.
        for (int i = 0; i < 3; i++) {
            float x = wrap(150 + i * 90 - time * 5.5f, 140, 140 + span);
            float y = 58 + i * 10;
            drawCloud(g, x, y, 120, 34, 15);
            drawCloud(g, x - span, y, 120, 34, 15);
        }

        // Lapisan dekat: lebih cepat dan sedikit lebih terang.
        for (int i = 0; i < 2; i++) {
            float x = wrap(170 + i * 130 - time * 11f, 140, 140 + span);
            float y = 88 + i * 6;
            drawCloud(g, x, y, 150, 30, 24);
            drawCloud(g, x - span, y, 150, 30, 24);
        }

        // Sorot tepi bulan pada awan, berdenyut pelan.
        int glow = (int) (24 + 14 * Math.sin(time * 1.1f));
        g.fill(Palette.alpha(Palette.MOON, Math.max(8, glow)));
        g.rect(222, 62, 42, 1);
        g.rect(208, 74, 26, 1);
        g.rect(250, 52, 24, 1);
    }

    private void drawCloud(PGraphics g, float x, float y, float w, float h, int alpha) {
        g.fill(Palette.alpha(Palette.FOG, alpha));
        g.ellipse(x, y, w, h);
        g.fill(Palette.alpha(Palette.FOG, Math.max(1, alpha / 2)));
        g.ellipse(x - w * 0.26f, y + h * 0.12f, w * 0.62f, h * 0.75f);
        g.ellipse(x + w * 0.28f, y + h * 0.08f, w * 0.7f, h * 0.8f);
    }

    /** Membungkus nilai ke dalam rentang [min, max) untuk animasi berulang. */
    private static float wrap(float value, float min, float max) {
        float span = max - min;
        float r = (value - min) % span;
        if (r < 0f) {
            r += span;
        }
        return min + r;
    }

    private void drawMoon(PGraphics g) {
        float cx = 276f;
        float cy = 46f;
        float pulse = 1f + 0.07f * (float) Math.sin(time * 1.1f);
        int halo = (int) (10 + 8 * (0.5f + 0.5f * Math.sin(time * 1.1f)));

        g.noStroke();
        g.fill(Palette.alpha(Palette.MOON, halo));
        g.ellipse(cx, cy, 54 * pulse, 54 * pulse);
        g.fill(Palette.alpha(Palette.MOON, 12));
        g.ellipse(cx, cy, 38, 38);
        g.fill(Palette.MOON);
        g.ellipse(cx, cy, 22, 22);

        g.fill(0xFFD2CDBA);
        g.rect(cx - 5, cy - 5, 3, 2);
        g.rect(cx + 2, cy - 2, 4, 3);
        g.rect(cx - 3, cy + 4, 2, 2);
        g.rect(cx + 5, cy + 5, 2, 1);
    }

    /** Meteor tipis yang melintas sesekali di langit kanan atas. */
    private void drawShootingStar(PGraphics g) {
        float cycle = 8.5f;
        float t = time % cycle;
        if (t > 0.9f) {
            return;
        }
        float p = t / 0.9f;
        float fade = (float) Math.sin(Math.PI * p);
        float x = 152 + p * 178;
        float y = 12 + p * 34;

        g.noStroke();
        for (int i = 0; i < 9; i++) {
            int a = (int) (210 * fade * (1f - i / 9f));
            if (a <= 4) {
                continue;
            }
            g.fill(Palette.alpha(Palette.MOON, a));
            g.rect(x - i * 2, y - i, 2, 1);
        }
        g.fill(Palette.alpha(Palette.TEXT, (int) (255 * fade)));
        g.rect(x, y, 2, 1);
    }

    /** Kunang-kunang hangat yang melayang di lembah kanan bawah. */
    private void drawFireflies(PGraphics g) {
        g.noStroke();
        for (int i = 0; i < FIREFLY_COUNT; i++) {
            float px = 186 + Palette.hash(i * 13 + 41) * 118;
            float py = 118 + Palette.hash(i * 13 + 42) * 20;
            float wobbleX = (float) Math.sin(time * (0.5f + i * 0.12f) + i) * 9f;
            float wobbleY = (float) Math.cos(time * (0.7f + i * 0.1f) + i * 1.7f) * 4f;
            float glow = 0.5f + 0.5f * (float) Math.sin(time * 2.2f + i * 1.3f);
            int a = 30 + (int) (110 * glow);

            g.fill(Palette.alpha(Palette.WARM, a));
            g.rect(px + wobbleX, py + wobbleY, 1, 1);
            if (glow > 0.7f) {
                g.fill(Palette.alpha(Palette.WARM, (int) (a * 0.35f)));
                g.rect(px + wobbleX - 1, py + wobbleY, 3, 1);
                g.rect(px + wobbleX, py + wobbleY - 1, 1, 3);
            }
        }
    }

    private void drawSkyline(PGraphics g) {
        g.noStroke();

        // Bukit jauh.
        g.fill(0xFF090B14);
        g.beginShape();
        g.vertex(0, 128);
        g.vertex(38, 120);
        g.vertex(82, 127);
        g.vertex(128, 118);
        g.vertex(176, 127);
        g.vertex(226, 120);
        g.vertex(272, 128);
        g.vertex(320, 121);
        g.vertex(320, 142);
        g.vertex(0, 142);
        g.endShape(PConstants.CLOSE);

        // Siluet manor & kapel di kanan agar kolom teks kiri tetap bersih.
        g.fill(0xFF07080F);
        g.rect(196, 118, 46, 24);
        g.triangle(192, 118, 219, 100, 246, 118);
        g.rect(248, 112, 26, 30);
        g.triangle(244, 112, 261, 96, 278, 112);
        g.rect(280, 124, 32, 18);
        g.triangle(277, 124, 296, 112, 315, 124);

        g.fill(0xFF04050A);
        g.rect(208, 124, 4, 18);
        g.rect(226, 128, 3, 14);
        g.rect(256, 118, 3, 24);
        g.rect(266, 118, 3, 24);

        int glow = (int) (24 + 10 * Math.sin(time * 1.6f));
        g.fill(Palette.alpha(Palette.WARM, glow));
        g.rect(209, 127, 2, 4);
        g.rect(257, 122, 1, 4);
        g.rect(267, 122, 1, 4);

        g.fill(Palette.alpha(Palette.MOON, 16));
        g.rect(196, 118, 46, 1);
        g.rect(248, 112, 26, 1);
    }

    /** Bukit gelap di kanan bawah plus siluet penyihir yang duduk. */
    private void drawHillAndWitch(PGraphics g) {
        g.noStroke();
        g.fill(0xFF0A100C);
        g.beginShape();
        g.vertex(148, 180);
        g.vertex(196, 160);
        g.vertex(248, 150);
        g.vertex(300, 146);
        g.vertex(320, 147);
        g.vertex(320, 180);
        g.endShape(PConstants.CLOSE);

        g.fill(0xFF0D1510);
        g.beginShape();
        g.vertex(170, 180);
        g.vertex(222, 160);
        g.vertex(280, 152);
        g.vertex(320, 151);
        g.vertex(320, 180);
        g.endShape(PConstants.CLOSE);

        // Penyihir kecil duduk menghadap lembah.
        int wx = 262;
        int wy = 148;
        g.fill(0xFF05070A);
        g.rect(wx - 3, wy - 6, 6, 6);
        g.rect(wx - 4, wy - 2, 9, 3);
        g.rect(wx - 2, wy - 12, 4, 6);
        g.rect(wx - 4, wy - 9, 8, 1);
        g.rect(wx - 3, wy - 13, 5, 1);

        // Pita merah yang berkibar (aksen warna dari referensi).
        int wave = (int) (Math.sin(time * 2.2f) * 1.5f);
        g.fill(0xFF8E2B33);
        g.rect(wx + 1, wy - 11, 5, 1);
        g.rect(wx + 5, wy - 12 + wave, 4, 1);
        g.fill(0xFFC0454F);
        g.rect(wx + 1, wy - 11, 2, 1);
    }

    private void drawFog(PGraphics g) {
        g.noStroke();

        // Lapisan kabut tipis yang bergeser di kaki langit.
        for (int i = 0; i < 8; i++) {
            float drift = (float) Math.sin(time * 0.18f + i * 0.9f) * 24f;
            int a = 4 + (i % 3);
            g.fill(Palette.alpha(Palette.FOG, a));
            g.rect(drift + i * 9 - 40, 130 + i * 2, 170, 1);
            g.rect(180 - drift + i * 5, 136 + i * 2, 140, 1);
        }

        // Gumpalan kabut lembut yang mengalir di lembah kanan bawah.
        for (int i = 0; i < 4; i++) {
            float x = wrap(time * (2.5f + i * 0.7f) + i * 80f, 150, 470);
            g.fill(Palette.alpha(Palette.FOG, 5));
            g.ellipse(x, 136 + i * 2, 90, 8);
        }
    }

    private void drawMotes(PGraphics g) {
        g.noStroke();
        for (int i = 0; i < MOTE_COUNT; i++) {
            float x = moteBaseX[i] + (float) Math.sin(time * 0.55f + motePhase[i]) * 6f;
            int alpha = 32 + (int) (46 * (0.5f + 0.5f * Math.sin(time * 1.3f + motePhase[i])));
            g.fill(Palette.alpha(Palette.FOG, alpha));
            g.rect(x, moteBaseY[i], (i % 3 == 0) ? 2 : 1, 1);
        }
    }

    // --- Kolom kiri: judul, pengantar, kolom nama ---

    private void drawNameColumn(PGraphics g) {
        g.noStroke();

        // Garis aksen merah di atas judul.
        g.fill(0xFF9E2F3A);
        g.rect(COL_X, 16, 46, 2);
        g.fill(0xFFC0454F);
        g.rect(COL_X, 16, 20, 1);

        // Judul satu baris: "SILENT WITCH" (tanpa pemisah baris).
        PixelUI.textSpaced(g, "SILENT WITCH", COL_X - 1, 20, 3, 1, Palette.TEXT);

        PixelUI.textSpaced(g, "SEBUAH MISTERI GOTHIC", COL_X, 50, 2, Palette.ACCENT_DIM);

        PixelUI.textSpaced(g, "KABUT MENYELIMUTI AULA TUA.", COL_X, 62, 1, Palette.TEXT_DIM);
        PixelUI.textSpaced(g, "EMPAT BUKTI MENUNJUK SATU KEBENARAN,", COL_X, 71, 1, Palette.TEXT_DIM);
        PixelUI.textSpaced(g, "DAN SEORANG PENYIHIR TAK BERSUARA.", COL_X, 80, 1, Palette.TEXT_DIM);

        boolean focused = nameEditing;
        int fieldFill = Palette.alpha(0x0B0D16, focused ? 210 : 160);
        int fieldBorder = focused ? Palette.ACCENT : Palette.BORDER;
        PixelUI.pill(g, COL_X, NAME_Y, NAME_W, NAME_H, fieldFill, fieldBorder);

        String shown = (nameInput == null) ? "" : nameInput;
        boolean empty = shown.isEmpty();
        boolean blink = ((int) (time * 2f) & 1) == 0;
        float textY = NAME_Y + (NAME_H - 7) / 2f;

        if (empty) {
            // Placeholder hilang saat kolom diklik, diganti kursor.
            if (focused) {
                PixelUI.text(g, blink ? "_" : "", COL_X + 8, textY, Palette.ACCENT);
            } else {
                PixelUI.textSpaced(g, "SIAPA NAMAMU?", COL_X + 8, textY, 1, Palette.TEXT_MUTED);
            }
        } else {
            PixelUI.textSpaced(g, shown + (focused && blink ? "_" : ""), COL_X + 8, textY,
                    1, Palette.TEXT);
        }

        PixelUI.textSpaced(g, "NAMA INI AKAN DISEBUT PARA PENGHUNI.", COL_X, 110, 1,
                Palette.TEXT_MUTED);

        if (noticeTime > 0f) {
            PixelUI.text(g, notice, COL_X, 173, Palette.DANGER);
        }
    }

    private void drawMenu(PGraphics g) {
        // Tombol utama (pil terang) dan sekunder (pil transparan).
        drawPillButton(g, COL_X, BTN_PRIMARY_Y, BTN_W, BTN_PRIMARY_H, ITEMS[0],
                selected == 0, true, true);
        drawPillButton(g, COL_X, BTN_SECOND_Y, BTN_W, BTN_SECOND_H, ITEMS[1],
                selected == 1, canContinue, false);

        if (selected == CONTINUE_INDEX) {
            String status;
            int color;
            if (canContinue) {
                status = "LANJUTKAN DARI SIMPANAN TERAKHIR";
                color = Palette.TEXT_MUTED;
            } else if (saveErrorMessage != null) {
                status = "DATA SAVE RUSAK (FILE ASLI AMAN)";
                color = Palette.DANGER;
            } else {
                status = "BELUM ADA DATA TERSIMPAN";
                color = Palette.TEXT_MUTED;
            }
            PixelUI.textSpaced(g, status, COL_X, BTN_SECOND_Y + BTN_SECOND_H + 4, 1, color);
        }
    }

    private void drawPillButton(PGraphics g, float x, float y, float w, float h,
                                String label, boolean isSelected, boolean enabled,
                                boolean primary) {
        boolean hot = isSelected || hovered == (primary ? 0 : 1);

        if (!enabled) {
            PixelUI.pill(g, x, y, w, h, Palette.alpha(0x0D0F18, 150), Palette.BORDER_DIM);
            PixelUI.textSpaced(g, label, x + 12, y + (h - 7) / 2f, 1, Palette.TEXT_MUTED);
            return;
        }

        if (primary) {
            int fill = hot ? 0xFFF3EFE0 : Palette.MOON;
            PixelUI.pill(g, x, y, w, h, fill, 0xFFB9B3A0);
            PixelUI.textSpaced(g, label, x + 12, y + (h - 7) / 2f, 1, Palette.INK);
            // Penanda pilihan: garis aksen kiri.
            if (isSelected) {
                g.noStroke();
                g.fill(Palette.ACCENT_DIM);
                g.rect(x + 4, y + 3, 2, h - 6);
            }
        } else {
            int fill = hot ? Palette.alpha(0x2A2740, 210) : Palette.alpha(0x151726, 190);
            PixelUI.pill(g, x, y, w, h, fill, isSelected ? Palette.ACCENT : Palette.BORDER);
            PixelUI.textSpaced(g, label, x + 12, y + (h - 7) / 2f, 1,
                    isSelected ? Palette.ACCENT : Palette.TEXT);
        }
    }

    private void drawQuitPill(PGraphics g) {
        PixelUI.pill(g, QUIT_X, QUIT_Y, QUIT_W, QUIT_H,
                hoverQuit ? Palette.alpha(0x2A2740, 220) : Palette.alpha(0x11131F, 170),
                hoverQuit ? Palette.DANGER : Palette.BORDER_DIM);
        PixelUI.textSpaced(g, "KELUAR", QUIT_X + 5, QUIT_Y + 3, 1,
                hoverQuit ? Palette.DANGER : Palette.TEXT_MUTED);
    }

    // --- Panel konfirmasi ---

    private void drawConfirmNewGame(PGraphics g) {
        PixelUI.dim(g, 210);
        int px = 20;
        int py = 46;
        int pw = 280;
        int ph = 88;

        PixelUI.panel(g, px, py, pw, ph);

        PixelUI.centered(g, "MULAI PERJALANAN BARU", 160, py + 8, Palette.ACCENT);
        PixelUI.divider(g, 160, py + 22, pw - 24);

        PixelUI.centered(g, "DATA INVESTIGASI LAMA AKAN DITIMPA.", 160, py + 30, Palette.TEXT);
        PixelUI.centered(g, "MULAI DARI AWAL SEBAGAI " + effectiveName() + "?", 160, py + 42,
                Palette.TEXT_DIM);

        boolean hov0 = (hoveredConfirm == 0);
        boolean hov1 = (hoveredConfirm == 1);
        PixelUI.button(g, 28, py + 60, 126, 14, "[ENTER] YA, MULAI", hov0, true);
        PixelUI.button(g, 166, py + 60, 126, 14, "[ESC] BATAL", hov1, true);
    }

    // --- Input ---

    @Override
    public void keyPressed(char key, int keyCode) {
        if (confirmNewGameOpen) {
            if (isEscape(key, keyCode)) {
                confirmNewGameOpen = false;
                hoveredConfirm = -1;
                return;
            }
            if (isConfirm(key, keyCode)) {
                confirmNewGameOpen = false;
                startJourney();
                return;
            }
            return;
        }

        if (nameEditing) {
            handleNameKey(key, keyCode);
            return;
        }

        if (keyCode == PConstants.UP || key == 'w' || key == 'W') {
            moveSelection(-1);
        } else if (keyCode == PConstants.DOWN || key == 's' || key == 'S') {
            moveSelection(1);
        } else if (isConfirm(key, keyCode)) {
            activate(selected);
        } else if (isEscape(key, keyCode)) {
            notice = "";
            noticeTime = 0f;
        }
    }

    private void handleNameKey(char key, int keyCode) {
        // Spasi tetap boleh diketik di nama, jadi konfirmasi hanya Enter.
        if (key == '\n' || key == '\r' || keyCode == 10 || keyCode == 13) {
            nameEditing = false;
            return;
        }
        if (isEscape(key, keyCode)) {
            nameEditing = false;
            return;
        }
        if (keyCode == PConstants.BACKSPACE || key == '\b') {
            if (!nameInput.isEmpty()) {
                nameInput = nameInput.substring(0, nameInput.length() - 1);
            }
            return;
        }
        if (keyCode == PConstants.DELETE) {
            nameInput = "";
            return;
        }
        if (key >= 32 && key != 127) {
            char c = key;
            if ((Character.isLetterOrDigit(c) || c == ' ' || c == '-')
                    && nameInput.length() < MAX_NAME_LEN) {
                nameInput = nameInput + c;
            }
        }
    }

    private void moveSelection(int delta) {
        selected = (selected + delta + ITEMS.length) % ITEMS.length;
        hovered = -1;
        noticeTime = 0f;
    }

    private void startJourney() {
        if (game == null) {
            return;
        }
        game.setPlayerName(effectiveName());
        game.startNewGame();
    }

    private String effectiveName() {
        if (nameInput == null || nameInput.trim().isEmpty()) {
            return GameManager.HERO_NAME_DEFAULT;
        }
        String clean = nameInput.trim();
        return clean.length() > MAX_NAME_LEN ? clean.substring(0, MAX_NAME_LEN) : clean;
    }

    private void activate(int index) {
        switch (index) {
            case 0:
                if (canContinue) {
                    confirmNewGameOpen = true;
                    hoveredConfirm = 0;
                    nameEditing = false;
                } else {
                    startJourney();
                }
                break;
            case 1:
                if (canContinue) {
                    game.continueGame();
                } else if (saveErrorMessage != null) {
                    notice = "FILE SAVE RUSAK: " + saveErrorMessage;
                    noticeTime = 5.0f;
                } else {
                    notice = "BELUM ADA DATA INVESTIGASI TERSIMPAN.";
                    noticeTime = 3.5f;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseMoved(float x, float y) {
        if (confirmNewGameOpen) {
            hoveredConfirm = confirmButtonAt(x, y);
            return;
        }

        hovered = itemAt(x, y);
        hoverQuit = quitAt(x, y);
    }

    @Override
    public void mousePressed(float x, float y) {
        if (confirmNewGameOpen) {
            int btn = confirmButtonAt(x, y);
            if (btn == 0) {
                confirmNewGameOpen = false;
                startJourney();
                return;
            }
            if (btn == 1) {
                confirmNewGameOpen = false;
                return;
            }
            if (x < 20 || x > 300 || y < 46 || y > 134) {
                confirmNewGameOpen = false;
            }
            return;
        }

        // Klik kolom nama.
        if (x >= COL_X && x < COL_X + NAME_W && y >= NAME_Y && y < NAME_Y + NAME_H) {
            nameEditing = true;
            return;
        }
        nameEditing = false;

        if (quitAt(x, y)) {
            game.quit();
            return;
        }

        int clicked = itemAt(x, y);
        if (clicked >= 0) {
            selected = clicked;
            hovered = clicked;
            activate(clicked);
        }
    }

    private int itemAt(float x, float y) {
        if (x < COL_X || x >= COL_X + BTN_W) {
            return -1;
        }
        if (y >= BTN_PRIMARY_Y && y < BTN_PRIMARY_Y + BTN_PRIMARY_H) {
            return 0;
        }
        if (y >= BTN_SECOND_Y && y < BTN_SECOND_Y + BTN_SECOND_H) {
            return 1;
        }
        return -1;
    }

    private boolean quitAt(float x, float y) {
        return x >= QUIT_X && x < QUIT_X + QUIT_W
                && y >= QUIT_Y && y < QUIT_Y + QUIT_H;
    }

    private static int confirmButtonAt(float x, float y) {
        int py = 46;
        if (y < py + 60 || y > py + 74) {
            return -1;
        }
        if (x >= 28 && x <= 28 + 126) {
            return 0;
        }
        if (x >= 166 && x <= 166 + 126) {
            return 1;
        }
        return -1;
    }

    private static boolean isConfirm(char key, int keyCode) {
        return key == '\n' || key == '\r' || key == ' '
                || keyCode == 10 || keyCode == 13;
    }

    private static boolean isEscape(char key, int keyCode) {
        return key == 27 || keyCode == 27;
    }
}
