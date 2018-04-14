package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Collection;

public class WireframeRenderer {

    private final ShapeRenderer renderer = new ShapeRenderer();

    public void render(Camera camera, Collection<Mesh> meshes) {
        renderer.setProjectionMatrix(camera.combined);
        renderer.setColor(0.5f, 0.5f, 1f, 1f);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        for (Mesh mesh : meshes) {
            float[] v = new float[mesh.getNumVertices() * 8];
            short[] i = new short[mesh.getNumIndices()];
            mesh.getVertices(v);
            mesh.getIndices(i);

            for (int c = 0; c < i.length; c += 3) {
                float x0 = v[i[c] * 8];
                float y0 = v[i[c] * 8 + 1];
                float z0 = v[i[c] * 8 + 2];

                float x1 = v[i[c + 1] * 8];
                float y1 = v[i[c + 1] * 8 + 1];
                float z1 = v[i[c + 1] * 8 + 2];

                float x2 = v[i[c + 2] * 8];
                float y2 = v[i[c + 2] * 8 + 1];
                float z2 = v[i[c + 2] * 8 + 2];

                renderer.line(x0, y0, z0, x1, y1, z1);
                renderer.line(x1, y1, z1, x2, y2, z2);
                renderer.line(x2, y2, z2, x0, y0, z0);
            }
        }
        renderer.end();
    }
}
