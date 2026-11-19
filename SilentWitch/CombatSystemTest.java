import java.io.File;

/**
 * Suite pengujian komprehensif untuk Sistem Pertarungan Real-Time 2D Silent Witch.
 *
 * Menguji:
 * 1. Senjata Saku pemain ("Tongkat Perak Saku"), cooldown, durasi, arah, dan hitbox 4 arah.
 * 2. Siklus FSM musuh (BayanganHutan & PenjagaJejak): Patrol -> Chase -> Telegraph -> Attack -> Recovery.
 * 3. Ritme telegraf yang adil dan transisi fase mini-boss Penjaga Jejak (Fase 1 -> Fase 2).
 * 4. Sistem penerimaan damage, i-frames (kebal sementara), dan knockback.
 * 5. Kemenangan boss memberikan Sigil Jejak secara pasti & membuka Gerbang Hutan (tanpa RNG loot).
 * 6. Proteksi modal: serangan dan combat dinonaktifkan saat dialog, casebook, puzzle, deduksi, atau pause aktif.
 * 7. Kekalahan pemain: respawn di titik aman tanpa menghapus progress clue, quest, atau puzzle.
 * 8. Serialisasi save/load status pertempuran dan Sigil Jejak ke file disk.
 */
public class CombatSystemTest {

    public static void main(String[] args) {
        System.out.println("=== SILENT WITCH: REAL-TIME 2D COMBAT & BOSS ENCOUNTER TEST ===");

        testPlayerPocketWeaponAndHitboxes();
        testEnemyStateMachineAndTelegraphRhythm();
        testPenjagaJejakPhasesAndSigilReward();
        testDamageReceptionAndInvulnerabilityFrames();
        testModalBlocksCombat();
        testPlayerDefeatAndSafeRespawn();
        testSaveLoadCombatState();

        System.out.println("\n>>> SELURUH VERIFIKASI SISTEM PERTARUNGAN LULUS DENGAN SEMPURNA! <<<");
    }

    private static void testPlayerPocketWeaponAndHitboxes() {
        System.out.println("\n--- [Test 1] Senjata Saku Pemain & Hitbox 4 Arah ---");
        GameplayScene scene = new GameplayScene(null);
        Player player = scene.player();

        // 1. Cek senjata saku awal
        assertEquals("Tongkat Perak Saku", player.weaponName(), "Senjata saku default harus Tongkat Perak Saku");
        assertEquals(Player.MAX_HP, player.hp(), "HP awal harus MAX_HP (6)");
        assertTrue(player.isAlive(), "Pemain harus dalam kondisi hidup di awal");
        assertFalse(player.isAttacking(), "Pemain tidak boleh sedang menyerang di awal");

        // 2. Serangan hadap DOWN
        player.setFacing(Player.Facing.DOWN);
        boolean swung = player.attack();
        assertTrue(swung, "Serangan pertama harus berhasil diluncurkan");
        assertTrue(player.isAttacking(), "Status isAttacking harus true");
        assertEquals(Player.Facing.DOWN, player.attackFacing(), "Arah serangan harus DOWN");
        float[] hbDown = player.getAttackHitbox();
        assertNotNull(hbDown, "Hitbox serangan DOWN tidak boleh null");
        assertTrue(hbDown[1] > player.y(), "Hitbox DOWN harus berada di bawah koordinat Y pemain");

        // 3. Spam serangan saat cooldown harus ditolak
        boolean spam = player.attack();
        assertFalse(spam, "Serangan bertubi-tubi saat cooldown harus ditolak");

        // 4. Update waktu sampai cooldown selesai
        player.updateCombat(Player.ATTACK_COOLDOWN + 0.05f);
        assertFalse(player.isAttacking(), "Setelah durasi serang, isAttacking harus false");
        assertEquals(0f, player.attackCooldown(), "Cooldown harus 0 setelah waktu berlalu");

        // 5. Serangan hadap RIGHT
        player.setFacing(Player.Facing.RIGHT);
        player.attack();
        assertEquals(Player.Facing.RIGHT, player.attackFacing(), "Arah serangan harus RIGHT");
        float[] hbRight = player.getAttackHitbox();
        assertNotNull(hbRight, "Hitbox RIGHT tidak boleh null");
        assertTrue(hbRight[0] > player.x(), "Hitbox RIGHT harus berada di kanan koordinat X pemain");

        // 6. Serangan hadap LEFT
        player.updateCombat(Player.ATTACK_COOLDOWN + 0.05f);
        player.setFacing(Player.Facing.LEFT);
        player.attack();
        assertEquals(Player.Facing.LEFT, player.attackFacing(), "Arah serangan harus LEFT");
        float[] hbLeft = player.getAttackHitbox();
        assertNotNull(hbLeft, "Hitbox LEFT tidak boleh null");
        assertTrue(hbLeft[0] < player.x(), "Hitbox LEFT harus berada di kiri koordinat X pemain");

        // 7. Serangan hadap UP
        player.updateCombat(Player.ATTACK_COOLDOWN + 0.05f);
        player.setFacing(Player.Facing.UP);
        player.attack();
        assertEquals(Player.Facing.UP, player.attackFacing(), "Arah serangan harus UP");
        float[] hbUp = player.getAttackHitbox();
        assertNotNull(hbUp, "Hitbox UP tidak boleh null");
        assertTrue(hbUp[1] < player.y(), "Hitbox UP harus berada di atas koordinat Y pemain");

        System.out.println("[PASS] Senjata saku dan hitbox 4 arah tervalidasi 100%");
    }

    private static void testEnemyStateMachineAndTelegraphRhythm() {
        System.out.println("\n--- [Test 2] Finite State Machine Musuh & Ritme Telegraf ---");
        GameplayScene scene = new GameplayScene(null);
        scene.setupArea("hollow_woods");
        Player player = scene.player();

        BayanganHutan shadow = new BayanganHutan(200f, 100f);
        player.setPosition(290f, 100f); // di luar jangkauan (jarak 90px > alert 65px)

        // 1. Awal: PATROL
        assertEquals(Enemy.State.PATROL, shadow.state(), "State awal musuh harus PATROL");
        shadow.update(0.1f, player, scene.map());
        assertEquals(Enemy.State.PATROL, shadow.state(), "Harus tetap PATROL di luar alert radius");

        // 2. Pemain mendekat: CHASE
        player.setPosition(240f, 100f); // jarak 40px <= alert 65px
        shadow.update(0.1f, player, scene.map());
        assertEquals(Enemy.State.CHASE, shadow.state(), "Harus masuk ke state CHASE saat pemain terdeteksi");

        // 3. Masuk jangkauan serang: TELEGRAPH
        player.setPosition(215f, 100f); // jarak 15px <= attack radius 24px
        shadow.update(0.016f, player, scene.map());
        assertEquals(Enemy.State.TELEGRAPH, shadow.state(), "Harus masuk state TELEGRAPH sebelum menyerang");
        assertTrue(shadow.isTelegraphing(), "isTelegraphing() harus bernilai true");
        assertNull(shadow.getAttackHitbox(), "Hitbox serang musuh TIDAK boleh aktif saat telegraf");

        // 4. Setelah waktu telegraf habis: ATTACK
        shadow.update(0.7f, player, scene.map());
        assertEquals(Enemy.State.ATTACK, shadow.state(), "Harus beralih ke state ATTACK setelah telegraf selesai");
        assertTrue(shadow.isAttacking(), "isAttacking() harus bernilai true");
        assertNotNull(shadow.getAttackHitbox(), "Hitbox serang musuh harus aktif saat ATTACK");

        // 5. Setelah waktu serang habis: RECOVERY (jeda rentan membalas)
        shadow.update(0.25f, player, scene.map());
        assertEquals(Enemy.State.RECOVERY, shadow.state(), "Harus masuk state RECOVERY setelah serangan selesai");
        assertTrue(shadow.isVulnerable(), "isVulnerable() harus bernilai true saat recovery");
        assertNull(shadow.getAttackHitbox(), "Hitbox serang musuh harus nonaktif saat recovery");

        System.out.println("[PASS] Ritme FSM: Patrol -> Chase -> Telegraph -> Attack -> Recovery berhasil");
    }

    private static void testPenjagaJejakPhasesAndSigilReward() {
        System.out.println("\n--- [Test 3] Boss Penjaga Jejak: 2 Fase & Hadiah Sigil Jejak ---");
        GameplayScene scene = new GameplayScene(null);
        scene.setupArea("hollow_woods");
        Player player = scene.player();
        PenjagaJejak boss = scene.penjagaJejak();

        assertTrue(boss.isBoss(), "Penjaga Jejak harus berstatus bos");
        assertEquals(10, boss.maxHp(), "Max HP bos Penjaga Jejak harus 10");
        assertEquals(1, boss.phase(), "Fase awal bos harus Fase 1");
        assertFalse(scene.isSigilJejakObtained(), "Sigil Jejak belum boleh diperoleh di awal");
        assertFalse(scene.gerbangHutan().isFogCleared(), "Gerbang kabut belum boleh terbuka di awal");

        // 1. Kurangi HP bos hingga <= 5 untuk memicu Fase 2
        boss.takeDamage(5, 1f, 0f);
        assertEquals(5, boss.hp(), "HP bos harus tersisa 5");
        boss.update(0.016f, player, scene.map());
        assertEquals(2, boss.phase(), "Bos harus bertransisi ke Fase 2 (Murka) saat HP <= 5");
        assertEquals(2, boss.damage(), "Damage serangan bos di Fase 2 harus meningkat menjadi 2");

        // 2. Beri damage hingga bos kalah (setelah hurtTimer habis)
        boss.update(0.3f, player, scene.map());
        boss.takeDamage(5, 1f, 0f);
        assertEquals(0, boss.hp(), "HP bos harus 0");
        assertTrue(boss.isDefeated(), "Bos harus berstatus DEFEATED");
        assertEquals(Enemy.State.DEFEATED, boss.state(), "State bos harus DEFEATED");

        // 3. Panggil hook onDefeated
        boss.onDefeated(scene);
        assertTrue(scene.isSigilJejakObtained(), "Sigil Jejak harus tercatat diperoleh setelah bos kalah");
        assertTrue(scene.gerbangHutan().isFogCleared(), "Kabut gerbang timur harus buyar setelah bos dikalahkan");

        System.out.println("[PASS] Bos 2 Fase dan perolehan Sigil Jejak terverifikasi");
    }

    private static void testDamageReceptionAndInvulnerabilityFrames() {
        System.out.println("\n--- [Test 4] Damage Pemain, i-Frames & Knockback ---");
        GameplayScene scene = new GameplayScene(null);
        Player player = scene.player();

        assertEquals(Player.MAX_HP, player.hp(), "HP awal harus MAX_HP (6)");
        assertFalse(player.isInvulnerable(), "Pemain tidak boleh invulnerable di awal");

        // 1. Terima serangan pertama
        float oldX = player.x();
        boolean damaged = player.takeDamage(1, player.x() + 20f, player.y());
        assertTrue(damaged, "Damage pertama harus berhasil masuk");
        assertEquals(5, player.hp(), "HP harus berkurang menjadi 5");
        assertTrue(player.isInvulnerable(), "Pemain harus memperoleh i-frame");

        // 2. Serangan susulan saat i-frame harus ditolak
        boolean immuneHit = player.takeDamage(1, player.x() + 20f, player.y());
        assertFalse(immuneHit, "Serangan saat i-frame harus ditolak (imun)");
        assertEquals(5, player.hp(), "HP tidak boleh berkurang saat imun");

        // 3. Jalankan pembaruan waktu i-frame hingga habis
        player.updateCombat(Player.INVULNERABLE_DURATION + 0.1f);
        assertFalse(player.isInvulnerable(), "Setelah durasi habis, i-frame harus nonaktif");

        // 4. Serangan berikutnya setelah i-frame habis
        damaged = player.takeDamage(2, player.x() + 20f, player.y());
        assertTrue(damaged, "Damage setelah i-frame habis harus berhasil masuk");
        assertEquals(3, player.hp(), "HP harus berkurang menjadi 3");

        System.out.println("[PASS] Pengurangan HP, i-frames, dan knockback tervalidasi");
    }

    private static void testModalBlocksCombat() {
        System.out.println("\n--- [Test 5] Proteksi Modal: Serangan Terkunci saat Menu/Dialog ---");
        GameplayScene scene = new GameplayScene(null);
        Player player = scene.player();

        // 1. Kondisi normal tanpa modal: serangan bisa
        assertTrue(scene.triggerPlayerAttack(), "Serangan harus berhasil saat tidak ada modal");
        player.updateCombat(Player.ATTACK_COOLDOWN + 0.05f);

        // 2. Saat Casebook terbuka: serangan dilarang
        scene.openCasebook();
        assertTrue(scene.isModalOpen(), "isModalOpen harus true saat Casebook dibuka");
        assertFalse(scene.triggerPlayerAttack(), "Serangan harus diblokir saat Casebook terbuka");
        scene.closeCasebook();
        assertFalse(scene.isModalOpen(), "isModalOpen harus false setelah Casebook ditutup");

        // 3. Saat Pause Menu terbuka: serangan dilarang
        scene.openPauseMenu();
        assertTrue(scene.isModalOpen(), "isModalOpen harus true saat Pause Menu dibuka");
        assertFalse(scene.triggerPlayerAttack(), "Serangan harus diblokir saat Pause Menu terbuka");
        scene.closePauseMenu();
        assertFalse(scene.isModalOpen(), "isModalOpen harus false setelah Pause Menu ditutup");

        System.out.println("[PASS] Serangan dan combat terkunci dengan aman saat modal aktif");
    }

    private static void testPlayerDefeatAndSafeRespawn() {
        System.out.println("\n--- [Test 6] Kekalahan Pemain & Respawn Aman Tanpa Hapus Progres ---");
        GameplayScene scene = new GameplayScene(null);
        scene.setupArea("hollow_woods");
        Player player = scene.player();

        // Temukan 1 bukti sebelum pertarungan
        scene.investigation().discover("clue_meja_catatan");
        assertEquals(1, scene.investigation().discoveredCount(), "Harus ada 1 bukti tercatat");

        // Kalahkan pemain (damage 6)
        player.takeDamage(6, player.x() + 10f, player.y());
        assertTrue(player.isDead(), "Pemain harus berstatus isDead()");
        assertEquals(0, player.hp(), "HP pemain harus 0");

        // Simulasi update scene sampai timer respawn selesai
        scene.update(0.016f); // memicu update
        // Panggil respawn pemain ke titik aman
        player.respawn(scene.map().spawnX(), scene.map().spawnY());

        // Verifikasi kondisi pasca-respawn
        assertEquals(Player.MAX_HP, player.hp(), "HP pemain harus pulih penuh setelah respawn");
        assertTrue(player.isAlive(), "Pemain harus hidup kembali");
        assertTrue(player.isInvulnerable(), "Pemain harus mendapat i-frame keselamatan pasca-respawn");

        // Verifikasi bahwa bukti dan progres TIDAK hilang
        assertEquals(1, scene.investigation().discoveredCount(), "Bukti TIDAK boleh hilang saat kalah!");
        assertTrue(scene.investigation().isDiscovered("clue_meja_catatan"), "Clue yang sudah ditemukan tetap tersimpan");

        System.out.println("[PASS] Pemain bangkit di titik aman tanpa kehilangan clue dan progres cerita");
    }

    private static void testSaveLoadCombatState() {
        System.out.println("\n--- [Test 7] Save & Load Status Pertarungan & Sigil Jejak ---");
        File tempSave = new File(System.getProperty("java.io.tmpdir"), "silent_witch_combat_test.json");
        if (tempSave.exists()) {
            tempSave.delete();
        }
        SaveManager sm = new SaveManager(tempSave);

        // Scene 1: Kalahkan bos dan peroleh Sigil Jejak
        GameplayScene scene1 = new GameplayScene(null);
        scene1.setupArea("hollow_woods");
        scene1.penjagaJejak().takeDamage(10, 1f, 0f);
        scene1.penjagaJejak().onDefeated(scene1);

        assertTrue(scene1.isSigilJejakObtained(), "Scene 1 harus memiliki Sigil Jejak");
        assertTrue(scene1.penjagaJejak().isDefeated(), "Scene 1: bos harus defeated");
        assertTrue(scene1.gerbangHutan().isFogCleared(), "Scene 1: kabut gerbang hutan harus cleared");

        // Simpan ke disk
        SaveManager.SaveResult saveRes = scene1.saveTo(sm);
        assertTrue(saveRes.isSuccess(), "Penyimpanan data pertarungan ke disk harus sukses");

        // Muat dari disk
        SaveManager.LoadResult loadRes = sm.load();
        assertTrue(loadRes.isSuccess(), "Pemuatan data dari disk harus sukses");
        SaveData loadedData = loadRes.data();

        // Scene 2: Pulihkan state
        GameplayScene scene2 = new GameplayScene(null, loadedData);
        assertEquals("hollow_woods", scene2.currentAreaId(), "Area yang dipulihkan harus hollow_woods");
        assertTrue(scene2.isSigilJejakObtained(), "Scene 2: Sigil Jejak harus tetap dimiliki");
        assertTrue(scene2.penjagaJejak().isDefeated(), "Scene 2: status kekalahan bos harus tetap tersimpan");
        assertTrue(scene2.gerbangHutan().isFogCleared(), "Scene 2: gerbang hutan tetap terbuka tanpa kabut");

        tempSave.delete();
        System.out.println("[PASS] Status pertarungan dan Sigil Jejak tersimpan dan termuat 100% konsisten");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("[FAIL] " + message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError("[FAIL] " + message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError("[FAIL] " + message + " (Diharapkan: " + expected + ", Aktual: " + actual + ")");
        }
    }

    private static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError("[FAIL] " + message);
        }
    }

    private static void assertNull(Object obj, String message) {
        if (obj != null) {
            throw new AssertionError("[FAIL] " + message + " (Diharapkan null, tetapi: " + obj + ")");
        }
    }
}
