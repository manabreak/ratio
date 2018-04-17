package me.manabreak.ratio.plugins.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.manabreak.ratio.editor.EditorPlugin;
import me.manabreak.ratio.editor.LoopListener;

public class EditorCameraPlugin extends EditorPlugin implements LoopListener {

    private static final float CAM_PAN_SPEED_KEYBOARD = 8f;
    private static final float CAM_PAN_SPEED_MOUSE = 480f;
    private static final float CAM_PAN_SPEED_MOUSE_PERSPECTIVE = 10f;
    private float camDistance = 100f;
    private int lastDx, lastDy;
    private CameraSnapMode snapMode = CameraSnapMode.TOPDOWN;

    @Override
    public void initialize() {
        editorController.addLoopListener(this);
        editorController.registerInputProcessor(this);
    }

    @Override
    public void onUpdate(float dt) {
        Camera cam = EditorCamera.main.getCamera();

        handleRotation(dt, cam);
        handleKeyboardMovement(dt, cam);
        handleMouseMovement(dt, cam);
        if (cam instanceof OrthographicCamera) {
            handleOrthographicCamera(dt, (OrthographicCamera) cam);
        }

        cam.update();
    }

    @Override
    public boolean scrolled(int amount) {
        final float scrollAmount = ((float) amount) * 3f;
        this.camDistance += scrollAmount;
        final Camera cam = EditorCamera.main.getCamera();
        if (cam instanceof PerspectiveCamera) {
            final Vector3 d = cam.direction.cpy();
            cam.position.add(d.scl(-scrollAmount));
        }
        return super.scrolled(amount);
    }

    private void handleMouseMovement(float dt, Camera cam) {
        if (!Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) return;

        int dx = Gdx.input.getDeltaX();
        int dy = Gdx.input.getDeltaY();

        int oldX = Gdx.input.getX() - dx;
        int oldY = Gdx.input.getY() - dy;
        int newX = Gdx.input.getX();
        int newY = Gdx.input.getY();

        if (cam instanceof OrthographicCamera) {
            Vector3 oldScr = cam.unproject(new Vector3(oldX, oldY, 0f));
            Vector3 newScr = cam.unproject(new Vector3(newX, newY, 0f));
            Vector3 d = oldScr.sub(newScr);
            cam.translate(d.scl(CAM_PAN_SPEED_MOUSE * dt));
        } else {
            cam.translate(new Vector3(-dx, dy, 0f).scl(dt * CAM_PAN_SPEED_MOUSE_PERSPECTIVE));
        }
    }

    private void handleOrthographicCamera(float dt, OrthographicCamera cam) {
        if (camDistance > 0f) {
            cam.zoom = MathUtils.lerp(cam.zoom, camDistance / 100f, dt * 8f);
        }
    }

    private void handleKeyboardMovement(float dt, Camera cam) {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            Vector3 d;
            if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                d = new Vector3(cam.up);
            } else {
                d = new Vector3(cam.direction);
                d.y = 0f;
                d.nor();
            }
            d = d.scl(dt * CAM_PAN_SPEED_KEYBOARD);
            cam.translate(d);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            Vector3 d;
            if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                d = new Vector3(cam.up);
            } else {
                d = new Vector3(cam.direction);
                d.y = 0f;
                d.nor();
            }
            d = d.scl(dt * -CAM_PAN_SPEED_KEYBOARD);
            cam.translate(d);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            Vector3 d = new Vector3(cam.direction);
            d.rotate(Vector3.Y, 90f);
            d.y = 0f;
            d.nor().scl(dt * CAM_PAN_SPEED_KEYBOARD);
            cam.translate(d);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            Vector3 d = new Vector3(cam.direction);
            d.rotate(Vector3.Y, -90f);
            d.y = 0f;
            d.nor().scl(dt * CAM_PAN_SPEED_KEYBOARD);
            cam.translate(d);
        }
    }

    private void handleRotation(float dt, Camera cam) {
        Vector3 p = new Vector3(cam.position);
        Vector3 d = new Vector3(cam.direction).scl(camDistance);
        p = p.add(d);

        boolean shouldRotate = shouldRotate();
        if (shouldRotate || Math.abs(lastDx) > 0) {
            int dx = shouldRotate ? Gdx.input.getDeltaX() : lastDx;
            int dy = shouldRotate ? Gdx.input.getDeltaY() : lastDy;

            cam.rotateAround(p, Vector3.Y, (float) dx * dt * -25f);
            if (snapMode == CameraSnapMode.NONE) {
                cam.rotateAround(p, Vector3.X, (float) dy * dt * -25f);
            }
            cam.up.set(0f, 1f, 0f);

            lastDx = (int) (dx * 0.9f);
            lastDy = (int) (dy * 0.9f);
        } else if (snapMode == CameraSnapMode.TOPDOWN) {
            // Lerp to correct orientation
            Vector2 target = new Vector2();
            if (Math.abs(cam.direction.x) > Math.abs(cam.direction.z)) {
                if (cam.direction.x > 0f) {
                    target.set(1f, 0f);
                } else {
                    target.set(-1f, 0f);
                }
            } else {
                if (cam.direction.z > 0f) {
                    target.set(0f, 1f);
                } else {
                    target.set(0f, -1f);
                }
            }

            float angle = target.angle(new Vector2(cam.direction.x, cam.direction.z));
            cam.rotateAround(p, Vector3.Y, angle * dt * 8f);
        }
    }

    private boolean shouldRotate() {
        return Gdx.input.isButtonPressed(Input.Buttons.RIGHT) ||
                (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT));
    }

    public void setCameraSnap(CameraSnapMode snap) {
        this.snapMode = snap;
    }

    public void setProjection(Boolean perspective) {
        if (perspective) {
            EditorCamera.main.setToPerspective();
            camDistance /= 2f;
        } else {
            EditorCamera.main.setToOrthogonal();
            camDistance *= 2f;
        }
    }
}
