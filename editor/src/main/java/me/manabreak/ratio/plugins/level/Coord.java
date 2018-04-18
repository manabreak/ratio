package me.manabreak.ratio.plugins.level;

public class Coord {
    public int x;
    public int y;
    public int z;

    public Coord() {

    }

    public Coord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Coord(Coord other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    @Override
    public String toString() {
        return "X: " + x + ", Y: " + y + ", Z: " + z;
    }
}
