package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.Input;

public class AppendMode extends LevelEditorMode {
    private LevelEditorPlugin plugin;
    private int clicksLeft = 0;
    private int clicksRight = 0;

    AppendMode(LevelEditorPlugin plugin) {
        this.plugin = plugin;
    }

    private void clear() {
        clicksRight = 0;
        clicksLeft = 0;
    }

    @Override
    boolean touchDown(int screenX, int screenY, int button) {
        if (button == Input.Buttons.LEFT) {
            if (clicksRight > 0) {
                clear();
                return true;
            }

            clicksLeft++;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int button) {
        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        return false;
    }
}
