package me.manabreak.ratio.editor;

import java.util.Collection;

public interface PluginHost {
    void addPlugin(EditorPlugin plugin);

    Collection<EditorPlugin> getPlugins();
}
