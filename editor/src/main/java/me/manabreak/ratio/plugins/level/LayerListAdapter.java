package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.adapter.ArrayListAdapter;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import me.manabreak.ratio.ui.Res;

import java.util.ArrayList;

public class LayerListAdapter extends ArrayListAdapter<TileLayer, VisTable> {

    private final Drawable bg = VisUI.getSkin().getDrawable("window-bg");
    private final Drawable selection = VisUI.getSkin().getDrawable("list-selection");
    private final LayerPresenter presenter;

    public LayerListAdapter(LayerPresenter presenter, ArrayList<TileLayer> array) {
        super(array);
        this.presenter = presenter;
        setSelectionMode(SelectionMode.SINGLE);
    }

    @Override
    protected VisTable createView(TileLayer item) {
        VisTable t = new VisTable(true);

        VisTextButton btnShow = new VisTextButton(item.isVisible() ? Res.ICON_EYE : Res.ICON_EYE_OFF, Res.ICONS_SMALL, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                item.setVisible(!item.isVisible());
                if (item.isVisible()) {
                    ((VisTextButton) actor).setText(Res.ICON_EYE);
                } else {
                    ((VisTextButton) actor).setText(Res.ICON_EYE_OFF);
                }
            }
        });
        t.add(btnShow).padLeft(2f).minWidth(16f);

        VisTextField name = new VisTextField(item.getName());
        name.setTextFieldListener((textField, c) -> {
            item.setName(textField.getText());
        });
        name.setEnterKeyFocusTraversal(true);
        name.setAlignment(Align.left);
        t.add(name).pad(2f).left();
        t.add().grow();

        return t;
    }

    @Override
    protected void selectView(VisTable view) {
        view.setBackground(selection);
    }

    @Override
    protected void deselectView(VisTable view) {
        view.setBackground(bg);
    }
}
