package me.manabreak.ratio.plugins.properties;

import com.kotcrab.vis.ui.widget.ListView;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import me.manabreak.ratio.common.Properties;
import me.manabreak.ratio.common.mvp.MvpView;
import me.manabreak.ratio.ui.Res;

public class LevelPropertiesUi extends MvpView<LevelPropertiesPresenter> {

    private final VisTable content = new VisTable();

    protected LevelPropertiesUi(LevelPropertiesPresenter presenter) {
        super(presenter);
        top();

        VisTable controls = new VisTable();
        VisTextButton btnAddProperty = new VisTextButton(Res.ICON_ADD, Res.ICONS_SMALL);
        new Tooltip.Builder("Add Property").target(btnAddProperty).build();
        btnAddProperty.addListener(new PropertyPrompt(btnAddProperty, presenter));
        controls.add(btnAddProperty);
        add(controls).expandX();

        row();
        addSeparator();
        row();

        add(content).grow();
    }

    public void showProperties(Properties props) {
        content.clear();
        final PropertyAdapter adapter = new PropertyAdapter(props);
        adapter.setDeleteCallback(entry -> presenter.removeProperty(entry.getKey()));
        ListView<Properties.Entry> listView = new ListView<>(adapter);
        content.add(listView.getMainTable()).grow().top();
    }
}
