package me.manabreak.ratio.plugins.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import me.manabreak.ratio.common.Properties;
import me.manabreak.ratio.common.mvp.MvpView;
import me.manabreak.ratio.plugins.properties.PropertyAdapter;
import me.manabreak.ratio.plugins.properties.PropertyPrompt;
import me.manabreak.ratio.ui.NumericTextFieldFilter;

public class ObjectEditorUi extends MvpView<ObjectEditorPresenter> {

    private final VisTable toolRegion;
    private final VisTable objectListRegion;
    private final VisTable objectDetailsRegion;
    private final VisTable objectPropertyActionsRegion;
    private final VisTable objectPropertyListRegion;
    private final VisTable objectBasicDetailsRegion;

    protected ObjectEditorUi(ObjectEditorPresenter presenter) {
        super(presenter);

        toolRegion = new VisTable();
        add(toolRegion).growX().pad(4f);

        VisTextButton btnAdd = new VisTextButton("New Object", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.createObject();
            }
        });
        toolRegion.add(btnAdd).pad(4f);

        row().pad(2f);

        objectListRegion = new VisTable();
        add(objectListRegion).pad(4f).growX().maxHeight(200f);

        row().pad(2f);

        objectDetailsRegion = new VisTable();
        add(objectDetailsRegion).grow().minHeight(300f);
        objectDetailsRegion.setVisible(false);

        objectBasicDetailsRegion = new VisTable();
        objectDetailsRegion.add(objectBasicDetailsRegion).growX();
        objectDetailsRegion.row();

        objectPropertyActionsRegion = new VisTable();
        VisTextButton btnAddProperty = new VisTextButton("Add Property");
        btnAddProperty.addListener(new PropertyPrompt(btnAddProperty, presenter));
        objectPropertyActionsRegion.add(btnAddProperty);
        objectDetailsRegion.add(objectPropertyActionsRegion).growX();
        objectDetailsRegion.row();

        objectPropertyListRegion = new VisTable();
        objectDetailsRegion.add(objectPropertyListRegion).grow();

        presenter.viewCreated();
    }

    public void hideDetails() {
        objectDetailsRegion.setVisible(false);
    }

    void showDetails(GameObject item) {
        System.out.println("Showing details for item " + item.getName());
        objectDetailsRegion.setVisible(true);
        objectBasicDetailsRegion.clear();
        objectBasicDetailsRegion.defaults()
                .height(20f)
                .pad(1f);
        objectBasicDetailsRegion.columnDefaults(0)
                .width(140f).padRight(4f);
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
        final PropertyAdapter adapter = new PropertyAdapter(item.getProperties());
        adapter.setDeleteCallback(entry -> presenter.removeProperty(entry.getKey()));
        ListView<Properties.Entry> propertyListView = new ListView<>(adapter);
        propertyListView.getScrollPane().setScrollingDisabled(false, false);
        objectPropertyListRegion.add(propertyListView.getMainTable()).grow().top();
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
