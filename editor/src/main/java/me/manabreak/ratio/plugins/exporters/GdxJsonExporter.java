package me.manabreak.ratio.plugins.exporters;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.JsonValue;
import me.manabreak.ratio.common.Properties;
import me.manabreak.ratio.plugins.level.*;
import me.manabreak.ratio.plugins.objects.GameObject;
import me.manabreak.ratio.plugins.tilesets.ImageTileset;
import me.manabreak.ratio.plugins.tilesets.PaletteTileset;
import me.manabreak.ratio.plugins.tilesets.Tileset;
import me.manabreak.ratio.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * LibGDX JSON format exporter.
 * <p>
 * Exports levels to LibGDX's JSON flavor. Currently supports the following data:
 * - Octrees (cells and their sizes)
 * - Image-based tilesets
 * - Palette tilesets (Each entry in the palette is written; no image files are created)
 * - Tile data
 * - Objects
 *
 * @author Harri Pellikka
 */
public class GdxJsonExporter implements Exporter {

    private static final String EXT = "txt";
    private static final String NAME = "LibGDX JSON Text File";

    @Override
    public String getFileExtension() {
        return EXT;
    }

    @Override
    public String getFormatName() {
        return NAME;
    }

    @Override
    public void save(Level level, List<GameObject> objects, Properties properties, FileHandle fh) {
        System.out.println("GdxJsonExporter: Saving level to " + fh.toString());
        fh.writeString(getJsonString(level, objects, properties), false);
    }

    private String getJsonString(Level level, List<GameObject> objects, Properties properties) {
        if (level == null) return "{}";

        JsonValue root = new JsonValue(JsonValue.ValueType.object);

        List<Tileset> tilesets = new ArrayList<>();
        for (TileLayer layer : level.getLayers()) {
            for (Tileset tileset : layer.getParts().keySet()) {
                if (!tilesets.contains(tileset)) {
                    tilesets.add(tileset);
                }
            }
        }

        JsonValue tilesetsJson = new JsonValue(JsonValue.ValueType.array);
        for (Tileset tileset : tilesets) {
            // Tileset
            writeTileset(tileset, tilesetsJson);
        }
        root.addChild("tilesets", tilesetsJson);

        // Level properties
        writeProperties(level, properties, root);

        // Tileset and cell data
        JsonValue jsonLayers = new JsonValue(JsonValue.ValueType.array);
        for (TileLayer layer : level.getLayers()) {
            JsonValue jsonLayer = new JsonValue(JsonValue.ValueType.object);
            jsonLayer.addChild("name", new JsonValue(layer.getName()));
            jsonLayer.addChild("visible", new JsonValue(layer.isVisible()));
            writeProperties(level, layer.getProperties(), jsonLayer);

            JsonValue jsonParts = new JsonValue(JsonValue.ValueType.array);
            for (Tileset tileset : layer.getParts().keySet()) {
                JsonValue jsonPart = new JsonValue(JsonValue.ValueType.object);

                // Index of the tileset in the tileset json portion
                jsonPart.addChild("tileset", new JsonValue(tilesets.indexOf(tileset)));

                // Tree
                Octree<Cell> octree = layer.getParts().get(tileset);
                List<Cell> cells = octree.flatten();

                // Cells
                JsonValue jsonCells = new JsonValue(JsonValue.ValueType.array);
                for (Cell cell : cells) {
                    writeCell(jsonCells, cell);
                }
                jsonPart.addChild("cells", jsonCells);

                // Part done, write it to parent
                jsonParts.addChild(jsonPart);
            }
            jsonLayer.addChild("parts", jsonParts);
            jsonLayers.addChild(jsonLayer);
        }
        root.addChild("layers", jsonLayers);


        // Objects
        JsonValue jsonObjects = new JsonValue(JsonValue.ValueType.array);
        for (GameObject obj : objects) {
            JsonValue jsonObj = new JsonValue(JsonValue.ValueType.object);
            jsonObj.addChild("name", new JsonValue(obj.getName()));

            if (!TextUtils.isNullOrEmpty(obj.getType())) {
                jsonObj.addChild("type", new JsonValue(obj.getType()));
            }

            writeArray(jsonObj, "location", obj.getX(), obj.getY(), obj.getZ());
            writeArray(jsonObj, "size", obj.getSizeX(), obj.getSizeY(), obj.getSizeZ());
            writeArray(jsonObj, "color", obj.getColor().r, obj.getColor().g, obj.getColor().b, obj.getColor().a);

            jsonObj.addChild("visible", new JsonValue(obj.isVisible()));

            writeProperties(level, obj.getProperties(), jsonObj);

            jsonObjects.addChild(jsonObj);
        }
        root.addChild("objects", jsonObjects);

        return root.toString();
    }

    private void writeArray(JsonValue parent, String location, float... values) {
        JsonValue locArray = new JsonValue(JsonValue.ValueType.array);
        for (float v : values) {
            locArray.addChild(new JsonValue(v));
        }
        parent.addChild(location, locArray);
    }

    private void writeProperties(Level level, Properties properties, JsonValue root) {
        JsonValue props = new JsonValue(JsonValue.ValueType.array);
        for (Properties.Entry entry : properties.getProperties()) {
            JsonValue prop = new JsonValue(JsonValue.ValueType.object);

            String key = entry.getKey();
            prop.addChild("key", new JsonValue(key));

            Object value = entry.getValue();
            if (value instanceof String) {
                prop.addChild("type", new JsonValue("string"));
                prop.addChild("value", new JsonValue((String) value));
            } else if (value instanceof Double) {
                prop.addChild("type", new JsonValue("double"));
                prop.addChild("value", new JsonValue((Double) value));
            } else if (value instanceof Boolean) {
                prop.addChild("type", new JsonValue("boolean"));
                prop.addChild("value", new JsonValue((Boolean) value));
            } else if (value instanceof Integer) {
                prop.addChild("type", new JsonValue("int"));
                prop.addChild("value", new JsonValue((Integer) value));
            } else {
                System.out.println("Warning: Unknown property type " + value.getClass().getSimpleName() + " for key " + key);
                continue;
            }
            props.addChild(prop);
        }

        root.addChild("properties", props);
    }

    private void writeCell(JsonValue root, Cell cell) {
        JsonValue jsonCell = new JsonValue(JsonValue.ValueType.object);
        jsonCell.addChild("x", new JsonValue(cell.getX()));
        jsonCell.addChild("y", new JsonValue(cell.getY()));
        jsonCell.addChild("z", new JsonValue(cell.getZ()));
        jsonCell.addChild("size", new JsonValue(cell.getSize()));
        jsonCell.addChild("floor", new JsonValue(cell.getType() == Cell.Type.FLOOR));

        JsonValue jsonFaces = new JsonValue(JsonValue.ValueType.array);
        jsonFaces.addChild(new JsonValue(cell.get(Face.FRONT)));
        jsonFaces.addChild(new JsonValue(cell.get(Face.BACK)));
        jsonFaces.addChild(new JsonValue(cell.get(Face.LEFT)));
        jsonFaces.addChild(new JsonValue(cell.get(Face.RIGHT)));
        jsonFaces.addChild(new JsonValue(cell.get(Face.TOP)));
        jsonCell.addChild("faces", jsonFaces);

        root.addChild(jsonCell);
    }

    private void writeTileset(Tileset tileset, JsonValue root) {
        JsonValue jsonTileset = new JsonValue(JsonValue.ValueType.object);
        jsonTileset.addChild("name", new JsonValue(tileset.getName()));
        jsonTileset.addChild("type", new JsonValue((tileset instanceof PaletteTileset) ? "palette" : "normal"));

        // If palette, write the color indexes
        if (tileset instanceof PaletteTileset) {
            JsonValue paletteEntries = new JsonValue(JsonValue.ValueType.array);
            PaletteTileset pt = (PaletteTileset) tileset;
            Map<PaletteTileset.Tuple, Color> entries = pt.getEntries();
            for (Map.Entry<PaletteTileset.Tuple, Color> entry : entries.entrySet()) {
                PaletteTileset.Tuple xy = entry.getKey();
                Color color = entry.getValue();

                JsonValue paletteEntry = new JsonValue(JsonValue.ValueType.object);
                paletteEntry.addChild("x", new JsonValue(xy.x));
                paletteEntry.addChild("y", new JsonValue(xy.y));
                paletteEntry.addChild("color", new JsonValue(color.toString()));
                paletteEntries.addChild(paletteEntry);
            }
            jsonTileset.addChild("colors", paletteEntries);
        } else if (tileset instanceof ImageTileset) {
            ImageTileset it = (ImageTileset) tileset;
            jsonTileset.addChild("path", new JsonValue(it.getPath()));
        }

        // Tile data
        JsonValue tiles = new JsonValue(JsonValue.ValueType.array);
        for (IntMap.Entry<Tile> entry : tileset.getTiles()) {
            int id = entry.key;
            Tile tile = entry.value;

            JsonValue t = new JsonValue(JsonValue.ValueType.object);
            t.addChild("id", new JsonValue(id));
            t.addChild("x", new JsonValue(tile.getX()));
            t.addChild("y", new JsonValue(tile.getY()));
            t.addChild("w", new JsonValue(tile.getWidth()));
            t.addChild("h", new JsonValue(tile.getHeight()));
            t.addChild("form", new JsonValue(tile.getForm().name()));
            tiles.addChild(t);
        }
        jsonTileset.addChild("tiles", tiles);

        root.addChild(jsonTileset);
    }
}
