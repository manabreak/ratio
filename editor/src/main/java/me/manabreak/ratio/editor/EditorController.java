package me.manabreak.ratio.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import me.manabreak.ratio.plugins.exporters.ExportManager;
import me.manabreak.ratio.plugins.importers.ImportManager;
import me.manabreak.ratio.plugins.importers.ImporterException;
import me.manabreak.ratio.plugins.importers.MissingImporterException;
import me.manabreak.ratio.plugins.level.Level;
import me.manabreak.ratio.plugins.level.LevelEditorPlugin;
import me.manabreak.ratio.plugins.objects.ObjectEditorPlugin;
import me.manabreak.ratio.plugins.properties.LevelPropertiesPlugin;
import me.manabreak.ratio.plugins.tilesets.TilesetManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EditorController implements PluginHost {

    private final PluginManager pluginManager;
    private final ExportManager exportManager;
    private final ImportManager importManager;
    private final TilesetManager tilesetManager;
    private InputMultiplexer multiplexer;
    private EditorView view;
    private FileHandle savedHandle;
    private List<EditorPlugin> activePlugins = new ArrayList<>();
    private List<LoopListener> loopListeners = new ArrayList<>();

    EditorController(PluginManager pluginManager, ExportManager exportManager, ImportManager importManager, TilesetManager tilesetManager) {
        this.pluginManager = pluginManager;
        this.exportManager = exportManager;
        this.importManager = importManager;
        this.tilesetManager = tilesetManager;
    }

    @Override
    public void addPlugin(EditorPlugin plugin) {
        System.out.println("addPlugin: " + plugin.getClass().getSimpleName());
        plugin.setEditorView(view);
        plugin.setEditorController(this);
        plugin.initialize();
        activePlugins.add(plugin);
    }

    public <T extends EditorPlugin> T getPlugin(Class<T> tClass) {
        for (EditorPlugin plugin : activePlugins) {
            if (plugin.getClass().equals(tClass)) {
                //noinspection unchecked
                return (T) plugin;
            }
        }
        throw new IllegalArgumentException("No plugin " + tClass.getSimpleName() + " registered!");
    }

    @Override
    public Collection<EditorPlugin> getPlugins() {
        return activePlugins;
    }

    public void addLoopListener(LoopListener listener) {
        if (!loopListeners.contains(listener)) {
            loopListeners.add(listener);
        }
    }

    public void registerInputProcessor(InputProcessor inputProcessor) {
        multiplexer.addProcessor(inputProcessor);
    }

    void setView(me.manabreak.ratio.editor.EditorView view) {
        this.view = view;
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(view);
        multiplexer.addProcessor(new ShortcutProcessor(this, (EditorStage) view));
        Gdx.input.setInputProcessor(multiplexer);
    }

    void act(float dt) {
        for (LoopListener listener : loopListeners) {
            listener.onUpdate(dt);
        }
    }

    public void onSaveClicked() {
        System.out.println("onSaveClicked()");
        if (savedHandle != null) {
            onSaveFileSelected(savedHandle);
        } else {
            onSaveAsClicked();
        }
    }

    public void onSaveAsClicked() {
        System.out.println("onSaveAsClicked()");
        view.showSaveAsDialog(exportManager.getExporters());
    }

    public void onSaveFileSelected(FileHandle fh) {
        System.out.println("Should save to file " + fh.toString());
        LevelEditorPlugin levelEditorPlugin = getPlugin(LevelEditorPlugin.class);
        ObjectEditorPlugin objectEditorPlugin = getPlugin(ObjectEditorPlugin.class);
        LevelPropertiesPlugin levelPropertiesPlugin = getPlugin(LevelPropertiesPlugin.class);
        exportManager.save(levelEditorPlugin.getLevel(), objectEditorPlugin.getPresenter().getObjects(), levelPropertiesPlugin.getPresenter().getProperties(), fh);
        savedHandle = fh;
    }

    public TilesetManager getTilesetManager() {
        return tilesetManager;
    }

    public void onOpenClicked() {
        System.out.println("onOpenClicked()");
        view.showOpenDialog(importManager.getImporters());
    }

    public void onOpenFileSelected(FileHandle fh) {
        System.out.println("Should open file " + fh.toString());
        // TODO If unsaved changes, prompt user before clearing
        try {
            Level level = importManager.load(fh, objects -> {
                getPlugin(ObjectEditorPlugin.class).loadObjects(objects);
            }, properties -> {
                getPlugin(LevelPropertiesPlugin.class).getPresenter().loadProperties(properties);
            });
            getPlugin(LevelEditorPlugin.class).setLevel(level);
            savedHandle = fh;
        } catch (ImporterException | MissingImporterException e) {
            // TODO Show error notification on UI
            e.printStackTrace();
        }
    }
}
