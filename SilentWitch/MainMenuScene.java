import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Main Menu (diperbarui pada Stage 02).
 *
 * Menu sudah mendukung keyboard dan mouse. Tombol "Lanjutkan" sengaja tampil
 * nonaktif karena sistem save baru dijadwalkan pada Stage 06.
 */
public final class MainMenuScene extends Scene {

    private static final String[] ITEMS = {
            "MULAI BARU",
            "LANJUTKAN",
            "KONTROL",
            "KELUAR"
    };

    private static final int ITEM_X = 88;
    private static final int ITEM_Y = 84;
    private static final int ITEM_W = 144;
    private static final int ITEM_H = 13;
    private static final int ITEM_GAP = 15;

    private static final int CONTINUE_INDEX = 1;
    private static final int STAR_COUNT = 36;
    private static final int MOTE_COUNT = 18;

    private final float[] starX = new float[STAR_COUNT];
    private final float[] starY = new float[STAR_COUNT];
    private final float[] starPhase = new float[STAR_COUNT];

    private final float[] moteBaseX = new float[MOTE_COUNT];
    private final float[] moteBaseY = new float[MOTE_COUNT];
    private final float[] moteSpeed = new float[MOTE_COUNT];
    private final float[] motePhase = new float[MOTE_COUNT];

    private int selected = 0;
    private float time = 0f;
    private boolean helpOpen = false;

    private String notice = "";
    private float noticeTime = 0f;
    private int hovered = -1;

    public MainMenuScene(GameManager game) {
        super(game);

        for (int i = 0; i < STAR_COUNT; i++) {
            starX[i] = Palette.hash(i * 5 + 1) * GameManager.VIRTUAL_W;
            starY[i] = 4f + Palette.hash(i * 5 + 2) * 88f;
            starPhase[i] = Palette.hash(i * 5 + 3) * 6.28318f;
        }

        for (int i = 0; i < MOTE_COUNT; i++) {
            moteBaseX[i] = 8f + Palette.hash(i * 7 + 11) * 304f;
            moteBaseY[i] = 94f + Palette.hash(i * 7 + 12) * 84f;
            moteSpeed[i] = 2.5f + Palette.hash(i * 7 + 13) * 6f;
            motePhase[i] = Palette.hash(i * 7 + 14) * 6.28318f;
        }
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
        drawTitle(g);
        drawMenu(g);
        drawFooter(g);

        if (helpOpen) {
            drawHelp(g);
        }
    }

    private void drawBackdrop(PGraphics g) {
        g.noStroke();

        for (int y = 0; y < 124; y++) {
            float t = y / 124f;
            g.fill(Palette.mix(Palette.NIGHT_TOP, Palette.NIGHT_BOTTOM, t));
            g.rect(0, y, GameManager.VIRTUAL_W, 1);
        }

        for (int i = 0; i < STAR_COUNT; i++) {
            int twinkle = (int) (90 + 80 * Math.sin(time * 1.4f + starPhase[i]));
            g.fill(Palette.alpha(Palette.MOON, Math.max(20, twinkle)));
            g.rect(starX[i], starY[i], 1, 1);
        }

        drawMoon(g);
        drawSkyline(g);

        g.fill(0xFF090A10);
        g.rect(0, 122, GameManager.VIRTUAL_W, 58);

        drawFog(g);
        drawMotes(g);
    }

    private void drawMoon(PGraphics g) {
        float cx = 274f;
        float cy = 48f;

        g.noStroke();
        g.fill(Palette.alpha(Palette.MOON, 7));
        g.ellipse(cx, cy, 50, 50);
        g.fill(Palette.alpha(Palette.MOON, 11));
        g.ellipse(cx, cy, 38, 38);
        g.fill(Palette.MOON);
        g.ellipse(cx, cy, 24, 24);

        g.fill(0xFFD2CDBA);
        g.rect(cx - 6, cy - 5, 3, 2);
        g.rect(cx + 2, cy - 2, 4, 3);
        g.rect(cx - 3, cy + 4, 2, 2);
        g.rect(cx + 5, cy + 6, 2, 1);
    }

    private void drawSkyline(PGraphics g) {
        g.noStroke();

        // Bukit jauh.
        g.fill(0xFF0B0D17);
        g.beginShape();
        g.vertex(0, 112);
        g.vertex(34, 103);
        g.vertex(70, 110);
        g.vertex(112, 101);
        g.vertex(156, 111);
        g.vertex(204, 104);
        g.vertex(250, 112);
        g.vertex(320, 105);
        g.vertex(320, 124);
        g.vertex(0, 124);
        g.endShape(PConstants.CLOSE);

        // Atap-atap desa di kiri.
        g.fill(0xFF0A0B12);
        g.rect(8, 112, 28, 12);
        g.triangle(4, 112, 22, 99, 40, 112);
        g.rect(48, 115, 22, 9);
        g.triangle(44, 115, 59, 106, 74, 115);
        g.rect(86, 113, 18, 11);
        g.triangle(82, 113, 95, 105, 108, 113);

        // Jendela hangat yang samar.
        int flicker = (int) (18 + 8 * Math.sin(time * 2.1f));
        g.fill(Palette.alpha(Palette.WARM, flicker));
        g.rect(18, 116, 3, 4);
        g.rect(54, 118, 3, 3);
        g.rect(91, 117, 2, 4);

        // Chapel of Whispers di kejauhan.
        g.fill(0xFF08090F);
        g.rect(230, 86, 58, 36);
        g.triangle(224, 86, 259, 50, 294, 86);
        g.rect(256, 40, 2, 10);
        g.rect(252, 43, 10, 2);
        g.rect(288, 102, 24, 20);
        g.triangle(285, 102, 300, 92, 315, 102);

        int windowGlow = (int) (30 + 14 * Math.sin(time * 1.7f + 1.2f));
        g.fill(Palette.alpha(Palette.WARM, windowGlow));
        g.rect(244, 96, 4, 8);
        g.rect(268, 96, 4, 8);
        g.rect(298, 108, 3, 5);

        g.fill(Palette.alpha(Palette.MOON, 20));
        g.rect(230, 86, 58, 1);
    }

    private void drawFog(PGraphics g) {
        g.noStroke();
        for (int i = 0; i < 9; i++) {
            float drift = (float) Math.sin(time * 0.18f + i * 0.9f) * 16f;
            g.fill(Palette.alpha(Palette.FOG, 5 + i % 3));
            g.rect(drift + i * 9 - 40, 116 + i * 3, 150, 1);
            g.rect(180 - drift + i * 5, 121 + i * 2, 130, 1);
        }
    }

    private void drawMotes(PGraphics g) {
        g.noStroke();
        for (int i = 0; i < MOTE_COUNT; i++) {
            float x = moteBaseX[i] + (float) Math.sin(time * 0.55f + motePhase[i]) * 5f;
            int alpha = 22 + (int) (18 * (0.5f + 0.5f * Math.sin(time * 1.3f + motePhase[i])));
            g.fill(Palette.alpha(Palette.FOG, alpha));
            g.rect(x, moteBaseY[i], 1, 1);
        }
    }

    private void drawTitle(PGraphics g) {
        PixelUI.shadowedCentered(g, "SILENT WITCH", 160, 21, 3,
                Palette.alpha(Palette.MAGIC_DIM, 150), Palette.TEXT);
        PixelUI.centered(g, "SEBUAH MISTERI DI ASHEN VILLAGE", 160, 52,
                Palette.TEXT_DIM);
        PixelUI.divider(g, 160, 66, 122);
    }

    private void drawMenu(PGraphics g) {
        for (int i = 0; i < ITEMS.length; i++) {
            boolean isSelected = selected == i;
            boolean enabled = i != CONTINUE_INDEX;
            int y = ITEM_Y + i * ITEM_GAP;

            PixelUI.button(g, ITEM_X, y, ITEM_W, ITEM_H, ITEMS[i], isSelected, enabled);

            if (!isSelected && hovered == i) {
                g.noStroke();
                g.fill(Palette.BORDER);
                g.rect(ITEM_X, y, ITEM_W, 1);
                g.rect(ITEM_X, y + ITEM_H - 1, ITEM_W, 1);
                g.rect(ITEM_X, y, 1, ITEM_H);
                g.rect(ITEM_X + ITEM_W - 1, y, 1, ITEM_H);
            }

            if (isSelected) {
                PixelUI.text(g, ">", ITEM_X - 9, y + 3, Palette.ACCENT);
            }

            if (i == CONTINUE_INDEX) {
                PixelUI.text(g, "TERKUNCI", ITEM_X + ITEM_W + 5, y + 3,
                        isSelected ? Palette.TEXT_DIM : Palette.TEXT_MUTED);
            }
        }

        if (noticeTime > 0f) {
            PixelUI.centered(g, notice, 160, 146, Palette.DANGER);
        } else if (selected == CONTINUE_INDEX) {
            PixelUI.centered(g, "BELUM ADA DATA INVESTIGASI TERSIMPAN", 160, 146,
                    Palette.TEXT_MUTED);
        }
    }

    private void drawFooter(PGraphics g) {
        PixelUI.centered(g, "W/S ATAU PANAH - PILIH   ENTER - KONFIRMASI", 160, 169,
                Palette.TEXT_MUTED);
        PixelFont.drawRight(g, "TAHAP 03", 312, 6, 1, Palette.TEXT_MUTED);
    }

    private void drawHelp(PGraphics g) {
        PixelUI.dim(g, 190);
        PixelUI.panel(g, 34, 38, 252, 106);

        PixelUI.centered(g, "KONTROL", 160, 46, 2, Palette.ACCENT);
        PixelUI.divider(g, 160, 62, 100);

        PixelUI.text(g, "PANAH ATAS/BAWAH / W / S - PILIH", 50, 70, Palette.TEXT_DIM);
        PixelUI.text(g, "ENTER / SPASI / KLIK - KONFIRMASI", 50, 80, Palette.TEXT_DIM);
        PixelUI.text(g, "ESC - TUTUP PANEL INI", 50, 90, Palette.TEXT_DIM);
        PixelUI.text(g, "E / ENTER / SPASI - BICARA & PERIKSA", 50, 100, Palette.TEXT_DIM);
        PixelUI.text(g, "ESC SAAT GAMEPLAY - KEMBALI KE MENU", 50, 110, Palette.TEXT_DIM);

        PixelUI.centered(g, "GERAK: WASD / PANAH DI DALAM GAME.", 160, 122,
                Palette.TEXT_MUTED);
        PixelUI.centered(g, "ESC / ENTER - TUTUP", 160, 136, Palette.TEXT_DIM);
    }

    @Override
    public void keyPressed(char key, int keyCode) {
        if (helpOpen) {
            if (isEscape(key, keyCode) || isConfirm(key, keyCode)) {
                helpOpen = false;
            }
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

    private void moveSelection(int delta) {
        selected = (selected + delta + ITEMS.length) % ITEMS.length;
        hovered = -1;
        noticeTime = 0f;
    }

    private void activate(int index) {
        switch (index) {
            case 0:
                game.showGameplay();
                break;
            case 1:
                notice = "BELUM ADA DATA INVESTIGASI.\nSIMPAN OTOMATIS DITAMBAHKAN PADA TAHAP 06.";
                noticeTime = 4.5f;
                break;
            case 2:
                helpOpen = true;
                break;
            case 3:
                game.quit();
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseMoved(float x, float y) {
        hovered = helpOpen ? -1 : itemAt(x, y);
    }

    @Override
    public void mousePressed(float x, float y) {
        if (helpOpen) {
            helpOpen = false;
            hovered = -1;
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
        if (x < ITEM_X || x >= ITEM_X + ITEM_W) {
            return -1;
        }
        for (int i = 0; i < ITEMS.length; i++) {
            int itemY = ITEM_Y + i * ITEM_GAP;
            if (y >= itemY && y < itemY + ITEM_H) {
                return i;
            }
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
