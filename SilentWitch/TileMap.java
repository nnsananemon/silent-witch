import processing.core.PGraphics;

/**
 * Peta tile sederhana untuk Stage 02.
 *
 * Peta disimpan sebagai grid karakter agar data area mudah dibaca dan diubah.
 * Tidak ada file aset eksternal: setiap tile digambar secara prosedural dengan
 * placeholder buatan sendiri (lihat ASSET_CREDITS.md).
 *
 * Legenda karakter:
 *   '#' dinding batu (solid)      'W' dinding dengan jendela (solid)
 *   '.' lantai kayu               ',' karpet (bisa dilewati)
 *   'B' rak buku (solid)          'T' meja (solid)
 *   'P' pilar (solid)             'S' titik spawn (lantai)
 */
public final class TileMap {

    public static final int TILE = 16;

    // Aula uji The Silent Manor. Ukuran 26x16 tile (416x256 piksel), lebih
    // besar dari viewport 320x180 sehingga kamera ikut bergerak.
    private static final String[] MANOR_HALL = {
            "####WW####WW####WW########",
            "#........................#",
            "#..BBBB..........BBBB....#",
            "#..BBBB..........BBBB....#",
            "#........................#",
            "#....PP..........PP......#",
            "#........................#",
            "#....TT..........TT......#",
            "#....TT..........TT......#",
            "#........................#",
            "#.........,,,,,,.........#",
            "#.........,,,,,,.........#",
            "#.........,,,,,,.........#",
            "#........................#",
            "#...........S............#",
            "##########################"
    };

    private final char[][] tiles;
    private final int cols;
    private final int rows;
    private int spawnCol = -1;
    private int spawnRow = -1;

    /**
     * @throws IllegalArgumentException bila layout kosong, tidak berbentuk
     *                                  persegi, atau tidak memiliki spawn.
     */
    public TileMap(String[] layout) {
        if (layout == null || layout.length == 0) {
            throw new IllegalArgumentException("Layout peta kosong.");
        }

        rows = layout.length;
        cols = layout[0].length();
        if (cols == 0) {
            throw new IllegalArgumentException("Baris pertama layout peta kosong.");
        }

        tiles = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            String line = layout[r];
            if (line == null || line.length() != cols) {
                throw new IllegalArgumentException("Baris peta " + r
                        + " memiliki panjang " + (line == null ? "null" : line.length())
                        + ", seharusnya " + cols + ".");
            }
            for (int c = 0; c < cols; c++) {
                char ch = line.charAt(c);
                tiles[r][c] = ch;
                if (ch == 'S') {
                    if (spawnCol >= 0) {
                        throw new IllegalArgumentException("Layout peta memiliki lebih dari satu spawn.");
                    }
                    spawnCol = c;
                    spawnRow = r;
                }
            }
        }

        if (spawnCol < 0) {
            throw new IllegalArgumentException("Layout peta tidak memiliki spawn 'S'.");
        }
    }

    public static TileMap manorHall() {
        return new TileMap(MANOR_HALL);
    }

    public int cols() {
        return cols;
    }

    public int rows() {
        return rows;
    }

    public int pixelWidth() {
        return cols * TILE;
    }

    public int pixelHeight() {
        return rows * TILE;
    }

    public float spawnX() {
        return spawnCol * TILE + TILE / 2f;
    }

    public float spawnY() {
        return spawnRow * TILE + TILE / 2f;
    }

    public char tileAt(int col, int row) {
        if (col < 0 || col >= cols || row < 0 || row >= rows) {
            return '#';
        }
        return tiles[row][col];
    }

    public boolean isSolid(int col, int row) {
        char ch = tileAt(col, row);
        return ch == '#' || ch == 'W' || ch == 'B' || ch == 'T' || ch == 'P';
    }

    /**
     * Mengecek tabrakan antara kotak dunia dan tile solid.
     * Koordinat di luar peta dianggap solid agar pemain tidak bisa keluar area.
     */
    public boolean rectCollides(float x, float y, float w, float h) {
        if (w <= 0f || h <= 0f) {
            return true;
        }
        int c0 = (int) Math.floor(x / TILE);
        int c1 = (int) Math.floor((x + w - 0.001f) / TILE);
        int r0 = (int) Math.floor(y / TILE);
        int r1 = (int) Math.floor((y + h - 0.001f) / TILE);

        if (c0 < 0 || r0 < 0 || c1 >= cols || r1 >= rows) {
            return true;
        }
        for (int r = r0; r <= r1; r++) {
            for (int c = c0; c <= c1; c++) {
                if (isSolid(c, r)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Menggambar tile yang terlihat pada kamera. */
    public void draw(PGraphics g, int cameraX, int cameraY) {
        int firstCol = Math.max(0, cameraX / TILE);
        int firstRow = Math.max(0, cameraY / TILE);
        int lastCol = Math.min(cols - 1, (cameraX + GameManager.VIRTUAL_W) / TILE);
        int lastRow = Math.min(rows - 1, (cameraY + GameManager.VIRTUAL_H) / TILE);

        for (int r = firstRow; r <= lastRow; r++) {
            for (int c = firstCol; c <= lastCol; c++) {
                drawTile(g, tiles[r][c], c, r, cameraX, cameraY);
            }
        }
    }

    private void drawTile(PGraphics g, char ch, int col, int row, int camX, int camY) {
        int x = col * TILE - camX;
        int y = row * TILE - camY;

        switch (ch) {
            case '#':
                drawWall(g, x, y, col, row, false);
                break;
            case 'W':
                drawWall(g, x, y, col, row, true);
                break;
            case ',':
                drawRug(g, x, y, col, row);
                break;
            case 'B':
                drawFloor(g, x, y, col, row);
                drawBookshelf(g, x, y, col, row);
                break;
            case 'T':
                drawFloor(g, x, y, col, row);
                drawTable(g, x, y, col, row);
                break;
            case 'P':
                drawFloor(g, x, y, col, row);
                drawPillar(g, x, y, col, row);
                break;
            case '.':
            case 'S':
            default:
                drawFloor(g, x, y, col, row);
                break;
        }
    }

    private void drawFloor(PGraphics g, int x, int y, int col, int row) {
        // Lantai kayu dengan kontras rendah: hindari grid terang yang membuat
        // lantai terbaca seperti dinding bata.
        g.noStroke();
        g.fill(0xFF2B2226);
        g.rect(x, y, TILE, TILE);

        if (row % 3 == 0) {
            g.fill(0xFF241D21);
            g.rect(x, y, TILE, 1);
        }

        if (col % 4 == 0 && (row & 1) == 0) {
            g.fill(0xFF241D21);
            g.rect(x + 9, y + 1, 1, TILE - 1);
        }

        if ((col * 5 + row * 3) % 7 == 0) {
            g.fill(0xFF33292D);
            g.rect(x + 3, y + 6, 9, 1);
            g.rect(x + 6, y + 11, 5, 1);
        }

        // Bayangan dan papan alas di bawah dinding utara.
        if (isWall(col, row - 1)) {
            g.fill(Palette.WOOD_LIGHT);
            g.rect(x, y + 1, TILE, 2);
            g.fill(Palette.alpha(0x000000, 100));
            g.rect(x, y + 3, TILE, 2);
        }
    }

    private boolean isWall(int col, int row) {
        char ch = tileAt(col, row);
        return ch == '#' || ch == 'W';
    }

    private void drawWall(PGraphics g, int x, int y, int col, int row, boolean window) {
        int variation = (int) (Palette.hash(col * 17 + row * 31) * 3);
        g.noStroke();
        g.fill(variation == 0 ? Palette.STONE_1 : (variation == 1 ? Palette.STONE_2 : 0xFF11121B));
        g.rect(x, y, TILE, TILE);

        // Pola bata.
        g.fill(0xFF0B0C12);
        g.rect(x, y + 7, TILE, 1);
        g.rect(x, y + 15, TILE, 1);
        int offset = ((row & 1) == 0) ? 0 : 8;
        g.rect(x + offset, y, 1, 7);
        g.rect(x + (offset + 8) % TILE, y + 8, 1, 7);

        g.fill(0xFF1A1C27);
        g.rect(x, y, TILE, 1);
        g.fill(0xFF0A0B10);
        g.rect(x, y, 1, TILE);

        if (window) {
            drawWindow(g, x, y);
        }
    }

    private void drawWindow(PGraphics g, int x, int y) {
        g.noStroke();
        g.fill(0xFF0A0B12);
        g.rect(x + 3, y + 2, 10, 12);
        g.fill(0xFF2A2C38);
        g.rect(x + 4, y + 3, 8, 10);
        g.fill(0xFF0B1020);
        g.rect(x + 5, y + 4, 6, 8);

        g.fill(Palette.alpha(Palette.MOON, 90));
        g.rect(x + 6, y + 5, 2, 2);
        g.fill(Palette.alpha(Palette.MOON, 40));
        g.rect(x + 9, y + 7, 1, 1);

        g.fill(0xFF2A2C38);
        g.rect(x + 7, y + 4, 1, 8);
        g.rect(x + 5, y + 7, 6, 1);
    }

    private void drawRug(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.RUG);
        g.rect(x, y, TILE, TILE);
        g.fill(0xFF4A2430);
        g.rect(x, y, TILE, 1);
        g.rect(x, y + TILE - 1, TILE, 1);
        g.rect(x, y, 1, TILE);
        g.rect(x + TILE - 1, y, 1, TILE);

        // Motif berlian kecil.
        g.fill(0xFF5C2E3A);
        int cx = x + 8;
        int cy = y + 8;
        g.triangle(cx, cy - 4, cx + 4, cy, cx, cy + 4);
        g.triangle(cx, cy - 4, cx - 4, cy, cx, cy + 4);

        if (((col + row) & 1) == 0) {
            g.fill(0xFF6B3947);
            g.rect(cx - 1, cy - 1, 2, 2);
        }
    }

    private void drawBookshelf(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(0xFF0A090D);
        g.rect(x, y, TILE, TILE);
        g.fill(Palette.WOOD);
        g.rect(x + 1, y + 1, TILE - 2, TILE - 2);
        g.fill(Palette.WOOD_LIGHT);
        g.rect(x + 2, y + 2, TILE - 4, 1);

        int color = BOOK_COLORS[(int) (Palette.hash(col * 13 + row * 7) * BOOK_COLORS.length)];
        int top = y + 4 + (int) (Palette.hash(col * 3 + row * 11) * 3);
        for (int i = 0; i < 4; i++) {
            g.fill(Palette.alpha(color, 220 - i * 12));
            g.rect(x + 2 + i * 3, top, 2, y + 12 - top);
        }
        g.fill(0xFF120D0C);
        g.rect(x + 2, y + 12, TILE - 4, 1);

        g.fill(Palette.alpha(0x000000, 90));
        g.rect(x + 1, y + TILE - 2, TILE - 2, 1);
    }

    private void drawTable(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.alpha(0x000000, 110));
        g.rect(x + 1, y + 4, 14, 12);

        // Permukaan meja.
        g.fill(0xFF3A2925);
        g.rect(x, y + 2, TILE, 10);
        g.fill(0xFF523B31);
        g.rect(x + 1, y + 3, TILE - 2, 2);
        g.fill(0xFF2A1C18);
        g.rect(x, y + 9, TILE, 3);

        // Kaki meja.
        g.fill(0xFF1D1512);
        g.rect(x + 2, y + 12, 2, 4);
        g.rect(x + TILE - 4, y + 12, 2, 4);

        // Lilin kecil sebagai penanda furnitur, bukan objek interaktif.
        if (((col + row) & 1) == 0) {
            g.fill(0xFFB8A88A);
            g.rect(x + 7, y + 4, 2, 4);
            g.fill(Palette.WARM);
            g.rect(x + 7, y + 2, 1, 2);
        }
    }

    private void drawPillar(PGraphics g, int x, int y, int col, int row) {
        g.noStroke();
        g.fill(Palette.alpha(0x000000, 120));
        g.rect(x + 3, y + 3, TILE - 4, TILE - 3);

        // Badan pilar batu.
        g.fill(0xFF2B2C36);
        g.rect(x + 2, y, TILE - 4, TILE);
        g.fill(0xFF3D3F4C);
        g.rect(x + 3, y + 1, 3, TILE - 2);
        g.fill(0xFF1B1C24);
        g.rect(x + TILE - 7, y + 1, 3, TILE - 2);

        // Kapital dan alas.
        g.fill(0xFF474956);
        g.rect(x, y, TILE, 3);
        g.fill(0xFF3A3C48);
        g.rect(x, y + 3, TILE, 1);
        g.fill(0xFF23242D);
        g.rect(x, y + TILE - 3, TILE, 3);
    }

    private static final int[] BOOK_COLORS = {
            0xFF6B4B4B,
            0xFF4C5A6B,
            0xFF5C4E6B,
            0xFF6B6042,
            0xFF465A52,
            0xFF5A4657
    };
}
