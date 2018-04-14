package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.manabreak.ratio.plugins.tilesets.Tileset;

public class DrawBlockCommand implements Command {

    private final LevelEditorPlugin plugin;
    private final TileLayer layer;
    private final Tileset tileset;
    private final int x;
    private final int y;
    private final int z;
    private final int size;
    private final TextureRegion region;

    public DrawBlockCommand(LevelEditorPlugin plugin, TileLayer layer, TextureRegion region, Tileset tileset, int x, int y, int z, int size) {
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
        layer.draw(tileset, x, y, z, size);
        final Tile tile = tileset.createTile(region);
        for (Face face : Face.values()) {
            layer.paint(tileset, x, y, z, size, face, tile);
        }
        plugin.recreateMeshes();
    }

    @Override
    public void undo() {
        layer.erase(tileset, x, y, z, size);
        plugin.recreateMeshes();
    }
}
