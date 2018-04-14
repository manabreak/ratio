package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tile {

    private final int id;
    private final TextureRegion region;
    private final int x, y, width, height;
    private Form form;

    public Tile(int id, TextureRegion region) {
        this.id = id;
        this.region = region;
        this.form = Form.OTHER;
        this.x = region.getRegionX();
        this.y = region.getRegionY();
        this.width = region.getRegionWidth();
        this.height = region.getRegionHeight();
    }

    public static Tile from(Texture texture, int id, int x, int y, int width, int height) {
        return new Tile(id, new TextureRegion(texture, x, y, width, height));
    }

    public int getId() {
        return id;
    }

    public TextureRegion getRegion() {
        return region;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (x != tile.x) return false;
        if (y != tile.y) return false;
        if (width != tile.width) return false;
        return height == tile.height;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // TODO Refactor and remove Form enum; make it possible to create new forms during runtime
    public enum Form {
        TOP_SINGLE("Top / Single"),
        TOP_HORIZONTAL_END_WEST("Top / West End"),
        TOP_VERTICAL_END_SOUTH("Top / South End"),
        TOP_CORNER_SW("Top / Corner SW"),
        TOP_HORIZONTAL_END_EAST("Top / East End"),
        TOP_HORIZONTAL("Top / Horizontal"),
        TOP_CORNER_SE("Top / Corner SE"),
        TOP_TRI_N("Top / T-Junction North"), // Tri-connector "pointing" north
        TOP_VERTICAL_END_NORTH("Top / North End"),
        TOP_CORNER_NW("Top / Corner NW"),
        TOP_VERTICAL("Top / Vertical"),
        TOP_TRI_E("Top / T-Junction East"),
        TOP_CORNER_NE("Top / Corner NE"),
        TOP_TRI_S("Top / T-Junction South"),
        TOP_TRI_W("Top / T-Junction West"),
        TOP_CROSS("Top / Cross"), // Four-way crossing

        SIDE_SINGLE("Side / Single"),
        SIDE_END_WEST("Side / West End"),
        SIDE_END_EAST("Side / East End"),
        SIDE("Side"),

        TOP_FILL("Top / Fill"), // Fill; when forming bigger top areas or floors
        OTHER("Other");

        private final String text;

        Form(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
