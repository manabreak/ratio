package me.manabreak.ratio.ui;

import com.kotcrab.vis.ui.widget.VisTextField;

public class NumericTextFieldFilter implements VisTextField.TextFieldFilter {
    @Override
    public boolean acceptChar(VisTextField textField, char c) {
        return Character.isDigit(c) || c == '.' || (textField.getText().length() == 0 && c == '-');
    }
}
