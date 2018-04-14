package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
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

        try {
            addCell(tileset, x, y, z, size, part);
        } catch (AutomapMissingException e) {
            // e.printStackTrace();
            System.out.println("Automap: No automap for size " + size + "(" + e.getMessage() + ")");
        }
    }

    private void addCell(Tileset tileset, int x, int y, int z, int size, Octree<Cell> part) throws AutomapMissingException {
        Cell cell = new Cell(x, y, z);
        cell.setSize(size);
        part.insert(x, y, z, size, cell);

        /*
        if (autoPaint) {
            autoPaint(tileset, x, y, z, size, part);
            autoPaint(tileset, x + size, y, z, size, part);
            autoPaint(tileset, x - size, y, z, size, part);
            autoPaint(tileset, x, y + size, z, size, part);
            autoPaint(tileset, x, y - size, z, size, part);
            autoPaint(tileset, x, y, z + size, size, part);
            autoPaint(tileset, x, y, z - size, size, part);
        }
        */
    }

    /*
    private void autoPaint(Tileset tileset, int x, int y, int z, int size, Octree<Cell> part) throws AutomapMissingException {
        if (tileset != null && autoPaint && part.get(x, y, z, size) != null) {
            boolean hasTop = part.get(x, y + size, z, size) != null;
            boolean hasFront = part.get(x, y, z + size, size) != null;
            boolean hasBack = part.get(x, y, z - size, size) != null;
            boolean hasLeft = part.get(x - size, y, z, size) != null;
            boolean hasRight = part.get(x + size, y, z, size) != null;

            // Top must be painted?
            int i = 0;
            if (!hasTop) {
                if (hasRight) i += 1;
                if (hasBack) i += 2;
                if (hasLeft) i += 4;
                if (hasFront) i += 8;

                paint(tileset, x, y, z, size, Face.TOP, tileset.getAutomapTile(i, size));
            }

            // Front must be painted?
            i = 16;
            if (!hasFront) {
                if (hasRight) i += 1;
                if (hasLeft) i += 2;
                paint(tileset, x, y, z, size, Face.FRONT, tileset.getAutomapTile(i, size));
            }

            i = 16;
            if (!hasLeft) {
                if (hasFront) i += 1;
                if (hasBack) i += 2;
                paint(tileset, x, y, z, size, Face.LEFT, tileset.getAutomapTile(i, size));
            }

            i = 16;
            if (!hasRight) {
                if (hasBack) i += 1;
                if (hasFront) i += 2;
                paint(tileset, x, y, z, size, Face.RIGHT, tileset.getAutomapTile(i, size));
            }

            i = 16;
            if (!hasBack) {
                if (hasLeft) i += 1;
                if (hasRight) i += 2;
                paint(tileset, x, y, z, size, Face.BACK, tileset.getAutomapTile(i, size));
            }
        }
    }
    */

    /*
    public void paint(Tileset tileset, int x, int y, int z, int size, Face face) {
        paint(tileset, x, y, z, size, face, tileset.createTile(currentRegion));
    }
    */

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


        /*
        try {
            autoPaint(tileset, x, y, z, size, part);
            autoPaint(tileset, x + size, y, z, size, part);
            autoPaint(tileset, x - size, y, z, size, part);
            autoPaint(tileset, x, y + size, z, size, part);
            autoPaint(tileset, x, y - size, z, size, part);
            autoPaint(tileset, x, y, z + size, size, part);
            autoPaint(tileset, x, y, z - size, size, part);
        } catch (AutomapMissingException e) {
            e.printStackTrace();
        }
        */
    }

    public Map<Tileset, Octree<Cell>> getParts() {
        return parts;
    }

    public BoundingBox getBounds() {
        Vector3 min = new Vector3(10000f, 10000f, 10000f);
        Vector3 max = new Vector3(0f, 0f, 0f);
        for (Octree<Cell> tree : parts.values()) {
            for (Cell cell : tree.flatten()) {
                min.x = Math.min(min.x, cell.getX());
                min.y = Math.min(min.y, cell.getY());
                min.z = Math.min(min.z, cell.getZ());
                max.x = Math.max(max.x, cell.getX());
                max.y = Math.max(max.y, cell.getY());
                max.z = Math.max(max.z, cell.getZ());
            }
        }
        return new BoundingBox(min, max);
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
