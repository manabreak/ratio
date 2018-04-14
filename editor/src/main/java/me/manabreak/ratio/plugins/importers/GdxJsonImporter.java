package me.manabreak.ratio.plugins.importers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import me.manabreak.ratio.common.Properties;
import me.manabreak.ratio.plugins.level.Face;
import me.manabreak.ratio.plugins.level.Level;
import me.manabreak.ratio.plugins.level.Tile;
import me.manabreak.ratio.plugins.level.TileLayer;
import me.manabreak.ratio.plugins.objects.GameObject;
import me.manabreak.ratio.plugins.tilesets.PaletteTileset;
import me.manabreak.ratio.plugins.tilesets.Tileset;
import me.manabreak.ratio.plugins.tilesets.TilesetManager;
import me.manabreak.ratio.utils.Action1;

import java.util.ArrayList;
import java.util.List;

/**
 * LibGDX JSON format importer.
 * <p>
 * Imports levels stored in LibGDX's JSON flavor.
 */
public class GdxJsonImporter implements Importer {
    private static final String EXT = "txt";
    private static final String NAME = "LibGDX JSON Text File";
    private final TilesetManager tilesetManager;

    public GdxJsonImporter(TilesetManager tilesetManager) {
        this.tilesetManager = tilesetManager;
    }

    @Override
    public String getFileExtension() {
        return EXT;
    }

    @Override
    public String getFormatName() {
        return NAME;
    }

    @Override
    public Level load(FileHandle fh, Action1<List<GameObject>> objectCallback, Action1<Properties> propertiesCallback) {
        Level level = new Level();
        String json = fh.readString();
        JsonValue root = new JsonReader().parse(json);

        final JsonValue layers = root.get("layers");
        for (JsonValue jsonLayer : layers) {
            TileLayer layer = level.createLayer(jsonLayer.getString("name"));
            layer.setVisible(jsonLayer.getBoolean("visible", true));

            JsonValue parts = jsonLayer.get("parts");
            for (JsonValue part : parts) {
                Tileset tileset = parseTileset(fh, part.get("tileset"));

                JsonValue cells = part.get("cells");
                for (JsonValue cell : cells) {
                    int x = cell.getInt("x");
                    int y = cell.getInt("y");
                    int z = cell.getInt("z");
                    int size = cell.getInt("size");
                    boolean floor = cell.getBoolean("floor", false);

                    if (floor) {
                        layer.drawFloor(tileset, x, y, z, size);
                    } else {
                        layer.draw(tileset, x, y, z, size);
                    }
                    JsonValue faces = cell.get("faces");
                    layer.paint(tileset, x, y, z, size, Face.FRONT, tileset.getTile(faces.getInt(0)));
                    layer.paint(tileset, x, y, z, size, Face.BACK, tileset.getTile(faces.getInt(1)));
                    layer.paint(tileset, x, y, z, size, Face.LEFT, tileset.getTile(faces.getInt(2)));
                    layer.paint(tileset, x, y, z, size, Face.RIGHT, tileset.getTile(faces.getInt(3)));
                    layer.paint(tileset, x, y, z, size, Face.TOP, tileset.getTile(faces.getInt(4)));
                }
            }
        }

        List<GameObject> objects = new ArrayList<>();
        JsonValue jsonObjects = root.get("objects");
        for (JsonValue obj : jsonObjects) {
            GameObject o = new GameObject();
            o.setName(obj.getString("name"));
            o.setType(obj.getString("type", ""));

            float[] location = obj.get("location").asFloatArray();
            o.setPosition(location[0], location[1], location[2]);

            float[] size = obj.get("size").asFloatArray();
            o.setSize(size[0], size[1], size[2]);

            float[] color = obj.get("color").asFloatArray();
            o.setColor(new Color(color[0], color[1], color[2], color[3]));

            o.setVisible(obj.getBoolean("visible"));

            Properties properties = o.getProperties();
            readProperties(obj, properties);
            objects.add(o);
        }

        Properties properties = new Properties();
        readProperties(root, properties);

        if (objectCallback != null) {
            objectCallback.call(objects);
        }

        if (propertiesCallback != null) {
            propertiesCallback.call(properties);
        }

        return level;
    }

    private void readProperties(JsonValue obj, Properties properties) {
        if (!obj.has("properties")) return;
        for (JsonValue property : obj.get("properties")) {
            String key = property.getString("key");
            String type = property.getString("type");
            switch (type) {
                case "string":
                    properties.setProperty(key, property.getString("value"));
                    break;
                case "double":
                    properties.setProperty(key, property.getDouble("value", 0.0));
                    break;
                case "boolean":
                    properties.setProperty(key, property.getBoolean("value", false));
                    break;
                case "int":
                    properties.setProperty(key, property.getInt("value", 0));
                    break;
            }
        }
    }

    private Tileset parseTileset(FileHandle saveFile, JsonValue tilesetValue) {
        Tileset tileset;
        String tilesetName = tilesetValue.getString("name");
        String tilesetType = tilesetValue.getString("type");
        if (tilesetType.equals("palette")) {
            PaletteTileset palette = tilesetManager.createPalette(tilesetName);
            for (JsonValue colorEntry : tilesetValue.get("colors")) {
                int x = colorEntry.getInt("x");
                int y = colorEntry.getInt("y");
                String colorStr = colorEntry.getString("color");
                palette.draw(x, y, Color.valueOf(colorStr));
            }
            tileset = palette;
        } else {
            String path = tilesetValue.getString("path");
            FileHandle tilesetFile = Gdx.files.internal(path);
            if (!tilesetFile.exists()) {
                tilesetFile = Gdx.files.internal(saveFile.path().substring(0, saveFile.path().lastIndexOf('/') + 1) + path);
            }
            tileset = tilesetManager.load(tilesetFile);
        }

        // Tile data
        JsonValue tiles = tilesetValue.get("tiles");
        for (JsonValue tile : tiles) {
            int id = tile.getInt("id");
            int x = tile.getInt("x");
            int y = tile.getInt("y");
            int w = tile.getInt("w");
            int h = tile.getInt("h");
            String formName = tile.getString("form");
            Tile.Form form = Tile.Form.valueOf(formName);
            Tile t = new Tile(id, new TextureRegion(tileset.getTexture(), x, y, w, h));
            t.setForm(form);
            tileset.putTile(id, t);
        }

        return tileset;
    }
}
