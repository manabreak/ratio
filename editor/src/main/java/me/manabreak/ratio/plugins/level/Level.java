package me.manabreak.ratio.plugins.level;

import me.manabreak.ratio.plugins.tilesets.Tileset;

import java.util.ArrayList;
import java.util.List;

public class Level {
    private final ArrayList<Tileset> tilesets = new ArrayList<>();
    private final ArrayList<TileLayer> layers = new ArrayList<>();

    public Level() {

    }

    public void addTileset(Tileset tileset) {
        this.tilesets.add(tileset);
    }

    public Tileset getTileset(int index) {
        return tilesets.get(index);
    }

    public Tileset getTileset(String name) {
        for (Tileset tileset : tilesets) {
            if (tileset.getName().equals(name)) return tileset;
        }
        return null;
    }

    public TileLayer createLayer(String name) {
        TileLayer layer = new TileLayer(name);
        layers.add(layer);
        return layer;
    }

    public void deleteLayer(TileLayer layer) {
        layers.remove(layer);
    }

    public TileLayer get(String name) {
        for (TileLayer layer : layers) {
            if (layer.getName().equals(name)) return layer;
        }
        throw new IllegalArgumentException("No such layer " + name);
    }

    public ArrayList<TileLayer> getLayers() {
        return layers;
    }

    public void addLayer(int i, TileLayer layer) {
        this.layers.add(i, layer);
    }

    public List<Tileset> getTilesets() {
        return tilesets;
    }
}
