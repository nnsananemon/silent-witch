# Changelog

Semua perubahan penting pada proyek ini dicatat di file ini.
Format mengikuti [Keep a Changelog](https://keepachangelog.com/) dan
[Semantic Versioning](https://semver.org/).

## [0.6.0] - 2026-11-02

### Added

- `SaveData` sebagai model serialisasi/deserialisasi status permainan ke format JSON berversi skema (`CURRENT_SCHEMA_VERSION = 1`):
  - `ObjectiveSave`: serialisasi sub-tujuan quest (ID, jumlah count, status selesai).
  - `QuestSave`: serialisasi quest dan seluruh sub-tujuannya.
  - `PuzzleSave`: serialisasi teka-teki logika (status solved, salah percobaan, tingkat petunjuk aktif).
  - Serialisasi koordinat pemain ($x, y$), arah hadap (`Facing`), ID area, daftar bukti yang ditemukan, quest aktif, dan status flag dunia (`worldFlags`).
  - Validasi ketat terhadap versi skema asing dan kelengkapan field utama.
- `SaveManager` sebagai pengelola berkas simpanan aman (NFR-06 / AC-07):
  - Penulisan atomik via berkas temporer (`.tmp`) dengan mekanisme move/replace untuk mencegah kerusakan data berkas utama.
  - Pemisahan hasil operasi: `SaveResult` dan `LoadResult` dengan status `SUCCESS`, `NO_SAVE`, dan `CORRUPT`.
  - Penanganan save rusak/cacat secara diagnostik tanpa menyebabkan crash aplikasi, dan **tidak pernah menghapus file save asli yang rusak**.
- Mekanisme autosave otomatis pada `GameplayScene` untuk setiap tonggak capaian investigasi:
  - Saat bukti baru aula ditemukan (`clue`).
  - Saat teka-teki Kotak Besi berhasil dipecahkan (`puzzle`).
  - Saat Gerbang Sayap Timur dibuka (`gate`).
  - Saat pemain keluar kembali ke menu utama melalui tombol ESC (`scene exit`).
- Indikator halus `[AUTOSAVE OK]` pada header HUD virtual saat progres berhasil disimpan.
- Dialog modal konfirmasi peringatan di `MainMenuScene` saat pemain memilih **MULAI BARU** jika data simpanan lama sudah ada, mencegah hilangnya progres pemain secara tidak disengaja.
- Tombol **LANJUTKAN** pada menu utama kini aktif secara dinamis:
  - Menampilkan tag `[TERSEDIA]` bila berkas save valid ditemukan dan langsung melanjutkan permainan dengan pemulihan state utuh.
  - Menampilkan tag `[RUSAK]` bila data tidak valid tanpa menghapus berkas di disk.
- Metode pemulihan state:
  - `Player.setPosition(x, y)` dan `Player.setFacing(facing)`.
  - `InvestigationSystem.restoreDiscovered(clueId)` dan `InvestigationSystem.allDiscoveredIds()`.
  - `Puzzle.restoreState(solved, wrongAttempts, hintLevel)`.
  - `QuestSystem.restoreQuestState(questId, status, objectives)`.
  - `GerbangTimur.restoreOpen(boolean open)` untuk pemulihan status gerbang dan pelepasan rintangan gerak.

### Changed

- Judul jendela Processing dan indikator HUD dinaikkan menjadi "Tahap 06".
- `GameManager` kini mengelola instans `SaveManager` dan menyediakan metode `startNewGame()` dan `continueGame()`.
- `GameplayScene` menyediakan konstruktor berparameter `SaveData` untuk pemulihan permainan (`restoreFrom(SaveData)`).
- Acceptance Criteria **AC-07 (Autosave dan load)** ditingkatkan statusnya menjadi **Pass**.
- Dokumentasi `README.md` diperbarui dengan penjelasan lokasi file simpanan, skema data JSON, keselamatan berkas, dan laporan uji menyeluruh.

### Notes

- Berkas save secara default berlokasi di `save/savegame.json` relatif terhadap root sketch Processing.
- Tetap tidak memakai dependensi eksternal: seluruh parsing data memakai `processing.data.JSONObject` bawaan engine.

## [0.5.0] - 2026-11-02

### Added

- `QuestObjective` sebagai model sub-tujuan spesifik dengan pelacakan progres bernomor dan status penyelesaian.
- `Quest` sebagai model data quest investigasi (ID stabil, judul, deskripsi, prasyarat bukti/quest, status `LOCKED`/`ACTIVE`/`COMPLETED`, deskripsi reward, dan kumpulan sub-tujuan).
- `QuestSystem` untuk pendaftaran quest, aktivasi, pembaruan progres otomatis via event hook (penemuan bukti, pemecahan teka-teki, dan pembukaan gerbang), notifikasi toast progres, serta ringkasan HUD.
- Quest aktif investigasi aula: `"MISTERI AULA UTARA"` (`quest_investigasi_aula`) dengan tiga sub-tujuan berurutan:
  - `obj_kumpulkan_bukti`: Kumpulkan 4 bukti aula (0/4 -> 4/4).
  - `obj_pecahkan_puzzle`: Pecahkan teka-teki Kotak Besi Aula (0/1 -> 1/1).
  - `obj_buka_gerbang`: Buka Gerbang Sayap Timur aula (0/1 -> 1/1).
- `Puzzle` sebagai model data teka-teki logika deduksi kondisi (PRD Seksi 5 & jenis 5.1 type 2):
  - Solusi eksplisit kombinasi 3 dial angka (`2 - 4 - 9`).
  - Validasi aturan kondisi numerik (digit I = prima genap terkecil, digit II = 2 x digit I, digit III = kuadrat 3, total = 15).
  - Sistem petunjuk bertingkat 3 level (PRD 5.3) yang progresif dan tidak merusak atau mengubah jawaban.
  - Umpan balik diagnostik edukatif saat jawaban salah tanpa merusak atau mereset progres (AC-05).
  - Perekaman status penyelesaian permanen dan penjelasan logika saat berhasil dipecahkan.
- `PuzzleSystem` sebagai antarmuka modal interaktif 300x164 pada kanvas virtual 320x180:
  - Kotak petunjuk plakat plakat kuno, 3 kotak dial angka besar (skala 2), tombol panah atas/bawah, tombol aksi `[ENTER] BUKA`, `[H] PETUNJUK (X/3)`, `[ESC] KEMBALI`, dan tombol `[X]`.
  - Sub-panel feedback diagnostik responsif dengan kode warna kontras.
  - Kontrol navigasi keyboard: `A`/`D` atau panah kiri/kanan untuk memilih dial, `W`/`S` atau panah atas/bawah untuk memutar angka (wrap-around 0-9), pengetikan digit angka langsung `0`..`9`, `Enter`/`Spasi` untuk menguji, `H`/`T` untuk meminta petunjuk, dan `Esc` untuk menutup.
  - Kontrol mouse lengkap: klik langsung pada dial, panah atas/bawah, tombol aksi, tombol `[X]`, dan klik di luar panel untuk menutup modal.
  - Penguncian gerakan pemain di dunia selama teka-teki terbuka.
- `KotakBesi` sebagai objek interaktif aula yang mengendalikan pembukaan teka-teki kotak besi:
  - Sprite prosedural peti besi kuno (16x11 px) dengan kait gembok bercahaya sihir dan bayangan lantai.
  - Saat belum terpecahkan: prompt `[E] BUKA KOTAK` dan interaksi membuka modal `PuzzleSystem`.
  - Saat terpecahkan: tutup peti sedikit terbuka dengan kilau emas, prompt berganti `[E] PERIKSA KOTAK`, dan menampilkan dialog status terbuka.
- `GerbangTimur` sebagai objek interaktif pembatas area Sayap Timur di dinding timur aula:
  - Terkunci rapat solid sebelum teka-teki kotak besi dipecahkan (AC-06).
  - Interaksi saat kotak belum terbuka memberikan dialog petunjuk bahwa kait terhubung ke kotak besi.
  - Setelah kotak besi terbuka, interaksi dengan gerbang melepaskan rantai kunci, mengubah tile map menjadi lorong terbuka, menghapus rintangan gerak pemain, memicu kilau lentera, dan menyelesaikan sub-tujuan quest terakhir.
- Integrasi gerbang `'G'` pada `TileMap` (solid saat tertutup dengan gambar kisi besi gembok ungu; passable saat terbuka dengan lorong bercahaya hangat emas).
- Metode `removeBlocker(x, y)` pada `Player` untuk melepaskan rintangan gerak secara dinamis saat pintu/gerbang dibuka.
- Glyphs `'^'` pada `PixelFont` untuk simbol tombol navigasi dial.

### Changed

- Judul jendela Processing dan indikator tahap diubah menjadi "Tahap 05".
- Header HUD disederhanakan (`AULA MANOR` di kiri, `BUKTI: X/4  QUEST: X/3  TAHAP 05` di kanan) untuk mencegah tumpang tindih teks pada layar 320x180.
- `GameplayScene` kini mengintegrasikan pemain, NPC, titik periksa bukti, modal casebook, kotak besi, puzzle system, quest system, dan gerbang timur.
- Menu utama diperbarui dengan ringkasan kontrol puzzle pada panel Kontrol.
- Dokumentasi `README.md` dan acceptance criteria diperbarui ke status Stage 05 (AC-05 & AC-06: Pass).

### Notes

- Seluruh mekanisme teka-teki dan quest tidak mengarang fakta kanon terlarang sesuai batasan PRD Seksi 20.
- Tombol Lanjutkan masih terkunci sampai Stage 06 (Save/Load).
- Tidak ada library atau file aset eksternal yang ditambahkan.

## [0.4.0] - 2026-11-02

### Added

- `Clue` sebagai model data bukti investigasi (ID stabil, judul, deskripsi, sumber,
  kondisi penemuan, relasi clue/quest, linimasa waktu, dan tingkat keandalan/reliability).
- `InvestigationSystem` untuk pendaftaran clue, pencatatan bukti, pencegahan duplikasi,
  notifikasi toast penemuan bukti baru, dan antarmuka Detective Casebook modal.
- Antarmuka Detective Casebook dengan dua tab navigasi:
  - Tab 1: **[1] DAFTAR BUKTI** (slot daftar bukti dengan masking non-spoiler untuk clue yang
    belum ditemukan, rincian metadata sumber, waktu, temuan, deskripsi, dan bukti terkait).
  - Tab 2: **[2] KRONOLOGI & HIPOTESIS** (urutan waktu peristiwa aula dan status hipotesis
    investigasi awal yang otomatis tervalidasi saat seluruh 4 bukti terkumpul).
- Notifikasi toast mengambang `BUKTI BARU TERCATAT: [JUDUL] (TEKAN C)` berdurasi 3,5 detik.
- Pintasan keyboard `C`, `Tab`, dan `J` untuk membuka/menutup buku kasus dari gameplay.
- Dukungan interaksi mouse pada Casebook: tombol tab, tombol tutup `[X]`, pemilihan slot bukti,
  dan klik di luar panel untuk menutup modal.
- Penguncian gerakan pemain selama buku kasus terbuka agar tidak meluncur di belakang panel.
- Integrasi `interact(InvestigationSystem)` pada `Interactable`, `ExamineSpot`, dan `Npc`:
  pencatatan otomatis saat pertama kali diinteraksikan, dialog temuan, dan dialog pengingat
  pada interaksi berulang.
- Empat bukti investigasi aula yang saling terhubung:
  - `clue_rak_harian`: Buku Harian Usang (Rak Buku, linimasa 3 Hari Lalu, Dokumen).
  - `clue_meja_catatan`: Sobekan Catatan Aula (Meja Aula, linimasa H-1 Malam, Bukti Fisik).
  - `clue_penjaga_kesaksian`: Kesaksian Penjaga Aula (Penjaga Aula, linimasa 23:30 Malam, Kesaksian).
  - `clue_jendela_goresan`: Bekas Goresan Kusen (Jendela Utara, linimasa Malam Kasus, Lingkungan).
- Header HUD menampilkan penghitung bukti yang ditemukan: `BUKTI: X/4`.
- Bantuan kontrol menu utama diperbarui dengan tombol pembuka buku kasus (`C / TAB / J`).

### Changed

- Judul jendela dan label tahap diubah menjadi "Tahap 04".
- `GameplayScene` kini mengintegrasikan sistem investigasi dengan 4 bukti aula.
- Dialog objek dan NPC kini memberikan informasi bukti nyata tanpa menambah fakta kanon terlarang.
- README dan dokumentasi acceptance criteria diperbarui untuk Stage 04.

### Notes

- Seluruh bukti dan dialog dirancang konsisten dan non-spoiler tanpa mengarang identitas
  korban, pelaku, atau motif final (sesuai batasan PRD Seksi 20).
- Tombol Lanjutkan masih terkunci sampai Stage 06 (Save/Load).
- Tidak ada aset eksternal yang digunakan.

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
