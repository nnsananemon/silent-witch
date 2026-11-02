import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import processing.data.JSONObject;

/**
 * Pengelola penyimpanan otomatis dan pemuatan data investigasi
 * sesuai PRD Seksi 7, 10.4, NFR-06, dan Acceptance Criteria AC-07.
 *
 * Fitur keselamatan:
 * - Menulis ke file temporer (.tmp) sebelum menggantikan file utama secara atomik.
 * - Penanganan save rusak dengan pesan diagnostik yang jelas.
 * - File save rusak TIDAK PERNAH dihapus atau ditimpa secara otomatis (NFR-06 / AC-07).
 */
public final class SaveManager {

    public static final String DEFAULT_SAVE_PATH = "save/savegame.json";

    public static final class SaveResult {
        private final boolean success;
        private final String errorMessage;

        private SaveResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage == null ? "" : errorMessage;
        }

        public static SaveResult success() {
            return new SaveResult(true, "");
        }

        public static SaveResult failure(String message) {
            return new SaveResult(false, message);
        }

        public boolean isSuccess() {
            return success;
        }

        public String errorMessage() {
            return errorMessage;
        }
    }

    public static final class LoadResult {
        public enum Status {
            SUCCESS,
            NO_SAVE,
            CORRUPT
        }

        private final Status status;
        private final SaveData data;
        private final String errorMessage;

        private LoadResult(Status status, SaveData data, String errorMessage) {
            this.status = status;
            this.data = data;
            this.errorMessage = errorMessage == null ? "" : errorMessage;
        }

        public static LoadResult success(SaveData data) {
            return new LoadResult(Status.SUCCESS, data, "");
        }

        public static LoadResult noSave(String message) {
            return new LoadResult(Status.NO_SAVE, null, message);
        }

        public static LoadResult corrupt(String message) {
            return new LoadResult(Status.CORRUPT, null, message);
        }

        public Status status() {
            return status;
        }

        public boolean isSuccess() {
            return status == Status.SUCCESS;
        }

        public boolean isCorrupt() {
            return status == Status.CORRUPT;
        }

        public boolean isNoSave() {
            return status == Status.NO_SAVE;
        }

        public SaveData data() {
            return data;
        }

        public String errorMessage() {
            return errorMessage;
        }
    }

    private final File saveFile;

    public SaveManager(File saveFile) {
        if (saveFile == null) {
            throw new IllegalArgumentException("File save tidak boleh null.");
        }
        this.saveFile = saveFile;
    }

    public SaveManager() {
        this(new File(DEFAULT_SAVE_PATH));
    }

    public File saveFile() {
        return saveFile;
    }

    public boolean hasSaveFile() {
        return saveFile.exists() && saveFile.isFile() && saveFile.length() > 0;
    }

    public boolean hasValidSave() {
        LoadResult res = load();
        return res.isSuccess();
    }

    /**
     * Menyimpan data save secara atomik dan aman (NFR-06).
     *
     * Menulis ke file .tmp terlebih dahulu, lalu memindahkannya ke file target.
     * Jika terjadi kegagalan penulisan di tengah jalan, file target tidak rusak.
     */
    public synchronized SaveResult save(SaveData data) {
        if (data == null) {
            return SaveResult.failure("Data save tidak boleh null.");
        }

        File parent = saveFile.getParentFile();
        if (parent != null && !parent.exists()) {
            boolean created = parent.mkdirs();
            if (!created && !parent.exists()) {
                return SaveResult.failure("Gagal membuat direktori folder save: " + parent.getAbsolutePath());
            }
        }

        File tmpFile = new File(saveFile.getAbsolutePath() + ".tmp");

        PrintWriter writer = null;
        try {
            JSONObject json = data.toJSON();
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(tmpFile), StandardCharsets.UTF_8));
            writer.print(json.format(2));
            writer.flush();
        } catch (Exception ex) {
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            return SaveResult.failure("Gagal menulis data temporer: " + ex.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        // Pindahkan file .tmp ke saveFile secara atomik
        try {
            Files.move(tmpFile.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            return SaveResult.success();
        } catch (Exception ex) {
            // Fallback jika sistem berkas tidak mendukung ATOMIC_MOVE
            try {
                Files.move(tmpFile.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return SaveResult.success();
            } catch (Exception ex2) {
                if (saveFile.exists()) {
                    saveFile.delete();
                }
                boolean renamed = tmpFile.renameTo(saveFile);
                if (renamed) {
                    return SaveResult.success();
                }
                return SaveResult.failure("Gagal memindahkan file save temporer: " + ex2.getMessage());
            }
        }
    }

    /**
     * Membaca dan memvalidasi file save.
     *
     * Jika file tidak ada: mengembalikan LoadResult.noSave.
     * Jika file rusak: mengembalikan LoadResult.corrupt tanpa menghapus file (AC-07 / NFR-06).
     * Jika file valid: mengembalikan LoadResult.success dengan SaveData.
     */
    public synchronized LoadResult load() {
        if (!saveFile.exists() || !saveFile.isFile()) {
            return LoadResult.noSave("File save tidak ditemukan.");
        }

        if (saveFile.length() == 0) {
            return LoadResult.corrupt("File save kosong (ukuran 0 byte).");
        }

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(saveFile), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception ex) {
            return LoadResult.corrupt("Gagal membaca file save dari disk: " + ex.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {
                }
            }
        }

        JSONObject json;
        try {
            json = JSONObject.parse(sb.toString());
            if (json == null) {
                return LoadResult.corrupt("Format data JSON tidak valid.");
            }
        } catch (Exception ex) {
            // NFR-06: File corrupt tidak dihapus
            return LoadResult.corrupt("Gagal mem-parse JSON save: " + ex.getMessage());
        }

        try {
            SaveData data = SaveData.fromJSON(json);
            return LoadResult.success(data);
        } catch (IllegalArgumentException ex) {
            // NFR-06: Skema atau data tidak valid, file tetap dipertahankan
            return LoadResult.corrupt("Validasi skema save gagal: " + ex.getMessage());
        } catch (Exception ex) {
            return LoadResult.corrupt("Terjadi kesalahan pembacaan data save: " + ex.getMessage());
        }
    }

    /** Menghapus file save; hanya dipakai saat pemain memilih reset manual atau dalam uji coba. */
    public synchronized boolean deleteSave() {
        if (saveFile.exists()) {
            return saveFile.delete();
        }
        return false;
    }
}
