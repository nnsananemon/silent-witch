import java.util.List;

import processing.core.PGraphics;

/**
 * Kontrak objek yang dapat diinteraksikan pemain.
 *
 * Semua posisi berada pada koordinat dunia (bukan layar). Jarak interaksi
 * dihitung dari titik kaki pemain ke titik objek.
 */
public interface Interactable {

    float x();

    float y();

    /** Jarak maksimum agar pemain dapat berinteraksi, dalam piksel dunia. */
    float radius();

    /** Kata kerja yang tampil pada prompt, misalnya "BICARA" atau "PERIKSA". */
    String prompt();

    /** Dialog yang dibuka saat berinteraksi. */
    Dialogue createDialogue();

    /** Digambar di dunia. Objek yang sudah digambar TileMap boleh kosong. */
    void draw(PGraphics g, int cameraX, int cameraY);

    /**
     * Mengembalikan objek terdekat yang berada dalam jangkauan, atau null.
     * Objek yang lebih dekat dipilih bila beberapa berada dalam jangkauan.
     */
    static Interactable nearest(List<Interactable> items, float playerX, float playerY) {
        if (items == null) {
            return null;
        }
        Interactable best = null;
        float bestDistance = Float.MAX_VALUE;
        for (Interactable item : items) {
            float dx = item.x() - playerX;
            float dy = item.y() - playerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance <= item.radius() && distance < bestDistance) {
                bestDistance = distance;
                best = item;
            }
        }
        return best;
    }
}
