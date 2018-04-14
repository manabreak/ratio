package me.manabreak.ratio.plugins.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import me.manabreak.ratio.common.mvp.MvpView;
import me.manabreak.ratio.plugins.properties.PropertyPrompt;
import me.manabreak.ratio.ui.IntegerTextFieldFilter;
import me.manabreak.ratio.ui.NumericTextFieldFilter;

import java.util.Map;

public class ObjectEditorUi extends MvpView<ObjectEditorPresenter> {

    private final VisTable toolRegion;
    private final VisTable objectListRegion;
    private final VisTable objectDetailsRegion;
    private final VisTable objectPropertyActionsRegion;
    private final VisTable objectPropertyListRegion;
    private final VisTable objectBasicDetailsRegion;

    protected ObjectEditorUi(ObjectEditorPresenter presenter) {
        super(presenter);

        toolRegion = new VisTable(true);
        add(toolRegion).growX().pad(4f);

        VisTextButton btnAdd = new VisTextButton("New Object", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.createObject();
            }
        });
        toolRegion.add(btnAdd).pad(4f);

        row().pad(2f);

        objectListRegion = new VisTable(true);
        add(objectListRegion).pad(4f).growX();

        row().pad(2f);

        objectDetailsRegion = new VisTable(true);
        add(objectDetailsRegion).grow().pad(4f);
        objectDetailsRegion.setVisible(false);

        objectBasicDetailsRegion = new VisTable(true);
        objectDetailsRegion.add(objectBasicDetailsRegion).growX().pad(4f);
        objectDetailsRegion.row();

        objectPropertyActionsRegion = new VisTable(true);
        VisTextButton btnAddProperty = new VisTextButton("Add Property");
        btnAddProperty.addListener(new PropertyPrompt(this, presenter));
        objectPropertyActionsRegion.add(btnAddProperty).pad(4f);
        objectDetailsRegion.add(objectPropertyActionsRegion).growX().pad(4f);
        objectDetailsRegion.row();

        objectPropertyListRegion = new VisTable(true);
        objectDetailsRegion.add(objectPropertyListRegion).grow().pad(4f);

        presenter.viewCreated();
    }

    public void hideDetails() {
        objectDetailsRegion.setVisible(false);
    }

    void showDetails(GameObject item) {
        System.out.println("Showing details for item " + item.getName());
        objectDetailsRegion.setVisible(true);
        objectBasicDetailsRegion.clear();
        objectBasicDetailsRegion.columnDefaults(0)
                .width(100f).padRight(4f);
        objectBasicDetailsRegion.columnDefaults(1)
                .growX();

        VisLabel lblType = new VisLabel("Type");
        objectBasicDetailsRegion.add(lblType);
        VisTextField tfType = new VisTextField(item.getType());
        tfType.setTextFieldListener((textField, c) -> {
            presenter.typeChanged(item, textField.getText());
        });
        objectBasicDetailsRegion.add(tfType);
        objectBasicDetailsRegion.row();

        createNumberField(item.getX(), "X", (textField, c) -> presenter.xChanged(item, textField.getText()));
        createNumberField(item.getY(), "Y", (textField, c) -> presenter.yChanged(item, textField.getText()));
        createNumberField(item.getZ(), "Z", (textField, c) -> presenter.zChanged(item, textField.getText()));

        createNumberField(item.getSizeX(), "Width", ((textField, c) -> presenter.widthChanged(item, textField.getText())));
        createNumberField(item.getSizeY(), "Height", ((textField, c) -> presenter.heightChanged(item, textField.getText())));
        createNumberField(item.getSizeZ(), "Depth", ((textField, c) -> presenter.depthChanged(item, textField.getText())));

        VisLabel lblColor = new VisLabel("Color");
        objectBasicDetailsRegion.add(lblColor);
        VisTextButton btnColor = new VisTextButton("Choose");
        btnColor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ColorPicker cp = new ColorPicker("Choose Color", new ColorPickerAdapter() {
                    @Override
                    public void changed(Color newColor) {
                        btnColor.setColor(newColor);
                        presenter.colorChanged(item, newColor);
                    }

                    @Override
                    public void finished(Color newColor) {
                        presenter.colorChanged(item, newColor);
                    }
                });
                getStage().addActor(cp);
            }
        });
        objectBasicDetailsRegion.add(btnColor);
        objectBasicDetailsRegion.row();

        objectPropertyListRegion.clear();

        VisTable content = new VisTable(true);
        content.columnDefaults(0)
                .width(Value.percentWidth(0.5f));
        content.columnDefaults(1)
                .width(Value.percentWidth(0.5f));

        for (Map.Entry<String, Object> entry : item.getProperties().getProperties().entrySet()) {
            String key = entry.getKey();

            VisTable keyContainer = new VisTable(true);
            VisTextButton btnRemove = new VisTextButton("-", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    presenter.removeProperty(key);
                }
            });
            keyContainer.add(btnRemove).top().padRight(4f).minWidth(32f);

            VisLabel lblKey = new VisLabel(key);
            lblKey.setEllipsis(true);
            keyContainer.add(lblKey).top().padRight(4f);
            content.add(keyContainer).top();

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
            } else if (value instanceof Vector3) {
                Vector3 v = (Vector3) value;

                VisTable vecContainer = new VisTable(true);

                VisTextField tfX = new VisTextField("" + v.x);
                tfX.setTextFieldFilter(new NumericTextFieldFilter());
                tfX.setTextFieldListener(((textField, c) -> {
                    try {
                        v.x = Float.parseFloat(tfX.getText());
                    } catch (NumberFormatException ignored) {

                    }
                }));
                vecContainer.add(tfX).growX();
                vecContainer.row();

                VisTextField tfY = new VisTextField("" + v.y);
                tfY.setTextFieldFilter(new NumericTextFieldFilter());
                tfY.setTextFieldListener(((textField, c) -> {
                    try {
                        v.y = Float.parseFloat(tfY.getText());
                    } catch (NumberFormatException ignored) {

                    }
                }));
                vecContainer.add(tfY).growX();
                vecContainer.row();

                VisTextField tfZ = new VisTextField("" + v.z);
                tfZ.setTextFieldFilter(new NumericTextFieldFilter());
                tfZ.setTextFieldListener(((textField, c) -> {
                    try {
                        v.z = Float.parseFloat(tfZ.getText());
                    } catch (NumberFormatException ignored) {

                    }
                }));
                vecContainer.add(tfZ).growX();

                content.add(vecContainer);

                VisTextButton btnNormalize = new VisTextButton("Nor", new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        v.nor();
                        tfX.setText("" + v.x);
                        tfY.setText("" + v.y);
                        tfZ.setText("" + v.z);
                    }
                });
                keyContainer.row();
                keyContainer.add(btnNormalize);
            }

            content.row();
            content.addSeparator().colspan(2);
            content.row();
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setScrollingDisabled(true, false);
        objectPropertyListRegion.add(scrollPane).grow().align(Align.top);
    }

    private void createNumberField(float initialValue, String labelText, VisTextField.TextFieldListener listener) {
        VisLabel label = new VisLabel(labelText);
        objectBasicDetailsRegion.add(label);
        VisTextField textField = new VisTextField("" + initialValue);
        textField.setTextFieldFilter(new NumericTextFieldFilter());
        textField.setTextFieldListener(listener);
        objectBasicDetailsRegion.add(textField);
        objectBasicDetailsRegion.row();
    }

    void createListView(ObjectListAdapter adapter) {
        ListView<GameObject> listView = new ListView<>(adapter);
        objectListRegion.add(listView.getMainTable()).growX().top();
        listView.setItemClickListener(presenter::objectClicked);
    }
}
