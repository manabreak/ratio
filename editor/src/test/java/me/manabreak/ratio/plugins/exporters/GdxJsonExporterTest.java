package me.manabreak.ratio.plugins.exporters;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import me.manabreak.ratio.common.Properties;
import me.manabreak.ratio.plugins.level.Level;
import me.manabreak.ratio.plugins.level.Tile;
import me.manabreak.ratio.plugins.level.TileLayer;
import me.manabreak.ratio.plugins.objects.GameObject;
import me.manabreak.ratio.plugins.tilesets.ImageTileset;
import me.manabreak.ratio.plugins.tilesets.PaletteTileset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GdxJsonExporterTest {

    @Mock
    private FileHandle fh;

    @Mock
    private ImageTileset imageTileset;

    @Mock
    private PaletteTileset paletteTileset;

    @Mock
    private TextureRegion imageRegion;

    @Mock
    private TextureRegion paletteRegion;

    @Captor
    private ArgumentCaptor<String> captor;

    private GdxJsonExporter exporter;

    @Before
    public void setUp() {
        exporter = new GdxJsonExporter();

        FileHandle parentFh = mock(FileHandle.class);
        when(parentFh.path()).thenReturn("/home/ratio");
        when(fh.parent()).thenReturn(parentFh);

        IntMap<Tile> tilemap = new IntMap<>();
        tilemap.put(1, new Tile(1, imageRegion));
        tilemap.put(2, new Tile(2, imageRegion));
        when(imageTileset.getTiles()).thenReturn(tilemap);
        when(imageTileset.getPath()).thenReturn("/home/ratio/tilesets/tileset.png");

        Map<PaletteTileset.Tuple, Color> paletteEntries = new HashMap<>();
        paletteEntries.put(new PaletteTileset.Tuple(0, 0), new Color(1f, 0f, 0f, 1f));
        paletteEntries.put(new PaletteTileset.Tuple(1, 0), new Color(0f, 1f, 0f, 1f));
        paletteEntries.put(new PaletteTileset.Tuple(0, 1), new Color(0f, 0f, 1f, 1f));
        when(paletteTileset.getEntries()).thenReturn(paletteEntries);

        IntMap<Tile> palettemap = new IntMap<>();
        palettemap.put(1, new Tile(1, paletteRegion));
        palettemap.put(2, new Tile(2, paletteRegion));
        palettemap.put(3, new Tile(3, paletteRegion));
        when(paletteTileset.getTiles()).thenReturn(palettemap);
    }

    @Test
    public void testExporting() {
        Level level = new Level();
        final TileLayer layer = level.createLayer("Test");

        // Set up properties for the layer
        layer.getProperties().setProperty("LayerIntProperty", 12345);

        // Draw few tiles with image tileset
        layer.draw(imageTileset, 0, 0, 0, 16);
        layer.draw(imageTileset, 16, 0, 0, 16);

        // ...and a few with palette
        layer.draw(paletteTileset, 0, 16, 0, 8);
        layer.draw(paletteTileset, 8, 16, 0, 8);

        List<GameObject> objectList = new ArrayList<>();
        GameObject obj1 = new GameObject();
        obj1.setName("Test1");
        obj1.setPosition(1f, 2f, 3f);
        obj1.setSize(5f, 6f, 7f);
        obj1.setVisible(true);
        obj1.setColor(new Color(0.4f, 0.6f, 0.8f, 0.1f));
        obj1.setProperty("TestStr", "Foobar");
        obj1.setProperty("TestInt", 42);
        obj1.setProperty("TestBoolTrue", true);
        obj1.setProperty("TestBoolFalse", false);
        obj1.setProperty("TestDouble", 3.14);
        objectList.add(obj1);

        Properties properties = new Properties();
        exporter.save(level, objectList, properties, fh);
        verify(fh).writeString(captor.capture(), eq(false));
        String jsonString = captor.getValue();
        System.out.println(jsonString);

        JsonValue val = new JsonReader().parse(jsonString);

        assertTrue(val.has("tilesets"));
        JsonValue tilesets = val.get("tilesets");
        assertEquals(2, tilesets.size);

        JsonValue imageTilesetPart = tilesets.get(0);
        assertEquals("normal", imageTilesetPart.getString("type"));
        assertEquals(2, imageTilesetPart.get("tiles").size);

        JsonValue paletteTilesetPart = tilesets.get(1);
        assertEquals("palette", paletteTilesetPart.getString("type"));
        assertEquals(3, paletteTilesetPart.get("tiles").size);

        assertTrue(val.has("layers"));
        JsonValue layers = val.get("layers");
        assertEquals(1, layers.size);

        JsonValue layerJson = layers.get(0);
        assertTrue(layerJson.has("properties"));
        assertEquals(1, layerJson.get("properties").size);

        JsonValue layerProperty = layerJson.get("properties").get(0);
        assertEquals("LayerIntProperty", layerProperty.getString("key"));
        assertEquals("int", layerProperty.getString("type"));
        assertEquals(12345, layerProperty.getInt("value"));

        assertTrue(layerJson.has("parts"));
        assertEquals(2, layerJson.get("parts").size);

        assertEquals(0, layerJson.get("parts").get(0).getInt("tileset"));
        assertEquals(1, layerJson.get("parts").get(1).getInt("tileset"));

        assertTrue(val.has("objects"));
        JsonValue objects = val.get("objects");
        assertEquals(1, objects.size);
        JsonValue o = objects.get(0);
        assertEquals("Test1", o.getString("name"));
        float[] location = o.get("location").asFloatArray();
        assertEquals(1f, location[0], 0.001f);
        assertEquals(2f, location[1], 0.001f);
        assertEquals(3f, location[2], 0.001f);

        float[] size = o.get("size").asFloatArray();
        assertEquals(5f, size[0], 0.001f);
        assertEquals(6f, size[1], 0.001f);
        assertEquals(7f, size[2], 0.001f);

        float[] color = o.get("color").asFloatArray();
        assertEquals(0.4f, color[0], 0.0001f);
        assertEquals(0.6f, color[1], 0.0001f);
        assertEquals(0.8f, color[2], 0.0001f);
        assertEquals(0.1f, color[3], 0.0001f);

        assertTrue(o.getBoolean("visible"));
    }
}