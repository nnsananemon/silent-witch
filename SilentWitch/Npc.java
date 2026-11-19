import processing.core.PGraphics;

/**
 * NPC Kakek Pustakawan di Manor Hall.
 *
 * Menampilkan animasi menoleh 4-arah (bawah/depan, kiri, atas/belakang, kanan)
 * dengan efek hop (bob) dan logika mirror bila diajak bicara atau saat menekan E.
 * Tubuh NPC juga menjadi penghalang agar pemain tidak berjalan menembusnya.
 *
 * Menyimpan dialog awal (yang memberikan kesaksian/clue) dan dialog pengingat
 * bila kesaksian telah dicatat ke dalam buku kasus.
 */
public final class Npc implements Interactable {

    public static final int DIR_DOWN = 0;
    public static final int DIR_LEFT = 1;
    public static final int DIR_UP = 2;
    public static final int DIR_RIGHT = 3;
    public static final float TURN_DURATION = 0.22f;

    public static final float INTERACT_RADIUS = 22f;
    public static final float BLOCK_W = 12f;
    public static final float BLOCK_H = 8f;

    private final float x;
    private final float y;
    private final String name;
    private final Clue clue;
    private final Dialogue dialogue;
    private final Dialogue repeatDialogue;

    private int dir = DIR_DOWN;
    private int targetDir = DIR_DOWN;
    private float turnTimer = 0f;

    public Npc(float x, float y, String name, Dialogue dialogue) {
        this(x, y, name, null, dialogue, null);
    }

    public Npc(float x, float y, String name, Clue clue, Dialogue initialDialogue, Dialogue repeatDialogue) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama NPC tidak boleh kosong.");
        }
        if (initialDialogue == null) {
            throw new IllegalArgumentException("NPC membutuhkan dialog.");
        }
        this.x = x;
        this.y = y;
        this.name = name;
        this.clue = clue;
        this.dialogue = initialDialogue;
        this.repeatDialogue = repeatDialogue;
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
        return "BICARA DENGAN " + name;
    }

    public String name() {
        return name;
    }

    public Clue clue() {
        return clue;
    }

    public Dialogue repeatDialogue() {
        return repeatDialogue;
    }

    @Override
    public Dialogue createDialogue() {
        return dialogue;
    }

    @Override
    public Dialogue interact(InvestigationSystem investigation) {
        if (clue != null && investigation != null) {
            if (!investigation.isDiscovered(clue.id())) {
                investigation.discover(clue);
                return dialogue;
            } else if (repeatDialogue != null) {
                return repeatDialogue;
            }
        }
        return dialogue;
    }

    /** Menghitung arah ke titik (targetX, targetY) dan memulai animasi menoleh bila arah berbeda. */
    public void lookAt(float targetX, float targetY) {
        float dx = targetX - x;
        float dy = targetY - y;
        int d;
        if (Math.abs(dx) > Math.abs(dy)) {
            d = dx > 0 ? DIR_RIGHT : DIR_LEFT;
        } else {
            d = dy > 0 ? DIR_DOWN : DIR_UP;
        }
        if (d != dir && turnTimer <= 0f) {
            targetDir = d;
            turnTimer = TURN_DURATION;
        }
    }

    /** Memulai animasi menoleh ke arah pemain saat diajak bicara atau saat menekan E. */
    public void triggerTalkAnimation(float targetX, float targetY) {
        float dx = targetX - x;
        float dy = targetY - y;
        int d;
        if (Math.abs(dx) > Math.abs(dy)) {
            d = dx > 0 ? DIR_RIGHT : DIR_LEFT;
        } else {
            d = dy > 0 ? DIR_DOWN : DIR_UP;
        }
        targetDir = d;
        turnTimer = TURN_DURATION;
    }

    /** Kembali menghadap depan setelah selesai berbicara. */
    public void goHome() {
        if (dir != DIR_DOWN && turnTimer <= 0f) {
            targetDir = DIR_DOWN;
            turnTimer = TURN_DURATION;
        }
    }

    /** Memperbarui timer animasi menoleh. */
    public void update(float dt) {
        if (turnTimer > 0f) {
            float half = TURN_DURATION / 2f;
            boolean beforeHalf = turnTimer > half;
            turnTimer -= dt;
            if (beforeHalf && turnTimer <= half) {
                dir = targetDir;
            }
            if (turnTimer < 0f) {
                turnTimer = 0f;
            }
        }
    }

    /** Lompatan kecil (bob) saat menoleh, bernilai negatif (ke atas). */
    public float bob() {
        if (turnTimer > 0f) {
            float progress = (TURN_DURATION - turnTimer) / TURN_DURATION;
            return -(float) Math.sin(progress * Math.PI) * 2.5f;
        }
        return 0f;
    }

    public int dir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
        this.targetDir = dir;
        this.turnTimer = 0f;
    }

    public boolean isTurning() {
        return turnTimer > 0f;
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

        if (Art.NPC_SPRITES || Art.ENABLED) {
            Art.drawAnimatedNpc(g, px, py, "librarian", dir, bob());
            return;
        }

        g.noStroke();

        // Bayangan di tanah
        g.fill(Palette.alpha(0x000000, 100));
        g.rect(px - 6, py - 1, 12, 3);

        int renderY = py + Math.round(bob());

        // Jubah kakek pustakawan (biru tua akademisi)
        g.fill(0xFF28344A);
        g.rect(px - 5, renderY - 16, 10, 14);
        g.fill(0xFF1E2838);
        g.rect(px - 4, renderY - 8, 8, 7);

        // Kerah dan lis jubah emas pudar
        g.fill(0xFFD4B870);
        g.rect(px - 2, renderY - 16, 4, 3);
        g.rect(px - 1, renderY - 13, 2, 7);

        // Sepatu
        g.fill(0xFF141820);
        g.rect(px - 4, renderY - 2, 3, 2);
        g.rect(px + 1, renderY - 2, 3, 2);

        // Kepala dan wajah tua
        g.fill(0xFFD6C6B0);
        g.rect(px - 3, renderY - 22, 6, 6);

        // Rambut dan janggut putih keperakan
        g.fill(0xFFE0E4E8);
        g.rect(px - 4, renderY - 24, 8, 3);
        g.rect(px - 4, renderY - 21, 1, 4);
        g.rect(px + 3, renderY - 21, 1, 4);
        // Janggut
        g.rect(px - 2, renderY - 17, 4, 3);
        g.rect(px - 1, renderY - 14, 2, 2);

        // Kacamata / mata bijak
        g.fill(0xFF4A4030);
        g.rect(px - 2, renderY - 20, 1, 1);
        g.rect(px + 1, renderY - 20, 1, 1);

        // Buku naskah tua di tangan
        g.fill(0xFF8B4513);
        g.rect(px + 3, renderY - 12, 4, 5);
        g.fill(0xFFE8DCC0);
        g.rect(px + 4, renderY - 11, 2, 3);
    }
}
