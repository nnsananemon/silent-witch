import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * Utilitas preview: merender MainMenuScene ke PNG tanpa membuka jendela,
 * lalu menyimpannya ke folder kerja agar layout bisa diperiksa.
 */
public class MenuPreview {

    public static void main(String[] args) {
        PApplet app = new PApplet();
        PGraphics g = new processing.awt.PGraphicsJava2D();
        g.setParent(app);
        g.setPrimary(false);
        g.setSize(320, 180);
        g.noSmooth();

        MainMenuScene menu = new MainMenuScene(null);
        float t = args.length > 1 ? Float.parseFloat(args[1]) : 0.6f;

        if (args.length > 2 && !args[2].isEmpty()) {
            menu.mousePressed(20, 98);
            for (char c : args[2].toCharArray()) {
                menu.keyPressed(c, 0);
            }
            menu.mousePressed(8, 30);
        }

        g.beginDraw();
        menu.update(t);
        menu.draw(g);
        g.endDraw();

        String out = args.length > 0 ? args[0] : "menu_preview.png";
        g.save(out);
        System.out.println("Preview disimpan: " + out);
    }
}
