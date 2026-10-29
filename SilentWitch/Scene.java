import processing.core.PGraphics;

/**
 * Kontrak dasar sebuah scene.
 *
 * Semua koordinat berada pada resolusi virtual 320x180. Input yang diteruskan
 * ke scene juga sudah dikonversi ke koordinat virtual oleh GameManager.
 */
public abstract class Scene {

    protected final GameManager game;

    protected Scene(GameManager game) {
        this.game = game;
    }

    public void onEnter() {
        // Opsional untuk scene yang membutuhkan inisialisasi.
    }

    public void onExit() {
        // Opsional untuk scene yang perlu membersihkan state.
    }

    public abstract void update(float dt);

    public abstract void draw(PGraphics g);

    public void keyPressed(char key, int keyCode) {
        // Opsional.
    }

    public void mousePressed(float x, float y) {
        // Opsional.
    }

    public void mouseMoved(float x, float y) {
        // Opsional.
    }
}
