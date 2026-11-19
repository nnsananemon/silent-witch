import processing.core.PApplet;
import processing.core.PGraphics;

public class TestNpcTalkIntegration {
    public static void main(String[] args) {
        System.out.println("=== NPC TALK & DIRECTION ANIMATION TEST ===");

        // Test 1: Npc (Librarian Elder) Direction calculation and animation
        Npc librarian = new Npc(200f, 176f, "KAKEK PUSTAKAWAN", new Dialogue("KAKEK PUSTAKAWAN", "Halo"));
        if (librarian.dir() != Npc.DIR_DOWN) {
            throw new AssertionError("Librarian default direction should be DIR_DOWN");
        }

        // Player to the left (e.g. at 170, 176)
        librarian.lookAt(170f, 176f);
        if (!librarian.isTurning()) {
            throw new AssertionError("Librarian should start turning when player is to the left");
        }
        if (librarian.bob() >= 0f) {
            // progress starts at 0, bob is 0 initially or negative during hop
        }

        // Halfway through turn
        librarian.update(Npc.TURN_DURATION * 0.55f);
        if (librarian.dir() != Npc.DIR_LEFT) {
            throw new AssertionError("Librarian should have turned to DIR_LEFT, got: " + librarian.dir());
        }
        if (librarian.bob() >= 0f) {
            throw new AssertionError("Librarian should have negative bob (hop up) mid-turn, got: " + librarian.bob());
        }

        // Finish turn
        librarian.update(Npc.TURN_DURATION);
        if (librarian.isTurning()) {
            throw new AssertionError("Librarian turn timer should have expired");
        }
        if (librarian.dir() != Npc.DIR_LEFT) {
            throw new AssertionError("Librarian direction should remain DIR_LEFT");
        }

        // Look at player above (e.g. at 200, 150)
        librarian.lookAt(200f, 150f);
        librarian.update(Npc.TURN_DURATION);
        if (librarian.dir() != Npc.DIR_UP) {
            throw new AssertionError("Librarian should face DIR_UP when player is above");
        }

        // Look at player to the right (e.g. at 230, 176)
        librarian.lookAt(230f, 176f);
        librarian.update(Npc.TURN_DURATION);
        if (librarian.dir() != Npc.DIR_RIGHT) {
            throw new AssertionError("Librarian should face DIR_RIGHT when player is to the right");
        }

        // Go home
        librarian.goHome();
        librarian.update(Npc.TURN_DURATION);
        if (librarian.dir() != Npc.DIR_DOWN) {
            throw new AssertionError("Librarian should face DIR_DOWN after goHome");
        }
        System.out.println("[PASS] 1. Librarian 4-direction turning and bob timing validated");

        // Test 2: TetuaDesa (Boy) and SaksiDesa (Girl)
        TetuaDesa boy = new TetuaDesa(160f, 130f);
        boy.lookAt(140f, 130f); // player left
        boy.update(TetuaDesa.TURN_DURATION);
        if (boy.dir() != TetuaDesa.DIR_LEFT) {
            throw new AssertionError("Boy should face DIR_LEFT");
        }
        boy.goHome();
        boy.update(TetuaDesa.TURN_DURATION);
        if (boy.dir() != TetuaDesa.DIR_DOWN) {
            throw new AssertionError("Boy should return to DIR_DOWN");
        }

        SaksiDesa girl = new SaksiDesa(312f, 86f); // default homeDir facing right towards the well
        if (girl.dir() != SaksiDesa.DIR_RIGHT) {
            throw new AssertionError("Girl default direction should be DIR_RIGHT facing the well");
        }
        girl.lookAt(290f, 86f); // player left
        girl.update(SaksiDesa.TURN_DURATION);
        if (girl.dir() != SaksiDesa.DIR_LEFT) {
            throw new AssertionError("Girl should face DIR_LEFT when player is to the left");
        }
        girl.goHome();
        girl.update(SaksiDesa.TURN_DURATION);
        if (girl.dir() != SaksiDesa.DIR_RIGHT) {
            throw new AssertionError("Girl should return to DIR_RIGHT (facing well)");
        }
        System.out.println("[PASS] 2. Villager boy and girl turning logic validated");

        // Test 3: GameplayScene interaction triggers turning and goHome on dialog close
        GameplayScene scene = new GameplayScene(null);
        scene.transitionToHallFromVillage();

        // Place player directly to the left of the librarian (librarian is at 200, 176)
        scene.player().setPosition(184f, 176f);
        scene.update(0.016f);

        // Press 'E'
        scene.keyPressed('e', 69);
        if (!scene.isDialogueOpen()) {
            throw new AssertionError("Dialogue should be open after pressing E near librarian");
        }
        // Librarian should start turning towards the left
        scene.update(0.12f);
        if (scene.npc().dir() != Npc.DIR_LEFT) {
            throw new AssertionError("Librarian should face DIR_LEFT when talking to player on the left, got: " + scene.npc().dir());
        }

        // Advance through dialogue until closed
        while (scene.isDialogueOpen()) {
            scene.update(0.3f); // let interact cooldown clear
            scene.keyPressed('e', 69);
        }

        // After dialog closes, librarian should trigger goHome
        scene.update(0.25f);
        if (scene.npc().dir() != Npc.DIR_DOWN) {
            throw new AssertionError("Librarian should return to DIR_DOWN after dialogue closes");
        }
        System.out.println("[PASS] 3. GameplayScene talk and return home interaction flow validated");

        System.out.println(">>> ALL NPC TALK TESTS PASSED! <<<");
    }
}
