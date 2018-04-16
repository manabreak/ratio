package me.manabreak.ratio.plugins.objects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.kotcrab.vis.ui.widget.VisTable;
import me.manabreak.ratio.editor.EditorPlugin;
import me.manabreak.ratio.plugins.camera.EditorCamera;

import java.util.List;

public class ObjectEditorPlugin extends EditorPlugin {

    private ObjectEditorUi ui;

    @Override
    public void initialize() {
        VisTable table = new VisTable(true);
        ui = new ObjectEditorUi(new ObjectEditorPresenter());
        table.add(ui).grow();
        editorView.addLeftPanelTab("Objects", table);
        editorController.registerInputProcessor(this);
    }

    public ObjectEditorPresenter getPresenter() {
        return ui.getPresenter();
    }

    public void loadObjects(List<GameObject> objects) {
        ui.getPresenter().loadObjects(objects);
    }

    public void selectAt(int x, int y) {
        Camera cam = EditorCamera.main.getCamera();
        final Ray ray = cam.getPickRay(x, y);
        Vector3 intersection = new Vector3();

        System.out.println("Clicking...");
        for (GameObject obj : getPresenter().getObjects()) {
            BoundingBox bb = new BoundingBox(
                    new Vector3(obj.x, obj.y, obj.z),
                    new Vector3(obj.x + obj.sizeX, obj.y + obj.sizeY, obj.z + obj.sizeZ)
            );
            if (Intersector.intersectRayBounds(ray, bb, intersection)) {
                System.out.println("Clicked object " + obj.getName());
                getPresenter().setSelection(obj);
                return;
            }
        }

        getPresenter().setSelection(null);
    }

    @Override
    public boolean keyUp(int keycode) {
        ui.getPresenter().onKeyEvent(keycode);
        return super.keyUp(keycode);
    }
}
