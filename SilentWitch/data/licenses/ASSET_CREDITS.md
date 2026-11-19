# Asset Credits - Silent Witch

## Status Stage 07 + Aset Karakter Utama

Sebagian besar visual digambar langsung oleh kode Java: tileset 16x16, sprite
NPC, musuh, properti, panel dialog, modal Detective Casebook, notifikasi toast
bukti, dan font bitmap 5x7 yang dibuat manual untuk proyek ini.

Satu-satunya aset gambar eksternal adalah sprite karakter utama
(`data/art/hero_knight_body.png`, `hero_knight_attack.png`, dan
`hero_knight_portrait.png`), yang diturunkan dari paket "Dark Knight"
(`knight_game_assets`, dari pengguna), sprite NPC warga desa
(`data/art/boy_front.png`, `boy_left.png`, `boy_back.png`, `boy_right.png`,
`girl_front.png`, `girl_left.png`, `girl_back.png`, dan `girl_right.png`), yang
diturunkan dari lembar sprite "Lembar Sprite Petualang Desa Bergaya Pixel"
(1536x1024, dari pengguna), latar penuh Ashen Village
(`data/art/bg_ashen_village.png`, render dari pengguna), serta latar penuh
Aula Manor (`data/art/bg_manor_hall.png`, dari gambar pengguna).
Status lisensi paket/lembar tersebut **belum terverifikasi** dan wajib dicek sebelum
rilis publik.

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
| `boy_front/left/back/right.png`, `girl_front/left/back/right.png` (sprite NPC warga desa) | belum diketahui | lembar sprite "Lembar Sprite Petualang Desa Bergaya Pixel" (1536x1024, dari pengguna) | **belum terverifikasi** | 2026-11-19 | Dipotong 4 kolom x 2 baris (baris cowok/cewek, tiap kolom satu arah hadap), latar hitam di-flood-fill menjadi transparan, trim sejajar agar kepala/kaki konsisten antar arah, downscale area-weighted box filter pixel-art tinggi 25 px dengan kontras tajam |
| `elder_front/left/back/right.png` (sprite Sage Istana Gothic di Aula Manor) | belum diketahui | lembar sprite "Lembar Sprite Sage Istana Gothic" (1536x1024, 1x4 kolom dari pengguna) | **belum terverifikasi** | 2026-11-19 | Dipotong 4 kolom (front, right, back, left), flood-fill latar hitam menjadi transparan, trim sejajar kepala dan kaki (y: 264..803), downscale area-weighted box filter tinggi 28 px dengan kontras tajam, mendukung animasi menoleh 4-arah |
| `door_iron.png`, `door_iron_open.png` | proyek ini | dibuat oleh `DoorBuilder` (kode Java) | milik proyek | 2026-11-18 | Sprite 16x32, dua varian (tertutup/terbuka) untuk tile gerbang 'G' |

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
