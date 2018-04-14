package me.manabreak.ratio.plugins.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import me.manabreak.ratio.common.mvp.MvpPresenter;
import me.manabreak.ratio.plugins.properties.PropertyHandler;

import java.util.ArrayList;
import java.util.List;

public class ObjectEditorPresenter extends MvpPresenter<ObjectEditorUi> implements PropertyHandler {

    private final ArrayList<GameObject> objects = new ArrayList<>();
    private final ObjectListAdapter adapter = new ObjectListAdapter(this, objects);

    private GameObject detailedItem;
    private GameObject selectedObject = null;

    public ObjectEditorPresenter() {

    }

    public void viewCreated() {
        view.createListView(adapter);
    }

    public void createObject() {
        System.out.println("Add object clicked");
        GameObject obj = new GameObject();
        obj.setName("GameObject 1");
        objects.add(obj);
        adapter.itemsChanged();
    }

    public void removeObject(GameObject item) {
        objects.remove(item);
        adapter.itemsChanged();
    }

    public void objectClicked(GameObject item) {
        detailedItem = item;
        view.showDetails(item);
        if (selectedObject != null) {
            selectedObject.setSelected(false);
            selectedObject = null;
        }
        this.selectedObject = item;
        item.setSelected(true);
    }

    public void changePropertyValue(String key, Object value) {
        detailedItem.setProperty(key, value);
    }

    public void removeProperty(String key) {
        detailedItem.removeProperty(key);
        view.showDetails(detailedItem);
    }

    public void createBooleanProperty(String key) {
        detailedItem.setProperty(key, true);
        view.showDetails(detailedItem);
    }

    public void createStringProperty(String key) {
        detailedItem.setProperty(key, "");
        view.showDetails(detailedItem);
    }

    public void createDoubleProperty(String key) {
        detailedItem.setProperty(key, 0.0);
        view.showDetails(detailedItem);
    }

    @Override
    public void createVec3Property(String key) {
        detailedItem.setProperty(key, new Vector3());
        view.showDetails(detailedItem);
    }

    public void createIntProperty(String key) {
        detailedItem.setProperty(key, 0);
        view.showDetails(detailedItem);
    }

    public void nameChanged(GameObject item, String name) {
        item.setName(name);
    }

    public void typeChanged(GameObject item, String text) {
        item.setType(text);
    }

    public void xChanged(GameObject item, String text) {
        try {
            float x = Float.parseFloat(text);
            item.setX(x);
        } catch (NumberFormatException ignored) {

        }
    }

    public void yChanged(GameObject item, String text) {
        try {
            float y = Float.parseFloat(text);
            item.setY(y);
        } catch (NumberFormatException ignored) {

        }
    }

    public void zChanged(GameObject item, String text) {
        try {
            float z = Float.parseFloat(text);
            item.setZ(z);
        } catch (NumberFormatException ignored) {

        }
    }

    public void colorChanged(GameObject item, Color newColor) {
        item.setColor(newColor);
    }

    public List<GameObject> getObjects() {
        return objects;
    }

    public void loadObjects(List<GameObject> objects) {
        this.objects.clear();
        this.objects.addAll(objects);
        adapter.itemsChanged();
    }

    public void widthChanged(GameObject item, String text) {
        try {
            float width = Float.parseFloat(text);
            item.setSizeX(width);
        } catch (NumberFormatException ignored) {

        }
    }

    public void heightChanged(GameObject item, String text) {
        try {
            float height = Float.parseFloat(text);
            item.setSizeY(height);
        } catch (NumberFormatException ignored) {

        }
    }

    public void depthChanged(GameObject item, String text) {
        try {
            float depth = Float.parseFloat(text);
            item.setSizeZ(depth);
        } catch (NumberFormatException ignored) {

        }
    }

    public void setSelection(GameObject obj) {
        if (this.selectedObject != null) {
            this.selectedObject.setSelected(false);
            this.selectedObject = null;
        }
        this.selectedObject = obj;
        if (obj != null) {
            obj.setSelected(true);
            adapter.getSelectionManager().select(obj);
            objectClicked(obj);
        } else {
            adapter.getSelectionManager().deselectAll();
            view.hideDetails();
        }
    }
}
