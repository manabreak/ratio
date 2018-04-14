package me.manabreak.ratio.plugins.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import me.manabreak.ratio.plugins.camera.EditorCamera;
import me.manabreak.ratio.editor.EditorPlugin;
import me.manabreak.ratio.editor.LoopListener;

public class EditorGrid extends EditorPlugin implements LoopListener {

    private static final int GRID_DISTANCE = 100;
    private static final float MAIN_LINE_ALPHA = 0.5f;
    private static final float Y = 1f;
    private static final float UNIT_SIZE = 16f;
    private ShapeRenderer renderer;
    private boolean enabled = true;
    private float y = Y;

    public EditorGrid() {
        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);
    }

    @Override
    public void initialize() {
        editorController.addLoopListener(this);
        editorController.registerInputProcessor(this);
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.G) {
            enabled = !enabled;
            return true;
        }
        return super.keyUp(keycode);
    }

    private void render() {
        if (!enabled) return;

        Camera camera = EditorCamera.main.getCamera();
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setProjectionMatrix(camera.combined);

        int x = 0;
        int z = 0;

        for (int i = 0; i < GRID_DISTANCE; ++i) {
            float a = 1f - Math.abs((float) i / ((float) GRID_DISTANCE));
            renderer.setColor(1f, 1f, 1f, MAIN_LINE_ALPHA * a);
            renderer.line(x + i, y, 0f, x + i, y, GRID_DISTANCE);
            renderer.line(0f, y, z + i, GRID_DISTANCE, y, z + i);
        }

        renderer.set(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(1f, 1f, 1f, 0.07f);
        renderer.box(0f, y, 0f, GRID_DISTANCE, -0.1f, -GRID_DISTANCE);
        renderer.end();

        Gdx.gl20.glClear(GL20.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void onUpdate(float dt) {
        render();
    }

    public void raiseBy(float amount) {
        y += amount;
        y = Math.round(y * UNIT_SIZE) / UNIT_SIZE - 0.001f;
    }

    public void lowerBy(float amount) {
        y -= amount;
        y = Math.round(y * UNIT_SIZE) / UNIT_SIZE - 0.001f;
    }
}
