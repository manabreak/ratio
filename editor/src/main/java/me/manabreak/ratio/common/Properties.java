package me.manabreak.ratio.common;

import java.util.ArrayList;

public class Properties {
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
        for (Entry entry : properties) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }

        Entry entry = new Entry(key, value);
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

        Entry(String key, Object value) {
            this.key = key;
            this.value = value;
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