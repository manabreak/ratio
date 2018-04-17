package me.manabreak.ratio.ui;

import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.ui.VisUI;

public class Res {

    public static final String ICONS_LARGE = "icons-large";
    public static final String ICONS_SMALL = "icons-small";
    public static final String ICONS_SMALL_TOGGLE = "icons-small-toggle";

    public static final String ICON_EYE = "\uf06e";
    public static final String ICON_EYE_OFF = "\uf070";
    public static final String ICON_ADD = "\uf067";
    public static final String ICON_UP = "\uf106";
    public static final String ICON_DOWN = "\uf107";
    public static final String ICON_DELETE = "\uf00d";
    public static final String ICON_BLOCK = "\uf1b2";
    public static final String ICON_FLOOR = "\uf0c8";
    public static final String ICON_ERASE = "\uf12d";
    public static final String ICON_PAINT = "\uf1fc";
    public static final String ICON_SELECT = "\uf245";
    public static final String ICON_BOX_CARET_LEFT = "\uf191";
    public static final String ICON_BOX_CARET_RIGHT = "\uf152";
    public static final String ICON_CAMERA = "\uf030";
    public static final String ICON_LOCK = "\uf023";

    public static void load() {
        VisUI.load(Gdx.files.internal("tinted.json"));
    }
}
