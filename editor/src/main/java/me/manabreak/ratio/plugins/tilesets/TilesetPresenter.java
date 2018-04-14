package me.manabreak.ratio.plugins.tilesets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.reactivex.subjects.PublishSubject;
import me.manabreak.ratio.common.mvp.MvpPresenter;

import java.util.Collection;

public class TilesetPresenter extends MvpPresenter<TilesetUi> {

    private final TilesetPlugin plugin;
    private final TilesetManager manager;
    private final PublishSubject<Boolean> automapSubject = PublishSubject.create();
    private final PublishSubject<TextureRegion> regionSubject = PublishSubject.create();

    private int toolSize = 16;
    private int tileWidth = 16, tileHeight = 16;
    private TextureRegion currentRegion;

    public TilesetPresenter(TilesetPlugin plugin, TilesetManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }


    public void loadTileset(FileHandle file) {
        Tileset tileset = manager.load(file);
        view.createTab(tileset);
        boolean palette = tileset instanceof PaletteTileset;
        view.createPicker(tileset, palette ? 1 : tileWidth, palette ? 1 : tileHeight);
    }

    public void createPalette() {
        view.promptPaletteName();
    }

    public void createPalette(String name) {
        PaletteTileset tileset = manager.createPalette(name);
        tileset.draw(0, 0, Color.RED);
        tileset.draw(0, 1, Color.GREEN);
        tileset.draw(1, 0, Color.BLUE);
        tileset.draw(1, 1, Color.YELLOW);
        view.createTab(tileset);
        view.createPicker(tileset, 1, 1);
    }

    public void selectTileset(Tileset tileset) {
        manager.setCurrentTileset(tileset);
    }

    public void tileWidthChanged(int width) {
        this.tileWidth = width;
        boolean palette = manager.getCurrentTileset() instanceof PaletteTileset;
        view.createPicker(manager.getCurrentTileset(), palette ? 1 : tileWidth, palette ? 1 : tileHeight);
    }

    public void tileHeightChanged(int height) {
        this.tileHeight = height;
        boolean palette = manager.getCurrentTileset() instanceof PaletteTileset;
        view.createPicker(manager.getCurrentTileset(), palette ? 1 : tileWidth, palette ? 1 : tileHeight);
    }

    public void automapChanged(boolean checked) {
        automapSubject.onNext(checked);
    }

    public void selectRegion(TextureRegion region) {
        this.currentRegion = region;
        regionSubject.onNext(region);
    }

    public PublishSubject<Boolean> getAutomapSubject() {
        return automapSubject;
    }

    public PublishSubject<TextureRegion> getRegionSubject() {
        return regionSubject;
    }

    public void levelLoaded() {
        Collection<Tileset> tilesets = manager.getTilesets();
        for (Tileset tileset : tilesets) {
            view.createTab(tileset);
            boolean palette = tileset instanceof PaletteTileset;
            view.createPicker(tileset, palette ? 1 : tileWidth, palette ? 1 : tileHeight);
        }
    }

    public TextureRegion getCurrentRegion() {
        return this.currentRegion;
    }
}
