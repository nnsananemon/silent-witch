import processing.core.PGraphics;

/** Panel, tombol, dan teks bergaya pixel-art yang dipakai lintas scene. */
public final class PixelUI {

    private PixelUI() {
        // Kelas utilitas.
    }

    /** Panel gelap dengan border pixel ganda. */
    public static void panel(PGraphics g, float x, float y, float w, float h) {
        g.noStroke();
        g.fill(Palette.PANEL);
        g.rect(x, y, w, h);

        g.fill(Palette.PANEL_DARK);
        g.rect(x + 2, y + 2, w - 4, h - 4);

        g.fill(Palette.BORDER);
        g.rect(x, y, w, 1);
        g.rect(x, y + h - 1, w, 1);
        g.rect(x, y, 1, h);
        g.rect(x + w - 1, y, 1, h);

        g.fill(Palette.BORDER_DIM);
        g.rect(x + 3, y + 3, w - 6, 1);
        g.rect(x + 3, y + h - 4, w - 6, 1);
        g.rect(x + 3, y + 3, 1, h - 6);
        g.rect(x + w - 4, y + 3, 1, h - 6);

        // Aksen pixel di setiap sudut.
        g.fill(Palette.ACCENT_DIM);
        g.rect(x + 3, y + 3, 3, 1);
        g.rect(x + 3, y + 3, 1, 3);
        g.rect(x + w - 6, y + 3, 3, 1);
        g.rect(x + w - 4, y + 3, 1, 3);
        g.rect(x + 3, y + h - 4, 3, 1);
        g.rect(x + 3, y + h - 6, 1, 3);
        g.rect(x + w - 6, y + h - 4, 3, 1);
        g.rect(x + w - 4, y + h - 6, 1, 3);
    }

    /**
     * Tombol sederhana dengan label terpusat.
     *
     * @param enabled tombol nonaktif tetap tampil, tetapi memakai label redup
     *                dan tetap boleh menerima klik untuk memberi umpan balik.
     */
    public static void button(PGraphics g, float x, float y, float w, float h,
                              String label, boolean selected, boolean enabled) {
        g.noStroke();
        g.fill(selected ? Palette.PANEL_LIGHT : Palette.PANEL_DARK);
        g.rect(x, y, w, h);

        int border = selected ? Palette.ACCENT : Palette.BORDER_DIM;
        g.fill(border);
        g.rect(x, y, w, 1);
        g.rect(x, y + h - 1, w, 1);
        g.rect(x, y, 1, h);
        g.rect(x + w - 1, y, 1, h);

        if (selected) {
            g.fill(Palette.ACCENT);
            g.rect(x, y, 2, h);
        }

        int color = !enabled ? Palette.TEXT_MUTED
                : (selected ? Palette.ACCENT : Palette.TEXT);
        int textY = Math.round(y + (h - PixelFont.GLYPH_H) / 2f);
        PixelFont.drawCentered(g, label, x + w / 2f + (selected ? 1 : 0), textY, 1, color);
    }

    public static void text(PGraphics g, String text, float x, float y, int color) {
        PixelFont.draw(g, text, x, y, 1, color);
    }

    public static void text(PGraphics g, String text, float x, float y, int scale, int color) {
        PixelFont.draw(g, text, x, y, scale, color);
    }

    public static void centered(PGraphics g, String text, float cx, float y,
                                int color) {
        PixelFont.drawCentered(g, text, cx, y, 1, color);
    }

    public static void centered(PGraphics g, String text, float cx, float y,
                                int scale, int color) {
        PixelFont.drawCentered(g, text, cx, y, scale, color);
    }

    public static void shadowedCentered(PGraphics g, String text, float cx, float y,
                                        int scale, int shadowColor, int color) {
        PixelFont.drawCentered(g, text, cx + scale, y + scale, scale, shadowColor);
        PixelFont.drawCentered(g, text, cx, y, scale, color);
    }

    /** Garis horizontal dengan ornamen berlian di tengah. */
    public static void divider(PGraphics g, float cx, float y, float width) {
        g.noStroke();
        g.fill(Palette.BORDER_DIM);
        g.rect(cx - width / 2f, y, width / 2f - 4, 1);
        g.rect(cx + 4, y, width / 2f - 4, 1);

        g.fill(Palette.ACCENT_DIM);
        g.rect(cx - 2, y - 1, 4, 3);
        g.fill(Palette.ACCENT);
        g.rect(cx - 1, y, 2, 1);
    }

    /** Overlay gelap untuk panel modal. */
    public static void dim(PGraphics g, int alpha) {
        g.noStroke();
        g.fill(Palette.alpha(0x000000, alpha));
        g.rect(0, 0, GameManager.VIRTUAL_W, GameManager.VIRTUAL_H);
    }

    /** Vignette tipis di tepi layar. */
    public static void vignette(PGraphics g) {
        g.noStroke();
        for (int i = 0; i < 10; i++) {
            g.fill(Palette.alpha(0x000000, 9));
            g.rect(0, i, GameManager.VIRTUAL_W, 1);
            g.rect(0, GameManager.VIRTUAL_H - 1 - i, GameManager.VIRTUAL_W, 1);
            g.rect(i, 0, 1, GameManager.VIRTUAL_H);
            g.rect(GameManager.VIRTUAL_W - 1 - i, 0, 1, GameManager.VIRTUAL_H);
        }
    }
}
