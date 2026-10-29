# SILENT WITCH

## Product Requirements Document (PRD) + Art & Asset Guide

**Versi:** 2.0\
**Platform target:** Windows PC\
**Engine / lingkungan:** Processing 4 --- Java Mode\
**Bahasa game:** Bahasa Indonesia\
**Genre:** Pixel-art mystery RPG, investigasi, dan puzzle logika
edukatif\
**Status dokumen:** Spesifikasi untuk AI coding assistant; belum
merupakan kode game

------------------------------------------------------------------------

## 1. Ringkasan Produk

**Silent Witch** adalah game pixel-art bergaya gothic mystery tentang
seorang penyihir pendiam yang menyelidiki kematian seorang anak
perempuan di Ashen Village. Kasus tersebut mengarah pada sebuah kelompok
pemujaan fiktif yang beroperasi secara rahasia. Pemain menjelajahi
dunia, berbicara dengan saksi, mengumpulkan bukti, menyusun kronologi,
dan menyelesaikan puzzle logika untuk menguji setiap kesimpulan.

Pemain tidak boleh memperoleh identitas pelaku hanya karena permainan
menyatakannya. Kesimpulan harus didukung oleh bukti yang ditemukan dan
hubungan logis yang dapat dijelaskan. Tema pembunuhan disampaikan secara
non-grafis dan berfokus pada penyelidikan, dampak pada warga, serta
penalaran.

### 1.1 Tujuan utama

1.  Membuat game RPG 2D pixel-art yang dapat dimainkan di Processing 4
    Java Mode.
2.  Mengajarkan penalaran logis melalui puzzle yang menjadi bagian dari
    investigasi.
3.  Memberikan pengalaman misteri yang konsisten: bukti, dialog,
    kronologi, dan ending tidak saling bertentangan.
4.  Menyediakan progres dan penyimpanan otomatis agar pemain dapat
    melanjutkan permainan.
5.  Menyediakan struktur kode dan aset yang mudah dikembangkan secara
    bertahap.

### 1.2 Prinsip desain

-   **Bukti sebelum kesimpulan:** klaim penting harus didukung clue yang
    sudah ditemukan.
-   **Tidak ada fakta kanon yang dikarang:** AI coding assistant tidak
    boleh menambah pelaku, motif, lokasi, atau fakta cerita tanpa
    memperbarui dokumen desain terlebih dahulu.
-   **Puzzle memiliki alasan naratif:** puzzle harus membantu membuka
    akses, memeriksa bukti, atau menguji kronologi.
-   **Gagal bukan berarti buntu:** pemain dapat meminta petunjuk dan
    memeriksa ulang bukti.
-   **Horror atmosferik, bukan grafis:** hindari visual gore atau detail
    eksplisit.
-   **Scope terkendali:** buat fondasi yang stabil sebelum menambahkan
    konten besar.

------------------------------------------------------------------------

## 2. Premis, Cerita, dan Dunia

### 2.1 Premis

Di Ashen Village, seorang anak perempuan ditemukan meninggal dalam
keadaan yang menimbulkan banyak pertanyaan. Penduduk takut membicarakan
kejadian itu. Sejumlah petunjuk mengarah pada aktivitas sebuah kultus
fiktif yang dikenal melalui simbol, catatan rahasia, dan pertemuan
tersembunyi. Pemain berperan sebagai **Silent Witch**, penyelidik yang
jarang berbicara dan lebih mengandalkan observasi, bukti, serta deduksi.

Identitas korban, pelaku, motif final, dan rincian kejadian harus
ditetapkan dalam dokumen narasi terpisah sebelum implementasi konten
final. Jangan mengarang jawaban kasus saat menulis kode.

### 2.2 Nada dan batas penggambaran

-   Gothic, sunyi, misterius, dan sedikit menyeramkan.
-   Kekerasan tidak ditampilkan secara eksplisit.
-   Korban diperlakukan dengan hormat; tidak menjadikan kematiannya
    sebagai lelucon atau kejutan murahan.
-   Kultus adalah organisasi fiktif dalam dunia game. Hindari menyamakan
    kelompok fiktif tersebut dengan agama atau kelompok nyata.
-   Ketegangan dibangun melalui lingkungan, dialog, simbol, suara, dan
    kontradiksi bukti.

### 2.3 Area dunia

Dunia terdiri dari area-area yang saling terhubung. Akses ke sebagian
area dapat dikunci oleh quest atau puzzle.

1.  **Ashen Village** --- desa utama, tempat pemain mengenal warga,
    mengumpulkan kesaksian, dan memulai investigasi.
2.  **The Silent Manor** --- rumah tua dengan dokumen, ruang terkunci,
    dan bukti yang perlu diperiksa.
3.  **Hollow Woods** --- hutan berkabut dengan jalur bercabang dan
    petunjuk lingkungan.
4.  **Chapel of Whispers** --- bangunan ibadah fiktif yang telah
    ditinggalkan; menyimpan simbol dan catatan yang relevan dengan
    kasus.
5.  **The Underground Archive** --- arsip tersembunyi untuk
    menghubungkan catatan, nama, tanggal, dan kejadian.
6.  **The Black Sanctuary** --- lokasi akhir investigasi; aksesnya harus
    dibuka oleh rangkaian bukti dan deduksi yang memadai.

Area-area di atas adalah rancangan target. Jika implementasi awal
terlalu besar, gunakan area kecil yang saling terhubung dan tambahkan
area berikutnya pada tahap pengembangan lanjutan.

### 2.4 Karakter

-   **Silent Witch:** karakter yang dikendalikan pemain. Pendiam,
    teliti, dan mengandalkan observasi serta catatan investigasi.
-   **Korban anak perempuan:** pusat kasus. Nama dan detail latar
    belakang harus ditetapkan dalam dokumen cerita sebelum dialog final
    ditulis.
-   **Warga dan saksi:** karakter pendukung yang memberikan informasi
    dengan tingkat keandalan berbeda. Pernyataan mereka dapat tidak
    lengkap atau saling bertentangan, tetapi kontradiksi harus disengaja
    dan dapat diuji.
-   **Pihak yang terkait dengan kultus:** identitas, jabatan, motif, dan
    hubungan dengan korban tidak boleh ditetapkan secara ad hoc oleh AI
    coding assistant.

------------------------------------------------------------------------

## 3. Tujuan Pemain dan Core Gameplay Loop

### 3.1 Tujuan pemain

Mengungkap fakta kasus dengan mengumpulkan bukti, menguji kesaksian,
menyusun kronologi, memecahkan puzzle, dan memilih kesimpulan yang
paling kuat dukungan buktinya.

### 3.2 Siklus gameplay

1.  Jelajahi area dan perhatikan objek atau kejadian yang mencurigakan.
2.  Interaksikan objek dan NPC untuk memperoleh informasi.
3.  Catat clue baru ke dalam **Detective Casebook**.
4.  Hubungkan clue, pernyataan saksi, lokasi, dan waktu.
5.  Selesaikan puzzle logika untuk menguji hipotesis atau membuka akses.
6.  Perbarui quest dan buka area sesuai prasyarat.
7.  Ajukan kesimpulan akhir berdasarkan bukti yang benar-benar
    ditemukan.

### 3.3 Kontrol target

-   **WASD / tombol panah:** bergerak.
-   **E / Enter / Space:** interaksi atau lanjut dialog.
-   **Esc:** membuka menu jeda atau menutup panel yang dapat ditutup.
-   **Mouse:** memilih pilihan pada menu, casebook, dan puzzle jika
    antarmuka mendukung mouse.

Kontrol harus konsisten dan dapat ditampilkan pada menu bantuan. Jangan
mengasumsikan dukungan gamepad pada versi awal.

------------------------------------------------------------------------

## 4. Sistem Investigasi

### 4.1 Detective Casebook

Casebook adalah pusat informasi investigasi. Minimal menyediakan:

-   Daftar clue yang ditemukan.
-   Deskripsi setiap clue.
-   Lokasi dan sumber clue.
-   Pernyataan saksi yang telah didengar.
-   Kronologi kejadian yang dapat disusun atau ditinjau.
-   Ringkasan hipotesis yang telah dibuka oleh progres permainan.
-   Indikator bukti yang masih dibutuhkan untuk kesimpulan tertentu.

Clue yang belum ditemukan tidak boleh muncul sebagai fakta yang
diketahui pemain. UI boleh menampilkan slot kosong atau kategori
terkunci tanpa membocorkan isi clue.

### 4.2 Struktur data clue

Setiap clue minimal memiliki:

-   `id` unik dan stabil.
-   `title` atau nama.
-   `description`.
-   `source` atau lokasi asal.
-   `discoveryCondition`.
-   `relatedClueIds`.
-   `relatedQuestIds`.
-   `timelineTimestamp` bila relevan.
-   `reliability` atau status validasi bila desain membutuhkannya.

ID clue tidak boleh berubah setelah dipakai oleh quest, puzzle, dialog,
atau save data. Jika ID harus diubah, migrasikan save data atau tandai
perubahan tersebut secara eksplisit.

### 4.3 Aturan konsistensi bukti

-   Setiap kesimpulan penting harus memiliki jalur bukti yang dapat
    diperiksa.
-   Dialog tidak boleh menyebut bahwa pemain telah menemukan clue jika
    flag penemuannya belum aktif.
-   Puzzle tidak boleh mensyaratkan clue yang tidak mungkin ditemukan
    sebelumnya.
-   Jika dua kesaksian bertentangan, game harus memberi konteks untuk
    menguji pertentangan itu.
-   Jangan gunakan RNG untuk menentukan fakta inti kasus.
-   Jangan mengubah hasil deduksi hanya untuk memaksa pemain menuju
    ending tertentu.

------------------------------------------------------------------------

## 5. Puzzle Logika Edukatif

Puzzle harus menjadi bagian alami dari investigasi, bukan sekadar
tantangan acak.

### 5.1 Jenis puzzle yang direncanakan

1.  **Pola dan urutan:** menemukan aturan dari simbol, angka, atau
    susunan objek.
2.  **Logika kondisi:** menentukan hasil dari beberapa kondisi yang
    dinyatakan secara jelas.
3.  **Deduksi dan eliminasi:** menguji kemungkinan berdasarkan petunjuk
    yang tersedia.
4.  **Urutan kejadian:** menyusun peristiwa berdasarkan waktu dan
    kesaksian.
5.  **Pencocokan bukti:** menghubungkan catatan, simbol, nama, atau
    lokasi berdasarkan aturan yang telah dijelaskan.

### 5.2 Persyaratan setiap puzzle

Setiap puzzle harus memiliki:

-   ID unik.
-   Tujuan yang jelas.
-   Instruksi yang dapat dipahami tanpa menebak aturan tersembunyi.
-   Data input yang konsisten.
-   Jawaban benar yang ditentukan secara eksplisit.
-   Validasi jawaban.
-   Umpan balik jika salah.
-   Petunjuk bertahap jika pemain meminta bantuan.
-   Penjelasan singkat mengapa jawaban benar.
-   Prasyarat clue atau quest, jika ada.

Puzzle yang memiliki beberapa jawaban valid harus menerima semua jawaban
yang memang sesuai aturan. Jangan menghukum pemain karena memilih
jawaban alternatif yang secara logis benar.

### 5.3 Sistem petunjuk

-   Petunjuk pertama mengarahkan perhatian pada informasi yang relevan.
-   Petunjuk kedua menjelaskan aturan atau hubungan yang perlu
    diperiksa.
-   Petunjuk terakhir boleh memperjelas langkah penyelesaian, tetapi
    tetap memberi pemain kesempatan untuk memilih jawaban.
-   Menggunakan petunjuk tidak boleh merusak save atau membuat quest
    tidak dapat diselesaikan.
-   Petunjuk tidak boleh memasukkan fakta cerita baru yang tidak
    didukung oleh konten.

### 5.4 Kriteria penerimaan puzzle

-   Semua puzzle dapat diselesaikan menggunakan informasi yang tersedia
    pada saat puzzle dibuka.
-   Jawaban benar dan salah diuji.
-   Puzzle tidak bisa dibypass dengan menekan tombol secara acak tanpa
    batas jika hal itu merusak pengalaman.
-   Setelah berhasil, progres tercatat dan puzzle tidak mengulang dari
    awal tanpa alasan.
-   Penjelasan jawaban konsisten dengan aturan puzzle.

------------------------------------------------------------------------

## 6. Quest, Progres, dan Ending

### 6.1 Quest system

Quest minimal menyimpan:

-   ID unik.
-   Judul dan deskripsi.
-   Status: belum terbuka, aktif, selesai, atau gagal bila memang
    dibutuhkan.
-   Prasyarat.
-   Tujuan dan kondisi penyelesaian.
-   Referensi clue, area, NPC, atau puzzle.
-   Dialog atau perubahan dunia yang dipicu setelah selesai.

Satu quest tidak boleh ditandai selesai hanya karena pemain memasuki
area apabila syarat investigasinya belum terpenuhi.

### 6.2 Rancangan ending

Ending ditentukan oleh kualitas deduksi dan bukti yang telah
dikumpulkan, bukan pilihan acak.

-   **The Truth (True Ending):** pemain menyusun penjelasan yang
    didukung bukti kunci dan menyelesaikan rangkaian deduksi.
-   **The Unfinished Case (Partial Ending):** pemain mencapai akhir
    tanpa cukup bukti untuk memastikan seluruh rangkaian kejadian.
-   **The False Accusation (Bad Deduction):** pemain memilih kesimpulan
    yang tidak didukung atau bertentangan dengan bukti yang telah
    ditemukan.

Rancangan ini belum menetapkan siapa pelaku sebenarnya atau motif final.
Detail tersebut harus diselesaikan pada tahap penulisan narasi dan
dicatat dalam dokumen kanon sebelum ending diimplementasikan.

### 6.3 Validasi ending

-   Game mengevaluasi bukti dan syarat deduksi yang tercatat.
-   Pemain diberi ringkasan alasan yang mendasari hasil investigasinya.
-   Ending tidak boleh bergantung pada variabel tersembunyi yang tidak
    pernah dapat dipengaruhi pemain.
-   Setelah ending, pemain dapat melihat ringkasan kasus. Fitur replay
    atau New Game+ bukan kebutuhan wajib versi awal.

------------------------------------------------------------------------

## 7. Save dan Autosave

### 7.1 Persyaratan

-   Game menyimpan progres secara otomatis setelah peristiwa penting,
    seperti clue ditemukan, puzzle selesai, quest berubah status, atau
    area baru dibuka.
-   Game menyediakan pilihan **Continue** bila save valid tersedia.
-   Game menyediakan **New Game** dan konfirmasi sebelum menimpa progres
    yang ada.
-   Game menangani save yang rusak atau tidak lengkap dengan pesan yang
    jelas dan tidak langsung crash.
-   Lokasi file save harus ditentukan secara konsisten dan dijelaskan di
    README.

### 7.2 Data minimum yang disimpan

-   Versi skema save.
-   Area dan posisi pemain terakhir yang aman.
-   Clue yang telah ditemukan.
-   Status quest.
-   Status puzzle.
-   Flag dialog atau peristiwa penting.
-   Pengaturan yang memang perlu dipertahankan.

Gunakan format data yang dapat dibaca dan diproses dengan aman oleh
aplikasi. Jangan menyimpan password atau rahasia yang tidak diperlukan.

### 7.3 Kriteria penerimaan save

1.  Setelah menemukan clue lalu menutup dan membuka game, clue tetap
    tercatat.
2.  Quest dan puzzle yang sudah selesai tidak kembali ke status awal.
3.  Save yang tidak ada menghasilkan alur New Game yang normal.
4.  Save yang tidak valid menghasilkan penanganan error yang jelas.
5.  Struktur save memiliki versi untuk mendukung perubahan skema.

------------------------------------------------------------------------

## 8. Visual dan Art Direction

### 8.1 Gaya visual

-   Pixel art klasik dengan atmosfer gothic.
-   Palet dominan: abu-abu arang, biru malam, ungu gelap, cokelat kusam,
    dan aksen cahaya hangat.
-   Siluet karakter harus terbaca pada ukuran sprite kecil.
-   Efek kabut, cahaya, dan partikel dipakai secukupnya agar tidak
    mengganggu keterbacaan puzzle.
-   UI harus memiliki kontras yang cukup untuk teks dan clue.

### 8.2 Ukuran dan rendering awal

-   Target internal resolution: **320 × 180** sebagai titik awal yang
    dapat diubah bila hasil uji menunjukkan kebutuhan lain.
-   Ukuran tile yang disarankan: **16 × 16 piksel**.
-   Gunakan skala integer untuk memperbesar tampilan tanpa blur jika
    memungkinkan.
-   Pertahankan tampilan nearest-neighbor dan hindari smoothing yang
    membuat pixel art kabur.
-   Pastikan game tetap dapat dimainkan pada ukuran jendela yang
    didukung.

### 8.3 Kebutuhan aset

Kelompok aset yang dibutuhkan:

-   Sprite pemain: idle dan berjalan, minimal empat arah jika desain map
    memerlukannya.
-   NPC dan portrait dialog.
-   Tileset desa, manor, hutan, chapel, arsip, dan sanctuary.
-   Objek interaktif: pintu, peti, buku, catatan, simbol, meja, lampu,
    dan objek investigasi.
-   Elemen UI: panel dialog, ikon clue, casebook, tombol, indikator
    quest, dan layar puzzle.
-   Efek sederhana: kabut, cahaya, partikel, atau transisi.
-   Audio opsional: ambience, langkah, interaksi, dan musik latar.

Jangan menganggap seluruh aset harus tersedia sejak tahap pertama.
Gunakan placeholder konsisten sampai aset final terpilih.

------------------------------------------------------------------------

## 9. Referensi Aset dan Lisensi

Sumber berikut adalah kandidat untuk ditinjau, bukan ketergantungan
wajib. Periksa lisensi dan ketentuan distribusi pada halaman resmi
sebelum memasukkan aset ke rilis game.

-   **Kenney --- Roguelike/RPG Pack:**
    https://kenney.nl/assets/roguelike-rpg-pack
-   **Kenney --- Tiny Dungeon:**
    https://kenney-assets.itch.io/tiny-dungeon
-   **Kaishido --- Dark World:** https://kaishido.itch.io/dark-world
-   **BTL Games --- 8-BIT HORROR TILES & SPRITES:**
    https://btl-games.itch.io/8bit-horror-tiles-sprites

### Aturan penggunaan aset

1.  Verifikasi lisensi terbaru di halaman sumber dan di file lisensi
    yang disertakan.
2.  Jangan menghapus atribusi yang diwajibkan.
3.  Catat nama aset, pembuat, URL sumber, lisensi, tanggal pemeriksaan,
    dan perubahan yang dilakukan.
4.  Jangan mengklaim aset pihak lain sebagai karya original.
5.  Jangan mengunduh atau mengemas ulang aset yang lisensinya belum
    dipastikan.
6.  Bila lisensi belum jelas, gunakan placeholder buatan sendiri sampai
    statusnya terverifikasi.

Sediakan file `data/licenses/ASSET_CREDITS.md` untuk mencatat seluruh
aset eksternal yang digunakan.

------------------------------------------------------------------------

## 10. Teknologi dan Batas Implementasi

### 10.1 Teknologi

-   Processing 4, Java Mode.
-   Bahasa pemrograman Java yang kompatibel dengan lingkungan
    Processing.
-   Aset lokal di folder `data/`.
-   File konten terpisah untuk dialog, clue, puzzle, quest, dan area
    bila pendekatan tersebut tetap sederhana dan stabil.

### 10.2 Batasan teknis

-   Jangan menambahkan framework besar tanpa alasan yang jelas.
-   Jangan mengasumsikan API, library, atau file tersedia sebelum
    memeriksa proyek.
-   Hindari satu file besar yang menampung seluruh logika game.
-   Jangan mengimplementasikan semua area dan konten sekaligus.
-   Jangan membuat fitur online, multiplayer, akun, atau server untuk
    versi awal.
-   Jangan mengandalkan internet saat game sedang berjalan.
-   Jika format JSON memerlukan library tambahan, periksa dukungan
    lingkungan Processing yang dipakai sebelum memilih implementasi.
    Dokumentasikan dependensi tambahan secara eksplisit.

### 10.3 Struktur proyek yang disarankan

``` text
SilentWitch/
├── SilentWitch.pde
├── src/
│   ├── GameManager.java
│   ├── SceneManager.java
│   ├── Player.java
│   ├── WorldManager.java
│   ├── Interactable.java
│   ├── DialogueSystem.java
│   ├── InvestigationSystem.java
│   ├── PuzzleSystem.java
│   ├── QuestSystem.java
│   ├── SaveManager.java
│   └── UIManager.java
├── data/
│   ├── assets/
│   │   ├── characters/
│   │   │   ├── player/
│   │   │   └── npc/
│   │   ├── tilesets/
│   │   ├── maps/
│   │   ├── objects/
│   │   ├── ui/
│   │   ├── portraits/
│   │   └── effects/
│   ├── content/
│   │   ├── dialogue.json
│   │   ├── clues.json
│   │   ├── puzzles.json
│   │   ├── quests.json
│   │   └── areas.json
│   └── licenses/
│       └── ASSET_CREDITS.md
├── README.md
└── CHANGELOG.md
```

Struktur ini adalah target organisasi, bukan bukti bahwa seluruh file
sudah dibuat. AI coding assistant harus memeriksa proyek nyata dan
membuat file secara bertahap. Jika Processing setup yang digunakan
memiliki batasan pada folder `src/`, pilih struktur yang kompatibel dan
jelaskan perubahannya.

### 10.4 Tanggung jawab modul

-   **GameManager:** mengatur state global dan siklus game.
-   **SceneManager:** mengelola perpindahan menu, gameplay, dialog,
    puzzle, dan ending.
-   **Player:** input, gerakan, dan interaksi dasar.
-   **WorldManager:** area, tile, collision, dan perpindahan antar-area.
-   **Interactable:** kontrak umum untuk objek yang dapat diperiksa.
-   **DialogueSystem:** dialog dan pilihan percakapan.
-   **InvestigationSystem:** clue, hubungan bukti, dan casebook.
-   **PuzzleSystem:** pemuatan, validasi, hint, dan hasil puzzle.
-   **QuestSystem:** prasyarat, tujuan, dan status quest.
-   **SaveManager:** autosave, load, validasi, dan versi save.
-   **UIManager:** menu, dialog, casebook, indikator quest, dan
    antarmuka puzzle.

Modul tambahan hanya dibuat jika benar-benar diperlukan. Jangan membuat
class kosong sekadar mengikuti daftar.

------------------------------------------------------------------------

## 11. UI dan UX

### 11.1 Layar minimum

1.  **Main Menu:** New Game, Continue (jika save tersedia), Settings
    sederhana, dan Quit.
2.  **Gameplay HUD:** petunjuk interaksi yang kontekstual dan indikator
    quest yang tidak menghalangi area.
3.  **Dialogue Panel:** nama karakter jika relevan, teks, dan pilihan
    respons.
4.  **Detective Casebook:** daftar clue dan ringkasan investigasi.
5.  **Puzzle Screen:** instruksi, elemen puzzle, input/jawaban, hint,
    dan feedback.
6.  **Pause Menu:** Resume, Settings sederhana, dan kembali ke menu
    dengan konfirmasi.
7.  **Ending Screen:** hasil investigasi dan ringkasan bukti yang
    mendukungnya.

### 11.2 Prinsip UX

-   Semua teks utama berbahasa Indonesia.
-   Pemain harus tahu tombol untuk melanjutkan dialog dan menutup panel.
-   Teks puzzle harus terbaca dan tidak terpotong.
-   Interaksi yang gagal harus memberi feedback yang jelas.
-   Jangan mengandalkan warna saja untuk menyampaikan status clue atau
    puzzle.
-   Simpan status penting sebelum perpindahan scene yang berisiko
    menghilangkan progres.

------------------------------------------------------------------------

## 12. Kebutuhan Fungsional

-   **FR-01 --- Movement:** pemain dapat bergerak di area yang tersedia.
-   **FR-02 --- Collision:** pemain tidak dapat melewati penghalang yang
    ditetapkan.
-   **FR-03 --- Interaction:** pemain dapat memeriksa objek dan
    berbicara dengan NPC dalam jangkauan.
-   **FR-04 --- Dialogue:** dialog ditampilkan dan dapat dilanjutkan;
    pilihan hanya muncul jika ditentukan oleh konten.
-   **FR-05 --- Clue Collection:** clue baru tercatat satu kali dan
    memperbarui casebook.
-   **FR-06 --- Investigation:** casebook menampilkan clue yang telah
    ditemukan dan informasi yang memang sudah diketahui.
-   **FR-07 --- Puzzle:** puzzle memvalidasi jawaban dan mencatat
    penyelesaian.
-   **FR-08 --- Quest:** quest berubah status sesuai prasyarat dan
    tujuan.
-   **FR-09 --- Area Unlock:** area terkunci terbuka berdasarkan kondisi
    yang eksplisit.
-   **FR-10 --- Autosave:** peristiwa progres penting disimpan secara
    otomatis.
-   **FR-11 --- Load:** pemain dapat melanjutkan dari save valid.
-   **FR-12 --- Ending:** hasil akhir ditentukan oleh syarat investigasi
    yang dapat dijelaskan.
-   **FR-13 --- Error Handling:** aset atau data yang tidak ditemukan
    ditangani dengan pesan/log yang membantu, bukan crash tanpa
    penjelasan.
-   **FR-14 --- Localization:** semua teks konten game awal menggunakan
    Bahasa Indonesia.

------------------------------------------------------------------------

## 13. Kebutuhan Nonfungsional

-   **NFR-01 --- Stabilitas:** alur dasar dari menu hingga gameplay
    dapat dijalankan tanpa exception yang tidak tertangani.
-   **NFR-02 --- Konsistensi:** ID konten dan flag progres stabil serta
    tidak saling bertentangan.
-   **NFR-03 --- Keterbacaan:** teks UI dan puzzle terbaca pada ukuran
    jendela target.
-   **NFR-04 --- Maintainability:** logika inti dipisah berdasarkan
    tanggung jawab, tanpa abstraksi berlebihan.
-   **NFR-05 --- Portabilitas aset:** aset lokal dimuat melalui path
    yang terdokumentasi dan konsisten.
-   **NFR-06 --- Save safety:** kegagalan membaca save tidak boleh
    diam-diam menghapus file save asli.
-   **NFR-07 --- Reproducibility:** puzzle dan hasil investigasi inti
    tidak bergantung pada randomness yang tidak tercatat.
-   **NFR-08 --- Truthful reporting:** laporan implementasi hanya
    menyebut fitur yang benar-benar dibuat dan diuji.

------------------------------------------------------------------------

## 14. Acceptance Criteria

### AC-01 --- Build dan startup

-   Proyek dapat dijalankan di Processing 4 Java Mode yang ditetapkan.
-   Main Menu muncul tanpa error fatal.
-   README menjelaskan cara membuka dan menjalankan proyek.

### AC-02 --- Movement dan collision

-   Pemain bergerak dengan kontrol yang ditentukan.
-   Pemain tidak menembus dinding atau objek solid.
-   Input tidak menyebabkan gerakan berulang yang tidak terkendali saat
    menu/dialog terbuka.

### AC-03 --- Interaksi

-   Objek yang dapat diperiksa memberi feedback.
-   NPC yang dapat diajak bicara membuka dialog.
-   Objek yang tidak interaktif tidak memicu dialog atau clue palsu.

### AC-04 --- Clue dan casebook

-   Clue yang ditemukan tercatat dan terlihat di casebook.
-   Clue yang belum ditemukan tidak dianggap sebagai pengetahuan pemain.
-   Menginteraksikan clue yang sama tidak menggandakan catatan secara
    tidak sengaja.

### AC-05 --- Puzzle

-   Setiap puzzle memiliki aturan, jawaban, validasi, dan feedback.
-   Jawaban salah tidak merusak progres.
-   Jawaban benar menyimpan status penyelesaian.
-   Hint tidak mengubah jawaban atau fakta kasus secara diam-diam.

### AC-06 --- Quest dan unlock

-   Quest hanya selesai jika syaratnya terpenuhi.
-   Area yang terkunci tetap terkunci sebelum prasyarat dipenuhi.
-   Setelah syarat dipenuhi, akses berubah sesuai rancangan.

### AC-07 --- Autosave dan load

-   Progres clue, quest, dan puzzle tetap ada setelah game dimulai
    ulang.
-   Continue hanya tersedia jika save dapat digunakan.
-   Save rusak ditangani dengan pesan yang jelas dan tanpa menimpa file
    asli secara otomatis.

### AC-08 --- Narasi dan logika

-   Dialog, clue, puzzle, dan ending tidak saling bertentangan.
-   Semua kesimpulan utama memiliki dukungan bukti yang dapat diperiksa.
-   Tidak ada identitas pelaku atau motif yang dibuat tanpa dasar
    dokumen kanon.

### AC-09 --- UI

-   Teks dialog dan puzzle tidak terpotong pada ukuran target.
-   Pemain mengetahui cara berinteraksi dan menutup panel.
-   Menu jeda tidak menghapus progres.

### AC-10 --- Dokumentasi dan lisensi

-   README memuat persyaratan, kontrol, dan cara menjalankan.
-   Semua aset eksternal yang benar-benar dipakai tercatat di
    `ASSET_CREDITS.md`.
-   Lisensi aset diperiksa sebelum distribusi.

------------------------------------------------------------------------

## 15. Scope

### 15.1 Wajib untuk versi awal (P0)

-   Proyek Processing 4 yang dapat dijalankan.
-   Main Menu dan satu scene gameplay.
-   Gerakan pemain, collision dasar, dan interaksi.
-   Satu area kecil yang dapat dijelajahi.
-   Satu NPC dengan dialog.
-   Sistem clue sederhana dan casebook dasar.
-   Satu puzzle logika dengan jawaban dan feedback.
-   Autosave/load dasar.
-   README dengan instruksi menjalankan proyek.

### 15.2 Berikutnya (P1)

-   Sistem quest yang lebih lengkap.
-   Beberapa area yang terhubung.
-   Beragam jenis puzzle.
-   Kronologi kasus dan hubungan clue.
-   Beberapa NPC dan dialog bercabang.
-   Beberapa ending berdasarkan deduksi.
-   UI dan efek visual yang lebih matang.

### 15.3 Opsional (P2)

-   Musik dan sound effect lengkap.
-   Animasi lingkungan tambahan.
-   Pengaturan aksesibilitas lanjutan.
-   Replay case atau New Game+.
-   Sistem achievement lokal.

### 15.4 Di luar scope

-   Multiplayer atau co-op.
-   Akun online dan backend server.
-   Pembelian dalam aplikasi atau iklan.
-   Dunia open-world berskala besar.
-   Generasi kasus acak yang mengubah fakta kanon.
-   Voice acting penuh.
-   Sistem combat kompleks kecuali ditambahkan melalui revisi PRD yang
    eksplisit.

------------------------------------------------------------------------

## 16. Roadmap Implementasi Bertahap

AI coding assistant harus mengerjakan satu tahap pada satu waktu.
Setelah setiap tahap, laporkan file yang diubah, cara menjalankan,
pengujian yang benar-benar dilakukan, hasilnya, dan masalah yang masih
ada.

### Stage 01 --- Audit dan fondasi

-   Periksa struktur proyek yang sudah ada sebelum mengubahnya.
-   Pastikan proyek minimal berjalan di Processing 4 Java Mode.
-   Buat game loop dan scene manager dasar.
-   Buat Main Menu sederhana dan scene gameplay placeholder.
-   Tetapkan ukuran render pixel-art.
-   Jangan mulai menulis seluruh cerita atau semua area.

**Syarat selesai:** proyek dapat dijalankan, menu tampil, dan
perpindahan ke scene placeholder berfungsi.

### Stage 02 --- Player dan world

-   Implementasikan gerakan pemain.
-   Tambahkan collision dan satu area kecil.
-   Tambahkan placeholder tileset jika aset final belum tersedia.
-   Uji batas area dan input saat scene tidak aktif.

**Syarat selesai:** pemain dapat bergerak dan tidak menembus penghalang
utama.

### Stage 03 --- Interaksi dan dialog

-   Implementasikan objek interaktif dan NPC.
-   Buat dialog sederhana.
-   Pastikan input dialog tidak menggerakkan pemain di belakang panel.

**Syarat selesai:** interaksi dan dialog berfungsi tanpa mengacaukan
input gameplay.

### Stage 04 --- Clue dan casebook

-   Tambahkan model data clue dengan ID stabil.
-   Implementasikan pengumpulan clue.
-   Tampilkan clue yang ditemukan dalam casebook.

**Syarat selesai:** clue tercatat satu kali dan tetap konsisten di UI.

### Stage 05 --- Puzzle dan quest

-   Implementasikan satu puzzle end-to-end.
-   Tambahkan validasi jawaban dan hint.
-   Hubungkan hasil puzzle dengan status quest atau unlock sederhana.

**Syarat selesai:** puzzle dapat diselesaikan, diuji untuk jawaban
salah/benar, dan progresnya tercatat.

### Stage 06 --- Save/load dan integrasi

-   Tambahkan autosave dan load.
-   Uji save baru, save valid, dan penanganan save rusak.
-   Pastikan progres tidak hilang saat berpindah scene.

**Syarat selesai:** data progres dasar dapat dipulihkan setelah restart.

### Stage 07 --- Konten, polish, dan validasi

-   Masukkan area dan konten cerita setelah dokumen kanon ditetapkan.
-   Tambahkan aset final dengan lisensi yang diperiksa.
-   Lengkapi ending berdasarkan aturan deduksi.
-   Uji konsistensi seluruh clue, dialog, quest, puzzle, dan ending.

**Syarat selesai:** konten yang sudah diimplementasikan dapat dimainkan
dari awal hingga akhir tanpa kontradiksi yang diketahui.

------------------------------------------------------------------------

## 17. Aturan Kerja untuk AI Coding Assistant

1.  **Audit sebelum edit.** Baca struktur file dan kode yang ada sebelum
    membuat, memindahkan, atau menghapus file.
2.  **Satu tahap per giliran.** Jangan melompat ke tahap berikutnya
    sebelum tahap saat ini selesai dan dilaporkan.
3.  **Jangan mengarang aset.** Periksa file aset yang benar-benar
    tersedia. Jika tidak ada, gunakan placeholder yang dibuat secara
    eksplisit.
4.  **Jangan mengarang API atau dependency.** Pastikan library dan API
    cocok dengan Processing 4 Java Mode yang digunakan.
5.  **Jangan mengarang fakta cerita.** Pelaku, motif, kronologi final,
    dan identitas karakter hanya boleh ditetapkan lewat dokumen kanon.
6.  **Jaga perubahan tetap fokus.** Jangan melakukan refactor besar yang
    tidak diperlukan untuk tahap saat ini.
7.  **Laporkan hasil secara jujur.** Bedakan antara kode yang ditulis,
    kode yang dijalankan, dan pengujian yang benar-benar lolos.
8.  **Jangan menyatakan build berhasil tanpa menjalankannya.** Jika
    lingkungan tidak memungkinkan pengujian, katakan secara eksplisit.
9.  **Tangani error dengan jelas.** Berikan pesan yang membantu dan
    hindari kegagalan diam-diam.
10. **Perbarui dokumentasi.** Perubahan kontrol, dependensi, struktur
    file, atau cara menjalankan harus dicatat.
11. **Jangan menambah scope diam-diam.** Fitur baru yang memengaruhi
    cerita atau arsitektur harus diajukan sebagai perubahan PRD.
12. **Jaga pengalaman aman dan non-grafis.** Misteri boleh gelap, tetapi
    tidak memerlukan penggambaran kekerasan eksplisit.

------------------------------------------------------------------------

## 18. Prompt Awal untuk AI Coding Assistant

Gunakan prompt berikut saat memulai implementasi:

> Baca seluruh `Silent_Witch_PRD.md` terlebih dahulu. Kerjakan **Stage
> 01 --- Audit dan fondasi saja**. Periksa struktur proyek yang
> benar-benar tersedia sebelum mengedit. Implementasikan fondasi minimal
> yang kompatibel dengan Processing 4 Java Mode: game loop, scene
> manager sederhana, pixel rendering, Main Menu, dan scene gameplay
> placeholder. Jangan mengimplementasikan stage berikutnya, jangan
> mengarang aset atau API, dan jangan menambahkan cerita final yang
> belum ditetapkan. Setelah selesai, laporkan file yang dibuat/diubah,
> cara menjalankan proyek, pengujian yang benar-benar dilakukan,
> hasilnya, dan keterbatasan yang masih ada. Jangan mengklaim pengujian
> berhasil jika tidak dijalankan.

------------------------------------------------------------------------

## 19. Definition of Done

Sebuah tahap dianggap selesai hanya jika:

-   Implementasi memenuhi kriteria tahap yang bersangkutan.
-   Perubahan dapat dijelaskan dan tidak menambahkan scope tak
    disetujui.
-   Jalur utama yang relevan telah diuji, atau keterbatasan pengujian
    dilaporkan.
-   Tidak ada error baru yang diketahui dan diabaikan.
-   Dokumentasi terkait diperbarui.
-   Status setiap acceptance criterion yang relevan dicatat sebagai
    **Pass**, **Fail**, atau **Not Tested**.

Untuk rilis game penuh, seluruh alur dari awal sampai ending harus dapat
dimainkan, save/load berfungsi, puzzle dapat diselesaikan, dan hubungan
antara bukti dengan kesimpulan akhir dapat dijelaskan.

------------------------------------------------------------------------

## 20. Pertanyaan Desain yang Masih Harus Diputuskan

Hal-hal berikut belum boleh diisi dengan tebakan oleh implementasi:

1.  Nama korban dan latar belakangnya.
2.  Identitas pelaku sebenarnya dan motif final.
3.  Kronologi kejadian yang kanonis.
4.  Hubungan spesifik setiap saksi dengan korban dan kultus.
5.  Puzzle final dan bukti kunci yang membedakan True Ending dari ending
    lain.
6.  Daftar aset final dan lisensi yang sudah diverifikasi.
7.  Apakah combat diperlukan; rekomendasi versi awal: **tidak perlu**,
    prioritaskan investigasi dan puzzle.

Keputusan ini harus ditulis dan disetujui sebelum konten cerita lengkap
diimplementasikan.

------------------------------------------------------------------------

**Akhir dokumen --- SILENT WITCH PRD v2.0**
