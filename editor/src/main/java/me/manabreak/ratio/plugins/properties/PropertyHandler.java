package me.manabreak.ratio.plugins.properties;

public interface PropertyHandler {
    void createBooleanProperty(String key);

    void createStringProperty(String key);

    void createIntProperty(String key);

    void createDoubleProperty(String key);

    void createVec3Property(String key);
}
