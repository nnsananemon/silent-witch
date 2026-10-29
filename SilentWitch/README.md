# Silent Witch

Game pixel-art mystery RPG untuk Windows PC, dibangun dengan **Processing 4 (Java Mode)**.
Dokumen kebutuhan lengkap ada di [`../Silent_Witch_PRD.md`](../Silent_Witch_PRD.md).

**Status saat ini: Stage 01 - Audit dan fondasi.**
Belum ada pemain, collision, clue, quest, puzzle, dialog NPC, atau save. Lihat
[Scope Stage 01](#scope-stage-01) dan [Status acceptance criteria](#status-acceptance-criteria).

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
| Gameplay placeholder | `Esc` | Kembali ke menu |
| Gameplay placeholder | Klik tombol `KEMBALI KE MENU` | Kembali ke menu |

`Esc` tidak menutup aplikasi. Keluar dilakukan melalui item **KELUAR** di menu utama.

## Struktur proyek

```text
SilentWitch/
├── SilentWitch.pde      # entry point Processing (settings/setup/draw/input)
├── GameManager.java     # render buffer 320x180, loop, viewport, input
├── SceneManager.java    # scene aktif dan siklus hidup
├── Scene.java           # kontrak dasar scene
├── MainMenuScene.java   # menu utama
├── GameplayScene.java   # scene gameplay placeholder
├── PixelFont.java       # font bitmap 5x7 bawaan
├── PixelUI.java         # panel, tombol, divider, vignette
├── Palette.java         # palet warna dan util warna
├── README.md
├── CHANGELOG.md
└── data/
    └── licenses/
        └── ASSET_CREDITS.md
```

### Penyimpangan dari struktur PRD

PRD menyarankan folder `src/` untuk kelas Java. Processing hanya mengompilasi tab
`.java` yang berada di **root folder sketch**, sehingga seluruh kelas proyek
diletakkan di root `SilentWitch/` dan bukan di `src/`. Struktur ini dipilih agar
proyek dapat langsung dibuka dan dijalankan di Processing 4 tanpa build tool
tambahan. Tidak ada kelas kosong yang dibuat hanya untuk mengikuti daftar modul PRD.

## Rendering

- Resolusi internal: **320 x 180** piksel.
- Jendela: **960 x 540** (skala integer 3x).
- Semua scene menggambar ke `PGraphics` 320x180, lalu hasilnya diperbesar ke jendela.
- `noSmooth()` dipanggil di `settings()` agar penskalaan memakai nearest-neighbor dan
  pixel art tidak kabur.
- Teks memakai font bitmap 5x7 buatan sendiri (`PixelFont.java`), sehingga tidak
  bergantung pada font sistem atau file aset. Huruf kecil ditampilkan sebagai huruf kapital.

## Scope Stage 01

Sudah ada:

- Proyek Processing 4 Java Mode yang dapat dikompilasi dan dijalankan.
- Game loop dengan delta time dan pembatas dt.
- `SceneManager` sederhana dengan perpindahan dua scene.
- Render buffer pixel-art 320x180 dengan skala integer.
- Main menu: **Mulai Baru**, **Lanjutkan** (nonaktif), **Kontrol**, **Keluar**.
- Navigasi menu dengan keyboard dan mouse.
- Panel Kontrol sederhana.
- Scene gameplay placeholder dengan latar prosedural, tombol kembali, dan `Esc`.
- Umpan balik jelas saat **Lanjutkan** dipilih: belum ada data investigasi/save.
- Dokumentasi, changelog, dan kredit aset.

Belum ada (sesuai tahap berikutnya):

- Pemain, gerakan, collision, dan eksplorasi (Stage 02).
- NPC, interaksi objek, dan dialog (Stage 03).
- Clue dan casebook (Stage 04).
- Puzzle, quest, dan area unlock (Stage 05).
- Save/autosave/load dan tombol **Lanjutkan** yang aktif (Stage 06).
- Konten cerita kanonis, ending, dan aset final (Stage 07).

## Save

Belum ada sistem save pada Stage 01. Karena itu:

- Item **Lanjutkan** selalu nonaktif dan diberi label `TERKUNCI`.
- Memilih item tersebut menampilkan pesan bahwa simpan otomatis baru akan
  ditambahkan pada Stage 06.
- Belum ada file save yang dibuat atau dibaca. Lokasi file save akan ditetapkan
  dan didokumentasikan saat Stage 06 diimplementasikan.

## Pengujian yang dilakukan

Tanggal pengujian: **29 Oktober 2026**. Lingkungan: Windows, Processing 4.5.7
(portable), Java Mode, tampilan 1920x1080 dengan DPI 125%.

| Pengujian | Metode | Hasil |
|---|---|---|
| Kompilasi proyek | `Processing.exe cli --sketch=... --build` | **Lulus**, exit code 0, 9 file `.class` dihasilkan |
| Startup aplikasi | `Processing.exe cli --sketch=... --run` | **Lulus**, jendela `Silent Witch - Tahap 01` muncul tanpa exception |
| Render menu | Tangkapan jendela (PrintWindow) | **Lulus**, judul, subtitle, empat item, label `TERKUNCI`, dan footer tampil |
| Render panel Kontrol | Harness memanggil `Down`, `Down`, `Enter` | **Lulus**, panel Kontrol tampil |
| Tutup panel Kontrol | Harness memanggil `Esc` | **Lulus**, panel tertutup |
| Pesan Lanjutkan | Harness memanggil `Up`, `Enter` | **Lulus**, pesan "belum ada data" tampil |
| Pindah ke gameplay | Harness memanggil `Up`, `Enter` dari menu | **Lulus**, scene gameplay placeholder tampil |
| Kembali ke menu | Harness memanggil `Esc` saat gameplay | **Lulus**, menu utama tampil kembali |
| Klik Mulai Baru | Harness memanggil `mouseMoved`/`mousePressed` di koordinat layar item | **Lulus**, scene gameplay terbuka; konversi koordinat 3x benar |
| Klik Kembali ke Menu | Harness mengklik tombol pada koordinat layar | **Lulus**, menu utama terbuka |
| Klik Kontrol | Harness mengklik item Kontrol | **Lulus**, panel Kontrol terbuka |
| Klik Lanjutkan | Harness mengklik item Lanjutkan | **Lulus**, pesan "belum ada data" tampil |
| Skala pixel | Inspeksi tangkapan 960x540 | **Lulus**, tepi pixel tajam; tidak ada blur |
| Input keyboard/mouse fisik | Injeksi input OS | **Tidak teruji**: sesi CI tidak memiliki desktop interaktif yang menerima injeksi input. Logika input diuji lewat pemanggilan langsung pada harness (termasuk konversi koordinat mouse), bukan perangkat fisik |

Harness uji berada di folder sementara di luar proyek dan **tidak** disertakan
sebagai bagian game. Tidak ada klaim bahwa pengujian visual dilakukan dengan
klik/keyboard fisik.

## Status acceptance criteria

Status berikut hanya menilai kriteria yang relevan dengan Stage 01.

| Kriteria | Status | Catatan |
|---|---|---|
| AC-01 Build dan startup | **Pass** | Build exit code 0; menu tampil; README tersedia |
| AC-02 Movement dan collision | **Not Tested** | Dijadwalkan Stage 02; belum ada pemain |
| AC-03 Interaksi | **Not Tested** | Dijadwalkan Stage 03; belum ada NPC/objek interaktif |
| AC-04 Clue dan casebook | **Not Tested** | Dijadwalkan Stage 04 |
| AC-05 Puzzle | **Not Tested** | Dijadwalkan Stage 05 |
| AC-06 Quest dan unlock | **Not Tested** | Dijadwalkan Stage 05-06 |
| AC-07 Autosave dan load | **Not Tested** | Dijadwalkan Stage 06; **Lanjutkan** sengaja nonaktif |
| AC-08 Narasi dan logika | **N/A** | Belum ada konten cerita final yang diimplementasikan |
| AC-09 UI | **Partial** | Teks menu/panel/placeholder tidak terpotong; dialog dan puzzle belum ada |
| AC-10 Dokumentasi dan lisensi | **Pass** | README dan `ASSET_CREDITS.md` tersedia; tidak ada aset eksternal |

## Lisensi aset

Stage 01 tidak memakai aset eksternal. Seluruh visual dibuat secara prosedural di
kode dan font 5x7 dibuat manual. Lihat
[`data/licenses/ASSET_CREDITS.md`](data/licenses/ASSET_CREDITS.md).

## Keterbatasan yang diketahui

- Jendela berukuran tetap 960x540 dan belum ada mode fullscreen/resize. Layar
  harus mampu menampilkan jendela tersebut.
- Font 5x7 hanya memiliki bentuk huruf kapital; huruf kecil otomatis ditampilkan
  sebagai kapital.
- Belum ada audio, animasi karakter, atau efek transisi antar-scene.
- Belum ada save, sehingga **Lanjutkan** tetap terkunci.
- Pengujian input fisik tidak dapat dilakukan di sesi CI; lihat catatan pada
  bagian pengujian.

## Tahap berikutnya

Stage 02 - Player dan world: gerakan pemain, collision, dan satu area kecil yang
dapat dijelajahi. Jangan lanjut ke Stage 02 sebelum Stage 01 disetujui.
