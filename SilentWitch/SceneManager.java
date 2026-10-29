import processing.core.PGraphics;

/** Menyimpan scene aktif dan meneruskan siklus hidup serta input. */
public final class SceneManager {

    private final GameManager game;
    private Scene current;

    public SceneManager(GameManager game) {
        this.game = game;
    }

    public Scene current() {
        return current;
    }

    public void switchTo(Scene next) {
        if (next == null) {
            throw new IllegalArgumentException("Scene tujuan tidak boleh null.");
        }
        if (current != null) {
            current.onExit();
        }
        current = next;
        current.onEnter();
    }

    public void update(float dt) {
        if (current != null) {
            current.update(dt);
        }
    }

    public void draw(PGraphics g) {
        if (current != null) {
            current.draw(g);
        }
    }

    public void keyPressed(char key, int keyCode) {
        if (current != null) {
            current.keyPressed(key, keyCode);
        }
    }

    public void mousePressed(float x, float y) {
        if (current != null) {
            current.mousePressed(x, y);
        }
    }

    public void mouseMoved(float x, float y) {
        if (current != null) {
            current.mouseMoved(x, y);
        }
    }
}
