package me.manabreak.ratio.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class EditorTab extends Tab {

    private final String title;
    private final VisTable content;

    public EditorTab(String title, VisTable content) {
        super(false, false);
        this.title = title;
        this.content = content;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public Table getContentTable() {
        return content;
    }
}
