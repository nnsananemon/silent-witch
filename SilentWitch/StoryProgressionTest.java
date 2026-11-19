import java.io.File;

/**
 * Test runner komprehensif untuk memverifikasi alur cerita, transisi boundary otomatis,
 * simulasi input keyPressed(), dan save/load file ke disk.
 */
public class StoryProgressionTest {

    public static void main(String[] args) {
        System.out.println("=== SILENT WITCH: ADVANCED STORY & PROGRESSION TEST ===");

        // --- Bagian 1: Alur Standar ---
        GameplayScene scene = new GameplayScene(null);
        assertArea(scene, "ashen_village", "Test 1: New game di Ashen Village");
        if (scene.gerbangDesa().canProceed()) {
            throw new AssertionError("Gerbang Desa harus terkunci di awal permainan");
        }
        System.out.println("[PASS] 1. New Game mulai di Ashen Village (Prolog)");

        // Simulasi interaksi keyPressed 'e' pada papan pengumuman
        scene.papanPengumuman().createDialogue();
        scene.gerbangDesa().updateCondition(scene.papanPengumuman().isExamined(), scene.tetuaDesa().hasSpoken());
        if (scene.gerbangDesa().canProceed()) {
            throw new AssertionError("Gerbang belum boleh terbuka!");
        }

        // Simulasi interaksi dengan tetua desa
        scene.tetuaDesa().createDialogue();
        scene.gerbangDesa().updateCondition(scene.papanPengumuman().isExamined(), scene.tetuaDesa().hasSpoken());
        if (!scene.gerbangDesa().canProceed()) {
            throw new AssertionError("Gerbang harus terbuka setelah bicara dengan tetua!");
        }

        // Boundary auto-transition dari Village ke Manor Hall
        scene.player().setPosition(590f, 136f);
        scene.update(0.016f);
        assertArea(scene, "the_silent_manor_hall", "Test 2: Auto-transition Village -> Manor Hall");
        System.out.println("[PASS] 2. Auto-transition Ashen Village -> Manor Hall sukses");

        // Bab 1: Kumpulkan bukti
        scene.investigation().discover("clue_meja_catatan");
        scene.investigation().discover("clue_rak_harian");
        scene.investigation().discover("clue_jendela_goresan");
        scene.investigation().discover("clue_penjaga_kesaksian");
        if (scene.investigation().discoveredCount() != 4) {
            throw new AssertionError("Bukti tidak lengkap!");
        }
        System.out.println("[PASS] 3. Bab 1: 4 Bukti Aula lengkap");

        // Bab 2: Hollow Woods
        scene.transitionToHollowWoods();
        assertArea(scene, "hollow_woods", "Hollow Woods");
        // Boundary check kembali ke hall
        scene.player().setPosition(5f, 72f);
        scene.update(0.016f);
        assertArea(scene, "the_silent_manor_hall", "Back to hall from woods");
        scene.transitionToHollowWoods();
        System.out.println("[PASS] 4. Bab 2: Hollow Woods bolak-balik aman");

        // Bab 3: Lembah (Cliff -> Floor -> Ruins)
        scene.transitionToValleyCliffPath();
        assertArea(scene, "valley_cliff_path", "Valley Cliff");
        scene.transitionToValleyFloor();
        assertArea(scene, "valley_floor", "Valley Floor");
        scene.transitionToValleyRuins();
        assertArea(scene, "valley_ruins", "Valley Ruins");

        // Boundary auto-transition dari Ruins ke Chapel
        scene.player().setPosition(612f, 72f);
        scene.update(0.016f);
        assertArea(scene, "chapel_of_whispers", "Test 5: Auto-transition Ruins -> Chapel");
        System.out.println("[PASS] 5. Bab 3 -> Bab 4: Ruins -> Chapel of Whispers sukses");

        // Bab 4: Chapel of Whispers
        if (scene.gerbangKapel().isUnlocked()) {
            throw new AssertionError("Gerbang kapel harusnya terkunci!");
        }
        // Coba jalan ke gerbang kapel saat masih terkunci -> tidak boleh auto-transisi
        scene.player().setPosition(625f, 72f);
        scene.update(0.016f);
        assertArea(scene, "chapel_of_whispers", "Chapel harus tetap aktif");

        // Buka via altar
        scene.gerbangKapel().unlock();
        scene.mapChapel().setGateOpen(true);
        // Sekarang jalan ke gerbang kapel -> auto-transition ke Archive
        scene.player().setPosition(625f, 72f);
        scene.update(0.016f);
        assertArea(scene, "underground_archive", "Test 6: Auto-transition Chapel -> Archive");
        System.out.println("[PASS] 6. Bab 4 -> Bab 5: Chapel -> Underground Archive sukses");

        // Bab 5: Underground Archive
        // Auto-transition dari Archive ke Manor Hall via secret shortcut
        scene.player().setPosition(605f, 72f);
        scene.update(0.016f);
        assertArea(scene, "the_silent_manor_hall", "Test 7: Auto-transition Archive -> Manor Hall");
        if (!scene.isExpeditionCompleted()) {
            throw new AssertionError("expeditionCompleted harus true!");
        }
        System.out.println("[PASS] 7. Bab 5 -> Bab 6: Shortcut Archive -> Manor Hall sukses (expedition completed)");

        // Bab 6 -> Bab 7: Buka Kotak Besi & Masuk East Wing
        Puzzle.ValidationResult vres = scene.puzzle().validate(new int[]{2, 4, 9});
        if (!vres.isSuccess()) {
            throw new AssertionError("Puzzle gagal dipecahkan!");
        }
        scene.gerbangTimur().unlock();
        scene.player().setPosition(390f, 176f);
        scene.update(0.016f);
        assertArea(scene, "the_silent_manor_east_wing", "Test 8: Auto-transition Hall -> East Wing");
        System.out.println("[PASS] 8. Bab 6 -> Bab 7: Kotak Besi 2-4-9 terbuka, masuk Sayap Timur");

        // Save ke disk menggunakan SaveManager
        File tempFile = new File(System.getProperty("java.io.tmpdir"), "silent_witch_test_save.json");
        SaveManager sm = new SaveManager(tempFile);
        SaveManager.SaveResult sres = scene.saveTo(sm);
        if (!sres.isSuccess()) {
            throw new AssertionError("Simpan ke disk gagal: " + sres.errorMessage());
        }

        SaveManager.LoadResult lres = sm.load();
        if (!lres.isSuccess()) {
            throw new AssertionError("Load dari disk gagal: " + lres.errorMessage());
        }

        GameplayScene reloaded = new GameplayScene(null, lres.data());
        assertArea(reloaded, "the_silent_manor_east_wing", "Reloaded area Sayap Timur");
        if (!reloaded.isExpeditionCompleted()) {
            throw new AssertionError("Reloaded scene harus memiliki expeditionCompleted true!");
        }
        if (reloaded.investigation().discoveredCount() != 4) {
            throw new AssertionError("Reloaded scene harus memiliki 4 bukti!");
        }
        if (!reloaded.puzzle().isSolved()) {
            throw new AssertionError("Reloaded puzzle harus tetap solved!");
        }
        System.out.println("[PASS] 9. Save & Load file ke disk sukses dan konsisten 100%");

        // Bersihkan temp save test
        sm.deleteSave();

        System.out.println("\n>>> SEMUA VERIFIKASI PROGRESSION LULUS DENGAN SEMPURNA! <<<");
    }

    private static void assertArea(GameplayScene scene, String expected, String testName) {
        if (!expected.equals(scene.currentAreaId())) {
            throw new AssertionError(testName + " GAGAL: Ekspektasi " + expected + ", aktual " + scene.currentAreaId());
        }
    }
}
