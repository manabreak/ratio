package me.manabreak.ratio.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;

public class UiFactory {

    public interface UiListener<T> {
        void onEvent(T value);
    }

    public static VisTable booleanField(String name, boolean checked, UiListener<Boolean> listener) {
        return booleanField(createDefaultTable(), name, checked, listener);
    }

    public static VisTable booleanField(VisTable parent, String name, boolean checked, UiListener<Boolean> listener) {
        VisLabel label = new VisLabel(name);
        parent.add(label);

        VisCheckBox cb = new VisCheckBox("", checked);
        parent.add(cb);

        if (listener != null) {
            cb.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    listener.onEvent(cb.isChecked());
                }
            });
        }

        return parent;
    }

    public static VisTable intField(String name, int value, UiListener<Integer> listener) {
        return intField(createDefaultTable(), name, value, listener);
    }

    public static VisTable intField(VisTable parent, String name, int value, UiListener<Integer> listener) {
        VisLabel label = new VisLabel(name);
        parent.add(label);

        VisTextField tf = new VisTextField("" + value);
        parent.add(tf);

        tf.setTextFieldFilter(new IntegerTextFieldFilter(true));

        if (listener != null) {
            tf.setTextFieldListener((visTextField, c) -> {
                if (tf.getText().length() > 0) {
                    try {
                        int i = Integer.parseInt(tf.getText());
                        listener.onEvent(i);
                    } catch (NumberFormatException e) {
                        // NOP
                    }
                }
            });
        }

        return parent;
    }

    public static VisTable floatField(String name, float value, UiListener<Float> listener) {
        return floatField(createDefaultTable(), name, value, listener);
    }

    public static VisTable floatField(VisTable parent, String name, float value, UiListener<Float> listener) {
        VisLabel label = new VisLabel(name);
        parent.add(label);

        VisTextField tf = new VisTextField("" + value);
        parent.add(tf);

        tf.setTextFieldFilter(new NumericTextFieldFilter());

        if (listener != null) {
            tf.setTextFieldListener((visTextField, c) -> {
                if (tf.getText().length() > 0) {
                    try {
                        float f = Float.parseFloat(tf.getText());
                        listener.onEvent(f);
                    } catch (NumberFormatException e) {
                        // NOP
                    }
                }
            });
        }

        return parent;
    }

    public static VisTable doubleField(String name, double value, UiListener<Double> listener) {
        return doubleField(createDefaultTable(), name, value, listener);
    }

    public static VisTable doubleField(VisTable parent, String name, double value, UiListener<Double> listener) {
        VisLabel label = new VisLabel(name);
        parent.add(label);

        VisTextField tf = new VisTextField("" + value);
        parent.add(tf);

        tf.setTextFieldFilter(new NumericTextFieldFilter());

        if (listener != null) {
            tf.setTextFieldListener((visTextField, c) -> {
                if (tf.getText().length() > 0) {
                    try {
                        double d = Double.parseDouble(tf.getText());
                        listener.onEvent(d);
                    } catch (NumberFormatException e) {
                        // NOP
                    }
                }
            });
        }

        return parent;
    }

    public static VisTable textField(String name, String value, UiListener<String> listener) {
        return textField(createDefaultTable(), name, value, listener);
    }

    public static VisTable textField(VisTable parent, String name, String value, UiListener<String> listener) {
        VisLabel label = new VisLabel(name);
        parent.add(label);

        VisTextField tf = new VisTextField("" + value);
        parent.add(tf);

        if (listener != null) {
            tf.setTextFieldListener((visTextField, c) -> {
                if (tf.getText().length() > 0) {
                    try {
                        listener.onEvent(tf.getText());
                    } catch (NumberFormatException e) {
                        // NOP
                    }
                }
            });
        }

        return parent;
    }

    public static VisTable createDefaultTable() {
        VisTable entry = new VisTable(true);
        entry.columnDefaults(0)
                .width(80f)
                .padLeft(8f)
                .padRight(4f);
        entry.columnDefaults(1)
                .expandX()
                .padRight(4f);
        return entry;
    }

}
