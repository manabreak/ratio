package me.manabreak.ratio.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import me.manabreak.ratio.plugins.tilesets.Tileset;

public class TilesetTab extends Tab {

    private final Tileset tileset;
    private final VisTable content;

    public TilesetTab(Tileset tileset, VisTable content) {
        super(false, false);
        this.tileset = tileset;
        this.content = content;
    }

    @Override
    public String getTabTitle() {
        return tileset.getName();
    }

    @Override
    public Table getContentTable() {
        return content;
    }

    public Tileset getTileset() {
        return tileset;
    }
}
