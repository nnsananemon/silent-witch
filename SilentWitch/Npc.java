import processing.core.PGraphics;

/**
 * NPC sederhana yang dapat diajak bicara.
 *
 * Sprite penjaga digambar secara prosedural (tanpa aset eksternal). Tubuh NPC
 * juga menjadi penghalang agar pemain tidak berjalan menembusnya.
 *
 * Nama dan dialog di sini masih placeholder tahap teknis; belum menambah
 * fakta cerita kanonis.
 */
public final class Npc implements Interactable {

    public static final float INTERACT_RADIUS = 22f;
    public static final float BLOCK_W = 12f;
    public static final float BLOCK_H = 8f;

    private final float x;
    private final float y;
    private final String name;
    private final Dialogue dialogue;

    public Npc(float x, float y, String name, Dialogue dialogue) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama NPC tidak boleh kosong.");
        }
        if (dialogue == null) {
            throw new IllegalArgumentException("NPC membutuhkan dialog.");
        }
        this.x = x;
        this.y = y;
        this.name = name;
        this.dialogue = dialogue;
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public float radius() {
        return INTERACT_RADIUS;
    }

    @Override
    public String prompt() {
        return "BICARA";
    }

    public String name() {
        return name;
    }

    @Override
    public Dialogue createDialogue() {
        return dialogue;
    }

    /** Kotak penghalang pada koordinat dunia, sejajar dengan kaki NPC. */
    public boolean blocks(float left, float top, float width, float height) {
        float bx = x - BLOCK_W / 2f;
        float by = y - BLOCK_H;
        return left < bx + BLOCK_W && left + width > bx
                && top < by + BLOCK_H && top + height > by;
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        int px = Math.round(x - cameraX);
        int py = Math.round(y - cameraY);

        g.noStroke();

        // Bayangan.
        g.fill(Palette.alpha(0x000000, 100));
        g.rect(px - 5, py - 1, 10, 2);

        // Mantel panjang.
        g.fill(0xFF242A36);
        g.rect(px - 4, py - 14, 8, 11);
        g.fill(0xFF2F3746);
        g.rect(px - 5, py - 7, 10, 5);
        g.fill(0xFF1A1F29);
        g.rect(px - 3, py - 10, 1, 8);

        // Ikat pinggang.
        g.fill(0xFF141820);
        g.rect(px - 5, py - 7, 10, 2);
        g.fill(Palette.ACCENT_DIM);
        g.rect(px - 1, py - 7, 2, 1);

        // Sepatu.
        g.fill(0xFF10141B);
        g.rect(px - 4, py - 3, 3, 2);
        g.rect(px + 1, py - 3, 3, 2);

        // Kepala dan tudung.
        g.fill(0xFFC6BCAC);
        g.rect(px - 3, py - 19, 6, 5);
        g.fill(0xFFA79D8E);
        g.rect(px - 3, py - 15, 6, 1);
        g.fill(0xFF1C222E);
        g.rect(px - 4, py - 20, 8, 2);
        g.rect(px - 4, py - 18, 1, 4);
        g.rect(px + 3, py - 18, 1, 4);

        // Mata.
        g.fill(0xFF121722);
        g.rect(px - 2, py - 17, 1, 2);
        g.rect(px + 1, py - 17, 1, 2);

        // Lentera kecil di sisi kanan.
        g.fill(0xFF2B2119);
        g.rect(px + 5, py - 12, 1, 5);
        g.fill(Palette.WARM_DIM);
        g.rect(px + 4, py - 8, 4, 4);
        g.fill(Palette.WARM);
        g.rect(px + 5, py - 7, 2, 2);
        g.fill(Palette.alpha(Palette.WARM, 28));
        g.rect(px + 2, py - 10, 8, 8);
    }
}
