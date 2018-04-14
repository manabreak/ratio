package me.manabreak.ratio.plugins.tilesets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import io.reactivex.subjects.PublishSubject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TilesetManager {
    private final Map<String, Texture> textures = new HashMap<>();
    private final Map<Texture, Tileset> tilesets = new HashMap<>();
    private final PublishSubject<Tileset> tilesetSubject = PublishSubject.create();

    private Tileset currentTileset = null;

    public PaletteTileset createPalette(String name) {
        if (textures.containsKey(name)) {
            return (PaletteTileset) tilesets.get(textures.get(name));
        }

        PaletteTileset t = new PaletteTileset(name);
        tilesets.put(t.getTexture(), t);
        textures.put(name, t.getTexture());
        return t;
    }

    public Tileset load(FileHandle handle) {
        String path = handle.path();
        if (textures.containsKey(path)) return tilesets.get(textures.get(path));

        Texture t = new Texture(handle, false);
        textures.put(path, t);

        Tileset tileset = new ImageTileset(handle.nameWithoutExtension(), Gdx.files.absolute(path).path(), t);
        tilesets.put(t, tileset);

        currentTileset = tileset;
        return tileset;
    }

    public Collection<Tileset> getTilesets() {
        return tilesets.values();
    }

    public Tileset getCurrentTileset() {
        return currentTileset;
    }

    public void setCurrentTileset(Tileset currentTileset) {
        this.currentTileset = currentTileset;
        tilesetSubject.onNext(currentTileset);
    }
}
