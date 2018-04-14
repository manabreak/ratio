package me.manabreak.ratio.plugins.tilesets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;
import me.manabreak.ratio.plugins.level.Tile;

public abstract class Tileset {
    private final String name;
    private final Texture texture;
    private final IntMap<Tile> tiles = new IntMap<>();

    public Tileset(String name, Texture texture) {
        this.name = name;
        this.texture = texture;
    }

    public String getName() {
        return name;
    }

    public Texture getTexture() {
        return texture;
    }

    public IntMap<Tile> getTiles() {
        return tiles;
    }

    public Tile getTile(int id) {
        return tiles.get(id);
    }

    public void putTile(int id, Tile tile) {
        tiles.put(id, tile);
    }

    public void removeTile(int id) {
        tiles.remove(id);
    }

    public int size() {
        return tiles.size;
    }

    public Tile createTile(TextureRegion region) {
        for (Tile tile : tiles.values()) {
            if (region.getRegionX() == tile.getX() && region.getRegionY() == tile.getY() &&
                    region.getRegionWidth() == tile.getWidth() && region.getRegionHeight() == tile.getHeight()) {
                return tile;
            }
        }

        Tile tile = new Tile(tiles.size, region);
        tiles.put(tile.getId(), tile);
        return tile;
    }
}
