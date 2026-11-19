# Changelog

Semua perubahan penting pada proyek ini dicatat di file ini.
Format mengikuti [Keep a Changelog](https://keepachangelog.com/) dan
[Semantic Versioning](https://semver.org/).

## [1.4.5] - 2026-11-19

### Added

- **Sistem Kabut Bergerak Dinamis (Moving Layered Fog) di Stage 3 Hollow Woods:**
  - 3 lapisan kabut mengalir dengan kecepatan dan paralaks kamera berbeda:
    1. Pita kabut tanah (*ground mist drift*, paralaks 0.75x) yang bergelombang dan mengalir lembut menyusuri lantai hutan.
    2. Gumpalan kabut atmosferik (*atmospheric wisp clouds*, paralaks 0.4x) yang melayang di udara.
    3. Kabut melayang cepat di latar depan (*foreground wisps*, paralaks 1.2x).
  - Memberikan kedalaman visual 2.5D gotik yang hidup tanpa menghalangi pandangan pertempuran atau telegraf serangan musuh.

### Fixed

- **Artefak Balok Rune Mengambang di Atas Sumur Tua Stage 1 (`TileMap.java`):**
  - Menghapus tile `'X'` (batu simbol rune) yang sempat tertimpa di koordinat sumur desa (kolom 20-21, baris 3-4) pada peta `ASHEN_VILLAGE` dan menggantinya dengan struktur padat `'V'`.
  - Sumur tua kini tampil bersih dan alami sesuai gambar latar `bg_ashen_village.png` tanpa artefak balok bercahaya, serta interaksi investigasi (`[E] PERIKSA`) dan batas fisik tetap berfungsi sempurna.
- **Pergerakan Musuh Pertama Bayangan Hutan (`BayanganHutan.java`):**
  - Menghilangkan bug osilasi maju-mundur / getar bolak-balik (*jitter oscillation*) akibat tumpang tindih orientasi hadap pemain dan batas patroli.
  - Musuh pertama kini berdiam bersiaga tenang di pos kabutnya (`State.PATROL` di titik anchor) tanpa mondar-mandir terus-menerus, dan baru akan maju melesat mengejar ketika karakter Dark Knight sudah mendekat.

### Changed

- **Penambahan Blok Kolisi Solid pada Batu-Batu & Reruntuhan Latar (`TileMap.java`):**
  - Seluruh formasi batu dan reruntuhan di Hollow Woods (`bg_hollow_woods.png`) kini dipetakan dengan tile solid (`'r'`, `'t'`, `'l'`), meliputi pagar kayu kiri, tangga batu datar, pilar batu kuno barat, pilar batu timur, bongkahan batu tengah, monumen salib gotik, tangga batu berundak, dan reruntuhan batu timur.
  - Karakter pemain maupun musuh tidak dapat lagi menembus formasi batu latar ("tidak tembus"), namun jalan utama di sepanjang panggung dan koridor menuju Batu Simbol tetap terbuka dan lancar.
- **Batasan Wilayah & Jarak Agresi Musuh Stage 3 (`BayanganHutan.java`, `PenjagaJejak.java`):**
  - **Maju Saat Karakter Sudah Dekat:** Musuh kini bersiaga di pos jaganya masing-masing dan hanya mulai maju menerjang (`State.CHASE`) apabila karakter pemain telah mendekat ke dalam perimeter kewaspadaannya (`alertRadius`), mencegah musuh mengejar dari kejauhan sebelum pemain tiba di area mereka.
  - **Batasan Wilayah (*Territory Leash Boundary*):** Apabila pemain mundur atau kabur melampaui batas wilayah pos jaga, musuh akan melepaskan kejaran dan kembali dengan tenang ke pos masing-masing tanpa mengejar tanpa batas.
  - **Deteksi Kolisi Musuh:** Gerakan musuh kini memeriksa tabrakan tile padat (`map.rectCollides`), memastikan musuh juga tidak menembus formasi batu saat mengejar maupun saat melakukan terjangan sabit (*lunge attack*).
- **Pembaruan Latar Penuh Stage 3 Hollow Woods (`bg_hollow_woods.png`, 640x256):**
  - Di-downscale secara presisi menggunakan `BackgroundBuilder` dengan filter rata-rata area subpixel dari gambar sumber gothic hutan berkabut (1986x792).
  - Menghilangkan artefak grid prosedural lama, menyatukan lengkungan batu barat, kanopi pohon pinus gotik, dan reruntuhan gerbang timur.
- **Penyelarasan Visual Musuh & Karakter dengan Background Baru:**
  - **Penjaga Jejak (`PenjagaJejak.java`):** Palet diselaraskan ke jubah ebony-teal gothic (`0xFF0E1822`, `0xFF182833`), zirah berkarat kuno (`0xFF253B47`), mata cyan mistis (`0xFF6BF7E8`), dan sabit bayangan raksasa. Efek fase murka (Phase 2) menampilkan kobaran api bayangan crimson-teal.
  - **Bayangan Hutan (`BayanganHutan.java`):** Diperbarui menjadi Forest Mist Wraith dengan siluet tudung kabut gelap dan mata cyan tajam yang serasi dengan nuansa Hollow Woods.
  - **Karakter Utama (Dark Knight):** Ditambahkan aura pendaran bulan perak-teal (`drawPlayerGlow`) saat berada di Hollow Woods sehingga siluet zirah ksatria tetap tegas dan atmosferik di tengah pepohonan gelap.
  - **Batu Simbol (`SymbolStone.java`):** Tekstur dan pendaran rune diselaraskan ke nuansa batu slate berlumut dengan ukiran rune bercahaya cyan.

## [1.4.4] - 2026-11-19

### Changed

- **Penggantian Sprite NPC Stage 2 (Aula Manor) dengan Lembar Sprite Sage Istana Gothic:**
  - `elder_front.png`, `elder_left.png`, `elder_back.png`, `elder_right.png`
    diganti penuh dari lembar sprite baru `Lembar Sprite Sage Istana Gothic.png`
    (1536x1024, 4 figur: depan, kanan, belakang, kiri).
  - **Pipeline Pemotongan & Penajaman Pixel Art:**
    - Flood-fill latar hitam dari tepi sel menjadi transparan tanpa merusak outline
      gelap dan lipatan jubah di dalam tubuh.
    - Penyelarasan batas Y (264..803, tinggi 540 px) agar posisi kepala dan kaki
      presisi dan sejajar sempurna di keempat arah saat menoleh.
    - Downscale area-weighted box filter presisi subpixel setinggi **28 px**
      (seimbang 1:1 dengan tinggi aktif Dark Knight).
    - Penajaman kontras outline, tongkat sihir/arsip, jubah gothic, dan detail
      wajah/janggut agar tampil tajam dan atmosferik di Aula Manor.
  - **Animasi Menoleh Interaktif (`lookAt`, `triggerTalkAnimation`, `goHome`):**
    - Saat pemain mengajak bicara dengan tombol 'E', Sage langsung menoleh
      menghadap pemain (arah kiri, kanan, atas, atau bawah sesuai posisi pemain)
      disertai efek hop/anggukan (*bob*).
    - Selama dialog berlangsung, ia tetap fokus menatap pemain.
    - Setelah dialog selesai atau pemain menjauh, Sage otomatis berputar kembali
      menghadap ke arah depan/bawah (`DIR_DOWN`).
  - Terverifikasi lewat test integrasi `TestSageManorHall`, `TestNpcTalkIntegration`,
    serta kompilasi penuh Processing CLI.

- **Penyesuaian Skala & Remaster Sprite NPC Prolog (Ashen Village) agar Jernih dan Proporsional:**
  - **Skala proporsional 25 px:** disesuaikan agar lebih ramping dan lebih pendek
    dari karakter utama (Dark Knight yang setinggi 28 px dan lebar 22-26 px
    berzirah tebal). Sprite cowok berukuran 13x25 px dan cewek 14x25 px.
  - **Pemindahan Saksi Desa (`SaksiDesa`) ke sebelah kiri Sumur Tua:**
    - Posisi dipindahkan dari (416, 128) ke (312, 86) di samping kiri sumur tua (330, 76).
    - Menghadap kanan (`DIR_RIGHT`) ke arah sumur saat berdiam, dan kembali
      menghadap sumur (`goHome()`) setelah selesai diajak bicara.
    - Menghaluskan radius interaksi menjadi 20 px agar spot interaksi Papan Pengumuman
      (296, 88), Saksi Desa (312, 86), dan Sumur Tua (330, 76) terpisah jelas
      dan nyaman diakses pemain.
    - Tilemap `ASHEN_VILLAGE` baris 5 kolom 19 diubah dari 'N' menjadi 'g'
      (rumput yang dapat dilewati) di antara papan pengumuman dan sumur.
    - Teks narasi dialog awal diperbarui dari *"di depan rumahnya"* menjadi
      *"di dekat sumur tua"*.
  - `boy_*` dan `girl_*` (8 arah) di-build ulang dari lembar sprite
    "Lembar Sprite Petualang Desa Bergaya Pixel" (1536x1024: 2 baris
    cowok/cewek x 4 kolom arah hadap) dengan pipeline penajaman pixel art:
    - **Menghilangkan color grading gelap/kusam** yang sebelumnya membuat warna
      menjadi cokelat lumpur dan menenggelamkan mata. Warna asli yang cerah
      (kulit peach hangat, kemeja putih linen, rambut kastanye kaya, mata gelap
      tegas) dipertahankan dengan rasio kontras melonjak dari 6.8x ke 94.5x.
    - **Filter area-weighted (box filter) presisi subpixel + penajaman kontras:**
      garis tepi dan mata hitam tetap tegas (bukan blur/smudge), sedangkan warna
      kulit dan pakaian bersih tanpa halo abu-abu.
  - **Dukungan resolusi dinamis pada `Art.drawAnimatedNpc()`:**
    tinggi render kini otomatis membaca tinggi native sprite (`sprites[0].height`),
    sehingga setiap piksel sprite digambar 1:1 tanpa ada scanline yang terbuang
    atau terdistorsi.
  - **Menambahkan 6 sprite arah baru:** `boy_left.png`, `boy_back.png`,
    `boy_right.png`, `girl_left.png`, `girl_back.png`, dan `girl_right.png`
    ke `data/art/`.
  - Seluruh rangkaian pengujian (unit, integrasi, regresi directional render,
    dan verifikasi in-game) berstatus **PASS**.

### Added

- **Util build sprite sheet `SheetTool.java`:** memotong lembar sprite 4 kolom x
  2 baris menjadi 8 sprite NPC 4-arah (`boy_*`/`girl_*`): mode `probe` (analisa
  warna & skala pixel art), `crop` (verifikasi arah hadap), dan `build`
  (flood-fill latar, trim sejajar, downscale pixel-art presisi area-weighted
  setinggi target yang diinginkan, penajaman outline/mata).

## [1.4.3] - 2026-11-19

### Added

- **Integrasi & Rebuild Sprite NPC dari `npc_talk` (`NpcSpriteBuilder.java`):**
  - Membuat utilitas build `NpcSpriteBuilder` untuk memproses seluruh sprite sumber (`boy_front.png`, `girl_front.png`, `elder_front.png`, `elder_left.png`, `elder_back.png`, `elder_right.png`) menjadi sprite pixel art tajam 1:1 tanpa efek buram/halo subpixel.
  - **Penyelarasan Warna (Color Grading) agar Menyatu dengan Latar Belakang:**
    - **Kakek Pustakawan (Aula Manor):** Rambut dan janggut di-grading menjadi perak kelabu sarjana bernuansa temaram lilin (menghilangkan putih neon siang hari), jubah diselaraskan ke beludru indigo/navy tua gothic aula manor, sulaman emas kusam antik, serta naskah kulit tua. Bagian bawah tubuh diberi gradasi falloff bayangan lantai.
    - **Warga Desa Pemuda & Gadis (Ashen Village):** Pakaian putih/linen (kemeja, celemek, tudung) di-grade menjadi linen tenun kusam malam berkabut, rompi dan gaun cokelat tanah pedesaan yang alami, serta kontur gelap yang serasi dengan paving batu dan palet Dark Knight.
  - Mengimplementasikan sistem animasi menoleh dinamis 4-arah (`lookAt`), efek lompat kecil (`bob`), serta logika mirror untuk sprite hadap samping pada `Art.drawAnimatedNpc()`.
  - Menambahkan saklar `Art.NPC_SPRITES = true` untuk rendering sprite NPC baru dengan fallback prosedural berkualitas bila aset tidak tersedia.
  - Menambahkan test suite regresi `TestNpcTalkIntegration.java` untuk memverifikasi arah 4-arah, timing animasi hop, serta alur interaksi bicara & kembali menghadap depan (`goHome()`).

### Changed

- **Penggantian NPC Manor Hall menjadi Kakek Pustakawan:**
  - NPC aula manor (`Npc.java`) diganti dari sosok penjaga upacara bersenjata menjadi Kakek Pustakawan (`"KAKEK PUSTAKAWAN"`).
  - Pustakawan secara otomatis menoleh ke arah pemain (bawah, kiri, atas, kanan) dengan animasi hop saat diajak bicara atau saat pemain menekan tombol `E`.
  - Saat dialog selesai atau pemain menutup dialog, pustakawan kembali menghadap depan secara natural.
- **Penggantian NPC Prolog Ashen Village:**
  - Tetua desa (`TetuaDesa.java`) kini menggunakan sprite pemuda warga desa (`boy_front.png`) dengan animasi responsif mirror dan hop saat diajak bicara.
  - Saksi desa (`SaksiDesa.java`) kini menggunakan sprite gadis warga desa (`girl_front.png`) dengan animasi responsif mirror dan hop saat diajak bicara.

## [1.4.2] - 2026-11-19

### Changed

- **Pemisahan jalur masuk gerbang Sayap Timur dan kotak pembuka (kait).**
  - Gerbang Sayap Timur berada di tangga kanan aula (baris 10-11, y = 176),
    lurus ke kanan dari area tengah tempat karakter berdiri.
  - Kotak pembuka (KotakBesi / kait pengunci) tetap di posisinya di kanan
    bawah (y = 212), terpisah dari pintu masuk.
  - Gambar latar Aula Manor (`bg_manor_hall.png`) tampil bersih tanpa sprite
    pintu overlay (`setDrawDoors(false)`), sehingga tidak ada kotak hitam
    atau pagar yang menutupi tangga dan dinding.
  - Setelah puzzle kotak selesai, pemain cukup berjalan lurus ke kanan
    menaiki tangga (x >= 388, y = 155..195) untuk masuk ke stage berikutnya.

### Removed

- Sprite gerbang besi / lorong gelap (`door_iron_open.png`) di sisi kiri
  peta Hollow Woods. Jalur keluar-masuk kini berupa jalan setapak tanah alami
  (`F`) tanpa bingkai pintu buatan.
- Penanda glow kuning di atas kait pengunci setelah puzzle selesai
  (`KotakBesi.draw`). Objek kait kini hanya menangani interaksi.

## [1.4.1] - 2026-11-18

### Added

- **Sprite pintu besi antar-area** (`data/art/door_iron.png` dan
  `door_iron_open.png`, 16x32) yang dibuat lewat util baru `DoorBuilder`.
  - Bergaya gotik gelap: lengkung batu, teralis besi vertikal, palang
    horizontal, dan plat kunci besi di tengah (tanpa zamrud, supaya tidak
    bentrok dengan kait pengunci Aula Manor).
  - Dua varian: tertutup (teralis utuh) dan terbuka (lorong gelap).
  - Bila berkasnya dihapus, gerbang kembali tanpa gambar tambahan (tidak
    error) karena pemuatan aset mengembalikan null.

### Changed

- **Masuk dari Aula Manor (stage 2) ke Hollow Woods (stage 3) kini lewat
  pintu besi.** Tile gerbang 'G' digambar memakai sprite baru; saat gerbang
  masih terkunci memakai `door_iron.png`, setelah kait pengunci terbuka
  berganti ke `door_iron_open.png`.
- Sprite pintu digambar terpisah lewat `TileMap.drawGates()` setelah seluruh
  tile selesai, supaya sprite setinggi 2 tile tidak tertimpa baris di bawahnya
  (termasuk saat peta memakai gambar latar).
- Ashen Village mematikan gambar pintu (`setDrawDoors(false)`) karena gambar
  latarnya sudah memuat lengkung gerbang sendiri.

### Removed

- **Aset gerbang prosedural lama** (tiang batu, batang terali, palang
  horizontal, dan gembok ungu `0xFF8E7CC3`) dihapus dari
  `TileMap.drawGate`. Tile gerbang sekarang hanya menggambar alas lantai
  sesuai area; bentuk gerbangnya berasal dari sprite pintu besi.

## [1.4.0] - 2026-11-14

### Changed

- **Latar Ashen Village (area awal game) memakai gambar sungguhan.**
  - `data/art/bg_ashen_village.png` (640x256) dibangun dari render besar 1983x793
    lewat util baru BackgroundBuilder (penyalinan rata-rata area supaya
    detail tetap terbaca sebagai pixel art).
  - `TileMap` sekarang mendukung gambar latar penuh peta: kalau ada, gambar itu
    menggantikan render tile lantai/dinding (rumput, jalan, bangunan) dan hanya
    tile objek yang digambar di atasnya. Interior objek interaktif
    (papan pengumuman, sumur, pintu) tetap interaktif seperti sebelumnya.
  - Fallback aman: bila berkasnya hilang, peta kembali ke renderer tile
    prosedural.
  - Util `BackgroundBuilder` (CLI) ditambahkan di root sketch untuk menyiapkan
    latar area berikutnya.
- **Batas collision Ashen Village digambar ulang mengikuti gambar baru.**
  - Bangunan atas dan bawah kini solid penuh (tidak bisa dimasuki), mengikuti
    kaki bangunan pada gambar; hanya pintu yang bisa dimasuki.
  - Lubang/lintasan di bawah gerbang kanan (kol 37-39, baris 6-9) dibuat bisa
    dilalui, jadi pintu masuk manor terlihat seperti Candi lewat lengkung
    gerbang, bukan tembok.
  - Gerbang desa (tile 'G') sekarang terkunci sampai prologue selesai (papan
    pengumuman dibaca + bicara dengan tetua desa). Setelah terbuka, pemain
    bisa berjalan ke dalam lengkung dan otomatis masuk ke Aula Manor.
  - Posisi objek interaktif digeser mengikuti gambar: papan pengumuman (296,88),
    gerbang (568,128), sumur (330,76), pintu rumah warga (104,68).
- **Latar Aula Manor (The Silent Manor Hall) memakai gambar sungguhan.**
  - `data/art/bg_manor_hall.png` (416x256) dibangun dari render "Aula Kastel
    dengan Tuas Rantai" (webp 1024x572, dikonversi ke PNG) lewat util baru
    `PixelScaler`.
  - `PixelScaler` memotong sumber ke rasio aspek peta dulu (supaya gambar tidak
    gepeng) lalu menurunkan ukuran dengan mode-color per blok, sehingga
    pixel art tetap tajam dan tidak memburam seperti penskalaan rata-rata.
  - Layout collision disesuaikan: dinding atas 5 baris solid, dua rak buku
    solid, area karpet/lantai terbuka, gerbang turun ke kanan bawah
    (tempat kait rantai pengunci pintu pada gambar).
  - Petunjuk/interaktif disesuaikan dengan gambar baru: RAK BUKU (rak kiri),
    PUING JENDELA (kaca pecah di tengah), KAIT RANTAI (kait di kanan bawah).
    NPC penjaga dan Kait Pengunci digeser ke posisi yang bisa dijangkau.
- **Aset gerbang & kotak besi Aula Manor dihapus, diganti aset gambar.**
  - Aset gerbang prosedural (terali besi + cahaya kuning) dihapus dari
    `TileMap.drawGate` dan `GerbangTimur.draw()`; gerbang kini memakai rantai
    dan tangga di ujung kanan gambar latar. Flag `overlayGate` dihapus.
  - Aset peti besi prosedural di `KotakBesi.draw()` dihapus; objeknya memakai
    kait pengunci beruas zamrud di ujung kanan bawah gambar (hanya kilau emas
    penanda saat sudah terbuka).
  - Kode internal "Kotak Besi" diubah jadi "Kait Pengunci" (dialog, prompt,
    judul puzzle, quest objective, dan teks penutup).
  - Menzez puzzle Kait Pengunci kini otomatis membuka gerbang sayap timur.
- **Perbaikan navigasi & collision Aula Manor.**
  - Rak buku kini menutup sampai baris 8 (tidak bisa dimasuki/dilewati).
  - Lorong kanan (kol 24) hanya bisa dilewati lewat baris gerbang 13-14.
  - Gerbang pindah ke ujung kanan bawah (kol 24) dan trigger transisi
    disesuaikan ke `x >= 388`.
  - Pesan transisi kembali dari Hollow Woods dan Sayap Timur diseragamkan
    menjadi "KEMBALI KE AULA MANOR".

---

## [1.3.0] - 2026-11-12

### Changed

- **Tokoh utama memakai paket "Dark Knight" (`knight_game_assets`).** Seluruh
  aset tokoh dari paket itu sekarang dipakai di game:
  - Sheet badan `data/art/hero_knight_body.png` (9 pose: idle/walk/run ×
    down/up/samping) dibangun dari folder `player/` paket lewat
    `SpriteSheetBuilder` (tinggi 28 px, skala 0,174).
  - **Arah hadap diperbaiki:** art `_left` dan `_right` pada paket ternyata sama
    sama menghadap kiri, sehingga tokoh terlihat "jalan mundur" saat bergerak
    ke kanan. Sekarang hanya art samping menghadap kiri yang dipakai
    dan dicerminkan otomatis saat pemain menghadap kanan (sama seperti
    pendekatan Golden Knight sebelumnya).
  - Sheet tebasan `data/art/hero_knight_attack.png` (5 frame) dibangun dari
    folder `attack/` (tinggi 26 px supaya ukuran tokohnya sama dengan sheet
    badan). Frame tebasan pada paket menghadap kiri, jadi dicerminkan otomatis
    saat pemain menghadap kanan.
  - Potret `data/art/hero_knight_portrait.png` dipakai di pause menu, di sebelah
    nama tokoh.
  - Paket ini menyediakan 1 pose per arah, jadi gerakan hidup disiasati dengan
    lift 1 piksel seperti contoh implementasi pada paket aslinya.
  - Busur tebasan prosedural (`Art.drawSwordSlash`) dihapus karena frame tebasan
    sudah membawa efek sabit; sheet Golden Knight lama (`hero_walk.png`,
    `hero_sword.png`) dan `knight_idle.png`/`knight_walk.png` yang tidak terpakai
    ikut dibersihkan.
  - Durasi tebasan `Player.ATTACK_DURATION` dinaikkan dari 0,18 s ke 0,24 s agar
    5 frame tebasan terbaca; hitbox tetap dihitung saat tebasan dimulai.
  - **Kaki tidak lagi melayang:** sel sprite punya 2 piksel bantalan bawah, jadi
    sprite digambar 1 piksel lebih bawah supaya alas kaki benar-benar menyentuh
    garis tanah (sebelumnya ada celah 1 piksel). Bayangan dipersempit dari 18
    ke 16 piksel agar tidak terbaca sebagai panggung gelap.
  - **Langkah kaki saat berjalan:** paket hanya menyediakan 1 pose per arah,
    jadi dibuat gait 2 frame sendiri - pose "jalan" dan "lari" selang-seling
    (8 langkah/detik) dengan badan terangkat 1 piksel saat kaki melewati.
    Percobaan menggeser baris kaki sprite secara horizontal dibatalkan karena
    membuat kaki terlihat gemuk dan bagian bawah art rusak.
  - **Arah tebasan dikunci:** animasi tebasan sekarang memakai arah saat F/J
    ditekan (`Player.attackFacing`), bukan arah badan saat ini. Sebelumnya
    visual ikut berputar saat pemain belok sehingga tebasan terlihat seperti
    memukul ke belakang/samping padahal hitbox tetap di arah awal.
  - **Efek tebasan selalu di depan tokoh:** art tebasan pada paket menyimpan
    jejak sabit di sisi berlawanan dengan arah tubuh, jadi efek utama (busur
    sabit) digambar sendiri secara prosedural di sisi depan sesuai arah
    tebasan (kanan/kiri/atas/bawah). Jejak bawaan paket tetap dipakai sebagai
    efek ekor ayunan.
  - `background/dungeon_scene.png` dari paket belum dipakai (bukan aset tokoh).

---

## [1.2.0] - 2026-11-12

### Changed

- **Tokoh utama dinamai "SILENT VERDICT".** Nama baku tokoh memakai konstanta
  `GameManager.HERO_NAME_DEFAULT` dan dipakai di menu awal (nama bawaan saat
  pemain tidak mengetik apa pun), HUD/pause menu, serta fallback file save.
  Nama yang diketik pemain tetap disimpan dan ditampilkan sebagai
  "SILENT VERDICT: <nama>" (save lama bertuliskan "PENYIHIR" tetap terbaca).
- **Pedang pindah ke belakang punggung saat tidak menyerang.**
  - Sprite badan sekarang tanpa pedang (`data/art/hero_walk.png`, 18 frame, dari
    folder "Golden Knight walk"), sheet lama `hero_idle.png` (dengan pedang) dihapus.
  - Pedang diekstrak terpisah dari frame "stand with sword idle" menjadi
    `data/art/hero_sword.png` (1 frame) lalu digambar di belakang badan saat
    idle/jalan, sehingga gagang dan bilahnya terlihat di sisi belakang.
  - Saat F/J/klik kanan ditekan, pedang diayunkan di depan tubuh mengikuti busur
    tebasan dan arah hadap (kanan/kiri/atas/bawah), dengan progres ayunan 0..1
    selama 0,18 detik.
  - Efek tebasan sabit tetap dipertahankan dan sekarang searah dengan ayunan pedang.

### Fixed

- Sheet badan dan sprite pedang memakai jangkar yang sama sehingga karakter
  tidak melompat horizontal saat berpindah antara idle, jalan, dan menyerang.

---

## [1.1.2] - 2026-11-12

### Fixed

- **Sprite Golden Knight kini muncul saat game dijalankan dari Processing IDE.**
  Penyebabnya: Processing IDE mengompilasi dan menjalankan sketch dari folder build
  sementara di `%TEMP%\processing\...` yang **tidak** memuat folder `data/`, sedangkan
  `Art` mencari `data/art` relatif terhadap folder kerja. Akibatnya sheet karakter
  utama gagal dimuat dan pemain jatuh ke sprite penyihir prosedural.
  - `Art.attach(PApplet)` baru: lokasi folder sketch diambil dari `app.sketchPath()`
    (Processing menerimanya lewat argumen `--sketch-path`), lalu `Art.dataRoot()`
    mencari aset dengan urutan: folder sketch -> properti `sketch.path` -> folder kerja
    beserta induknya -> folder tempat class berada beserta induknya.
  - Dipanggil dari `setup()` pada `SilentWitch.pde` dan dari konstruktor `GameManager`,
    sebelum scene pertama digambar.
  - Tetap aman: bila sheet tidak ditemukan, pemain kembali ke penyihir prosedural.

---

## [1.1.1] - 2026-11-11

### Changed

- **Karakter utama memakai sprite Golden Knight (paket "Asset Terbaru"):**
  - Sheet baru `data/art/hero_idle.png` (12 frame) dan `hero_walk.png` (18 frame) dibangun ulang dari render 900x500 lewat `SpriteSheetBuilder`.
  - Sheet lama `knight_idle.png`/`knight_walk.png` tidak dipakai lagi: sel 48x32 membuat pedang terpotong tepat di batas sel (x=47).
  - Tinggi karakter dinormalkan ke 24 px (setara NPC/penyihir lama), badan dipusatkan, kaki menempel di dasar sel, jadi tidak ada lagi kesan "gumpalan cahaya kuning".
  - Arah hadap: kanan = default, kiri = dicerminkan, atas = digelapkan sebagai punggung.
  - Busur tebasan diganti sabit melingkar yang mengikuti 4 arah hadap (sebelumnya batang kuning datar) dan ikon pedang tambahan dihapus karena spritenya sudah memegang pedang.

### Fixed

- Lapisan tile berbasis gambar (`Art.ENABLED`) dimatikan. Lantai dan dinding sebelumnya memakai potongan tekstur yang sama, sehingga latar desa/hutan jadi bintik dan kapel/arsip jadi tembok penuh. Latar kembali ke renderer prosedural yang kontrasnya jelas.
- `Art.drawHero` tidak lagi bergantung pada `erodaxia.png`; sprite karakter utama dimuat terpisah lewat saklar `Art.HERO_SPRITE`.

---

## [1.1.0] - 2026-11-07

### Added

- **Sistem Pertarungan Real-Time 2D & Senjata Saku Pemain:**
  - Pemain selalu membawa senjata saku ("Tongkat Perak Saku") di saku mantelnya dengan indikator visual prosedural pixel art.
  - Serangan tebasan sihir sabit 4 arah via **Klik Kanan Mouse** atau tombol keyboard alternatif **F** / **J**.
  - Hitbox serangan akurat sesuai arah hadap (UP, DOWN, LEFT, RIGHT) dengan efek kilau busur sihir violet-putih dan percikan energi emas.
  - Status pertarungan pemain pada `Player.java`: `hp` (6 HP / 3 hati gothic), cooldown serang, i-frames (invulnerability blink pasca-kena serang), dan peluruhan knockback halus.
- **Hierarki Kelas Musuh & Finite State Machine (`Enemy.java`):**
  - Mesin status: `PATROL` -> `CHASE` -> `TELEGRAPH` -> `ATTACK` -> `RECOVERY` -> `HURT` -> `DEFEATED`.
  - Indikator telegraf visual bahaya pada lantai arena sebelum serangan aktif untuk memberi sinyal bahaya yang adil dan terbaca.
  - Jeda rentan (recovery) saat senjata/cakar musuh tertancap di tanah untuk memberikan kesempatan pemain melakukan serangan balasan.
- **Musuh Reguler Hollow Woods (`BayanganHutan.java`):**
  - Monster kabut bayangan berpatroli dengan 3 HP, mengejar pemain, dan menebaskan cakar bayangan bertelegraf merah.
- **Mini-Boss Hollow Woods (`PenjagaJejak.java`):**
  - Penjaga jalur lembah berstatus bos dengan 10 HP, siluet tudung berpelat besi kuno, dan sabit bayangan raksasa.
  - Memiliki 2 Fase:
    - *Fase 1 (HP > 5):* Serangan sapuan sabit bayangan lebar (Cleave) dengan jeda pemulihan 0.85s.
    - *Fase 2 (HP <= 5):* Murka bayangan dengan aura ungu membara, damage meningkat, serta kombinasi berseling antara sapuan sabit dan terjangan lurus (Lunge).
- **Progres Deterministik Tanpa RNG:**
  - Mengalahkan Penjaga Jejak memberikan **Sigil Jejak** secara pasti dan otomatis membuyarkan kabut tebal Gerbang Timur Hutan (`GerbangHutan`) menuju Lembah.
- **Proteksi Modal Menyeluruh:**
  - Serangan dan interaksi pertempuran terkunci saat dialog NPC, buku kasus, teka-teki kotak besi, altar deduksi, atau pause menu aktif.
- **Mekanik Kalah & Respawn Aman:**
  - Pemain yang kehabisan HP mengalami transisi kabut dan terbangun kembali di titik awal area dengan HP penuh, tanpa kehilangan bukti (clue), quest, atau status gerbang yang telah terbuka.
- **HUD Pertarungan & Bar Bos:**
  - Tampilan 3 hati gothic di bawah header kiri bersama status kesiapan senjata saku `[R-Click/F]`.
  - Bar HP bos orisinal di bagian atas tengah layar saat bertarung melawan Penjaga Jejak di Hollow Woods.
- **Suite Pengujian Otomatis Pertarungan (`CombatSystemTest.java`):**
  - Memverifikasi 7 aspek pertempuran: senjata saku & hitbox 4 arah, siklus FSM musuh, 2 fase bos & hadiah Sigil Jejak, damage & i-frames, proteksi modal, respawn tanpa data loss, dan serialisasi save/load status pertempuran ke disk.

---

## [1.0.0] - 2026-11-02

### Added

- Dokumen Kanon Resmi `Silent_Witch_Canon.md` sesuai PRD Seksi 2, 6.2, dan 20:
  - Penetapan identitas korban: Clara Thorne (15 tahun).
  - Penetapan identitas pelaku: Garrick (Penjaga Malam Aula).
  - Penetapan motif final: Persekongkolan dan keserakahan menjarah relik Sayap Timur bersama kultus bayangan.
  - Kronologi kanonis kejadian dan analisis kontradiksi bukti fisik.
- Area baru **Sayap Timur (Arsip & Altar Rekonstruksi)** pada `TileMap.java`:
  - Layout tile 26x16 dengan render prosedural batu altar kuno (`'A'`), meja memorial Clara Thorne (`'M'`), rak arsip mahoni, pilar batu, karpet beludru, dan lorong barat.
  - Metode instansiasi `TileMap.manorEastWing()`.
- Objek interaktif baru di Sayap Timur:
  - `AltarDeduksi`: Altar batu kuno tempat pemain menyusun deduksi akhir.
  - `GerbangBarat`: Lorong dua arah untuk kembali ke Aula Manor.
  - Titik periksa `Memorial Clara Thorne` dengan potret, bunga putih, dan catatan wasiat.
  - Titik periksa `Arsip Kuno Sayap Timur` dengan kronik sejarah pelindung desa dan segel relik.
- Sistem Rekonstruksi Deduksi Kasus (`DeductionSystem.java`):
  - Antarmuka modal interaktif 300x164 dengan 3 langkah deduksi kanonis.
  - Navigasi penuh via keyboard (W/S, panah, angka 1-3, Enter, Esc) dan mouse.
  - Evaluasi deduksi logis terhadap bukti yang ditemukan menghasilkan 3 rute ending.
- Layar Hasil Investigasi & Ending (`EndingScene.java`):
  - **Ending I (The Truth / True Ending):** Narasi kebenaran, pembongkaran alibi Garrick, pembersihan nama Clara Thorne, dan pulihnya ketenangan desa.
  - **Ending II (The Unfinished Case / Partial Ending):** Narasi investigasi menggantung bila bukti kurang dari 4, Garrick kabur tanpa kepastian hukum.
  - **Ending III (The False Accusation / Bad Deduction):** Narasi kegagalan bila deduksi salah, pelaku sebenarnya bebas, dan relik jatuh ke tangan kultus.
  - Rangkuman statistik bukti, status kotak besi, dan putusan investigasi.
- **Pause Menu (PRD 11.1 Layar minimum point 6)** pada `GameplayScene.java`:
  - Tombol ESC membuka panel jeda terpusat (Resume, Pengaturan Cepat Tampilan 960x540 skala 3x, Kembali ke Menu Utama).
  - Dialog konfirmasi aman sebelum kembali ke menu dengan penyimpanan progres otomatis.
- Dukungan Multi-Map pada `Player.java`:
  - Metode `setMap(TileMap)` dan `clearBlockers()` untuk pergantian area yang mulus.
- Serialisasi SaveData multi-area yang menyimpan dan memulihkan status area `the_silent_manor_east_wing` secara transparan.

### Changed

- Status Acceptance Criteria **AC-08 (Narasi dan logika)** dinaikkan menjadi **Pass**.
- Seluruh 10 Acceptance Criteria (**AC-01** sampai **AC-10**) kini berstatus **Pass**.
- Versi proyek dan sketch ditingkatkan menjadi **Stage 07 v1.0.0**.
- Prompt `GerbangTimur` saat terbuka diperbarui menjadi `"MASUK SAYAP TIMUR"`.
- `GameManager` kini memiliki metode `changeScene(Scene)` untuk transisi langsung ke `EndingScene`.

---

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
