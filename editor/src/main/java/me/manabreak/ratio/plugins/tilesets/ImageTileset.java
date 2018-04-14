package me.manabreak.ratio.plugins.tilesets;

import com.badlogic.gdx.graphics.Texture;

public class ImageTileset extends Tileset {
    private final String path;

    public ImageTileset(String name, String path, Texture texture) {
        super(name, texture);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
