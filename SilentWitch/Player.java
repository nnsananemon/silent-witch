import java.util.ArrayList;
import java.util.List;

import processing.core.PGraphics;

/**
 * Pemain: gerakan empat arah, collision sumbu terpisah, dan sprite procedural
 * sederhana.
 *
 * Posisi (x, y) adalah titik tengah kotak kaki pemain, bukan sudut sprite.
 * Kotak collision sengaja lebih kecil dari sprite agar gerakan terasa wajar
 * pada tile 16x16.
 */
public final class Player {

    public enum Facing {
        DOWN, UP, LEFT, RIGHT
    }

    public static final float SPEED = 54f;
    public static final int MAX_HP = 6;
    private static final float HITBOX_W = 10f;
    private static final float HITBOX_H = 6f;
    private static final float MAX_STEP = 4f;

    /** Durasi animasi tebasan (5 frame dari paket Dark Knight). */
    public static final float ATTACK_DURATION = 0.24f;
    public static final float ATTACK_COOLDOWN = 0.32f;
    public static final float INVULNERABLE_DURATION = 1.0f;

    private TileMap map;
    private final List<float[]> blockers = new ArrayList<float[]>();
    private float x;
    private float y;
    private Facing facing = Facing.DOWN;
    private boolean moving = false;
    private float animTime = 0f;
    private boolean sitting = false;

    // --- Status Pertarungan & Senjata Saku ---
    private int hp = MAX_HP;
    private float invulnerableTimer = 0f;
    private float attackTimer = 0f;
    private float attackCooldown = 0f;
    private Facing attackFacing = Facing.DOWN;
    private float knockbackVx = 0f;
    private float knockbackVy = 0f;
    private final String weaponName = "Tongkat Perak Saku";
    private int attackDamage = 1;
    private int swingId = 0;

    public Player(TileMap map, float startX, float startY) {
        if (map == null) {
            throw new IllegalArgumentException("Pemain membutuhkan TileMap.");
        }
        this.map = map;
        this.x = startX;
        this.y = startY;
        if (collides(x, y)) {
            throw new IllegalArgumentException("Posisi awal pemain berada di tile solid.");
        }
    }

    public void setMap(TileMap map) {
        if (map == null) {
            throw new IllegalArgumentException("TileMap pemain tidak boleh null.");
        }
        this.map = map;
    }

    public void clearBlockers() {
        blockers.clear();
    }

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

    public void setFacing(Facing facing) {
        if (facing != null) {
            this.facing = facing;
        }
    }

    /**
     * Menambahkan penghalang tambahan di luar tile peta, misalnya tubuh NPC.
     * Koordinat adalah titik tengah kotak pada koordinat dunia.
     */
    public void addBlocker(float centerX, float centerY, float width, float height) {
        if (width <= 0f || height <= 0f) {
            throw new IllegalArgumentException("Ukuran penghalang harus lebih dari nol.");
        }
        blockers.add(new float[]{
                centerX - width / 2f,
                centerY - height / 2f,
                width,
                height
        });
    }

    /** Menghapus blocker berdasarkan titik tengahnya; dipakai saat pintu/gerbang dibuka. */
    public boolean removeBlocker(float centerX, float centerY) {
        for (int i = 0; i < blockers.size(); i++) {
            float[] b = blockers.get(i);
            float cx = b[0] + b[2] / 2f;
            float cy = b[1] + b[3] / 2f;
            if (Math.abs(cx - centerX) < 1f && Math.abs(cy - centerY) < 1f) {
                blockers.remove(i);
                return true;
            }
        }
        return false;
    }

    public Facing facing() {
        return facing;
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean isSitting() {
        return sitting;
    }

    public void setSitting(boolean value) {
        sitting = value;
        if (sitting) {
            moving = false;
        }
    }

    /** Waktu animasi lokal, dipakai agar aksesis (mis. obor) seirama dengan langkah. */
    public float animTime() {
        return animTime;
    }

    // --- Getter & Setter Status Pertarungan ---
    public int hp() {
        return hp;
    }

    public int maxHp() {
        return MAX_HP;
    }

    public void setHp(int hp) {
        this.hp = Math.max(0, Math.min(MAX_HP, hp));
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public boolean isAttacking() {
        return attackTimer > 0f;
    }

    public boolean isInvulnerable() {
        return invulnerableTimer > 0f;
    }

    public float invulnerableTimer() {
        return invulnerableTimer;
    }

    public float attackCooldown() {
        return attackCooldown;
    }

    public String weaponName() {
        return weaponName;
    }

    public int attackDamage() {
        return attackDamage;
    }

    public int swingId() {
        return swingId;
    }

    public Facing attackFacing() {
        return attackFacing;
    }

    /**
     * Memulai serangan sihir/tebasan dengan senjata saku (Tongkat Perak).
     * @return true jika serangan berhasil diluncurkan.
     */
    public boolean attack() {
        if (hp <= 0 || attackCooldown > 0f) {
            return false;
        }
        attackCooldown = ATTACK_COOLDOWN;
        attackTimer = ATTACK_DURATION;
        attackFacing = facing;
        swingId++;
        return true;
    }

    /**
     * Memperbarui hitungan cooldown, durasi serang, i-frame, dan knockback.
     */
    public void updateCombat(float dt) {
        if (dt <= 0f) {
            return;
        }
        if (attackTimer > 0f) {
            attackTimer -= dt;
            if (attackTimer < 0f) {
                attackTimer = 0f;
            }
        }
        if (attackCooldown > 0f) {
            attackCooldown -= dt;
            if (attackCooldown < 0f) {
                attackCooldown = 0f;
            }
        }
        if (invulnerableTimer > 0f) {
            invulnerableTimer -= dt;
            if (invulnerableTimer < 0f) {
                invulnerableTimer = 0f;
            }
        }
        if (knockbackVx != 0f || knockbackVy != 0f) {
            moveAxis(knockbackVx * dt, true);
            moveAxis(knockbackVy * dt, false);
            knockbackVx *= Math.max(0f, 1f - 12f * dt);
            knockbackVy *= Math.max(0f, 1f - 12f * dt);
            if (Math.abs(knockbackVx) < 1f) {
                knockbackVx = 0f;
            }
            if (Math.abs(knockbackVy) < 1f) {
                knockbackVy = 0f;
            }
        }
    }

    /**
     * Menerima damage dari sumber musuh, memberikan i-frame dan dorongan knockback.
     * @return true jika pemain berhasil dikenai damage.
     */
    public boolean takeDamage(int amount, float sourceX, float sourceY) {
        if (hp <= 0 || invulnerableTimer > 0f || amount <= 0) {
            return false;
        }
        hp = Math.max(0, hp - amount);
        invulnerableTimer = INVULNERABLE_DURATION;

        float dx = x - sourceX;
        float dy = y - sourceY;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len > 0.001f) {
            knockbackVx = (dx / len) * 85f;
            knockbackVy = (dy / len) * 85f;
        } else {
            knockbackVx = 0f;
            knockbackVy = -85f;
        }
        return true;
    }

    public void heal(int amount) {
        if (amount > 0 && hp > 0) {
            hp = Math.min(MAX_HP, hp + amount);
        }
    }

    /**
     * Memulihkan pemain saat kalah di checkpoint aman terdekat.
     */
    public void respawn(float spawnX, float spawnY) {
        this.x = spawnX;
        this.y = spawnY;
        this.hp = MAX_HP;
        this.invulnerableTimer = 1.5f;
        this.attackTimer = 0f;
        this.attackCooldown = 0f;
        this.knockbackVx = 0f;
        this.knockbackVy = 0f;
        this.moving = false;
    }

    /**
     * Mengembalikan kotak collision fisik pemain: [left, top, width, height].
     */
    public float[] getHitbox() {
        return new float[]{ x - HITBOX_W / 2f, y - HITBOX_H / 2f, HITBOX_W, HITBOX_H };
    }

    /**
     * Mengembalikan kotak jangkauan serangan aktif pemain berdasarkan arah hadap serang.
     * Mengembalikan null jika pemain sedang tidak mengayunkan senjata saku.
     */
    public float[] getAttackHitbox() {
        if (attackTimer <= 0f) {
            return null;
        }
        switch (attackFacing) {
            case LEFT:
                return new float[]{ x - 18f, y - 9f, 16f, 16f };
            case RIGHT:
                return new float[]{ x + 2f, y - 9f, 16f, 16f };
            case UP:
                return new float[]{ x - 9f, y - 20f, 18f, 16f };
            case DOWN:
            default:
                return new float[]{ x - 9f, y + 2f, 18f, 16f };
        }
    }

    /**
     * Memperbarui pemain berdasarkan arah input (-1, 0, 1). Diagonal
     * dinormalisasi agar kecepatan tidak bertambah.
     */
    public void update(float dt, float inputX, float inputY) {
        if (dt <= 0f) {
            return;
        }
        updateCombat(dt);

        float dirX = clampDirection(inputX);
        float dirY = clampDirection(inputY);
        moving = dirX != 0f || dirY != 0f;

        if (!moving) {
            animTime = 0f;
            return;
        }

        float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        float stepX = dirX / length * SPEED * dt;
        float stepY = dirY / length * SPEED * dt;

        updateFacing(dirX, dirY);
        moveAxis(stepX, true);
        moveAxis(stepY, false);
        animTime += dt;
    }

    private static float clampDirection(float value) {
        if (value > 1f) {
            return 1f;
        }
        if (value < -1f) {
            return -1f;
        }
        return value;
    }

    private void updateFacing(float dirX, float dirY) {
        if (Math.abs(dirX) > Math.abs(dirY)) {
            facing = dirX < 0f ? Facing.LEFT : Facing.RIGHT;
        } else {
            facing = dirY < 0f ? Facing.UP : Facing.DOWN;
        }
    }

    /** Memindahkan satu sumbu dengan sub-langkah agar tidak menembus tile. */
    private void moveAxis(float delta, boolean horizontal) {
        if (delta == 0f) {
            return;
        }
        float remaining = delta;
        while (Math.abs(remaining) > 0.0001f) {
            float step = Math.max(-MAX_STEP, Math.min(MAX_STEP, remaining));
            float nextX = horizontal ? x + step : x;
            float nextY = horizontal ? y : y + step;
            if (collides(nextX, nextY)) {
                break;
            }
            x = nextX;
            y = nextY;
            remaining -= step;
        }
    }

    private boolean collides(float cx, float cy) {
        float left = cx - HITBOX_W / 2f;
        float top = cy - HITBOX_H / 2f;
        if (map.rectCollides(left, top, HITBOX_W, HITBOX_H)) {
            return true;
        }
        for (float[] blocker : blockers) {
            if (left < blocker[0] + blocker[2] && left + HITBOX_W > blocker[0]
                    && top < blocker[1] + blocker[3] && top + HITBOX_H > blocker[1]) {
                return true;
            }
        }
        return false;
    }

    /** Pose duduk di tepi perapian: sprite 4 arah, fallback prosedural. */
    private void drawSittingPose(PGraphics g, int px, int py) {
        if (Art.sitSpriteReady()) {
            // 0=kiri, 1=kanan, 2=depan, 3=belakang
            int index;
            switch (facing) {
                case LEFT: index = 0; break;
                case RIGHT: index = 1; break;
                case UP: index = 3; break;
                case DOWN:
                default: index = 2; break;
            }
            int alpha = 255;
            if (invulnerableTimer > 0f) {
                int blink = (int) (invulnerableTimer * 20f);
                if ((blink & 1) == 0) alpha = 110;
            }
            g.noStroke();
            g.fill(Palette.alpha(0x000000, 80));
            g.ellipse(px, py - 1f, 16f, 3f);
            Art.drawSit(g, px, py, index, alpha);
            return;
        }

        g.noStroke();
        // Bayangan
        g.fill(Palette.alpha(0x000000, 90));
        g.rect(px - 6f, py - 1f, 12f, 2f);
        // Kaki ditekuk ke depan
        g.fill(0xFF15121C);
        g.rect(px - 5f, py - 5f, 10f, 4f);
        g.fill(0xFF2A2138);
        g.rect(px - 6f, py - 6f, 12f, 3f);
        // Jubah / badan
        g.fill(0xFF2A2138);
        g.rect(px - 5f, py - 13f, 10f, 8f);
        g.fill(0xFF3B2E52);
        g.rect(px - 6f, py - 10f, 12f, 2f);
        // Lengan bertumpu di lutut
        g.fill(0xFF3B2E52);
        g.rect(px - 7f, py - 9f, 2f, 5f);
        g.rect(px + 5f, py - 9f, 2f, 5f);
        // Kepala sedikit menunduk ke arah api
        g.fill(0xFFD9CFC0);
        g.rect(px - 3f, py - 17f, 6f, 5f);
        g.fill(0xFF201A24);
        g.rect(px - 3f, py - 17f, 6f, 1f);
        g.rect(px - 3f, py - 16f, 1f, 3f);
        g.rect(px + 2f, py - 16f, 1f, 3f);
        g.fill(0xFF1A1520);
        g.rect(px - 2f, py - 14f, 1f, 1f);
        g.rect(px + 1f, py - 14f, 1f, 1f);
    }

    public void draw(PGraphics g, int cameraX, int cameraY) {
        int px = Math.round(x - cameraX);
        int py = Math.round(y - cameraY);

        if (sitting) {
            drawSittingPose(g, px, py);
            return;
        }

        if (Art.heroSpriteReady() || Art.ENABLED) {
            int alpha = 255;
            if (invulnerableTimer > 0f) {
                int blink = (int) (invulnerableTimer * 20f);
                if ((blink & 1) == 0) {
                    alpha = 110;
                }
            }
            float swing = attackTimer > 0f
                    ? 1f - attackTimer / ATTACK_DURATION
                    : 0f;
            // Saat menebas, arah gambar dikunci ke arah tebasan supaya animasi
            // dan hitbox selalu searah (tidak ikut berputar saat pemain belok).
            Facing drawFacing = attackTimer > 0f ? attackFacing : facing;
            Art.drawHero(g, px, py, drawFacing, moving, animTime, attackTimer > 0f, swing, alpha);
            return;
        }

        // Efek kedip i-frame saat terkena serangan
        if (invulnerableTimer > 0f) {
            int blink = (int) (invulnerableTimer * 20f);
            if ((blink & 1) == 0) {
                // Bayangan samar tetap digambar saat berkedip
                g.noStroke();
                g.fill(Palette.alpha(0x000000, 40));
                g.rect(px - 5, py - 1, 10, 2);
                return;
            }
        }

        int bob = moving && ((int) (animTime * 8f) & 1) == 0 ? 1 : 0;
        int feetY = py - bob;
        int stride = moving && ((int) (animTime * 8f) & 1) == 0 ? 1 : 0;

        g.noStroke();

        // Bayangan.
        g.fill(Palette.alpha(0x000000, 90));
        g.rect(px - 5, py - 1, 10, 2);

        // Jubah.
        g.fill(0xFF2A2138);
        g.rect(px - 4, feetY - 12, 8, 8);
        g.rect(px - 5, feetY - 8, 10, 5);
        g.fill(0xFF3B2E52);
        g.rect(px - 5, feetY - 5, 10, 1);
        g.fill(0xFF221B2E);
        g.rect(px - 3, feetY - 9, 1, 4);

        // Saku jubah dan Tongkat Sihir Saku (selalu dibawa di saku)
        int pocketSide = (facing == Facing.LEFT) ? (px - 5) : (px + 3);
        g.fill(0xFF1E1728);
        g.rect(pocketSide, feetY - 8, 2, 3);
        if (attackTimer <= 0f) {
            // Tongkat Perak Saku terselip di saku: gagang perak + kristal ungu
            g.fill(0xFFDEE2EC);
            g.rect(pocketSide, feetY - 10, 1, 3);
            g.fill(Palette.MAGIC);
            g.rect(pocketSide, feetY - 11, 1, 1);
        }

        // Sepatu.
        g.fill(0xFF15121C);
        g.rect(px - 4, feetY - 3 + stride, 3, 2);
        g.rect(px + 2, feetY - 2 - stride, 3, 2);

        // Kepala.
        g.fill(0xFFD9CFC0);
        g.rect(px - 3, feetY - 16, 6, 5);
        g.fill(0xFFB9AE9F);
        g.rect(px - 3, feetY - 12, 6, 1);

        // Rambut.
        g.fill(0xFF201A24);
        g.rect(px - 3, feetY - 16, 6, 1);
        g.rect(px - 3, feetY - 15, 1, 3);
        g.rect(px + 2, feetY - 15, 1, 3);
        g.rect(px - 2, feetY - 15, 1, 1);

        // Mata mengikuti arah hadap.
        g.fill(0xFF1A1520);
        if (facing == Facing.DOWN) {
            g.rect(px - 2, feetY - 14, 1, 2);
            g.rect(px + 1, feetY - 14, 1, 2);
        } else if (facing == Facing.LEFT) {
            g.rect(px - 2, feetY - 14, 1, 2);
        } else if (facing == Facing.RIGHT) {
            g.rect(px + 1, feetY - 14, 1, 2);
        }

        // Topi penyihir.
        g.fill(0xFF4E3D6B);
        g.rect(px - 6, feetY - 18, 13, 2);
        g.rect(px - 4, feetY - 19, 9, 1);
        g.rect(px - 3, feetY - 20, 7, 1);
        g.rect(px - 2, feetY - 21, 5, 1);
        g.rect(px - 1, feetY - 22, 3, 1);
        g.fill(0xFF6C57A0);
        g.rect(px - 6, feetY - 18, 13, 1);
        g.rect(px - 1, feetY - 22, 2, 1);

        // Pita topi dan kilau kecil di ujung.
        g.fill(Palette.ACCENT_DIM);
        g.rect(px - 4, feetY - 19, 9, 1);
        g.fill(Palette.alpha(Palette.MAGIC, 140));
        g.rect(px, feetY - 23, 1, 1);

        // Visual Serangan: Tongkat Perak ditarik dari saku dan menebaskan sihir
        if (attackTimer > 0f) {
            drawAttackVisual(g, px, feetY);
        }
    }

    private void drawAttackVisual(PGraphics g, int px, int feetY) {
        g.noStroke();
        switch (attackFacing) {
            case RIGHT:
                // Tangan & Tongkat Perak diayunkan ke kanan
                g.fill(0xFFD9CFC0);
                g.rect(px + 4, feetY - 8, 2, 2);
                g.fill(0xFFDEE2EC);
                g.rect(px + 6, feetY - 8, 4, 1);
                g.fill(Palette.MAGIC);
                g.rect(px + 10, feetY - 8, 1, 1);

                // Busur tebasan sihir sabit
                g.fill(Palette.alpha(Palette.MAGIC, 190));
                g.rect(px + 10, feetY - 15, 2, 12);
                g.rect(px + 12, feetY - 13, 2, 8);
                g.fill(0xFFFFFFFF);
                g.rect(px + 9, feetY - 14, 2, 10);
                g.fill(Palette.ACCENT);
                g.rect(px + 13, feetY - 16, 1, 1);
                g.rect(px + 14, feetY - 8, 1, 1);
                break;

            case LEFT:
                // Tangan & Tongkat Perak diayunkan ke kiri
                g.fill(0xFFD9CFC0);
                g.rect(px - 6, feetY - 8, 2, 2);
                g.fill(0xFFDEE2EC);
                g.rect(px - 10, feetY - 8, 4, 1);
                g.fill(Palette.MAGIC);
                g.rect(px - 11, feetY - 8, 1, 1);

                // Busur tebasan sihir sabit
                g.fill(Palette.alpha(Palette.MAGIC, 190));
                g.rect(px - 12, feetY - 15, 2, 12);
                g.rect(px - 14, feetY - 13, 2, 8);
                g.fill(0xFFFFFFFF);
                g.rect(px - 11, feetY - 14, 2, 10);
                g.fill(Palette.ACCENT);
                g.rect(px - 14, feetY - 16, 1, 1);
                g.rect(px - 15, feetY - 8, 1, 1);
                break;

            case UP:
                // Tangan & Tongkat Perak diayunkan ke atas
                g.fill(0xFFD9CFC0);
                g.rect(px - 1, feetY - 14, 2, 2);
                g.fill(0xFFDEE2EC);
                g.rect(px, feetY - 18, 1, 4);
                g.fill(Palette.MAGIC);
                g.rect(px, feetY - 19, 1, 1);

                // Busur tebasan sihir sabit horizontal atas
                g.fill(Palette.alpha(Palette.MAGIC, 190));
                g.rect(px - 7, feetY - 21, 14, 2);
                g.rect(px - 5, feetY - 23, 10, 2);
                g.fill(0xFFFFFFFF);
                g.rect(px - 6, feetY - 20, 12, 1);
                g.fill(Palette.ACCENT);
                g.rect(px - 8, feetY - 22, 1, 1);
                g.rect(px + 7, feetY - 22, 1, 1);
                break;

            case DOWN:
            default:
                // Tangan & Tongkat Perak diayunkan ke bawah
                g.fill(0xFFD9CFC0);
                g.rect(px - 1, feetY - 6, 2, 2);
                g.fill(0xFFDEE2EC);
                g.rect(px, feetY - 4, 1, 4);
                g.fill(Palette.MAGIC);
                g.rect(px, feetY, 1, 1);

                // Busur tebasan sihir sabit horizontal bawah
                g.fill(Palette.alpha(Palette.MAGIC, 190));
                g.rect(px - 7, feetY + 3, 14, 2);
                g.rect(px - 5, feetY + 5, 10, 2);
                g.fill(0xFFFFFFFF);
                g.rect(px - 6, feetY + 2, 12, 1);
                g.fill(Palette.ACCENT);
                g.rect(px - 8, feetY + 4, 1, 1);
                g.rect(px + 7, feetY + 4, 1, 1);
                break;
        }
    }
}
