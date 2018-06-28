package me.manabreak.ratio.editor;

import com.badlogic.gdx.InputAdapter;
import me.manabreak.ratio.plugins.level.LevelEditorPlugin;
import me.manabreak.ratio.plugins.toolbar.ToolbarPlugin;

import static com.badlogic.gdx.Input.Keys.*;

public class ShortcutProcessor extends InputAdapter {
    private final EditorController controller;
    private final EditorStage stage;

    private boolean controlDown;
    private boolean shiftDown;

    ShortcutProcessor(EditorController controller, EditorStage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (stage.getKeyboardFocus() != null) return false;

        switch (keycode) {
            case CONTROL_LEFT:
            case CONTROL_RIGHT:
                controlDown = true;
                break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                shiftDown = true;
                controller.getPlugin(LevelEditorPlugin.class).engageLineTool();
                break;
            case B:
                controller.getPlugin(ToolbarPlugin.class).getUi().onPaintToolClicked();
                break;
            case D:
                controller.getPlugin(ToolbarPlugin.class).getUi().onBlockToolClicked();
                break;
            case E:
                controller.getPlugin(ToolbarPlugin.class).getUi().onEraseToolClicked();
                break;
            case F:
                controller.getPlugin(ToolbarPlugin.class).getUi().onFloorToolClicked();
                break;
            case H:
                controller.getPlugin(LevelEditorPlugin.class).toggleLayerHighlighting();
                break;
            case O:
                if (controlDown) {
                    controller.onOpenClicked();
                    controlDown = false;
                } else {
                    controller.getPlugin(LevelEditorPlugin.class).getObjectRenderer().rotateDrawMode();
                }
                break;
            case P:
                if (!controlDown) {
                    controller.getPlugin(LevelEditorPlugin.class).getObjectRenderer().toggleSeeThrough();
                }
                break;
            case S:
                if (controlDown) {
                    if (shiftDown) {
                        controller.onSaveAsClicked();
                        shiftDown = false;
                    } else {
                        controller.onSaveClicked();
                    }
                    controlDown = false;
                } else {
                    controller.getPlugin(ToolbarPlugin.class).getUi().onSelectToolClicked();
                }
                break;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case CONTROL_LEFT:
            case CONTROL_RIGHT:
                controlDown = false;
                break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                shiftDown = false;
                controller.getPlugin(LevelEditorPlugin.class).disengageLineTool();
        }

        return false;
    }
}
