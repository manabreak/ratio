package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.manabreak.ratio.plugins.tilesets.Tileset;

import java.util.*;

import static me.manabreak.ratio.plugins.level.Face.*;

public class LevelMeshCreator {

    public LevelMeshCreator() {

    }

    public Map<TileLayer, Map<Tileset, Mesh>> create(Level level) {
        Map<TileLayer, Map<Tileset, Mesh>> meshes = new HashMap<>();

        final List<TileLayer> layers = level.getLayers();
        for (TileLayer layer : layers) {
            Map<Tileset, Mesh> layerMeshes = new HashMap<>();
            Map<Tileset, Octree<Cell>> parts = layer.getParts();
            for (Tileset tileset : parts.keySet()) {
                Octree<Cell> octree = parts.get(tileset);
                int faces = octree.getItemCount() * 5;
                int cv = 0;
                float[] v = new float[faces * 4 * (3 + 2 + 3)];
                short[] i = new short[faces * 6];
                int inCount = 0;

                Mesh mesh = new Mesh(true, faces * 4, faces * 6, VertexAttribute.Position(), VertexAttribute.TexCoords(0), VertexAttribute.Normal());

                Deque<Octree<Cell>> queue = new ArrayDeque<>();
                queue.push(octree);

                while (queue.size() > 0) {
                    Octree<Cell> tree = queue.pop();

                    Cell item = tree.getItem();
                    if (item != null) {
                        float s = item.getSize() / 16f;
                        float x0 = item.getX() / 16f;
                        float x1 = x0 + s;
                        float y0 = item.getY() / 16f;
                        float y1 = y0 + s;
                        float z0 = item.getZ() / 16f;
                        float z1 = z0 + s;

                        int x = item.getX();
                        int y = item.getY();
                        int z = item.getZ();
                        int size = item.getSize();

                        boolean hasTop = octree.get(x, y + size, z, size) != null;
                        boolean hasFront = octree.get(x, y, z + size, size) != null;
                        boolean hasBack = octree.get(x, y, z - size, size) != null;
                        boolean hasLeft = octree.get(x - size, y, z, size) != null;
                        boolean hasRight = octree.get(x + size, y, z, size) != null;

                        if (item.getType() == Cell.Type.BLOCK) {
                            // FRONT
                            if (!hasFront) {
                                TextureRegion r = getRegion(tileset, item, FRONT);
                                cv = addVertex(v, cv, x0, y1, z1, r.getU(), r.getV(), 0f, 0f, 1f);
                                cv = addVertex(v, cv, x0, y0, z1, r.getU(), r.getV2(), 0f, 0f, 1f);
                                cv = addVertex(v, cv, x1, y0, z1, r.getU2(), r.getV2(), 0f, 0f, 1f);
                                cv = addVertex(v, cv, x1, y1, z1, r.getU2(), r.getV(), 0f, 0f, 1f);
                                inCount += 6;
                            }
                            // TOP
                            if (!hasTop) {
                                TextureRegion r = getRegion(tileset, item, TOP);
                                cv = addVertex(v, cv, x0, y1, z0, r.getU(), r.getV(), 0f, 1f, 0f);
                                cv = addVertex(v, cv, x0, y1, z1, r.getU(), r.getV2(), 0f, 1f, 0f);
                                cv = addVertex(v, cv, x1, y1, z1, r.getU2(), r.getV2(), 0f, 1f, 0f);
                                cv = addVertex(v, cv, x1, y1, z0, r.getU2(), r.getV(), 0f, 1f, 0f);
                                inCount += 6;
                            }
                            // LEFT
                            if (!hasLeft) {
                                TextureRegion r = getRegion(tileset, item, LEFT);
                                cv = addVertex(v, cv, x0, y1, z0, r.getU(), r.getV(), -1f, 0f, 0f);
                                cv = addVertex(v, cv, x0, y0, z0, r.getU(), r.getV2(), -1f, 0f, 0f);
                                cv = addVertex(v, cv, x0, y0, z1, r.getU2(), r.getV2(), -1f, 0f, 0f);
                                cv = addVertex(v, cv, x0, y1, z1, r.getU2(), r.getV(), -1f, 0f, 0f);
                                inCount += 6;
                            }
                            // RIGHT
                            if (!hasRight) {
                                TextureRegion r = getRegion(tileset, item, RIGHT);
                                cv = addVertex(v, cv, x1, y1, z1, r.getU(), r.getV(), 1f, 0f, 0f);
                                cv = addVertex(v, cv, x1, y0, z1, r.getU(), r.getV2(), 1f, 0f, 0f);
                                cv = addVertex(v, cv, x1, y0, z0, r.getU2(), r.getV2(), 1f, 0f, 0f);
                                cv = addVertex(v, cv, x1, y1, z0, r.getU2(), r.getV(), 1f, 0f, 0f);
                                inCount += 6;
                            }
                            // BACK
                            if (!hasBack) {
                                TextureRegion r = getRegion(tileset, item, BACK);
                                cv = addVertex(v, cv, x1, y1, z0, r.getU(), r.getV(), 0f, 0f, -1f);
                                cv = addVertex(v, cv, x1, y0, z0, r.getU(), r.getV2(), 0f, 0f, -1f);
                                cv = addVertex(v, cv, x0, y0, z0, r.getU2(), r.getV2(), 0f, 0f, -1f);
                                cv = addVertex(v, cv, x0, y1, z0, r.getU2(), r.getV(), 0f, 0f, -1f);
                                inCount += 6;
                            }
                        } else if (item.getType() == Cell.Type.FLOOR) {
                            TextureRegion r = getRegion(tileset, item, TOP);
                            cv = addVertex(v, cv, x0, y1, z1, r.getU(), r.getV(), 0f, 1f, 0f);
                            cv = addVertex(v, cv, x0, y1, z0, r.getU(), r.getV2(), 0f, 1f, 0f);
                            cv = addVertex(v, cv, x1, y1, z0, r.getU2(), r.getV2(), 0f, 1f, 0f);
                            cv = addVertex(v, cv, x1, y1, z1, r.getU2(), r.getV(), 0f, 1f, 0f);

                            inCount += 6;
                        }
                    }

                    for (int oct = 0; oct < 8; ++oct) {
                        Octree o = tree.get(oct);
                        if (o != null) queue.push(o);
                    }
                }

                int fc = 0, c = 0;
                while (fc < i.length) {
                    i[fc++] = (short) c;
                    i[fc++] = (short) (c + 1);
                    i[fc++] = (short) (c + 2);
                    i[fc++] = (short) (c + 2);
                    i[fc++] = (short) (c + 3);
                    i[fc++] = (short) c;
                    c += 4;
                }

                mesh.setVertices(v);
                mesh.setIndices(i, 0, inCount);
                layerMeshes.put(tileset, mesh);
            }
            meshes.put(layer, layerMeshes);
        }

        return meshes;
    }

    private TextureRegion getRegion(Tileset tileset, Cell item, Face face) {
        return tileset.getTile(item.get(face)).getRegion();
    }

    private int addVertex(float[] vertices, int offset, float x, float y, float z, float u, float v, float nx, float ny, float nz) {
        vertices[offset++] = x;
        vertices[offset++] = y;
        vertices[offset++] = z;
        vertices[offset++] = u;
        vertices[offset++] = v;
        vertices[offset++] = nx;
        vertices[offset++] = ny;
        vertices[offset++] = nz;
        return offset;
    }
}
