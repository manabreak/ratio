package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.manabreak.ratio.plugins.tilesets.Tileset;

public class EraseBlockCommand implements Command {

    private final LevelEditorPlugin plugin;
    private final TileLayer layer;
    private final TextureRegion region;
    private final Tileset tileset;
    private final int x;
    private final int y;
    private final int z;
    private final int size;

    public EraseBlockCommand(LevelEditorPlugin plugin, TileLayer layer, TextureRegion region, Tileset tileset, int x, int y, int z, int size) {
        this.plugin = plugin;
        this.layer = layer;
        this.region = region;
        this.tileset = tileset;
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
    }

    @Override
    public void execute() {
        layer.erase(tileset, x, y, z, size);
        plugin.recreateMeshes();
    }

    @Override
    public void undo() {
        /*
        TextureRegion tmpRegion = layer.getCurrentRegion();
        layer.setCurrentRegion(region);
        layer.draw(tileset, x, y, z, size);
        layer.setCurrentRegion(tmpRegion);
        */
        plugin.recreateMeshes();
    }
}
