package me.manabreak.ratio.plugins.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EditorCamera {
    public static final EditorCamera main = new EditorCamera();
    private Viewport viewport;

    private EditorCamera() {
        this.viewport = new ExtendViewport(48f, 27f);
        viewport.getCamera().far = 1000f;
        viewport.getCamera().near = 0.1f;
        viewport.apply();

        viewport.getCamera().position.set(0f, 50f, 100f);
        viewport.getCamera().lookAt(0f, 0f, 0f);

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport.getCamera().update();
    }

    public Camera getCamera() {
        return viewport.getCamera();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void setToPerspective() {
        final Camera old = viewport.getCamera();
        PerspectiveCamera newCamera = new PerspectiveCamera(45f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        newCamera.near = 0.1f;
        newCamera.far = 1000f;
        newCamera.direction.set(old.direction);
        newCamera.position.set(old.position).add(old.direction.scl(40f));
        newCamera.up.set(old.up);
        newCamera.update();
        viewport.setCamera(newCamera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void setToOrthogonal() {
        final Camera old = viewport.getCamera();
        OrthographicCamera newCamera = new OrthographicCamera(48f, 27f);
        newCamera.near = 0.1f;
        newCamera.far = 1000f;
        newCamera.direction.set(old.direction);
        newCamera.position.set(old.position).add(old.direction.scl(-40f));
        newCamera.up.set(old.up);
        newCamera.update();
        viewport.setCamera(newCamera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
}
