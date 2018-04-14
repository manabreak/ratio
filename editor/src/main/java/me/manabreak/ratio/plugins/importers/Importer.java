package me.manabreak.ratio.plugins.importers;

import com.badlogic.gdx.files.FileHandle;
import me.manabreak.ratio.common.Properties;
import me.manabreak.ratio.plugins.level.Level;
import me.manabreak.ratio.plugins.objects.GameObject;
import me.manabreak.ratio.utils.Action1;

import java.util.List;

public interface Importer {
    String getFileExtension();

    String getFormatName();

    Level load(FileHandle fh, Action1<List<GameObject>> objectCallback, Action1<Properties> propertiesCallback) throws ImporterException;
}
