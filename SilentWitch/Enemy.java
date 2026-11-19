import processing.core.PGraphics;

/**
 * Kelas dasar musuh untuk sistem pertarungan real-time 2D.
 *
 * Mengimplementasikan finite state machine (IDLE, PATROL, CHASE, TELEGRAPH,
 * ATTACK, RECOVERY, HURT, DEFEATED) dengan siklus ritme telegraf yang adil:
 * Telegraph (peringatan bahaya jelas) -> Attack (hitbox aktif) -> Recovery (jeda rentan membalas).
 */
public abstract class Enemy {

    public enum State {
        IDLE,
        PATROL,
        CHASE,
        TELEGRAPH,
        ATTACK,
        RECOVERY,
        HURT,
        DEFEATED
    }

    protected float x;
    protected float y;
    protected int maxHp;
    protected int hp;
    protected int damage;
    protected float speed;
    protected float hitboxW;
    protected float hitboxH;

    protected State state = State.IDLE;
    protected float stateTimer = 0f;
    protected float hurtTimer = 0f;
    protected int facingDir = -1; // -1 = kiri, 1 = kanan
    protected boolean defeated = false;
    protected float defeatedTimer = 0f;

    protected float alertRadius = 70f;
    protected float attackRadius = 24f;
    protected float telegraphDuration = 0.7f;
    protected float attackDuration = 0.2f;
    protected float recoveryDuration = 0.6f;

    protected String name;
    protected boolean boss = false;

    public Enemy(String name, float x, float y, int maxHp, int damage, float speed, float hitboxW, float hitboxH) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama musuh tidak boleh kosong.");
        }
        if (maxHp <= 0 || damage <= 0) {
            throw new IllegalArgumentException("HP dan damage musuh harus lebih dari nol.");
        }
        this.name = name;
        this.x = x;
        this.y = y;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.damage = damage;
        this.speed = speed;
        this.hitboxW = hitboxW;
        this.hitboxH = hitboxH;
    }

    public abstract void update(float dt, Player player, TileMap map);

    public abstract void draw(PGraphics g, int cameraX, int cameraY);

    /**
     * Menggambar indikator visual telegraph bahaya sebelum serangan aktif
     * (misal: area merah/lingkaran bahaya pada lantai arena).
     */
    public abstract void drawTelegraph(PGraphics g, int cameraX, int cameraY);

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public void setPosition(float newX, float newY) {
        this.x = newX;
        this.y = newY;
    }

    public int hp() {
        return hp;
    }

    public int maxHp() {
        return maxHp;
    }

    public int damage() {
        return damage;
    }

    public String name() {
        return name;
    }

    public boolean isBoss() {
        return boss;
    }

    public State state() {
        return state;
    }

    public boolean isAlive() {
        return hp > 0 && !defeated;
    }

    public boolean isDefeated() {
        return defeated || hp <= 0;
    }

    public boolean isTelegraphing() {
        return state == State.TELEGRAPH;
    }

    public boolean isAttacking() {
        return state == State.ATTACK;
    }

    public boolean isVulnerable() {
        return state == State.RECOVERY || state == State.IDLE || state == State.PATROL || state == State.CHASE;
    }

    public void setDefeated(boolean d) {
        this.defeated = d;
        if (d) {
            this.hp = 0;
            this.state = State.DEFEATED;
        }
    }

    /**
     * Kotak collision tubuh musuh [left, top, width, height].
     */
    public float[] getHitbox() {
        return new float[]{ x - hitboxW / 2f, y - hitboxH / 2f, hitboxW, hitboxH };
    }

    /**
     * Kotak jangkauan serang aktif musuh saat state == ATTACK.
     * Mengembalikan null jika sedang tidak menyerang.
     */
    public abstract float[] getAttackHitbox();

    /**
     * Menerima damage dari tebasan/sihir pemain.
     * @return true jika damage diterima.
     */
    public boolean takeDamage(int amount, float knockbackDirX, float knockbackDirY) {
        if (!isAlive() || hurtTimer > 0f || amount <= 0) {
            return false;
        }
        hp = Math.max(0, hp - amount);
        hurtTimer = 0.22f;

        // Terapkan sedikit dorongan mundur
        x += knockbackDirX * 6f;
        y += knockbackDirY * 6f;

        if (hp <= 0) {
            defeated = true;
            state = State.DEFEATED;
            defeatedTimer = 0.8f;
        } else {
            state = State.HURT;
            stateTimer = 0.22f;
        }
        return true;
    }

    /**
     * Hook yang dipanggil saat musuh pertama kali dikalahkan.
     * Dipakai untuk memberikan sigil, membuka segel, atau mencatat bukti.
     */
    public void onDefeated(GameplayScene scene) {
        // Implementasi default kosong; dioverride oleh boss / penjaga
    }
}
