package me.manabreak.ratio.plugins.exporters;

import com.badlogic.gdx.files.FileHandle;
import me.manabreak.ratio.common.Properties;
import me.manabreak.ratio.plugins.level.Level;
import me.manabreak.ratio.plugins.objects.GameObject;

import java.util.List;

/**
 * Interface for all exporters formats.
 */
public interface Exporter {

    /**
     * Retrieves the file extension without the leading dot, for example "txt"
     *
     * @return File extension used for the exporters format
     */
    String getFileExtension();

    /**
     * Retrieves the human-readable name of the format, eg. "JSON File"
     *
     * @return The name of the format
     */
    String getFormatName();

    /**
     * Saves the given level to the given file
     *  @param level   to save
     * @param objects List of game objects
     * @param properties
     * @param fh      file handle of the target file
     */
    void save(Level level, List<GameObject> objects, Properties properties, FileHandle fh);
}
