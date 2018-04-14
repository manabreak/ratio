package me.manabreak.ratio.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Properties {
    private final Map<String, Object> properties = new HashMap<String, Object>();

    public Properties() {
    }

    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public <T> T getProperty(String key, T defaultValue) {
        if (!hasProperty(key)) return defaultValue;
        Object o = properties.get(key);
        //noinspection unchecked
        return (T) o;
    }

    public <T> T removeProperty(String key) {
        //noinspection unchecked
        return (T) properties.remove(key);
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }
}