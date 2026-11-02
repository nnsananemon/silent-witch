import processing.core.PGraphics;

/**
 * Objek interaktif Gerbang Sayap Timur (Stage 05).
 *
 * Terkunci rapat sebelum puzzle kotak besi aula diselesaikan (AC-06).
 * Setelah kotak besi terbuka, interaksi dengan gerbang ini akan melepaskan
 * kunci, mengubah tile map menjadi terbuka, menghapus rintangan gerak pemain,
 * dan menandai tujuan quest selesai.
 */
public final class GerbangTimur implements Interactable {

    private final float x;
    private final float y;
    private final float radius;
    private final TileMap map;
    private final Player player;
    private final Puzzle puzzle;
    private final QuestSystem questSystem;

    public GerbangTimur(float x, float y, float radius, TileMap map, Player player,
                        Puzzle puzzle, QuestSystem questSystem) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius interaksi gerbang harus lebih dari nol.");
        }
        if (map == null) {
            throw new IllegalArgumentException("Gerbang membutuhkan TileMap.");
        }
        if (player == null) {
            throw new IllegalArgumentException("Gerbang membutuhkan Player.");
        }
        if (puzzle == null) {
            throw new IllegalArgumentException("Gerbang membutuhkan Puzzle.");
        }
        if (questSystem == null) {
            throw new IllegalArgumentException("Gerbang membutuhkan QuestSystem.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.map = map;
        this.player = player;
        this.puzzle = puzzle;
        this.questSystem = questSystem;
    }

    public GerbangTimur(float x, float y, TileMap map, Player player, Puzzle puzzle, QuestSystem questSystem) {
        this(x, y, 28f, map, player, puzzle, questSystem);
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public float radius() {
        return radius;
    }

    @Override
    public String prompt() {
        if (map.isGateOpen()) {
            return "PERIKSA LORONG";
        }
        return puzzle.isSolved() ? "BUKA GERBANG" : "PERIKSA GERBANG";
    }

    public boolean isGateOpen() {
        return map.isGateOpen();
    }

    public void restoreOpen(boolean open) {
        if (open && !map.isGateOpen()) {
            map.setGateOpen(true);
            player.removeBlocker(x, y);
        }
    }

    public void unlock() {
        if (!map.isGateOpen()) {
            map.setGateOpen(true);
            player.removeBlocker(x, y);
            questSystem.onGateUnlocked();
        }
    }

    @Override
    public Dialogue createDialogue() {
        if (map.isGateOpen()) {
            return new Dialogue("GERBANG SAYAP TIMUR",
                    "Gerbang besi telah terbuka penuh.",
                    "Lengkungan batu kuno kini membuka jalan bebas menuju Sayap Timur The Silent Manor.");
        }

        if (puzzle.isSolved()) {
            unlock();
            return new Dialogue("GERBANG SAYAP TIMUR",
                    "Kait mekanis dari Kotak Besi Aula telah terlepas!",
                    "Terali besi terangkat perlahan ke atas dinding batu.",
                    "Akses menuju Sayap Timur aula kini terbuka bebas!");
        }

        return new Dialogue("GERBANG SAYAP TIMUR",
                "Gerbang besi kokoh menuju Sayap Timur terkunci rapat oleh palang rantai tebal.",
                "Kait penguncinya terhubung lewat kabel baja ke mekanisme Kotak Besi di sudut timur laut aula.",
                "Pecahkan teka-teki kotak besi untuk melepaskan pengait gerbang.");
    }

    @Override
    public Dialogue interact(InvestigationSystem investigation) {
        return createDialogue();
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        // Tile gerbang sudah digambar prosedural oleh TileMap.
        // Kita tambahkan kilau lentera jika gerbang sudah terbuka.
        if (map.isGateOpen()) {
            int px = Math.round(x - cameraX);
            int py = Math.round(y - cameraY);
            g.noStroke();
            g.fill(Palette.alpha(Palette.WARM, 35));
            g.ellipse(px + 4, py, 26, 32);
        }
    }
}
