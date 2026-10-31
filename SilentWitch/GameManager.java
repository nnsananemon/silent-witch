import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * Titik pusat siklus game: resolusi virtual, render buffer, scene, dan input.
 *
 * Processing memanggil tick() dari draw(). Seluruh gameplay digambar ke canvas
 * 320x180 lalu diperbesar dengan skala integer agar tetap tajam.
 */
public final class GameManager {

    public static final int VIRTUAL_W = 320;
    public static final int VIRTUAL_H = 180;

    private final PApplet app;
    private final PGraphics canvas;
    private final SceneManager scenes;

    private int viewportW = -1;
    private int viewportH = -1;
    private int scale = 1;
    private int offsetX = 0;
    private int offsetY = 0;

    private float lastTime = -1f;

    public GameManager(PApplet app) {
        this.app = app;
        this.canvas = app.createGraphics(VIRTUAL_W, VIRTUAL_H);
        this.canvas.noSmooth();
        this.scenes = new SceneManager(this);
        updateViewport();
    }

    public void start() {
        scenes.switchTo(new MainMenuScene(this));
        lastTime = -1f;
    }

    public PApplet app() {
        return app;
    }

    public void showMenu() {
        scenes.switchTo(new MainMenuScene(this));
    }

    public void showGameplay() {
        scenes.switchTo(new GameplayScene(this));
    }

    /** Scene aktif; dipakai harness pengujian dan debug. */
    public Scene currentScene() {
        return scenes.current();
    }

    public void quit() {
        app.exit();
    }

    public void tick() {
        updateViewport();

        float now = app.millis() / 1000f;
        float dt = lastTime < 0f ? 0f : Math.min(0.05f, now - lastTime);
        lastTime = now;

        scenes.update(dt);

        canvas.beginDraw();
        canvas.background(Palette.INK);
        scenes.draw(canvas);
        canvas.endDraw();

        app.background(Palette.VOID);
        app.imageMode(PApplet.CORNER);
        app.image(canvas, offsetX, offsetY, VIRTUAL_W * scale, VIRTUAL_H * scale);
    }

    private void updateViewport() {
        if (app.width == viewportW && app.height == viewportH) {
            return;
        }
        viewportW = app.width;
        viewportH = app.height;
        scale = Math.max(1, Math.min(viewportW / VIRTUAL_W, viewportH / VIRTUAL_H));
        offsetX = (viewportW - VIRTUAL_W * scale) / 2;
        offsetY = (viewportH - VIRTUAL_H * scale) / 2;
    }

    public void keyPressed(char key, int keyCode) {
        scenes.keyPressed(key, keyCode);
    }

    public void keyReleased(char key, int keyCode) {
        scenes.keyReleased(key, keyCode);
    }

    public void focusLost() {
        scenes.focusLost();
    }

    public void mousePressed(float screenX, float screenY) {
        if (!insideCanvas(screenX, screenY)) {
            return;
        }
        scenes.mousePressed(toVirtualX(screenX), toVirtualY(screenY));
    }

    public void mouseMoved(float screenX, float screenY) {
        if (!insideCanvas(screenX, screenY)) {
            return;
        }
        scenes.mouseMoved(toVirtualX(screenX), toVirtualY(screenY));
    }

    private boolean insideCanvas(float screenX, float screenY) {
        return screenX >= offsetX && screenX < offsetX + VIRTUAL_W * scale
                && screenY >= offsetY && screenY < offsetY + VIRTUAL_H * scale;
    }

    private float toVirtualX(float screenX) {
        return (screenX - offsetX) / scale;
    }

    private float toVirtualY(float screenY) {
        return (screenY - offsetY) / scale;
    }
}
