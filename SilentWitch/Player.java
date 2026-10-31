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
    private static final float HITBOX_W = 10f;
    private static final float HITBOX_H = 6f;
    private static final float MAX_STEP = 4f;

    private final TileMap map;
    private final List<float[]> blockers = new ArrayList<float[]>();
    private float x;
    private float y;
    private Facing facing = Facing.DOWN;
    private boolean moving = false;
    private float animTime = 0f;

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

    public float x() {
        return x;
    }

    public float y() {
        return y;
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

    public Facing facing() {
        return facing;
    }

    public boolean isMoving() {
        return moving;
    }

    /**
     * Memperbarui pemain berdasarkan arah input (-1, 0, 1). Diagonal
     * dinormalisasi agar kecepatan tidak bertambah.
     */
    public void update(float dt, float inputX, float inputY) {
        if (dt <= 0f) {
            return;
        }
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

    public void draw(PGraphics g, int cameraX, int cameraY) {
        int px = Math.round(x - cameraX);
        int py = Math.round(y - cameraY);
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
    }
}
