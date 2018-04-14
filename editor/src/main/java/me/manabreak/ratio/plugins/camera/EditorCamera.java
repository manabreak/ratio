package me.manabreak.ratio.plugins.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EditorCamera {
    public static final EditorCamera main = new EditorCamera();
    // private final Camera camera;
    protected Viewport viewport;

    private EditorCamera() {
        // this.viewport = new ExtendViewport(100f, 100f);
        // Camera camera = viewport.getCamera();
        // camera = new OrthographicCamera(48f, 27f);
        // camera = new PerspectiveCamera(45f, 16f, 9f);

        this.viewport = new ExtendViewport(48f, 27f);
        viewport.apply();

        Preferences prefs = Gdx.app.getPreferences("ratio");
        Gdx.app.addLifecycleListener(new LifecycleListener() {
            @Override
            public void pause() {

            }

            @Override
            public void resume() {

            }

            @Override
            public void dispose() {
                /*
                prefs.putFloat("editor.camera.far", camera.far);
                prefs.putFloat("editor.camera.near", camera.near);
                prefs.putFloat("editor.camera.x", camera.position.x);
                prefs.putFloat("editor.camera.y", camera.position.y);
                prefs.putFloat("editor.camera.z", camera.position.z);
                prefs.flush();
                */
            }
        });

        viewport.getCamera().far = prefs.getFloat("editor.camera.far", 1000f);
        viewport.getCamera().near = prefs.getFloat("editor.camera.near", 0.1f);

        /*
        float x = prefs.getFloat("editor.camera.x", 0f);
        float y = prefs.getFloat("editor.camera.y", 50f);
        float z = prefs.getFloat("editor.camera.z", 100f);
        */
        viewport.getCamera().position.set(0f, 50f, 100f);
        viewport.getCamera().lookAt(0f, 0f, 0f);

        // viewport.setScreenBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport.getCamera().update();
    }

    public void update(boolean updateFrustum) {
        viewport.getCamera().update(updateFrustum);
    }

    public Camera getCamera() {
        return viewport.getCamera();
    }

    public void resize(int width, int height) {
        // camera.setToOrtho(false, width, height);
        viewport.update(width, height);
    }
}
