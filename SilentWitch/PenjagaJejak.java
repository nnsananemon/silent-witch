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
    private final float leashRadius = 90f;
    private AttackType nextAttack = AttackType.CLEAVE;
    private float animTime = 0f;
    private int phase = 1;

    public PenjagaJejak(float x, float y) {
        super("Penjaga Jejak", x, y, 10, 1, 28f, 14f, 16f);
        this.anchorX = x;
        this.anchorY = y;
        this.boss = true;
        this.alertRadius = 70f;
        this.attackRadius = 34f;
        this.telegraphDuration = 0.70f;
        this.attackDuration = 0.22f;
        this.recoveryDuration = 0.75f;
        this.state = State.IDLE;
        this.facingDir = -1; // Sikap siaga selalu menghadap ke samping kiri (ke arah jalan masuk pemain)
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

        // Cek transisi fase saat HP <= 5 (Fase 2 lebih agresif & kuat)
        if (hp <= 5 && phase == 1) {
            phase = 2;
            damage = 2;
            speed = 36f;
            telegraphDuration = 0.55f;
            recoveryDuration = 0.52f;
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

        switch (state) {
            case IDLE:
            case PATROL:
                if (dist <= alertRadius && player.isAlive()) {
                    facingDir = dx < 0 ? -1 : 1;
                    state = State.CHASE;
                    break;
                }
                // Jika bos berada di luar pos jaganya, kembali dengan tenang ke anchor
                float toAnchorX = anchorX - x;
                float toAnchorY = anchorY - y;
                float anchorDist = (float) Math.sqrt(toAnchorX * toAnchorX + toAnchorY * toAnchorY);
                if (anchorDist > 3f) {
                    facingDir = toAnchorX < 0 ? -1 : 1;
                    moveWithCollision((toAnchorX / anchorDist) * (speed * 0.45f) * dt,
                            (toAnchorY / anchorDist) * (speed * 0.45f) * dt, map);
                } else {
                    // Berjaga di pos: default selalu menghadap samping kiri ke arah datangnya pemain
                    facingDir = dx < 0 ? -1 : (dx > 0 ? 1 : -1);
                }
                break;

            case CHASE:
                if (!player.isAlive()) {
                    state = State.IDLE;
                    break;
                }
                facingDir = dx < 0 ? -1 : 1;
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
                // Batasan wilayah: jika pemain menjauh atau bos terlalu jauh dari pos jaga, lepaskan kejaran
                float distFromAnchor = (float) Math.hypot(x - anchorX, y - anchorY);
                if (dist > alertRadius * 1.35f || distFromAnchor > leashRadius) {
                    state = State.IDLE;
                    break;
                }
                // Bergerak mendekati pemain dengan deteksi tabrakan batu
                float dirX = dx / dist;
                float dirY = dy / dist;
                moveWithCollision(dirX * speed * dt, dirY * speed * dt, map);
                break;

            case TELEGRAPH:
                stateTimer -= dt;
                if (stateTimer <= 0f) {
                    state = State.ATTACK;
                    stateTimer = attackDuration;
                    if (nextAttack == AttackType.LUNGE) {
                        // Terjangan maju cepat dengan deteksi tabrakan batu
                        moveWithCollision(facingDir * 18f, 0f, map);
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

    /**
     * Memindahkan Penjaga Jejak dengan memeriksa tabrakan tile padat (batu/pohon),
     * sehingga bos tidak tembus batu di latar.
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

        // Sprite wizard gothic: selalu menghadap ke samping (side profile, bukan ke depan layar).
        if (Art.wizardSpriteReady()) {
            int alpha = defeated
                    ? (int) (Math.max(0f, Math.min(1f, defeatedTimer)) * 255)
                    : 255;

            int frame;
            if (defeated) {
                frame = 14;
            } else if (state == State.TELEGRAPH) {
                float p = 1f - Math.max(0f, Math.min(1f, stateTimer / telegraphDuration));
                frame = p < 0.45f ? 13 : 12; // 13: cincin percikan samping, 12: api membara samping
            } else if (state == State.ATTACK) {
                frame = 11; // 11: semburan api besar ke samping
            } else if (state == State.RECOVERY) {
                frame = 14; // jeda rentan menyamping
            } else if (state == State.HURT) {
                frame = 14;
            } else {
                frame = 14; // sikap siaga menyamping menghadap kiri
            }

            // Aura fase 2 digambar di belakang sprite (disesuaikan dengan skala tubuh penyihir).
            if (phase == 2 && !defeated) {
                int pulse = ((int) (animTime * 8f) & 1);
                g.noStroke();
                g.fill(Palette.alpha(0xDE2A40, 40 + pulse * 24));
                g.ellipse(px, py - 16, 32, 42);
                g.fill(Palette.alpha(0x5DE0D2, 28));
                g.ellipse(px, py - 18, 22, 30);
            }

            // Frame 11-14 di sheet dasar sudah menghadap ke kiri.
            // Jika facingDir > 0 (menghadap ke kanan), baru dibalik.
            boolean flip = facingDir > 0;
            Art.drawWizard(g, px, py, flip, frame, alpha, hurtTimer > 0f, animTime);
            return;
        }

        if (Art.ENABLED) {
            int bob = ((int) (animTime * 5f) & 1);
            int alpha = defeated ? (int) (Math.max(0f, Math.min(1f, defeatedTimer)) * 255) : 255;
            drawShadowWarden(g, px, py + bob, alpha, hurtTimer > 0f, animTime);
            return;
        }

        int bob = ((int) (animTime * 5f) & 1);

        g.noStroke();

        if (defeated) {
            // Efek pembubaran bayangan kuno teal-ebony
            g.fill(Palette.alpha(0x0E1822, (int) (defeatedTimer * 255)));
            g.rect(px - 9, py - 14, 18, 22);
            g.fill(Palette.alpha(0x3A5868, (int) (defeatedTimer * 200)));
            g.rect(px - 6, py - 20, 12, 8);
            g.fill(Palette.alpha(0x5DE0D2, (int) (defeatedTimer * 160)));
            g.rect(px - 2, py - 8, 4, 4);
            return;
        }

        drawShadowWarden(g, px, py + bob, 255, hurtTimer > 0f, animTime);
    }

    /** Menggambar Penjaga Jejak dalam wujud gothic shadow warden bersabit raksasa. */
    private void drawShadowWarden(PGraphics g, int px, int py, int alpha, boolean hurt, float anim) {
        g.noStroke();

        if (defeated) {
            g.fill(Palette.alpha(0x0E1822, (int) (defeatedTimer * 255)));
            g.rect(px - 9, py - 14, 18, 22);
            g.fill(Palette.alpha(0x3A5868, (int) (defeatedTimer * 200)));
            g.rect(px - 6, py - 20, 12, 8);
            g.fill(Palette.alpha(0x5DE0D2, (int) (defeatedTimer * 160)));
            g.rect(px - 2, py - 8, 4, 4);
            return;
        }

        // Bayangan besar di tanah
        g.fill(0, 0, 0, (int) (95 * (alpha / 255f)));
        g.rect(px - 10, py + 6, 20, 4);

        // Aura Fase 2: kobaran api bayangan crimson-teal
        if (phase == 2) {
            int auraPulse = ((int) (anim * 8f) & 1);
            g.fill(Palette.alpha(0xDE2A40, (int) ((110 + auraPulse * 45) * (alpha / 255f))));
            g.rect(px - 11, py - 22, 22, 26);
            g.fill(Palette.alpha(0x5DE0D2, (int) (90 * (alpha / 255f))));
            g.rect(px - 9 - auraPulse, py - 24, 4, 5);
            g.rect(px + 6 + auraPulse, py - 24, 4, 5);
        }

        // Jubah Robek Penjaga Menghadap Samping (Ebony Teal Gothic)
        int bodyColor = hurt ? 0xFFFFFFFF : 0xFF0E1822;
        g.fill(Palette.alpha(bodyColor, alpha));
        // Badan profil samping
        int bodyX = facingDir < 0 ? (px - 6) : (px - 4);
        g.rect(bodyX, py - 18, 10, 20);
        // Jubah menjuntai ke belakang
        int capeX = facingDir < 0 ? px : (px - 9);
        g.rect(capeX, py - 14, 9, 16);

        // Plat Zirah Berkarat Kuno di Sisi Dada
        g.fill(Palette.alpha(hurt ? 0xFFFFFFFF : 0xFF283B46, alpha));
        int armorX = facingDir < 0 ? (px - 5) : (px - 1);
        g.rect(armorX, py - 14, 6, 8);
        g.fill(Palette.alpha(hurt ? 0xFFFFFFFF : 0xFF3D5866, alpha));
        g.rect(armorX, py - 12, 5, 2);

        // Tudung Kepala Robek Menyamping
        g.fill(Palette.alpha(hurt ? 0xFFFFFFFF : 0xFF0A121A, alpha));
        int hoodX = facingDir < 0 ? (px - 6) : (px - 3);
        g.rect(hoodX, py - 25, 9, 10);
        int peakX = facingDir < 0 ? (px - 2) : (px - 7);
        g.rect(peakX, py - 27, 6, 3);

        // Satu Mata Menyamping di Dalam Tudung
        int eyeX = facingDir < 0 ? (px - 5) : (px + 2);
        if (state == State.TELEGRAPH) {
            g.fill(0xFFFF1E2E);
            g.rect(eyeX, py - 21, 3, 2);
            g.fill(0xFFFFB2BC);
            g.rect(eyeX, py - 21, 1, 1);
        } else {
            // Mata Cyan Kuno bercahaya
            int eyeCol = phase == 2 ? 0xFFFF3848 : 0xFF6BF7E8;
            g.fill(Palette.alpha(eyeCol, alpha));
            g.rect(eyeX, py - 21, 3, 2);
        }

        // Senjata: Sabit Bayangan Raksasa
        int weaponX = facingDir < 0 ? (px - 10) : (px + 7);
        if (state == State.ATTACK) {
            // Ayunan sabit menebas melengkung
            g.fill(Palette.alpha(0xFF1E2E38, alpha));
            int swingX = facingDir < 0 ? (px - 20) : (px + 5);
            g.rect(swingX, py - 9, 16, 3);
            g.fill(Palette.alpha(0xFFDE2A40, alpha));
            g.rect(swingX, py - 16, 4, 18);
            g.fill(Palette.alpha(0xFFFF7A8A, alpha));
            g.rect(swingX + 1, py - 14, 2, 14);
        } else if (state == State.RECOVERY) {
            // Sabit menancap di tanah
            g.fill(Palette.alpha(0xFF1E2B33, alpha));
            g.rect(weaponX, py - 6, 2, 14);
            g.fill(Palette.alpha(0xFF3B5666, alpha));
            g.rect(weaponX - 2, py + 4, 6, 3);
            // Hawa lelah / titik rentan
            g.fill(Palette.alpha(0xFF5DE0D2, 160));
            g.rect(px - 2, py - 28, 1, 2);
            g.rect(px + 1, py - 27, 1, 2);
        } else {
            // Sabit dipegang tegak
            g.fill(Palette.alpha(0xFF1A262E, alpha));
            g.rect(weaponX, py - 24, 2, 26);
            g.fill(Palette.alpha(0xFF324A57, alpha));
            g.rect(weaponX - (facingDir < 0 ? 5 : 0), py - 26, 7, 5);
            g.fill(Palette.alpha(0xFF6794A8, alpha));
            g.rect(weaponX - (facingDir < 0 ? 4 : -1), py - 25, 3, 2);
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
