package me.manabreak.ratio.plugins.properties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.*;
import me.manabreak.ratio.common.Properties;
import me.manabreak.ratio.common.mvp.MvpView;
import me.manabreak.ratio.ui.IntegerTextFieldFilter;
import me.manabreak.ratio.ui.NumericTextFieldFilter;

import java.util.Map;

public class LevelPropertiesUi extends MvpView<LevelPropertiesPresenter> {

    private final VisTable content = new VisTable();

    protected LevelPropertiesUi(LevelPropertiesPresenter presenter) {
        super(presenter);
        top();

        VisTable controls = new VisTable();
        VisTextButton btnAddProperty = new VisTextButton("Add Property");
        btnAddProperty.addListener(new PropertyPrompt(this, presenter));
        controls.add(btnAddProperty);
        add(controls).expandX();

        row();
        addSeparator();
        row();

        content.top();
        content.columnDefaults(1)
                .width(100f).padRight(4f);
        content.columnDefaults(2)
                .growX();

        ScrollPane pane = new ScrollPane(content);
        add(pane).expand();
    }

    public void showProperties(Properties props) {
        content.clear();
        for (Map.Entry<String, Object> entry : props.getProperties().entrySet()) {
            String key = entry.getKey();

            VisTextButton btnRemove = new VisTextButton("-", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    presenter.removeProperty(key);
                }
            });
            content.add(btnRemove).padRight(4f).minWidth(32f);

            VisLabel lblKey = new VisLabel(key);
            content.add(lblKey);

            Object value = entry.getValue();
            if (value instanceof String) {
                VisTextField tfValue = new VisTextField((String) value);
                tfValue.setTextFieldListener((textField, c) -> {
                    presenter.changePropertyValue(key, tfValue.getText());
                });
                content.add(tfValue).growX();
            } else if (value instanceof Boolean) {
                VisCheckBox cbValue = new VisCheckBox("", (Boolean) value);
                cbValue.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        presenter.changePropertyValue(key, cbValue.isChecked());
                    }
                });
                content.add(cbValue).growX();
            } else if (value instanceof Integer) {
                VisTextField tfValue = new VisTextField(value.toString());
                tfValue.setTextFieldFilter(new IntegerTextFieldFilter(true));
                tfValue.setTextFieldListener((textField, c) -> {
                    try {
                        int val = Integer.parseInt(tfValue.getText());
                        presenter.changePropertyValue(key, val);
                    } catch (NumberFormatException ignored) {

                    }
                });
                content.add(tfValue).growX();
            } else if (value instanceof Double) {
                VisTextField tfValue = new VisTextField(value.toString());
                tfValue.setTextFieldFilter(new NumericTextFieldFilter());
                tfValue.setTextFieldListener((textField, c) -> {
                    try {
                        double val = Double.parseDouble(tfValue.getText());
                        presenter.changePropertyValue(key, val);
                    } catch (NumberFormatException ignored) {

                    }
                });
                content.add(tfValue).growX();
            }

            content.row();
        }
    }
}
