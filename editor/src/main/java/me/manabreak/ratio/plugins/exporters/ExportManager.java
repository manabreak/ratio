package me.manabreak.ratio.plugins.exporters;

import com.badlogic.gdx.files.FileHandle;
import me.manabreak.ratio.common.Properties;
import me.manabreak.ratio.plugins.level.Level;
import me.manabreak.ratio.plugins.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public class ExportManager {

    private final List<Exporter> exporters = new ArrayList<>();

    public void register(Exporter exporter) {
        exporters.add(exporter);
    }

    public List<Exporter> getExporters() {
        return exporters;
    }

    public void save(Level level, List<GameObject> objects, Properties properties, FileHandle fh) {
        for (Exporter exporter : exporters) {
            if (exporter.getFileExtension().equalsIgnoreCase(fh.extension())) {
                exporter.save(level, objects, properties, fh);
                break;
            }
        }
    }
}
