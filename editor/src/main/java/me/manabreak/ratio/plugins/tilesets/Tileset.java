package me.manabreak.ratio.plugins.tilesets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;
import me.manabreak.ratio.plugins.level.AutomapMissingException;
import me.manabreak.ratio.plugins.level.AutomapRule;
import me.manabreak.ratio.plugins.level.Tile;

public abstract class Tileset {
    private final String name;
    private final Texture texture;
    private final IntMap<Tile> tiles = new IntMap<>();
    private final IntMap<AutomapRule> automapRules = new IntMap<>();

    public Tileset(String name, Texture texture) {
        this.name = name;
        this.texture = texture;
    }

    public void addAutomapRule(int size, AutomapRule rule) {
        automapRules.put(size, rule);
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

    public Tile getAutomapTile(int i, int size) throws AutomapMissingException {
        System.out.println("getAutomapTile: " + i);
        if (!automapRules.containsKey(size)) {
            throw new AutomapMissingException("Automap rules missing for size " + size);
        }

        AutomapRule rule = automapRules.get(size);
        Tile.Form form = Tile.Form.values()[i];
        System.out.println("Form: " + form.name());
        Tile tile = rule.getTile(form);
        if (tile == null) throw new AutomapMissingException("Automap missing rule for form " + form.name());

        System.out.println("i: " + i + ", region: " + tile.getX() + ", " + tile.getY() + ", " + tile.getWidth() + ", " + tile.getHeight());
        putTile(tile.getId(), tile);

        return tile;
    }
}
