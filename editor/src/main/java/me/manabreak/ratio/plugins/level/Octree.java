package me.manabreak.ratio.plugins.level;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Quadtree implementation which has just one item per leaf cell
 */
public class Octree<T> {

    private final int level;
    private final int x;
    private final int y;
    private final int z;
    private T item = null;
    private Octree[] octants = new Octree[8];

    public Octree() {
        level = 1 << 30;
        x = 0;
        y = 0;
        z = 0;
    }

    public Octree(int rootLevel) {
        level = 1 << rootLevel;
        x = 0;
        y = 0;
        z = 0;
    }

    private Octree(int level, int x, int y, int z) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void clear() {
        item = null;
        for (int i = 0; i < 8; ++i) {
            if (octants[i] != null) {
                octants[i].clear();
                octants[i] = null;
            }
        }
    }

    public void remove(int x, int y, int z, int size) {
        if (size == level && this.item != null) {
            this.item = null;
            return;
        }

        for (int i = 0; i < 8; ++i) {
            if (octants[i] != null && octants[i].containsPoint(x, y, z)) {
                octants[i].remove(x, y, z, size);
                break;
            }
        }
    }

    private void split() {
        if (level == 1) throw new IllegalStateException("Cannot split level 1!");
        int subs = level >> 1;
        octants[0] = new Octree(subs, x + subs, y, z);
        octants[1] = new Octree(subs, x, y, z);
        octants[2] = new Octree(subs, x, y + subs, z);
        octants[3] = new Octree(subs, x + subs, y + subs, z);
        octants[4] = new Octree(subs, x + subs, y, z + subs);
        octants[5] = new Octree(subs, x, y, z + subs);
        octants[6] = new Octree(subs, x, y + subs, z + subs);
        octants[7] = new Octree(subs, x + subs, y + subs, z + subs);
    }

    private boolean containsPoint(int x, int y, int z) {
        return x >= this.x && y >= this.y && z >= this.z &&
                x < this.x + level && y < this.y + level && z < this.z + level;
    }

    public void insert(int x, int y, int z, int size, T item) {
        if (size == level && this.item == null) {
            this.item = item;
            return;
        } else if (size < level && this.item != null) {
            this.item = null;
        }

        if (octants[0] == null) {
            split();
        }

        for (int i = 0; i < 8; ++i) {
            if (octants[i].containsPoint(x, y, z)) {
                //noinspection unchecked
                octants[i].insert(x, y, z, size, item);
                break;
            }
        }
    }

    public Octree get(int octant) {
        return octants[octant];
    }

    public int getLevel() {
        return level;
    }

    public T getItem() {
        return item;
    }

    public int getItemCount() {
        int c = 0;
        if (item != null) c++;
        for (int i = 0; i < 8; ++i) {
            if (octants[i] != null) {
                c += octants[i].getItemCount();
            }
        }
        return c;
    }

    public T get(int x, int y, int z, int size) {
        if (size == level) {
            return item;
        }

        if (octants[0] != null) {
            for (int i = 0; i < 8; ++i) {
                if (octants[i].containsPoint(x, y, z)) {
                    //noinspection unchecked
                    T t = (T) octants[i].get(x, y, z, size);
                    if (t != null) return t;
                }
            }
        }

        return null;
    }

    public List<T> flatten() {
        List<T> list = new ArrayList<T>();

        Deque<Octree<T>> queue = new ArrayDeque<>();
        queue.push(this);

        while (queue.size() > 0) {
            Octree<T> tree = queue.pop();
            if (tree.item != null) {
                list.add(tree.item);
            }

            for (int oct = 0; oct < 8; ++oct) {
                //noinspection unchecked
                Octree<T> o = tree.get(oct);
                if (o != null) queue.push(o);
            }
        }

        return list;
    }
}
