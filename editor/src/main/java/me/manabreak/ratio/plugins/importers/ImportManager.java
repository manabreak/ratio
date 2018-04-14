package me.manabreak.ratio.plugins.importers;

import com.badlogic.gdx.files.FileHandle;
import me.manabreak.ratio.common.Properties;
import me.manabreak.ratio.plugins.level.Level;
import me.manabreak.ratio.plugins.objects.GameObject;
import me.manabreak.ratio.utils.Action1;

import java.util.ArrayList;
import java.util.List;

public class ImportManager {

    private final List<Importer> importers = new ArrayList<>();

    public void register(Importer importer) {
        importers.add(importer);
    }

    public List<Importer> getImporters() {
        return importers;
    }

    public Level load(FileHandle fh, Action1<List<GameObject>> objectCallback, Action1<Properties> propertyCallback) throws ImporterException, MissingImporterException {
        for (Importer importer : importers) {
            if (importer.getFileExtension().equalsIgnoreCase(fh.extension())) {
                return importer.load(fh, objectCallback, propertyCallback);
            }
        }

        throw new MissingImporterException(fh);
    }
}
