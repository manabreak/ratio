package me.manabreak.ratio.plugins.properties;

import me.manabreak.ratio.editor.EditorPlugin;

public class LevelPropertiesPlugin extends EditorPlugin {

    private LevelPropertiesUi ui;

    @Override
    public void initialize() {
        ui = new LevelPropertiesUi(new LevelPropertiesPresenter());
        editorView.addLeftPanelTab("Properties", ui);
    }

    public LevelPropertiesPresenter getPresenter() {
        return ui.getPresenter();
    }
}
