# Silent Witch

Game pixel-art mystery RPG untuk Windows PC, dibangun dengan **Processing 4 (Java Mode)**.

Repositori ini berisi implementasi bertahap sesuai PRD. Status saat ini:
**Stage 06 - Save/load dan integrasi**.

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

## Scope Stage 06

Sudah ada:
- Seluruh fondasi Stage 01-05 (menu, scene manager, render 320x180, area aula 26x16 tile, pemain, collision, kamera, NPC, titik periksa, panel dialog, bukti kasus, casebook modal, pencegahan duplikasi, notifikasi toast, quest investigasi, puzzle kotak besi, gerbang sayap timur).
- Sistem penyimpanan dan pemuatan game (`SaveData`, `SaveManager`):
  - Skema data berversi (`CURRENT_SCHEMA_VERSION = 1`) menyimpan posisi pemain, arah hadap, bukti yang ditemukan, quest aktif, progres sub-tujuan, status puzzle/hint, dan flag dunia.
  - Penulisan atomik via berkas temporer (`.tmp`) untuk mencegah kerusakan berkas saat proses simpan terputus (NFR-06 / AC-07).
  - Penanganan berkas save rusak/cacat secara diagnostik tanpa crash dan tanpa menghapus berkas asli yang rusak.
  - Autosave otomatis pada peristiwa penting (clue ditemukan, puzzle dipecahkan, gerbang dibuka, keluar scene).
  - Tombol **LANJUTKAN** aktif dinamis di menu utama, dan modal konfirmasi peringatan saat memilih **MULAI BARU** jika data save lama sudah ada.
- Seluruh kriteria penerimaan AC-01 sampai AC-07 (kecuali AC-08 yang menunggu cerita kanon final) berstatus **Pass**.

Belum ada: area Sayap Timur penuh / transisi antar-peta (Stage 07), konten cerita kanon final dan ending deduksi (Stage 07).

## Lisensi aset

Stage 06 tidak memakai aset eksternal. Seluruh visual (tileset placeholder, sprite pemain, NPC, kotak besi, gerbang, panel modal, toast, dan font bitmap 5x7) dibuat secara prosedural. Lihat [`SilentWitch/data/licenses/ASSET_CREDITS.md`](SilentWitch/data/licenses/ASSET_CREDITS.md).
