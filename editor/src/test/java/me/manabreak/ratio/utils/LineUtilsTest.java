package me.manabreak.ratio.utils;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class LineUtilsTest {

    @Test
    public void testBresenham2D_AlongPositiveX() {
        List<Integer> points = LineUtils.bresenham(0, 0, 4, 0);
        assertEquals(10, points.size());
        assertEquals(0, points.get(0).intValue());
        assertEquals(1, points.get(2).intValue());
        assertEquals(2, points.get(4).intValue());
        assertEquals(3, points.get(6).intValue());
        assertEquals(4, points.get(8).intValue());
    }

    @Test
    public void testBresenham2D_AlongPositiveY() {
        List<Integer> points = LineUtils.bresenham(0, 0, 0, 4);
        assertEquals(10, points.size());
        assertEquals(0, points.get(1).intValue());
        assertEquals(1, points.get(3).intValue());
        assertEquals(2, points.get(5).intValue());
        assertEquals(3, points.get(7).intValue());
        assertEquals(4, points.get(9).intValue());
    }
}