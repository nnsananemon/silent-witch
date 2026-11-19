# SILENT WITCH — Revisi Alur Cerita & Sistem Pertarungan

**Status:** Usulan addendum untuk PRD v2.0 dan Dokumen Kanon Resmi v1.0  
**Tujuan:** Memperpanjang permainan dengan alur bertahap, variasi gameplay, dan pertarungan yang mendukung misteri—tanpa mengubah identitas pelaku, motif, kronologi, clue, atau ending yang sudah ditetapkan.

---

## 1. Arah Desain

Silent Witch diarahkan menjadi **2D side-scrolling gothic mystery adventure**: dunia saling terhubung, jalur bercabang dan rahasia tersembunyi, suasana sunyi-melankolis, serta pertarungan boss yang punya pola dan fase. Referensinya adalah rasa eksplorasi dan intensitas pertarungan dari game metroidvania gothic, tetapi Silent Witch harus punya identitas sendiri: penyihir penyelidik, Ashen Village, arsip manor, simbol-simbol, dan kasus Clara. Jangan menyalin karakter, desain musuh, peta, lore, animasi, atau UI dari game lain.

Pertarungan bukan cara untuk menemukan siapa pelaku; jawaban kasus tetap harus dibuktikan lewat clue, kesaksian, kronologi, dan deduksi. Combat menjaga ritme petualangan dan memberi tantangan fisik, sementara investigasi tetap menjadi pembeda utama Silent Witch.

### Arah suasana dan latar

- Pakai kamera **side-view 2D** dan ruang-ruang yang terhubung: desa berkabut, aula/manor yang terasa sempit dan berhantu, hutan vertikal, lembah dalam, serta reruntuhan simbolik.
- Bangun nuansa sunyi lewat ruang kosong, kabut berlapis, cahaya lentera, siluet jauh, dan ambience yang berubah saat ancaman mendekat. Jangan isi setiap layar dengan musuh atau efek.
- Boss fight harus terasa seperti peristiwa: arena punya komposisi dan musik/ambience sendiri, kemunculan boss diberi jeda dramatis, dan perubahan fase terlihat pada arena atau pola serangan.
- Jaga identitas visual original: desain berdasarkan sihir sunyi, relik, arsip, dan simbol Ashen Village; hindari meniru bentuk karakter, musuh, lokasi, UI, atau lore game referensi.

**Pilar pengalaman yang disarankan:**

1. **Investigasi adalah inti:** pemain mengumpulkan bukti sebelum menarik kesimpulan.
2. **Pertarungan sinematik dan terbaca:** musuh besar punya telegraph, pola yang bisa dipelajari, arena khas, dan fase yang berubah; tantangannya harus terasa adil.
3. **Eksplorasi berlapis:** area saling terhubung dan punya jalan pintas, rahasia, rute terkunci, serta petunjuk simbol yang membuat pemain ingin kembali dengan pemahaman baru.
4. **Progres berbasis pengetahuan:** clue dan puzzle membuka jalan; bukan sekadar mengalahkan musuh.
5. **Kegagalan tidak membuat pemain buntu:** kalah membuat pemain kembali ke checkpoint dengan konsekuensi ringan, bukan kehilangan bukti atau save.
6. **Horror tetap non-grafis:** musuh dan efek dibuat berupa bayangan, kabut, atau konstruksi sihir; tidak ada kekerasan grafis.

### Durasi target

Untuk versi cerita penuh dengan rute hutan-lembah, arahkan ke **4–6 jam untuk penyelesaian pertama**, dengan rentang lebih singkat untuk pemain yang fokus dan sedikit mengeksplorasi. Angka ini target desain, bukan jaminan; validasi lewat playtest. Durasi diperpanjang dengan eksplorasi, dialog saksi, puzzle opsional, dan investigasi sampingan—bukan backtracking kosong atau pertarungan berulang.

---

## 2. Struktur Cerita yang Disarankan

Alur berikut memakai area dan fakta yang sudah ada di PRD/kanon, tetapi memberi ekspedisi hutan-lembah beberapa segmen agar perjalanan terasa lebih panjang. Nama puzzle sampingan, karakter saksi tambahan, dan detail peristiwa lokal masih berupa usulan dan harus disetujui sebelum menjadi fakta kanon. Empat bukti aula yang disebut dokumen kanon tetap menjadi syarat True Ending; jangan mengubah makna atau jumlahnya tanpa revisi kanon.

### Prolog — Desa yang Menahan Napas

**Lokasi:** Ashen Village  
**Tujuan:** Mengenalkan kontrol, nada, Clara, dan cara kerja Casebook.

- Silent Witch tiba setelah kematian Clara Thorne.
- Temui tetua desa dan satu saksi awal; warga memberi informasi yang belum lengkap, bukan jawaban kasus.
- Periksa papan pengumuman, jalan menuju manor, dan satu benda yang berkaitan dengan Clara.
- Casebook memperkenalkan kategori **Bukti**, **Kesaksian**, dan **Kronologi**.
- Tutup prolog dengan gerbang manor yang hanya dapat dibuka setelah pemain mencatat alasan kedatangannya dan mengonfirmasi tujuan investigasi.

**Gameplay:** eksplorasi ringan, dialog, satu clue aman untuk tutorial, tanpa combat.  
**Durasi:** 15–25 menit.

### Bab 1 — Rumah yang Mengingat

**Lokasi:** The Silent Manor, aula dan ruang yang dapat diakses  
**Tujuan:** Menemukan bahwa cerita “penyusup dari luar” belum cocok dengan keadaan tempat kejadian.

- Kenalkan tata letak manor dan area investigasi utama.
- Kumpulkan bukti aula yang telah ditetapkan dokumen kanon, termasuk petunjuk pada jendela utara, meja catatan jaga, dan kesaksian penjaga.
- Beri pemain kesempatan membandingkan ucapan Garrick dengan kondisi fisik lokasi; jangan tampilkan deduksi final otomatis.
- Puzzle pertama membuka akses aman ke ruang berikutnya. Gunakan puzzle sebagai pemeriksaan logika, bukan kode yang muncul tanpa petunjuk.
- Pemain menemukan rujukan pada buku harian kuno dan Sayap Timur.

**Gameplay:** investigasi detail, satu puzzle urutan/kronologi, dialog bercabang ringan.  
**Combat:** tidak ada.  
**Durasi:** 35–50 menit.

### Bab 2 — Gerbang Terbuka, Jalan Menyempit

**Lokasi:** Aula manor → gerbang luar manor → Hollow Woods  
**Tujuan:** Setelah mekanisme gerbang berhasil dibuka, pemain menemukan bahwa jalan keluar dari aula mengarah ke hutan berkabut dan jalur menuju lembah.

- Membuka gerbang aula menjadi pemicu cerita: bunyi mekanisme menarik perhatian ancaman gaib di luar manor. Jangan langsung lempar pemain ke pertarungan; beri transisi, checkpoint, dan kesempatan memeriksa perlengkapan.
- Di ambang gerbang, pemain menemukan **jejak simbol pertama** pada batu atau pohon. Simbol berfungsi sebagai penanda arah, bukan bukti kasus Clara.
- Jalur hutan dibagi menjadi beberapa ruang side-scrolling yang saling terhubung, dengan platform, tepian, celah, dan pintu/rute terkunci. Pemain membaca bentuk simbol, arah goresan, dan perubahan lingkungan untuk memilih jalur yang benar.
- Jalur buntu memberi clue lingkungan atau jalan pintas setelah ditemukan; jangan menghukum pemain dengan harus mengulang area panjang.
- **Pertarungan 1 — Penjaga Jejak (mini-boss):** musuh bayangan menjaga pecahan/penanda simbol yang diperlukan untuk mengaktifkan jalur turun ke lembah. Serangannya mengajarkan pola dasar; kemenangan membuka rute, sedangkan pemain tetap dapat menemukan jalan memutar yang lebih sulit.
- Hadiah encounter adalah **Sigil Jejak** (nama sementara): kunci progres untuk membuka rute hutan berikutnya. Sigil tidak dihitung sebagai salah satu dari empat bukti aula.

**Gameplay:** eksplorasi, tutorial combat, membaca simbol, pembukaan jalur.  
**Durasi:** 25–40 menit.

### Bab 3 — Menyusuri Lembah Berkabut

**Lokasi:** Hollow Woods → jalur turun → lembah  
**Tujuan:** Memperpanjang perjalanan dengan navigasi, puzzle lingkungan, dan dua encounter yang masing-masing membuka rute selanjutnya.

- Lembah memiliki tiga segmen side-scrolling yang saling tersambung: **Jalur Tebing**, **Dasar Lembah**, dan **Lintasan Reruntuhan**. Tambahkan platform vertikal, celah, pintu terkunci, titik istirahat, jalan pintas yang dibuka dari sisi lain, serta satu-dua ruang rahasia. Setiap segmen berisi satu tujuan yang jelas serta satu area opsional.
- **Jalur Tebing:** ikuti urutan simbol pada batu penanda. Petunjuk dibaca dari bentuk dan orientasi simbol yang sudah dilihat; kegagalan hanya mengarahkan pemain ke jalan memutar singkat.
- **Pertarungan 2 — Bayangan Penjaga Tebing (boss):** arena lebar di tepi lembah, serangan area yang terlihat sebelum aktif, lalu pola kedua saat HP boss turun. Kemenangan memberi **Fragmen Sigil** atau membuka tuas/jalur aman untuk turun.
- **Dasar Lembah:** pemain menemukan rangkaian jejak simbol yang sebagian tertutup kabut. Puzzle observasi meminta pemain membedakan jejak asli dari pantulan/umpan; jawabannya dapat disimpulkan dari petunjuk visual.
- Sebuah titik aman di tengah lembah berfungsi sebagai checkpoint dan tempat Casebook memberi ringkasan tujuan berikutnya.
- **Lintasan Reruntuhan — Pertarungan 3 (boss utama area):** penjaga yang lebih kuat mengubah pola dan arena dalam dua atau tiga fase yang jelas. Setelah menang, pemain memperoleh **Sigil Lengkap** atau kunci rute menuju Chapel of Whispers. Buat kemenangan terasa epik melalui perubahan cahaya, kabut, musik/ambience, dan animasi arena—bukan efek visual berlebihan yang menutupi serangan.
- Pertarungan tidak harus memberi loot acak. Pemain mendapat akses/kunci secara konsisten setelah encounter agar progres cerita tidak bergantung pada drop RNG.

**Gameplay:** platforming dan eksplorasi bertahap, puzzle orientasi dan observasi, satu boss dan satu boss utama area dengan fungsi unlock.  
**Durasi:** 45–70 menit.

### Bab 4 — Simbol yang Disalahartikan

**Lokasi:** Chapel of Whispers  
**Tujuan:** Mengurai simbol dan konteks kultus tanpa menjadikan keberadaan kultus sebagai bukti otomatis bahwa kultus membunuh Clara.

- Sigil Lengkap membuka jalur menuju Chapel of Whispers. Di sini pemain membandingkan simbol lembah dengan simbol pada catatan/relief kapel; kemiripan simbol hanya petunjuk konteks, bukan kesimpulan bersalah.
- Periksa catatan dan kondisi bangunan yang ditinggalkan.
- Sediakan puzzle pencocokan simbol dengan aturan yang dapat dipelajari dari catatan sekitar.
- Pemain menemukan bahwa aktivitas rahasia memang ada, tetapi hubungan langsungnya dengan kematian Clara masih harus dibuktikan.
- **Pertarungan penjaga opsional/pendek:** musuh bayangan menjaga ruang catatan. Kemenangan membuka ruang, tetapi catatan tetap dapat diakses lewat puzzle alternatif agar pemain investigatif tidak dipaksa menguasai combat.
- Catatan kapel memberi petunjuk arah atau mekanisme akses ke Underground Archive.

**Gameplay:** deduksi simbol, puzzle pencocokan, encounter singkat.  
**Durasi:** 35–55 menit.

### Bab 5 — Arsip di Bawah Tanah

**Lokasi:** The Underground Archive  
**Tujuan:** Menghubungkan catatan, waktu, dan akses terhadap Gerbang Sayap Timur.

- Arsip dibagi menjadi beberapa ruang kecil dengan fungsi berbeda: katalog, ruang catatan, dan ruang mekanisme.
- Pemain merangkai tanggal atau urutan dari sumber tertulis; berikan semua informasi yang diperlukan sebelum meminta jawaban.
- Buka kembali bukti lama yang mungkin terlewat melalui petunjuk arah di Casebook, bukan dengan membuat clue wajib hilang permanen.
- Puzzle besar menghubungkan bukti jaga, jendela, dan mekanisme akses. Jawaban puzzle membuka jalan, tetapi deduksi siapa pelaku tetap menunggu meja rekonstruksi.
- Pertarungan opsional atau satu encounter pendek dapat dipakai sebagai puncak bab. Hindari membuat beberapa gelombang musuh yang mengganggu tempo membaca dokumen.

**Gameplay:** kronologi, deduksi, puzzle utama, eksplorasi terarah.  
**Durasi:** 40–60 menit.

### Bab 6 — Kembali ke Aula dan Gerbang Sayap Timur

**Lokasi:** The Silent Manor, aula dan Gerbang Sayap Timur  
**Tujuan:** Setelah ekspedisi hutan, pemain kembali ke aula untuk memakai bukti dan petunjuk yang telah ditemukan guna membuka Kotak Besi serta Gerbang Sayap Timur. Gerbang luar manor yang membuka jalan ke hutan pada Bab 2 adalah gerbang yang berbeda.

- Pemain kembali dari hutan/lembah ke manor dengan pemahaman baru terhadap petunjuk pada Kotak Besi. Tunjukkan jalan pulang yang lebih singkat setelah area hutan selesai agar perjalanan tidak menjadi backtracking kosong.
- Syarat membuka area harus konsisten dengan kanon: kombinasi Kotak Besi **2–4–9**, ditemukan/diturunkan dari petunjuk yang tersedia, bukan ditebak acak.
- Sediakan pemeriksaan Casebook sebelum membuka gerbang: pemain dapat meninjau empat bukti aula dan membandingkan kesaksian dengan keadaan fisik.
- Hindari menambah combat wajib di sini; tantangan utama adalah menerapkan bukti dan memecahkan kombinasi.
- Autosave sebelum puzzle final dan sebelum masuk area akhir.

**Gameplay:** kembali ke lokasi lama dengan tujuan baru, puzzle kode, lalu membuka gerbang menuju Sayap Timur. Tidak ada boss di akhir kasus; klimaks deduksi tetap jadi puncak naratif.  
**Durasi:** 25–40 menit.

### Bab 7 — Rekonstruksi dan Konsekuensi

**Lokasi:** Arsip Sayap Timur / meja altar rekonstruksi  
**Tujuan:** Menyimpulkan siapa pelaku, bagaimana alibinya terbantahkan, dan motifnya berdasarkan bukti.

- Pemain memeriksa ruang akhir dan menghubungkan bukti dengan kronologi kanonis.
- Meja rekonstruksi menanyakan tiga hal sesuai dokumen kanon: pelaku, bukti alibi palsu, dan motif.
- Tampilkan bukti yang sudah ditemukan sebagai pilihan dan izinkan pemain membuka kembali deskripsinya.
- **The Truth:** hanya jika seluruh empat bukti aula, puzzle Kotak Besi, akses Arsip Sayap Timur, dan deduksi benar terpenuhi.
- **The Unfinished Case:** pemain mencapai akhir tanpa bukti yang cukup atau memilih berhenti sebelum deduksi lengkap.
- **The False Accusation:** pemain mengunci kesimpulan yang bertentangan dengan bukti. Beri ringkasan alasan kegagalannya dengan jelas.
- Hindari pertarungan setelah pemain mulai menyusun deduksi; klimaks terakhir harus berupa penalaran dan konsekuensi moral, bukan boss fight.

**Durasi:** 20–35 menit.

---

## 3. Combat Side-Scrolling dan Boss Fight

### Sasaran sistem

Combat memakai **real-time 2D side-scrolling**. Karakter bergerak dan melompat di arena, membaca telegraph, lalu menyerang pada celah yang tepat. Boss boleh terasa epik dan memiliki beberapa fase, tetapi pola serangannya harus terbaca dan adil.

- **Serangan dasar:** serangan sihir jarak dekat/tebasan tongkat yang mengarah ke kiri atau kanan sesuai arah karakter.
- **Lompat:** penting untuk melintasi celah dan menghindari sapuan/gelombang serangan boss. Tambahkan kontrol udara yang stabil dan collision platform yang akurat.
- **Boss:** tiap boss memiliki arena, siluet, suara, dan pola khas Silent Witch. Susun 2 fase untuk boss normal dan maksimal 3 fase untuk boss utama area; fase baru menambah atau mengubah pola, bukan sekadar mempercepat semua serangan.
- **Ritme boss:** telegraph → serangan → jeda untuk membalas. Sisipkan momen arena yang berubah secara visual, tetapi jangan menutupi bahaya atau membuatnya mustahil dibaca.
- **Fokus sihir:** energi terbatas untuk kemampuan khusus; serangan dasar tetap tersedia agar pemain tidak kehabisan cara melawan.
- **Checkpoint:** tempat aman sebelum arena besar. Kalah mengembalikan pemain ke checkpoint; clue dan puzzle yang sudah selesai tetap tersimpan.
- **Hadiah progres:** kemenangan membuka segel/rute dan memberi Sigil secara pasti. Jangan gunakan loot RNG untuk item wajib.
- **Kalah:** kembali ke checkpoint terdekat dengan HP/energi pulih sebagian. Clue dan puzzle yang sudah selesai tetap tersimpan.
- **Opsional menghindari:** beberapa encounter bisa dielakkan lewat jalur samping atau puzzle, bila tidak merusak pacing.

### Kontrol awal yang disarankan

- **A/D atau panah kiri/kanan:** bergerak horizontal.
- **Shift:** lompat.
- **Klik kanan:** serangan dasar.
- **E:** interaksi.
- **Tab:** Casebook.

Tampilkan kontrol pada tutorial dan menu bantuan. Pastikan klik kanan tidak memicu serangan saat dialog, puzzle, atau menu terbuka; interaksi tetap memakai E. Untuk Processing, baca tombol mouse kanan melalui event/button yang sesuai dan pastikan tidak membuka menu konteks sistem. Sediakan opsi remap atau tombol keyboard alternatif bila klik kanan tidak nyaman bagi sebagian pemain.

### Jangan masukkan pada versi pertama

- Pohon skill besar, loot acak, rarity gear, crafting, atau banyak senjata.
- Level musuh berbasis grind.
- Combat yang menjadi syarat mengulang pertarungan untuk farming.
- Boss multi-fase sebelum gerakan, lompatan, collision, dan telegraph musuh teruji.
- Damage pada korban atau penggambaran kekerasan grafis.

---

## 4. Cara Memanjangkan Game Tanpa Membuatnya Terasa Mengulur

1. **Tambahkan saksi dengan fungsi berbeda**, bukan sekadar NPC yang mengulang clue yang sama. Pastikan hubungan dan pernyataannya ditambahkan ke dokumen kanon lebih dulu.
2. **Buat side investigation opsional** tentang sejarah manor atau kehidupan Clara. Hadiahnya berupa konteks emosional dan detail dunia, bukan jawaban alternatif yang membingungkan kasus utama.
3. **Gunakan clue berlapis:** satu temuan mengarahkan pemain ke sumber lain; jangan menambah fetch quest tanpa informasi baru.
4. **Buat area berubah setelah progres:** pintu terbuka, NPC memberi respons baru, atau objek yang dulu tidak bermakna kini dapat ditafsirkan ulang.
5. **Sediakan ringkasan tujuan yang jelas** di Casebook agar pemain tidak tersesat hanya untuk menambah waktu bermain.
6. **Hindari backtracking kosong:** ketika pemain kembali ke manor, tujuan baru harus ditandai dan ada perubahan dialog/akses.
7. **Sediakan hint bertingkat dan jurnal kronologi** supaya puzzle menantang tetapi adil.

---

## 5. Perubahan PRD yang Perlu Ditambahkan

### Tambahan pada prinsip desain

- **Combat mendukung misteri:** encounter tidak boleh memberikan jawaban kasus atau menggantikan kebutuhan bukti.
- **Combat berkualitas, bukan grind:** jumlah encounter terkendali; boss penting punya pola/arena yang dirancang, bukan musuh yang diulang untuk memperpanjang durasi.
- **Progress tidak hilang saat kalah:** clue, puzzle, dan quest yang sudah selesai tidak dihapus.

### Tambahan modul

Tambahkan hanya setelah movement/interaksi stabil:

- `CombatSystem`: input serangan, hit detection, cooldown, dan aturan damage.
- `Enemy`: state sederhana seperti idle, telegraph, attack, recovery, defeated.
- `CombatUI`: HP, energi, dan indikator serangan yang seperlunya.

Modul dapat digabung jika implementasi Processing tetap sederhana; jangan membuat arsitektur berlebihan.

### Tambahan acceptance criteria

- Serangan pemain hanya aktif pada scene combat dan tidak berjalan di dialog/menu/puzzle.
- Musuh memberi telegraph yang terlihat sebelum menyerang.
- Kemenangan memicu flag progres yang eksplisit dan save bila sesuai.
- Kekalahan tidak menghapus clue, menyetel ulang ending, atau membuat quest tidak dapat diselesaikan.
- Setiap pertarungan dapat diuji tanpa memerlukan grind.
- Jalur untuk menghindari encounter opsional, jika ada, tetap mempertahankan progres cerita yang benar.
- Combat tidak bisa digunakan untuk melewati puzzle atau memperoleh kesimpulan tanpa bukti.

### Revisi prioritas scope

- **P0:** tanpa combat; tuntaskan loop eksplorasi–interaksi–clue–puzzle terlebih dahulu.
- **P1:** prototipe gerak side-scrolling, lompat, serangan klik kanan, satu musuh biasa, dan satu mini-boss tutorial di Hollow Woods.
- **P2:** boss tebing dan boss reruntuhan dengan 2–3 fase, arena khas, checkpoint, animasi/efek, balancing, serta opsi aksesibilitas.

Ini menjaga proyek awal tetap realistis. Jangan menambahkan combat ke P0 sebelum versi investigasi dasar dapat dimainkan.

---

## 6. Catatan Konsistensi Sebelum Implementasi

1. PRD menyebut enam area, sementara dokumen kanon menetapkan detail yang berpusat pada aula, jendela utara, Kotak Besi, Gerbang Sayap Timur, dan Arsip Sayap Timur. Tetapkan hubungan lokasi-lokasi itu pada peta agar pemain tidak bingung.
2. Pastikan empat bukti aula yang disyaratkan True Ending diberi daftar ID dan nama final dalam dokumen konten. Kanon yang tersedia menyebut beberapa clue secara eksplisit; jangan mengisi slot lain dengan fakta baru tanpa persetujuan.
3. Sinkronkan kronologi: dokumen kanon mencatat Clara ditemukan di koridor Sayap Timur, sementara akses gerbang dan Kotak Besi merupakan bagian investigasi. Jelaskan kapan dan bagaimana kondisi lokasi diketahui tanpa menciptakan kontradiksi.
4. Tentukan apa tepatnya yang membuka akses ke setiap area dan clue mana yang wajib/opsional.
5. Jika musuh gaib ditambahkan, tetapkan asal-usulnya di lore sebagai unsur dunia fiksi dan jangan menyamakannya dengan kultus nyata. Keberadaan musuh tidak boleh mengubah motif Garrick atau hasil kasus.
6. Tambahkan combat ke prompt AI coding assistant hanya setelah addendum disetujui, dan implementasikan bertahap setelah movement, interaksi, clue, dan save dasar stabil.

---

## 7. Rekomendasi Akhir

Pilihan paling cocok dengan arah baru adalah: **tujuh bab, eksplorasi side-scrolling gothic, perjalanan hutan-lembah yang berlapis, klik kanan untuk menyerang, Shift untuk lompat, dan tiga encounter penting termasuk boss multi-fase**. Encounter kapel dapat opsional atau punya solusi puzzle. Boss besar berada di perjalanan untuk membuka Sigil/rute; jangan tempatkan boss setelah deduksi final, karena klimaks cerita tetap momen pemain membuktikan kebenaran kasus Clara.
