import processing.core.PGraphics;

/**
 * Batu Simbol di tengah Hollow Woods (Bab 2).
 *
 * Objek interaktif berupa batu besar dengan ukiran simbol pelindung kuno.
 * Saat diperiksa, sang penyihir mengenali tanda-tanda sebagai bagian dari
 * jejak sigil — penanda navigasi leluhur, bukan petunjuk kasus.
 *
 * Memeriksa batu ini membuka kabut di pintu keluar timur hutan (GerbangHutan).
 */
public final class SymbolStone implements Interactable {

    private final float x;
    private final float y;
    private final float radius;
    private final GerbangHutan gerbangHutan;

    private boolean examined = false;

    private final Dialogue initialDialogue;
    private final Dialogue repeatDialogue;

    public SymbolStone(float x, float y, float radius, GerbangHutan gerbangHutan) {
        if (radius <= 0f) {
            throw new IllegalArgumentException("Radius SymbolStone harus lebih dari nol.");
        }
        if (gerbangHutan == null) {
            throw new IllegalArgumentException("SymbolStone membutuhkan referensi GerbangHutan.");
        }
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.gerbangHutan = gerbangHutan;

        this.initialDialogue = new Dialogue("BATU SIMBOL",
                "Batu besar setinggi pinggang berdiri di tengah rerumputan.",
                "Permukaannya diukir dengan simbol-simbol melingkar yang kau kenali...",
                "Ini adalah sigil pelindung kuno — tanda yang ditinggalkan penjaga hutan dari generasi lampau.",
                "Saat tanganmu menyentuh ukiran, udara bergetar pelan dan kabut di kejauhan mulai menyingkap.",
                "Jejak sigil pertama telah ditemukan. Jalan ke timur kini terbuka.");

        this.repeatDialogue = new Dialogue("BATU SIMBOL",
                "Simbol pelindung pada batu ini masih bersinar redup dalam gelap.",
                "Kau merasakan resonansi samar dari ukiran kuno itu.",
                "Kabut di jalur timur telah menyingkap berkat tanda ini.");
    }

    public SymbolStone(float x, float y, GerbangHutan gerbangHutan) {
        this(x, y, 26f, gerbangHutan);
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
        return examined ? "PERIKSA BATU" : "SENTUH BATU";
    }

    public boolean isExamined() {
        return examined;
    }

    public void restoreExamined(boolean state) {
        examined = state;
        if (state) {
            gerbangHutan.clearFog();
        }
    }

    @Override
    public Dialogue createDialogue() {
        if (!examined) {
            examined = true;
            gerbangHutan.clearFog();
            return initialDialogue;
        }
        return repeatDialogue;
    }

    @Override
    public Dialogue interact(InvestigationSystem investigation) {
        return createDialogue();
    }

    @Override
    public void draw(PGraphics g, int cameraX, int cameraY) {
        int px = Math.round(x - cameraX);
        int py = Math.round(y - cameraY);

        if (Art.ENABLED) {
            g.noStroke();
            // Bayangan lembut di tanah
            g.fill(0, 0, 0, 95);
            g.rect(px - 7, py + 4, 14, 3);
            // Batu rune berlumut slat
            g.fill(0xFF252E34);
            g.rect(px - 6, py - 8, 12, 13);
            g.fill(0xFF34424B);
            g.rect(px - 5, py - 7, 2, 10);
            g.fill(0xFF141B21);
            g.rect(px + 4, py - 7, 2, 12);
            // Ukiran rune
            g.fill(Palette.alpha(Palette.SYMBOL_GLOW, examined ? 230 : 140));
            g.rect(px - 1, py - 6, 2, 1);
            g.rect(px - 2, py - 5, 1, 3);
            g.rect(px + 1, py - 5, 1, 3);
            g.rect(px - 1, py - 2, 2, 1);
            if (examined) {
                g.fill(Palette.alpha(Palette.SYMBOL_GLOW, 65));
                g.rect(px - 5, py - 7, 10, 9);
            }
            return;
        }

        g.noStroke();

        // Bayangan batu
        g.fill(Palette.alpha(0x000000, 90));
        g.rect(px - 7, py + 3, 14, 5);

        // Badan batu utama
        g.fill(0xFF2A2C36);
        g.rect(px - 6, py - 6, 12, 12);
        g.fill(0xFF353842);
        g.rect(px - 5, py - 5, 10, 3);
        g.fill(0xFF1E2028);
        g.rect(px - 5, py + 2, 10, 3);

        // Lumut pada batu
        g.fill(Palette.MOSS);
        g.rect(px - 6, py + 4, 4, 2);
        g.rect(px + 4, py + 3, 2, 2);

        // Ukiran simbol sigil
        if (examined) {
            // Simbol bersinar setelah disentuh
            g.fill(Palette.alpha(Palette.SYMBOL_GLOW, 50));
            g.ellipse(px, py - 1, 16, 16);
            g.fill(Palette.SYMBOL_GLOW);
            g.rect(px - 2, py - 4, 4, 1);
            g.rect(px - 3, py - 2, 1, 3);
            g.rect(px + 2, py - 2, 1, 3);
            g.rect(px - 1, py + 1, 2, 1);
            // Titik pusat bercahaya
            g.fill(0xFFBBFFF4);
            g.rect(px - 1, py - 2, 2, 2);
        } else {
            // Simbol redup belum aktif
            g.fill(Palette.alpha(Palette.SYMBOL_GLOW, 80));
            g.rect(px - 2, py - 4, 4, 1);
            g.rect(px - 3, py - 2, 1, 3);
            g.rect(px + 2, py - 2, 1, 3);
            g.rect(px - 1, py + 1, 2, 1);
            g.fill(Palette.alpha(Palette.SYMBOL_GLOW, 50));
            g.rect(px - 1, py - 2, 2, 2);
        }
    }
}
