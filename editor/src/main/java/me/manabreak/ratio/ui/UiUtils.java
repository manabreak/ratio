package me.manabreak.ratio.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import me.manabreak.ratio.utils.Action1;

public class UiUtils {
    public static void promptProperty(Stage stage, String type, Action1<String> key) {
        Dialogs.InputDialog dialog = new Dialogs.InputDialog("New " + type, "Name", true, input -> !input.isEmpty(), new InputDialogAdapter() {
            @Override
            public void finished(String input) {
                key.call(input);
            }
        });
        stage.addActor(dialog);
    }
}
