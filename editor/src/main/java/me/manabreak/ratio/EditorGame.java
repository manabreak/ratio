package me.manabreak.ratio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import me.manabreak.ratio.editor.EditorGameScreen;
import me.manabreak.ratio.editor.PluginManager;
import me.manabreak.ratio.plugins.camera.EditorCameraPlugin;
import me.manabreak.ratio.plugins.exporters.ExportManager;
import me.manabreak.ratio.plugins.exporters.GdxJsonExporter;
import me.manabreak.ratio.plugins.importers.GdxJsonImporter;
import me.manabreak.ratio.plugins.importers.ImportManager;
import me.manabreak.ratio.plugins.level.*;
import me.manabreak.ratio.plugins.objects.ObjectEditorPlugin;
import me.manabreak.ratio.plugins.properties.LevelPropertiesPlugin;
import me.manabreak.ratio.plugins.scene.EditorGrid;
import me.manabreak.ratio.plugins.tilesets.TilesetManager;
import me.manabreak.ratio.plugins.tilesets.TilesetPlugin;
import me.manabreak.ratio.plugins.toolbar.ToolbarPlugin;
import me.manabreak.ratio.ui.Res;

public class EditorGame extends Game {

    private Screen gameScreen;
    private TilesetManager tilesetManager;

    @Override
    public void create() {
        Res.load();
        FileChooser.setDefaultPrefsName("me.manabreak.ratio.filechooser");
        FileChooser.setSaveLastDirectory(true);
        tilesetManager = new TilesetManager();
        gameScreen = createGameScreen();
        setScreen(gameScreen);
    }

    private Screen createGameScreen() {
        PluginManager pluginManager = new PluginManager();
        pluginManager.register(
                new ToolbarPlugin(),
                new EditorCameraPlugin(),
                new EditorGrid(),
                new LevelEditorPlugin(new LevelShader(), new WireframeRenderer(), new ObjectRenderer(new ShapeRenderer(), new SpriteBatch(), new BitmapFont()), new ToolRenderer()),
                new ObjectEditorPlugin(),
                new LevelPropertiesPlugin(),
                new TilesetPlugin()
        );

        ExportManager exportManager = new ExportManager();
        exportManager.register(
                new GdxJsonExporter()
        );

        ImportManager importManager = new ImportManager();
        importManager.register(
                new GdxJsonImporter(tilesetManager)
        );

        return new EditorGameScreen(pluginManager, exportManager, importManager, tilesetManager);
    }
}
