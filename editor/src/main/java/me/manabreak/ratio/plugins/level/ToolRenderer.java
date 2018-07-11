package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ToolRenderer {

    private ShapeRenderer renderer = new ShapeRenderer();

    public ToolRenderer() {

    }

    void renderWireCube(Camera camera, Coord coord, int size, Color color) {
        renderCube(camera, coord, size, color, ShapeRenderer.ShapeType.Line);
    }

    void renderWireCube(Camera camera, Coord start, Coord end, Color color) {
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(color);

        int minX = Math.min(start.x, end.x);
        int minY = Math.min(start.y, end.y);
        int minZ = Math.min(start.z, end.z);

        int maxX = Math.max(start.x, end.x);
        int maxY = Math.max(start.y, end.y);
        int maxZ = Math.max(start.z, end.z);

        float s0 = (float) maxX - (float) minX + 1;
        float s1 = (float) maxY - (float) minY + 1;
        float s2 = (float) maxZ - (float) minZ + 1;

        renderer.box((float) minX, (float) minY, (float) minZ, s0, s1, -s2);
        renderer.end();
    }

    void renderCube(Camera camera, Coord coord, int cellSize, Color color) {
        renderCube(camera, coord, cellSize, color, ShapeRenderer.ShapeType.Filled);
    }

    private void renderCube(Camera camera, Coord coord, int cellSize, Color color, ShapeRenderer.ShapeType type) {
        float cf = (float) cellSize;
        renderer.setProjectionMatrix(camera.combined);
        if (coord.x >= 0 && coord.y >= 0 && coord.z >= 0) {
            renderer.begin(type);
            renderer.setColor(color);

            float cx = coord.x;
            float cy = coord.y;
            float cz = coord.z;
            renderer.box(cx, cy, cz + cf, cf, cf, cf);
            renderer.end();
        }
    }

    void renderFace(Camera camera, Coord coord, int cellSize, Face face) {
        float cf = (float) cellSize;
        renderer.setProjectionMatrix(camera.combined);
        if (coord.x >= 0 && coord.y >= 0 && coord.z >= 0) {
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.setColor(1f, 0f, 1f, 1f);

            float cx = coord.x;
            float cy = coord.y;
            float cz = coord.z;

            switch (face) {
                case FRONT:
                    renderer.line(cx, cy, cz + cf, cx + cf, cy, cz + cf);
                    renderer.line(cx, cy, cz + cf, cx, cy + cf, cz + cf);
                    renderer.line(cx + cf, cy + cf, cz + cf, cx, cy + cf, cz + cf);
                    renderer.line(cx + cf, cy + cf, cz + cf, cx + cf, cy, cz + cf);
                    break;
                case BACK:
                    renderer.line(cx + cf, cy, cz, cx, cy, cz);
                    renderer.line(cx + cf, cy, cz, cx + cf, cy + cf, cz);
                    renderer.line(cx, cy + cf, cz, cx + cf, cy + cf, cz);
                    renderer.line(cx, cy + cf, cz, cx, cy, cz);
                    break;
                case LEFT:
                    renderer.line(cx, cy, cz, cx, cy + cf, cz);
                    renderer.line(cx, cy, cz + cf, cx, cy + cf, cz + cf);
                    renderer.line(cx, cy + cf, cz, cx, cy + cf, cz + cf);
                    renderer.line(cx, cy, cz, cx, cy, cz + cf);
                    break;
                case RIGHT:
                    renderer.line(cx + cf, cy, cz, cx + cf, cy + cf, cz);
                    renderer.line(cx + cf, cy, cz + cf, cx + cf, cy + cf, cz + cf);
                    renderer.line(cx + cf, cy + cf, cz, cx + cf, cy + cf, cz + cf);
                    renderer.line(cx + cf, cy, cz, cx + cf, cy, cz + cf);
                    break;
                case TOP:
                    renderer.line(cx, cy + cf, cz, cx + cf, cy + cf, cz);
                    renderer.line(cx, cy + cf, cz + cf, cx + cf, cy + cf, cz + cf);
                    renderer.line(cx, cy + cf, cz, cx, cy + cf, cz + cf);
                    renderer.line(cx + cf, cy + cf, cz, cx + cf, cy + cf, cz + cf);
                    break;
                case BOTTOM:
                    cy += 0.01f;
                    renderer.line(cx, cy, cz, cx + cf, cy, cz);
                    renderer.line(cx, cy, cz + cf, cx + cf, cy, cz + cf);
                    renderer.line(cx, cy, cz, cx, cy, cz + cf);
                    renderer.line(cx + cf, cy, cz, cx + cf, cy, cz + cf);
                    break;
            }

            renderer.end();
        }
    }
}
