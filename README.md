# Silent Witch

Game pixel-art mystery RPG untuk Windows PC, dibangun dengan **Processing 4 (Java Mode)**.

Repositori ini berisi implementasi bertahap sesuai PRD. Status saat ini:
**Stage 07 - Konten, polish, dan validasi (v1.0.0)**.

## Isi repositori

- [`SilentWitch/`](SilentWitch/) - proyek Processing 4 yang dapat dijalankan.
- [`SilentWitch/README.md`](SilentWitch/README.md) - panduan lengkap, kontrol, struktur, dan status pengujian.
- [`Silent_Witch_PRD.md`](Silent_Witch_PRD.md) - Product Requirements Document master.
- [`Silent_Witch_Canon.md`](Silent_Witch_Canon.md) - Dokumen cerita kanon resmi (identitas korban, pelaku, motif, timeline, dan alibi).

## Menjalankan

Buka [`SilentWitch/SilentWitch.pde`](SilentWitch/SilentWitch.pde) di Processing 4 (Java Mode),
lalu tekan **Run**.

```powershell
Processing.exe cli --sketch="SilentWitch" --run
```

## Scope Stage 07 (v1.0.0)

Sudah ada:
- Seluruh fondasi Stage 01-06 (menu utama, save/load atomik, render virtual 320x180 integer 3x, collision, kamera halus, NPC, titik periksa, 4 bukti investigasi, Detective Casebook dua tab, quest berprogres, teka-teki logika kotak besi 2-4-9, dan gerbang sayap timur).
- **Eksplorasi Multi-Area:**
  - Peta Sayap Timur (Arsip & Altar Rekonstruksi) 26x16 tile dengan visual prosedural altar batu ungu, meja memorial Clara Thorne, rak arsip kuno mahoni, dan pilar batu.
  - Transisi dua arah yang mulus antara Aula Manor dan Sayap Timur melalui interaksi gerbang maupun langkah kaki.
- **Sistem Rekonstruksi Deduksi Kasus (`DeductionSystem`):**
  - Antarmuka modal 3 langkah untuk menyatukan benang merah kasus berdasarkan bukti yang ditemukan.
  - Pertanyaan kanonis: penetapan pelaku, pembuktian alibi palsu, dan pengungkapan motif konspirasi relik.
- **3 Variasi Ending Kanonis (`EndingScene`):**
  - **The Truth (True Ending):** Pemain mengumpulkan 4 bukti dan menjawab deduksi dengan tepat, mendakwa Garrick dan membersihkan nama Clara Thorne.
  - **The Unfinished Case (Partial Ending):** Penutupan kasus dengan bukti kurang dari 4, pelaku melarikan diri tanpa kepastian hukum.
  - **The False Accusation (Bad Deduction):** Menjatuhkan tuduhan keliru pada pihak yang tidak bersalah, membiarkan pelaku bebas dan relik jatuh ke kultus.
- **Pause Menu Lengkap (PRD 11.1):**
  - Akses jeda via tombol `ESC` dengan opsi Lanjutkan, Pengaturan Cepat Tampilan 960x540 (Skala 3x), dan Kembali ke Menu Utama dengan konfirmasi simpan aman.
- **Kepatuhan Seluruh Kriteria Penerimaan:**
  - Seluruh kriteria penerimaan (**AC-01 sampai AC-10**), termasuk **AC-08 (Narasi dan Logika)** berstatus **Pass**.

## Lisensi aset

Stage 07 tidak memakai aset eksternal. Seluruh visual (tileset, sprite pemain, NPC, kotak besi, gerbang, altar, memorial, modal, toast, dan font bitmap 5x7) dibuat secara murni prosedural. Lihat [`SilentWitch/data/licenses/ASSET_CREDITS.md`](SilentWitch/data/licenses/ASSET_CREDITS.md).
