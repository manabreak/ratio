package me.manabreak.ratio.editor.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import me.manabreak.ratio.editor.EditorController;
import me.manabreak.ratio.plugins.level.LevelEditorPlugin;

public class EditorMenuBar extends MenuBar {

    private final Stage stage;
    private final EditorController controller;

    public EditorMenuBar(Stage stage, EditorController controller) {
        this.stage = stage;
        this.controller = controller;
        addMenu(createFileMenu());
        addMenu(createOptionsMenu());
    }

    private Menu createOptionsMenu() {
        Menu optMenu = new Menu("Options");

        MenuItem itemHighlight = new MenuItem("Toggle layer highlighting", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.getPlugin(LevelEditorPlugin.class).toggleLayerHighlighting();
            }
        });
        itemHighlight.setShortcut(Input.Keys.H);
        optMenu.addItem(itemHighlight);

        return optMenu;
    }

    private Menu createFileMenu() {
        Menu fileMenu = new Menu("File");

        MenuItem itemNew = new MenuItem("New scene", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Dialogs.showConfirmDialog(stage,
                        "Discard Changes?",
                        "Unsaved changes will be lost!\nAre you sure you want to continue?",
                        new String[]{"Cancel", "OK"},
                        new Boolean[]{false, true},
                        result -> {
                            if (!result) return;
                            // TODO Handle reset
                        });
            }
        });
        fileMenu.addItem(itemNew);

        MenuItem itemOpen = new MenuItem("Open", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.onOpenClicked();
            }
        });
        itemOpen.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.O);
        fileMenu.addItem(itemOpen);

        MenuItem itemSave = new MenuItem("Save", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.onSaveClicked();
            }
        });
        itemSave.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.S);
        fileMenu.addItem(itemSave);

        MenuItem itemSaveAs = new MenuItem("Save As...", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.onSaveAsClicked();
            }
        });
        itemSaveAs.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.SHIFT_LEFT, Input.Keys.S);
        fileMenu.addItem(itemSaveAs);
        fileMenu.addSeparator();

        fileMenu.addItem(new ConfirmMenuItem(stage, "Exit", "Confirm exit", "Are you sure you want to quit? Unsaved changes will be lost.", result -> {
            if (result) {
                Gdx.app.exit();
            }
        }));

        return fileMenu;
    }
}
