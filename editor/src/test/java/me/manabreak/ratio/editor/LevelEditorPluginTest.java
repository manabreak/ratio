package me.manabreak.ratio.editor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;
import me.manabreak.ratio.plugins.level.*;
import me.manabreak.ratio.plugins.tilesets.PaletteTileset;
import me.manabreak.ratio.plugins.tilesets.TilesetManager;
import me.manabreak.ratio.test.GdxTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LevelEditorPluginTest extends GdxTest {

    @Mock
    private EditorController controller;

    @Mock
    private TilesetManager tilesetManager;

    @Mock
    private LevelShader levelShader;

    @Mock
    private WireframeRenderer wireframeRenderer;

    @Mock
    private ObjectRenderer objectRenderer;

    @Mock
    private ToolRenderer toolRenderer;

    private PaletteTileset tileset;
    private LevelEditorPlugin plugin;

    @Before
    public void setUp() {
        tileset = new PaletteTileset("Test");
        tileset.draw(0, 0, new Color(1f, 0f, 0f, 1f));
        tileset.draw(1, 0, new Color(0f, 1f, 0f, 1f));
        tileset.draw(0, 1, new Color(0f, 0f, 1f, 1f));
        when(controller.getTilesetManager()).thenReturn(tilesetManager);
        when(tilesetManager.getCurrentTileset()).thenReturn(tileset);

        plugin = new LevelEditorPlugin(levelShader, wireframeRenderer, objectRenderer, toolRenderer);
        plugin.editorController = controller;
    }

    @Ignore
    @Test
    public void testUndoRedo_Draw_ChangeRegionInBetween() {
        TextureRegion tr0 = new TextureRegion(tileset.getTexture(), 0, 0, 1, 1);
        TextureRegion tr1 = new TextureRegion(tileset.getTexture(), 1, 0, 1, 1);

        // TODO FIXME Set the first select region to tr0
        assertEquals(0, tileset.getTiles().size);
        plugin.setCellCoord(0, 0, 0);
        plugin.setToolSize(16);
        plugin.touchUp(0, 0, 0, Input.Buttons.LEFT);

        // Assert that tileset got a new tile
        IntMap<Tile> tiles = tileset.getTiles();
        assertEquals(1, tiles.size);
        Tile tile = tiles.get(0);
        assertNotNull(tile);
        assertEquals(0, tile.getX());
        assertEquals(0, tile.getY());
        assertEquals(1, tile.getWidth());
        assertEquals(1, tile.getHeight());

        // Assert that level got a new cell at the right coordinates
        Level level = plugin.getLevel();
        final TileLayer layer = level.getLayers().get(0);
        assertEquals(1, layer.getParts().size());
        Octree<Cell> tree = layer.getParts().get(tileset);
        Cell cell = tree.get(0, 0, 0, 16);
        assertNotNull(cell);
        assertEquals(0, cell.get(Face.FRONT).intValue());

        // Undo
        plugin.undo();

        cell = tree.get(0, 0, 0, 16);
        assertNull(cell);

        // Select new region before redoing // FIXME
        plugin.redo();

        cell = tree.get(0, 0, 0, 16);
        assertNotNull(cell);

        // Assert that the ID did not change from 0 to 1 when redoing the action
        assertEquals(0, cell.get(Face.FRONT).intValue());
    }

    @Ignore
    @Test
    public void testUndoRedo_Erase_ChangeRegionInBetween() {
        /*
        TextureRegion tr0 = new TextureRegion(tileset.getTexture(), 0, 0, 1, 1);
        TextureRegion tr1 = new TextureRegion(tileset.getTexture(), 1, 0, 1, 1);

        assertEquals(0, tileset.getTiles().size);
        plugin.keyDown(Input.Keys.D);
        plugin.setCellCoord(0, 0, 0);
        plugin.setToolSize(16);
        plugin.getLevel().setCurrentRegion(tr0);
        plugin.touchUp(0, 0, 0, Input.Buttons.LEFT);

        // Assert that tileset got a new tile
        IntMap<Tile> tiles = tileset.getTiles();
        assertEquals(1, tiles.size);
        Tile tile = tiles.get(0);
        assertNotNull(tile);
        assertEquals(0, tile.getX());
        assertEquals(0, tile.getY());
        assertEquals(1, tile.getWidth());
        assertEquals(1, tile.getHeight());

        // Assert that level got a new cell at the right coordinates
        Level level = plugin.getLevel();
        assertEquals(1, level.getParts().size());
        Octree<Cell> tree = level.getParts().get(tileset);
        Cell cell = tree.get(0, 0, 0, 16);
        assertNotNull(cell);
        assertEquals(0, cell.get(Face.FRONT).intValue());

        // Erase
        plugin.keyDown(Input.Keys.E);
        plugin.touchUp(0, 0, 0, Input.Buttons.LEFT);

        cell = tree.get(0, 0, 0, 16);
        assertNull(cell);

        // Select new region before undoing
        plugin.getLevel().setCurrentRegion(tr1);
        plugin.undo();

        cell = tree.get(0, 0, 0, 16);
        assertNotNull(cell);

        // Assert that the ID did not change from 0 to 1 when redoing the action
        assertEquals(0, cell.get(Face.FRONT).intValue());
        */
    }
}