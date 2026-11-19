import processing.core.PGraphics;

/**
 * Musuh reguler di Hollow Woods: Bayangan Hutan (Forest Shadow).
 *
 * Berpatroli di kabut hutan, mengejar pemain jika mendekat,
 * memberikan telegraf peringatan cakar merah sebelum menerjang,
 * lalu memberi jeda pemulihan (recovery) yang rentan diserang balik.
 */
public final class BayanganHutan extends Enemy {

    private final float patrolCenterX;
    private final float patrolRange = 28f;
    private float animTime = 0f;

    public BayanganHutan(float x, float y) {
        super("Bayangan Hutan", x, y, 3, 1, 32f, 14f, 12f);
        this.patrolCenterX = x;
        this.alertRadius = 65f;
        this.attackRadius = 24f;
        this.telegraphDuration = 0.65f;
        this.attackDuration = 0.20f;
        this.recoveryDuration = 0.60f;
        this.state = State.PATROL;
    }

    @Override
    public void update(float dt, Player player, TileMap map) {
        if (dt <= 0f) {
            return;
        }
        animTime += dt;

        if (hurtTimer > 0f) {
            hurtTimer -= dt;
            if (hurtTimer < 0f) {
                hurtTimer = 0f;
            }
        }

        if (defeated) {
            if (defeatedTimer > 0f) {
                defeatedTimer -= dt;
            }
            return;
        }

        float dx = player.x() - x;
        float dy = player.y() - y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dx != 0f) {
            facingDir = dx < 0 ? -1 : 1;
        }

        switch (state) {
            case IDLE:
            case PATROL:
                if (dist <= alertRadius && player.isAlive()) {
                    state = State.CHASE;
                    break;
                }
                // Patroli perlahan bolak-balik
                x += facingDir * (speed * 0.5f) * dt;
                if (Math.abs(x - patrolCenterX) > patrolRange) {
                    facingDir = -facingDir;
                    x = patrolCenterX + facingDir * patrolRange;
                }
                break;

            case CHASE:
                if (!player.isAlive()) {
                    state = State.PATROL;
                    break;
                }
                if (dist <= attackRadius) {
                    state = State.TELEGRAPH;
                    stateTimer = telegraphDuration;
                    break;
                }
                if (dist > alertRadius * 1.5f) {
                    state = State.PATROL;
                    break;
                }
                // Kejar pemain
                float dirX = dx / dist;
                float dirY = dy / dist;
                x += dirX * speed * dt;
                y += dirY * speed * dt;
                break;

            case TELEGRAPH:
                stateTimer -= dt;
                if (stateTimer <= 0f) {
                    state = State.ATTACK;
                    stateTimer = attackDuration;
                    // Lunge sedikit ke arah hadap
                    x += facingDir * 6f;
                }
                break;

            case ATTACK:
                stateTimer -= dt;
                if (stateTimer <= 0f) {
                    state = State.RECOVERY;
                    stateTimer = recoveryDuration;
                }
                break;

            case RECOVERY:
                stateTimer -= dt;
                if (stateTimer <= 0f) {
                    state = dist <= alertRadius ? State.CHASE : State.PATROL;
                }
                break;

            case HURT:
                stateTimer -= dt;
                if (stateTimer <= 0f) {
                    state = State.RECOVERY;
                    stateTimer = recoveryDuration * 0.5f;
                }
                break;

            case DEFEATED:
                break;
        }
    }

    @Override
    public float[] getAttackHitbox() {
        if (state != State.ATTACK) {
            return null;
        }
        float hitW = 16f;
        float hitH = 14f;
        float hitX = facingDir < 0 ? (x - hitW) : x;
        float hitY = y - hitH / 2f;
        return new float[]{ hitX, hitY, hitW, hitH };
    }

    @Override
    public void drawTelegraph(PGraphics g, int cameraX, int cameraY) {
        if (state != State.TELEGRAPH || defeated) {
            return;
        }
        int px = Math.round(x - cameraX);
        int py = Math.round(y - cameraY);
        float progress = 1f - (stateTimer / telegraphDuration);

        g.noStroke();
        // Area ancaman merah di tanah
        g.fill(Palette.alpha(0xD93838, (int) (60 + progress * 100)));
        int warnW = Math.round(18 * progress);
        int warnX = facingDir < 0 ? (px - warnW) : px;
        g.rect(warnX, py - 4, warnW, 8);

        // Tanda seru bahaya di atas musuh
        g.fill(0xFFFF3333);
        g.rect(px - 1, py - 18, 2, 4);
        g.rect(px - 1, py - 13, 2, 2);
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        if (defeated && defeatedTimer <= 0f) {
            return;
        }
        int px = Math.round(x - cameraX);
        int py = Math.round(y - cameraY);

        if (Art.ENABLED) {
            int bob = ((int) (animTime * 6f) & 1);
            int alpha = defeated ? (int) (Math.max(0f, Math.min(1f, defeatedTimer)) * 255) : 255;
            Art.drawEnemy(g, px, py + bob, "slime", hurtTimer > 0f, alpha, facingDir > 0, animTime);
            return;
        }

        int bob = ((int) (animTime * 6f) & 1);

        g.noStroke();

        if (defeated) {
            // Efek asap bayangan lenyap
            g.fill(Palette.alpha(0x281B34, (int) (defeatedTimer * 255)));
            g.rect(px - 6, py - 6, 12, 10);
            g.fill(Palette.alpha(0x6E4A8A, (int) (defeatedTimer * 180)));
            g.rect(px - 4, py - 10, 8, 4);
            return;
        }

        // Bayangan di tanah
        g.fill(Palette.alpha(0x000000, 70));
        g.rect(px - 6, py + 4, 12, 3);

        // Tubuh bayangan
        int bodyColor = (hurtTimer > 0f) ? 0xFFFFFFFF : 0xFF1D1426;
        g.fill(bodyColor);
        g.rect(px - 6, py - 8 + bob, 12, 10);
        g.rect(px - 5, py - 11 + bob, 10, 3);

        // Detail bulu/kabut bayangan
        g.fill(0xFF2C1F3A);
        g.rect(px - 4, py - 6 + bob, 8, 6);
        g.rect(px - 7, py - 3 + bob, 2, 4);
        g.rect(px + 5, py - 3 + bob, 2, 4);

        // Mata membara merah
        if (state == State.TELEGRAPH) {
            // Mata menyala tajam saat telegraf
            g.fill(0xFFFF2222);
            int eyeX = facingDir < 0 ? (px - 5) : (px + 2);
            g.rect(eyeX, py - 9 + bob, 3, 2);
            g.fill(0xFFFFAAAA);
            g.rect(eyeX + (facingDir < 0 ? 0 : 1), py - 9 + bob, 1, 1);
        } else {
            g.fill(0xFFD42C2C);
            int eyeX = facingDir < 0 ? (px - 4) : (px + 2);
            g.rect(eyeX, py - 9 + bob, 2, 2);
        }

        // Cakar saat menyerang
        if (state == State.ATTACK) {
            g.fill(0xFFFF4D4D);
            int clawX = facingDir < 0 ? (px - 12) : (px + 6);
            g.rect(clawX, py - 6, 6, 2);
            g.rect(clawX + 2, py - 3, 6, 2);
            g.rect(clawX, py, 6, 2);
        }
    }
}
