package me.manabreak.ratio.plugins.properties;

import me.manabreak.ratio.editor.EditorPlugin;

public class LayerPropertiesPlugin extends EditorPlugin {

    private LayerPropertiesUi ui;

    @Override
    public void initialize() {
        ui = new LayerPropertiesUi(new LayerPropertiesPresenter());
        editorView.addLeftPanelTab("Layer", ui);
    }

    public LayerPropertiesPresenter getPresenter() {
        return ui.getPresenter();
    }
}
