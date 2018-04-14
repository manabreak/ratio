package me.manabreak.ratio.plugins.toolbar;

import me.manabreak.ratio.editor.EditorPlugin;
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
    }

    public ToolbarPresenter getPresenter() {
        return toolbarUi.getPresenter();
    }

    public ToolbarUi getUi() {
        return toolbarUi;
    }
}
