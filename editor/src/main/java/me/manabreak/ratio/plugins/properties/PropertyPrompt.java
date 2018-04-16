package me.manabreak.ratio.plugins.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import me.manabreak.ratio.ui.UiUtils;

public class PropertyPrompt extends ChangeListener {
    private final Table parent;
    private final PropertyHandler propertyHandler;

    public PropertyPrompt(Table parent, PropertyHandler propertyHandler) {
        this.parent = parent;
        this.propertyHandler = propertyHandler;
    }

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        PopupMenu menu = new PopupMenu();
        MenuItem booleanItem = new MenuItem("Boolean", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                UiUtils.promptProperty(parent.getStage(), "Boolean", propertyHandler::createBooleanProperty);
            }
        });
        menu.addItem(booleanItem);

        MenuItem stringItem = new MenuItem("String", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                UiUtils.promptProperty(parent.getStage(), "String", propertyHandler::createStringProperty);
            }
        });
        menu.addItem(stringItem);

        MenuItem intItem = new MenuItem("Integer", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                UiUtils.promptProperty(parent.getStage(), "Integer", propertyHandler::createIntProperty);
            }
        });
        menu.addItem(intItem);

        MenuItem doubleItem = new MenuItem("Double", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                UiUtils.promptProperty(parent.getStage(), "Double", propertyHandler::createDoubleProperty);
            }
        });
        menu.addItem(doubleItem);

        MenuItem vec3Item = new MenuItem("Vector3", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                UiUtils.promptProperty(parent.getStage(), "Vector3", propertyHandler::createVec3Property);
            }
        });
        menu.addItem(vec3Item);

        menu.showMenu(parent.getStage(), parent);
    }
}
