package me.manabreak.ratio.plugins.objects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.adapter.ArrayListAdapter;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.util.ArrayList;

public class ObjectListAdapter extends ArrayListAdapter<GameObject, VisTable> {

    private final Drawable bg = VisUI.getSkin().getDrawable("window-bg");
    private final Drawable selection = VisUI.getSkin().getDrawable("list-selection");
    private final ObjectEditorPresenter presenter;

    public ObjectListAdapter(ObjectEditorPresenter presenter, ArrayList<GameObject> array) {
        super(array);
        this.presenter = presenter;
        setSelectionMode(SelectionMode.SINGLE);
    }

    @Override
    protected VisTable createView(GameObject item) {
        VisTable t = new VisTable(true);

        VisTextButton btnRemove = new VisTextButton("-", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.removeObject(item);
            }
        });
        t.add(btnRemove).padRight(4f).minWidth(32f);

        VisTextField name = new VisTextField(item.getName());
        name.setTextFieldListener((textField, c) -> {
            item.setName(textField.getText());
        });
        name.setEnterKeyFocusTraversal(true);
        name.setAlignment(Align.left);
        t.add(name).pad(2f).left();
        t.add().growX();
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
