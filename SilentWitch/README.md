# Silent Witch

Game pixel-art mystery RPG untuk Windows PC, dibangun dengan **Processing 4 (Java Mode)**.
Dokumen kebutuhan lengkap ada di [`../Silent_Witch_PRD.md`](../Silent_Witch_PRD.md), dan dokumen kanon cerita resmi di [`../Silent_Witch_Canon.md`](../Silent_Witch_Canon.md).

**Status saat ini: Stage 07 - Konten, Polish, dan Validasi (v1.0.0).**
Pemain dapat menjelajah dua area yang terhubung (**Aula Manor** dan **Sayap Timur: Arsip & Altar**), berinteraksi dengan NPC dan objek periksa, mengumpulkan 4 bukti investigasi yang saling terhubung di **Detective Casebook**, menyelesaikan quest **MISTERI AULA UTARA**, memecahkan teka-teki logika deduksi kondisi **Kotak Besi Aula** (`2 - 4 - 9`), membuka rintangan **Gerbang Sayap Timur**, menjelajahi arsip serta memorial Clara Thorne, mengakses **Pause Menu** lengkap dengan konfirmasi keluar, menyusun deduksi akhir di **Altar Rekonstruksi Kasus**, membuka salah satu dari **3 variasi ending kanonis** (**The Truth**, **The Unfinished Case**, atau **The False Accusation**) pada layar hasil investigasi **EndingScene**, serta menyimpan dan memuat seluruh progres permainan secara aman dengan **SaveManager**.

Seluruh 10 Kriteria Penerimaan (**AC-01** sampai **AC-10**), termasuk **AC-08 (Narasi dan Logika)**, kini berstatus **Pass**.

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
| Menu Utama | `W` / `S` atau panah atas/bawah | Pilih item menu |
| Menu Utama | `Enter` / `Spasi` / klik mouse | Konfirmasi item |
| Menu Utama | `Esc` | Menutup panel Kontrol atau membatalkan pesan |
| Gameplay | `W` / `A` / `S` / `D` atau tombol panah | Bergerak empat arah |
| Gameplay | `Klik Kanan` / `F` / `J` | Serang dengan Senjata Saku ("Tongkat Perak Saku") |
| Gameplay | `E` / `Enter` / `Spasi` | Interaksi dengan NPC, periksa objek, buka kotak besi, atau masuk gerbang |
| Gameplay | `C` / `Tab` | Buka/tutup Detective Casebook (Buku Kasus) |
| Gameplay | `Esc` | Membuka Pause Menu (Jeda) |
| Pause Menu | `W` / `S` atau panah atas/bawah | Pilih opsi jeda (Lanjutkan / Pengaturan Tampilan / Menu Utama) |
| Pause Menu | `Enter` / `Spasi` / klik mouse | Pilih opsi jeda aktif |
| Pause Menu | `A` / `D` (pada popup konfirmasi keluar) | Pilih konfirmasi `YA` atau `BATAL` |
| Pause Menu | `Esc` | Kembali melanjutkan permainan (Resume) |
| Dialog terbuka | `E` / `Enter` / `Spasi` | Lanjut ke baris berikutnya; menutup pada baris terakhir |
| Dialog terbuka | `Esc` | Menutup dialog tanpa jeda menu |
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
| Puzzle terbuka | Klik mouse | Navigasi dial, tombol bantuan, tombol buka, atau klik luar untuk keluar |
| Deduksi terbuka | `W` / `S` atau panah atas/bawah | Pilih alternatif jawaban deduksi |
| Deduksi terbuka | Angka `1` / `2` / `3` | Memilih jawaban secara instan |
| Deduksi terbuka | `Enter` / `Spasi` / klik mouse | Konfirmasi jawaban dan maju ke langkah berikutnya |
| Deduksi terbuka | `Esc` | Membatalkan / menutup modal rekonstruksi deduksi |
| Ending Screen | `Enter` / `Spasi` / `Esc` / klik mouse | Kembali ke Menu Utama |

`Esc` tidak menutup aplikasi secara tiba-tiba. Keluar dilakukan melalui Pause Menu atau item **KELUAR** di menu utama.

## Struktur proyek

```text
SilentWitch/
├── SilentWitch.pde          # entry point Processing (settings/setup/draw/input)
├── GameManager.java         # render buffer 320x180, loop, viewport, scene switcher, input
├── SceneManager.java        # scene aktif dan siklus hidup
├── Scene.java               # kontrak dasar scene
├── MainMenuScene.java       # menu utama (Mulai Baru, Lanjutkan, Kontrol, Keluar, modal overwrite)
├── GameplayScene.java       # gameplay multi-area (Aula & Sayap Timur), kamera, NPC, bukti, casebook, pause, deduksi
├── EndingScene.java         # layar hasil investigasi deduksi (The Truth, Unfinished Case, False Accusation)
├── TileMap.java             # data tile 26x16 Aula & Sayap Timur, collision, render prosedural altar/memorial/gate
├── Player.java              # gerakan, combat senjata saku ("Tongkat Perak"), HP, i-frames, sprite prosedural
├── Enemy.java               # basis kelas musuh: FSM (Patrol, Chase, Telegraph, Attack, Recovery)
├── BayanganHutan.java       # musuh reguler kabut Hollow Woods dengan telegraf cakar bayangan
├── PenjagaJejak.java        # mini-boss Hollow Woods: 2 fase, telegraf lantai merah, hadiah Sigil Jejak
├── CombatSystemTest.java    # automated test suite lengkap sistem pertarungan real-time 2D
├── StoryProgressionTest.java# automated test suite transisi multi-area, boundary, dan save/load
├── Interactable.java        # kontrak objek interaktif + integrasi investigasi
├── Npc.java                 # NPC penjaga aula, sprite, radius bicara, dialog awal & pengingat
├── ExamineSpot.java         # titik periksa objek dunia dengan integrasi clue & multi-dialog
├── KotakBesi.java           # objek interaktif peti besi aula pengunci teka-teki
├── GerbangTimur.java        # objek interaktif gerbang pembatas sayap timur aula (akses lorong)
├── GerbangBarat.java        # objek interaktif gerbang penghubung Sayap Timur kembali ke Aula
├── AltarDeduksi.java        # objek interaktif altar rekonstruksi kasus di Sayap Timur
├── Dialogue.java            # model data dialog (pembicara + baris)
├── DialogueSystem.java      # state dialog dan panel bawah
├── Clue.java                # model data bukti tervalidasi (ID stabil, sumber, linimasa)
├── InvestigationSystem.java # manajemen bukti, casebook modal, notifikasi toast
├── QuestObjective.java      # model sub-tujuan spesifik dengan pelacakan progres bernomor
├── Quest.java               # model data quest (status, prasyarat, sub-tujuan, reward)
├── QuestSystem.java         # manajemen quest, event hooks, notifikasi progres, HUD summary
├── Puzzle.java              # model teka-teki logika deduksi kondisi, validasi, 3-tier hints
├── PuzzleSystem.java        # modal antarmuka puzzle 300x164, dial navigasi, feedback edukatif
├── DeductionSystem.java     # modal rekonstruksi deduksi akhir 3 pertanyaan dengan evaluasi 3 ending
├── SaveData.java            # model serialisasi/deserialisasi state investigasi (JSON v1, multi-area)
├── SaveManager.java         # penulisan file atomik (.tmp), validasi, penanganan save corrupt aman
├── PixelFont.java           # font bitmap 5x7 bawaan (dengan glyph '^', '>', 'X')
├── PixelUI.java             # panel, tombol, divider, vignette, modal overlay
├── Palette.java             # palet warna dan util warna
├── Art.java                 # lapisan gambar opsional (tile/properti) + sprite karakter utama
├── SpriteSheetBuilder.java  # util CLI: render frame besar -> sprite sheet pixel-art
├── BackgroundBuilder.java   # util CLI: render latar besar -> ukuran peta (area average)
├── NpcSpriteBuilder.java    # util CLI: build & grade sprite NPC agar menyatu dengan latar
├── AssetExport.java         # util CLI: ekspor seluruh visual ke PNG referensi
├── MenuPreview.java         # util CLI: render menu utama ke PNG
├── README.md
├── CHANGELOG.md
└── data/
    ├── art/                 # sprite karakter utama + latar peta (bg_*.png)
    └── licenses/
        └── ASSET_CREDITS.md
```

## Rendering

- Resolusi internal: **320 x 180** piksel.
- Jendela: **960 x 540** (skala integer 3x).
- Semua scene menggambar ke `PGraphics` 320x180, lalu hasilnya diperbesar ke jendela dengan integer nearest-neighbor tanpa blur.
- Teks memakai font bitmap 5x7 buatan sendiri (`PixelFont.java`), bebas dari dependensi font sistem.
- NPC, musuh, dan properti dibuat murni secara prosedural (tanpa file gambar eksternal).
  Beberapa area memakai gambar latar penuh dari pengguna: **Ashen Village**
  (`data/art/bg_ashen_village.png`), **Aula Manor** (`bg_manor_hall.png`, 416x256),
  **Hollow Woods** (`bg_hollow_woods.png`), dan **Jalur Tebing Lembah**
  (`bg_valley_cliff.png`) — semuanya 640x256 kecuali aula. Peta yang punya gambar
  latar menggambar gambar itu sebagai lapisan dasar dan hanya tile objek yang
  digambar di atasnya; kalau berkasnya hilang, peta kembali ke tile prosedural.
- **Jalur Tebing gelap:** area ini hanya diterangi obor. Ambil obor di dekat
  pintu masuk dengan `E`, lalu obor digenggam dan cahayanya mengikuti pemain.
  Sprite obor (`data/art/torch_anim.png`, CC0) tidak bergantung pada aset lain;
  bila berkasnya hilang, obor tetap bisa diambil namun hanya memancarkan cahaya
  tanpa sprite.
- Karakter utama memakai sprite dari paket "Dark Knight" (`knight_game_assets`):
  `data/art/hero_knight_body.png` (12 pose idle/walk/run × 4 arah),
  `data/art/hero_knight_attack.png` (5 frame tebasan), dan
  `data/art/hero_knight_portrait.png` (potret di pause menu) dengan saklar
  `Art.HERO_SPRITE`. Bila sheet tidak ditemukan, pemain otomatis kembali ke
  penyihir prosedural.
- Lokasi aset tidak bergantung pada folder kerja: `Art.attach(PApplet)` membaca
  `app.sketchPath()` (Processing menerimanya lewat `--sketch-path`), lalu `Art.dataRoot()`
  mencari `data/art` dari folder sketch, folder kerja, dan folder build class.
  Ini penting karena Processing IDE menjalankan sketch dari folder sementara di
  `%TEMP%` yang tidak memuat folder `data/`.

### Membangun ulang sprite karakter utama

Sheet dibuat dari paket "Dark Knight" (`knight_game_assets`) memakai
`SpriteSheetBuilder`, yang menormalkan tinggi karakter, memusatkan badan, dan
menempelkan kaki di dasar sel. Urutan frame pada sheet mengikuti urutan nama
file di folder masukan, jadi diberi nomor urut:

```powershell
# Badan: 00_idle_down, 01_idle_up, 02_idle_left,
#        03_walk_down, 04_walk_up, 05_walk_left,
#        06_run_down,  07_run_up,  08_run_left  -> 9 pose
java -cp . SpriteSheetBuilder "<folderFrameBadan>" "data/art/hero_knight_body.png" 28

# Tebasan: 01..05 -> 5 frame (tinggi 26 supaya sama dengan badan)
java -cp . SpriteSheetBuilder "<folderFrameTebasan>" "data/art/hero_knight_attack.png" 26

# Potret pause menu
java -cp . SpriteSheetBuilder "<folderPotret>" "data/art/hero_knight_portrait.png" 20
```

Angka `28`/`26`/`20` adalah tinggi sprite dalam piksel dunia. Jangkar gambar
selalu `(lebarSel / 2, tinggiSel)`, jadi sel boleh berbeda ukuran antar animasi.

**Catatan arah hadap:** paket "Dark Knight" hanya menyediakan satu art samping
yang menghadap kiri — berkas `knight_*_right.png` di paket itu juga menghadap
kiri (tokoh akan terlihat "jalan mundur" saat bergerak ke kanan). Karena itu
sheet badan hanya memakai pose `down/up/left`, dan art samping dicerminkan
otomatis di `Art.drawHero` saat pemain menghadap kanan.


## Konten Kanonis & Deduksi (Stage 07)

Sesuai dokumen resmi `Silent_Witch_Canon.md`:

### 1. Fakta Cerita Kanon
- **Korban:** Clara Thorne (15 tahun), putri mantan kurator perpustakaan The Silent Manor.
- **Pelaku Sebenarnya:** Garrick (Penjaga Malam Aula).
- **Motif Final:** Keserakahan dan persekongkolan dengan kultus rahasia Ashen untuk menjarah relik kuno di Sayap Timur.
- **Kronologi:** Garrick memanipulasi jadwal jaga aula agar berjaga sendirian, dipergoki oleh Clara, mengunci Clara di Sayap Timur, lalu merekayasa cungkilan jendela utara untuk menyalahkan pihak luar.

### 2. Tiga Jalur Ending Kanonis (PRD 6.2)
1. **The Truth (True Ending):**
   - Pemain mengumpulkan seluruh 4 bukti aula ($4/4$).
   - Menyelesaikan teka-teki kotak besi dan membuka gerbang.
   - Pada Altar Rekonstruksi, memilih kesimpulan yang benar:
     - Pelaku: Garrick (Penjaga Malam Aula).
     - Bukti bantahan: Rekayasa cungkilan jendela dan manipulasi jadwal jaga.
     - Motif: Persekongkolan menjarah relik Sayap Timur bersama kultus.
   - **Hasil:** Garrick mengakui kejahatannya di hadapan tetua desa. Nama Clara Thorne dibersihkan dan dihormati, relik disegel kembali, dan kedamaian kembali ke Ashen Village.
2. **The Unfinished Case (Partial Ending):**
   - Pemain menyelesaikan rekonstruksi sebelum seluruh 4 bukti terkumpul, atau menutup investigasi lebih awal.
   - **Hasil:** Bukti tidak cukup untuk mendakwa Garrick secara sah. Garrick melarikan diri, kasus ditutup menggantung, dan desa tetap dibayangi ketidakpastian.
3. **The False Accusation (Bad Deduction):**
   - Pemain memiliki 4 bukti namun memilih kesimpulan yang keliru (misalnya menyalahkan penyusup asing atau mengabaikan kejanggalan jendela).
   - **Hasil:** Garrick bebas dari jerat hukum, relik Sayap Timur jatuh ke tangan kultus bayangan, dan pengorbanan Clara Thorne terkubur selamanya.

---

## Status Acceptance Criteria

| Kriteria | Status | Catatan |
|---|---|---|
| AC-01 Build dan startup | **Pass** | Build Processing CLI sukses; main menu dan gameplay tampil normal dengan aset prosedural |
| AC-02 Movement dan collision | **Pass** | Gerakan empat arah, collision dinding multi-area, penghalang NPC/objek, dan penguncian gerak saat dialog/casebook/puzzle/deduksi/pause tervalidasi |
| AC-03 Interaksi | **Pass** | NPC, tiga titik periksa aula, kotak besi, gerbang timur, memorial Clara, arsip kuno, altar deduksi, dan gerbang barat dapat diinteraksikan dengan prompt kontekstual |
| AC-04 Clue dan casebook | **Pass** | Clue tercatat satu kali tanpa duplikasi, masking slot terkunci bekerja, casebook modal 2 tab navigasi normal |
| AC-05 Puzzle | **Pass** | Teka-teki Kotak Besi memiliki aturan eksplisit, kombinasi 2-4-9, validasi 4 kondisi numerik, feedback edukatif saat salah tanpa merusak progres, hint 3 tingkat tidak mengubah jawaban, dan jawaban benar merekam status penyelesaian |
| AC-06 Quest dan unlock | **Pass** | Quest "MISTERI AULA UTARA" hanya selesai setelah 3 sub-tujuan terpenuhi; Gerbang Sayap Timur tetap terkunci solid sebelum kotak besi dipecahkan; setelah dipecahkan, akses terbuka bebas dan rintangan fisik dihapus |
| AC-07 Autosave dan load | **Pass** | Autosave pada peristiwa penting, penulisan atomik via `.tmp`, safe corrupt save handling tanpa crash dan tanpa hapus file (NFR-06), tombol Lanjutkan aktif memuat seluruh state utuh (termasuk status area Sayap Timur), konfirmasi modal Mulai Baru |
| AC-08 Narasi dan logika | **Pass** | Narasi terikat erat dengan dokumen kanon resmi `Silent_Witch_Canon.md`, tanpa kontradiksi logika bukti, serta evaluasi 3 variasi ending kanonis didasarkan pada kualitas bukti yang ditemukan |
| AC-09 UI | **Pass** | HUD aula/sayap timur, dialog bawah, notifikasi toast, casebook, modal puzzle, pause menu, modal deduksi, dan ending screen pada resolusi 320x180 proporsional dan bebas clipping |
| AC-10 Dokumentasi dan lisensi | **Pass** | README, changelog, dan kredit aset diperbarui; seluruh visual prosedural |

---

## Lisensi aset

Seluruh visual (tileset placeholder, sprite NPC, musuh, kotak besi, gerbang, altar,
memorial, panel dialog, modal Casebook, modal teka-teki, modal deduksi, layar ending,
pause menu, notifikasi toast, dan font 5x7) dibuat secara prosedural di kode.

Satu-satunya aset gambar eksternal adalah sprite karakter utama
(`data/art/hero_knight_body.png`, `hero_knight_attack.png`, `hero_knight_portrait.png`)
dari paket "Dark Knight" (`knight_game_assets`). Status lisensi paket tersebut
**belum terverifikasi** dan wajib dicek sebelum rilis publik. Lihat
[`data/licenses/ASSET_CREDITS.md`](data/licenses/ASSET_CREDITS.md).
