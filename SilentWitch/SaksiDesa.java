import processing.core.PGraphics;

/**
 * NPC Saksi Desa di Ashen Village (Prolog).
 *
 * Menggunakan sprite gadis desa (girl_front.png) dengan animasi menoleh (mirror)
 * dan efek hop (bob) saat diajak bicara.
 *
 * Seorang perempuan muda yang memberikan kesaksian pertama tentang
 * malam kematian Clara. Informasi yang diberikan belum lengkap
 * dan mendorong pemain untuk menyelidiki lebih lanjut.
 */
public final class SaksiDesa implements Interactable {

    public static final int DIR_DOWN = 0;
    public static final int DIR_LEFT = 1;
    public static final int DIR_UP = 2;
    public static final int DIR_RIGHT = 3;
    public static final float TURN_DURATION = 0.22f;

    public static final float INTERACT_RADIUS = 22f;
    public static final float BLOCK_W = 10f;
    public static final float BLOCK_H = 6f;

    private final float x;
    private final float y;
    private boolean spoken = false;

    private final Dialogue initialDialogue;
    private final Dialogue repeatDialogue;

    private int dir = DIR_DOWN;
    private int targetDir = DIR_DOWN;
    private float turnTimer = 0f;

    public SaksiDesa(float x, float y) {
        this.x = x;
        this.y = y;

        this.initialDialogue = new Dialogue("WARGA DESA",
                "Seorang gadis desa berdiri di depan rumahnya, tampak cemas.",
                "\"Kau menyelidiki kasus Clara? Bagus. Seseorang harus melakukannya.\"",
                "\"Malam itu... aku mendengar suara dari arah manor. Seperti besi beradu.\"",
                "\"Tapi ketika aku melihat ke jendela, yang kulihat hanya kabut.\"",
                "\"Clara sering lewat sini setiap sore. Dia selalu ramah.\"",
                "\"Garrick — si penjaga — bilang ada penyusup dari luar desa.\"",
                "\"Tapi pintu manor hanya bisa dibuka dari dalam. Semua orang tahu itu.\"",
                "\"Periksa sendiri keadaan di manor. Aku yakin ada yang disembunyikan.\"");

        this.repeatDialogue = new Dialogue("WARGA DESA",
                "\"Pintu manor hanya bisa dibuka dari dalam. Ingat itu.\"",
                "\"Aku tidak tahu siapa yang melakukannya, tapi cerita penyusup itu... aneh.\"");
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
            Art.drawAnimatedNpc(g, sx, sy, "girl", dir, bob());
            return;
        }

        g.noStroke();

        // Bayangan di tanah
        g.fill(Palette.alpha(0x000000, 100));
        g.rect(sx - 4, sy - 1, 8, 2);

        int renderY = sy + Math.round(bob());

        // Gaun gadis desa (celemek putih gading, rok cokelat)
        g.fill(0xFF4A3428);
        g.rect(sx - 3, renderY - 10, 6, 9);
        g.fill(0xFFEDE8D8);
        g.rect(sx - 2, renderY - 10, 4, 6);

        // Sepatu
        g.fill(0xFF1E1410);
        g.rect(sx - 2, renderY, 2, 2);
        g.rect(sx + 1, renderY, 2, 2);

        // Kepala
        g.fill(0xFFDCC8B0);
        g.rect(sx - 2, renderY - 16, 4, 5);

        // Tudung putih dan rambut cokelat
        g.fill(0xFFEDE8D8);
        g.rect(sx - 3, renderY - 18, 6, 3);
        g.rect(sx - 3, renderY - 15, 1, 4);
        g.rect(sx + 2, renderY - 15, 1, 4);
        g.fill(0xFF3E2818);
        g.rect(sx - 2, renderY - 15, 4, 2);
    }
}
