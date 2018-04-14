package me.manabreak.ratio.utils;

import java.util.ArrayList;
import java.util.List;

public class LineUtils {

    /**
     * Plots a line in 2D space between two coordinates.
     *
     * @param x0 start X
     * @param y0 start Y
     * @param x1 end X
     * @param y1 end Y
     * @return all the coordinates along the line
     */
    public static List<Integer> bresenham(int x0, int y0, int x1, int y1) {
        List<Integer> list = new ArrayList<>(Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0)));
        int d = 0;
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int dx2 = 2 * dx;
        int dy2 = 2 * dy;
        int ix = x0 < x1 ? 1 : -1;
        int iy = y0 < y1 ? 1 : -1;

        int x = x0;
        int y = y0;
        if (dx >= dy) {
            while (true) {
                list.add(x);
                list.add(y);
                if (x == x1) break;
                x += ix;
                d += dy2;
                if (d > dx) {
                    y += iy;
                    d -= dx2;
                }
            }
        } else {
            while (true) {
                list.add(x);
                list.add(y);
                if (y == y1) break;
                y += iy;
                d += dx2;
                if (d > dy) {
                    x += ix;
                    d -= dy2;
                }
            }
        }
        return list;
    }
}
