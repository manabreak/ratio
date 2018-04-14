package me.manabreak.ratio.plugins.level;

import me.manabreak.ratio.plugins.tilesets.Tileset;

import java.util.LinkedHashMap;
import java.util.Map;

public class TileLayer {
    private final Map<Tileset, Octree<Cell>> parts = new LinkedHashMap<>();
    private String name;
    private boolean visible = true;

    public TileLayer(String name) {
        this.name = name;
    }

    private Octree<Cell> getPart(Tileset tileset) {
        if (parts.containsKey(tileset)) {
            return parts.get(tileset);
        }

        Octree<Cell> tree = new Octree<>();
        parts.put(tileset, tree);
        return tree;
    }

    public void drawFloor(Tileset tileset, int x, int y, int z, int size) {
        Octree<Cell> part = getPart(tileset);
        if (part.getItem() != null) {
            System.out.println("Warning: Re-drawing on same tile?");
            return;
        }
        x = x - x % size;
        y = y - y % size;
        z = z - z % size;

        if (part.get(x, y, z, size) != null) return;

        Cell cell = new Cell(x, y, z);
        cell.setType(Cell.Type.FLOOR);
        cell.setSize(size);
        part.insert(x, y, z, size, cell);
    }

    public void draw(Tileset tileset, int x, int y, int z, int size) {
        Octree<Cell> part = getPart(tileset);
        if (part.getItem() != null) {
            System.out.println("Warning: Re-drawing on same tile?");
            return;
        }
        x = x - x % size;
        y = y - y % size;
        z = z - z % size;

        addCell(x, y, z, size, part);
    }

    private void addCell(int x, int y, int z, int size, Octree<Cell> part) {
        Cell cell = new Cell(x, y, z);
        cell.setSize(size);
        part.insert(x, y, z, size, cell);
    }

    public void paint(Tileset tileset, int x, int y, int z, int size, Face face, Tile tile) {
        Octree<Cell> part = getPart(tileset);
        x = x - x % size;
        y = y - y % size;
        z = z - z % size;
        Cell cell = part.get(x, y, z, size);
        if (cell != null) {
            cell.set(face, tile);
        } else {
            System.out.println("Warning: No cell at (" + x + ", " + y + ", " + z + ") when trying to paint; tileset: " + tileset.getName());
        }
    }

    public void erase(Tileset tileset, int x, int y, int z, int size) {
        Octree<Cell> part = getPart(tileset);
        x = x - x % size;
        y = y - y % size;
        z = z - z % size;
        part.remove(x, y, z, size);
    }

    public Map<Tileset, Octree<Cell>> getParts() {
        return parts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Tile getTile(Tileset tileset, int x, int y, int z, int size, Face face) {
        final Octree<Cell> part = getPart(tileset);
        if (part == null) return null;

        final Cell cell = part.get(x, y, z, size);
        if (cell == null) return null;

        final Integer i = cell.get(face);
        return tileset.getTile(i);
    }
}
