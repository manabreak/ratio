package me.manabreak.ratio.plugins.level;

import java.util.ArrayList;

public class Level {
    private final ArrayList<TileLayer> layers = new ArrayList<>();

    public Level() {

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
}
