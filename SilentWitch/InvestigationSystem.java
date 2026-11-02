import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Sistem investigasi dan Detective Casebook sesuai PRD Seksi 4 dan 10.4.
 *
 * Mengelola pendaftaran clue, pencatatan bukti (mencegah duplikasi), notifikasi
 * toast penemuan bukti baru, dan antarmuka buku kasus (Casebook) modal.
 */
public final class InvestigationSystem {

    public static final float TOAST_DURATION = 3.5f;

    public static final int TAB_BUKTI = 0;
    public static final int TAB_KRONOLOGI = 1;

    private static final int FRAME_X = 6;
    private static final int FRAME_Y = 6;
    private static final int FRAME_W = 308;
    private static final int FRAME_H = 168;

    private final Map<String, Clue> registeredClues = new LinkedHashMap<String, Clue>();
    private final List<String> registeredOrder = new ArrayList<String>();
    private final Set<String> discoveredIds = new LinkedHashSet<String>();

    private boolean casebookOpen = false;
    private int activeTab = TAB_BUKTI;
    private int selectedIndex = 0;
    private int hoveredIndex = -1;
    private int hoveredTab = -1;
    private boolean hoveredClose = false;

    private String toastMessage = null;
    private float toastTimer = 0f;

    public InvestigationSystem() {
        // Sistem dimulai kosong; clue didaftarkan oleh scene atau data loader.
    }

    /**
     * Mendaftarkan clue ke dalam sistem investigasi.
     *
     * @throws IllegalArgumentException bila clue null atau ID sudah terdaftar.
     */
    public void registerClue(Clue clue) {
        if (clue == null) {
            throw new IllegalArgumentException("Clue yang didaftarkan tidak boleh null.");
        }
        if (registeredClues.containsKey(clue.id())) {
            throw new IllegalArgumentException("Clue dengan ID '" + clue.id() + "' sudah terdaftar.");
        }
        registeredClues.put(clue.id(), clue);
        registeredOrder.add(clue.id());
    }

    public boolean isRegistered(String clueId) {
        return clueId != null && registeredClues.containsKey(clueId);
    }

    public boolean isDiscovered(String clueId) {
        return clueId != null && discoveredIds.contains(clueId);
    }

    /**
     * Menandai clue sebagai ditemukan.
     *
     * @return true jika clue baru pertama kali ditemukan; false jika sudah pernah dicatat.
     */
    public boolean discover(Clue clue) {
        if (clue == null) {
            throw new IllegalArgumentException("Clue yang ditemukan tidak boleh null.");
        }
        if (!registeredClues.containsKey(clue.id())) {
            registerClue(clue);
        }
        return discoverById(clue.id(), clue.title());
    }

    /**
     * Menandai clue sebagai ditemukan berdasarkan ID terdaftar.
     *
     * @return true jika baru ditemukan; false jika sudah pernah dicatat.
     */
    public boolean discover(String clueId) {
        if (clueId == null || !registeredClues.containsKey(clueId)) {
            throw new IllegalArgumentException("Clue ID '" + clueId + "' belum terdaftar di sistem investigasi.");
        }
        Clue clue = registeredClues.get(clueId);
        return discoverById(clueId, clue.title());
    }

    private boolean discoverById(String clueId, String title) {
        if (discoveredIds.contains(clueId)) {
            return false;
        }
        discoveredIds.add(clueId);
        toastMessage = "BUKTI BARU: " + title + " (TEKAN C)";
        toastTimer = TOAST_DURATION;
        return true;
    }

    public int totalCount() {
        return registeredOrder.size();
    }

    public int discoveredCount() {
        return discoveredIds.size();
    }

    public Clue getClue(String clueId) {
        return registeredClues.get(clueId);
    }

    public Clue clueAt(int index) {
        if (index < 0 || index >= registeredOrder.size()) {
            throw new IndexOutOfBoundsException("Indeks clue " + index + " di luar batas.");
        }
        return registeredClues.get(registeredOrder.get(index));
    }

    public List<Clue> allRegisteredClues() {
        List<Clue> list = new ArrayList<Clue>(registeredOrder.size());
        for (String id : registeredOrder) {
            list.add(registeredClues.get(id));
        }
        return Collections.unmodifiableList(list);
    }

    public List<Clue> allDiscoveredClues() {
        List<Clue> list = new ArrayList<Clue>(discoveredIds.size());
        for (String id : discoveredIds) {
            list.add(registeredClues.get(id));
        }
        return Collections.unmodifiableList(list);
    }

    public List<String> allDiscoveredIds() {
        return new ArrayList<String>(discoveredIds);
    }

    public void restoreDiscovered(String clueId) {
        if (clueId != null && registeredClues.containsKey(clueId)) {
            discoveredIds.add(clueId);
        }
    }

    public void reset() {
        discoveredIds.clear();
        casebookOpen = false;
        selectedIndex = 0;
        activeTab = TAB_BUKTI;
        clearNotification();
    }

    // --- Notifikasi Toast ---

    public void setNotification(String message, float duration) {
        this.toastMessage = message;
        this.toastTimer = duration;
    }

    public void clearNotification() {
        this.toastMessage = null;
        this.toastTimer = 0f;
    }

    public String currentNotification() {
        return toastMessage;
    }

    public boolean hasNotification() {
        return toastTimer > 0f && toastMessage != null;
    }

    public void update(float dt) {
        if (toastTimer > 0f) {
            toastTimer = Math.max(0f, toastTimer - dt);
            if (toastTimer == 0f) {
                toastMessage = null;
            }
        }
    }

    // --- State Casebook ---

    public boolean isCasebookOpen() {
        return casebookOpen;
    }

    public void openCasebook() {
        casebookOpen = true;
        hoveredIndex = -1;
        hoveredTab = -1;
        hoveredClose = false;
        if (selectedIndex >= totalCount()) {
            selectedIndex = Math.max(0, totalCount() - 1);
        }
    }

    public void closeCasebook() {
        casebookOpen = false;
        hoveredIndex = -1;
        hoveredTab = -1;
        hoveredClose = false;
    }

    public void toggleCasebook() {
        if (casebookOpen) {
            closeCasebook();
        } else {
            openCasebook();
        }
    }

    public int activeTab() {
        return activeTab;
    }

    public void setActiveTab(int tab) {
        if (tab == TAB_BUKTI || tab == TAB_KRONOLOGI) {
            this.activeTab = tab;
        }
    }

    public int selectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (totalCount() == 0) {
            selectedIndex = 0;
            return;
        }
        selectedIndex = Math.max(0, Math.min(totalCount() - 1, index));
    }

    public Clue selectedClue() {
        if (totalCount() == 0 || selectedIndex < 0 || selectedIndex >= totalCount()) {
            return null;
        }
        return clueAt(selectedIndex);
    }

    public boolean isSelectedDiscovered() {
        Clue c = selectedClue();
        return c != null && isDiscovered(c.id());
    }

    // --- Input Casebook ---

    public void keyPressed(char key, int keyCode) {
        if (!casebookOpen) {
            return;
        }

        if (key == 27 || keyCode == 27 || key == 'c' || key == 'C' || key == 'j' || key == 'J') {
            closeCasebook();
            return;
        }

        if (key == 'q' || key == 'Q' || keyCode == PConstants.LEFT) {
            activeTab = TAB_BUKTI;
            return;
        }

        if (key == 'e' || key == 'E' || keyCode == PConstants.RIGHT || key == '\t') {
            activeTab = (activeTab == TAB_BUKTI) ? TAB_KRONOLOGI : TAB_BUKTI;
            return;
        }

        if (key == '1') {
            activeTab = TAB_BUKTI;
            return;
        }

        if (key == '2') {
            activeTab = TAB_KRONOLOGI;
            return;
        }

        if (activeTab == TAB_BUKTI && totalCount() > 0) {
            if (keyCode == PConstants.UP || key == 'w' || key == 'W') {
                selectedIndex = (selectedIndex - 1 + totalCount()) % totalCount();
            } else if (keyCode == PConstants.DOWN || key == 's' || key == 'S') {
                selectedIndex = (selectedIndex + 1) % totalCount();
            }
        }
    }

    public void mouseMoved(float x, float y) {
        if (!casebookOpen) {
            return;
        }

        hoveredTab = -1;
        if (y >= 23 && y <= 36) {
            if (x >= 12 && x <= 122) {
                hoveredTab = TAB_BUKTI;
            } else if (x >= 126 && x <= 266) {
                hoveredTab = TAB_KRONOLOGI;
            }
        }

        hoveredClose = (x >= 284 && x <= 306 && y >= 23 && y <= 36);

        hoveredIndex = -1;
        if (activeTab == TAB_BUKTI && x >= 12 && x <= 130) {
            for (int i = 0; i < totalCount(); i++) {
                int rowY = 40 + i * 27;
                if (y >= rowY && y < rowY + 25) {
                    hoveredIndex = i;
                    break;
                }
            }
        }
    }

    public void mousePressed(float x, float y) {
        if (!casebookOpen) {
            return;
        }

        if (x < FRAME_X || x > FRAME_X + FRAME_W || y < FRAME_Y || y > FRAME_Y + FRAME_H) {
            closeCasebook();
            return;
        }

        if (hoveredClose) {
            closeCasebook();
            return;
        }

        if (hoveredTab == TAB_BUKTI || hoveredTab == TAB_KRONOLOGI) {
            activeTab = hoveredTab;
            return;
        }

        if (activeTab == TAB_BUKTI && hoveredIndex >= 0 && hoveredIndex < totalCount()) {
            selectedIndex = hoveredIndex;
        }
    }

    // --- Render Casebook dan Notifikasi ---

    public void drawNotification(PGraphics g) {
        if (!hasNotification() || casebookOpen) {
            return;
        }

        int bannerW = 270;
        int bannerH = 15;
        int bannerX = (GameManager.VIRTUAL_W - bannerW) / 2;
        int bannerY = 18;

        g.noStroke();
        g.fill(Palette.PANEL_DARK);
        g.rect(bannerX, bannerY, bannerW, bannerH);

        g.fill(Palette.ACCENT);
        g.rect(bannerX, bannerY, bannerW, 1);
        g.rect(bannerX, bannerY + bannerH - 1, bannerW, 1);
        g.rect(bannerX, bannerY, 2, bannerH);
        g.rect(bannerX + bannerW - 2, bannerY, 2, bannerH);

        PixelUI.centered(g, toastMessage, GameManager.VIRTUAL_W / 2f, bannerY + 4, Palette.ACCENT);
    }

    public void drawCasebook(PGraphics g) {
        if (!casebookOpen) {
            return;
        }

        PixelUI.dim(g, 215);
        PixelUI.panel(g, FRAME_X, FRAME_Y, FRAME_W, FRAME_H);

        drawCasebookHeader(g);
        drawCasebookTabs(g);

        if (activeTab == TAB_BUKTI) {
            drawClueListTab(g);
        } else {
            drawTimelineTab(g);
        }

        drawCasebookFooter(g);
    }

    private void drawCasebookHeader(PGraphics g) {
        PixelUI.text(g, "BUKU KASUS PENYELIDIK", FRAME_X + 8, FRAME_Y + 7, Palette.ACCENT);

        String countText = "BUKTI: " + discoveredCount() + " / " + totalCount() + " DITEMUKAN";
        PixelFont.drawRight(g, countText, FRAME_X + FRAME_W - 32, FRAME_Y + 7, 1, Palette.TEXT_MUTED);

        PixelUI.divider(g, FRAME_X + FRAME_W / 2f, FRAME_Y + 18, FRAME_W - 16);
    }

    private void drawCasebookTabs(PGraphics g) {
        boolean sel0 = activeTab == TAB_BUKTI;
        PixelUI.button(g, 12, 23, 110, 13, "[1] DAFTAR BUKTI", sel0, true);

        boolean sel1 = activeTab == TAB_KRONOLOGI;
        PixelUI.button(g, 126, 23, 140, 13, "[2] KRONOLOGI & HIPOTESIS", sel1, true);

        PixelUI.button(g, 284, 23, 22, 13, "[X]", hoveredClose, true);
    }

    private static String getShortTitle(Clue clue) {
        String t = clue.title();
        if (t.contains("SOBEKAN")) return "SOBEKAN CATATAN";
        if (t.contains("HARIAN")) return "BUKU HARIAN";
        if (t.contains("GORESAN")) return "GORESAN KUSEN";
        if (t.contains("KESAKSIAN")) return "SAKSI PENJAGA";
        if (t.length() > 16) return t.substring(0, 15) + ".";
        return t;
    }

    private static String getReliabilityBadge(Clue clue) {
        String r = clue.reliability().toUpperCase();
        if (r.contains("FISIK")) return "[BUKTI FISIK]";
        if (r.contains("DOKUMEN")) return "[DOKUMEN]";
        if (r.contains("LINGKUNGAN")) return "[LINGKUNGAN]";
        if (r.contains("KESAKSIAN") || r.contains("LISAN")) return "[KESAKSIAN]";
        return "[" + clue.reliability() + "]";
    }

    private void drawClueListTab(PGraphics g) {
        // Kolom Kiri: Daftar Clue
        int listX = 12;
        int listY = 39;
        int listW = 118;
        int listH = 114;

        g.noStroke();
        g.fill(Palette.PANEL_DARK);
        g.rect(listX, listY, listW, listH);
        g.fill(Palette.BORDER_DIM);
        g.rect(listX, listY, listW, 1);
        g.rect(listX, listY + listH - 1, listW, 1);
        g.rect(listX, listY, 1, listH);
        g.rect(listX + listW - 1, listY, 1, listH);

        for (int i = 0; i < totalCount(); i++) {
            Clue clue = clueAt(i);
            boolean isDisc = isDiscovered(clue.id());
            boolean isSel = (selectedIndex == i);
            boolean isHov = (hoveredIndex == i);

            int rowY = listY + 2 + i * 27;
            int rowH = 25;
            int rowW = listW - 4;

            g.fill(isSel ? Palette.PANEL_LIGHT : (isHov ? Palette.PANEL : Palette.PANEL_DARK));
            g.rect(listX + 2, rowY, rowW, rowH);

            if (isSel) {
                g.fill(Palette.ACCENT);
                g.rect(listX + 2, rowY, 2, rowH);
                PixelUI.text(g, ">", listX + 5, rowY + 4, Palette.ACCENT);
            }

            String slotTitle = isDisc ? ((i + 1) + ". " + getShortTitle(clue)) : ((i + 1) + ". ??? TERKUNCI");
            int titleCol = isSel ? Palette.ACCENT : (isDisc ? Palette.TEXT : Palette.TEXT_MUTED);
            PixelUI.text(g, slotTitle, listX + (isSel ? 12 : 6), rowY + 4, titleCol);

            String badge = isDisc ? getReliabilityBadge(clue) : "[BELUM DITEMUKAN]";
            int badgeCol = isDisc ? Palette.TEXT_DIM : Palette.TEXT_MUTED;
            PixelUI.text(g, badge, listX + (isSel ? 12 : 6), rowY + 14, badgeCol);
        }

        // Kolom Kanan: Rincian Bukti Terpilih
        int detX = 134;
        int detY = 39;
        int detW = 172;
        int detH = 114;

        PixelUI.panel(g, detX, detY, detW, detH);

        Clue current = selectedClue();
        if (current == null) {
            PixelUI.centered(g, "TIDAK ADA DATA BUKTI", detX + detW / 2f, detY + 45, Palette.TEXT_MUTED);
            return;
        }

        if (isDiscovered(current.id())) {
            PixelUI.text(g, current.title(), detX + 8, detY + 6, Palette.ACCENT);
            PixelUI.text(g, "TIPE: " + current.reliability(), detX + 8, detY + 16, Palette.TEXT_MUTED);
            PixelUI.divider(g, detX + detW / 2f, detY + 26, detW - 16);

            PixelUI.text(g, "SUMBER: " + current.source(), detX + 8, detY + 30, Palette.TEXT_DIM);
            PixelUI.text(g, "WAKTU : " + current.timelineTimestamp(), detX + 8, detY + 40, Palette.TEXT_DIM);
            PixelUI.divider(g, detX + detW / 2f, detY + 50, detW - 16);

            // Deskripsi lengkap
            PixelFont.drawWrapped(g, current.description(), detX + 8, detY + 55, detW - 16, 1, Palette.TEXT);

            // Hubungan bukti
            if (current.relatedClueIds().length > 0) {
                StringBuilder rel = new StringBuilder("TERKAIT: ");
                for (int r = 0; r < current.relatedClueIds().length; r++) {
                    String rid = current.relatedClueIds()[r];
                    if (r > 0) {
                        rel.append(", ");
                    }
                    if (isDiscovered(rid)) {
                        Clue rc = getClue(rid);
                        rel.append(rc != null ? getShortTitle(rc) : rid);
                    } else {
                        rel.append("???");
                    }
                }
                PixelUI.text(g, rel.toString(), detX + 8, detY + detH - 12, Palette.TEXT_MUTED);
            }
        } else {
            PixelUI.text(g, "??? BUKTI TERKUNCI", detX + 8, detY + 8, Palette.TEXT_MUTED);
            PixelUI.divider(g, detX + detW / 2f, detY + 20, detW - 16);

            String lockMsg = "BUKTI ATAU INFORMASI INI BELUM DITEMUKAN.\n\n"
                    + "JELAJAHI AREA AULA, PERIKSA SETIAP SUDUT YANG MENCURIGAKAN, DAN AJAK BICARA SAKSI UNTUK MENGUMPULKAN KETERANGAN.";
            PixelFont.drawWrapped(g, lockMsg, detX + 8, detY + 28, detW - 16, 1, Palette.TEXT_DIM);
        }
    }

    private void drawTimelineTab(PGraphics g) {
        int paneX = 12;
        int paneY = 39;
        int paneW = 294;
        int paneH = 114;

        PixelUI.panel(g, paneX, paneY, paneW, paneH);

        // Bagian 1: Kronologi Urutan Waktu Sesuai Linimasa Kasus
        PixelUI.text(g, "KRONOLOGI INVESTIGASI AULA", paneX + 8, paneY + 5, Palette.ACCENT);
        PixelUI.divider(g, paneX + paneW / 2f, paneY + 14, paneW - 16);

        String[] timelineKeys = {"clue_rak_harian", "clue_meja_catatan", "clue_penjaga_kesaksian", "clue_jendela_goresan"};

        int lineY = paneY + 17;
        for (String key : timelineKeys) {
            Clue c = getClue(key);
            if (c == null) {
                continue;
            }
            boolean disc = isDiscovered(c.id());

            String timeTag = "[" + c.timelineTimestamp() + "]";
            PixelUI.text(g, timeTag, paneX + 8, lineY, Palette.ACCENT_DIM);

            String eventDesc = disc ? (getShortTitle(c) + " (" + c.source() + ")") : "??? BUKTI BELUM DITEMUKAN";
            int col = disc ? Palette.TEXT : Palette.TEXT_MUTED;
            PixelUI.text(g, eventDesc, paneX + 96, lineY, col);

            lineY += 9;
        }

        // Bagian 2: Hipotesis Kasus
        PixelUI.divider(g, paneX + paneW / 2f, paneY + 56, paneW - 16);

        if (discoveredCount() == totalCount() && totalCount() > 0) {
            PixelUI.text(g, "HIPOTESIS KASUS: [TERVALIDASI (4/4)]", paneX + 8, paneY + 60, Palette.ACCENT);
            String hyp = "PENYUSUP MENGETAHUI JADWAL DARI CATATAN MEJA, MEMBUKA JENDELA UTARA DARI LUAR, LALU MERUSAK BUKU HARIAN DI RAK SEBELUM TERDENGAR PENJAGA.";
            PixelFont.drawWrapped(g, hyp, paneX + 8, paneY + 70, paneW - 16, 1, Palette.TEXT);
        } else {
            PixelUI.text(g, "HIPOTESIS KASUS: [BELUM LENGKAP (" + discoveredCount() + "/" + totalCount() + ")]", paneX + 8, paneY + 60, Palette.WARM);
            String hyp = "KRONOLOGI MASIH MEMILIKI CELAH. TEMUKAN SELURUH PETUNJUK DI AULA UNTUK MENGHUBUNGKAN PERISTIWA MALAM ITU DAN MENGUJI KESAKSIAN.";
            PixelFont.drawWrapped(g, hyp, paneX + 8, paneY + 70, paneW - 16, 1, Palette.TEXT_DIM);
        }
    }

    private void drawCasebookFooter(PGraphics g) {
        int y = FRAME_Y + FRAME_H - 12;
        PixelUI.divider(g, FRAME_X + FRAME_W / 2f, y - 2, FRAME_W - 16);

        if (activeTab == TAB_BUKTI) {
            PixelUI.centered(g, "W/S: PILIH BUKTI   Q/E: TAB   ESC/C: TUTUP",
                    FRAME_X + FRAME_W / 2f, y + 2, Palette.TEXT_MUTED);
        } else {
            PixelUI.centered(g, "Q/E: TAB   ESC/C: TUTUP",
                    FRAME_X + FRAME_W / 2f, y + 2, Palette.TEXT_MUTED);
        }
    }
}
