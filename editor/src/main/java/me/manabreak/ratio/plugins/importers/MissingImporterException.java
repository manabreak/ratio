package me.manabreak.ratio.plugins.importers;

import com.badlogic.gdx.files.FileHandle;

public class MissingImporterException extends Exception {
    public MissingImporterException(FileHandle fh) {
        super("Missing importer for file format " + fh.extension());
    }
}
