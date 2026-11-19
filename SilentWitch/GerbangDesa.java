import processing.core.PGraphics;

/**
 * Gerbang dari Ashen Village menuju Manor (Prolog → Bab 1).
 *
 * Hanya dapat dibuka setelah pemain membaca papan pengumuman
 * dan berbicara dengan tetua desa, mengkonfirmasi tujuan penyelidikan.
 */
public final class GerbangDesa implements Interactable {

    private final float x;
    private final float y;
    private final float radius;
    private boolean canProceed = false;

    public GerbangDesa(float x, float y, float radius) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius GerbangDesa harus lebih dari nol.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public GerbangDesa(float x, float y) {
        this(x, y, 28f);
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

    public boolean canProceed() {
        return canProceed;
    }

    public void setCanProceed(boolean state) {
        this.canProceed = state;
    }

    public void updateCondition(boolean hasExaminedBoard, boolean hasSpokenElder) {
        if (hasExaminedBoard && hasSpokenElder) {
            this.canProceed = true;
        }
    }

    @Override
    public String prompt() {
        return canProceed ? "MENUJU MANOR" : "GERBANG MANOR";
    }

    @Override
    public Dialogue createDialogue() {
        if (!canProceed) {
            return new Dialogue("GERBANG MANOR",
                    "Gerbang besi tua menuju manor berdiri kokoh di ujung jalan desa.",
                    "Sebelum pergi, sebaiknya bicara dengan penduduk dan periksa area desa.",
                    "Mungkin ada informasi yang berguna sebelum kau memulai penyelidikan.");
        }
        return new Dialogue("GERBANG MANOR",
                "Gerbang terbuka. Jalan berbatu mengarah ke The Silent Manor.",
                "Kabut menyelimuti jalanan, tapi tujuanmu sudah jelas.");
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        int sx = Math.round(x - cameraX);
        int sy = Math.round(y - cameraY);

        g.noStroke();
        if (!canProceed) {
            g.fill(Palette.alpha(Palette.MAGIC_DIM, 12));
            g.rect(sx - 8, sy - 10, 16, 20);
        } else {
            g.fill(Palette.alpha(Palette.WARM, 14));
            g.rect(sx - 8, sy - 10, 16, 20);
            g.fill(Palette.alpha(Palette.WARM, 8));
            g.rect(sx - 4, sy - 6, 8, 12);
        }
    }
}
