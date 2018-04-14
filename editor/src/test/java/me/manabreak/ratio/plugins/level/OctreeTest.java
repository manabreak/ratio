package me.manabreak.ratio.plugins.level;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class OctreeTest {

    @Test
    public void testAddNode() {
        Octree<Integer> o = new Octree<>(6);
        assertEquals(64, o.getLevel());
        assertEquals(0, o.getItemCount());

        o.insert(0, 0, 0, 8, 42);
        assertEquals(1, o.getItemCount());

        Octree o1 = o.get(1);
        assertEquals(32, o1.getLevel());

        Octree o2 = o1.get(1);
        assertEquals(16, o2.getLevel());

        Octree o3 = o2.get(1);
        assertEquals(8, o3.getLevel());
        assertEquals(42, o3.getItem());

        o.insert(4, 0, 0, 4, 66);
        assertEquals(1, o.getItemCount());
        assertNull(o3.getItem());

        Octree o4 = o3.get(0);
        assertEquals(66, o4.getItem());

        o.insert(0, 0, 0, 4, 69);
        assertEquals(2, o.getItemCount());

        Octree o5 = o3.get(1);
        assertEquals(69, o5.getItem());
        assertEquals(66, o4.getItem());

        o5.clear();
        assertEquals(1, o.getItemCount());
        assertNull(o5.getItem());
        assertEquals(66, o4.getItem());

        o.clear();
        assertEquals(0, o.getItemCount());
        assertNull(o.getItem());
        for (int i = 0; i < 8; ++i) {
            assertNull(o.get(i));
        }
    }

    @Test
    public void testRemove() {
        Octree<Integer> o = new Octree<>(6);
        o.insert(0, 0, 0, 8, 42);
        assertEquals(1, o.getItemCount());

        o.remove(0, 0, 0, 8);
        assertEquals(0, o.getItemCount());
    }

    @Test
    public void testFlatten() {
        Octree<String> o = new Octree<>();
        o.insert(0, 0, 0, 8, "Foo");
        o.insert(32, 16, 8, 4, "Bar");

        List<String> f = o.flatten();
        assertEquals(2, f.size());
        assertTrue(f.contains("Foo"));
        assertTrue(f.contains("Bar"));
    }
}