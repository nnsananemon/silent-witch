# Changelog

Semua perubahan penting pada proyek ini dicatat di file ini.
Format mengikuti [Keep a Changelog](https://keepachangelog.com/) dan
[Semantic Versioning](https://semver.org/).

## [0.3.0] - 2026-10-31

### Added

- `Interactable` sebagai kontrak objek interaktif (posisi, radius, prompt, dialog,
  render) beserta `Interactable.nearest()` untuk memilih target terdekat.
- `Dialogue` sebagai model data dialog tervalidasi (pembicara, baris, penyalinan array).
- `DialogueSystem` untuk state dialog dan panel bawah: nama pembicara, nomor baris,
  teks terbungkus, dan indikator lanjut/tutup.
- `Npc` dengan sprite penjaga prosedural, radius bicara 22 px, prompt **BICARA**, dan
  dukungan penghalang tubuh.
- `ExamineSpot` untuk objek yang digambar `TileMap`: meja aula, rak buku, dan jendela.
- Prompt `[E] BICARA` / `[E] PERIKSA` di atas objek terdekat, mengikuti kamera dan
  dibatasi agar tetap di dalam layar.
- Input interaksi dengan `E`, `Enter`, atau `Spasi`; dialog maju per baris dan menutup
  pada baris terakhir.
- `Esc` menutup dialog tanpa keluar ke menu.
- Cooldown input 0,18 detik untuk mencegah dialog terbuka kembali atau maju beruntun.
- Pengurutan kedalaman NPC dan pemain berdasarkan posisi `y`.
- `Player.addBlocker()` dan pemeriksaan penghalang tambahan pada collision pemain.
- `GameManager.currentScene()` dan akses posisi/prompt pada `GameplayScene` untuk
  kebutuhan pengujian.
- Petunjuk HUD berubah saat dialog terbuka dan saat berada di dekat objek.

### Changed

- Scene gameplay kini menampung NPC, tiga titik periksa, panel dialog, dan penguncian
  gerak saat dialog terbuka.
- Tombol gerak dilepas otomatis saat dialog dibuka agar pemain tidak meluncur.
- Panel Kontrol menu utama menampilkan tombol interaksi.
- Judul jendela dan label tahap menjadi "Tahap 03".
- README, changelog, dan kredit aset diperbarui untuk Stage 03.

### Notes

- Dialog Stage 03 masih placeholder teknis dan tidak menambah fakta cerita kanonis.
- Belum ada clue, casebook, puzzle, quest, atau save/load.
- Tombol Lanjutkan masih terkunci sampai Stage 06.
- Tidak ada aset eksternal yang digunakan.

## [0.2.0] - 2026-10-31

### Added

- `TileMap` untuk data area berbasis karakter, validasi layout, collision kotak, dan
  render tileset placeholder prosedural 16x16.
- Area uji **The Silent Manor - Aula Uji** berukuran 26x16 tile (416x256 piksel).
- `Player` dengan gerakan empat arah (`WASD`/panah), normalisasi diagonal, dan
  sub-langkah collision agar tidak menembus tile.
- Collision terhadap dinding, jendela, rak, meja, pilar, batas peta, dan objek solid
  di tengah ruangan.
- Kamera yang mengikuti pemain dan dibatasi pada tepi peta.
- Sprite pemain prosedural dengan empat arah hadap dan animasi langkah sederhana.
- HUD nama area dan petunjuk kontrol, plus overlay debu, glow, dan vignette.
- Penerusan event `keyReleased` dari PDE ke `SceneManager` dan scene.
- Reset input gerak saat scene gameplay keluar dan saat jendela kehilangan fokus,
  agar tombol tidak tertinggal aktif.
- Validasi data peta dengan `IllegalArgumentException` berpesan jelas.

### Changed

- Scene gameplay placeholder Stage 01 diganti dengan area uji yang dapat dijelajahi.
- Judul jendela dan label tahap menjadi "Tahap 02".
- README, changelog, dan kredit aset diperbarui untuk Stage 02.

### Notes

- Stage 02 belum memiliki NPC, dialog, clue, casebook, puzzle, quest, atau save/load.
- Tombol Lanjutkan masih terkunci sampai Stage 06.
- Tidak ada aset eksternal yang digunakan.

## [0.1.0] - 2026-10-29

### Added

- Proyek Processing 4 Java Mode dengan entry point `SilentWitch.pde`.
- Game loop berbasis delta time dan render buffer internal 320x180.
- Penskalaan integer 3x ke jendela 960x540 dengan nearest-neighbor (`noSmooth`).
- `GameManager` untuk siklus game, viewport, konversi input mouse, dan render buffer.
- `SceneManager` untuk perpindahan scene dan penerusan input.
- `Scene` sebagai kontrak dasar scene.
- Main menu dengan item Mulai Baru, Lanjutkan (nonaktif), Kontrol, dan Keluar.
- Navigasi menu dengan `W`/`S`, panah, `Enter`, `Spasi`, dan mouse.
- Panel Kontrol yang dapat dibuka/ditutup dengan keyboard dan mouse.
- Pesan umpan balik saat memilih Lanjutkan yang belum memiliki data save.
- Scene gameplay placeholder dengan latar prosedural, tombol Kembali, dan `Esc`.
- Font bitmap 5x7 bawaan tanpa aset eksternal.
- Utilitas UI pixel: panel, tombol, divider, overlay, dan vignette.
- Palet warna gothic bersama.
- Dokumentasi README, changelog, dan kredit aset.

### Notes

- Stage 01 belum memiliki pemain, collision, NPC, dialog, clue, casebook, puzzle,
  quest, ending, atau save/load.
- Tombol Lanjutkan sengaja terkunci sampai Stage 06.
- Tidak ada aset eksternal yang digunakan.
- Build diuji dengan Processing 4.5.7 Java Mode.
