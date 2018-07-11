package me.manabreak.ratio.plugins.level;

abstract class LevelEditorMode {

    boolean touchDown(int screenX, int screenY, int button) {
        return false;
    }

    boolean touchUp(int screenX, int screenY, int button) {
        return false;
    }

    boolean mouseMoved(int x, int y) {
        return false;
    }
}
