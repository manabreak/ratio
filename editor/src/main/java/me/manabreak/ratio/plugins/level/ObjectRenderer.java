package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import me.manabreak.ratio.plugins.objects.GameObject;
import me.manabreak.ratio.plugins.objects.ObjectRenderMode;
import me.manabreak.ratio.utils.TextUtils;

import java.util.List;

public class ObjectRenderer {

    private static final Color SELECTION_COLOR = Color.CHARTREUSE;
    private final ShapeRenderer renderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private Vector3 tmp = new Vector3();
    private ObjectRenderMode mode = ObjectRenderMode.Line;
    private boolean seeThrough = true;

    public ObjectRenderer(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font) {
        this.renderer = renderer;
        this.batch = batch;
        this.font = font;
    }

    public void render(Camera camera, List<GameObject> objects) {
        renderObjects(camera, objects);
        renderNames(camera, objects);
    }

    public void rotateDrawMode() {
        int i = mode.ordinal();
        i++;
        i %= ObjectRenderMode.values().length;
        mode = ObjectRenderMode.values()[i];
    }

    private void renderNames(Camera camera, List<GameObject> objects) {
        batch.begin();
        for (GameObject obj : objects) {
            if (obj.isSelected()) {
                font.setColor(SELECTION_COLOR);
            } else {
                font.setColor(Color.WHITE);
            }

            float x = obj.getX() + (obj.getSizeX()) / 2f;
            float y = obj.getY() + (obj.getSizeY()) + 1.5f;
            float z = obj.getZ() + (obj.getSizeZ()) / 2f;
            tmp.set(x, y, z);
            tmp = camera.project(tmp);

            String text = obj.getName();
            if (!TextUtils.isNullOrEmpty(obj.getType())) {
                text += " (" + obj.getType() + ")";
            }
            GlyphLayout layout = new GlyphLayout(font, text);
            final float fontX = tmp.x - (layout.width) / 2f;
            final float fontY = tmp.y - (layout.height) / 2f;

            font.draw(batch, layout, fontX, fontY);
        }
        batch.end();
    }

    private void renderObjects(Camera camera, List<GameObject> objects) {
        if (mode == ObjectRenderMode.None) return;

        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);

        if (seeThrough) {
            Gdx.gl20.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        }

        renderer.setProjectionMatrix(camera.combined);
        switch (mode) {
            case Line:
                renderer.begin(ShapeRenderer.ShapeType.Line);
                break;
            case Filled:
                renderer.begin(ShapeRenderer.ShapeType.Filled);
                break;
        }
        for (GameObject obj : objects) {
            float sx = obj.getSizeX();
            float sy = obj.getSizeY();
            float sz = obj.getSizeZ();

            float x = obj.getX();
            float y = obj.getY();
            float z = obj.getZ() + sz;

            renderer.setColor(obj.isSelected() ? SELECTION_COLOR : obj.getColor());
            renderer.box(x, y, z, sx, sy, sz);
        }
        renderer.end();
    }

    public void toggleSeeThrough() {
        seeThrough = !seeThrough;
    }
}
