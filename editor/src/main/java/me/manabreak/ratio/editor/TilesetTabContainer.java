package me.manabreak.ratio.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import me.manabreak.ratio.plugins.tilesets.TilesetPresenter;

public class TilesetTabContainer extends Table implements TabbedPaneListener {
    private final TilesetPresenter presenter;
    private TabbedPane tabs;
    private Table content;

    public TilesetTabContainer(TilesetPresenter presenter) {
        super();
        this.presenter = presenter;

        tabs = new TabbedPane();
        add(tabs.getTable()).left().top().growX();
        row();
        tabs.addListener(this);

        content = new VisTable();
        add(content).grow().top();
    }

    public void addTab(Tab tab) {
        tabs.add(tab);
    }

    @Override
    public void switchedTab(Tab tab) {
        content.clear();
        content.add(tab.getContentTable()).grow();
        content.pack();

        TilesetTab tt = (TilesetTab) tab;
        presenter.selectTileset(tt.getTileset());
    }

    @Override
    public void removedTab(Tab tab) {

    }

    @Override
    public void removedAllTabs() {

    }

    public TabbedPane getTabs() {
        return tabs;
    }
}
