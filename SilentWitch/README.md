# Silent Witch

Game pixel-art mystery RPG untuk Windows PC, dibangun dengan **Processing 4 (Java Mode)**.
Dokumen kebutuhan lengkap ada di [`../Silent_Witch_PRD.md`](../Silent_Witch_PRD.md).

**Status saat ini: Stage 03 - Interaksi dan dialog.**
Pemain dapat bergerak, bertabrakan dengan dunia dan NPC, bicara dengan NPC, serta
memeriksa objek. Dialog tampil pada panel bawah dan mengunci gerakan pemain selama
terbuka. Belum ada clue, casebook, quest, puzzle, atau save. Lihat
[Scope Stage 03](#scope-stage-03) dan [Status acceptance criteria](#status-acceptance-criteria).

---

## Persyaratan

- Windows 10/11.
- Processing 4, Java Mode.
- Versi yang dipakai untuk pengujian: **Processing 4.5.7** (portable, Java Mode).
- Tidak ada library, dependency, aset eksternal, atau koneksi internet yang dibutuhkan saat bermain.

## Menjalankan

### Dari Processing IDE

1. Buka `SilentWitch/SilentWitch.pde` di Processing 4.
2. Pastikan mode berada di **Java**.
3. Tekan tombol **Run**.

### Dari command line

Jika Processing 4 terpasang dan `processing-java`/CLI tersedia:

```powershell
processing-java --sketch="C:\path\ke\SilentWitch" --run
```

Pada Processing 4.5.7 portable, perintah CLI dapat dipanggil lewat executable:

```powershell
.\Processing.exe cli --sketch="C:\path\ke\SilentWitch" --run
```

Untuk hanya memeriksa kompilasi:

```powershell
.\Processing.exe cli --sketch="C:\path\ke\SilentWitch" --build
```

## Kontrol

| Konteks | Tombol | Fungsi |
|---|---|---|
| Menu | `W` / `S` atau panah atas/bawah | Pilih item |
| Menu | `Enter` / `Spasi` / klik mouse | Konfirmasi item |
| Menu | `Esc` | Menutup panel Kontrol atau membatalkan pesan |
| Gameplay | `W` / `A` / `S` / `D` atau tombol panah | Bergerak empat arah |
| Gameplay | `E` / `Enter` / `Spasi` | Bicara dengan NPC atau periksa objek terdekat |
| Gameplay | `Esc` | Kembali ke menu utama |
| Dialog terbuka | `E` / `Enter` / `Spasi` | Lanjut ke baris berikutnya; menutup pada baris terakhir |
| Dialog terbuka | `Esc` | Menutup dialog tanpa keluar ke menu |
| Dialog terbuka | Tombol gerak | Diabaikan; pemain tidak bergerak di belakang panel |
| Gameplay | Klik mouse | Belum ada aksi mouse untuk gameplay |

`Esc` tidak menutup aplikasi. Keluar dilakukan melalui item **KELUAR** di menu utama.

## Struktur proyek

```text
SilentWitch/
├── SilentWitch.pde      # entry point Processing (settings/setup/draw/input)
├── GameManager.java     # render buffer 320x180, loop, viewport, input
├── SceneManager.java    # scene aktif dan siklus hidup
├── Scene.java           # kontrak dasar scene
├── MainMenuScene.java   # menu utama
├── GameplayScene.java   # gameplay: peta, kamera, input, NPC, dialog, HUD
├── TileMap.java         # data tile 26x16, collision, render placeholder
├── Player.java          # gerakan, collision tile + NPC, sprite prosedural
├── Interactable.java    # kontrak objek interaktif + pemilihan target terdekat
├── Npc.java             # NPC penjaga, sprite, radius bicara, penghalang
├── ExamineSpot.java     # titik periksa untuk objek yang digambar TileMap
├── Dialogue.java        # model data dialog (pembicara + baris)
├── DialogueSystem.java  # state dialog dan panel bawah
├── PixelFont.java       # font bitmap 5x7 bawaan
├── PixelUI.java         # panel, tombol, divider, vignette
├── Palette.java         # palet warna dan util warna
├── README.md
├── CHANGELOG.md
└── data/
    └── licenses/
        └── ASSET_CREDITS.md
```

### Penyimpangan dari struktur PRD

- PRD menyarankan folder `src/` untuk kelas Java. Processing hanya mengompilasi tab
  `.java` yang berada di **root folder sketch**, sehingga seluruh kelas proyek
  diletakkan di root `SilentWitch/` dan bukan di `src/`. Struktur ini dipilih agar
  proyek dapat langsung dibuka dan dijalankan di Processing 4 tanpa build tool tambahan.
- PRD menyarankan `WorldManager`. Untuk satu area statis, tanggung jawab tersebut masih
  kecil sehingga dipecah menjadi `TileMap` (data tile, collision, render),
  `Player` (gerakan), dan daftar `Interactable` di `GameplayScene`. `WorldManager`
  akan dibuat saat sudah ada lebih dari satu area dan perpindahan antar-area
  (Stage 05-06), agar tidak ada kelas kosong.
- Tidak ada kelas kosong yang dibuat hanya untuk mengikuti daftar modul PRD.

## Rendering

- Resolusi internal: **320 x 180** piksel.
- Jendela: **960 x 540** (skala integer 3x).
- Semua scene menggambar ke `PGraphics` 320x180, lalu hasilnya diperbesar ke jendela.
- `noSmooth()` dipanggil di `settings()` agar penskalaan memakai nearest-neighbor dan
  pixel art tidak kabur.
- Teks memakai font bitmap 5x7 buatan sendiri (`PixelFont.java`), sehingga tidak
  bergantung pada font sistem atau file aset. Huruf kecil ditampilkan sebagai huruf kapital.
- Tile placeholder 16x16, sprite pemain, dan sprite NPC digambar prosedural; tidak ada
  file gambar.

## Area uji

Area: **The Silent Manor - Aula Uji**, grid 26x16 tile (416x256 piksel). Viewport
320x180 lebih kecil dari peta, sehingga kamera mengikuti pemain dan berhenti di tepi peta.

Legenda tile pada `TileMap.java`:

| Karakter | Isi | Solid |
|---|---|---|
| `#` | Dinding batu | Ya |
| `W` | Dinding dengan jendela | Ya |
| `.` | Lantai kayu | Tidak |
| `,` | Karpet | Tidak |
| `B` | Rak buku | Ya |
| `T` | Meja | Ya |
| `P` | Pilar | Ya |
| `S` | Titik spawn pemain | Tidak |

Peta divalidasi saat dibuat: baris harus sama panjang, harus ada tepat satu spawn, dan
spawn tidak boleh berada di tile solid. Kesalahan data menghasilkan
`IllegalArgumentException` dengan pesan yang jelas, bukan crash tanpa penjelasan.

## Scope Stage 03

Sudah ada:

- Seluruh fondasi Stage 01-02 tetap berfungsi: menu, game loop, render 320x180,
  scene manager, area uji, gerakan, collision, dan kamera.
- `Interactable` sebagai kontrak objek interaktif: posisi, radius, prompt, dialog, dan
  render opsional. `Interactable.nearest()` memilih objek terdekat yang berada dalam
  jangkauan.
- `Npc` dengan sprite penjaga prosedural, radius bicara 22 px, prompt **BICARA**, dan
  tubuh yang menjadi penghalang agar pemain tidak berjalan menembusnya.
- `ExamineSpot` untuk objek yang sudah digambar `TileMap` (meja, rak buku, jendela)
  dengan prompt **PERIKSA**.
- Prompt `[E] ...` muncul di atas objek terdekat dan mengikuti kamera.
- `Dialogue` sebagai model data tervalidasi (pembicara dan baris tidak boleh kosong).
- `DialogueSystem` menampilkan panel bawah berisi nama pembicara, nomor baris, teks
  terbungkus, dan indikator lanjut/tutup.
- Interaksi dengan `E`, `Enter`, atau `Spasi`; dialog maju per baris dan menutup pada
  baris terakhir.
- `Esc` menutup dialog tanpa keluar ke menu; `Esc` di gameplay biasa tetap kembali ke menu.
- Input gerak dikunci selama dialog terbuka dan tombol gerak yang sedang ditekan
  dilepas saat dialog dibuka, sehingga pemain tidak meluncur di belakang panel.
- Cooldown input 0,18 detik untuk mencegah dialog terbuka kembali atau maju beberapa
  baris dalam satu tekanan tombol.
- NPC dan pemain digambar dengan urutan kedalaman berdasarkan posisi `y`.
- HUD berubah saat dialog terbuka: petunjuk menjadi `E / ENTER : LANJUT` dan
  `ESC : TUTUP`.
- Dialog tetap di dalam area layar 320x180 dan tidak menutupi bar petunjuk bawah.
- Dokumentasi, changelog, dan kredit aset diperbarui.

Belum ada (sesuai tahap berikutnya):

- Clue, bukti, dan casebook (Stage 04).
- Puzzle logika, quest, dan area unlock (Stage 05).
- Save/autosave/load dan tombol **Lanjutkan** yang aktif (Stage 06).
- Konten cerita kanonis, ending, dan aset final (Stage 07).

### Catatan konten dialog

Seluruh teks dialog pada Stage 03 adalah **placeholder teknis**. Tidak ada fakta cerita
kanonis yang ditambahkan: belum ada identitas korban, pelaku, motif, atau kronologi.
Teks hanya menjelaskan fungsi teknis (objek dapat diperiksa, cerita menyusul) agar
sistem dialog dapat diuji tanpa mengarang kanon.

## Save

Belum ada sistem save pada Stage 03. Karena itu:

- Item **Lanjutkan** selalu nonaktif dan diberi label `TERKUNCI`.
- Memilih item tersebut menampilkan pesan bahwa simpan otomatis baru akan
  ditambahkan pada Stage 06.
- Belum ada file save yang dibuat atau dibaca. Lokasi file save akan ditetapkan
  dan didokumentasikan saat Stage 06 diimplementasikan.

## Pengujian yang dilakukan

Tanggal pengujian: **31 Oktober 2026**. Lingkungan: Windows, Processing 4.5.7 portable
(Java Mode), JDK 8 (Temurin 1.8.0_504) untuk uji headless dan build.

| Pengujian | Metode | Hasil |
|---|---|---|
| Kompilasi kelas Java | `javac -Xlint:all` terhadap Processing core 3.3.7 | **Lulus**, tanpa error/warning |
| Build Processing | `Processing.exe cli --sketch=... --build` | **Lulus**, exit code 0, `Finished.` |
| Validasi `Dialogue` | Harness headless | **Lulus**, pembicara kosong, tanpa baris, baris kosong/null, dan indeks di luar batas ditolak; array masukan disalin |
| Transisi `DialogueSystem` | Harness headless | **Lulus**, buka, maju, baris terakhir, tutup, reset indeks, dan `open(null)` ditolak |
| `Interactable.nearest` | Harness headless | **Lulus**, daftar null/kosong aman, objek di luar radius diabaikan, target terdekat dipilih, batas radius tepat diterima |
| Validasi `Npc` dan `ExamineSpot` | Harness headless | **Lulus**, nama/label/radius/dialog divalidasi; prompt dan posisi benar |
| Penghalang NPC pada `Player` | Harness headless | **Lulus**, pemain berhenti di bawah NPC (y=227,5) dan tetap bisa lewat tanpa penghalang |
| Seluruh uji headless Stage 03 | `java Stage03Test` | **Lulus**, 59 dari 59 pemeriksaan |
| Uji regresi Stage 02 | `java Stage02Test` | **Lulus**, 36 dari 36 pemeriksaan (gerakan, collision, layout) |
| Startup aplikasi | `Processing.exe cli --sketch=... --run` | **Lulus**, jendela `Silent Witch - Tahap 03` muncul dan render loop berjalan |
| Prompt NPC | Harness in-app `GameManager` + `save()` | **Lulus**, prompt `[E] BICARA` tampil setelah pemain mendekat |
| Dialog NPC | Harness in-app | **Lulus**, panel menampilkan `PENJAGA AULA`, nomor `1/3`, dan baris berganti saat `E` ditekan |
| Kunci gerak saat dialog | Tahan `A` 0,83 detik saat dialog terbuka | **Lulus**, posisi pemain tetap `x=219,13 y=212,87` sebelum dan sesudah; dialog tetap terbuka |
| Penutupan dialog | `E` pada baris terakhir dan `Esc` | **Lulus**, dialog tertutup dan prompt kembali tampil |
| Prompt objek | Jalan ke meja | **Lulus**, prompt `[E] PERIKSA` tampil pada jarak interaksi |
| Dialog objek | `E` di dekat meja | **Lulus**, panel `MEJA AULA` menampilkan baris objek |
| `Esc` menutup dialog | Tekan `Esc` saat dialog objek | **Lulus**, dialog tertutup, tetap di gameplay (tidak kembali ke menu) |
| Render akhir Stage 03 | Sepuluh frame 320x180 dari `GameManager` yang sama | **Lulus**, menu, gameplay, prompt NPC, tiga baris dialog, prompt meja, dialog objek, dan keadaan setelah `Esc` tampil benar |
| Input keyboard fisik | Perangkat fisik | **Tidak teruji**: harness memanggil `GameManager.keyPressed/keyReleased` langsung, bukan keyboard fisik |

Harness uji, harness tampilan, dan skrip tangkapan berada di folder sementara di luar
proyek dan **tidak** disertakan sebagai bagian game. Tidak ada klaim pengujian dengan
keyboard fisik. Frame verifikasi dihasilkan oleh `GameManager` yang sama dengan build
final (skala internal 320x180), bukan mockup terpisah.

## Status acceptance criteria

Status berikut menilai kriteria yang relevan sampai Stage 03.

| Kriteria | Status | Catatan |
|---|---|---|
| AC-01 Build dan startup | **Pass** | Build Processing exit code 0; menu dan gameplay tampil |
| AC-02 Movement dan collision | **Pass** | Gerakan, batas peta, collision tile, dan penghalang NPC diuji headless |
| AC-03 Interaksi | **Pass** | NPC dan tiga objek dapat diinteraksikan; prompt, dialog, kunci gerak, dan penutupan diuji |
| AC-04 Clue dan casebook | **Not Tested** | Dijadwalkan Stage 04 |
| AC-05 Puzzle | **Not Tested** | Dijadwalkan Stage 05 |
| AC-06 Quest dan unlock | **Not Tested** | Dijadwalkan Stage 05-06 |
| AC-07 Autosave dan load | **Not Tested** | Dijadwalkan Stage 06; **Lanjutkan** sengaja nonaktif |
| AC-08 Narasi dan logika | **N/A** | Dialog masih placeholder; belum ada konten cerita final |
| AC-09 UI | **Partial** | Prompt dan panel dialog tampil tanpa terpotong; casebook dan puzzle belum ada |
| AC-10 Dokumentasi dan lisensi | **Pass** | README, changelog, dan `ASSET_CREDITS.md` diperbarui; tidak ada aset eksternal |

## Lisensi aset

Stage 03 tidak memakai aset eksternal. Seluruh visual (tileset placeholder, sprite
pemain, sprite NPC, panel dialog, dan font 5x7) dibuat secara prosedural di kode.
Lihat [`data/licenses/ASSET_CREDITS.md`](data/licenses/ASSET_CREDITS.md).

## Keterbatasan yang diketahui

- Jendela berukuran tetap 960x540 dan belum ada mode fullscreen/resize. Layar
  harus mampu menampilkan jendela tersebut.
- Font 5x7 hanya memiliki bentuk huruf kapital; huruf kecil otomatis ditampilkan
  sebagai kapital.
- Baru ada satu area; belum ada perpindahan antar-area.
- Baru ada satu NPC dan tiga titik periksa. Dialog masih placeholder dan belum
  terhubung ke clue, quest, atau casebook.
- Sprite pemain dan NPC digambar dengan kode (belum ada spritesheet/animasi penuh).
- Belum ada audio, efek transisi antar-scene, atau dukungan mouse untuk gameplay.
- Belum ada save, sehingga **Lanjutkan** tetap terkunci.
- Pengujian input fisik tidak dapat dilakukan di sesi ini; lihat catatan pada
  bagian pengujian.

## Tahap berikutnya

Stage 04 - Clue dan casebook: objek/bukti yang dapat dikumpulkan, pencatatan bukti,
dan tampilan casebook. Jangan lanjut ke Stage 04 sebelum Stage 03 disetujui.
