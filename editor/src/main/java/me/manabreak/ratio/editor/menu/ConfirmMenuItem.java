package me.manabreak.ratio.editor.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.MenuItem;

import me.manabreak.ratio.utils.Action1;

public class ConfirmMenuItem extends MenuItem {
    public ConfirmMenuItem(Stage stage, String text, String title, String message, Action1<Boolean> result) {
        super(text, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Dialogs.showConfirmDialog(stage, title, message, new String[] {"Cancel", "OK"}, new Boolean[] {false, true}, result::call);
            }
        });
    }
}
