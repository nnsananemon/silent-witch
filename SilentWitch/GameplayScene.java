import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Scene gameplay placeholder untuk Stage 01.
 *
 * Belum ada pemain, collision, clue, quest, atau puzzle. Scene ini hanya
 * membuktikan perpindahan scene, render pixel, dan pemisahan input dari menu.
 */
public final class GameplayScene extends Scene {

    private static final int BACK_X = 112;
    private static final int BACK_Y = 150;
    private static final int BACK_W = 96;
    private static final int BACK_H = 14;

    private static final int FLOOR_Y = 112;
    private static final int DUST_COUNT = 14;

    private static final int[] BOOK_COLORS = {
            0xFF6B4B4B,
            0xFF4C5A6B,
            0xFF5C4E6B,
            0xFF6B6042,
            0xFF465A52,
            0xFF5A4657
    };

    private final float[] dustX = new float[DUST_COUNT];
    private final float[] dustY = new float[DUST_COUNT];
    private final float[] dustSpeed = new float[DUST_COUNT];
    private final float[] dustPhase = new float[DUST_COUNT];

    private float time = 0f;
    private boolean hoverBack = false;

    public GameplayScene(GameManager game) {
        super(game);
        for (int i = 0; i < DUST_COUNT; i++) {
            dustX[i] = 108f + Palette.hash(i * 9 + 21) * 100f;
            dustY[i] = 60f + Palette.hash(i * 9 + 22) * 92f;
            dustSpeed[i] = 1.5f + Palette.hash(i * 9 + 23) * 4f;
            dustPhase[i] = Palette.hash(i * 9 + 24) * 6.28318f;
        }
    }

    @Override
    public void update(float dt) {
        time += dt;
        for (int i = 0; i < DUST_COUNT; i++) {
            dustY[i] -= dustSpeed[i] * dt;
            if (dustY[i] < 56f) {
                dustY[i] = 150f;
            }
        }
    }

    @Override
    public void draw(PGraphics g) {
        drawRoom(g);
        PixelUI.vignette(g);
        drawHeader(g);
        drawInfoPanel(g);
        drawBackButton(g);
    }

    private void drawRoom(PGraphics g) {
        g.noStroke();

        // Dinding batu.
        for (int ty = 0; ty < 7; ty++) {
            for (int tx = 0; tx < 20; tx++) {
                int h = (int) (Palette.hash(tx * 31 + ty * 17) * 3);
                int base = h == 0 ? Palette.STONE_1 : (h == 1 ? Palette.STONE_2 : 0xFF11121B);
                g.fill(base);
                g.rect(tx * 16, ty * 16, 16, 16);
                g.fill(0xFF0C0D14);
                g.rect(tx * 16, ty * 16 + 15, 16, 1);
                g.rect(tx * 16 + 15, ty * 16, 1, 16);
            }
        }

        // Lantai.
        for (int ty = 0; ty < 5; ty++) {
            for (int tx = 0; tx < 20; tx++) {
                int y = FLOOR_Y + ty * 16;
                int tone = (tx + ty) % 2 == 0 ? 0xFF13141C : 0xFF0F1017;
                g.fill(tone);
                g.rect(tx * 16, y, 16, 16);
                g.fill(0xFF0A0B10);
                g.rect(tx * 16, y, 16, 1);
                g.rect(tx * 16, y, 1, 16);
            }
        }

        // Papan dasar dinding.
        g.fill(Palette.WOOD);
        g.rect(0, FLOOR_Y - 3, GameManager.VIRTUAL_W, 3);
        g.fill(Palette.WOOD_LIGHT);
        g.rect(0, FLOOR_Y - 3, GameManager.VIRTUAL_W, 1);

        drawWindow(g);
        drawBookshelf(g);
        drawTable(g);
        drawRug(g);
        drawSconce(g, 76, 46);
        drawCracks(g);
        drawDust(g);
    }

    private void drawWindow(PGraphics g) {
        int x = 136;
        int y = 16;

        g.fill(0xFF0A0B12);
        g.rect(x - 4, y - 4, 56, 62);
        g.fill(0xFF2A2C38);
        g.rect(x - 3, y - 3, 54, 60);
        g.fill(0xFF0B1020);
        g.rect(x, y, 48, 54);

        g.fill(Palette.alpha(Palette.MOON, 10));
        g.ellipse(x + 24, y + 22, 26, 26);
        g.fill(Palette.alpha(Palette.MOON, 160));
        g.ellipse(x + 24, y + 22, 13, 13);

        g.fill(0xFF2A2C38);
        g.rect(x + 23, y, 2, 54);
        g.rect(x, y + 26, 48, 2);

        // Berkas cahaya bulan ke lantai.
        g.noStroke();
        g.fill(Palette.alpha(Palette.MOON, 7));
        g.beginShape();
        g.vertex(x + 2, y + 54);
        g.vertex(x + 46, y + 54);
        g.vertex(x + 84, 178);
        g.vertex(x - 34, 178);
        g.endShape(PConstants.CLOSE);

        g.fill(Palette.alpha(Palette.MOON, 5));
        g.rect(x, y + 54, 48, 3);
    }

    private void drawBookshelf(PGraphics g) {
        int x = 14;
        int y = 40;
        int w = 44;
        int h = 70;

        g.fill(0xFF0A090D);
        g.rect(x - 2, y - 2, w + 4, h + 4);
        g.fill(Palette.WOOD);
        g.rect(x, y, w, h);
        g.fill(Palette.WOOD_LIGHT);
        g.rect(x + 2, y + 2, w - 4, 2);

        for (int shelf = 0; shelf < 3; shelf++) {
            int shelfY = y + 18 + shelf * 20;
            g.fill(0xFF120D0C);
            g.rect(x + 4, shelfY, w - 8, 2);

            int bookX = x + 5;
            int bookIndex = shelf * 11;
            while (bookX < x + w - 6) {
                int bw = 2 + (int) (Palette.hash(bookIndex * 13 + 3) * 3);
                int bh = 8 + (int) (Palette.hash(bookIndex * 13 + 4) * 6);
                int color = BOOK_COLORS[(int) (Palette.hash(bookIndex * 13 + 5) * BOOK_COLORS.length)];
                g.fill(Palette.alpha(color, 200));
                g.rect(bookX, shelfY - bh, bw, bh);
                g.fill(Palette.alpha(0x000000, 70));
                g.rect(bookX + bw - 1, shelfY - bh, 1, bh);
                bookX += bw + 1;
                bookIndex++;
            }
        }
    }

    private void drawTable(PGraphics g) {
        g.fill(0xFF0A0807);
        g.rect(212, 86, 82, 6);
        g.fill(Palette.WOOD_LIGHT);
        g.rect(215, 84, 76, 4);
        g.fill(Palette.WOOD);
        g.rect(218, 88, 4, 28);
        g.rect(284, 88, 4, 28);
        g.rect(222, 104, 62, 3);

        // Kertas dan buku di atas meja.
        g.fill(0xFF9A927E);
        g.rect(228, 80, 13, 5);
        g.fill(0xFF7F7868);
        g.rect(229, 79, 11, 1);
        g.fill(0xFF4C4152);
        g.rect(252, 78, 10, 7);
        g.fill(0xFF6B6042);
        g.rect(264, 80, 8, 5);

        drawCandle(g, 242, 84);
    }

    private void drawCandle(PGraphics g, int x, int baseY) {
        float flicker = (float) Math.sin(time * 9f + x) * 1.2f;
        int flameY = (int) (baseY - 15 + flicker);

        g.noStroke();
        g.fill(Palette.alpha(Palette.WARM, 9));
        g.ellipse(x + 2, baseY - 12, 34, 30);
        g.fill(Palette.alpha(Palette.WARM, 13));
        g.ellipse(x + 2, baseY - 11, 20, 18);

        g.fill(0xFFB8A88A);
        g.rect(x, baseY - 10, 4, 10);
        g.fill(0xFFE8DCC2);
        g.rect(x, baseY - 10, 1, 10);
        g.fill(Palette.WARM);
        g.rect(x + 1, flameY, 2, 4);
        g.fill(0xFFF2D68A);
        g.rect(x + 1, flameY + 1, 1, 2);
    }

    private void drawSconce(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(Palette.alpha(Palette.WARM, 9));
        g.ellipse(x + 2, y, 30, 30);

        g.fill(0xFF211A18);
        g.rect(x - 2, y + 6, 8, 2);
        g.rect(x + 1, y + 2, 2, 5);
        g.fill(0xFFB8A88A);
        g.rect(x, y - 1, 3, 5);

        float flicker = (float) Math.sin(time * 8f + 1.7f) * 1.2f;
        int flameY = (int) (y - 5 + flicker);
        g.fill(Palette.WARM);
        g.rect(x + 1, flameY, 2, 4);
        g.fill(0xFFF2D68A);
        g.rect(x + 1, flameY + 1, 1, 2);
    }

    private void drawRug(PGraphics g) {
        g.noStroke();
        g.fill(Palette.RUG);
        g.rect(116, 130, 88, 36);
        g.fill(0xFF4A2430);
        g.rect(116, 130, 88, 1);
        g.rect(116, 165, 88, 1);
        g.rect(116, 130, 1, 36);
        g.rect(203, 130, 1, 36);

        g.fill(0xFF5C2E3A);
        g.rect(124, 138, 72, 1);
        g.rect(124, 157, 72, 1);
        g.rect(124, 138, 1, 20);
        g.rect(195, 138, 1, 20);

        g.fill(0xFF6B3947);
        g.triangle(160, 140, 174, 148, 160, 156);
        g.triangle(160, 140, 146, 148, 160, 156);
    }

    private void drawCracks(PGraphics g) {
        g.noStroke();
        g.fill(0xFF0C0D14);
        g.rect(104, 24, 1, 5);
        g.rect(105, 29, 4, 1);
        g.rect(105, 30, 1, 4);
        g.rect(206, 38, 1, 7);
        g.rect(207, 45, 3, 1);
        g.rect(310, 66, 1, 6);
    }

    private void drawDust(PGraphics g) {
        g.noStroke();
        for (int i = 0; i < DUST_COUNT; i++) {
            float x = dustX[i] + (float) Math.sin(time * 0.7f + dustPhase[i]) * 4f;
            int alpha = 18 + (int) (24 * (0.5f + 0.5f * Math.sin(time * 1.1f + dustPhase[i])));
            g.fill(Palette.alpha(Palette.MOON, alpha));
            g.rect(x, dustY[i], 1, 1);
        }
    }

    private void drawHeader(PGraphics g) {
        g.noStroke();
        g.fill(Palette.alpha(Palette.INK, 200));
        g.rect(0, 0, GameManager.VIRTUAL_W, 14);
        g.fill(Palette.BORDER_DIM);
        g.rect(0, 14, GameManager.VIRTUAL_W, 1);

        PixelUI.text(g, "THE SILENT MANOR - AREA UJI", 8, 4, Palette.TEXT_DIM);
        PixelUI.text(g, "TAHAP 01", 271, 4, Palette.TEXT_MUTED);
    }

    private void drawInfoPanel(PGraphics g) {
        PixelUI.panel(g, 48, 88, 224, 58);

        PixelUI.centered(g, "RUANG UJI - TAHAP 01", 160, 94, Palette.ACCENT);
        PixelUI.divider(g, 160, 104, 110);

        PixelUI.centered(g, "TAHAP 02 AKAN MENAMBAHKAN PEMAIN,", 160, 110,
                Palette.TEXT_DIM);
        PixelUI.centered(g, "COLLISION, DAN EKSPLORASI AREA.", 160, 120,
                Palette.TEXT_DIM);
        PixelUI.centered(g, "CLUE DAN QUEST BELUM TERSEDIA.", 160, 130,
                Palette.TEXT_MUTED);
    }

    private void drawBackButton(PGraphics g) {
        PixelUI.button(g, BACK_X, BACK_Y, BACK_W, BACK_H, "KEMBALI KE MENU",
                hoverBack, true);
        PixelUI.centered(g, "ESC", 160, 169, Palette.TEXT_MUTED);
    }

    @Override
    public void keyPressed(char key, int keyCode) {
        if (key == 27 || keyCode == 27) {
            game.showMenu();
        }
    }

    @Override
    public void mouseMoved(float x, float y) {
        hoverBack = insideBack(x, y);
    }

    @Override
    public void mousePressed(float x, float y) {
        if (insideBack(x, y)) {
            game.showMenu();
        }
    }

    private boolean insideBack(float x, float y) {
        return x >= BACK_X && x < BACK_X + BACK_W
                && y >= BACK_Y && y < BACK_Y + BACK_H;
    }
}
