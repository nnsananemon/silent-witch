import processing.core.PApplet;
import processing.core.PGraphics;

import java.io.File;

/**
 * Mengekspor seluruh aset visual prosedural Silent Witch menjadi PNG.
 *
 * Semua gambar dibuat dari kode (tidak ada file gambar di folder data), jadi
 * utilitas ini merender peta penuh dan sprite ke kanvas offscreen, lalu
 * menyimpannya ke Desktop agar bisa didesain ulang.
 *
 * Jalankan dari baris perintah:
 *   java -cp "&lt;core.jar&gt;;&lt;folder sketch&gt;" AssetExport [folderTujuan]
 *
 * Tanpa argumen, hasil disimpan ke:
 *   %USERPROFILE%\Desktop\SilentWitch_Assets
 */
public final class AssetExport {

    private static final int SPRITE_W = 72;
    private static final int SPRITE_H = 72;
    /** Titik jangkar sprite di kanvas (tengah horizontal, agak ke bawah). */
    private static final int ANCHOR_X = 36;
    private static final int ANCHOR_Y = 52;
    /** Versi diperbesar dengan nearest-neighbor agar tetap tajam. */
    private static final int SCALE = 4;

    private interface Drawable {
        void draw(PGraphics g, int cameraX, int cameraY);
    }

    private AssetExport() {
    }

    public static void main(String[] args) {
        String target = (args.length > 0 && !args[0].trim().isEmpty())
                ? args[0].trim()
                : System.getProperty("user.home") + File.separator + "Desktop"
                        + File.separator + "SilentWitch_Assets";

        File root = new File(target);
        File backgrounds = new File(root, "backgrounds");
        File characters = new File(root, "characters");
        File npcs = new File(root, "npcs");
        File enemies = new File(root, "enemies");
        File props = new File(root, "props");
        File ui = new File(root, "ui");

        File[] dirs = {backgrounds, characters, npcs, enemies, props, ui};
        for (File dir : dirs) {
            if (!dir.exists() && !dir.mkdirs()) {
                System.err.println("Gagal membuat folder: " + dir);
                return;
            }
        }

        PApplet app = new PApplet();
        int count = 0;
        try {
            count += exportBackgrounds(app, backgrounds);
            count += exportCharacters(app, characters);
            count += exportNpcs(app, npcs);
            count += exportEnemies(app, enemies);
            count += exportProps(app, props);
            count += exportMenu(app, ui);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        writeReadme(root);
        System.out.println("Selesai. " + count + " gambar diekspor ke: " + root.getAbsolutePath());
    }

    // ------------------------------------------------------------------
    // Background: seluruh peta area, ukuran penuh tanpa kamera.
    // ------------------------------------------------------------------

    private static int exportBackgrounds(PApplet app, File dir) {
        int n = 0;
        n += exportMap(app, dir, "01_aula_manor", TileMap.manorHall());
        n += exportMap(app, dir, "02_sayap_timur_manor", TileMap.manorEastWing());
        n += exportMap(app, dir, "03_hutan_berongga", TileMap.hollowWoods());
        n += exportMap(app, dir, "04_jalur_tebing_lembah", TileMap.valleyCliffPath());
        n += exportMap(app, dir, "05_dasar_lembah", TileMap.valleyFloor());
        n += exportMap(app, dir, "06_reruntuhan_lembah", TileMap.valleyRuins());
        n += exportMap(app, dir, "07_desa_abu", TileMap.ashenVillage());
        n += exportMap(app, dir, "08_kapel_bisikan", TileMap.chapelWhispers());
        n += exportMap(app, dir, "09_arsip_bawah_tanah", TileMap.undergroundArchive());
        return n;
    }

    private static int exportMap(PApplet app, File dir, String name, TileMap map) {
        int w = map.pixelWidth();
        int h = map.pixelHeight();
        PGraphics g = canvas(app, w, h);
        g.beginDraw();
        g.clear();
        map.drawAll(g);
        g.endDraw();
        return saveBoth(app, g, dir, name, w, h);
    }

    // ------------------------------------------------------------------
    // Karakter utama: empat arah hadap.
    // ------------------------------------------------------------------

    private static int exportCharacters(PApplet app, File dir) {
        Player player = new Player(TileMap.manorHall(), 100f, 100f);
        String[] names = {"bawah", "atas", "kiri", "kanan"};
        Player.Facing[] facings = {
                Player.Facing.DOWN,
                Player.Facing.UP,
                Player.Facing.LEFT,
                Player.Facing.RIGHT
        };

        int n = 0;
        for (int i = 0; i < facings.length; i++) {
            player.setFacing(facings[i]);
            n += exportSprite(app, dir, "penyihir_" + names[i],
                    player.x(), player.y(), player::draw);

            // Pose menyerang dengan Tongkat Perak Saku.
            if (player.attack()) {
                n += exportSprite(app, dir, "penyihir_serang_" + names[i],
                        player.x(), player.y(), player::draw);
                player.updateCombat(Player.ATTACK_COOLDOWN + 0.01f);
            }
        }
        return n;
    }

    // ------------------------------------------------------------------
    // NPC.
    // ------------------------------------------------------------------

    private static int exportNpcs(PApplet app, File dir) {
        int n = 0;

        Npc pustakawan = new Npc(100f, 100f, "KAKEK PUSTAKAWAN",
                new Dialogue("KAKEK PUSTAKAWAN", "Contoh dialog untuk pratinjau aset."));
        n += exportSprite(app, dir, "npc_kakek_pustakawan", pustakawan.x(), pustakawan.y(), pustakawan::draw);
        n += exportSprite(app, dir, "npc_penjaga_aula", pustakawan.x(), pustakawan.y(), pustakawan::draw);

        TetuaDesa tetua = new TetuaDesa(100f, 100f);
        n += exportSprite(app, dir, "tetua_desa", tetua.x(), tetua.y(), tetua::draw);

        SaksiDesa saksi = new SaksiDesa(100f, 100f);
        n += exportSprite(app, dir, "saksi_desa", saksi.x(), saksi.y(), saksi::draw);

        return n;
    }

    // ------------------------------------------------------------------
    // Musuh.
    // ------------------------------------------------------------------

    private static int exportEnemies(PApplet app, File dir) {
        int n = 0;

        BayanganHutan bayangan = new BayanganHutan(100f, 100f);
        n += exportSprite(app, dir, "bayangan_hutan", bayangan.x(), bayangan.y(), bayangan::draw);

        PenjagaJejak penjaga = new PenjagaJejak(100f, 100f);
        n += exportSprite(app, dir, "penjaga_jejak", penjaga.x(), penjaga.y(), penjaga::draw);

        return n;
    }

    // ------------------------------------------------------------------
    // Properti yang punya sprite sendiri (bukan bagian dari tile peta).
    // ------------------------------------------------------------------

    private static int exportProps(PApplet app, File dir) {
        int n = 0;

        KotakBesi kotak = new KotakBesi(100f, 100f, dummyPuzzle());
        n += exportSprite(app, dir, "kotak_besi", kotak.x(), kotak.y(), kotak::draw);

        SymbolStone batu = new SymbolStone(100f, 100f, new GerbangHutan(100f, 100f));
        n += exportSprite(app, dir, "batu_simbol", batu.x(), batu.y(), batu::draw);

        RestPoint istirahat = new RestPoint(100f, 100f);
        n += exportSprite(app, dir, "titik_istirahat", istirahat.x(), istirahat.y(), istirahat::draw);

        return n;
    }

    private static Puzzle dummyPuzzle() {
        return new Puzzle("pratinjau", "Kotak Besi", "Putar tiga dial angka.",
                new int[]{1, 2, 3},
                new String[]{"Petunjuk satu.", "Petunjuk dua.", "Petunjuk tiga."},
                "Kombinasi terbuka.");
    }

    // ------------------------------------------------------------------
    // Menu utama (referensi tampilan, bukan background gameplay).
    // ------------------------------------------------------------------

    private static int exportMenu(PApplet app, File dir) {
        PGraphics g = canvas(app, GameManager.VIRTUAL_W, GameManager.VIRTUAL_H);
        MainMenuScene menu = new MainMenuScene(null);
        g.beginDraw();
        g.clear();
        menu.update(1.2f);
        menu.draw(g);
        g.endDraw();
        return saveBoth(app, g, dir, "menu_utama", GameManager.VIRTUAL_W, GameManager.VIRTUAL_H);
    }

    // ------------------------------------------------------------------
    // Utilitas render & simpan.
    // ------------------------------------------------------------------

    /** Kanvas offscreen Java2D, pola yang sama dengan MenuPreview. */
    private static PGraphics canvas(PApplet app, int w, int h) {
        PGraphics g = new processing.awt.PGraphicsJava2D();
        g.setParent(app);
        g.setPrimary(false);
        g.setSize(w, h);
        g.noSmooth();
        return g;
    }

    /**
     * Menyimpan satu sprite dengan kaki di titik jangkar kanvas, lalu versi
     * diperbesar ke subfolder "4x".
     */
    private static int exportSprite(PApplet app, File dir, String name,
                                    float x, float y, Drawable drawable) {
        PGraphics g = canvas(app, SPRITE_W, SPRITE_H);
        int cameraX = Math.round(x) - ANCHOR_X;
        int cameraY = Math.round(y) - ANCHOR_Y;

        g.beginDraw();
        g.clear();
        drawable.draw(g, cameraX, cameraY);
        g.endDraw();

        return saveBoth(app, g, dir, name, SPRITE_W, SPRITE_H);
    }

    /** Menyimpan PNG 1x dan versi 4x (nearest-neighbor) di subfolder "4x". */
    private static int saveBoth(PApplet app, PGraphics g, File dir, String name, int w, int h) {
        g.save(new File(dir, name + ".png").getAbsolutePath());

        File upDir = new File(dir, SCALE + "x");
        if (!upDir.exists() && !upDir.mkdirs()) {
            System.err.println("Gagal membuat folder: " + upDir);
            return 1;
        }

        PGraphics big = canvas(app, w * SCALE, h * SCALE);
        big.beginDraw();
        big.clear();
        big.image(g, 0, 0, w * SCALE, h * SCALE);
        big.endDraw();
        big.save(new File(upDir, name + ".png").getAbsolutePath());
        return 1;
    }

    private static void writeReadme(File root) {
        String text = "SILENT WITCH - EKSPOR ASET VISUAL\n"
                + "=================================\n\n"
                + "Semua gambar di folder ini dirender langsung dari kode game\n"
                + "(100% prosedural, tanpa file gambar eksternal).\n"
                + "Gunakan sebagai referensi untuk mendesain ulang aset.\n\n"
                + "STRUKTUR FOLDER\n"
                + "---------------\n"
                + "backgrounds/  Peta penuh setiap area, 1x dan 4x/ (nearest-neighbor).\n"
                + "characters/   Karakter utama (penyihir): 4 arah hadap + 4 pose serang.\n"
                + "npcs/         Sprite NPC: penjaga aula, tetua desa, saksi desa.\n"
                + "enemies/      Sprite musuh: Bayangan Hutan dan Penjaga Jejak.\n"
                + "props/        Properti ber-sprite: kotak besi, batu simbol, titik istirahat.\n"
                + "ui/           Potret menu utama 320x180 sebagai referensi tampilan.\n\n"
                + "CATATAN\n"
                + "-------\n"
                + "- Latar transparan pada folder characters, npcs, enemies, dan props.\n"
                + "- Sprite berukuran 72x72; titik jangkar berada di (36, 52).\n"
                + "- Background berukuran penuh: 416x256 (manor) atau 640x256 (area lain).\n"
                + "- Gerbang, altar, papan pengumuman, dan ornamen peta sudah termasuk\n"
                + "  di dalam gambar background karena digambar sebagai tile.\n"
                + "- Subfolder 4x/ adalah hasil perbesaran tanpa blur, aman dibuka di\n"
                + "  editor gambar untuk dijiplak atau diwarnai ulang.\n";

        try {
            java.nio.file.Files.write(
                    new File(root, "README.txt").toPath(),
                    text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (java.io.IOException e) {
            System.err.println("Gagal menulis README: " + e.getMessage());
        }
    }
}
