/**
 * SILENT WITCH - Stage 03: Interaksi dan dialog.
 *
 * Processing 4, Java Mode. Entry point ini sengaja tipis: seluruh logika
 * berada di tab .java agar dapat diuji terpisah dan tidak menumpuk di file PDE.
 *
 * Catatan struktur:
 * Processing hanya mengompilasi tab .java yang berada di folder sketch ini,
 * sehingga kelas-kelas proyek diletakkan di root sketch (bukan di src/).
 */

GameManager game;

void settings() {
  // Resolusi internal 320x180 diperbesar 3x dengan skala integer.
  size(960, 540);
  noSmooth();
}

void setup() {
  surface.setTitle("Silent Witch - Tahap 03");
  game = new GameManager(this);
  game.start();
}

void draw() {
  game.tick();
}

void keyPressed() {
  // Cegah Processing keluar otomatis saat ESC ditekan. Nilai asli tetap
  // diteruskan ke GameManager agar ESC dapat dipakai untuk navigasi.
  char typed = key;
  if (typed == ESC) {
    key = 0;
  }
  game.keyPressed(typed, keyCode);
}

void keyReleased() {
  game.keyReleased(key, keyCode);
}

void focusLost() {
  // Mencegah tombol gerak tertahan aktif saat jendela kehilangan fokus.
  game.focusLost();
}

void mousePressed() {
  game.mousePressed(mouseX, mouseY);
}

void mouseMoved() {
  game.mouseMoved(mouseX, mouseY);
}

void mouseDragged() {
  game.mouseMoved(mouseX, mouseY);
}
