package me.manabreak.ratio.plugins.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import me.manabreak.ratio.editor.EditorController;
import me.manabreak.ratio.plugins.level.LevelEditorPlugin;

public class LayerNudgePrompt extends ChangeListener {
    private final EditorController controller;
    private final Stage stage;

    public LayerNudgePrompt(Stage stage, EditorController controller) {
        this.stage = stage;
        this.controller = controller;
    }

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        Dialogs.InputDialog dialog = new Dialogs.InputDialog("Nudge", "Y", true, input -> {
            try {
                int i = Integer.parseInt(input);
                return true;
            } catch (NumberFormatException ignored) {

            }
            return false;
        }, new InputDialogAdapter() {
            @Override
            public void finished(String input) {
                controller.getPlugin(LevelEditorPlugin.class)
                        .nudgeSelectedLayerBy(Integer.parseInt(input));
            }
        });
        stage.addActor(dialog);
    }
}
