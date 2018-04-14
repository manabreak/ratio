package me.manabreak.ratio.plugins.level;

import me.manabreak.ratio.plugins.tilesets.ImageTileset;
import me.manabreak.ratio.plugins.tilesets.Tileset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class LevelTest {

    @Mock
    private ImageTileset tileset;

    private Level level;

    @Before
    public void setUp() throws Exception {
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
}