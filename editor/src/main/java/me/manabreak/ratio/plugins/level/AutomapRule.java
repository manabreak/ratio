package me.manabreak.ratio.plugins.level;

import java.util.EnumMap;
import java.util.Map;

public class AutomapRule {

    private final int size;
    private final Map<Tile.Form, Tile> tiles = new EnumMap<>(Tile.Form.class);

    public AutomapRule(int size) {
        this.size = size;
    }

    public AutomapRule set(Tile.Form form, Tile tile) {
        tiles.put(form, tile);
        tile.setForm(form);
        return this;
    }

    public int getSize() {
        return size;
    }

    public Tile getTile(Tile.Form form) {
        return tiles.get(form);
    }
}
