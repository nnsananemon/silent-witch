# Changelog

Semua perubahan penting pada proyek ini dicatat di file ini.
Format mengikuti [Keep a Changelog](https://keepachangelog.com/) dan
[Semantic Versioning](https://semver.org/).

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
