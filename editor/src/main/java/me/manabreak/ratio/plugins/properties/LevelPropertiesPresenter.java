package me.manabreak.ratio.plugins.properties;

import com.badlogic.gdx.math.Vector3;
import me.manabreak.ratio.common.Properties;
import me.manabreak.ratio.common.mvp.MvpPresenter;

public class LevelPropertiesPresenter extends MvpPresenter<LevelPropertiesUi> implements PropertyHandler {

    private Properties properties = new Properties();

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

    public void changePropertyValue(String key, Object value) {
        properties.setProperty(key, value);
        view.showProperties(properties);
    }

    public Properties getProperties() {
        return properties;
    }

    public void loadProperties(Properties properties) {
        this.properties = properties;
        view.showProperties(properties);
    }
}
