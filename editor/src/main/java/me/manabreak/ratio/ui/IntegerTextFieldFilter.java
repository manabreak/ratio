package me.manabreak.ratio.ui;

import com.kotcrab.vis.ui.widget.VisTextField;

public class IntegerTextFieldFilter implements VisTextField.TextFieldFilter {

    private boolean allowNegative = true;

    public IntegerTextFieldFilter() {
        allowNegative = true;
    }

    public IntegerTextFieldFilter(boolean allowNegative) {
        this.allowNegative = allowNegative;
    }

    @Override
    public boolean acceptChar(VisTextField textField, char c) {
        return Character.isDigit(c) || (allowNegative && textField.getText().length() == 0 && c == '-');
    }
}
