package me.manabreak.ratio.plugins.tilesets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import io.reactivex.subjects.PublishSubject;
import me.manabreak.ratio.plugins.level.AutomapRule;
import me.manabreak.ratio.plugins.level.Tile;
import me.manabreak.ratio.plugins.level.Tile.Form;

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
        tileset.addAutomapRule(16, new AutomapRule(16)
                .set(Form.TOP_SINGLE, Tile.from(t, 1, 0, 0, 16, 8))
                .set(Form.TOP_HORIZONTAL_END_WEST, Tile.from(t, 2, 16, 0, 16, 8))
                .set(Form.TOP_HORIZONTAL, Tile.from(t, 3, 32, 0, 16, 8))
                .set(Form.TOP_HORIZONTAL_END_EAST, Tile.from(t, 4, 48, 0, 16, 8))
                .set(Form.TOP_VERTICAL_END_NORTH, Tile.from(t, 5, 64, 0, 16, 8))
                .set(Form.TOP_CORNER_NW, Tile.from(t, 6, 80, 0, 16, 8))
                .set(Form.TOP_CORNER_NE, Tile.from(t, 7, 96, 0, 16, 8))
                .set(Form.TOP_VERTICAL, Tile.from(t, 8, 112, 0, 16, 8))
                .set(Form.TOP_CORNER_SW, Tile.from(t, 9, 128, 0, 16, 8))
                .set(Form.TOP_CORNER_SE, Tile.from(t, 10, 144, 0, 16, 8))
                .set(Form.TOP_VERTICAL_END_SOUTH, Tile.from(t, 11, 160, 0, 16, 8))
                .set(Form.TOP_TRI_E, Tile.from(t, 11, 176, 0, 16, 8))
                .set(Form.TOP_TRI_W, Tile.from(t, 12, 192, 0, 16, 8))
                .set(Form.TOP_CROSS, Tile.from(t, 13, 208, 0, 16, 8))
                .set(Form.TOP_TRI_N, Tile.from(t, 14, 224, 0, 16, 8))
                .set(Form.TOP_TRI_S, Tile.from(t, 15, 240, 0, 16, 8))

                .set(Form.SIDE_SINGLE, Tile.from(t, 16, 0, 64, 16, 16))
                .set(Form.SIDE_END_WEST, Tile.from(t, 17, 16, 64, 16, 16))
                .set(Form.SIDE, Tile.from(t, 18, 32, 64, 16, 16))
                .set(Form.SIDE_END_EAST, Tile.from(t, 19, 48, 64, 16, 16))

                .set(Form.OTHER, Tile.from(t, 33, 0, 8, 16, 8))
        );
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

    public Tileset getTileset(String name) {
        for (Tileset tileset : tilesets.values()) {
            if (tileset.getName().equals(name)) return tileset;
        }

        throw new IllegalArgumentException("No such tileset " + name);
    }

    public void loadAll() {
        FileHandle[] pngs = Gdx.files.internal("assets/tilesets").list("png");
        for (FileHandle png : pngs) {
            load(png);
        }
    }

    public io.reactivex.Observable<Tileset> getTilesetSubject() {
        return tilesetSubject;
    }
}
