# Silent Witch

Game pixel-art mystery RPG untuk Windows PC, dibangun dengan **Processing 4 (Java Mode)**.

Repositori ini berisi implementasi bertahap sesuai PRD. Status saat ini:
**Stage 01 - Audit dan fondasi**.

## Isi repositori

- [`SilentWitch/`](SilentWitch/) - proyek Processing 4 yang dapat dijalankan.
- [`SilentWitch/README.md`](SilentWitch/README.md) - cara menjalankan, kontrol, struktur, dan status pengujian.
- [`Silent_Witch_PRD.md`](Silent_Witch_PRD.md) - Product Requirements Document lengkap.

## Menjalankan

Buka [`SilentWitch/SilentWitch.pde`](SilentWitch/SilentWitch.pde) di Processing 4 (Java Mode),
lalu tekan **Run**.

```powershell
Processing.exe cli --sketch="SilentWitch" --run
```

## Scope Stage 01

Sudah ada: main menu, panel kontrol, scene gameplay placeholder, render pixel-art
320x180 dengan skala integer, navigasi keyboard/mouse, dan dokumentasi.

Belum ada: pemain/collision, NPC/dialog, clue/casebook, puzzle/quest, save/load,
konten cerita final, dan ending. Tombol **Lanjutkan** sengaja terkunci sampai Stage 06.

## Lisensi aset

Stage 01 tidak memakai aset eksternal. Seluruh visual dibuat prosedural dan font
5x7 dibuat manual. Lihat [`SilentWitch/data/licenses/ASSET_CREDITS.md`](SilentWitch/data/licenses/ASSET_CREDITS.md).
