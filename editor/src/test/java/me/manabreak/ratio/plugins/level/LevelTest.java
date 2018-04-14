package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.manabreak.ratio.plugins.tilesets.ImageTileset;
import me.manabreak.ratio.plugins.tilesets.Tileset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LevelTest {

    @Mock
    private ImageTileset tileset;

    private Level level;

    @Before
    public void setUp() {
        level = new Level();
    }

    @Test
    public void testDrawEraseBlock() {
        assertEquals(0, level.getLayers().size());
        final TileLayer layer = level.createLayer("Test");
        assertEquals(0, layer.getParts().size());

        layer.draw(tileset, 0, 0, 0, 16);
        assertEquals(1, layer.getParts().size());

        Map<Tileset, Octree<Cell>> parts = layer.getParts();
        assertTrue(parts.containsKey(tileset));

        Octree<Cell> tree = parts.get(tileset);
        assertEquals(1, tree.getItemCount());
        Cell cell = tree.get(0, 0, 0, 16);
        assertNotNull(cell);
        for (Face face : Face.values()) {
            assertEquals(0, cell.get(face).intValue());
            assertEquals(Cell.Type.BLOCK, cell.getType());
        }

        layer.erase(tileset, 0, 0, 0, 16);
        assertEquals(0, tree.getItemCount());
    }

    @Test
    public void testDrawFloor() {
        final TileLayer layer = level.createLayer("Test");
        layer.drawFloor(tileset, 0, 0, 0, 16);

        Map<Tileset, Octree<Cell>> parts = layer.getParts();
        assertEquals(1, parts.size());
        assertTrue(parts.containsKey(tileset));
        Octree<Cell> tree = parts.get(tileset);
        assertEquals(1, tree.getItemCount());
        Cell cell = tree.get(0, 0, 0, 16);
        assertNotNull(cell);
        assertEquals(Cell.Type.FLOOR, cell.getType());

        layer.erase(tileset, 0, 0, 0, 16);
        assertEquals(0, tree.getItemCount());
    }

    @Test
    public void testCreateDeleteLayer() {
        assertEquals(0, level.getLayers().size());

        final TileLayer a = level.createLayer("A");
        assertEquals(1, level.getLayers().size());

        final TileLayer b = level.createLayer("B");
        assertEquals(2, level.getLayers().size());

        final TileLayer otherA = level.get("A");
        assertEquals(a, otherA);

        TileLayer manuallyCreated = new TileLayer("Manual");
        level.addLayer(0, manuallyCreated);
        assertEquals(3, level.getLayers().size());
        assertEquals(manuallyCreated, level.getLayers().get(0));

        level.deleteLayer(a);
        assertEquals(2, level.getLayers().size());
        level.deleteLayer(b);
        level.deleteLayer(manuallyCreated);
        assertEquals(0, level.getLayers().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLayer_noSuchLayer() {
        level.createLayer("bar");
        level.get("foo");
    }

    @Test
    public void testVisibility() {
        final TileLayer a = level.createLayer("A");
        final TileLayer b = level.createLayer("B");
        assertTrue(a.isVisible());
        assertTrue(b.isVisible());
        a.setVisible(false);
        assertFalse(a.isVisible());
        assertTrue(b.isVisible());
    }

    @Test
    public void testSetName() {
        final TileLayer a = level.createLayer("A");
        assertEquals("A", a.getName());
        a.setName("B");
        assertEquals("B", a.getName());
        assertEquals(a, level.get("B"));
    }

    @Test
    public void testGetTile() {
        Tile mockTile = mock(Tile.class);
        when(tileset.getTile(anyInt())).thenReturn(mockTile);
        final TileLayer a = level.createLayer("A");

        // Empty layer, no tiles
        assertNull(a.getTile(tileset, 0, 0, 0, 16, Face.FRONT));

        a.draw(tileset, 0, 0, 0, 16);
        final Tile tile = a.getTile(tileset, 0, 0, 0, 16, Face.FRONT);
        assertNotNull(tile);
    }

    @Test
    public void testPaint() {
        Texture t = mock(Texture.class);
        Tileset tileset = new ImageTileset("tileset", "tileset", t);
        final Tile tile = tileset.createTile(new TextureRegion(t));
        final TileLayer a = level.createLayer("A");

        // No cell to paint; should fail silently
        a.paint(tileset, 0, 0, 0, 16, Face.FRONT, tile);

        a.draw(tileset, 0, 0, 0, 16);
        a.paint(tileset, 0, 0, 0, 16, Face.FRONT, tile);
        final Tile otherTile = a.getTile(tileset, 0, 0, 0, 16, Face.FRONT);
        assertEquals(tile, otherTile);
    }
}