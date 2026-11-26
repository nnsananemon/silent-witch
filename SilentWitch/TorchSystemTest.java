import processing.core.PGraphics;
import processing.core.PImage;

public class TorchSystemTest {

    public static void main(String[] args) {
        System.out.println("=== TEST SISTEM OBOR DINDING & GENGGAM ===");

        // 1. Verifikasi aset obor termuat
        if (!Art.torchSpriteReady()) {
            throw new AssertionError("[FAIL] Sprite obor tidak siap!");
        }
        System.out.println("[PASS] 1. Sprite obor berhasil dimuat ke Art.java");

        // 2. Render dummy PGraphics
        processing.awt.PGraphicsJava2D g = new processing.awt.PGraphicsJava2D();
        g.setPrimary(false);
        g.setSize(320, 180);
        g.beginDraw();

        // Tes render Art.drawTorch
        for (float t = 0f; t < 2f; t += 0.2f) {
            Art.drawTorch(g, 46f, 56f, t, 1f, false);
            Art.drawTorch(g, 46f, 56f, t, 1f, true);
        }
        System.out.println("[PASS] 2. Art.drawTorch() berhasil dirender di berbagai frame");

        // Tes render Art.drawHeldTorch di 4 arah hadap
        for (Player.Facing facing : Player.Facing.values()) {
            Art.drawHeldTorch(g, 50, 50, facing, false, 0.5f, 255);
            Art.drawHeldTorch(g, 50, 50, facing, true, 1.2f, 255);
            Art.drawHeldTorch(g, 50, 50, facing, false, 0.5f, 120);
        }
        System.out.println("[PASS] 3. Art.drawHeldTorch() berhasil dirender di 4 arah hadap");

        // 4. Verifikasi GameplayScene di Dasar Lembah
        GameplayScene scene = new GameplayScene(null);
        scene.transitionToValleyFloor();

        Obor obor = scene.oborTebing();
        if (obor == null) {
            throw new AssertionError("[FAIL] Obor tidak ditemukan di scene!");
        }
        if (Math.abs(obor.x() - 46f) > 1f || Math.abs(obor.y() - 56f) > 1f) {
            throw new AssertionError("[FAIL] Posisi Obor tidak di dinding (46, 56): (" + obor.x() + ", " + obor.y() + ")");
        }
        System.out.println("[PASS] 4. Obor terpasang di dinding Dasar Lembah (46, 56)");

        // 5. Interaksi pengambilan obor
        scene.player().setPosition(46f, 66f); // berdiri di dekat dinding
        scene.update(0.016f);
        String prompt = scene.focusedPrompt();
        if (prompt == null || !prompt.contains("OBOR")) {
            throw new AssertionError("[FAIL] Focused prompt tidak mengarah ke obor: " + prompt);
        }

        // Tekan 'E' untuk ambil obor
        scene.keyPressed('e', 0);
        if (!scene.isTorchObtained()) {
            throw new AssertionError("[FAIL] torchObtained harusnya bernilai true setelah interaksi!");
        }
        if (!obor.isPickedUp()) {
            throw new AssertionError("[FAIL] obor.isPickedUp() harus true!");
        }
        System.out.println("[PASS] 5. Interaksi pengambilan obor sukses (torchObtained = true)");

        // 6. Draw loop GameplayScene tanpa exception
        scene.draw(g);
        System.out.println("[PASS] 6. Render GameplayScene dengan obor sukses tanpa error");

        System.out.println(">>> SEMUA TES SISTEM OBOR LULUS 100%! <<<");
    }
}
