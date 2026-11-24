import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * Scene gameplay Stage 07+: Konten, Polish, dan Validasi.
 *
 * Mengintegrasikan The Silent Manor dengan:
 * - Enam area terhubung: Aula Manor, Hollow Woods (Bab 2),
 *   Jalur Tebing / Dasar Lembah / Lintasan Reruntuhan (Bab 3),
 *   dan Sayap Timur (Arsip &amp; Altar Rekonstruksi)
 * - 4 bukti investigasi aula dan Detective Casebook
 * - Quest "MISTERI AULA UTARA" dengan sub-tujuan lengkap
 * - Teka-teki Kotak Besi Aula 3-dial angka (jawaban 2-4-9)
 * - Gerbang Sayap Timur → transisi ke Hollow Woods berkabut
 * - Batu Simbol dan Sigil Jejak untuk membuka jalur melalui hutan
 * - Lembah Berkabut dengan 3 segmen: tebing, sungai, reruntuhan kuno
 * - Altar Rekonstruksi Kasus dengan 3-tier ending deduksi kanonis:
 *   1. The Truth (True Ending)
 *   2. The Unfinished Case (Partial Ending)
 *   3. The False Accusation (Bad Deduction)
 * - Pause Menu (Resume, Pengaturan Cepat, Kembali ke Menu dengan konfirmasi simpan)
 * - Autosave otomatis pada peristiwa penting (clue, puzzle, gate, area transition, exit)
 * - Pemulihan state utuh (areaId, posisi, clue, quest, puzzle, gate, hutan) saat Load/Lanjutkan
 */
public final class GameplayScene extends Scene {

    private static final int HEADER_H = 15;
    private static final int HINT_H = 12;
    private static final int DUST_COUNT = 16;
    private static final float INPUT_COOLDOWN = 0.18f;

    private TileMap map;
    private final TileMap mapVillage;
    private final TileMap mapHall;
    private final TileMap mapEastWing;
    private final TileMap mapHollowWoods;
    private final TileMap mapValleyCliff;
    private final TileMap mapValleyFloor;
    private final TileMap mapValleyRuins;
    private final TileMap mapChapel;
    private final TileMap mapArchive;
    private String currentAreaId = "ashen_village";

    private final Player player;
    private final Npc npc;
    private Interactable activeSpeaker = null;
    private final List<Interactable> interactables = new ArrayList<Interactable>();
    private final List<Interactable> villageInteractables = new ArrayList<Interactable>();
    private final List<Interactable> hallInteractables = new ArrayList<Interactable>();
    private final List<Interactable> eastWingInteractables = new ArrayList<Interactable>();
    private final List<Interactable> hollowWoodsInteractables = new ArrayList<Interactable>();
    private final List<Interactable> valleyCliffInteractables = new ArrayList<Interactable>();
    private final List<Interactable> valleyFloorInteractables = new ArrayList<Interactable>();
    private final List<Interactable> valleyRuinsInteractables = new ArrayList<Interactable>();
    private final List<Interactable> chapelInteractables = new ArrayList<Interactable>();
    private final List<Interactable> archiveInteractables = new ArrayList<Interactable>();

    private final DialogueSystem dialogue = new DialogueSystem();
    private final InvestigationSystem investigation = new InvestigationSystem();
    private final QuestSystem quest = new QuestSystem();
    private final PuzzleSystem puzzleSystem = new PuzzleSystem();
    private final DeductionSystem deductionSystem = new DeductionSystem();

    private final Puzzle puzzleKotak;
    private final KotakBesi kotakBesi;
    private final GerbangTimur gerbangTimur;
    private final AltarDeduksi altarDeduksi;
    private final GerbangBarat gerbangBarat;
    private final GerbangHutan gerbangHutan;
    private final SymbolStone symbolStone;
    private final PapanPengumuman papanPengumuman;
    private final TetuaDesa tetuaDesa;
    private final SaksiDesa saksiDesa;
    private final GerbangDesa gerbangDesa;
    private final GerbangKapel gerbangKapel;
    private final ExamineSpot altarKapel;
    private final GerbangArsip gerbangArsip;
    private final Obor oborTebing;
    private final RestPoint restLembah;
    private final RestPoint restReruntuhan;
    private final RestPoint restKapel;
    private final RestPoint restArsip;
    private boolean expeditionCompleted = false;
    private boolean torchObtained = false;

    /** Topeng cahaya obor (dibuat sekali, dipakai ulang). */
    private PImage torchLightMask;

    // --- Musuh & Sistem Pertarungan Real-Time 2D ---
    private final List<Enemy> enemies = new ArrayList<Enemy>();
    private final List<Enemy> hollowWoodsEnemies = new ArrayList<Enemy>();
    private final BayanganHutan bayanganHutan;
    private final PenjagaJejak penjagaJejak;
    private float respawnTimer = 0f;
    private boolean respawnActive = false;
    private boolean sigilJejakObtained = false;
    private int lastProcessedSwingId = -1;
    private final Set<Enemy> hitEnemiesInCurrentSwing = new HashSet<Enemy>();

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

    // --- State Pause Menu ---
    private boolean pauseOpen = false;
    private int pauseSelected = 0;
    private boolean pauseConfirmQuit = false;
    private int pauseConfirmSelected = 1;
    private String playerName = GameManager.HERO_NAME_DEFAULT;

    public GameplayScene(GameManager game) {
        this(game, null);
    }

    public GameplayScene(GameManager game, SaveData loadData) {
        super(game);
        if (game != null && game.playerName() != null && !game.playerName().trim().isEmpty()) {
            this.playerName = game.playerName();
        }
        this.mapVillage = TileMap.ashenVillage();
        this.mapHall = TileMap.manorHall();
        this.mapEastWing = TileMap.manorEastWing();
        this.mapHollowWoods = TileMap.hollowWoods();
        this.mapValleyCliff = TileMap.valleyCliffPath();
        this.mapValleyFloor = TileMap.valleyFloor();
        this.mapValleyRuins = TileMap.valleyRuins();
        this.mapChapel = TileMap.chapelWhispers();
        this.mapArchive = TileMap.undergroundArchive();
        this.map = mapVillage;
        this.player = new Player(map, map.spawnX(), map.spawnY());
        // Obor di pintu masuk Jalur Tebing — sumber cahaya satu-satunya di kegelapan.
        this.oborTebing = new Obor(56f, 72f, 28f);

        // --- Pendaftaran Clue Aula (Kanonis) ---
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
                "KESAKSIAN KAKEK PUSTAKAWAN",
                "PUSTAKAWAN MENDENGAR SUARA LANGKAH KAKI TERGESA DI DEKAT RAK BUKU SEBELUM TENGAH MALAM.",
                "KAKEK PUSTAKAWAN",
                "DIPEROLEH DARI PERCAKAPAN INVESTIGASI DENGAN KAKEK PUSTAKAWAN AULA.",
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

        // --- Pendaftaran Quest Investigasi ---
        List<QuestObjective> objectives = new ArrayList<QuestObjective>();
        objectives.add(new QuestObjective("obj_kumpulkan_bukti", "Kumpulkan 4 bukti aula", 4));
        objectives.add(new QuestObjective("obj_pecahkan_puzzle", "Pecahkan teka-teki Kait Pengunci Aula", 1));
        objectives.add(new QuestObjective("obj_buka_gerbang", "Buka Gerbang Sayap Timur aula", 1));

        Quest questAula = new Quest("quest_investigasi_aula",
                "MISTERI AULA UTARA",
                "Selidiki aula manor, kumpulkan bukti, dan buka kait pengunci gerbang sayap timur.",
                new String[0],
                new String[]{"clue_meja_catatan", "clue_rak_harian", "clue_penjaga_kesaksian", "clue_jendela_goresan"},
                "AKSES GERBANG SAYAP TIMUR TERBUKA",
                objectives);

        quest.registerQuest(questAula);
        quest.activateQuest("quest_investigasi_aula");

        // --- Pendaftaran Puzzle Kait Pengunci ---
        String[] hints = new String[]{
                "Periksa kembali aturan angka: cari bilangan prima genap untuk digit pertama, lalu kalikan dua untuk digit kedua.",
                "Satu-satunya bilangan prima genap adalah 2. Digit kedua adalah 2 x 2 = 4. Digit ketiga adalah 3 x 3 = 9.",
                "Kombinasi kunci kait pengunci adalah 2 - 4 - 9."
        };

        this.puzzleKotak = new Puzzle("puzzle_kotak_besi",
                "KAIT PENGUNCI AULA",
                "1. DIGIT I: PRIMA GENAP TERKECIL.\n2. DIGIT II: DUA KALI LIPAT DIGIT I.\n3. DIGIT III: KUADRAT ANGKA 3 (3X3).\n4. JUMLAH DIGIT = 15.",
                new int[]{2, 4, 9},
                hints,
                "Kombinasi 2-4-9 cocok! Digit I=2, II=4, III=9. Mekanisme kotak berputar dan membuka pengait gerbang sayap timur!");

        puzzleSystem.setOnSolvedListener(new PuzzleSystem.OnSolvedListener() {
            @Override
            public void onPuzzleSolved(Puzzle p) {
                quest.onPuzzleSolved(p.id());
                // Kait pengunci di ujung kanan bawah Aula terbuka ->
                // gerbang sayap timur ikut lepas.
                gerbangTimur.unlock();
                investigation.setNotification("KAIT TERBUKA! GERBANG SAYAP TIMUR TERBUKA!", 3.5f);
                autosave();
            }
        });

        // --- Deduction System Callback (Stage 07) ---
        deductionSystem.setOnCompleteListener(new DeductionSystem.OnDeductionComplete() {
            @Override
            public void onComplete(DeductionSystem.EndingType endingType) {
                if (game != null) {
                    game.changeScene(new EndingScene(game, endingType,
                            investigation.discoveredCount(), investigation.totalCount(), puzzleKotak.isSolved()));
                }
            }
        });

        // --- Objek Interaktif Aula ---
        this.npc = new Npc(200f, 176f, "KAKEK PUSTAKAWAN", cluePenjaga,
                new Dialogue("KAKEK PUSTAKAWAN",
                        "Penyelidik... buku-buku di aula ini terasa gelisah malam ini.",
                        "Sebelum tengah malam, aku mendengar suara langkah tergesa di dekat rak buku.",
                        "Saat kuperiksa, tidak ada siapa-siapa... hanya derak jendela dan lembaran naskah yang berhamburan.",
                        "Kesaksian kakek pustakawan telah dicatat ke dalam Buku Kasus."),
                new Dialogue("KAKEK PUSTAKAWAN",
                        "Aku tetap merawat buku-buku di aula ini sampai fajar menyingsing.",
                        "Semua yang kudengar malam itu sudah kukatakan padamu.",
                        "Periksalah rak buku dan puing jendela jika kau butuh bukti nyata."));

        ExamineSpot spotMeja = new ExamineSpot(228f, 136f, 26f, "PUING JENDELA", clueMeja,
                new Dialogue("PUING JENDELA",
                        "Kaca jendela yang pecah berserakan di lantai batu ini.",
                        "Di antara pecahan kaca, ada sobekan catatan berlumur lilin.",
                        "Catatan itu memuat jadwal jaga malam dan sketsa lingkaran misterius.",
                        "Sobekan catatan telah dicatat ke dalam Buku Kasus."),
                new Dialogue("PUING JENDELA",
                        "Serpihan kaca ini sudah diperiksa dengan teliti.",
                        "Sobekan catatan jadwal jaga sudah tersimpan di Buku Kasus.",
                        "Tidak ada petunjuk lain yang tertinggal di sini."));

        ExamineSpot spotRak = new ExamineSpot(72f, 148f, 26f, "RAK BUKU", clueRak,
                new Dialogue("RAK BUKU",
                        "Di antara deretan buku tebal tanpa judul, ada jilid kecil bersampul kulit.",
                        "Sebagian halaman tengah telah dirobek dengan tergesa-gesa.",
                        "Halaman tersisa menyebut pertemuan rahasia di dekat kapel sunyi.",
                        "Buku harian usang telah dicatat ke dalam Buku Kasus."),
                new Dialogue("RAK BUKU",
                        "Rak buku berdebu yang penuh naskah tua tanpa judul.",
                        "Buku harian usang sudah kau periksa sebelumnya.",
                        "Halaman lainnya di rak ini hanya teks usang yang tidak terbaca."));

        ExamineSpot spotJendela = new ExamineSpot(384f, 148f, 30f, "KAIT RANTAI", clueJendela,
                new Dialogue("KAIT RANTAI",
                        "Kait rantai tergantung di dinding, siap mengunci pintu mana pun.",
                        "Di mata kait itu, terlihat bekas goresan logam yang masih baru.",
                        "Seseorang tampaknya memaksa gerbang ini dari arah luar.",
                        "Bekas goresan kait telah dicatat ke dalam Buku Kasus."),
                new Dialogue("KAIT RANTAI",
                        "Kait rantai menghadap ke kabut malam yang pekat.",
                        "Bekas cungkilan logam sudah tercatat rapi di Buku Kasus.",
                        "Tidak ada tanda jejak kaki baru di bawah terpaan angin luar."));

        this.kotakBesi = new KotakBesi(376f, 212f, puzzleKotak);
        this.gerbangTimur = new GerbangTimur(392f, 176f, mapHall, player, puzzleKotak, quest);

        hallInteractables.add(npc);
        hallInteractables.add(spotMeja);
        hallInteractables.add(spotRak);
        hallInteractables.add(spotJendela);
        hallInteractables.add(kotakBesi);
        hallInteractables.add(gerbangTimur);

        // --- Objek Interaktif Sayap Timur (Stage 07) ---
        this.altarDeduksi = new AltarDeduksi(192f, 64f);
        this.gerbangBarat = new GerbangBarat(24f, 104f);

        ExamineSpot memorialClara = new ExamineSpot(336f, 112f, 26f, "MEMORIAL CLARA",
                new Dialogue("MEMORIAL CLARA THORNE",
                        "Potret Clara Thorne (15 tahun) tersandar di atas meja arsip berpelitur gelap.",
                        "Setangkai bunga mawar putih diletakkan dengan hormat di samping potret.",
                        "Catatan riset Clara: 'Garrick bersekongkol dengan kultus untuk menjarah relik malam ini. Aku harus menghentikannya.'",
                        "Pengorbanan Clara demi melindungi warisan desa terpatri abadi di sini."),
                new Dialogue("MEMORIAL CLARA THORNE",
                        "Meja memorial Clara Thorne tertata rapi dalam keheningan.",
                        "Catatan riset dan potretnya membuktikan keberanian seorang anak melindungi Ashen Village.",
                        "Semua yang kau butuhkan untuk menegakkan keadilan telah tercatat."));

        ExamineSpot rakArsip = new ExamineSpot(80f, 48f, 26f, "ARSIP KUNO",
                new Dialogue("ARSIP KUNO SAYAP TIMUR",
                        "Deretan naskah kuno tersusun rapi di rak mahoni setinggi langit-langit.",
                        "Sebuah kronik tua mencatat sumpah leluhur desa untuk menjaga segel peninggalan.",
                        "Kunci segel itu hanya dapat dibuka melalui mekanisme Kait Pengunci Aula.",
                        "Catatan sejarah ini membantah kemungkinan keterlibatan pihak luar."),
                new Dialogue("ARSIP KUNO SAYAP TIMUR",
                        "Naskah-naskah kuno penjaga Ashen Village.",
                        "Informasi tentang segel relik dan mekanisme penguncinya sudah kau pelajari.",
                        "Tidak ada dokumen lain yang mencurigakan di rak ini."));

        eastWingInteractables.add(altarDeduksi);
        eastWingInteractables.add(memorialClara);
        eastWingInteractables.add(rakArsip);
        eastWingInteractables.add(gerbangBarat);

        // --- Objek Interaktif Hollow Woods (Bab 2) ---
        // Gerbang keluar hutan (sisi timur, tile 'E' di kolom 35, baris 4)
        this.gerbangHutan = new GerbangHutan(568f, 72f);

        // Batu simbol (tile 'X' di kolom 13, baris 9)
        this.symbolStone = new SymbolStone(216f, 152f, gerbangHutan);

        // Spot periksa: jejak simbol di batu dekat gerbang masuk
        ExamineSpot jejakSimbol = new ExamineSpot(72f, 72f, 26f, "BATU PENANDA",
                new Dialogue("BATU PENANDA",
                        "Sebuah batu pipih tertanam di tanah, nyaris tertutup lumut.",
                        "Goresan samar pada permukaannya membentuk pola panah mengarah ke timur.",
                        "Di bawah panah terukir simbol lingkaran dengan titik di tengah — tanda kuno penjaga hutan.",
                        "Simbol ini terasa familiar... seperti yang pernah disebut dalam buku harian usang di aula."),
                new Dialogue("BATU PENANDA",
                        "Batu penanda dengan simbol panah dan lingkaran kuno.",
                        "Arah panah menunjuk ke timur, lebih dalam ke hutan."));

        // Spot periksa: sisa perapian lama di dekat bebatuan (rocks at col ~12, row 6-7)
        ExamineSpot sisaPerapian = new ExamineSpot(184f, 104f, 26f, "SISA PERAPIAN",
                new Dialogue("SISA PERAPIAN LAMA",
                        "Abu yang sudah lama dingin berserakan di lingkaran batu.",
                        "Seseorang pernah berkemah di sini — mungkin sudah bertahun-tahun yang lalu.",
                        "Di antara abu, kau menemukan pecahan tembikar dengan motif yang sama seperti pada batu simbol.",
                        "Hutan ini pernah menjadi jalur yang sering dilalui, jauh sebelum kabut menyelimutinya."),
                new Dialogue("SISA PERAPIAN LAMA",
                        "Lingkaran batu berisi abu dingin dan pecahan tembikar bermotif kuno.",
                        "Tak ada yang baru di sini selain kenangan perjalanan yang sudah lama berakhir."));

        // Spot periksa: pohon tua dengan ukiran di area kabut (col ~28, row 4)
        ExamineSpot pohonTua = new ExamineSpot(456f, 72f, 26f, "POHON TUA",
                new Dialogue("POHON TUA BERUKIRAN",
                        "Batang pohon raksasa ini diukir dengan garis-garis vertikal yang teratur.",
                        "Di antara goresan itu ada simbol yang lebih dalam — seperti peringatan atau larangan.",
                        "Goresan terbaru terlihat lebih segar dari yang lain... seseorang melewati sini belum lama.",
                        "Kabut berputar lebih pekat di sekitar pohon ini, seakan menjaga batas wilayah."),
                new Dialogue("POHON TUA BERUKIRAN",
                        "Pohon besar dengan goresan kuno dan simbol peringatan.",
                        "Kabut tetap berputar pekat di sekelilingnya."));

        hollowWoodsInteractables.add(symbolStone);
        hollowWoodsInteractables.add(gerbangHutan);
        hollowWoodsInteractables.add(jejakSimbol);
        hollowWoodsInteractables.add(sisaPerapian);
        hollowWoodsInteractables.add(pohonTua);

        // --- Musuh Hollow Woods (Bab 2): Regular Enemy & Mini-Boss Penjaga Jejak ---
        this.bayanganHutan = new BayanganHutan(312f, 72f);
        this.penjagaJejak = new PenjagaJejak(480f, 72f);
        this.hollowWoodsEnemies.add(bayanganHutan);
        this.hollowWoodsEnemies.add(penjagaJejak);

        // --- Objek Interaktif Valley — Jalur Tebing (Bab 3A) ---
        // X symbol stones: (296,168), (296,168)... let me use correct positions
        ExamineSpot tebingSimbol1 = new ExamineSpot(312f, 56f, 26f, "BATU SIMBOL TEBING",
                new Dialogue("BATU SIMBOL TEBING",
                        "Batu besar menjorok keluar dari dinding tebing.",
                        "Simbol panah dan lingkaran terukir di permukaannya — sama seperti di hutan.",
                        "Orientasi panah mengarah ke bawah dan ke kanan, menandai jalur aman."),
                new Dialogue("BATU SIMBOL TEBING",
                        "Simbol panah dan lingkaran kuno. Menunjuk jalur turun ke kanan."));

        ExamineSpot tebingSimbol2 = new ExamineSpot(296f, 168f, 26f, "BATU SIMBOL BAWAH",
                new Dialogue("BATU SIMBOL BAWAH",
                        "Batu lain dengan ukiran serupa, tertanam di lereng bawah.",
                        "Di samping panah arah ada goresan tambahan — dua garis sejajar.",
                        "Mungkin tanda peringatan? Atau penanda jarak dari tujuan?"),
                new Dialogue("BATU SIMBOL BAWAH",
                        "Batu simbol dengan panah dan dua garis sejajar."));

        ExamineSpot tebingSimbol3 = new ExamineSpot(392f, 168f, 26f, "BATU SIMBOL KETIGA",
                new Dialogue("BATU SIMBOL KETIGA",
                        "Batu ketiga dalam rangkaian. Simbol di sini lebih rumit.",
                        "Selain panah, ada pola bintang kecil dan garis melengkung.",
                        "Ketiganya membentuk urutan — seperti kode navigasi untuk melewati lembah."),
                new Dialogue("BATU SIMBOL KETIGA",
                        "Batu simbol ketiga. Panah, bintang, dan lengkungan — kode navigasi."));

        GerbangLembah gerbangTebing = new GerbangLembah(616f, 72f, "DASAR LEMBAH");

        valleyCliffInteractables.add(tebingSimbol1);
        valleyCliffInteractables.add(tebingSimbol2);
        valleyCliffInteractables.add(tebingSimbol3);
        valleyCliffInteractables.add(oborTebing);
        valleyCliffInteractables.add(gerbangTebing);

        // --- Objek Interaktif Valley — Dasar Lembah (Bab 3B) ---
        ExamineSpot lembahSimbol = new ExamineSpot(408f, 168f, 26f, "BATU SIMBOL LEMBAH",
                new Dialogue("BATU SIMBOL LEMBAH",
                        "Di antara rerumputan dan aliran air, sebuah batu penanda muncul.",
                        "Simbol yang diukir di sini berbeda — pola spiral ke dalam.",
                        "Kabut di sekitarnya berputar mengikuti arah spiral, seakan terhubung.",
                        "Beberapa jejak di tanah mengarah ke simbol palsu di dekat kabut. Hati-hati."),
                new Dialogue("BATU SIMBOL LEMBAH",
                        "Batu simbol dengan pola spiral. Kabut berputar di sekelilingnya."));

        restLembah = new RestPoint(328f, 128f, RestPoint.Style.BRAZIER);

        GerbangLembah gerbangLembah = new GerbangLembah(600f, 72f, "LINTASAN RERUNTUHAN");

        valleyFloorInteractables.add(lembahSimbol);
        valleyFloorInteractables.add(restLembah);
        valleyFloorInteractables.add(gerbangLembah);

        // --- Objek Interaktif Valley — Lintasan Reruntuhan (Bab 3C) ---
        ExamineSpot reruntuhanSimbol = new ExamineSpot(337f, 124f, 26f, "BATU SIMBOL RERUNTUHAN",
                new Dialogue("BATU SIMBOL RERUNTUHAN",
                        "Batu paling besar di antara semua yang kau temui.",
                        "Simbol di sini menggabungkan semua pola sebelumnya: panah, spiral, bintang.",
                        "Sebuah teks kuno terukir di bawahnya — huruf-huruf yang nyaris terhapus waktu.",
                        "\"...jangan buka yang tersegel, kecuali kebenaran sudah kau genggam.\""),
                new Dialogue("BATU SIMBOL RERUNTUHAN",
                        "Batu simbol besar dengan gabungan semua pola dan pesan kuno."));

        // Brazier di atas pedestal altar (y=184). X=273 rapih di tengah pedestal
        // batu. Jangan diturunkan ke y=192: blocker-nya akan menutupi seluruh
        // baris tile 11 (y176..191), satu-satunya jalur walkable di atas altar.
        restReruntuhan = new RestPoint(273f, 184f, RestPoint.Style.BRAZIER);

        GerbangLembah gerbangReruntuhan = new GerbangLembah(600f, 72f, "CHAPEL OF WHISPERS");

        valleyRuinsInteractables.add(reruntuhanSimbol);
        valleyRuinsInteractables.add(restReruntuhan);
        valleyRuinsInteractables.add(gerbangReruntuhan);

        // --- Objek Interaktif Ashen Village (Prolog) ---
        // Posisi mengikuti gambar latar: papan di kol 18, sumur di kol 20,
        // saksi desa di sebelah kiri sumur (kol 19, menghadap sumur),
        // tetua desa di dekat jalan, dan gerbang di ujung jalan kanan (kol 35-36).
        this.papanPengumuman = new PapanPengumuman(296f, 88f);
        this.tetuaDesa = new TetuaDesa(192f, 128f);
        this.saksiDesa = new SaksiDesa(312f, 86f);
        this.gerbangDesa = new GerbangDesa(568f, 128f);

        ExamineSpot sumurDesa = new ExamineSpot(330f, 76f, 26f, "SUMUR TUA DESA",
                new Dialogue("SUMUR TUA ASHEN VILLAGE",
                        "Sumur batu tua di tengah pemukiman warga.",
                        "Airnya dingin dan jernih, merefleksikan langit kelabu berkabut.",
                        "Warga desa berkumpul di sini setiap pagi sebelum kabar duka tentang Clara tersebar."),
                new Dialogue("SUMUR TUA ASHEN VILLAGE",
                        "Sumur tua yang tenang. Riak airnya memantulkan kabut tebal."));

        ExamineSpot rumahWarga = new ExamineSpot(104f, 68f, 26f, "PINTU RUMAH WARGA",
                new Dialogue("PINTU RUMAH WARGA",
                        "Pintu kayu berderit tertutup rapat.",
                        "Penduduk desa memilih tinggal di dalam rumah sejak peristiwa kelam menimpa keluarga Thorne.",
                        "Hanya bisik-bisik ketakutan yang terdengar dari balik dinding kayu."),
                new Dialogue("PINTU RUMAH WARGA",
                        "Pintu rumah tertutup rapat dari dalam."));

        villageInteractables.add(papanPengumuman);
        villageInteractables.add(tetuaDesa);
        villageInteractables.add(saksiDesa);
        villageInteractables.add(gerbangDesa);
        villageInteractables.add(sumurDesa);
        villageInteractables.add(rumahWarga);

        // --- Objek Interaktif Chapel of Whispers (Bab 4) ---
        this.gerbangKapel = new GerbangKapel(616f, 72f);

        this.altarKapel = new ExamineSpot(504f, 72f, 28f, "ALTAR KAPEL",
                new Dialogue("ALTAR KAPEL SUNYI",
                        "Altar marmer hitam dengan ukiran relief kuno.",
                        "Sebuah plat perunggu tertanam di tengahnya, menggambarkan susunan simbol penjaga.",
                        "Saat menyentuh lekukan plat, terdengar suara mekanisme batu bergeser di sisi timur!",
                        "Pintu batu menuju Arsip Bawah Tanah kini terbuka!"),
                new Dialogue("ALTAR KAPEL SUNYI",
                        "Altar marmer hitam. Mekanisme pengunci pintu batu telah terbuka."));

        // Altar kapel memakai brazier masonry yang sama dengan area lembah.
        // Tapaknya di (490,149) supaya duduk rapih di atas lempengan batu altar
        // (sisa lilin lama di bg_chapel_whispers.png sudah dibersihkan).
        restKapel = new RestPoint(490f, 149f, RestPoint.Style.BRAZIER);

        ExamineSpot chapelSimbol = new ExamineSpot(312f, 136f, 26f, "SIMBOL KONTUR KAPEL",
                new Dialogue("SIMBOL KONTUR KAPEL",
                        "Relief batu gotik yang menggambarkan simbol kultus masa lalu.",
                        "Bentuknya menyerupai simbol di lembah, namun terukir dengan motif tanaman berduri.",
                        "Catatan sejarah kapel menegaskan kultus telah lama bubar — bukan ancaman baru."),
                new Dialogue("SIMBOL KONTUR KAPEL",
                        "Relief simbol kultus masa lampau. Hanya konteks sejarah yang tersisa."));

        ExamineSpot kacaPatri = new ExamineSpot(144f, 24f, 26f, "KACA PATRI GOTIK",
                new Dialogue("KACA PATRI GOTIK",
                        "Kaca patri berwarna merah dan biru tua yang membiaskan cahaya redup ke lantai kapel.",
                        "Pola kaca membentuk figur pelindung desa yang memegang kunci kuno."),
                new Dialogue("KACA PATRI GOTIK",
                        "Kaca patri membiaskan cahaya temaram ke lantai kapel."));

        ExamineSpot bangkuKapel = new ExamineSpot(80f, 40f, 26f, "BANGKU JEMAAT",
                new Dialogue("BANGKU JEMAAT",
                        "Deretan bangku kayu tua berlapis debu tebal.",
                        "Sebuah sobekan kain hitam tersangkut di ujung sandaran bangku."),
                new Dialogue("BANGKU JEMAAT",
                        "Bangku kayu berdebu dalam keheningan kapel."));

        chapelInteractables.add(gerbangKapel);
        chapelInteractables.add(altarKapel);
        chapelInteractables.add(restKapel);
        chapelInteractables.add(chapelSimbol);
        chapelInteractables.add(kacaPatri);
        chapelInteractables.add(bangkuKapel);

        // --- Objek Interaktif Underground Archive (Bab 5) ---
        this.gerbangArsip = new GerbangArsip(600f, 72f);

        ExamineSpot arsipMekanisme = new ExamineSpot(264f, 40f, 26f, "MEKANISME BATU KUNO",
                new Dialogue("MEKANISME BATU KUNO",
                        "Roda-roda gigi tembaga besar tertanam di dinding batu ruang arsip.",
                        "Mekanisme ini terhubung dengan lorong pelarian rahasia yang mengarah langsung ke The Silent Manor.",
                        "Tuas pengungkit telah diturunkan, membuka akses jalan pintas kembali ke aula."),
                new Dialogue("MEKANISME BATU KUNO",
                        "Mekanisme lorong rahasia telah aktif. Jalur ke aula manor terbuka."));

        ExamineSpot dokumenKultus = new ExamineSpot(64f, 40f, 26f, "DOKUMEN KULTUS KUNO",
                new Dialogue("DOKUMEN KULTUS KUNO",
                        "Gulungan perkamen yang tersimpan di dalam tabung perak.",
                        "Isinya membuktikan sekte lembah telah tidak aktif selama lebih dari dua dekade.",
                        "Kematian Clara bukan ulah kultus luar — seseorang di dalam manor mencoba menjadikannya kambing hitam."),
                new Dialogue("DOKUMEN KULTUS KUNO",
                        "Perkamen yang membantah keterlibatan sekte luar dalam tragedi ini."));

        ExamineSpot mejaArsip = new ExamineSpot(384f, 72f, 26f, "MEJA KATALOG ARSIP",
                new Dialogue("MEJA KATALOG ARSIP",
                        "Buku katalog tebal yang mencatat setiap berkas rahasia manor.",
                        "Catatan inventaris ruang Sayap Timur tercatat rapi di sini.",
                        "Ada paraf bendahara manor yang cocok dengan goresan pada catatan jadwal jaga."),
                new Dialogue("MEJA KATALOG ARSIP",
                        "Buku katalog inventaris dan catatan rahasia manor."));

        restArsip = new RestPoint(104f, 216f, RestPoint.Style.BRAZIER);

        ExamineSpot batuArsip = new ExamineSpot(616f, 200f, 26f, "PRASASTI LELUHUR",
                new Dialogue("PRASASTI LELUHUR THORNE",
                        "Prasasti batu bertuliskan sumpah perlindungan Ashen Village.",
                        "\"Hanya kejujuran dan keberanian yang berhak mengungkap apa yang tersimpan di Sayap Timur.\""),
                new Dialogue("PRASASTI LELUHUR THORNE",
                        "Prasasti sumpah perlindungan keluarga Thorne."));

        archiveInteractables.add(gerbangArsip);
        archiveInteractables.add(arsipMekanisme);
        archiveInteractables.add(dokumenKultus);
        archiveInteractables.add(mejaArsip);
        archiveInteractables.add(restArsip);
        archiveInteractables.add(batuArsip);

        // Setup awal area
        setupArea(loadData != null ? loadData.areaId() : "ashen_village");

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
            investigation.setNotification("ASHEN VILLAGE — SELAMAT DATANG, " + playerName, 3.5f);
        }
    }

    public void setupArea(String areaId) {
        enemies.clear();
        if ("ashen_village".equals(areaId)) {
            currentAreaId = "ashen_village";
            map = mapVillage;
            player.setMap(mapVillage);
            player.clearBlockers();
            player.addBlocker(tetuaDesa.x(), tetuaDesa.y() - TetuaDesa.BLOCK_H / 2f, TetuaDesa.BLOCK_W, TetuaDesa.BLOCK_H);
            player.addBlocker(saksiDesa.x(), saksiDesa.y() - SaksiDesa.BLOCK_H / 2f, SaksiDesa.BLOCK_W, SaksiDesa.BLOCK_H);
            player.addBlocker(papanPengumuman.x() - 16f, papanPengumuman.y() - 16f, 32f, 32f);
            interactables.clear();
            interactables.addAll(villageInteractables);
        } else if ("the_silent_manor_east_wing".equals(areaId)) {
            currentAreaId = "the_silent_manor_east_wing";
            map = mapEastWing;
            player.setMap(mapEastWing);
            player.clearBlockers();
            interactables.clear();
            interactables.addAll(eastWingInteractables);
        } else if ("hollow_woods".equals(areaId)) {
            currentAreaId = "hollow_woods";
            map = mapHollowWoods;
            player.setMap(mapHollowWoods);
            player.clearBlockers();
            interactables.clear();
            interactables.addAll(hollowWoodsInteractables);
            enemies.addAll(hollowWoodsEnemies);
        } else if ("valley_cliff_path".equals(areaId)) {
            currentAreaId = "valley_cliff_path";
            map = mapValleyCliff;
            player.setMap(mapValleyCliff);
            player.clearBlockers();
            interactables.clear();
            interactables.addAll(valleyCliffInteractables);
        } else if ("valley_floor".equals(areaId)) {
            currentAreaId = "valley_floor";
            map = mapValleyFloor;
            player.setMap(mapValleyFloor);
            player.clearBlockers();
            // Blocker Dasar Lembah: sisi bawah diperpanjang supaya pemain
            // yang duduk di bawah brazier (frame bawah) tidak terlalu mepet.
            player.addBlocker(328f, 119f, 30f, 26f);
            interactables.clear();
            interactables.addAll(valleyFloorInteractables);
        } else if ("valley_ruins".equals(areaId)) {
            currentAreaId = "valley_ruins";
            map = mapValleyRuins;
            player.setMap(mapValleyRuins);
            player.clearBlockers();
            // Bloker Reruntuhan: jarak per arah atas=15, bawah=25, kanan=20,
            // kiri=24 px dari brazier (dihitung dari tepi kotak + hitbox pemain).
            player.addBlocker(271f, 189f, 34f, 34f);
            // Batu simbol rune (337,124): blocker pas seukuran relief-nya supaya
            // ksatria tidak bisa menimpa/melewati batu saat memeriksa.
            player.addBlocker(337f, 122f, 20f, 22f);
            interactables.clear();
            interactables.addAll(valleyRuinsInteractables);
        } else if ("chapel_of_whispers".equals(areaId)) {
            currentAreaId = "chapel_of_whispers";
            map = mapChapel;
            player.setMap(mapChapel);
            player.clearBlockers();
            interactables.clear();
            interactables.addAll(chapelInteractables);
        } else if ("underground_archive".equals(areaId)) {
            currentAreaId = "underground_archive";
            map = mapArchive;
            player.setMap(mapArchive);
            player.clearBlockers();
            interactables.clear();
            interactables.addAll(archiveInteractables);
        } else {
            currentAreaId = "the_silent_manor_hall";
            map = mapHall;
            player.setMap(mapHall);
            player.clearBlockers();
            player.addBlocker(npc.x(), npc.y() - Npc.BLOCK_H / 2f, Npc.BLOCK_W, Npc.BLOCK_H);
            // Blocker fisik hanya seluas mekanisme kait yang terlihat di latar
            // (sekitar x=372..388, y=209..235), bukan area di sekitarnya.
            player.addBlocker(kotakBesi.x() + 4f, kotakBesi.y() + 10f, 16f, 26f);
            if (!gerbangTimur.isGateOpen()) {
                player.addBlocker(gerbangTimur.x(), gerbangTimur.y(), 16f, 32f);
            }
            interactables.clear();
            interactables.addAll(hallInteractables);
        }
    }

    public void transitionToVillage() {
        setupArea("ashen_village");
        player.setPosition(mapVillage.spawnX(), mapVillage.spawnY());
        player.setFacing(Player.Facing.RIGHT);
        centerCameraOnPlayer();
        investigation.setNotification("PROLOG: ASHEN VILLAGE — DESA BERKABUT", 3.5f);
        autosave();
    }

    public void transitionToHallFromVillage() {
        setupArea("the_silent_manor_hall");
        player.setPosition(mapHall.spawnX(), mapHall.spawnY());
        player.setFacing(Player.Facing.UP);
        centerCameraOnPlayer();
        investigation.setNotification("BAB 1: MEMASUKI THE SILENT MANOR — AULA UTARA", 3.5f);
        autosave();
    }

    public void transitionToChapel() {
        setupArea("chapel_of_whispers");
        player.setPosition(mapChapel.spawnX(), mapChapel.spawnY());
        player.setFacing(Player.Facing.RIGHT);
        centerCameraOnPlayer();
        investigation.setNotification("BAB 4: CHAPEL OF WHISPERS — SIMBOL YANG DISALAHARTIKAN", 3.5f);
        autosave();
    }

    public void transitionToChapelFromArchive() {
        setupArea("chapel_of_whispers");
        player.setPosition(600f, 72f);
        player.setFacing(Player.Facing.LEFT);
        centerCameraOnPlayer();
        investigation.setNotification("KEMBALI KE CHAPEL OF WHISPERS", 2.5f);
        autosave();
    }

    public void transitionToArchive() {
        setupArea("underground_archive");
        player.setPosition(mapArchive.spawnX(), mapArchive.spawnY());
        player.setFacing(Player.Facing.RIGHT);
        centerCameraOnPlayer();
        investigation.setNotification("BAB 5: THE UNDERGROUND ARCHIVE — ARSIP DI BAWAH TANAH", 3.5f);
        autosave();
    }

    public void transitionToHallFromArchive() {
        expeditionCompleted = true;
        setupArea("the_silent_manor_hall");
        player.setPosition(192f, 100f);
        player.setFacing(Player.Facing.DOWN);
        centerCameraOnPlayer();
        investigation.setNotification("BAB 6: LORONG RAHASIA MEMBAWA KEMBALI KE AULA MANOR", 3.5f);
        autosave();
    }

    public void transitionToEastWing() {
        setupArea("the_silent_manor_east_wing");
        player.setPosition(56f, 104f);
        player.setFacing(Player.Facing.RIGHT);
        centerCameraOnPlayer();
        investigation.setNotification("MASUK KE SAYAP TIMUR: ARSIP & ALTAR", 3.0f);
        autosave();
    }

    public void transitionToHollowWoods() {
        setupArea("hollow_woods");
        player.setPosition(mapHollowWoods.spawnX(), mapHollowWoods.spawnY());
        player.setFacing(Player.Facing.RIGHT);
        centerCameraOnPlayer();
        investigation.setNotification("MEMASUKI HOLLOW WOODS — HUTAN BERKABUT", 3.5f);
        autosave();
    }

    public void transitionToHallFromWoods() {
        setupArea("the_silent_manor_hall");
        player.setPosition(370f, 176f);
        player.setFacing(Player.Facing.LEFT);
        centerCameraOnPlayer();
        investigation.setNotification("KEMBALI KE AULA MANOR", 2.5f);
        autosave();
    }

    public void transitionToValleyCliffPath() {
        setupArea("valley_cliff_path");
        player.setPosition(mapValleyCliff.spawnX(), mapValleyCliff.spawnY());
        player.setFacing(Player.Facing.RIGHT);
        centerCameraOnPlayer();
        investigation.setNotification("JALUR TEBING — LEMBAH BERKABUT", 3.0f);
        autosave();
    }

    public void transitionToValleyFloor() {
        setupArea("valley_floor");
        player.setPosition(mapValleyFloor.spawnX(), mapValleyFloor.spawnY());
        player.setFacing(Player.Facing.RIGHT);
        centerCameraOnPlayer();
        investigation.setNotification("DASAR LEMBAH — SUNGAI DAN KABUT", 3.0f);
        autosave();
    }

    /** Kembali dari Lintasan Reruntuhan ke ujung kanan Dasar Lembah. */
    public void transitionToValleyFloorFromRuins() {
        setupArea("valley_floor");
        player.setPosition(600f, 72f);
        player.setFacing(Player.Facing.LEFT);
        centerCameraOnPlayer();
        investigation.setNotification("KEMBALI KE DASAR LEMBAH", 2.5f);
        autosave();
    }

    /** Kembali dari Dasar Lembah ke ujung kanan Jalur Tebing (nyambung). */
    public void transitionToValleyCliffPathFromFloor() {
        setupArea("valley_cliff_path");
        player.setPosition(600f, 72f);
        player.setFacing(Player.Facing.LEFT);
        centerCameraOnPlayer();
        investigation.setNotification("KEMBALI KE JALUR TEBING", 2.5f);
        autosave();
    }

    public void transitionToValleyRuins() {
        setupArea("valley_ruins");
        player.setPosition(mapValleyRuins.spawnX(), mapValleyRuins.spawnY());
        player.setFacing(Player.Facing.RIGHT);
        centerCameraOnPlayer();
        investigation.setNotification("LINTASAN RERUNTUHAN — RERUNTUHAN KUNO", 3.0f);
        autosave();
    }

    /** Kembali dari Chapel of Whispers ke ujung kanan Lintasan Reruntuhan. */
    public void transitionToValleyRuinsFromChapel() {
        setupArea("valley_ruins");
        player.setPosition(600f, 72f);
        player.setFacing(Player.Facing.LEFT);
        centerCameraOnPlayer();
        investigation.setNotification("KEMBALI KE LINTASAN RERUNTUHAN", 2.5f);
        autosave();
    }

    public void transitionToValleyFromCliff() {
        setupArea("hollow_woods");
        // Kembali ke dekat exit timur hutan
        player.setPosition(540f, 72f);
        player.setFacing(Player.Facing.LEFT);
        centerCameraOnPlayer();
        investigation.setNotification("KEMBALI KE HOLLOW WOODS", 2.5f);
        autosave();
    }

    public void transitionToHall() {
        setupArea("the_silent_manor_hall");
        player.setPosition(370f, 176f);
        player.setFacing(Player.Facing.LEFT);
        centerCameraOnPlayer();
        investigation.setNotification("KEMBALI KE AULA MANOR", 2.5f);
        autosave();
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

    public boolean isDeductionOpen() {
        return deductionSystem.isOpen();
    }

    public boolean isPauseOpen() {
        return pauseOpen;
    }

    public DeductionSystem deductionSystem() {
        return deductionSystem;
    }

    public String currentAreaId() {
        return currentAreaId;
    }

    public InvestigationSystem investigation() {
        return investigation;
    }

    public QuestSystem questSystem() {
        return quest;
    }

    public QuestSystem quest() {
        return quest;
    }

    public GerbangHutan gerbangHutan() {
        return gerbangHutan;
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

    public AltarDeduksi altarDeduksi() {
        return altarDeduksi;
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

    public void openPauseMenu() {
        clearInput();
        pauseOpen = true;
        pauseSelected = 0;
        pauseConfirmQuit = false;
        pauseConfirmSelected = 1;
    }

    public void closePauseMenu() {
        pauseOpen = false;
        pauseConfirmQuit = false;
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

    public String playerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.playerName = name.trim();
        }
    }

    public PenjagaJejak penjagaJejak() {
        return penjagaJejak;
    }

    public BayanganHutan bayanganHutan() {
        return bayanganHutan;
    }

    public List<Enemy> enemies() {
        return enemies;
    }

    public boolean isSigilJejakObtained() {
        return sigilJejakObtained;
    }

    public void setSigilJejakObtained(boolean obtained) {
        this.sigilJejakObtained = obtained;
    }

    public boolean isRespawnActive() {
        return respawnActive;
    }

    public void setWorldFlag(String key, boolean val) {
        if ("sigil_jejak_obtained".equals(key)) {
            this.sigilJejakObtained = val;
        } else if ("penjaga_jejak_defeated".equals(key) && penjagaJejak != null) {
            penjagaJejak.setDefeated(val);
        } else if ("bayangan_hutan_defeated".equals(key) && bayanganHutan != null) {
            bayanganHutan.setDefeated(val);
        }
    }

    /**
     * Memeriksa apakah modal/dialog/puzzle/casebook/deduksi/pause sedang aktif.
     */
    public boolean isModalOpen() {
        return dialogue.isOpen() || investigation.isCasebookOpen()
                || puzzleSystem.isOpen() || deductionSystem.isOpen() || pauseOpen;
    }

    /**
     * Memicu serangan tebasan sihir senjata saku pemain.
     * Dinonaktifkan saat modal/dialog terbuka atau saat respawn.
     */
    public boolean triggerPlayerAttack() {
        if (isModalOpen() || player.isDead() || respawnActive) {
            return false;
        }
        boolean swung = player.attack();
        if (swung) {
            checkPlayerAttackHit();
        }
        return swung;
    }

    private void checkPlayerAttackHit() {
        float[] ab = player.getAttackHitbox();
        if (ab == null) {
            return;
        }

        int swingId = player.swingId();
        if (swingId != lastProcessedSwingId) {
            lastProcessedSwingId = swingId;
            hitEnemiesInCurrentSwing.clear();
        }

        // Cari musuh terdekat yang belum terkena damage pada tebasan ayunan pedang ini
        Enemy closestEnemy = null;
        float closestDistSq = Float.MAX_VALUE;

        for (Enemy e : enemies) {
            if (!e.isAlive() || hitEnemiesInCurrentSwing.contains(e)) {
                continue;
            }
            float[] eb = e.getHitbox();
            if (rectOverlap(ab[0], ab[1], ab[2], ab[3], eb[0], eb[1], eb[2], eb[3])) {
                float dx = e.x() - player.x();
                float dy = e.y() - player.y();
                float distSq = dx * dx + dy * dy;
                if (distSq < closestDistSq) {
                    closestDistSq = distSq;
                    closestEnemy = e;
                }
            }
        }

        if (closestEnemy != null) {
            float knockX = (player.attackFacing() == Player.Facing.LEFT) ? -1f : (player.attackFacing() == Player.Facing.RIGHT ? 1f : 0f);
            float knockY = (player.attackFacing() == Player.Facing.UP) ? -1f : (player.attackFacing() == Player.Facing.DOWN ? 1f : 0f);
            boolean hit = closestEnemy.takeDamage(player.attackDamage(), knockX, knockY);
            if (hit) {
                hitEnemiesInCurrentSwing.add(closestEnemy);
                if (closestEnemy.isDefeated()) {
                    closestEnemy.onDefeated(this);
                }
            }
        }
    }

    private static boolean rectOverlap(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
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
        worldFlags.put("symbol_stone_examined", symbolStone.isExamined());
        worldFlags.put("forest_fog_cleared", gerbangHutan.isFogCleared());
        worldFlags.put("village_board_examined", papanPengumuman.isExamined());
        worldFlags.put("village_elder_spoken", tetuaDesa.hasSpoken());
        worldFlags.put("village_witness_spoken", saksiDesa.hasSpoken());
        worldFlags.put("village_gate_open", gerbangDesa.canProceed());
        worldFlags.put("chapel_gate_unlocked", gerbangKapel.isUnlocked());
        worldFlags.put("expedition_completed", expeditionCompleted);
        worldFlags.put("bayangan_hutan_defeated", bayanganHutan != null && bayanganHutan.isDefeated());
        worldFlags.put("penjaga_jejak_defeated", penjagaJejak != null && penjagaJejak.isDefeated());
        worldFlags.put("sigil_jejak_obtained", sigilJejakObtained);
        worldFlags.put("torch_obtained", torchObtained);

        return new SaveData(
                SaveData.CURRENT_SCHEMA_VERSION,
                System.currentTimeMillis(),
                currentAreaId,
                player.x(),
                player.y(),
                player.facing(),
                clues,
                quest.activeQuestId(),
                questSaves,
                puzzleSaves,
                worldFlags,
                playerName
        );
    }

    public SaveData createSaveData() {
        return captureCurrentState();
    }

    public void restoreFrom(SaveData data) {
        if (data == null) {
            return;
        }

        setupArea(data.areaId());

        // 1. Posisi dan arah hadap pemain
        player.setPosition(data.playerX(), data.playerY());
        player.setFacing(data.playerFacing());
        if (data.playerName() != null && !data.playerName().trim().isEmpty()) {
            this.playerName = data.playerName();
        }

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

        // 4. Teka-teki Kait Pengunci
        SaveData.PuzzleSave ps = data.puzzle(puzzleKotak.id());
        if (ps != null) {
            puzzleKotak.restoreState(ps.isSolved(), ps.wrongAttempts(), ps.hintLevel());
        }

        // 5. World flags (status gerbang sayap timur dan hutan)
        boolean gateOpen = data.isWorldFlag("gate_east_open", false);
        if (gateOpen || (ps != null && ps.isSolved() && quest.isQuestCompleted("quest_investigasi_aula"))) {
            gerbangTimur.restoreOpen(true);
            mapHall.setGateOpen(true);
        }

        // 6. Status Hollow Woods
        boolean symbolExamined = data.isWorldFlag("symbol_stone_examined", false);
        if (symbolExamined) {
            symbolStone.restoreExamined(true);
        }
        boolean fogCleared = data.isWorldFlag("forest_fog_cleared", false);
        if (fogCleared) {
            gerbangHutan.clearFog();
        }
        boolean bayanganDefeated = data.isWorldFlag("bayangan_hutan_defeated", false);
        if (bayanganDefeated && bayanganHutan != null) {
            bayanganHutan.setDefeated(true);
        }
        boolean penjagaDefeated = data.isWorldFlag("penjaga_jejak_defeated", false);
        if (penjagaDefeated && penjagaJejak != null) {
            penjagaJejak.setDefeated(true);
        }
        boolean sigilObtained = data.isWorldFlag("sigil_jejak_obtained", false);
        if (sigilObtained) {
            sigilJejakObtained = true;
            gerbangHutan.clearFog();
        }
        torchObtained = data.isWorldFlag("torch_obtained", false);
        oborTebing.restorePickedUp(torchObtained);

        // 7. Status Ashen Village
        boolean boardExamined = data.isWorldFlag("village_board_examined", false);
        if (boardExamined) {
            papanPengumuman.restoreExamined(true);
        }
        boolean elderSpoken = data.isWorldFlag("village_elder_spoken", false);
        if (elderSpoken) {
            tetuaDesa.restoreSpoken(true);
        }
        boolean witnessSpoken = data.isWorldFlag("village_witness_spoken", false);
        if (witnessSpoken) {
            saksiDesa.restoreSpoken(true);
        }
        boolean villageGate = data.isWorldFlag("village_gate_open", false);
        if (villageGate || (boardExamined && elderSpoken)) {
            gerbangDesa.setCanProceed(true);
            mapVillage.setGateOpen(true);
        }

        // 8. Status Chapel & Archive
        boolean chapelGate = data.isWorldFlag("chapel_gate_unlocked", false);
        if (chapelGate) {
            gerbangKapel.restoreUnlocked(true);
            mapChapel.setGateOpen(true);
        }
        this.expeditionCompleted = data.isWorldFlag("expedition_completed", false);

        // Bersihkan notifikasi awal agar layar bersih saat baru dimuat
        investigation.clearNotification();
        quest.clearNotification();
        centerCameraOnPlayer();
    }

    @Override
    public void onEnter() {
        clearInput();
        dialogue.close();
        onDialogueClosed();
        investigation.closeCasebook();
        puzzleSystem.close();
        deductionSystem.close();
        closePauseMenu();
        focused = null;
        centerCameraOnPlayer();
    }

    @Override
    public void onExit() {
        autosave();
        clearInput();
        dialogue.close();
        onDialogueClosed();
        investigation.closeCasebook();
        puzzleSystem.close();
        deductionSystem.close();
        closePauseMenu();
    }

    private void onDialogueClosed() {
        if (activeSpeaker == npc) {
            npc.goHome();
        } else if (activeSpeaker == tetuaDesa) {
            tetuaDesa.goHome();
        } else if (activeSpeaker == saksiDesa) {
            saksiDesa.goHome();
        }
        activeSpeaker = null;
    }

    private void clearInput() {
        keyUp = false;
        keyDown = false;
        keyLeft = false;
        keyRight = false;
    }

    /**
     * Membuka gerbang desa (tile 'G' di ujung jalan kanan) begitu prologue
     * selesai: papan pengumuman sudah dibaca dan tetua desa sudah bicara.
     */
    private void syncVillageGate() {
        if (gerbangDesa.canProceed()) {
            mapVillage.setGateOpen(true);
        }
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

        if (respawnActive) {
            respawnTimer -= dt;
            if (respawnTimer <= 0f) {
                respawnActive = false;
                player.respawn(map.spawnX(), map.spawnY());
                if (bayanganHutan != null) {
                    bayanganHutan.resetCombat();
                }
                if (penjagaJejak != null) {
                    penjagaJejak.resetCombat();
                }
                quest.setNotification("Bangkit kembali di titik aman.");
            }
            return;
        }

        // Bangkit dari duduk ketika dialog selesai atau modal lain terbuka.
        if (player.isSitting() && !dialogue.isOpen()) {
            player.setSitting(false);
        }

        float inputX = 0f;
        float inputY = 0f;
        // Kunci gerakan pemain jika modal terbuka
        if (!isModalOpen()) {
            inputX = (keyRight ? 1f : 0f) - (keyLeft ? 1f : 0f);
            inputY = (keyDown ? 1f : 0f) - (keyUp ? 1f : 0f);
        }
        player.update(dt, inputX, inputY);

        if ("the_silent_manor_hall".equals(currentAreaId)) {
            npc.update(dt);
        } else if ("ashen_village".equals(currentAreaId)) {
            tetuaDesa.update(dt);
            saksiDesa.update(dt);
        }

        // Update musuh & deteksi tabrakan serangan musuh ke pemain
        if (!isModalOpen()) {
            if (player.isAttacking()) {
                checkPlayerAttackHit();
            }
            for (Enemy e : enemies) {
                e.update(dt, player, map);
                if (e.isAttacking() && player.isAlive() && !respawnActive) {
                    float[] eb = e.getAttackHitbox();
                    if (eb != null) {
                        float[] pb = player.getHitbox();
                        if (rectOverlap(eb[0], eb[1], eb[2], eb[3], pb[0], pb[1], pb[2], pb[3])) {
                            boolean damaged = player.takeDamage(e.damage(), e.x(), e.y());
                            if (damaged && player.isDead()) {
                                respawnActive = true;
                                respawnTimer = 1.3f;
                            }
                        }
                    }
                }
            }
        }

        // Transisi otomatis antar-ruang saat melangkah ke ambang gerbang terbuka
        syncVillageGate();
        if (currentAreaId.equals("ashen_village") && gerbangDesa.canProceed()) {
            if (player.x() >= 584f) {
                clearInput();
                transitionToHallFromVillage();
            }
        } else if (currentAreaId.equals("the_silent_manor_hall") && gerbangTimur.isGateOpen()) {
            // Gerbang di tangga kanan Aula (kol 24, baris 10-11):
            // sejajar dengan tangga dan lurus ke kanan dari tengah aula.
            if (player.y() >= 155f && player.y() <= 195f && player.x() >= 388f) {
                clearInput();
                if (expeditionCompleted) {
                    transitionToEastWing();
                } else {
                    transitionToHollowWoods();
                }
            }
        } else if (currentAreaId.equals("hollow_woods")) {
            // Kembali ke aula dari sisi kiri hutan (tile G di col 0)
            if (player.x() <= 8f) {
                clearInput();
                transitionToHallFromWoods();
            }
        } else if (currentAreaId.equals("valley_cliff_path")) {
            if (player.x() <= 8f) {
                clearInput();
                transitionToValleyFromCliff();
            }
        } else if (currentAreaId.equals("valley_floor")) {
            if (player.x() <= 8f) {
                clearInput();
                transitionToValleyCliffPathFromFloor();
            }
        } else if (currentAreaId.equals("valley_ruins")) {
            if (player.x() <= 8f) {
                clearInput();
                transitionToValleyFloorFromRuins();
            } else if (player.x() >= 608f) {
                clearInput();
                transitionToChapel();
            }
        } else if (currentAreaId.equals("chapel_of_whispers")) {
            if (player.x() <= 8f) {
                clearInput();
                transitionToValleyRuinsFromChapel();
            } else if (gerbangKapel.isUnlocked() && player.x() >= 616f) {
                clearInput();
                transitionToArchive();
            }
        } else if (currentAreaId.equals("underground_archive")) {
            if (player.x() <= 8f) {
                clearInput();
                transitionToChapelFromArchive();
            } else if (player.x() >= 600f) {
                clearInput();
                transitionToHallFromArchive();
            }
        } else if (currentAreaId.equals("the_silent_manor_east_wing")) {
            if (player.x() <= 20f) {
                clearInput();
                transitionToHall();
            }
        }

        focused = isModalOpen() ? null
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

        if ("valley_floor".equals(currentAreaId)) {
            drawAnimatedWater(g, camX, camY);
        }

        drawTelegraphs(g, camX, camY);
        drawPlayerGlow(g, camX, camY);
        drawEntities(g, camX, camY);
        drawHeldTorch(g, camX, camY);
        drawDust(g);

        if ("hollow_woods".equals(currentAreaId)) {
            drawMovingFog(g, camX, camY);
        }

        drawTorchDarkness(g, camX, camY);
        PixelUI.vignette(g);
        drawPrompt(g, camX, camY);
        drawHeader(g);
        drawCombatHUD(g);
        drawBossBar(g);
        drawHint(g);

        dialogue.draw(g);
        investigation.drawNotification(g);
        investigation.drawCasebook(g);
        puzzleSystem.draw(g);
        deductionSystem.draw(g);

        if (respawnActive) {
            drawDefeatOverlay(g);
        }

        if (pauseOpen) {
            drawPauseMenu(g);
        }
    }

    /**Riak air yang bergerak di atas latar Dasar Lembah (tile 'w'). */
    private void drawAnimatedWater(PGraphics g, int camX, int camY) {
        if (!Art.waterSpriteReady()) {
            return;
        }
        for (int row = 0; row < map.rows(); row++) {
            for (int col = 0; col < map.cols(); col++) {
                if (map.tileAt(col, row) != 'w') {
                    continue;
                }
                int px = col * TileMap.TILE - camX;
                int py = row * TileMap.TILE - camY;
                if (px < -16 || py < -16 || px > GameManager.VIRTUAL_W
                        || py > GameManager.VIRTUAL_H) {
                    continue;
                }
                Art.drawWater(g, px, py, time, col, row);
            }
        }
    }

    private void drawTelegraphs(PGraphics g, int camX, int camY) {
        for (Enemy e : enemies) {
            e.drawTelegraph(g, camX, camY);
        }
    }

    private void drawCombatHUD(PGraphics g) {
        if (isModalOpen()) {
            return;
        }
        int hx = 6;
        int hy = HEADER_H + 4;
        int currentHp = player.hp();

        g.noStroke();
        // 3 hati gothic (masing-masing 2 HP = 6 HP total)
        for (int i = 0; i < 3; i++) {
            int heartHp = currentHp - (i * 2);
            int heartX = hx + i * 9;
            // Bingkai soket hati
            g.fill(0xFF140F1D);
            g.rect(heartX, hy, 8, 7);
            if (heartHp >= 2) {
                // Hati penuh merah menyala
                g.fill(0xFFDE3545);
                g.rect(heartX + 1, hy + 1, 6, 5);
                g.fill(0xFFFF8A9A);
                g.rect(heartX + 2, hy + 1, 2, 1);
                g.rect(heartX + 5, hy + 1, 1, 1);
            } else if (heartHp == 1) {
                // Setengah hati
                g.fill(0xFFDE3545);
                g.rect(heartX + 1, hy + 1, 3, 5);
                g.fill(0xFF281E2E);
                g.rect(heartX + 4, hy + 1, 3, 5);
            } else {
                // Soket kosong
                g.fill(0xFF281E2E);
                g.rect(heartX + 1, hy + 1, 6, 5);
            }
        }

        // Indikator Senjata (Pedang Kesatria)
        int wx = hx + 30;
        boolean cd = player.attackCooldown() > 0f;
        Art.drawIcon(g, "upg_sword", wx, hy - 2, 12, 12);
        if (cd) {
            g.fill(40, 20, 50, 130);
            g.rect(wx, hy - 2, 12, 12);
        }
        PixelUI.text(g, "[R-Click/F] " + player.weaponName(), wx + 15, hy + 1, cd ? Palette.TEXT_MUTED : Palette.TEXT_DIM);
    }

    private void drawBossBar(PGraphics g) {
        if (!"hollow_woods".equals(currentAreaId) || isModalOpen()) {
            return;
        }
        if (bayanganHutan == null && penjagaJejak == null) {
            return;
        }
        boolean bhAlive = bayanganHutan != null && bayanganHutan.isAlive();
        boolean pjAlive = penjagaJejak != null && penjagaJejak.isAlive();
        if (!bhAlive && !pjAlive) {
            return;
        }

        int barW = 82;
        int barH = 4;
        int by = HEADER_H + 4;

        g.noStroke();

        // 1. Bar Nyawa Musuh Depan (Bayangan Hutan) di sebelah kiri
        if (bayanganHutan != null) {
            int bx = 68;
            g.fill(Palette.alpha(0x0C0E17, 210));
            g.rect(bx - 2, by - 2, barW + 4, barH + 4);
            g.fill(bayanganHutan.isFinalForm() ? 0xFF8A3020 : 0xFF424A54);
            g.rect(bx - 1, by - 1, barW + 2, barH + 2);

            g.fill(0xFF140C0E);
            g.rect(bx, by, barW, barH);

            float fillFrac = (float) bayanganHutan.hp() / bayanganHutan.maxHp();
            int fillW = Math.max(0, Math.round(barW * fillFrac));
            int fillCol = bayanganHutan.isFinalForm() ? 0xFFFF2818 : 0xFFE04422;
            g.fill(fillCol);
            g.rect(bx, by, fillW, barH);
            g.fill(0xFFFF9988);
            g.rect(bx, by, fillW, 1);

            String title = bayanganHutan.isDefeated()
                    ? "BAYANGAN [GUGUR]"
                    : (bayanganHutan.isFinalForm() ? "BAYANGAN [MURKA]" : "BAYANGAN DEPAN");
            int textCol = bayanganHutan.isDefeated()
                    ? Palette.TEXT_MUTED
                    : (bayanganHutan.isFinalForm() ? 0xFFFFB255 : Palette.TEXT);
            PixelUI.centered(g, title + " " + bayanganHutan.hp() + "/" + bayanganHutan.maxHp(),
                    bx + barW / 2f, by - 8, textCol);
        }

        // 2. Bar Nyawa Penyihir (Penjaga Jejak) di sebelah kanan
        if (penjagaJejak != null) {
            int bx = 170;
            g.fill(Palette.alpha(0x0C0E17, 210));
            g.rect(bx - 2, by - 2, barW + 4, barH + 4);
            g.fill(penjagaJejak.phase() == 2 ? 0xFF8A2435 : 0xFF4D3866);
            g.rect(bx - 1, by - 1, barW + 2, barH + 2);

            g.fill(0xFF120818);
            g.rect(bx, by, barW, barH);

            float fillFrac = (float) penjagaJejak.hp() / penjagaJejak.maxHp();
            int fillW = Math.max(0, Math.round(barW * fillFrac));
            int fillCol = penjagaJejak.phase() == 2 ? 0xFFDE2A40 : 0xFF9836D6;
            g.fill(fillCol);
            g.rect(bx, by, fillW, barH);
            g.fill(penjagaJejak.phase() == 2 ? 0xFFFF9999 : 0xFFDB88FF);
            g.rect(bx, by, fillW, 1);

            String title = penjagaJejak.isDefeated()
                    ? "WIZARD [GUGUR]"
                    : (penjagaJejak.phase() == 2 ? "WIZARD [MURKA]" : "PENJAGA WIZARD");
            int textCol = penjagaJejak.isDefeated()
                    ? Palette.TEXT_MUTED
                    : (penjagaJejak.phase() == 2 ? 0xFFFF8595 : 0xFFE2C4FF);
            PixelUI.centered(g, title + " " + penjagaJejak.hp() + "/" + penjagaJejak.maxHp(),
                    bx + barW / 2f, by - 8, textCol);
        }
    }

    private void drawDefeatOverlay(PGraphics g) {
        g.noStroke();
        g.fill(Palette.alpha(0x08060E, 200));
        g.rect(0, 0, GameManager.VIRTUAL_W, GameManager.VIRTUAL_H);
        String t1 = "KAU TERCENGKRAM KEGELAPAN...";
        String t2 = "Memulihkan diri di titik aman...";
        PixelUI.centered(g, t1, GameManager.VIRTUAL_W / 2f, 78, Palette.DANGER);
        PixelUI.centered(g, t2, GameManager.VIRTUAL_W / 2f, 92, Palette.TEXT_MUTED);
    }

    /** Menggambar seluruh objek interaktif, musuh, dan pemain dengan kedalaman Y yang akurat. */
    private void drawEntities(PGraphics g, int camX, int camY) {
        List<Interactable> sorted = new ArrayList<Interactable>(interactables);
        Collections.sort(sorted, new Comparator<Interactable>() {
            @Override
            public int compare(Interactable a, Interactable b) {
                return Float.compare(a.y(), b.y());
            }
        });

        boolean playerDrawn = false;
        for (Interactable item : sorted) {
            if (!playerDrawn && player.y() < item.y()) {
                player.draw(g, camX, camY);
                playerDrawn = true;
            }
            item.draw(g, camX, camY);
        }
        if (!playerDrawn) {
            player.draw(g, camX, camY);
        }

        // Gambar seluruh musuh di atas layer entitas
        for (Enemy e : enemies) {
            e.draw(g, camX, camY);
        }
    }

    private void drawPlayerGlow(PGraphics g, int camX, int camY) {
        float px = player.x() - camX;
        float py = player.y() - camY - 10;
        g.noStroke();
        if ("hollow_woods".equals(currentAreaId)) {
            // Aura perak-teal bulan hutan berongga
            g.fill(Palette.alpha(0x5DE0D2, 10));
            g.ellipse(px, py, 58, 50);
            g.fill(Palette.alpha(0x98CED6, 12));
            g.ellipse(px, py, 32, 28);
        } else {
            g.fill(Palette.alpha(Palette.MAGIC, 6));
            g.ellipse(px, py, 54, 48);
            g.fill(Palette.alpha(Palette.MAGIC, 5));
            g.ellipse(px, py, 30, 28);
        }
    }

    /** True bila area aktif memakai sistem gelap + obor genggam. */
    private boolean isTorchArea() {
        return "valley_cliff_path".equals(currentAreaId)
                || "valley_floor".equals(currentAreaId)
                || "valley_ruins".equals(currentAreaId);
    }

    /** Obor digenggam di tangan tokoh saat berjalan di area gelap lembah. */
    private void drawHeldTorch(PGraphics g, int camX, int camY) {
        if (!torchObtained || !isTorchArea()
                || !Art.torchSpriteReady() || isModalOpen()) {
            return;
        }
        int px = Math.round(player.x() - camX);
        int py = Math.round(player.y() - camY);
        int alpha = 255;
        if (player.isInvulnerable()) {
            int blink = (int) (player.invulnerableTimer() * 20f);
            if ((blink & 1) == 0) {
                alpha = 110;
            }
        }
        Art.drawHeldTorch(g, px, py, player.facing(), player.isMoving(),
                player.animTime(), alpha);
    }

    /**
     * Kegelapan Jalur Tebing. Sebelum obor diambil, hanya titik obor di pintu
     * masuk yang bercahaya; sesudah diambil, cahaya mengikuti pemain.
     */
    private void drawTorchDarkness(PGraphics g, int camX, int camY) {
        if (!isTorchArea() || respawnActive) {
            return;
        }
        ensureTorchLightAssets();

        float flicker = 1f + 0.05f * (float) Math.sin(time * 7.3f)
                + 0.03f * (float) Math.sin(time * 11.7f);
        float cx;
        float cy;
        float radius;
        int baseAlpha;
        if (torchObtained) {
            cx = player.x() - camX;
            cy = player.y() - camY - 10f;
            radius = 82f * flicker;
            baseAlpha = 244;
        } else if ("valley_floor".equals(currentAreaId)) {
            // Belum bawa obor: brazier titik istirahat jadi sumber cahaya.
            cx = restLembah.x() - camX;
            cy = restLembah.y() - 16f - camY;
            radius = 70f * flicker;
            baseAlpha = 232;
        } else if ("valley_ruins".equals(currentAreaId)) {
            // Belum bawa obor: brazier di titik istirahat jadi sumber cahaya.
            cx = restReruntuhan.x() - camX;
            cy = restReruntuhan.y() - 12f - camY;
            radius = 70f * flicker;
            baseAlpha = 232;
        } else {
            cx = oborTebing.x() - camX;
            cy = oborTebing.y() - camY - 10f;
            radius = 58f * flicker;
            baseAlpha = 232;
        }

        // Hangatnya cahaya obor di dunia, digambar sebelum lapisan kegelapan.
        g.noStroke();
        for (int i = 3; i >= 1; i--) {
            float r = radius * (0.32f + i * 0.2f);
            g.fill(255, 150, 60, 5 + (3 - i) * 4);
            g.ellipse(cx, cy, r, r);
        }

        // Gelap diterapkan langsung ke piksel kanvas. Memakai PImage perantara
        // tidak aman karena PGraphics.image() menimpa alpha sumber menjadi 255,
        // yang membuat lubang cahaya ikut tertutup.
        g.loadPixels();
        int w = g.width;
        int h = g.height;
        int[] pixels = g.pixels;
        int maskSize = torchLightMask.width;
        int maskR = maskSize / 2;
        float scale = maskSize / (radius * 2f);
        int[] mask = torchLightMask.pixels;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int mx = (int) ((x - cx) * scale + maskR);
                int my = (int) ((y - cy) * scale + maskR);
                int a = baseAlpha;
                if (mx >= 0 && my >= 0 && mx < maskSize && my < maskSize) {
                    a = (mask[my * maskSize + mx] >>> 24) & 0xFF;
                    if (a > baseAlpha) {
                        a = baseAlpha;
                    }
                }
                if (a <= 0) {
                    continue;
                }
                int idx = y * w + x;
                int src = pixels[idx];
                int inv = 255 - a;
                int r = (((src >> 16) & 0xFF) * inv + 0x06 * a) / 255;
                int gr = (((src >> 8) & 0xFF) * inv + 0x08 * a) / 255;
                int b = ((src & 0xFF) * inv + 0x10 * a) / 255;
                pixels[idx] = 0xFF000000 | (r << 16) | (gr << 8) | b;
            }
        }
        g.updatePixels();
    }

    private void ensureTorchLightAssets() {
        if (torchLightMask == null) {
            int size = 192;
            torchLightMask = new PImage(size, size);
            int center = size / 2;
            float[] alpha = new float[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    float dx = x - center + 0.5f;
                    float dy = y - center + 0.5f;
                    float d = (float) Math.sqrt(dx * dx + dy * dy) / center;
                    float a;
                    if (d <= 0.16f) {
                        a = 0f;
                    } else if (d >= 1f) {
                        a = 255f;
                    } else {
                        float t = (d - 0.16f) / (1f - 0.16f);
                        a = 255f * t * t;
                    }
                    alpha[y * size + x] = a;
                }
            }
            torchLightMask.loadPixels();
            for (int i = 0; i < alpha.length; i++) {
                torchLightMask.pixels[i] = (int) alpha[i] << 24;
            }
            torchLightMask.updatePixels();
        }
    }

    /**
     * Efek kabut bergerak berlapis di Hollow Woods (Stage 3).
     * Terdiri dari pita kabut tanah yang mengalir dan gumpalan kabut atmosferik
     * dengan paralaks kamera, menciptakan kedalaman hutan gotik berkabut.
     */
    private void drawMovingFog(PGraphics g, int camX, int camY) {
        g.noStroke();
        int screenW = GameManager.VIRTUAL_W;
        int screenH = GameManager.VIRTUAL_H;

        // 1. Lapisan pita kabut rendah menyusuri lantai hutan (paralaks 0.75x)
        for (int i = 0; i < 5; i++) {
            float speed = 11f + i * 4f;
            float worldOffset = (time * speed + i * 85f);
            float screenOffset = (worldOffset - camX * 0.75f);
            float fx = ((screenOffset % (screenW + 140f)) + (screenW + 140f)) % (screenW + 140f) - 70f;
            float fy = 110f + i * 20f + (float) Math.sin(time * 0.85f + i * 1.4f) * 4f - (camY * 0.2f);
            int mistW = 96 + (i % 3) * 32;
            int mistH = 12 + (i % 2) * 6;

            g.fill(Palette.alpha(0x284D58, 14));
            g.rect(fx - mistW / 2f, fy - mistH / 2f, mistW, mistH);
            g.fill(Palette.alpha(0x5DA8B5, 11));
            g.rect(fx - mistW * 0.32f, fy - 2, mistW * 0.64f, 4);
        }

        // 2. Lapisan gumpalan kabut atmosferik lembut di udara (paralaks 0.4x)
        for (int i = 0; i < 4; i++) {
            float speed = 6.5f + i * 3f;
            float worldOffset = (time * speed + i * 115f);
            float screenOffset = (worldOffset - camX * 0.4f);
            float rawX = ((screenOffset % (screenW + 180f)) + (screenW + 180f)) % (screenW + 180f) - 90f;
            float waveY = 50f + i * 30f + (float) Math.cos(time * 0.6f + i * 1.8f) * 6f;
            float radX = 80f + (i % 2) * 35f;
            float radY = 20f + (i % 3) * 6f;

            g.fill(Palette.alpha(0x356673, 11));
            g.ellipse(rawX, waveY, radX * 1.25f, radY * 1.25f);
            g.fill(Palette.alpha(0x85CFDC, 9));
            g.ellipse(rawX, waveY, radX * 0.75f, radY * 0.65f);
        }

        // 3. Kabut melayang cepat di latar depan (foreground wisps, paralaks 1.2f)
        for (int i = 0; i < 3; i++) {
            float speed = 16f + i * 5f;
            float worldOffset = (time * speed + i * 140f);
            float screenOffset = (worldOffset - camX * 1.2f);
            float rawX = ((screenOffset % (screenW + 160f)) + (screenW + 160f)) % (screenW + 160f) - 80f;
            float waveY = 30f + i * 55f + (float) Math.sin(time * 1.1f + i * 2.1f) * 5f;

            g.fill(Palette.alpha(0x72BFCB, 8));
            g.ellipse(rawX, waveY, 110, 16);
        }
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
        if (focused == null || dialogue.isOpen() || investigation.isCasebookOpen()
                || puzzleSystem.isOpen() || deductionSystem.isOpen() || pauseOpen) {
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

        String areaTitle = "AULA MANOR";
        if (currentAreaId.equals("ashen_village")) {
            areaTitle = "ASHEN VILLAGE";
        } else if (currentAreaId.equals("the_silent_manor_east_wing")) {
            areaTitle = "SAYAP TIMUR";
        } else if (currentAreaId.equals("hollow_woods")) {
            areaTitle = "HOLLOW WOODS";
        } else if (currentAreaId.equals("valley_cliff_path")) {
            areaTitle = "JALUR TEBING";
        } else if (currentAreaId.equals("valley_floor")) {
            areaTitle = "DASAR LEMBAH";
        } else if (currentAreaId.equals("valley_ruins")) {
            areaTitle = "LINTASAN RERUNTUHAN";
        } else if (currentAreaId.equals("chapel_of_whispers")) {
            areaTitle = "CHAPEL OF WHISPERS";
        } else if (currentAreaId.equals("underground_archive")) {
            areaTitle = "ARSIP BAWAH TANAH";
        }
        PixelUI.text(g, areaTitle, 6, 4, Palette.TEXT_DIM);

        if (autosaveNoticeTimer > 0f) {
            int autoAlpha = (int) (255 * Math.min(1f, autosaveNoticeTimer));
            PixelUI.text(g, "[SIMPAN OTOMATIS]", 76, 4, Palette.alpha(Palette.WARM, autoAlpha));
        }

        String clueProgress = "BUKTI: " + investigation.discoveredCount() + "/" + investigation.totalCount();
        int clueColor = (investigation.discoveredCount() == investigation.totalCount() && investigation.totalCount() > 0)
                ? Palette.ACCENT : Palette.TEXT_MUTED;
        PixelUI.text(g, clueProgress, 192, 4, clueColor);

        PixelUI.text(g, "[C] BUKU KASUS", 244, 4,
                investigation.isCasebookOpen() ? Palette.ACCENT : Palette.TEXT_DIM);
    }

    private void drawHint(PGraphics g) {
        if (investigation.isCasebookOpen() || puzzleSystem.isOpen() || deductionSystem.isOpen() || pauseOpen) {
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
            PixelUI.centered(g, "GERAK: WASD   E: INTERAKSI   C: BUKU KASUS   ESC: JEDA", 160, y + 3,
                    Palette.TEXT_MUTED);
        }
    }

    /** Label tokoh di HUD: nama baku, ditambah nama pilihan pemain bila ada. */
    private String heroLabel() {
        String n = (playerName == null) ? "" : playerName.trim();
        if (n.isEmpty() || GameManager.HERO_NAME_DEFAULT.equalsIgnoreCase(n)) {
            return GameManager.HERO_NAME_DEFAULT;
        }
        return GameManager.HERO_NAME_DEFAULT + ": " + n;
    }

    private void drawPauseMenu(PGraphics g) {
        PixelUI.dim(g, 190);
        int pw = 180;
        int ph = 110;
        int px = (GameManager.VIRTUAL_W - pw) / 2;
        int py = (GameManager.VIRTUAL_H - ph) / 2;

        PixelUI.panel(g, px, py, pw, ph);
        PixelUI.centered(g, "JEDA", px + pw / 2, py + 4, 2, Palette.WARM);
        if (Art.drawHeroPortrait(g, px + 6, py + 26)) {
            PixelUI.text(g, heroLabel(), px + 32, py + 18, Palette.ACCENT_DIM);
        } else {
            PixelUI.centered(g, heroLabel(), px + pw / 2, py + 19, Palette.ACCENT_DIM);
        }
        PixelUI.divider(g, px + pw / 2, py + 29, pw - 24);

        if (!pauseConfirmQuit) {
            String[] options = {
                    "LANJUTKAN",
                    "KEMBALI KE MENU UTAMA"
            };

            int btnY = py + 34;
            int btnH = 16;
            int gap = 4;
            for (int i = 0; i < options.length; i++) {
                boolean isSelected = (i == pauseSelected);
                PixelUI.button(g, px + 16, btnY + i * (btnH + gap), pw - 32, btnH, options[i], isSelected, true);
            }

            PixelUI.divider(g, px + pw / 2, py + ph - 16, pw - 24);
            PixelUI.centered(g, "W/S: PILIH   ENTER/KLIK: OK   ESC: RESUME", px + pw / 2, py + ph - 11, Palette.TEXT_MUTED);
        } else {
            PixelUI.centered(g, "KEMBALI KE MENU UTAMA?", px + pw / 2, py + 32, Palette.TEXT);
            PixelUI.centered(g, "PROGRES DISIMPAN OTOMATIS", px + pw / 2, py + 44, Palette.ACCENT_DIM);

            int btnW = 66;
            int btnH = 16;
            int bY = py + 62;
            PixelUI.button(g, px + 18, bY, btnW, btnH, "YA", pauseConfirmSelected == 0, true);
            PixelUI.button(g, px + pw - 18 - btnW, bY, btnW, btnH, "BATAL", pauseConfirmSelected == 1, true);

            PixelUI.divider(g, px + pw / 2, py + ph - 16, pw - 24);
            PixelUI.centered(g, "A/D: PILIH   ENTER: KONFIRMASI   ESC: BATAL", px + pw / 2, py + ph - 11, Palette.TEXT_MUTED);
        }
    }

    private void handlePauseKey(char key, int keyCode) {
        if (!pauseConfirmQuit) {
            if (isEscape(key, keyCode)) {
                closePauseMenu();
                return;
            }

            if (keyCode == PConstants.UP || key == 'w' || key == 'W') {
                pauseSelected = (pauseSelected - 1 + 2) % 2;
                return;
            }

            if (keyCode == PConstants.DOWN || key == 's' || key == 'S') {
                pauseSelected = (pauseSelected + 1) % 2;
                return;
            }

            if (key == '\n' || key == '\r' || key == ' ' || keyCode == 10 || keyCode == 13) {
                executePauseAction(pauseSelected);
            }
        } else {
            if (isEscape(key, keyCode)) {
                pauseConfirmQuit = false;
                return;
            }

            if (keyCode == PConstants.LEFT || keyCode == PConstants.RIGHT || key == 'a' || key == 'A' || key == 'd' || key == 'D') {
                pauseConfirmSelected = 1 - pauseConfirmSelected;
                return;
            }

            if (key == '\n' || key == '\r' || key == ' ' || keyCode == 10 || keyCode == 13) {
                if (pauseConfirmSelected == 0) { // YA
                    autosave();
                    closePauseMenu();
                    game.showMenu();
                } else { // BATAL
                    pauseConfirmQuit = false;
                }
            }
        }
    }

    private void executePauseAction(int index) {
        switch (index) {
            case 0: // Resume
                closePauseMenu();
                break;
            case 1: // Kembali ke menu utama
                pauseConfirmQuit = true;
                pauseConfirmSelected = 1;
                break;
            default:
                break;
        }
    }

    private void handlePauseMouseMove(float x, float y) {
        int pw = 180;
        int ph = 110;
        int px = (GameManager.VIRTUAL_W - pw) / 2;
        int py = (GameManager.VIRTUAL_H - ph) / 2;

        if (!pauseConfirmQuit) {
            int btnY = py + 34;
            int btnH = 16;
            int gap = 4;
            for (int i = 0; i < 2; i++) {
                int curY = btnY + i * (btnH + gap);
                if (x >= px + 16 && x <= px + pw - 16 && y >= curY && y <= curY + btnH) {
                    pauseSelected = i;
                    break;
                }
            }
        } else {
            int btnW = 66;
            int btnH = 16;
            int bY = py + 62;
            if (x >= px + 18 && x <= px + 18 + btnW && y >= bY && y <= bY + btnH) {
                pauseConfirmSelected = 0;
            } else if (x >= px + pw - 18 - btnW && x <= px + pw - 18 && y >= bY && y <= bY + btnH) {
                pauseConfirmSelected = 1;
            }
        }
    }

    private void handlePauseMouseClick(float x, float y) {
        int pw = 180;
        int ph = 110;
        int px = (GameManager.VIRTUAL_W - pw) / 2;
        int py = (GameManager.VIRTUAL_H - ph) / 2;

        if (!pauseConfirmQuit) {
            int btnY = py + 34;
            int btnH = 16;
            int gap = 4;
            for (int i = 0; i < 2; i++) {
                int curY = btnY + i * (btnH + gap);
                if (x >= px + 16 && x <= px + pw - 16 && y >= curY && y <= curY + btnH) {
                    executePauseAction(i);
                    return;
                }
            }
        } else {
            int btnW = 66;
            int btnH = 16;
            int bY = py + 62;
            if (x >= px + 18 && x <= px + 18 + btnW && y >= bY && y <= bY + btnH) {
                autosave();
                closePauseMenu();
                game.showMenu();
            } else if (x >= px + pw - 18 - btnW && x <= px + pw - 18 && y >= bY && y <= bY + btnH) {
                pauseConfirmQuit = false;
            }
        }
    }

    @Override
    public void keyPressed(char key, int keyCode) {
        // 0. Pause menu terbuka
        if (pauseOpen) {
            handlePauseKey(key, keyCode);
            return;
        }

        // 1. Rekonstruksi Deduksi Kasus terbuka
        if (deductionSystem.isOpen()) {
            deductionSystem.keyPressed(key, keyCode);
            return;
        }

        // 2. Puzzle terbuka
        if (puzzleSystem.isOpen()) {
            puzzleSystem.keyPressed(key, keyCode);
            return;
        }

        // 3. Casebook terbuka
        if (investigation.isCasebookOpen()) {
            investigation.keyPressed(key, keyCode);
            return;
        }

        // 4. Dialog terbuka
        if (dialogue.isOpen()) {
            if (isEscape(key, keyCode)) {
                dialogue.close();
                onDialogueClosed();
                interactCooldown = INPUT_COOLDOWN;
                return;
            }
            if (isInteract(key, keyCode) && interactCooldown <= 0f) {
                if (!dialogue.advance()) {
                    onDialogueClosed();
                } else {
                    if (activeSpeaker == npc) {
                        npc.lookAt(player.x(), player.y());
                    } else if (activeSpeaker == tetuaDesa) {
                        tetuaDesa.lookAt(player.x(), player.y());
                    } else if (activeSpeaker == saksiDesa) {
                        saksiDesa.lookAt(player.x(), player.y());
                    }
                }
                interactCooldown = INPUT_COOLDOWN;
            }
            return;
        }

        // 5. Serangan Senjata Saku (F atau J)
        if (key == 'f' || key == 'F' || key == 'j' || key == 'J') {
            if (triggerPlayerAttack()) {
                return;
            }
        }

        // 6. Shortcut Casebook (C, Tab)
        if (key == 'c' || key == 'C' || key == '\t') {
            openCasebook();
            return;
        }

        // 6. ESC membuka Pause Menu
        if (isEscape(key, keyCode)) {
            openPauseMenu();
            return;
        }

        // 7. Interaksi dengan objek / NPC
        if (isInteract(key, keyCode)) {
            if (interactCooldown <= 0f && focused != null) {
                // Objek kait pengunci membuka puzzle bila belum terpecahkan
                if (focused instanceof KotakBesi) {
                    KotakBesi kb = (KotakBesi) focused;
                    if (!kb.isSolved()) {
                        clearInput();
                        puzzleSystem.open(kb.puzzle());
                        interactCooldown = INPUT_COOLDOWN;
                        return;
                    }
                }

                // Altar rekonstruksi membuka modal deduksi kasus
                if (focused instanceof AltarDeduksi) {
                    clearInput();
                    deductionSystem.open(investigation.discoveredCount());
                    interactCooldown = INPUT_COOLDOWN;
                    return;
                }

                // Gerbang timur saat sudah terbuka melakukan transisi
                if (focused instanceof GerbangTimur) {
                    GerbangTimur gt = (GerbangTimur) focused;
                    if (gt.isGateOpen()) {
                        clearInput();
                        if (expeditionCompleted) {
                            transitionToEastWing();
                        } else {
                            transitionToHollowWoods();
                        }
                        interactCooldown = INPUT_COOLDOWN;
                        return;
                    }
                }

                // Gerbang barat kembali ke Aula
                if (focused instanceof GerbangBarat) {
                    clearInput();
                    transitionToHall();
                    interactCooldown = INPUT_COOLDOWN;
                    return;
                }

                // Gerbang keluar hutan (sisi timur) — transisi ke Jalur Tebing
                if (focused instanceof GerbangHutan) {
                    GerbangHutan gh = (GerbangHutan) focused;
                    if (gh.isFogCleared()) {
                        clearInput();
                        transitionToValleyCliffPath();
                        interactCooldown = INPUT_COOLDOWN;
                        return;
                    }
                }

                // Gerbang transisi antar segmen lembah
                if (focused instanceof GerbangLembah) {
                    GerbangLembah gl = (GerbangLembah) focused;
                    clearInput();
                    String label = gl.targetLabel();
                    if ("DASAR LEMBAH".equals(label)) {
                        transitionToValleyFloor();
                    } else if ("LINTASAN RERUNTUHAN".equals(label)) {
                        transitionToValleyRuins();
                    } else if ("CHAPEL OF WHISPERS".equals(label)) {
                        transitionToChapel();
                    }
                    interactCooldown = INPUT_COOLDOWN;
                    return;
                }

                // Gerbang Desa ke Aula Manor (Prolog -> Bab 1)
                if (focused instanceof GerbangDesa) {
                    GerbangDesa gd = (GerbangDesa) focused;
                    if (gd.canProceed()) {
                        clearInput();
                        transitionToHallFromVillage();
                        interactCooldown = INPUT_COOLDOWN;
                        return;
                    }
                }

                // Gerbang Kapel ke Arsip Bawah Tanah (Bab 4 -> Bab 5)
                if (focused instanceof GerbangKapel) {
                    GerbangKapel gk = (GerbangKapel) focused;
                    if (gk.isUnlocked()) {
                        clearInput();
                        transitionToArchive();
                        interactCooldown = INPUT_COOLDOWN;
                        return;
                    }
                }

                // Gerbang Arsip kembali ke Manor Hall (Bab 5 -> Bab 6)
                if (focused instanceof GerbangArsip) {
                    clearInput();
                    transitionToHallFromArchive();
                    interactCooldown = INPUT_COOLDOWN;
                    return;
                }

                // Obor Jalur Tebing: diambil sekali, lalu cahayanya mengikuti pemain
                if (focused instanceof Obor) {
                    boolean wasPicked = ((Obor) focused).isPickedUp();
                    Dialogue torchDialogue = focused.interact(investigation);
                    if (!wasPicked) {
                        torchObtained = true;
                        investigation.setNotification("OBOR DIAMBIL — CAHAYA MENERANGI LORONG!", 3.5f);
                        autosave();
                    }
                    dialogue.open(torchDialogue);
                    clearInput();
                    interactCooldown = INPUT_COOLDOWN;
                    return;
                }

                boolean prevGate = gerbangTimur.isGateOpen();
                boolean prevSymbol = symbolStone.isExamined();
                boolean prevVillageGate = gerbangDesa.canProceed();
                int prevClues = investigation.discoveredCount();

                if (focused == npc) {
                    npc.triggerTalkAnimation(player.x(), player.y());
                    activeSpeaker = npc;
                } else if (focused == tetuaDesa) {
                    tetuaDesa.triggerTalkAnimation(player.x(), player.y());
                    activeSpeaker = tetuaDesa;
                } else if (focused == saksiDesa) {
                    saksiDesa.triggerTalkAnimation(player.x(), player.y());
                    activeSpeaker = saksiDesa;
                } else {
                    activeSpeaker = null;
                }

                // Titik istirahat: duduk langsung di tempat berdiri tanpa teleport,
                // menghadap ke arah api (4 frame arah sesuai sprite).
                if (focused instanceof RestPoint) {
                    RestPoint rp = (RestPoint) focused;
                    if ("valley_floor".equals(currentAreaId)
                            || "valley_ruins".equals(currentAreaId)
                            || "chapel_of_whispers".equals(currentAreaId)
                            || "underground_archive".equals(currentAreaId)) {
                        float dx = rp.x() - player.x();
                        float dy = rp.y() - player.y();
                        if (Math.abs(dx) >= Math.abs(dy)) {
                            player.setFacing(dx < 0f ? Player.Facing.LEFT : Player.Facing.RIGHT);
                        } else {
                            player.setFacing(dy < 0f ? Player.Facing.UP : Player.Facing.DOWN);
                        }
                        player.setSitting(true);
                    }
                }

                Dialogue d = focused.interact(investigation);
                if (focused instanceof PapanPengumuman || focused instanceof TetuaDesa) {
                    gerbangDesa.updateCondition(papanPengumuman.isExamined(), tetuaDesa.hasSpoken());
                    syncVillageGate();
                }
                if (focused == altarKapel && !gerbangKapel.isUnlocked()) {
                    gerbangKapel.unlock();
                    mapChapel.setGateOpen(true);
                    investigation.setNotification("MEKANISME ALTAR BERPUTAR — PINTU BATU ARSIP TERBUKA!", 3.5f);
                    autosave();
                }
                if (investigation.discoveredCount() > prevClues) {
                    quest.onClueDiscovered(investigation.discoveredCount());
                    autosave();
                }
                if (!prevGate && gerbangTimur.isGateOpen()) {
                    autosave();
                }
                if (!prevSymbol && symbolStone.isExamined()) {
                    investigation.setNotification("SIGIL JEJAK DITEMUKAN — KABUT MENYINGKAP!", 3.5f);
                    autosave();
                }
                if (!prevVillageGate && gerbangDesa.canProceed()) {
                    investigation.setNotification("GERBANG MENUJU THE SILENT MANOR TELAH TERBUKA!", 3.5f);
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
        mousePressed(x, y, PConstants.LEFT);
    }

    @Override
    public void mousePressed(float x, float y, int button) {
        if (pauseOpen) {
            handlePauseMouseClick(x, y);
            return;
        }
        if (deductionSystem.isOpen()) {
            deductionSystem.mousePressed(x, y);
            return;
        }
        if (puzzleSystem.isOpen()) {
            puzzleSystem.mousePressed(x, y);
            return;
        }
        if (investigation.isCasebookOpen()) {
            investigation.mousePressed(x, y);
            return;
        }
        if (dialogue.isOpen()) {
            return;
        }

        // Serangan klik kanan senjata saku
        if (button == PConstants.RIGHT) {
            triggerPlayerAttack();
        }
    }

    @Override
    public void mouseMoved(float x, float y) {
        if (pauseOpen) {
            handlePauseMouseMove(x, y);
            return;
        }
        if (deductionSystem.isOpen()) {
            deductionSystem.mouseMoved(x, y);
            return;
        }
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

    public TileMap mapVillage() {
        return mapVillage;
    }

    public TileMap mapChapel() {
        return mapChapel;
    }

    public TileMap mapArchive() {
        return mapArchive;
    }

    public GerbangDesa gerbangDesa() {
        return gerbangDesa;
    }

    public GerbangKapel gerbangKapel() {
        return gerbangKapel;
    }

    public GerbangArsip gerbangArsip() {
        return gerbangArsip;
    }

    public PapanPengumuman papanPengumuman() {
        return papanPengumuman;
    }

    public TetuaDesa tetuaDesa() {
        return tetuaDesa;
    }

    public SaksiDesa saksiDesa() {
        return saksiDesa;
    }

    public Npc npc() {
        return npc;
    }

    public boolean isExpeditionCompleted() {
        return expeditionCompleted;
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

