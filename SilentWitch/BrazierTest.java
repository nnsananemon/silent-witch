import processing.core.PApplet;
import processing.core.PGraphics;

public class BrazierTest {
    public static void main(String[] args) {
        System.out.println("=== TEST TILE-BASED BRAZIER & REST POINT ===");

        // Inisialisasi PGraphics headless
        processing.awt.PGraphicsJava2D g = new processing.awt.PGraphicsJava2D();
        g.setPrimary(false);
        g.setSize(640, 256);
        g.beginDraw();

        // 1. Verifikasi Art.brazierSpriteReady()
        if (!Art.brazierSpriteReady()) {
            throw new AssertionError("Art.brazierSpriteReady() harus true!");
        }
        System.out.println("[PASS] 1. brazier_anim.png berhasil dimuat ke Art.java");

        // 2. Render brazier di berbagai frame waktu
        for (float t = 0f; t < 2f; t += 0.25f) {
            Art.drawBrazier(g, 100, 100, t);
        }
        System.out.println("[PASS] 2. Art.drawBrazier() berhasil dirender di beberapa time frame");

        // 3. Verifikasi RestPoint styles
        RestPoint rpBrazier = new RestPoint(328f, 128f, RestPoint.Style.BRAZIER);
        if (rpBrazier.style() != RestPoint.Style.BRAZIER) {
            throw new AssertionError("RestPoint style harus BRAZIER");
        }
        rpBrazier.draw(g, 0, 0);

        RestPoint rpCandle = new RestPoint(488f, 136f, RestPoint.Style.CANDLE);
        if (rpCandle.style() != RestPoint.Style.CANDLE) {
            throw new AssertionError("RestPoint style harus CANDLE");
        }
        rpCandle.draw(g, 0, 0);

        RestPoint rpCampfire = new RestPoint(100f, 100f, RestPoint.Style.CAMPFIRE);
        rpCampfire.draw(g, 0, 0);
        System.out.println("[PASS] 3. Semua varian RestPoint (BRAZIER, CANDLE, CAMPFIRE) dapat dirender tanpa error");

        // 4. Geometri blocker: brazier menutup api + tapak, lilin tidak berlebihan
        float[] bb = rpBrazier.blockerRect();
        if (bb[2] != 16f || bb[3] != 12f || bb[1] != 128f - 4f) {
            throw new AssertionError("Blocker BRAZIER harus 16x12 di (x, y-4)");
        }
        float[] cb = rpCandle.blockerRect();
        if (cb[2] != 10f || cb[3] != 12f) {
            throw new AssertionError("Blocker CANDLE harus 10x12 (tidak 30x20 lama)");
        }
        System.out.println("[PASS] 4. Geometri blocker BRAZIER (16x12) & CANDLE (10x12) sesuai");

        // 5. Verifikasi gameplay scene di semua area cave
        GameplayScene scene = new GameplayScene(null);
        scene.setupArea("valley_floor");
        if (!"valley_floor".equals(scene.currentAreaId())) {
            throw new AssertionError("Area harus valley_floor");
        }
        scene.draw(g);

        scene.setupArea("valley_ruins");
        if (!"valley_ruins".equals(scene.currentAreaId())) {
            throw new AssertionError("Area harus valley_ruins");
        }
        scene.draw(g);

        scene.setupArea("underground_archive");
        if (!"underground_archive".equals(scene.currentAreaId())) {
            throw new AssertionError("Area harus underground_archive");
        }
        scene.draw(g);
        System.out.println("[PASS] 5. GameplayScene valley_floor, valley_ruins & archive sukses dirender");

        // 6. Collision chapel: tembok, bangku, altar solid; rune & brazier lewat.
        TileMap chapel = TileMap.chapelWhispers();
        if (!chapel.isSolid(0, 0) || !chapel.isSolid(3, 2)
                || !chapel.isSolid(5, 9) || !chapel.isSolid(30, 4)) {
            throw new AssertionError("Tembok, bangku, dan altar kapel harus solid");
        }
        if (chapel.isSolid(21, 8) || chapel.isSolid(30, 8)) {
            throw new AssertionError("Rune dan brazier kapel harus walkable");
        }
        for (int c = 2; c < 38; c++) {
            if (chapel.isSolid(c, 5)) {
                throw new AssertionError("Koridor utama baris 5 harus bebas");
            }
        }
        scene.setupArea("chapel_of_whispers");
        if (!"chapel_of_whispers".equals(scene.currentAreaId())) {
            throw new AssertionError("Area harus chapel_of_whispers");
        }
        scene.draw(g);
        System.out.println("[PASS] 6. Collision chapel: tembok/bangku/altar solid, koridor bebas");

        g.endDraw();
        System.out.println(">>> SEMUA TES BRAZIER LULUS 100%! <<<");
    }
}
