import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Scene gameplay Stage 06: Save/Load dan Integrasi.
 *
 * Mengintegrasikan area The Silent Manor dengan:
 * - 4 bukti investigasi aula dan Detective Casebook
 * - Quest "MISTERI AULA UTARA" dengan sub-tujuan pengumpulan bukti, puzzle, dan gerbang
 * - Teka-teki Kotak Besi Aula 3-dial angka (jawaban 2-4-9) dengan validasi kondisi dan 3-tier hint
 * - Gerbang Sayap Timur yang terkunci rapat sebelum puzzle dipecahkan
 * - Autosave otomatis pada peristiwa penting (clue, puzzle, gate, exit)
 * - Pemulihan state utuh (posisi, clue, quest, puzzle, gate) saat Load/Lanjutkan
 * - Penguncian gerak saat dialog, casebook, atau puzzle terbuka
 */
public final class GameplayScene extends Scene {

    private static final int HEADER_H = 15;
    private static final int HINT_H = 12;
    private static final int DUST_COUNT = 16;
    private static final float INPUT_COOLDOWN = 0.18f;

    private final TileMap map;
    private final Player player;
    private final Npc npc;
    private final List<Interactable> interactables = new ArrayList<Interactable>();
    private final DialogueSystem dialogue = new DialogueSystem();
    private final InvestigationSystem investigation = new InvestigationSystem();
    private final QuestSystem quest = new QuestSystem();
    private final PuzzleSystem puzzleSystem = new PuzzleSystem();

    private final Puzzle puzzleKotak;
    private final KotakBesi kotakBesi;
    private final GerbangTimur gerbangTimur;

    private final float[] dustX = new float[DUST_COUNT];
    private final float[] dustY = new float[DUST_COUNT];
    private final float[] dustSpeed = new float[DUST_COUNT];
    private final float[] dustPhase = new float[DUST_COUNT];

    private float time = 0f;
    private float cameraX = 0f;
    private float cameraY = 0f;
    private float interactCooldown = 0f;
    private float autosaveNoticeTimer = 0f;
    private Interactable focused = null;

    private boolean keyUp = false;
    private boolean keyDown = false;
    private boolean keyLeft = false;
    private boolean keyRight = false;

    public GameplayScene(GameManager game) {
        this(game, null);
    }

    public GameplayScene(GameManager game, SaveData loadData) {
        super(game);
        this.map = TileMap.manorHall();
        this.player = new Player(map, map.spawnX(), map.spawnY());

        // --- Pendaftaran Clue Aula ---
        Clue clueRak = new Clue("clue_rak_harian",
                "BUKU HARIAN USANG",
                "BUKU HARIAN BERSAMPUL KULIT GELAP DENGAN BEBERAPA HALAMAN TENGAH SOBEK SECARA PAKSA.",
                "RAK BUKU",
                "DITEMUKAN DI ANTARA DERETAN JILID TEBAL PADA RAK BUKU AULA.",
                new String[]{"clue_jendela_goresan"},
                new String[]{"quest_investigasi_aula"},
                "3 HARI LALU",
                "DOKUMEN");

        Clue clueMeja = new Clue("clue_meja_catatan",
                "SOBEKAN CATATAN AULA",
                "SOBEKAN KERTAS TUA BERISI DIAGRAM LINGKAR DAN JADWAL PERGANTIAN PENJAGA MALAM ASHEN VILLAGE.",
                "MEJA AULA",
                "TERSELIP DI CELAH LACI MEJA AULA DI BAWAH TUMPUKAN DEBU.",
                new String[]{"clue_penjaga_kesaksian"},
                new String[]{"quest_investigasi_aula"},
                "H-1 MALAM",
                "BUKTI FISIK");

        Clue cluePenjaga = new Clue("clue_penjaga_kesaksian",
                "KESAKSIAN PENJAGA AULA",
                "PENJAGA MENDENGAR SUARA LANGKAH KAKI TERGESA DI DEKAT RAK BUKU SEBELUM TENGAH MALAM.",
                "PENJAGA AULA",
                "DIPEROLEH DARI PERCAKAPAN INVESTIGASI DENGAN PENJAGA MALAM AULA.",
                new String[]{"clue_meja_catatan", "clue_jendela_goresan"},
                new String[]{"quest_investigasi_aula"},
                "23:30 MALAM",
                "KESAKSIAN");

        Clue clueJendela = new Clue("clue_jendela_goresan",
                "BEKAS GORESAN KUSEN",
                "BEKAS CUNGKILAN LOGAM SEGAR PADA AMBANG JENDELA UTARA YANG MENGHADAP KE HUTAN.",
                "JENDELA UTARA",
                "TERLIHAT SAAT MEMERIKSA ENGSEL DAN AMBANG JENDELA UTARA DARI DEKAT.",
                new String[]{"clue_penjaga_kesaksian"},
                new String[]{"quest_investigasi_aula"},
                "MALAM KASUS",
                "LINGKUNGAN");

        investigation.registerClue(clueMeja);
        investigation.registerClue(clueRak);
        investigation.registerClue(clueJendela);
        investigation.registerClue(cluePenjaga);

        // --- Pendaftaran Quest Stage 05 ---
        List<QuestObjective> objectives = new ArrayList<QuestObjective>();
        objectives.add(new QuestObjective("obj_kumpulkan_bukti", "Kumpulkan 4 bukti aula", 4));
        objectives.add(new QuestObjective("obj_pecahkan_puzzle", "Pecahkan teka-teki Kotak Besi Aula", 1));
        objectives.add(new QuestObjective("obj_buka_gerbang", "Buka Gerbang Sayap Timur aula", 1));

        Quest questAula = new Quest("quest_investigasi_aula",
                "MISTERI AULA UTARA",
                "Selidiki aula manor, kumpulkan bukti, dan buka peti besi kuno pengunci gerbang sayap timur.",
                new String[0],
                new String[]{"clue_meja_catatan", "clue_rak_harian", "clue_penjaga_kesaksian", "clue_jendela_goresan"},
                "AKSES GERBANG SAYAP TIMUR TERBUKA",
                objectives);

        quest.registerQuest(questAula);
        quest.activateQuest("quest_investigasi_aula");

        // --- Pendaftaran Puzzle Kotak Besi (PRD 5.1 type 2: 3-dial code 2-4-9) ---
        String[] hints = new String[]{
                "Periksa kembali aturan angka: cari bilangan prima genap untuk digit pertama, lalu kalikan dua untuk digit kedua.",
                "Satu-satunya bilangan prima genap adalah 2. Digit kedua adalah 2 x 2 = 4. Digit ketiga adalah 3 x 3 = 9.",
                "Kombinasi kunci kotak besi adalah 2 - 4 - 9."
        };

        this.puzzleKotak = new Puzzle("puzzle_kotak_besi",
                "KOTAK BESI AULA",
                "1. DIGIT I: PRIMA GENAP TERKECIL.\n2. DIGIT II: DUA KALI LIPAT DIGIT I.\n3. DIGIT III: KUADRAT ANGKA 3 (3X3).\n4. JUMLAH DIGIT = 15.",
                new int[]{2, 4, 9},
                hints,
                "Kombinasi 2-4-9 cocok! Digit I=2, II=4, III=9. Mekanisme kotak berputar dan membuka pengait gerbang sayap timur!");

        puzzleSystem.setOnSolvedListener(new PuzzleSystem.OnSolvedListener() {
            @Override
            public void onPuzzleSolved(Puzzle p) {
                quest.onPuzzleSolved(p.id());
                investigation.setNotification("TEKA-TEKI SELESAI: KOTAK BESI TERBUKA!", 3.5f);
                autosave();
            }
        });

        // --- NPC dan Objek Interaktif ---
        this.npc = new Npc(232f, 200f, "PENJAGA AULA", cluePenjaga,
                new Dialogue("PENJAGA AULA",
                        "Penyelidik... malam ini terasa lebih dingin dari biasanya.",
                        "Sebelum tengah malam, aku mendengar suara langkah di dekat rak buku.",
                        "Saat kupastikan, tidak ada siapa-siapa di sana... hanya jendela utara yang bergoyang.",
                        "Kesaksian penjaga telah dicatat ke dalam Buku Kasus."),
                new Dialogue("PENJAGA AULA",
                        "Aku tetap berjaga di aula ini sampai fajar menyingsing.",
                        "Semua yang kudengar malam itu sudah kukatakan padamu.",
                        "Periksalah meja, rak, dan jendela utara jika kau butuh bukti nyata."));
        player.addBlocker(npc.x(), npc.y() - Npc.BLOCK_H / 2f, Npc.BLOCK_W, Npc.BLOCK_H);
        interactables.add(npc);

        interactables.add(new ExamineSpot(96f, 126f, 26f, "MEJA AULA", clueMeja,
                new Dialogue("MEJA AULA",
                        "Debu tebal menutupi permukaan meja kayu tua ini.",
                        "Terselip di celah laci, ada sobekan catatan berlumur lilin.",
                        "Catatan itu memuat jadwal jaga malam dan sketsa lingkaran misterius.",
                        "Sobekan catatan telah dicatat ke dalam Buku Kasus."),
                new Dialogue("MEJA AULA",
                        "Meja kayu ini sudah diperiksa dengan teliti.",
                        "Sobekan catatan jadwal jaga sudah tersimpan di Buku Kasus.",
                        "Tidak ada petunjuk lain yang tertinggal di sini.")));

        interactables.add(new ExamineSpot(80f, 62f, 26f, "RAK BUKU", clueRak,
                new Dialogue("RAK BUKU",
                        "Di antara deretan buku tebal tanpa judul, ada jilid kecil bersampul kulit.",
                        "Sebagian halaman tengah telah dirobek dengan tergesa-gesa.",
                        "Halaman tersisa menyebut pertemuan rahasia di dekat kapel sunyi.",
                        "Buku harian usang telah dicatat ke dalam Buku Kasus."),
                new Dialogue("RAK BUKU",
                        "Rak buku berdebu yang penuh naskah tua tanpa judul.",
                        "Buku harian usang sudah kau periksa sebelumnya.",
                        "Halaman lainnya di rak ini hanya teks usang yang tidak terbaca.")));

        interactables.add(new ExamineSpot(168f, 16f, 30f, "JENDELA UTARA", clueJendela,
                new Dialogue("JENDELA UTARA",
                        "Angin malam berhembus melalui celah daun jendela yang renggang.",
                        "Di ambang jendela luar, terlihat bekas cungkilan logam yang masih baru.",
                        "Seseorang tampaknya memaksa jendela ini terbuka dari arah luar.",
                        "Bekas goresan kusen telah dicatat ke dalam Buku Kasus."),
                new Dialogue("JENDELA UTARA",
                        "Kusen jendela utara menghadap ke kabut malam yang pekat.",
                        "Bekas cungkilan logam sudah tercatat rapi di Buku Kasus.",
                        "Tidak ada tanda jejak kaki baru di bawah terpaan angin luar.")));

        // --- Objek Puzzle & Gerbang Stage 05 ---
        this.kotakBesi = new KotakBesi(368f, 48f, puzzleKotak);
        interactables.add(kotakBesi);

        this.gerbangTimur = new GerbangTimur(388f, 104f, map, player, puzzleKotak, quest);
        player.addBlocker(388f, 104f, 16f, 32f);
        interactables.add(gerbangTimur);

        for (int i = 0; i < DUST_COUNT; i++) {
            dustX[i] = Palette.hash(i * 9 + 21) * GameManager.VIRTUAL_W;
            dustY[i] = Palette.hash(i * 9 + 22) * GameManager.VIRTUAL_H;
            dustSpeed[i] = 1.5f + Palette.hash(i * 9 + 23) * 4f;
            dustPhase[i] = Palette.hash(i * 9 + 24) * 6.28318f;
        }

        if (loadData != null) {
            restoreFrom(loadData);
        } else {
            centerCameraOnPlayer();
        }
    }

    public int interactableCount() {
        return interactables.size();
    }

    public boolean isDialogueOpen() {
        return dialogue.isOpen();
    }

    public boolean isCasebookOpen() {
        return investigation.isCasebookOpen();
    }

    public boolean isPuzzleOpen() {
        return puzzleSystem.isOpen();
    }

    public InvestigationSystem investigation() {
        return investigation;
    }

    public QuestSystem questSystem() {
        return quest;
    }

    public PuzzleSystem puzzleSystem() {
        return puzzleSystem;
    }

    public Puzzle puzzle() {
        return puzzleKotak;
    }

    public KotakBesi kotakBesi() {
        return kotakBesi;
    }

    public GerbangTimur gerbangTimur() {
        return gerbangTimur;
    }

    public int discoveredClueCount() {
        return investigation.discoveredCount();
    }

    public void openCasebook() {
        clearInput();
        investigation.openCasebook();
    }

    public void closeCasebook() {
        investigation.closeCasebook();
    }

    public void toggleCasebook() {
        clearInput();
        investigation.toggleCasebook();
    }

    public float playerX() {
        return player.x();
    }

    public float playerY() {
        return player.y();
    }

    public String focusedPrompt() {
        return focused == null ? null : focused.prompt();
    }

    public TileMap map() {
        return map;
    }

    public Player player() {
        return player;
    }

    public boolean autosave() {
        if (game != null && game.saveManager() != null) {
            SaveData state = captureCurrentState();
            SaveManager.SaveResult res = game.saveManager().save(state);
            if (res.isSuccess()) {
                autosaveNoticeTimer = 2.0f;
                return true;
            }
        }
        return false;
    }

    public SaveManager.SaveResult saveTo(SaveManager sm) {
        if (sm != null) {
            return sm.save(captureCurrentState());
        }
        return SaveManager.SaveResult.failure("SaveManager null");
    }

    public SaveData captureCurrentState() {
        List<String> clues = investigation.allDiscoveredIds();

        List<SaveData.QuestSave> questSaves = new ArrayList<SaveData.QuestSave>();
        for (Quest q : quest.allQuests()) {
            List<SaveData.ObjectiveSave> objSaves = new ArrayList<SaveData.ObjectiveSave>();
            for (QuestObjective o : q.objectives()) {
                objSaves.add(new SaveData.ObjectiveSave(o.id(), o.currentCount(), o.isComplete()));
            }
            questSaves.add(new SaveData.QuestSave(q.id(), q.status().name(), objSaves));
        }

        List<SaveData.PuzzleSave> puzzleSaves = new ArrayList<SaveData.PuzzleSave>();
        puzzleSaves.add(new SaveData.PuzzleSave(puzzleKotak.id(), puzzleKotak.isSolved(),
                puzzleKotak.wrongAttempts(), puzzleKotak.revealedHintLevel()));

        Map<String, Boolean> worldFlags = new LinkedHashMap<String, Boolean>();
        worldFlags.put("gate_east_open", gerbangTimur.isGateOpen());

        return new SaveData(
                SaveData.CURRENT_SCHEMA_VERSION,
                System.currentTimeMillis(),
                "the_silent_manor_hall",
                player.x(),
                player.y(),
                player.facing(),
                clues,
                quest.activeQuestId(),
                questSaves,
                puzzleSaves,
                worldFlags
        );
    }

    public void restoreFrom(SaveData data) {
        if (data == null) {
            return;
        }

        // 1. Posisi dan arah hadap pemain
        player.setPosition(data.playerX(), data.playerY());
        player.setFacing(data.playerFacing());

        // 2. Daftar clue yang telah ditemukan
        for (String clueId : data.discoveredClueIds()) {
            investigation.restoreDiscovered(clueId);
        }

        // 3. Status quest dan sub-tujuan
        for (SaveData.QuestSave qs : data.quests()) {
            quest.restoreQuestState(qs.id(), qs.status(), qs.objectives());
        }
        if (data.activeQuestId() != null) {
            quest.setActiveQuest(data.activeQuestId());
        }

        // 4. Teka-teki Kotak Besi
        SaveData.PuzzleSave ps = data.puzzle(puzzleKotak.id());
        if (ps != null) {
            puzzleKotak.restoreState(ps.isSolved(), ps.wrongAttempts(), ps.hintLevel());
        }

        // 5. World flags (status gerbang sayap timur)
        boolean gateOpen = data.isWorldFlag("gate_east_open", false);
        if (gateOpen || (ps != null && ps.isSolved() && quest.isQuestCompleted("quest_investigasi_aula"))) {
            gerbangTimur.restoreOpen(true);
        }

        // Bersihkan notifikasi awal agar layar bersih saat baru dimuat
        investigation.clearNotification();
        quest.clearNotification();
        centerCameraOnPlayer();
    }

    @Override
    public void onEnter() {
        clearInput();
        dialogue.close();
        investigation.closeCasebook();
        puzzleSystem.close();
        focused = null;
        centerCameraOnPlayer();
    }

    @Override
    public void onExit() {
        autosave();
        clearInput();
        dialogue.close();
        investigation.closeCasebook();
        puzzleSystem.close();
    }

    private void clearInput() {
        keyUp = false;
        keyDown = false;
        keyLeft = false;
        keyRight = false;
    }

    @Override
    public void update(float dt) {
        time += dt;

        if (interactCooldown > 0f) {
            interactCooldown = Math.max(0f, interactCooldown - dt);
        }

        if (autosaveNoticeTimer > 0f) {
            autosaveNoticeTimer = Math.max(0f, autosaveNoticeTimer - dt);
        }

        investigation.update(dt);
        quest.update(dt);

        if (quest.hasActiveNotification()) {
            investigation.setNotification(quest.lastNotification(), 3.5f);
            quest.clearNotification();
        }

        for (int i = 0; i < DUST_COUNT; i++) {
            dustY[i] -= dustSpeed[i] * dt;
            if (dustY[i] < HEADER_H) {
                dustY[i] = GameManager.VIRTUAL_H;
            }
        }

        float inputX = 0f;
        float inputY = 0f;
        // Kunci gerakan pemain jika dialog, buku kasus, atau puzzle sedang terbuka
        if (!dialogue.isOpen() && !investigation.isCasebookOpen() && !puzzleSystem.isOpen()) {
            inputX = (keyRight ? 1f : 0f) - (keyLeft ? 1f : 0f);
            inputY = (keyDown ? 1f : 0f) - (keyUp ? 1f : 0f);
        }
        player.update(dt, inputX, inputY);

        focused = (dialogue.isOpen() || investigation.isCasebookOpen() || puzzleSystem.isOpen()) ? null
                : Interactable.nearest(interactables, player.x(), player.y());

        float targetX = player.x() - GameManager.VIRTUAL_W / 2f;
        float targetY = player.y() - GameManager.VIRTUAL_H / 2f;
        float smooth = Math.min(1f, dt * 9f);
        cameraX += (clampCameraX(targetX) - cameraX) * smooth;
        cameraY += (clampCameraY(targetY) - cameraY) * smooth;
    }

    private void centerCameraOnPlayer() {
        cameraX = clampCameraX(player.x() - GameManager.VIRTUAL_W / 2f);
        cameraY = clampCameraY(player.y() - GameManager.VIRTUAL_H / 2f);
    }

    private float clampCameraX(float value) {
        return Math.max(0f, Math.min(map.pixelWidth() - GameManager.VIRTUAL_W, value));
    }

    private float clampCameraY(float value) {
        return Math.max(0f, Math.min(map.pixelHeight() - GameManager.VIRTUAL_H, value));
    }

    @Override
    public void draw(PGraphics g) {
        int camX = Math.round(cameraX);
        int camY = Math.round(cameraY);

        g.background(Palette.INK);
        map.draw(g, camX, camY);

        drawPlayerGlow(g, camX, camY);
        drawEntities(g, camX, camY);
        drawDust(g);

        PixelUI.vignette(g);
        drawPrompt(g, camX, camY);
        drawHeader(g);
        drawHint(g);

        dialogue.draw(g);
        investigation.drawNotification(g);
        investigation.drawCasebook(g);
        puzzleSystem.draw(g);
    }

    /** Menggambar objek interaktif dunia, NPC, dan pemain dengan urutan kedalaman. */
    private void drawEntities(PGraphics g, int camX, int camY) {
        kotakBesi.draw(g, camX, camY);
        gerbangTimur.draw(g, camX, camY);

        if (npc.y() <= player.y()) {
            npc.draw(g, camX, camY);
            player.draw(g, camX, camY);
        } else {
            player.draw(g, camX, camY);
            npc.draw(g, camX, camY);
        }
    }

    private void drawPlayerGlow(PGraphics g, int camX, int camY) {
        float px = player.x() - camX;
        float py = player.y() - camY - 10;
        g.noStroke();
        g.fill(Palette.alpha(Palette.MAGIC, 6));
        g.ellipse(px, py, 54, 48);
        g.fill(Palette.alpha(Palette.MAGIC, 5));
        g.ellipse(px, py, 30, 28);
    }

    private void drawDust(PGraphics g) {
        g.noStroke();
        for (int i = 0; i < DUST_COUNT; i++) {
            float x = dustX[i] + (float) Math.sin(time * 0.7f + dustPhase[i]) * 4f;
            int alpha = 16 + (int) (22 * (0.5f + 0.5f * Math.sin(time * 1.1f + dustPhase[i])));
            g.fill(Palette.alpha(Palette.MOON, alpha));
            g.rect(x, dustY[i], 1, 1);
        }
    }

    private void drawPrompt(PGraphics g, int camX, int camY) {
        if (focused == null || dialogue.isOpen() || investigation.isCasebookOpen() || puzzleSystem.isOpen()) {
            return;
        }
        String label = "[E] " + focused.prompt();
        int w = PixelFont.measureLine(label, 1) + 10;
        int h = 12;
        int bob = (int) Math.round(Math.sin(time * 4f));

        int x = Math.round(focused.x() - camX - w / 2f);
        int y = Math.round(focused.y() - camY - 30f) + bob;
        x = clampInt(x, 3, GameManager.VIRTUAL_W - 3 - w);
        y = clampInt(y, HEADER_H + 3, GameManager.VIRTUAL_H - HINT_H - 3 - h);

        PixelUI.panel(g, x, y, w, h);
        PixelUI.text(g, label, x + 5, y + 3, Palette.TEXT);
    }

    private static int clampInt(int value, int min, int max) {
        if (max < min) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }

    private void drawHeader(PGraphics g) {
        g.noStroke();
        g.fill(Palette.alpha(Palette.INK, 210));
        g.rect(0, 0, GameManager.VIRTUAL_W, HEADER_H);
        g.fill(Palette.BORDER_DIM);
        g.rect(0, HEADER_H, GameManager.VIRTUAL_W, 1);

        PixelUI.text(g, "AULA MANOR", 6, 4, Palette.TEXT_DIM);

        if (autosaveNoticeTimer > 0f) {
            PixelUI.text(g, "[AUTOSAVE OK]", 76, 4, Palette.WARM);
        }

        String info = "BUKTI: " + investigation.discoveredCount() + "/" + investigation.totalCount()
                + "  " + quest.hudSummary() + "  TAHAP 06";
        PixelFont.drawRight(g, info, 314, 4, 1, Palette.TEXT_MUTED);
    }

    private void drawHint(PGraphics g) {
        if (investigation.isCasebookOpen() || puzzleSystem.isOpen()) {
            return;
        }

        int y = GameManager.VIRTUAL_H - HINT_H;
        g.noStroke();
        g.fill(Palette.alpha(Palette.INK, 170));
        g.rect(0, y, GameManager.VIRTUAL_W, HINT_H);
        g.fill(Palette.BORDER_DIM);
        g.rect(0, y, GameManager.VIRTUAL_W, 1);

        if (dialogue.isOpen()) {
            String text = dialogue.isLastLine()
                    ? "E / ENTER : TUTUP     ESC : TUTUP"
                    : "E / ENTER : LANJUT     ESC : TUTUP";
            PixelUI.centered(g, text, 160, y + 3, Palette.TEXT_DIM);
        } else {
            PixelUI.centered(g, "GERAK: WASD   E: INTERAKSI   C: BUKU KASUS   ESC: MENU", 160, y + 3,
                    Palette.TEXT_MUTED);
        }
    }

    @Override
    public void keyPressed(char key, int keyCode) {
        // 1. Jika Puzzle terbuka, seluruh input keyboard diarahkan ke Puzzle
        if (puzzleSystem.isOpen()) {
            puzzleSystem.keyPressed(key, keyCode);
            return;
        }

        // 2. Jika Casebook terbuka, seluruh input keyboard diarahkan ke Casebook
        if (investigation.isCasebookOpen()) {
            investigation.keyPressed(key, keyCode);
            return;
        }

        // 3. Jika dialog terbuka, tangani navigasi dialog
        if (dialogue.isOpen()) {
            if (isEscape(key, keyCode)) {
                dialogue.close();
                interactCooldown = INPUT_COOLDOWN;
                return;
            }
            if (isInteract(key, keyCode) && interactCooldown <= 0f) {
                dialogue.advance();
                interactCooldown = INPUT_COOLDOWN;
            }
            return;
        }

        // 4. Gameplay biasa: cek tombol buka Casebook (C, J, Tab)
        if (key == 'c' || key == 'C' || key == 'j' || key == 'J' || key == '\t') {
            openCasebook();
            return;
        }

        // 5. ESC pada gameplay kembali ke menu utama
        if (isEscape(key, keyCode)) {
            game.showMenu();
            return;
        }

        // 6. Interaksi dengan objek atau NPC
        if (isInteract(key, keyCode)) {
            if (interactCooldown <= 0f && focused != null) {
                // Objek kotak besi membuka puzzle bila belum terpecahkan
                if (focused instanceof KotakBesi) {
                    KotakBesi kb = (KotakBesi) focused;
                    if (!kb.isSolved()) {
                        clearInput();
                        puzzleSystem.open(kb.puzzle());
                        interactCooldown = INPUT_COOLDOWN;
                        return;
                    }
                }

                boolean prevGate = gerbangTimur.isGateOpen();
                int prevClues = investigation.discoveredCount();
                Dialogue d = focused.interact(investigation);
                if (investigation.discoveredCount() > prevClues) {
                    quest.onClueDiscovered(investigation.discoveredCount());
                    autosave();
                }
                if (!prevGate && gerbangTimur.isGateOpen()) {
                    autosave();
                }
                dialogue.open(d);
                clearInput();
                interactCooldown = INPUT_COOLDOWN;
            }
            return;
        }

        setMovementKey(key, keyCode, true);
    }

    @Override
    public void keyReleased(char key, int keyCode) {
        setMovementKey(key, keyCode, false);
    }

    @Override
    public void mousePressed(float x, float y) {
        if (puzzleSystem.isOpen()) {
            puzzleSystem.mousePressed(x, y);
        } else if (investigation.isCasebookOpen()) {
            investigation.mousePressed(x, y);
        }
    }

    @Override
    public void mouseMoved(float x, float y) {
        if (puzzleSystem.isOpen()) {
            puzzleSystem.mouseMoved(x, y);
        } else if (investigation.isCasebookOpen()) {
            investigation.mouseMoved(x, y);
        }
    }

    @Override
    public void focusLost() {
        clearInput();
    }

    private void setMovementKey(char key, int keyCode, boolean pressed) {
        if (keyCode == PConstants.UP || key == 'w' || key == 'W') {
            keyUp = pressed;
        }
        if (keyCode == PConstants.DOWN || key == 's' || key == 'S') {
            keyDown = pressed;
        }
        if (keyCode == PConstants.LEFT || key == 'a' || key == 'A') {
            keyLeft = pressed;
        }
        if (keyCode == PConstants.RIGHT || key == 'd' || key == 'D') {
            keyRight = pressed;
        }
    }

    private static boolean isInteract(char key, int keyCode) {
        return key == 'e' || key == 'E' || key == ' ' || key == 10 || key == 13
                || keyCode == 10 || keyCode == 13 || keyCode == 32;
    }

    private static boolean isEscape(char key, int keyCode) {
        return key == 27 || keyCode == 27;
    }
}
