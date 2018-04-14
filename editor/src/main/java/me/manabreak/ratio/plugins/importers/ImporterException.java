package me.manabreak.ratio.plugins.importers;

import com.badlogic.gdx.files.FileHandle;

public class ImporterException extends Exception {
    public static ImporterException create(Importer importer, FileHandle fh, String reason) {
        return new ImporterException("Failed to load level file " + fh.toString() + " using importer " + importer.getClass().getSimpleName() + ", reason: " + reason);
    }

    private ImporterException(String message) {
        super(message);
    }
}
