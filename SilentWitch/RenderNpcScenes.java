import processing.core.PApplet;
import processing.core.PGraphics;

public class RenderNpcScenes {
    public static void main(String[] args) {
        PApplet app = new PApplet();
        PGraphics g = new processing.awt.PGraphicsJava2D();
        g.setParent(app);
        g.setPrimary(false);
        g.setSize(GameManager.VIRTUAL_W, GameManager.VIRTUAL_H);

        // 1. Render Ashen Village with Boy & Girl
        GameplayScene sceneVillage = new GameplayScene(null);
        sceneVillage.player().setPosition(175f, 130f);
        sceneVillage.player().setFacing(Player.Facing.LEFT);
        sceneVillage.tetuaDesa().lookAt(175f, 130f); // boy looks right towards player
        sceneVillage.saksiDesa().lookAt(175f, 130f); // girl looks left towards player
        sceneVillage.update(0.12f);

        g.beginDraw();
        sceneVillage.draw(g);
        g.endDraw();
        g.save("C:/Users/runneradmin/AppData/Local/Temp/2/opencode/preview_village_npcs.png");
        System.out.println("Saved preview_village_npcs.png");

        // 2. Render Manor Hall with Librarian Elder
        GameplayScene sceneHall = new GameplayScene(null);
        sceneHall.transitionToHallFromVillage();
        sceneHall.player().setPosition(184f, 176f);
        sceneHall.player().setFacing(Player.Facing.RIGHT);
        sceneHall.npc().lookAt(184f, 176f); // elder looks left towards player
        sceneHall.update(0.12f);

        g.beginDraw();
        sceneHall.draw(g);
        g.endDraw();
        g.save("C:/Users/runneradmin/AppData/Local/Temp/2/opencode/preview_manor_librarian.png");
        System.out.println("Saved preview_manor_librarian.png");
    }
}
