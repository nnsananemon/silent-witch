# Asset Credits - Silent Witch

## Status Stage 07 + Aset Karakter Utama

Sebagian besar visual digambar langsung oleh kode Java: tileset 16x16, sprite
NPC, musuh, properti, panel dialog, modal Detective Casebook, notifikasi toast
bukti, dan font bitmap 5x7 yang dibuat manual untuk proyek ini.

Aset gambar eksternal yang sudah dipakai:

- Sprite karakter utama (`data/art/hero_knight_body.png`,
  `hero_knight_attack.png`, dan `hero_knight_portrait.png`), diturunkan dari
  paket "Dark Knight" (`knight_game_assets`, dari pengguna).
- Sprite NPC warga desa (`data/art/boy_front.png`, `boy_left.png`,
  `boy_back.png`, `boy_right.png`, `girl_front.png`, `girl_left.png`,
  `girl_back.png`, dan `girl_right.png`), diturunkan dari lembar sprite
  "Lembar Sprite Petualang Desa Bergaya Pixel" (1536x1024, dari pengguna).
- Latar penuh Ashen Village (`data/art/bg_ashen_village.png`, render dari
  pengguna), Aula Manor (`data/art/bg_manor_hall.png`, dari gambar pengguna),
  Hollow Woods (`data/art/bg_hollow_woods.png`, dari gambar pengguna), dan
  Jalur Tebing Lembah (`data/art/bg_valley_cliff.png`, dari gambar pengguna).
- Sprite penyihir gothic (`data/art/wizard.png`) dan flameling
  (`data/art/flamelings.png`) untuk musuh Stage 3.
- Sprite obor genggam (`data/art/torch_anim.png`, 48x32 = 6 frame 16x16)
  dari OpenGameArt karya Natural_Privateer, lisensi **CC0** — dipakai di
  Jalur Tebing sebagai satu-satunya sumber cahaya.
- Latar penuh Dasar Lembah (`data/art/bg_valley_floor.png`, 640x256),
  sprite nyala api (`flame_anim.png`, 6 frame), dan riak air
  (`water_anim.png`), diturunkan dari tileset & render pengguna
  (1254x1254 / 1983x793).
- Sprite pose duduk ksatria (`data/art/hero_sit.png`, 4 arah), diturunkan
  dari lembar "Lembar Sprite Ksatria Berkemah" (1536x1024, dari pengguna).
- Latar penuh Lintasan Reruntuhan (`data/art/bg_valley_ruins.png`, 640x256),
  dari render "Reruntuhan Dungeon dengan Rune Bercahaya" (1983x793, dari
  pengguna), di-downscale ke 640x256. Tileset "Tileset Pixel Art Reruntuhan
  Gelap" (1254x1254) jadi referensi gaya yang senada.

Status lisensi paket Dark Knight, lembar NPC desa, latar buatan pengguna, dan
flamelings **belum terverifikasi** dan wajib dicek sebelum rilis publik.
Paket **Gothicvania Church** karya Luis Zuno (@ansimuz) yang menjadi sumber
sprite `wizard.png` berlisensi **public domain** sesuai `public-license.txt`
paket tersebut (kredit tidak wajib, tetapi dicantumkan sebagai apresiasi).
Sprite `torch_anim.png` berlisensi **CC0** (Natural_Privateer, OpenGameArt),
jadi aman dipakai tanpa atribusi wajib; kredit tetap dicantumkan sebagai
apresiasi.

Sprite `data/art/door_iron.png` dan `door_iron_open.png` bukan aset eksternal:
keduanya digambar oleh kode proyek lewat util `DoorBuilder`.

Processing 4.5.7 dan JDK yang disertakan digunakan sebagai engine/runtime, bukan
sebagai aset game.

## Catatan untuk tahap berikutnya

Sebelum aset eksternal dimasukkan ke proyek, isi tabel di bawah dan verifikasi
lisensi pada halaman resmi sumber. Jangan menghapus atribusi yang diwajibkan.

| Nama aset | Pembuat | URL sumber | Lisensi | Tanggal verifikasi | Perubahan |
|---|---|---|---|---|---|
| `hero_knight_body.png` (turunan `player/knight_{idle,walk,run}_{down,up,left}.png`) | belum diketahui | paket "Dark Knight" / `knight_game_assets` (dari pengguna) | **belum terverifikasi** | 2026-11-12 | Di-downscale lewat `SpriteSheetBuilder` (skala 0,174), 9 pose dalam satu sheet, tinggi 28 px. Pose `*_right` di paket tidak dipakai karena art itu juga menghadap kiri; arah kanan memakai cermin dari pose `*_left` |
| `hero_knight_attack.png` (turunan `attack/knight_attack_1..5.png`) | belum diketahui | paket "Dark Knight" / `knight_game_assets` (dari pengguna) | **belum terverifikasi** | 2026-11-12 | Di-downscale lewat `SpriteSheetBuilder` (skala 0,151), 5 frame, tinggi 26 px, efek sabit ikut terbawa |
| `hero_knight_portrait.png` (turunan `ui/knight_portrait.png`) | belum diketahui | paket "Dark Knight" / `knight_game_assets` (dari pengguna) | **belum terverifikasi** | 2026-11-12 | Di-downscale lewat `SpriteSheetBuilder`, 1 frame, tinggi 20 px, dipakai di pause menu |
| `bg_ashen_village.png` (latar Ashen Village) | belum diketahui | render 1983x793 dari pengguna | **belum terverifikasi** | 2026-11-14 | Di-downscale ke 640x256 (ukuran peta) lewat `BackgroundBuilder` (rata-rata area), dipakai sebagai latar penuh area awal |
| `bg_manor_hall.png` (latar Aula Manor) | belum diketahui | gambar 1024x572 dari pengguna (`.webp`) | **belum terverifikasi** | 2026-11-15 | Dikonversi ke PNG lewat WPF `PresentationCore`, lalu di-crop 930x572 @ (75,0) dan di-downscale ke 416x256 lewat `PixelScaler` (mode-color per blok) |
| `bg_hollow_woods.png` (latar Hollow Woods Stage 3) | belum diketahui | gambar gothic hutan berkabut 1986x792 dari pengguna | **belum terverifikasi** | 2026-11-19 | Di-downscale ke 640x256 (ukuran peta) lewat `BackgroundBuilder` (rata-rata area subpixel), dipakai sebagai latar penuh stage 3 Hollow Woods |
| `bg_valley_cliff.png` (latar Jalur Tebing Lembah, Bab 3) | belum diketahui | gambar reruntuhan bata batu dengan tiga simbol cyan 1983x793 dari pengguna | **belum terverifikasi** | 2026-11-21 | Di-downscale ke 640x256 (ukuran peta) lewat `BackgroundBuilder` (rata-rata area), dipakai sebagai latar penuh `valley_cliff_path`; ketiga simbol sejajar dengan tile 'X' dan `ExamineSpot` batu simbol |
| `boy_front/left/back/right.png`, `girl_front/left/back/right.png` (sprite NPC warga desa) | belum diketahui | lembar sprite "Lembar Sprite Petualang Desa Bergaya Pixel" (1536x1024, dari pengguna) | **belum terverifikasi** | 2026-11-19 | Dipotong 4 kolom x 2 baris (baris cowok/cewek, tiap kolom satu arah hadap), latar hitam di-flood-fill menjadi transparan, trim sejajar agar kepala/kaki konsisten antar arah, downscale area-weighted box filter pixel-art tinggi 25 px dengan kontras tajam |
| `elder_front/left/back/right.png` (sprite Sage Istana Gothic di Aula Manor) | belum diketahui | lembar sprite "Lembar Sprite Sage Istana Gothic" (1536x1024, 1x4 kolom dari pengguna) | **belum terverifikasi** | 2026-11-19 | Dipotong 4 kolom (front, right, back, left), flood-fill latar hitam menjadi transparan, trim sejajar kepala dan kaki (y: 264..803), downscale area-weighted box filter tinggi 28 px dengan kontras tajam, mendukung animasi menoleh 4-arah |
| `door_iron.png`, `door_iron_open.png` | proyek ini | dibuat oleh `DoorBuilder` (kode Java) | milik proyek | 2026-11-18 | Sprite 16x32, dua varian (tertutup/terbuka) untuk tile gerbang 'G' |
| `wizard.png` | Luis Zuno (@ansimuz) | paket "Gothicvania Church" (dari pengguna) | **public domain** (`public-license.txt`) | 2026-11-20 | Sheet 1215x66, 15 frame: 5 idle + 10 cast/fire. Dipakai sebagai sprite mini-boss Penjaga Jejak |
| `wizard_purple.png` | belum diketahui | lembar sprite wizard ungu gothic 2172x724 dari pengguna (`872961d4-ec02-4d3a-bd6b-5c728f6b8b46.png`) | **belum terverifikasi** | 2026-11-20 | 15 frame profil menyamping menghadap kiri (160x160 per sel transparan), mencakup idle, telegraf sihir ungu, dan semburan api depan |
| `flamelings.png` | belum diketahui | dari pengguna (`flamelings_sprites.png`) | **belum terverifikasi** | 2026-11-20 | Sheet 384x128 berisi 3 wujud (kecil/sedang/besar). Latar biru rata dibuang saat runtime, lalu dipangkas. Dipakai Bayangan Hutan (wujud kecil→sedang→besar sesuai state) dan api Rest Point |
| `torch_anim.png` (obor genggam Jalur Tebing) | Natural_Privateer | OpenGameArt, "Simple Torch Animation 16x16" (`opengameart.org/content/simple-torch-animation-16x16`) | **CC0** | 2026-11-21 | Sheet 48x32 = 6 frame 16x16 (3 kolom x 2 baris). Dipakai obor yang diambil di pintu masuk Jalur Tebing dan digenggam pemain saat berjalan; cahayanya menjadi satu-satunya penerang area gelap |
| `bg_valley_floor.png`, `flame_anim.png`, `water_anim.png` (Dasar Lembah) | belum diketahui | render 1983x793 & tileset 1254x1254 dari pengguna | **belum terverifikasi** | 2026-11-21 | Render di-downscale ke 640x256 lewat `BackgroundBuilder`-style area-average; nyala api diekstrak 6 frame dari altar, riak air dari variasi tile air. Dipakai di area `valley_floor` |
| `hero_sit.png` (pose duduk ksatria) | belum diketahui | lembar "Lembar Sprite Ksatria Berkemah" 1536x1024 dari pengguna | **belum terverifikasi** | 2026-11-21 | 4 pose (kiri/kanan/depan/belakang) dipotong 2x2, latar putih dibuang, downscale area-weighted tinggi 28 px dengan sedikit boost kecerahan. Dipakai saat pemain istirahat di dekat api unggun |
| `brazier_anim.png` (brazier batu dungeon beranimasi) | belum diketahui | animasi GIF "Animasi Brazier Api di Dungeon Masonry.gif" (380x508, 8 frame) dari pengguna | **belum terverifikasi** | 2026-11-22 | Diekstrak 8 frame (16x32 per frame), latar belakang gua di-keying transparan dengan tetap menjaga kehalusan perimeter api dan partikel percikan, disusun horizontal (128x32). Menggantikan nyala api prosedural/flame_anim di seluruh area gua (`valley_floor`, `valley_ruins`, tile `Z`). |
| `bg_chapel_whispers.png` (latar Chapel of Whispers) | belum diketahui | gambar "Reruntuhan Kuil Rune Bercahaya" 1983x793 dari pengguna | **belum terverifikasi** | 2026-11-26 | Di-downscale ke 640x256 (area-average/BOX). Interior gelap dengan altar bercahaya di sekitar kolom 30-31; latar penuh `chapel_of_whispers`. Altar (tile 'a') & brazier (RestPoint) digambar sebagai overlay buatan sendiri di atasnya. |

Bila berkas sprite di atas dihapus, game otomatis kembali memakai penyihir
prosedural (`Art.HERO_SPRITE` gagal dimuat) sehingga tidak ada aset eksternal
yang tersisa. `background/dungeon_scene.png` dari paket yang sama belum dipakai
di game.

## Aturan

1. Periksa lisensi terbaru di halaman sumber dan file lisensi yang disertakan.
2. Catat nama aset, pembuat, URL, lisensi, tanggal pemeriksaan, dan perubahan.
3. Jangan mengklaim aset pihak lain sebagai karya original.
4. Jangan mengemas ulang aset yang lisensinya belum jelas.
5. Gunakan placeholder buatan sendiri sampai status lisensi terverifikasi.
