import processing.core.PGraphics;

/**
 * Mini-boss di Hollow Woods: Penjaga Jejak (Shadow Trail Warden).
 *
 * Sesuai PRD Bab 2:
 * "Musuh bayangan menjaga pecahan/penanda simbol yang diperlukan untuk
 * mengaktifkan jalur turun ke lembah. Serangannya mengajarkan pola dasar;
 * kemenangan membuka rute dan memberi Sigil Jejak secara pasti tanpa RNG."
 *
 * Ritme pertempuran:
 * Telegraph (peringatan area merah jelas di tanah) -> Serangan sapuan/tusukan ->
 * Recovery (senjata tertancap di tanah, jeda rentan untuk diserang pemain).
 * Memiliki 2 fase pola serangan (Fase 1: Sapuan sabit bayangan; Fase 2: Sapuan & Terjangan lurus).
 */
public final class PenjagaJejak extends Enemy {

    public enum AttackType {
        CLEAVE, // Sapuan sabit bayangan lebar
        LUNGE   // Terjangan lurus bayangan
    }

    private final float anchorX;
    private final float anchorY;
    private AttackType nextAttack = AttackType.CLEAVE;
    private float animTime = 0f;
    private int phase = 1;

    public PenjagaJejak(float x, float y) {
        super("Penjaga Jejak", x, y, 10, 1, 24f, 18f, 24f);
        this.anchorX = x;
        this.anchorY = y;
        this.boss = true;
        this.alertRadius = 95f;
        this.attackRadius = 34f;
        this.telegraphDuration = 0.75f;
        this.attackDuration = 0.22f;
        this.recoveryDuration = 0.85f;
        this.state = State.IDLE;
    }

    public int phase() {
        return phase;
    }

    @Override
    public void update(float dt, Player player, TileMap map) {
        if (dt <= 0f) {
            return;
        }
        animTime += dt;

        // Cek transisi fase saat HP <= 5
        if (hp <= 5 && phase == 1) {
            phase = 2;
            damage = 2;
            telegraphDuration = 0.62f;
            recoveryDuration = 0.68f;
        }

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

        if (dx != 0f && state != State.ATTACK) {
            facingDir = dx < 0 ? -1 : 1;
        }

        switch (state) {
            case IDLE:
            case PATROL:
                if (dist <= alertRadius && player.isAlive()) {
                    state = State.CHASE;
                }
                break;

            case CHASE:
                if (!player.isAlive()) {
                    state = State.IDLE;
                    break;
                }
                if (dist <= attackRadius) {
                    state = State.TELEGRAPH;
                    stateTimer = telegraphDuration;
                    if (phase == 2) {
                        // Di fase 2 selingi sapuan dan terjangan
                        nextAttack = (nextAttack == AttackType.CLEAVE) ? AttackType.LUNGE : AttackType.CLEAVE;
                    } else {
                        nextAttack = AttackType.CLEAVE;
                    }
                    break;
                }
                // Bergerak mendekati pemain
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
                    if (nextAttack == AttackType.LUNGE) {
                        // Terjangan maju cepat
                        x += facingDir * 18f;
                    }
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
                    state = dist <= alertRadius ? State.CHASE : State.IDLE;
                }
                break;

            case HURT:
                stateTimer -= dt;
                if (stateTimer <= 0f) {
                    state = State.RECOVERY;
                    stateTimer = recoveryDuration * 0.6f;
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
        if (nextAttack == AttackType.LUNGE) {
            float hitW = 28f;
            float hitH = 16f;
            float hitX = facingDir < 0 ? (x - hitW) : x;
            float hitY = y - hitH / 2f;
            return new float[]{ hitX, hitY, hitW, hitH };
        } else {
            // Cleave lebar
            float hitW = 24f;
            float hitH = 26f;
            float hitX = facingDir < 0 ? (x - hitW) : x;
            float hitY = y - hitH / 2f;
            return new float[]{ hitX, hitY, hitW, hitH };
        }
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
        if (nextAttack == AttackType.LUNGE) {
            // Jalur garis merah telegraf untuk terjangan lurus
            g.fill(Palette.alpha(0xD92222, (int) (60 + progress * 130)));
            int laneW = Math.round(36 * progress);
            int laneX = facingDir < 0 ? (px - laneW) : px;
            g.rect(laneX, py - 4, laneW, 8);
            // Garis pembatas bahaya
            g.fill(0xFFFF3333);
            g.rect(laneX, py - 5, laneW, 1);
            g.rect(laneX, py + 4, laneW, 1);
        } else {
            // Busur sapuan sabit merah di lantai
            g.fill(Palette.alpha(0xD92222, (int) (60 + progress * 130)));
            int arcW = Math.round(26 * progress);
            int arcX = facingDir < 0 ? (px - arcW) : px;
            g.rect(arcX, py - 12, arcW, 24);
            // Batas tebing bahaya
            g.fill(0xFFFF4444);
            int edgeX = facingDir < 0 ? (arcX) : (arcX + arcW - 1);
            g.rect(edgeX, py - 12, 1, 24);
        }

        // Tanda peringatan bos di atas kepala
        g.fill(0xFFFF2222);
        g.rect(px - 1, py - 28, 2, 6);
        g.rect(px - 1, py - 20, 2, 2);
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        if (defeated && defeatedTimer <= 0f) {
            return;
        }
        int px = Math.round(x - cameraX);
        int py = Math.round(y - cameraY);

        if (Art.ENABLED) {
            int bob = ((int) (animTime * 5f) & 1);
            int alpha = defeated ? (int) (Math.max(0f, Math.min(1f, defeatedTimer)) * 255) : 255;
            if (phase == 2 && !defeated) {
                g.noStroke();
                g.fill(160, 30, 50, 70 + ((int) (animTime * 8f) & 1) * 30);
                g.rect(px - 16, py - 42, 32, 44);
            }
            Art.drawEnemy(g, px, py + bob, "orc", hurtTimer > 0f, alpha, facingDir > 0, animTime);
            return;
        }

        int bob = ((int) (animTime * 5f) & 1);

        g.noStroke();

        if (defeated) {
            // Efek pembubaran bayangan kuno
            g.fill(Palette.alpha(0x1B1424, (int) (defeatedTimer * 255)));
            g.rect(px - 9, py - 14, 18, 22);
            g.fill(Palette.alpha(0x8B3A62, (int) (defeatedTimer * 200)));
            g.rect(px - 6, py - 20, 12, 8);
            g.fill(Palette.alpha(0xE8B86D, (int) (defeatedTimer * 160)));
            g.rect(px - 2, py - 8, 4, 4);
            return;
        }

        // Bayangan besar di tanah
        g.fill(Palette.alpha(0x000000, 90));
        g.rect(px - 9, py + 6, 18, 4);

        // Aura Fase 2 (berkobar merah keunguan di sekeliling bos)
        if (phase == 2) {
            int auraPulse = ((int) (animTime * 8f) & 1);
            g.fill(Palette.alpha(0x8A2234, 120 + auraPulse * 40));
            g.rect(px - 10, py - 20 + bob, 20, 24);
            g.fill(Palette.alpha(0xC9384E, 100));
            g.rect(px - 8 - auraPulse, py - 22, 3, 4);
            g.rect(px + 6 + auraPulse, py - 22, 3, 4);
        }

        // Tubuh / Jubah Robek Penjaga
        int bodyColor = (hurtTimer > 0f) ? 0xFFFFFFFF : 0xFF140F1D;
        g.fill(bodyColor);
        g.rect(px - 7, py - 16 + bob, 14, 18);
        g.rect(px - 8, py - 10 + bob, 16, 12);

        // Plat Besi Berkarat Kuno di Dada
        g.fill(0xFF383244);
        g.rect(px - 5, py - 12 + bob, 10, 8);
        g.fill(0xFF4C445C);
        g.rect(px - 4, py - 10 + bob, 8, 2);
        g.rect(px - 2, py - 7 + bob, 4, 3);

        // Tudung Kepala
        g.fill(0xFF100C17);
        g.rect(px - 6, py - 22 + bob, 12, 8);
        g.rect(px - 5, py - 24 + bob, 10, 3);

        // Mata Membara Merah di Dalam Tudung
        if (state == State.TELEGRAPH) {
            g.fill(0xFFFF1A1A);
            int eyeX = facingDir < 0 ? (px - 4) : px;
            g.rect(eyeX, py - 19 + bob, 3, 2);
            g.rect(eyeX + 3, py - 19 + bob, 2, 2);
            g.fill(0xFFFFAAAA);
            g.rect(eyeX + 1, py - 19 + bob, 1, 1);
        } else {
            g.fill(0xFFD62828);
            int eyeX = facingDir < 0 ? (px - 4) : px;
            g.rect(eyeX, py - 19 + bob, 2, 2);
            g.rect(eyeX + 3, py - 19 + bob, 2, 2);
        }

        // Senjata: Sabit Bayangan Raksasa
        int weaponX = facingDir < 0 ? (px - 9) : (px + 6);
        if (state == State.ATTACK) {
            // Ayunan sabit menebas ke depan
            g.fill(0xFF4A3860);
            int swingX = facingDir < 0 ? (px - 18) : (px + 4);
            g.rect(swingX, py - 8, 14, 3);
            g.fill(0xFFD94862);
            g.rect(swingX, py - 14, 3, 16);
            g.fill(0xFFFF7A8A);
            g.rect(swingX + 1, py - 12, 2, 12);
        } else if (state == State.RECOVERY) {
            // Sabit menancap di tanah, bos membungkuk lelah
            g.fill(0xFF2C2436);
            g.rect(weaponX, py - 6, 2, 14);
            g.fill(0xFF4A3860);
            g.rect(weaponX - 2, py + 4, 6, 3);
            // Asap lelah / celah serang
            g.fill(Palette.alpha(Palette.ACCENT, 150));
            g.rect(px - 2, py - 26, 1, 2);
            g.rect(px + 1, py - 25, 1, 2);
        } else {
            // Sabit dipegang tegak
            g.fill(0xFF2C2436);
            g.rect(weaponX, py - 22 + bob, 2, 24);
            g.fill(0xFF5A4476);
            g.rect(weaponX - (facingDir < 0 ? 4 : 0), py - 24 + bob, 6, 4);
            g.fill(0xFF9070B0);
            g.rect(weaponX - (facingDir < 0 ? 3 : -1), py - 23 + bob, 2, 2);
        }
    }

    @Override
    public void onDefeated(GameplayScene scene) {
        if (scene != null) {
            scene.setWorldFlag("penjaga_jejak_defeated", true);
            scene.setWorldFlag("sigil_jejak_obtained", true);
            if (scene.gerbangHutan() != null) {
                scene.gerbangHutan().clearFog();
            }
            if (scene.quest() != null) {
                scene.quest().setNotification("Sigil Jejak diperoleh! Kabut gerbang timur buyar.");
            }
        }
    }
}
