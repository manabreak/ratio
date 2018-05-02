package me.manabreak.ratio.plugins.properties;

import com.badlogic.gdx.math.Vector3;
import me.manabreak.ratio.common.Properties;
import me.manabreak.ratio.common.mvp.MvpPresenter;
import me.manabreak.ratio.plugins.level.TileLayer;

public class LayerPropertiesPresenter extends MvpPresenter<LayerPropertiesUi> implements PropertyHandler {

    private Properties properties;
    private PropertyAdapter adapter;

    public void layerSelected(TileLayer layer) {
        if (layer == null) {
            this.properties = null;
            view.clearProperties();
        } else {
            this.properties = layer.getProperties();
            view.showProperties(properties);
        }
    }

    public void createBooleanProperty(String key) {
        properties.setProperty(key, true);
        view.showProperties(properties);
    }

    public void createStringProperty(String key) {
        properties.setProperty(key, "");
        view.showProperties(properties);
    }

    public void createIntProperty(String key) {
        properties.setProperty(key, 0);
        view.showProperties(properties);
    }

    public void createDoubleProperty(String key) {
        properties.setProperty(key, 0.0);
        view.showProperties(properties);
    }

    @Override
    public void createVec3Property(String key) {
        properties.setProperty(key, new Vector3());
        view.showProperties(properties);
    }

    public void removeProperty(String key) {
        properties.removeProperty(key);
        view.showProperties(properties);
    }
}
