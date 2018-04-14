package me.manabreak.ratio.editor;

import java.util.LinkedHashMap;
import java.util.Map;

public class PluginManager {

    private final Map<Class<? extends EditorPlugin>, EditorPlugin> plugins = new LinkedHashMap<>();

    public PluginManager() {

    }

    @SafeVarargs
    public final <T extends EditorPlugin> void register(T... plugins) {
        for (T plugin : plugins) {
            if (this.plugins.containsKey(plugin.getClass())) {
                throw new IllegalArgumentException("Cannot register same plugin twice!\nPlugin class in question: " + plugin.getClass().getSimpleName());
            }
            this.plugins.put(plugin.getClass(), plugin);
        }
    }

    void bindPlugins(PluginHost host) {
        for (Map.Entry<Class<? extends EditorPlugin>, EditorPlugin> entry : plugins.entrySet()) {
            host.addPlugin(entry.getValue());
        }

        host.getPlugins().forEach(EditorPlugin::postInitialize);
    }
}
