# Silent Witch

Game pixel-art mystery RPG untuk Windows PC, dibangun dengan **Processing 4 (Java Mode)**.
Dokumen kebutuhan lengkap ada di [`../Silent_Witch_PRD.md`](../Silent_Witch_PRD.md).

**Status saat ini: Stage 06 - Save/Load dan Integrasi.**
Pemain dapat menjelajah aula manor, berinteraksi dengan NPC dan objek periksa, mengumpulkan
4 bukti investigasi yang saling terhubung, meninjau **Detective Casebook** modal, menjalankan
quest aktif **MISTERI AULA UTARA**, memecahkan teka-teki logika deduksi kondisi **Kotak Besi Aula**
berkombinasi 3-dial angka (`2 - 4 - 9`), membuka rintangan **Gerbang Sayap Timur**, serta
menyimpan dan memuat progres investigasi secara otomatis dan aman melalui **SaveManager**
dan **SaveData**. Tersedia pilihan **LANJUTKAN** saat save valid tersedia, penanganan save
rusak tanpa penghapusan file (NFR-06 / AC-07), serta modal konfirmasi sebelum menimpa data
lama pada pilihan **MULAI BARU** (PRD 7.1). Lihat [Scope Stage 06](#scope-stage-06)
dan [Status acceptance criteria](#status-acceptance-criteria).

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
| Gameplay | `E` / `Enter` / `Spasi` | Bicara dengan NPC, periksa objek, atau buka kotak besi |
| Gameplay | `C` / `Tab` / `J` | Buka/tutup Detective Casebook (Buku Kasus) |
| Gameplay | `Esc` | Kembali ke menu utama |
| Dialog terbuka | `E` / `Enter` / `Spasi` | Lanjut ke baris berikutnya; menutup pada baris terakhir |
| Dialog terbuka | `Esc` | Menutup dialog tanpa keluar ke menu |
| Dialog terbuka | Tombol gerak | Diabaikan; pemain tidak bergerak di belakang panel |
| Casebook terbuka | `W` / `S` atau panah atas/bawah | Pilih bukti pada daftar bukti |
| Casebook terbuka | `Q` / `E` / panah kiri/kanan / angka `1`/`2` | Beralih antar-tab (Daftar Bukti / Kronologi & Hipotesis) |
| Casebook terbuka | `Esc` / `C` / `J` | Menutup Casebook dan kembali ke gameplay |
| Casebook terbuka | Klik mouse | Memilih tab, memilih slot bukti, atau menekan tombol `[X]` |
| Casebook terbuka | Tombol gerak | Diabaikan; pemain tidak bergerak di belakang panel |
| Puzzle terbuka | `A` / `D` atau panah kiri/kanan | Pilih dial angka yang aktif (Digit I, II, atau III) |
| Puzzle terbuka | `W` / `S` atau panah atas/bawah | Putar nilai angka pada dial terpilih (0-9 dengan wrap-around) |
| Puzzle terbuka | Angka `0` s.d. `9` | Memasukkan angka secara langsung ke dial aktif |
| Puzzle terbuka | `Enter` / `Spasi` | Uji kombinasi angka (Submit validasi) |
| Puzzle terbuka | `H` / `T` | Meminta petunjuk bertingkat berikutnya (Hint 1 s.d. 3) |
| Puzzle terbuka | `Esc` | Menutup modal teka-teki dan kembali ke gameplay |
| Puzzle terbuka | Klik mouse | Memilih dial, menekan tombol `[^]`/`[v]`, tombol `[ENTER] BUKA`, `[H] PETUNJUK`, `[ESC] KEMBALI`, `[X]`, atau klik di luar panel untuk menutup |
| Puzzle terbuka | Tombol gerak | Diabaikan; pemain tidak bergerak di belakang modal |

`Esc` tidak menutup aplikasi. Keluar dilakukan melalui item **KELUAR** di menu utama.

## Struktur proyek

```text
SilentWitch/
├── SilentWitch.pde          # entry point Processing (settings/setup/draw/input)
├── GameManager.java         # render buffer 320x180, loop, viewport, input
├── SceneManager.java        # scene aktif dan siklus hidup
├── Scene.java               # kontrak dasar scene
├── MainMenuScene.java       # menu utama
├── GameplayScene.java       # gameplay: peta, kamera, NPC, bukti, casebook, quest, puzzle, gerbang
├── TileMap.java             # data tile 26x16, collision, render placeholder, tile gerbang 'G'
├── Player.java              # gerakan, collision tile + blocker dinamis, sprite prosedural
├── Interactable.java        # kontrak objek interaktif + integrasi investigasi
├── Npc.java                 # NPC penjaga, sprite, radius bicara, dialog awal & pengingat
├── ExamineSpot.java         # titik periksa objek dunia dengan integrasi clue
├── Dialogue.java            # model data dialog (pembicara + baris)
├── DialogueSystem.java      # state dialog dan panel bawah
├── Clue.java                # model data bukti tervalidasi (ID stabil, sumber, linimasa)
├── InvestigationSystem.java # manajemen bukti, casebook modal, notifikasi toast
├── QuestObjective.java      # model sub-tujuan spesifik dengan pelacakan progres bernomor
├── Quest.java               # model data quest (status, prasyarat, sub-tujuan, reward)
├── QuestSystem.java         # manajemen quest, event hooks, notifikasi progres, HUD summary
├── Puzzle.java              # model teka-teki logika deduksi kondisi, validasi, 3-tier hints
├── PuzzleSystem.java        # modal antarmuka puzzle 300x164, dial navigasi, feedback edukatif
├── KotakBesi.java           # objek interaktif peti besi aula pengunci teka-teki
├── GerbangTimur.java        # objek interaktif gerbang pembatas sayap timur aula
├── SaveData.java            # model serialisasi/deserialisasi state investigasi (JSON v1)
├── SaveManager.java         # penulisan file atomik (.tmp), validasi, penanganan save corrupt
├── PixelFont.java           # font bitmap 5x7 bawaan (dengan glyph '^')
├── PixelUI.java             # panel, tombol, divider, vignette
├── Palette.java             # palet warna dan util warna
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
- PRD menyarankan `WorldManager`. Untuk satu area statis dengan gerbang, tanggung jawab
  tersebut ditangani secara bersih oleh `TileMap` (data tile, collision, gerbang),
  `Player` (gerakan & blocker dinamis), dan daftar `Interactable` di `GameplayScene`.
  `WorldManager` akan diperluas saat perpindahan antar-scene/antar-peta diimplementasikan.
- Tidak ada kelas kosong yang dibuat hanya untuk mengikuti daftar modul PRD.

## Rendering

- Resolusi internal: **320 x 180** piksel.
- Jendela: **960 x 540** (skala integer 3x).
- Semua scene menggambar ke `PGraphics` 320x180, lalu hasilnya diperbesar ke jendela.
- `noSmooth()` dipanggil di `settings()` agar penskalaan memakai nearest-neighbor dan
  pixel art tidak kabur.
- Teks memakai font bitmap 5x7 buatan sendiri (`PixelFont.java`), sehingga tidak
  bergantung pada font sistem atau file aset. Huruf kecil ditampilkan sebagai huruf kapital.
- Tile placeholder 16x16, sprite pemain, NPC, kotak besi, dan gerbang digambar prosedural;
  tidak ada file gambar eksternal.

## Area Aula, Teka-teki, dan Quest

Area: **The Silent Manor - Aula Uji**, grid 26x16 tile (416x256 piksel). Viewport
320x180 lebih kecil dari peta, sehingga kamera mengikuti pemain dan berhenti di tepi peta.

### 1. Bukti Investigasi Aula (Stage 04)

Empat bukti investigasi aula yang terdaftar dan saling terhubung:

| ID Clue | Judul | Sumber | Linimasa | Keandalan | Objek Terkait |
|---|---|---|---|---|---|
| `clue_rak_harian` | BUKU HARIAN USANG | RAK BUKU | 3 HARI LALU | DOKUMEN | Rak buku di dinding utara-barat |
| `clue_meja_catatan` | SOBEKAN CATATAN AULA | MEJA AULA | H-1 MALAM | BUKTI FISIK | Meja kayu aula |
| `clue_penjaga_kesaksian` | KESAKSIAN PENJAGA AULA | PENJAGA AULA | 23:30 MALAM | KESAKSIAN | Penjaga malam di atas karpet |
| `clue_jendela_goresan` | BEKAS GORESAN KUSEN | JENDELA UTARA | MALAM KASUS | LINGKUNGAN | Kusen jendela utara aula |

### 2. Teka-teki Kotak Besi Aula (Stage 05)

Teka-teki logika deduksi kondisi (PRD Seksi 5.1 type 2) yang terletak di sudut timur laut aula:

- **Instruksi Plakat:**
  1. Digit I: Bilangan prima genap terkecil.
  2. Digit II: Bernilai dua kali lipat Digit I.
  3. Digit III: Berupa bilangan kuadrat dari angka 3 ($3 \times 3$).
  4. Total ketiga digit tepat sama dengan 15.
- **Kombinasi Solusi:** `2 - 4 - 9` ($2 + 4 + 9 = 15$).
- **Sistem Petunjuk Bertingkat (PRD 5.3):**
  - *Tingkat 1:* "Periksa kembali aturan angka: cari bilangan prima genap untuk digit pertama, lalu kalikan dua untuk digit kedua."
  - *Tingkat 2:* "Satu-satunya bilangan prima genap adalah 2. Digit kedua adalah 2 x 2 = 4. Digit ketiga adalah 3 x 3 = 9."
  - *Tingkat 3:* "Kombinasi kunci kotak besi adalah 2 - 4 - 9."
- **Validasi & Umpan Balik Edukatif (AC-05):**
  - Jika kombinasi salah, sistem memberi tahu aturan mana yang dilanggar (misalnya Digit I salah, Digit II salah, dsb.) tanpa merusak atau mereset progres investigasi.
  - Jika kombinasi benar, sistem mengunci status terbuka, memutar roda mekanis, melepaskan pengait gerbang, dan menampilkan penjelasan logika solusi secara lengkap.

### 3. Quest "MISTERI AULA UTARA" (Stage 05)

Quest investigasi aktif (`quest_investigasi_aula`) dengan tiga sub-tujuan bertahap:

1. `obj_kumpulkan_bukti`: Kumpulkan 4 bukti aula ($0/4 \rightarrow 4/4$).
2. `obj_pecahkan_puzzle`: Pecahkan teka-teki Kotak Besi Aula ($0/1 \rightarrow 1/1$).
3. `obj_buka_gerbang`: Buka Gerbang Sayap Timur aula ($0/1 \rightarrow 1/1$).

Status quest otomatis diperbarui saat event terjadi di dunia dan menampilkan notifikasi mengambang.

### 4. Gerbang Sayap Timur (Stage 05)

- Pembatas area menuju sayap timur manor pada koordinat $(388, 104)$.
- Terkunci solid dan tidak dapat dilewati sebelum teka-teki kotak besi dipecahkan (AC-06).
- Setelah kotak besi terbuka, interaksi dengan gerbang melepaskan rantai kunci, mengubah tile map menjadi lorong terbuka dengan kilau lentera emas, menghapus rintangan fisik pemain (`removeBlocker`), dan menandai quest selesai utuh.

---

## Scope Stage 06

Sudah ada:

- Seluruh fondasi Stage 01-05 (menu, game loop, render 320x180, scene manager, area uji, gerakan, collision, kamera, NPC, dialog, bukti, casebook, toast notifikasi, puzzle logika kotak besi, quest aktif, gerbang sayap timur).
- Model data `SaveData` berversi skema (`CURRENT_SCHEMA_VERSION = 1`) yang mencakup:
  - Timestamp penyimpanan dan ID area (`the_silent_manor_hall`).
  - Koordinat posisi pemain ($x, y$) dan arah hadap (`Facing`).
  - Daftar ID bukti (`Clue`) yang telah dikumpulkan.
  - Status quest aktif dan seluruh progres sub-tujuan numerik.
  - Status teka-teki (status terpecahkan, jumlah percobaan salah, tingkat petunjuk aktif).
  - Status flag dunia (seperti `gate_east_open`).
- `SaveManager` untuk penanganan berkas simpanan yang aman (NFR-06 / AC-07):
  - Penulisan atomik via berkas temporer (`.tmp`) sebelum menggantikan berkas utama untuk mencegah korupsi data saat penulisan terputus.
  - Pembacaan dan validasi skema data JSON yang kuat.
  - Penanganan berkas save rusak/kosong/skema asing tanpa crash dan **TIDAK PERNAH menghapus berkas asli yang rusak**.
- Autosave otomatis pada peristiwa penting gameplay (PRD 7.1):
  - Setiap kali bukti baru ditemukan (`discover`).
  - Saat teka-teki kotak besi berhasil dipecahkan (`onPuzzleSolved`).
  - Saat gerbang sayap timur dibuka (`unlock`).
  - Saat pemain kembali ke menu utama (`onExit` / ESC).
  - Indikator halus `[AUTOSAVE OK]` pada header HUD virtual saat penyimpanan berhasil.
- Integrasi `MainMenuScene`:
  - Tombol **LANJUTKAN** aktif secara dinamis dengan label `[TERSEDIA]` saat save valid ada di disk, atau `[RUSAK]` bila data rusak.
  - Pemilihan **LANJUTKAN** langsung memuat seluruh progres permainan tanpa kehilangan state investigasi.
  - Modal konfirmasi peringatan saat memilih **MULAI BARU** jika data save lama sudah ada, mencegah penimpaan progres yang tidak disengaja.
  - Dukungan penuh kontrol keyboard dan mouse pada menu dan dialog konfirmasi.

Belum ada (sesuai tahap berikutnya):

- Area baru Sayap Timur dan transisi antar-scene/antar-peta penuh (Stage 07).
- Konten cerita kanonis final, identitas korban/pelaku, dan ending deduksi (Stage 07).

### Catatan konten cerita

Seluruh bukti, teka-teki, dan dialog pada Stage 06 dirancang sejalan dengan atmosfer gothic mystery
aula manor dan tidak mengarang fakta cerita kanon terlarang: nama korban, pelaku final,
dan motif inti tetap dikosongkan untuk tahap kanon sesuai batasan PRD Seksi 20.

## Save dan Autosave

Sistem save diimplementasikan secara terstruktur sesuai PRD Seksi 7, Seksi 10.4, dan NFR-06:

### 1. Lokasi Berkas Save
Lokasi berkas ditentukan secara konsisten dan relatif terhadap direktori kerja sketch Processing:
- Berkas Utama: `save/savegame.json` (dapat diakses via `sketchPath("save/savegame.json")`).
- Berkas Temporer: `save/savegame.json.tmp` (otomatis dihapus/dipindahkan setelah penulisan selesai).

### 2. Format dan Skema Data
Format save menggunakan JSON terstruktur (`SaveData.java`, skema versi 1):
```json
{
  "schemaVersion": 1,
  "timestamp": 1762102800000,
  "areaId": "the_silent_manor_hall",
  "player": {
    "x": 386.0,
    "y": 104.0,
    "facing": "RIGHT"
  },
  "clues": [
    "clue_meja_catatan",
    "clue_rak_harian",
    "clue_penjaga_kesaksian",
    "clue_jendela_goresan"
  ],
  "quests": {
    "activeQuestId": "quest_investigasi_aula",
    "list": [
      {
        "id": "quest_investigasi_aula",
        "status": "COMPLETED",
        "objectives": [
          { "id": "obj_kumpulkan_bukti", "count": 4, "complete": true },
          { "id": "obj_pecahkan_puzzle", "count": 1, "complete": true },
          { "id": "obj_buka_gerbang", "count": 1, "complete": true }
        ]
      }
    ]
  },
  "puzzles": [
    {
      "id": "puzzle_kotak_besi",
      "solved": true,
      "wrongAttempts": 0,
      "hintLevel": 0
    }
  ],
  "worldFlags": {
    "gate_east_open": true
  }
}
```

### 3. Keselamatan dan Integritas Berkas (NFR-06 / AC-07)
- **Penulisan Atomik:** Data ditulis ke berkas `.tmp` terlebih dahulu, lalu dipindahkan menggunakan `Files.move(..., ATOMIC_MOVE, REPLACE_EXISTING)`. Jika proses terputus, berkas asli tidak akan menjadi korup.
- **Penanganan Save Rusak:** Jika berkas kosong (0 byte), sintaks JSON cacat, atau versi skema tidak didukung, sistem melaporkan error diagnostik ke antarmuka Main Menu (`[RUSAK]`) tanpa menyebabkan aplikasi crash. Berkas yang rusak **tidak pernah dihapus** oleh sistem.
- **Konfirmasi Penimpaan:** Memilih Mulai Baru saat berkas simpanan tersedia memunculkan dialog modal peringatan untuk mencegah data lama tertimpa secara tidak sengaja.

## Pengujian yang dilakukan

Tanggal pengujian: **02 November 2026**. Lingkungan: Windows, Processing 4.5.7 portable
(Java Mode), JDK bawaan Processing untuk build dan uji headless.

| Pengujian | Metode | Hasil |
|---|---|---|
| Kompilasi seluruh kelas Java | `javac -Xlint:all` terhadap Processing core 4.5.7 | **Lulus**, 0 error, 0 warning |
| Build Processing CLI | `Processing.exe cli --sketch=... --build` | **Lulus**, exit code 0 |
| Model data & skema `SaveData` | Harness headless | **Lulus**, serialisasi dan deserialisasi JSON v1 utuh (pemain, arah hadap, bukti, quest, puzzle, world flags), penolakan skema asing |
| Penulisan atomik `SaveManager` | Harness headless | **Lulus**, penulisan via berkas `.tmp`, pemindahan atomik, load berkas valid, penanganan berkas belum ada (`NO_SAVE`) |
| Kepatuhan NFR-06 & AC-07 Save Safety | Harness headless | **Lulus**, sintaks korup/0-byte/skema tidak cocok ditangani aman dengan pesan diagnostik, berkas fisik asli yang korup tidak dihapus |
| Logika `MainMenuScene` Save & Modal | Harness headless | **Lulus**, tombol Lanjutkan terkunci saat tidak ada save, label status `[TERSEDIA]` / `[RUSAK]`, modal konfirmasi timpa saat Mulai Baru (Enter konfirmasi, Esc batal) |
| Autosave & Pemulihan `GameplayScene` | Harness headless | **Lulus**, pemulihan posisi pemain, facing, bukti yang ditemukan, status quest aktif & sub-tujuan, status puzzle & hint, dan keterbukaan gerbang |
| Alur Milestone Autosave & Kriteria AC-07 | Harness headless | **Lulus**, autosave terpicu pada setiap penemuan bukti, pemecahan teka-teki (2-4-9), pembukaan gerbang, dan scene exit; keluar ke menu lalu Continue memuat seluruh progres secara akurat |
| Uji regresi menyeluruh Stage 01 s.d. Stage 05 | Pengujian ulang seluruh modul lama | **Lulus**, pergerakan, dialog, tab casebook, puzzle logic, collision gerbang timur tetap berfungsi sempurna |
| Seluruh pemeriksaan headless Stage 06 | `java Stage06Test` | **Lulus**, **108 dari 108 pemeriksaan lolos tanpa error** |
| Pemeriksaan headless Stage 05 | `java Stage05Test` | **Lulus**, **159 dari 159 pemeriksaan lolos tanpa error** |
| Input keyboard fisik | Perangkat fisik | **Tidak teruji**: pengujian otomatis memanggil handler event game secara langsung |

## Status acceptance criteria

Status berikut menilai kriteria yang relevan sampai Stage 06.

| Kriteria | Status | Catatan |
|---|---|---|
| AC-01 Build dan startup | **Pass** | Build Processing CLI sukses; main menu dan gameplay tampil normal dengan aset prosedural |
| AC-02 Movement dan collision | **Pass** | Gerakan empat arah, collision dinding, penghalang NPC, dan penguncian gerak saat dialog/casebook/puzzle tervalidasi |
| AC-03 Interaksi | **Pass** | NPC, tiga titik periksa aula, kotak besi, dan gerbang timur dapat diinteraksikan dengan prompt kontekstual |
| AC-04 Clue dan casebook | **Pass** | Clue tercatat satu kali tanpa duplikasi, masking slot terkunci bekerja, casebook modal 2 tab navigasi normal |
| AC-05 Puzzle | **Pass** | Teka-teki Kotak Besi memiliki aturan eksplisit, kombinasi 2-4-9, validasi 4 kondisi numerik, feedback edukatif saat salah tanpa merusak progres, hint 3 tingkat tidak mengubah jawaban, dan jawaban benar merekam status penyelesaian |
| AC-06 Quest dan unlock | **Pass** | Quest "MISTERI AULA UTARA" hanya selesai setelah 3 sub-tujuan terpenuhi; Gerbang Sayap Timur tetap terkunci solid sebelum kotak besi dipecahkan; setelah dipecahkan, akses terbuka bebas dan rintangan fisik dihapus |
| AC-07 Autosave dan load | **Pass** | Autosave pada peristiwa penting, penulisan atomik via `.tmp`, safe corrupt save handling tanpa crash dan tanpa hapus file (NFR-06), tombol Lanjutkan aktif memuat seluruh state utuh, konfirmasi modal Mulai Baru |
| AC-08 Narasi dan logika | **N/A** | Dialog, petunjuk plakat, teka-teki, dan quest konsisten; fakta kanon final belum ditetapkan sesuai PRD Seksi 20 |
| AC-09 UI | **Pass** | HUD aula, dialog bawah, notifikasi toast, casebook, modal puzzle, dan modal konfirmasi New Game pada resolusi 320x180 proporsional dan bebas clipping |
| AC-10 Dokumentasi dan lisensi | **Pass** | README, changelog, dan kredit aset diperbarui; seluruh visual prosedural |

## Lisensi aset

Stage 06 tidak memakai aset eksternal. Seluruh visual (tileset placeholder, sprite
pemain, sprite NPC, kotak besi, gerbang, panel dialog, modal Casebook, modal teka-teki,
modal konfirmasi simpan, notifikasi toast, dan font 5x7) dibuat secara prosedural di kode. Lihat [`data/licenses/ASSET_CREDITS.md`](data/licenses/ASSET_CREDITS.md).

## Keterbatasan yang diketahui

- Jendela berukuran tetap 960x540 dan belum ada mode fullscreen/resize.
- Font 5x7 hanya memiliki bentuk huruf kapital; huruf kecil otomatis ditampilkan sebagai kapital.
- Baru ada satu area aktif (The Silent Manor - Aula Uji); lorong Sayap Timur baru dibuka secara visual dan collision pada peta aula saat ini (transisi area penuh dijadwalkan pada tahap berikutnya).

## Tahap berikutnya

Stage 07 - Polish dan Konten Kanon: implementasi area Sayap Timur / area luar, transisi scene antar-peta penuh,
integrasi cerita kanon final (korban, pelaku, motif) sesuai dokumen desain resmi, rekonstruksi deduksi akhir,
dan final polishing untuk rilis v1.0.0.
