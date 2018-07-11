package me.manabreak.ratio.plugins.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import me.manabreak.ratio.plugins.camera.EditorCamera;
import me.manabreak.ratio.editor.EditorPlugin;
import me.manabreak.ratio.editor.LoopListener;
import me.manabreak.ratio.plugins.level.LevelEditorPlugin;

public class EditorGrid extends EditorPlugin implements LoopListener {

    private static final int GRID_DISTANCE = 100;
    private static final float MAIN_LINE_ALPHA = 0.5f;
    private static final float Y = 1f;
    private static final float UNIT_SIZE = 1f;
    private ShapeRenderer renderer;
    private boolean enabled = true;
    private float y = Y;
    private LevelEditorPlugin plugin;

    public EditorGrid() {
        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);
    }

    @Override
    public void initialize() {
        plugin = editorController.getPlugin(LevelEditorPlugin.class);
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

        int size = plugin.getWorldSize();
        Camera camera = EditorCamera.main.getCamera();
        boolean drawLowerXY = camera.position.z > 0f;
        boolean drawUpperXY = camera.position.z < size;
        boolean drawLowerXZ = camera.position.y > 0f;
        boolean drawUpperXZ = camera.position.y < size;
        boolean drawLowerZY = camera.position.x > 0f;
        boolean drawUpperZY = camera.position.x < size;

        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl20.glCullFace(GL20.GL_BACK);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL20.GL_LESS);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setProjectionMatrix(camera.combined);

        int x = 0;
        int z = 0;
        float dist = Math.min(GRID_DISTANCE, size);

        renderer.setColor(1f, 1f, 1f, MAIN_LINE_ALPHA);
        for (int i = 0; i < GRID_DISTANCE && i < size; ++i) {
            if (drawLowerXZ) {
                renderer.line(x + i, y, 0f, x + i, y, dist);
                renderer.line(0f, y, z + i, dist, y, z + i);
            }

            if (drawUpperXZ) {
                renderer.line(x + i, y + size, 0f, x + i, y + size, dist);
                renderer.line(0f, y + size, z + i, dist, y + size, z + i);
            }

            if (drawLowerXY) {
                renderer.line(x + i, y, 0f, x + i, y + size, 0f);
                renderer.line(0f, y + i, 0f, dist, y + i, 0f);
            }

            if (drawUpperXY) {
                renderer.line(x + i, y, dist, x + i, y + size, dist);
                renderer.line(0f, y + i, dist, dist, y + i, dist);
            }

            if (drawLowerZY) {
                renderer.line(0f, y, z + i, 0f, y + size, z + i);
                renderer.line(0f, y + i, 0f, 0f, y + i, dist);
            }

            if (drawUpperZY) {
                renderer.line(dist, y, z + i, dist, y + size, z + i);
                renderer.line(dist, y + i, 0f, dist, y + i, dist);
            }
        }

        renderer.end();

        Gdx.gl20.glDisable(GL20.GL_CULL_FACE);
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
