package me.manabreak.ratio.editor;

import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import me.manabreak.ratio.ui.DefTable;

public class TabbedContainer extends DefTable implements TabbedPaneListener {
    private TabbedPane tabs;
    private VisTable content;

    public TabbedContainer() {
        super();

        tabs = new TabbedPane();
        tabs.getTable().top();
        add(tabs.getTable()).growX();

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
    }

    @Override
    public void removedTab(Tab tab) {

    }

    @Override
    public void removedAllTabs() {

    }
}
