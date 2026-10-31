# Silent Witch

Game pixel-art mystery RPG untuk Windows PC, dibangun dengan **Processing 4 (Java Mode)**.

Repositori ini berisi implementasi bertahap sesuai PRD. Status saat ini:
**Stage 03 - Interaksi dan dialog**.

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

## Scope Stage 03

Sudah ada: seluruh fondasi Stage 01-02 (menu, scene manager, render 320x180, area uji
26x16 tile, pemain, collision, kamera), ditambah NPC penjaga, tiga objek yang dapat
diperiksa, prompt interaksi, panel dialog, dan penguncian gerak selama dialog terbuka.

Belum ada: clue/casebook, puzzle/quest, save/load, konten cerita final, dan ending.
Tombol **Lanjutkan** sengaja terkunci sampai Stage 06. Dialog saat ini masih
placeholder dan tidak menambah fakta cerita kanonis.

## Lisensi aset

Stage 03 tidak memakai aset eksternal. Seluruh visual dibuat prosedural dan font
5x7 dibuat manual. Lihat [`SilentWitch/data/licenses/ASSET_CREDITS.md`](SilentWitch/data/licenses/ASSET_CREDITS.md).
