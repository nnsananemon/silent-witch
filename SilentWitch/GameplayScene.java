import java.util.ArrayList;
import java.util.List;

import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Scene gameplay Stage 03.
 *
 * Berisi area uji The Silent Manor dengan pemain, collision, kamera, NPC, objek
 * yang dapat diperiksa, dan panel dialog. Saat dialog terbuka, input gerak
 * diabaikan sehingga pemain tidak berjalan di belakang panel.
 *
 * Seluruh dialog masih placeholder teknis dan belum menambah fakta cerita
 * kanonis.
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

    private final float[] dustX = new float[DUST_COUNT];
    private final float[] dustY = new float[DUST_COUNT];
    private final float[] dustSpeed = new float[DUST_COUNT];
    private final float[] dustPhase = new float[DUST_COUNT];

    private float time = 0f;
    private float cameraX = 0f;
    private float cameraY = 0f;
    private float interactCooldown = 0f;
    private Interactable focused = null;

    private boolean keyUp = false;
    private boolean keyDown = false;
    private boolean keyLeft = false;
    private boolean keyRight = false;

    public GameplayScene(GameManager game) {
        super(game);
        this.map = TileMap.manorHall();
        this.player = new Player(map, map.spawnX(), map.spawnY());

        this.npc = new Npc(232f, 200f, "PENJAGA AULA", new Dialogue("PENJAGA AULA",
                "Aku penjaga aula ini. Malam masih panjang.",
                "Meja, rak, dan jendela bisa kau periksa.",
                "Belum ada cerita lain yang bisa kuceritakan."));
        player.addBlocker(npc.x(), npc.y() - Npc.BLOCK_H / 2f, Npc.BLOCK_W, Npc.BLOCK_H);
        interactables.add(npc);

        interactables.add(new ExamineSpot(96f, 126f, 26f, "MEJA AULA", new Dialogue("MEJA AULA",
                "Debu tebal menutupi permukaan meja kayu.",
                "Tidak ada catatan yang bisa dibaca di sini.",
                "Petunjuk akan ditambahkan pada tahap berikut.")));
        interactables.add(new ExamineSpot(80f, 62f, 26f, "RAK BUKU", new Dialogue("RAK BUKU",
                "Rak itu penuh buku tanpa judul di punggungnya.",
                "Belum ada isi yang ditulis untuk tahap ini.",
                "Nanti benda ini akan menyimpan petunjuk.")));
        interactables.add(new ExamineSpot(168f, 16f, 30f, "JENDELA UTARA", new Dialogue("JENDELA UTARA",
                "Di luar hanya kabut dan malam yang sunyi.",
                "Tidak ada yang bergerak di kejauhan.",
                "Area lain belum bisa dibuka pada tahap ini.")));

        for (int i = 0; i < DUST_COUNT; i++) {
            dustX[i] = Palette.hash(i * 9 + 21) * GameManager.VIRTUAL_W;
            dustY[i] = Palette.hash(i * 9 + 22) * GameManager.VIRTUAL_H;
            dustSpeed[i] = 1.5f + Palette.hash(i * 9 + 23) * 4f;
            dustPhase[i] = Palette.hash(i * 9 + 24) * 6.28318f;
        }

        centerCameraOnPlayer();
    }

    /** Jumlah objek interaktif; dipakai oleh pengujian headless. */
    public int interactableCount() {
        return interactables.size();
    }

    public boolean isDialogueOpen() {
        return dialogue.isOpen();
    }

    /** Posisi pemain pada koordinat dunia; dipakai harness pengujian. */
    public float playerX() {
        return player.x();
    }

    public float playerY() {
        return player.y();
    }

    /** Prompt objek yang sedang dalam jangkauan, atau null. */
    public String focusedPrompt() {
        return focused == null ? null : focused.prompt();
    }

    @Override
    public void onEnter() {
        clearInput();
        dialogue.close();
        focused = null;
        centerCameraOnPlayer();
    }

    @Override
    public void onExit() {
        // Mencegah input "nyangkut" saat scene gameplay tidak aktif.
        clearInput();
        dialogue.close();
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

        for (int i = 0; i < DUST_COUNT; i++) {
            dustY[i] -= dustSpeed[i] * dt;
            if (dustY[i] < HEADER_H) {
                dustY[i] = GameManager.VIRTUAL_H;
            }
        }

        float inputX = 0f;
        float inputY = 0f;
        if (!dialogue.isOpen()) {
            inputX = (keyRight ? 1f : 0f) - (keyLeft ? 1f : 0f);
            inputY = (keyDown ? 1f : 0f) - (keyUp ? 1f : 0f);
        }
        player.update(dt, inputX, inputY);

        focused = dialogue.isOpen() ? null
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
    }

    /** Menggambar NPC dan pemain dengan urutan kedalaman berdasarkan posisi y. */
    private void drawEntities(PGraphics g, int camX, int camY) {
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

    /** Prompt kecil di atas objek terdekat: "[E] BICARA" atau "[E] PERIKSA". */
    private void drawPrompt(PGraphics g, int camX, int camY) {
        if (focused == null || dialogue.isOpen()) {
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

        PixelUI.text(g, "THE SILENT MANOR - AULA UJI", 6, 4, Palette.TEXT_DIM);
        PixelFont.drawRight(g, "TAHAP 03", 314, 4, 1, Palette.TEXT_MUTED);
    }

    private void drawHint(PGraphics g) {
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
            PixelUI.centered(g, "GERAK: WASD/PANAH   E: INTERAKSI   ESC: MENU", 160, y + 3,
                    Palette.TEXT_MUTED);
        }
    }

    @Override
    public void keyPressed(char key, int keyCode) {
        if (isEscape(key, keyCode)) {
            if (dialogue.isOpen()) {
                dialogue.close();
                interactCooldown = INPUT_COOLDOWN;
                return;
            }
            game.showMenu();
            return;
        }

        if (dialogue.isOpen()) {
            // Hanya tombol lanjut yang diproses; tombol gerak diabaikan.
            if (isInteract(key, keyCode) && interactCooldown <= 0f) {
                dialogue.advance();
                interactCooldown = INPUT_COOLDOWN;
            }
            return;
        }

        if (isInteract(key, keyCode)) {
            if (interactCooldown <= 0f && focused != null) {
                dialogue.open(focused.createDialogue());
                // Lepas semua tombol gerak agar pemain tidak meluncur saat dialog.
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
    public void focusLost() {
        // Jendela kehilangan fokus: hentikan gerakan agar tombol tidak tertahan.
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
