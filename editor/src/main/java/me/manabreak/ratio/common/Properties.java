package me.manabreak.ratio.common;

import java.util.ArrayList;

public class Properties {

    /**
     * Properties that can be added and removed
     */
    private final ArrayList<Entry> properties = new ArrayList<>();

    public Properties() {

    }

    public boolean hasProperty(String key) {
        for (Entry entry : properties) {
            if (entry.key.equals(key)) return true;
        }
        return false;
    }

    public void setProperty(String key, Object value) {
        setProperty(key, value, true);
    }

    public void setStaticProperty(String key, Object value) {
        setProperty(key, value, false);
    }

    private void setProperty(String key, Object value, boolean canDelete) {
        for (Entry entry : properties) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }

        Entry entry = new Entry(key, value, canDelete);
        properties.add(entry);
    }

    public <T> T getProperty(String key, T defaultValue) {
        for (Entry entry : properties) {
            if (entry.key.equals(key)) {
                //noinspection unchecked
                return (T) entry.value;
            }
        }
        return defaultValue;
    }

    public <T> T removeProperty(String key) {
        for (Entry entry : properties) {
            if (entry.key.equals(key)) {
                properties.remove(entry);
                //noinspection unchecked
                return (T) entry.value;
            }
        }
        return null;
    }

    public ArrayList<Entry> getProperties() {
        return properties;
    }

    public static class Entry {
        String key;
        Object value;
        final boolean canDelete;

        Entry(String key, Object value) {
            this.key = key;
            this.value = value;
            canDelete = true;
        }

        Entry(String key, Object value, boolean canDelete) {
            this.key = key;
            this.value = value;
            this.canDelete = canDelete;
        }

        public boolean canDelete() {
            return canDelete;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}