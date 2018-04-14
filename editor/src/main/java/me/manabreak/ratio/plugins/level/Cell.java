package me.manabreak.ratio.plugins.level;

import java.util.EnumMap;
import java.util.Map;

public class Cell {
    private Map<Face, Integer> faces = new EnumMap<>(Face.class);
    private int x, y, z;
    private int size = 1;
    private Type type = Type.BLOCK;

    public Cell(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        for (Face face : Face.values()) {
            faces.put(face, 0);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer get(Face face) {
        return faces.get(face);
    }

    public void clear(Face face) {
        faces.put(face, 0);
        size = 1;
    }

    public void set(Face face, Tile tile) {
        faces.put(face, tile.getId());
    }

    public boolean has(Face face) {
        return get(face) != 0;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public enum Type {
        BLOCK,
        FLOOR
    }
}
