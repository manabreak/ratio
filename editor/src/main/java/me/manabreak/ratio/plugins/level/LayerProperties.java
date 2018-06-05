package me.manabreak.ratio.plugins.level;

import me.manabreak.ratio.common.Properties;

public class LayerProperties extends Properties {

    private static final String PROPERTY_OFFSET_X = "offset_x";
    private static final String PROPERTY_OFFSET_Y = "offset_y";
    private static final String PROPERTY_OFFSET_Z = "offset_z";

    LayerProperties() {
        setStaticProperty(PROPERTY_OFFSET_X, 0);
        setStaticProperty(PROPERTY_OFFSET_Y, 0);
        setStaticProperty(PROPERTY_OFFSET_Z, 0);
    }

    public int getOffsetX() {
        return getProperty(PROPERTY_OFFSET_X, 0);
    }

    public int getOffsetY() {
        return getProperty(PROPERTY_OFFSET_Y, 0);
    }

    public int getOffsetZ() {
        return getProperty(PROPERTY_OFFSET_Z, 0);
    }
}
