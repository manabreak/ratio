package me.manabreak.ratio.plugins.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.adapter.ArrayListAdapter;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import me.manabreak.ratio.common.Properties;
import me.manabreak.ratio.ui.IntegerTextFieldFilter;
import me.manabreak.ratio.ui.NumericTextFieldFilter;
import me.manabreak.ratio.ui.Res;
import me.manabreak.ratio.utils.Action1;

public class PropertyAdapter extends ArrayListAdapter<Properties.Entry, VisTable> {

    private Action1<Properties.Entry> deleteCallback;

    public PropertyAdapter(Properties properties) {
        super(properties.getProperties());
    }

    @Override
    protected VisTable createView(Properties.Entry item) {
        VisTable t = new VisTable();

        if (item.canDelete()) {
            final VisTextButton btnDelete = new VisTextButton(Res.ICON_DELETE, Res.ICONS_SMALL, new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (deleteCallback != null) deleteCallback.call(item);
                }
            });
            t.add(btnDelete).size(20f).padRight(2f);
        }

        t.add(item.getKey()).width(140f);

        if (item.getValue() instanceof Boolean) {
            VisCheckBox cb = new VisCheckBox("", (Boolean) item.getValue());
            cb.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    item.setValue(cb.isChecked());
                }
            });
            t.add(cb).growX();
        } else if (item.getValue() instanceof String) {
            VisTextField tf = new VisTextField((String) item.getValue());
            tf.setTextFieldListener((textField, c) -> item.setValue(textField.getText()));
            t.add(tf).growX();
        } else if (item.getValue() instanceof Double) {
            VisTextField tf = new VisTextField("" + item.getValue());
            tf.setTextFieldFilter(new NumericTextFieldFilter());
            tf.setTextFieldListener(((textField, c) -> {
                final String text = textField.getText();
                try {
                    final double v = Double.parseDouble(text);
                    item.setValue(v);
                } catch (NumberFormatException ignored) {

                }
            }));
            t.add(tf).growX();
        } else if (item.getValue() instanceof Integer) {
            VisTextField tf = new VisTextField("" + item.getValue());
            tf.setTextFieldFilter(new IntegerTextFieldFilter());
            tf.setTextFieldListener(((textField, c) -> {
                final String text = textField.getText();
                try {
                    final int v = Integer.parseInt(text);
                    item.setValue(v);
                } catch (NumberFormatException ignored) {

                }
            }));
            t.add(tf).growX();
        }
        return t;
    }

    public void setDeleteCallback(Action1<Properties.Entry> deleteCallback) {
        this.deleteCallback = deleteCallback;
    }
}
