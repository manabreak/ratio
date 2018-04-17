package me.manabreak.ratio.plugins.toolbar;

import me.manabreak.ratio.editor.EditorPlugin;
import me.manabreak.ratio.plugins.camera.EditorCameraPlugin;
import me.manabreak.ratio.plugins.level.LevelEditorPlugin;

public class ToolbarPlugin extends EditorPlugin {

    private ToolbarUi toolbarUi;

    @Override
    public void initialize() {
        toolbarUi = new ToolbarUi(new ToolbarPresenter(editorView));
        editorView.registerToolbar(toolbarUi);
    }

    @Override
    public void postInitialize() {
        toolbarUi.getPresenter().getToolSubject()
                .subscribe(tool -> {
                    getPlugin(LevelEditorPlugin.class).setTool(tool);
                });

        toolbarUi.getPresenter().getCameraSnapObservable()
                .subscribe(snap -> {
                    getPlugin(EditorCameraPlugin.class).setCameraSnap(snap);
                });

        toolbarUi.getPresenter().getCameraProjectionObservable()
                .subscribe(perspective -> {
                    getPlugin(EditorCameraPlugin.class).setProjection(perspective);
                });
    }

    public ToolbarPresenter getPresenter() {
        return toolbarUi.getPresenter();
    }

    public ToolbarUi getUi() {
        return toolbarUi;
    }
}
