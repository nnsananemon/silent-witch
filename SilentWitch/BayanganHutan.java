import processing.core.PGraphics;

/**
 * Musuh reguler di Hollow Woods: Bayangan Hutan (Forest Shadow).
 *
 * Berpatroli di kabut hutan, mengejar pemain jika mendekat,
 * memberikan telegraf peringatan cakar merah sebelum menerjang,
 * lalu memberi jeda pemulihan (recovery) yang rentan diserang balik.
 */
public final class BayanganHutan extends Enemy {

    private final float anchorX;
    private final float anchorY;
    private final float leashRadius = 75f;
    private float animTime = 0f;

    public BayanganHutan(float x, float y) {
        super("Bayangan Hutan", x, y, 3, 1, 32f, 14f, 12f);
        this.anchorX = x;
        this.anchorY = y;
        this.alertRadius = 60f;
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

        switch (state) {
            case IDLE:
            case PATROL:
                if (dist <= alertRadius && player.isAlive()) {
                    // Baru maju mengejar jika karakter pemain sudah dekat
                    facingDir = dx < 0 ? -1 : 1;
                    state = State.CHASE;
                    break;
                }
                // Jika berada di luar pos jaganya, kembali melayang ke anchor dengan tenang
                float toAnchor = anchorX - x;
                if (Math.abs(toAnchor) > 2f) {
                    facingDir = toAnchor < 0 ? -1 : 1;
                    moveWithCollision(facingDir * (speed * 0.4f) * dt, 0f, map);
                } else {
                    // Berdiam di pos jaga, tidak bergerak maju-mundur
                    x = anchorX;
                    if (dx != 0f) {
                        facingDir = dx < 0 ? -1 : 1;
                    }
                }
                break;

            case CHASE:
                if (!player.isAlive()) {
                    state = State.PATROL;
                    break;
                }
                facingDir = dx < 0 ? -1 : 1;
                if (dist <= attackRadius) {
                    state = State.TELEGRAPH;
                    stateTimer = telegraphDuration;
                    break;
                }
                // Batasan wilayah: jika pemain menjauh atau musuh keluar dari batas leash, lepaskan kejaran
                if (dist > alertRadius * 1.35f || Math.abs(x - anchorX) > leashRadius) {
                    state = State.PATROL;
                    break;
                }
                // Maju mengejar pemain dengan deteksi halangan batu/pohon
                float dirX = dx / dist;
                float dirY = dy / dist;
                moveWithCollision(dirX * speed * dt, dirY * speed * dt, map);
                break;

            case TELEGRAPH:
                facingDir = dx < 0 ? -1 : 1;
                stateTimer -= dt;
                if (stateTimer <= 0f) {
                    state = State.ATTACK;
                    stateTimer = attackDuration;
                    // Lunge sedikit ke arah hadap dengan deteksi tabrakan
                    moveWithCollision(facingDir * 6f, 0f, map);
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

    /**
     * Memindahkan musuh dengan memeriksa tabrakan tile padat (batu/pohon),
     * sehingga musuh tidak tembus batu di latar.
     */
    private void moveWithCollision(float vx, float vy, TileMap map) {
        if (map == null) {
            x += vx;
            y += vy;
            return;
        }
        float halfW = hitboxW / 2f;
        float halfH = hitboxH / 2f;
        if (vx != 0f) {
            float nextX = x + vx;
            if (!map.rectCollides(nextX - halfW, y - halfH, hitboxW, hitboxH)) {
                x = nextX;
            }
        }
        if (vy != 0f) {
            float nextY = y + vy;
            if (!map.rectCollides(x - halfW, nextY - halfH, hitboxW, hitboxH)) {
                y = nextY;
            }
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
            drawForestWraith(g, px, py + bob, alpha, hurtTimer > 0f, animTime);
            return;
        }

        int bob = ((int) (animTime * 6f) & 1);

        g.noStroke();

        if (defeated) {
            // Efek pembubaran kabut bayangan teal
            g.fill(Palette.alpha(0x12242C, (int) (defeatedTimer * 255)));
            g.rect(px - 6, py - 6, 12, 10);
            g.fill(Palette.alpha(0x5DE0D2, (int) (defeatedTimer * 180)));
            g.rect(px - 4, py - 10, 8, 4);
            return;
        }

        drawForestWraith(g, px, py + bob, 255, hurtTimer > 0f, animTime);

        // Cakar saat menyerang
        if (state == State.ATTACK) {
            g.fill(0xFFFF4D4D);
            int clawX = facingDir < 0 ? (px - 12) : (px + 6);
            g.rect(clawX, py - 6, 6, 2);
            g.rect(clawX + 2, py - 3, 6, 2);
            g.rect(clawX, py, 6, 2);
        }
    }

    /** Menggambar hantu kabut hutan (Forest Mist Wraith) dengan mata cyan menyala. */
    private void drawForestWraith(PGraphics g, int px, int py, int alpha, boolean hurt, float anim) {
        g.noStroke();

        // Bayangan tipis di tanah
        g.fill(0, 0, 0, (int) (70 * (alpha / 255f)));
        g.rect(px - 6, py + 4, 12, 3);

        // Gerigi kabut berputar di sekeliling kaki
        int wispOffset = (int) (anim * 8f) & 3;
        g.fill(Palette.alpha(0x2A4852, (int) (100 * (alpha / 255f))));
        g.rect(px - 7 + wispOffset, py + 1, 2, 2);
        g.rect(px + 5 - wispOffset, py + 2, 2, 2);

        // Tubuh bayangan kabut ebony-teal
        int bodyCol = hurt ? 0xFFFFFFFF : 0xFF0E1822;
        g.fill(Palette.alpha(bodyCol, alpha));
        g.rect(px - 6, py - 8, 12, 10);
        g.rect(px - 5, py - 11, 10, 4);
        g.rect(px - 4, py + 2, 3, 2);
        g.rect(px + 1, py + 2, 3, 2);

        // Tudung kabut tebal
        g.fill(Palette.alpha(hurt ? 0xFFFFFFFF : 0xFF182B36, alpha));
        g.rect(px - 4, py - 7, 8, 6);
        g.rect(px - 7, py - 3, 2, 4);
        g.rect(px + 5, py - 3, 2, 4);

        // Mata membara cyan gothic
        if (state == State.TELEGRAPH) {
            g.fill(0xFFFF4D5E);
            int eyeX = facingDir < 0 ? (px - 5) : (px + 2);
            g.rect(eyeX, py - 9, 3, 2);
            g.fill(0xFFFFC2CC);
            g.rect(eyeX + (facingDir < 0 ? 0 : 1), py - 9, 1, 1);
        } else {
            g.fill(Palette.alpha(0xFF77F0E4, alpha));
            int eyeX = facingDir < 0 ? (px - 4) : (px + 2);
            g.rect(eyeX, py - 9, 2, 2);
        }
    }
}
