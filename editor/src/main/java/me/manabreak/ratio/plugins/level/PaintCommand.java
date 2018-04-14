package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.manabreak.ratio.plugins.tilesets.Tileset;

public class PaintCommand implements Command {
    private final LevelEditorPlugin plugin;
    private final TileLayer layer;
    private final TextureRegion region;
    private final Tileset tileset;
    private final int x;
    private final int y;
    private final int z;
    private final int size;
    private final Face face;

    // Stored on execute for undoing
    private Tile oldTile;

    public PaintCommand(LevelEditorPlugin plugin, TileLayer layer, TextureRegion region, Tileset tileset, int x, int y, int z, int size, Face face) {
        this.plugin = plugin;
        this.layer = layer;
        this.region = region;
        this.tileset = tileset;
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        this.face = face;
    }

    @Override
    public void execute() {
        oldTile = layer.getTile(tileset, x, y, z, size, face);
        layer.paint(tileset, x, y, z, size, face, tileset.createTile(region));
        plugin.recreateMeshes();
    }

    @Override
    public void undo() {
        layer.paint(tileset, x, y, z, size, face, oldTile);
        plugin.recreateMeshes();
    }
}
