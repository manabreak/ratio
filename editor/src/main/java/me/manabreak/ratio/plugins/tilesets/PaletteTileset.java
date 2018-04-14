package me.manabreak.ratio.plugins.tilesets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PaletteTileset extends Tileset {

    private final Map<Tuple, Color> indexes = new HashMap<>();
    private TextureData data;
    private Pixmap pixmap;

    public PaletteTileset(String name) {
        super(name, new Texture(64, 64, Pixmap.Format.RGB888));
        pixmap = new Pixmap(64, 64, Pixmap.Format.RGB888);
        pixmap.setFilter(Pixmap.Filter.NearestNeighbour);
        data = new PixmapTextureData(pixmap, Pixmap.Format.RGB888, false, false);
        getTexture().load(data);
    }

    public void draw(int x, int y, Color color) {
        System.out.println("Drawing color " + color.toString() + " to " + x + ", " + y);
        int col = Color.rgba8888(color);
        pixmap.drawPixel(x, y, col);
        getTexture().load(data);
        indexes.put(new Tuple(x, y), color);
    }

    public Color getColorAt(int x, int y) {
        System.out.println("Fetching color from " + x + ", " + y);
        int i = pixmap.getPixel(x, y);
        Color c = new Color(i);
        System.out.println(" --> " + c.toString());
        return c;
    }

    public Map<Tuple, Color> getEntries() {
        return indexes;
    }

    public static class Tuple {
        public final int x, y;

        public Tuple(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tuple tuple = (Tuple) o;
            return x == tuple.x &&
                    y == tuple.y;
        }

        @Override
        public int hashCode() {

            return Objects.hash(x, y);
        }
    }
}
