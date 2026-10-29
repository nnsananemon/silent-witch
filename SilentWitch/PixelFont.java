import processing.core.PGraphics;

import java.util.HashMap;
import java.util.Map;

/**
 * Font bitmap 5x7 bawaan proyek.
 *
 * Tidak memakai font sistem atau aset eksternal agar hasil teks tetap tajam
 * pada resolusi internal 320x180. Huruf kecil dipetakan ke huruf kapital.
 */
public final class PixelFont {

    public static final int GLYPH_W = 5;
    public static final int GLYPH_H = 7;
    public static final int TRACKING = 1;
    public static final int LINE_GAP = 3;

    private static final Map<Character, String[]> GLYPHS = new HashMap<Character, String[]>();

    private PixelFont() {
        // Kelas utilitas.
    }

    private static void glyph(char c, String r0, String r1, String r2, String r3,
                              String r4, String r5, String r6) {
        GLYPHS.put(c, new String[]{r0, r1, r2, r3, r4, r5, r6});
    }

    static {
        glyph('A', "01110", "10001", "10001", "11111", "10001", "10001", "10001");
        glyph('B', "11110", "10001", "10001", "11110", "10001", "10001", "11110");
        glyph('C', "01110", "10001", "10000", "10000", "10000", "10001", "01110");
        glyph('D', "11110", "10001", "10001", "10001", "10001", "10001", "11110");
        glyph('E', "11111", "10000", "10000", "11110", "10000", "10000", "11111");
        glyph('F', "11111", "10000", "10000", "11110", "10000", "10000", "10000");
        glyph('G', "01110", "10001", "10000", "10111", "10001", "10001", "01111");
        glyph('H', "10001", "10001", "10001", "11111", "10001", "10001", "10001");
        glyph('I', "11111", "00100", "00100", "00100", "00100", "00100", "11111");
        glyph('J', "00111", "00010", "00010", "00010", "00010", "10010", "01100");
        glyph('K', "10001", "10010", "10100", "11000", "10100", "10010", "10001");
        glyph('L', "10000", "10000", "10000", "10000", "10000", "10000", "11111");
        glyph('M', "10001", "11011", "10101", "10101", "10001", "10001", "10001");
        glyph('N', "10001", "11001", "10101", "10011", "10001", "10001", "10001");
        glyph('O', "01110", "10001", "10001", "10001", "10001", "10001", "01110");
        glyph('P', "11110", "10001", "10001", "11110", "10000", "10000", "10000");
        glyph('Q', "01110", "10001", "10001", "10001", "10101", "10010", "01101");
        glyph('R', "11110", "10001", "10001", "11110", "10100", "10010", "10001");
        glyph('S', "01111", "10000", "10000", "01110", "00001", "00001", "11110");
        glyph('T', "11111", "00100", "00100", "00100", "00100", "00100", "00100");
        glyph('U', "10001", "10001", "10001", "10001", "10001", "10001", "01110");
        glyph('V', "10001", "10001", "10001", "10001", "10001", "01010", "00100");
        glyph('W', "10001", "10001", "10001", "10101", "10101", "10101", "01010");
        glyph('X', "10001", "10001", "01010", "00100", "01010", "10001", "10001");
        glyph('Y', "10001", "10001", "01010", "00100", "00100", "00100", "00100");
        glyph('Z', "11111", "00001", "00010", "00100", "01000", "10000", "11111");

        glyph('0', "01110", "10001", "10011", "10101", "11001", "10001", "01110");
        glyph('1', "00100", "01100", "00100", "00100", "00100", "00100", "01110");
        glyph('2', "01110", "10001", "00001", "00010", "00100", "01000", "11111");
        glyph('3', "11111", "00010", "00100", "00010", "00001", "10001", "01110");
        glyph('4', "00010", "00110", "01010", "10010", "11111", "00010", "00010");
        glyph('5', "11111", "10000", "11110", "00001", "00001", "10001", "01110");
        glyph('6', "00110", "01000", "10000", "11110", "10001", "10001", "01110");
        glyph('7', "11111", "00001", "00010", "00100", "01000", "01000", "01000");
        glyph('8', "01110", "10001", "10001", "01110", "10001", "10001", "01110");
        glyph('9', "01110", "10001", "10001", "01111", "00001", "00010", "01100");

        glyph(' ', "00000", "00000", "00000", "00000", "00000", "00000", "00000");
        glyph('.', "00000", "00000", "00000", "00000", "00000", "01100", "01100");
        glyph(',', "00000", "00000", "00000", "00000", "01100", "01100", "00100");
        glyph(':', "00000", "01100", "01100", "00000", "01100", "01100", "00000");
        glyph(';', "00000", "01100", "01100", "00000", "01100", "01100", "00100");
        glyph('!', "00100", "00100", "00100", "00100", "00100", "00000", "00100");
        glyph('?', "01110", "10001", "00001", "00010", "00100", "00000", "00100");
        glyph('\'', "00100", "00100", "00000", "00000", "00000", "00000", "00000");
        glyph('"', "01010", "01010", "00000", "00000", "00000", "00000", "00000");
        glyph('-', "00000", "00000", "00000", "11111", "00000", "00000", "00000");
        glyph('_', "00000", "00000", "00000", "00000", "00000", "00000", "11111");
        glyph('/', "00001", "00010", "00010", "00100", "01000", "01000", "10000");
        glyph('\\', "10000", "01000", "01000", "00100", "00010", "00010", "00001");
        glyph('(', "00010", "00100", "01000", "01000", "01000", "00100", "00010");
        glyph(')', "01000", "00100", "00010", "00010", "00010", "00100", "01000");
        glyph('[', "01110", "01000", "01000", "01000", "01000", "01000", "01110");
        glyph(']', "01110", "00010", "00010", "00010", "00010", "00010", "01110");
        glyph('+', "00000", "00100", "00100", "11111", "00100", "00100", "00000");
        glyph('=', "00000", "00000", "11111", "00000", "11111", "00000", "00000");
        glyph('<', "00010", "00100", "01000", "10000", "01000", "00100", "00010");
        glyph('>', "01000", "00100", "00010", "00001", "00010", "00100", "01000");
        glyph('%', "11001", "11010", "00010", "00100", "01000", "01011", "10011");
        glyph('*', "00000", "10101", "01110", "11111", "01110", "10101", "00000");
        glyph('#', "01010", "01010", "11111", "01010", "11111", "01010", "01010");
        glyph('&', "01100", "10010", "10100", "01000", "10101", "10010", "01101");
        glyph('|', "00100", "00100", "00100", "00100", "00100", "00100", "00100");
        glyph('\u00b0', "01110", "01010", "01110", "00000", "00000", "00000", "00000");
    }

    private static char normalize(char c) {
        if (c >= 'a' && c <= 'z') {
            return Character.toUpperCase(c);
        }
        switch (c) {
            case '\u2014':
            case '\u2013':
                return '-';
            case '\u2019':
                return '\'';
            case '\u201c':
            case '\u201d':
                return '"';
            case '\u00a0':
                return ' ';
            default:
                return c;
        }
    }

    /** Lebar satu baris teks dalam piksel virtual. */
    public static int measureLine(String text, int scale) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        scale = Math.max(1, scale);
        int chars = text.length();
        return chars * (GLYPH_W + TRACKING) * scale - TRACKING * scale;
    }

    /** Lebar baris terpanjang pada teks yang boleh memiliki newline. */
    public static int measure(String text, int scale) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        int widest = 0;
        for (String line : text.split("\n", -1)) {
            widest = Math.max(widest, measureLine(line, scale));
        }
        return widest;
    }

    public static int lineHeight(int scale) {
        return (GLYPH_H + LINE_GAP) * Math.max(1, scale);
    }

    public static void draw(PGraphics g, String text, float x, float y, int scale, int color) {
        if (text == null || text.isEmpty()) {
            return;
        }
        scale = Math.max(1, scale);
        g.noStroke();
        g.fill(color);

        float originX = Math.round(x);
        float cx = originX;
        float cy = Math.round(y);
        int line = 0;

        for (int i = 0; i < text.length(); i++) {
            char raw = text.charAt(i);
            if (raw == '\n') {
                line++;
                cx = originX;
                continue;
            }
            char c = normalize(raw);
            if (c == ' ') {
                cx += (GLYPH_W + TRACKING) * scale;
                continue;
            }
            String[] rows = GLYPHS.get(c);
            if (rows == null) {
                rows = GLYPHS.get('?');
            }
            for (int row = 0; row < GLYPH_H; row++) {
                String bits = rows[row];
                for (int col = 0; col < GLYPH_W; col++) {
                    if (bits.charAt(col) == '1') {
                        g.rect(cx + col * scale,
                                cy + (line * (GLYPH_H + LINE_GAP) + row) * scale,
                                scale, scale);
                    }
                }
            }
            cx += (GLYPH_W + TRACKING) * scale;
        }
    }

    /** Menggambar teks yang setiap barisnya dipusatkan pada cx. */
    public static void drawCentered(PGraphics g, String text, float cx, float y,
                                    int scale, int color) {
        if (text == null) {
            return;
        }
        int line = 0;
        for (String part : text.split("\n", -1)) {
            int w = measureLine(part, scale);
            draw(g, part, Math.round(cx - w / 2f), y + line * lineHeight(scale), scale, color);
            line++;
        }
    }

    public static void drawRight(PGraphics g, String text, float rightX, float y,
                                 int scale, int color) {
        draw(g, text, rightX - measureLine(text, scale), y, scale, color);
    }

    /**
     * Menggambar teks dengan pembungkus kata. Mengembalikan posisi y setelah
     * baris terakhir (tanpa jarak tambahan di bawahnya).
     */
    public static float drawWrapped(PGraphics g, String text, float x, float y,
                                    float maxWidth, int scale, int color) {
        if (text == null || text.isEmpty()) {
            return y;
        }
        scale = Math.max(1, scale);
        float cy = y;
        String[] paragraphs = text.split("\n", -1);

        for (String paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                cy += lineHeight(scale);
                continue;
            }
            String[] words = paragraph.split(" ");
            StringBuilder line = new StringBuilder();
            for (String word : words) {
                String candidate = line.length() == 0 ? word : line + " " + word;
                if (line.length() > 0 && measureLine(candidate, scale) > maxWidth) {
                    draw(g, line.toString(), x, cy, scale, color);
                    cy += lineHeight(scale);
                    line.setLength(0);
                    line.append(word);
                } else {
                    line.setLength(0);
                    line.append(candidate);
                }
            }
            if (line.length() > 0) {
                draw(g, line.toString(), x, cy, scale, color);
                cy += lineHeight(scale);
            }
        }
        return cy - lineHeight(scale);
    }
}
