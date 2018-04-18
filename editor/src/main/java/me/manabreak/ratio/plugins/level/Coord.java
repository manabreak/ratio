package me.manabreak.ratio.plugins.level;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return x == coord.x &&
                y == coord.y &&
                z == coord.z;
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y, z);
    }
}
