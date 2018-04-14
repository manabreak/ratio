package me.manabreak.ratio.plugins.objects;

import com.badlogic.gdx.graphics.Color;
import me.manabreak.ratio.common.Properties;

public class GameObject {

    private final Properties properties = new Properties();

    public float x, y = 1f, z;
    public float sizeX = 1f, sizeY = 1f, sizeZ = 1f;
    public Color color = new Color(1f, 1f, 1f, 1f);
    public boolean visible = true;
    private String name = "";
    private String type = "";
    private boolean selected = false;

    public GameObject() {

    }

    public void setProperty(String key, Object value) {
        properties.setProperty(key, value);
    }

    public <T> T getProperty(String key, T defaultValue) {
        //noinspection unchecked
        return properties.getProperty(key, defaultValue);
    }

    public <T> T removeProperty(String key) {
        //noinspection unchecked
        return properties.removeProperty(key);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (this.name == null) this.name = "";
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getSizeX() {
        return sizeX;
    }

    public void setSizeX(float sizeX) {
        this.sizeX = sizeX;
    }

    public float getSizeY() {
        return sizeY;
    }

    public void setSizeY(float sizeY) {
        this.sizeY = sizeY;
    }

    public float getSizeZ() {
        return sizeZ;
    }

    public void setSizeZ(float sizeZ) {
        this.sizeZ = sizeZ;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setSize(float sx, float sy, float sz) {
        this.sizeX = sx;
        this.sizeY = sy;
        this.sizeZ = sz;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        if (this.type == null) this.type = "";
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
