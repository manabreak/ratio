package me.manabreak.ratio.editor;

import com.badlogic.gdx.InputAdapter;

/**
 * Base class for all editor plugins
 */
public abstract class EditorPlugin extends InputAdapter {

    protected EditorController editorController;
    protected EditorView editorView;
    private boolean active = false;

    protected EditorPlugin() {

    }

    void setEditorController(EditorController editorController) {
        this.editorController = editorController;
    }

    void setEditorView(EditorView editorView) {
        this.editorView = editorView;
    }

    public boolean isActive() {
        return active;
    }

    void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Called when all the plugin dependencies are set and
     * the plugin is ready to initialize itself.
     */
    public void initialize() {
        // NOP
    }

    /**
     * Called when all plugins have initialized themselves.
     */
    public void postInitialize() {
        // NOP
    }

    protected <T extends EditorPlugin> T getPlugin(Class<T> aClass) {
        return editorController.getPlugin(aClass);
    }
}
