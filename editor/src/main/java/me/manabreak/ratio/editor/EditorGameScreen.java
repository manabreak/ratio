package me.manabreak.ratio.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import me.manabreak.ratio.plugins.camera.EditorCamera;
import me.manabreak.ratio.plugins.exporters.ExportManager;
import me.manabreak.ratio.plugins.importers.ImportManager;
import me.manabreak.ratio.plugins.tilesets.TilesetManager;

import java.util.ArrayList;
import java.util.List;

public class EditorGameScreen extends ScreenAdapter {

    private final EditorController editorController;
    private final List<FileHandle> reloadList = new ArrayList<>();
    private final TilesetManager tilesetManager;
    private EditorStage editorStage;

    public EditorGameScreen(PluginManager pluginManager, ExportManager exportManager, ImportManager importManager, TilesetManager tilesetManager) {
        this.tilesetManager = tilesetManager;
        editorController = new EditorController(pluginManager, exportManager, importManager, tilesetManager);
        editorStage = new EditorStage(editorController);
        pluginManager.bindPlugins(editorController);

        tilesetManager.getTextureReloadSubject()
                .subscribe(fh -> {
                    synchronized (reloadList) {
                        reloadList.add(fh);
                    }
                });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        synchronized (reloadList) {
            if (reloadList.size() > 0) {
                for (FileHandle fileHandle : reloadList) {
                    tilesetManager.reload(fileHandle);
                }
                reloadList.clear();
            }
        }

        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        editorController.act(delta);
        editorStage.act(delta);
        editorStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("Resize: " + width + "," + height);
        editorStage.resize(width, height);
        EditorCamera.main.resize(width, height);
    }
}
