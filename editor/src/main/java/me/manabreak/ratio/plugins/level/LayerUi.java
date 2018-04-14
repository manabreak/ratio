package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.ListView;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import me.manabreak.ratio.common.mvp.MvpView;
import me.manabreak.ratio.ui.Res;

public class LayerUi extends MvpView<LayerPresenter> {

    private VisTable content;
    private VisTable actions;

    protected LayerUi(LayerPresenter presenter) {
        super(presenter);

        actions = new VisTable(true);
        actions.add("Layers").left().padLeft(8f).expandX();

        actions.defaults().right().width(32f).height(32f).pad(2f);
        VisTextButton btnNew = new VisTextButton(Res.ICON_ADD, Res.ICONS_SMALL, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.createLayer();
            }
        });
        actions.add(btnNew);

        VisTextButton btnMoveUp = new VisTextButton(Res.ICON_UP, Res.ICONS_SMALL, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.moveLayerUp();
            }
        });
        actions.add(btnMoveUp);

        VisTextButton btnMoveDown = new VisTextButton(Res.ICON_DOWN, Res.ICONS_SMALL, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.moveLayerDown();
            }
        });
        actions.add(btnMoveDown);

        VisTextButton btnDelete = new VisTextButton(Res.ICON_DELETE, Res.ICONS_SMALL, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.deleteLayer();
            }
        });
        actions.add(btnDelete);

        add(actions).growX();

        row();

        content = new VisTable(true);
        add(content).grow().maxHeight(300f);


        presenter.viewCreated();
    }

    public void createListView(LayerListAdapter adapter) {
        ListView<TileLayer> listView = new ListView<>(adapter);
        content.add(listView.getMainTable()).grow().top().pad(2f);
        listView.setItemClickListener(presenter::layerClicked);
    }
}
