package me.manabreak.ratio.plugins.level;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Single piece of a level consisting of multiple pieces
 */
public class LevelPart {
    static final int PART_SIZE = 160;
    private static final int MAX_FACES = 1000;
    private Cell[][][] cells; //[z][y][x]
    private int x;
    private int y;
    private int z;

    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int minZ;
    private int maxZ;

    private int faces = 0;

    LevelPart() {
        cells = new Cell[PART_SIZE][][];
        for (int i = 0; i < PART_SIZE; ++i) {
            cells[i] = new Cell[PART_SIZE][];
            for (int j = 0; j < PART_SIZE; ++j) {
                cells[i][j] = new Cell[PART_SIZE];
            }
        }

        minX = PART_SIZE;
        maxX = 0;
        minY = PART_SIZE;
        maxY = 0;
        minZ = PART_SIZE;
        maxZ = 0;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    Cell getCell(int x, int y, int z) {
        return cells[z][y][x];
    }

    boolean isEmpty(int x, int y, int z) {
        if (x < 0 || x >= PART_SIZE || y < 0 || y >= PART_SIZE || z < 0 || z >= PART_SIZE) return true;
        Cell c = getCell(x, y, z);
        return c == null;
    }

    public void set(int x, int y, int z, int size) {
        x = x - x % size;
        y = y - y % size;

        clear(x, y, z, size);
        Cell c = getCell(x, y, z);
        if (c == null) {
            c = new Cell(x, y, z);
            cells[z][y][x] = c;
        }
        c.setSize(size);
        for (int j = y; j < y + size; ++j) {
            for (int i = x; i < x + size; ++i) {
                cells[z][j][i] = c;
            }
        }

        refreshBounds(x, y, z, size);
        faces += 5; // All faces excluding bottom
        /*
        boolean top, left, right, front, back;
        top = isEmpty(x, y + 1, z);
        left = isEmpty(x - 1, y, z);
        right = isEmpty(x + 1, y, z);
        front = isEmpty(x, y, z + 1);
        back = isEmpty(x, y, z - 1);
        */
    }

    private void refreshBounds(int x, int y, int z, int size) {
        if (x < minX) minX = x;
        if (x + size > maxX) maxX = x + size;
        if (y < minY) minY = y;
        if (y + size > maxY) maxY = y + size;
        if (z < minZ) minZ = z;
        if (z + size > maxZ) maxZ = z + size;
    }

    private void clear(int x, int y, int z, int size) {
        Cell c = getCell(x, y, z);

        x = x - x % size;
        y = y - y % size;
        z = z - z % size;
        size = Math.max(Math.max(size, c == null ? size : c.getSize()), getLargestSize(x, y, z));
        x = x - x % size;
        y = y - y % size;
        z = z - z % size;

        Set<Cell> removed = new HashSet<>();
        for (int k = z; k < z + size; ++k) {
            for (int j = y; j < y + size; ++j) {
                for (int i = x; i < x + size; ++i) {
                    Cell cell = cells[k][j][i];
                    if (cell != null) {
                        removed.add(cell);
                        cells[k][j][i] = null;
                    }
                }
            }
        }

        faces -= removed.size() * 6;
    }

    private int getLargestSize(int x, int y, int z) {
        int s = 1;
        for (int k = z; k < z + 8; ++k) {
            for (int j = y; j < y + 8; ++j) {
                for (int i = x; i < x + 8; ++i) {
                    Cell cell = cells[k][j][i];
                    if (cell != null) {
                        int size = cell.getSize();
                        if (size == 8) return 8;
                        s = Math.max(s, size);
                    }
                }
            }
        }
        return s;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxZ() {
        return maxZ;
    }

    int getFaces() {
        return faces;
    }

    boolean isFull() {
        return faces >= MAX_FACES;
    }

    void paint(int x, int y, int z, Face face, Tile tile) {
        Cell cell = getCell(x, y, z);
        if (cell == null) return;
        cell.set(face, tile);
    }

    void erase(int x, int y, int z, int size) {
        clear(x, y, z, size);
    }

    Collection<Cell> getCells() {
        Set<Cell> set = new HashSet<>();
        for (int z = 0; z < cells.length; ++z) {
            for (int y = 0; y < cells[z].length; ++y) {
                for (int x = 0; x < cells[z][y].length; ++x) {
                    Cell cell = getCell(x, y, z);
                    if (cell != null) {
                        set.add(cell);
                    }
                }
            }
        }
        return set;
    }
}
