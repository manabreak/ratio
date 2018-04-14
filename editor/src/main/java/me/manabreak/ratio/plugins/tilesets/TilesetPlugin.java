package me.manabreak.ratio.plugins.tilesets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.manabreak.ratio.editor.EditorPlugin;
import me.manabreak.ratio.plugins.level.Level;

public class TilesetPlugin extends EditorPlugin {
    private TilesetUi ui;

    @Override
    public void initialize() {
        ui = new TilesetUi(new TilesetPresenter(this, editorController.getTilesetManager()));
        editorView.setRightPanelBottom(ui);
    }

    public TextureRegion getCurrentRegion() {
        return ui.getPresenter().getCurrentRegion();
    }

    public void levelLoaded(Level level) {
        // TODO
    }
}
