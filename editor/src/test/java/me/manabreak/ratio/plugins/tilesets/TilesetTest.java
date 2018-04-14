package me.manabreak.ratio.plugins.tilesets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.manabreak.ratio.plugins.level.Tile;
import me.manabreak.ratio.test.GdxTest;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TilesetTest extends GdxTest {

    @Test
    public void testCreatePalette() {
        PaletteTileset p = new PaletteTileset("Pal");
        assertEquals("Pal", p.getName());
        assertEquals(Color.BLACK, p.getColorAt(0, 0));

        p.draw(0, 0, Color.TAN);
        assertEquals(Color.TAN, p.getColorAt(0, 0));

        final Map<PaletteTileset.Tuple, Color> entries = p.getEntries();
        assertEquals(1, entries.size());

        assertNotNull(p.getTexture());
        final Tile tile = p.createTile(new TextureRegion(p.getTexture()));
        assertNotNull(tile);
        assertEquals(1, p.getTiles().size);
        assertEquals(1, p.size());
        assertEquals(0, tile.getId());

        // Re-create the same tile; should return the same tile
        final Tile tile2 = p.createTile(new TextureRegion(p.getTexture()));
        assertEquals(tile, tile2);
        assertEquals(1, p.getTiles().size);
        assertEquals(1, p.size());
        assertEquals(0, tile2.getId());

        // Remove the tile
        p.removeTile(0);
        assertEquals(0, p.getTiles().size);
    }
}