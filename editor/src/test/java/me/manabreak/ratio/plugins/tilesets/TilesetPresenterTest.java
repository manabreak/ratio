package me.manabreak.ratio.plugins.tilesets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TilesetPresenterTest {

    @Mock
    private TilesetPlugin plugin;

    @Mock
    private TilesetManager manager;

    @Mock
    private TilesetUi view;

    private TilesetPresenter presenter;

    @Before
    public void setUp() throws Exception {
        presenter = new TilesetPresenter(plugin, manager);
        presenter.attachView(view);
    }

    @Test
    public void testLoadTileset() {
        ImageTileset tileset = mock(ImageTileset.class);
        when(manager.load(any())).thenReturn(tileset);

        FileHandle fh = mock(FileHandle.class);
        presenter.loadTileset(fh);

        verify(manager).load(eq(fh));
        verify(view).createTab(eq(tileset));
        verify(view).createPicker(eq(tileset), eq(16), eq(16));
    }

    @Test
    public void testCreatePalette() {
        PaletteTileset tileset = mock(PaletteTileset.class);
        when(manager.createPalette(anyString())).thenReturn(tileset);

        presenter.createPalette();
        verify(view).promptPaletteName();

        presenter.createPalette("Test");
        verify(manager).createPalette(eq("Test"));
        verify(view).createTab(eq(tileset));
        verify(view).createPicker(eq(tileset), eq(1), eq(1));
    }

    @Test
    public void testSelectTileset() {
        Tileset tileset = mock(Tileset.class);
        presenter.selectTileset(tileset);
        verify(manager).setCurrentTileset(eq(tileset));
    }

    @Test
    public void testTileSizeChanged() {
        Tileset tileset = mock(Tileset.class);
        FileHandle fh = mock(FileHandle.class);
        when(manager.load(any())).thenReturn(tileset);
        when(manager.getCurrentTileset()).thenReturn(tileset);
        presenter.loadTileset(fh);
        verify(view).createPicker(eq(tileset), eq(16), eq(16));

        presenter.tileWidthChanged(8);
        verify(view).createPicker(eq(tileset), eq(8), eq(16));

        presenter.tileHeightChanged(4);
        verify(view).createPicker(eq(tileset), eq(8), eq(4));
    }

    @Test
    public void testSelectRegion() {
        TextureRegion region = mock(TextureRegion.class);
        TestObserver<TextureRegion> test = presenter.getRegionSubject().test();
        presenter.selectRegion(region);
        test.assertValue(region);
    }

    @Test
    public void testAutomapChanged() {
        TestObserver<Boolean> test = presenter.getAutomapSubject().test();
        presenter.automapChanged(true);
        presenter.automapChanged(false);
        test.assertValues(true, false);
    }

    @Test
    public void testLevelLoaded() {
        List<Tileset> tilesets = new ArrayList<>();
        ImageTileset imageTileset = mock(ImageTileset.class);
        PaletteTileset paletteTileset = mock(PaletteTileset.class);
        tilesets.add(imageTileset);
        tilesets.add(paletteTileset);
        when(manager.getTilesets()).thenReturn(tilesets);
        presenter.levelLoaded();

        verify(view).createTab(eq(imageTileset));
        verify(view).createPicker(eq(imageTileset), eq(16), eq(16));

        verify(view).createTab(eq(paletteTileset));
        verify(view).createPicker(eq(paletteTileset), eq(1), eq(1));
    }
}