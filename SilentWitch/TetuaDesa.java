import processing.core.PGraphics;

/**
 * NPC Tetua Desa di Ashen Village (Prolog).
 *
 * Menggunakan sprite warga muda (boy_front.png) dengan animasi menoleh (mirror)
 * dan efek hop (bob) saat diajak bicara.
 *
 * NPC pertama yang ditemui pemain. Memberikan konteks awal tentang
 * kematian Clara dan mengarahkan pemain ke manor untuk memulai
 * penyelidikan. Menyimpan dialog awal dan dialog pengingat.
 */
public final class TetuaDesa implements Interactable {

    public static final int DIR_DOWN = 0;
    public static final int DIR_LEFT = 1;
    public static final int DIR_UP = 2;
    public static final int DIR_RIGHT = 3;
    public static final float TURN_DURATION = 0.22f;

    public static final float INTERACT_RADIUS = 24f;
    public static final float BLOCK_W = 12f;
    public static final float BLOCK_H = 8f;

    private final float x;
    private final float y;
    private boolean spoken = false;

    private final Dialogue initialDialogue;
    private final Dialogue repeatDialogue;

    private int dir = DIR_DOWN;
    private int targetDir = DIR_DOWN;
    private float turnTimer = 0f;

    public TetuaDesa(float x, float y) {
        this.x = x;
        this.y = y;

        this.initialDialogue = new Dialogue("TETUA DESA",
                "Seorang pemuda perwakilan desa berdiri di tengah alun-alun.",
                "\"Kau pasti yang mereka kirim. Penyihir... penyelidik, katanya.\"",
                "\"Clara Thorne ditemukan tiga hari lalu di koridor Sayap Timur manor.\"",
                "\"Tidak ada tanda kekerasan yang jelas, tapi juga bukan kematian wajar.\"",
                "\"Penjaga manor — Garrick — yang menemukan jasadnya. Dia bilang ada penyusup.\"",
                "\"Tapi kami warga desa tahu... jawaban pertama jarang benar.\"",
                "\"Pergilah ke manor, di ujung jalan ini ke timur. Periksa semuanya sendiri.\"",
                "\"Dan jangan percaya siapa pun di sana sepenuhnya.\"");

        this.repeatDialogue = new Dialogue("TETUA DESA",
                "\"Manor ada di ujung jalan ke timur.\"",
                "\"Periksa papan pengumuman kalau kau belum. Ada detail yang mungkin berguna.\"",
                "\"Hati-hati di sana. Tempat itu... tidak ramah pada pendatang.\"");
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

    @Override
    public Dialogue createDialogue() {
        if (!spoken) {
            spoken = true;
            return initialDialogue;
        }
        return repeatDialogue;
    }

    public boolean hasSpoken() {
        return spoken;
    }

    public void restoreSpoken(boolean state) {
        this.spoken = state;
    }

    public float blockW() {
        return BLOCK_W;
    }

    public float blockH() {
        return BLOCK_H;
    }

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

    public void goHome() {
        if (dir != DIR_DOWN && turnTimer <= 0f) {
            targetDir = DIR_DOWN;
            turnTimer = TURN_DURATION;
        }
    }

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

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        int sx = Math.round(x - cameraX);
        int sy = Math.round(y - cameraY);

        if (Art.NPC_SPRITES || Art.ENABLED) {
            Art.drawAnimatedNpc(g, sx, sy, "boy", dir, bob());
            return;
        }

        g.noStroke();

        // Bayangan di tanah
        g.fill(Palette.alpha(0x000000, 100));
        g.rect(sx - 5, sy - 1, 10, 2);

        int renderY = sy + Math.round(bob());

        // Pakaian pemuda desa (rompi cokelat, kemeja cerah)
        g.fill(0xFFE8E0D0);
        g.rect(sx - 3, renderY - 12, 6, 8);
        g.fill(0xFF5A4030);
        g.rect(sx - 3, renderY - 12, 2, 8);
        g.rect(sx + 1, renderY - 12, 2, 8);

        // Celana cokelat tua
        g.fill(0xFF382820);
        g.rect(sx - 3, renderY - 4, 6, 4);

        // Sepatu
        g.fill(0xFF1E1410);
        g.rect(sx - 3, renderY, 3, 2);
        g.rect(sx + 1, renderY, 3, 2);

        // Kepala
        g.fill(0xFFDCC8B0);
        g.rect(sx - 2, renderY - 17, 5, 5);

        // Rambut cokelat pendek
        g.fill(0xFF4A3428);
        g.rect(sx - 3, renderY - 19, 7, 3);
        g.rect(sx - 3, renderY - 16, 1, 3);
        g.rect(sx + 3, renderY - 16, 1, 3);
    }
}
