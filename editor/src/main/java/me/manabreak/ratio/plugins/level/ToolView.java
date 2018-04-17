package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import me.manabreak.ratio.common.mvp.MvpView;

public class ToolView extends MvpView<ToolPresenter> {

    private final ChangeListener toolSizeListener;
    private final VisSlider toolSizeSlider;
    private final VisLabel toolSizeLabel;

    protected ToolView(ToolPresenter presenter) {
        super(presenter);

        add("Tool Settings").padLeft(4f).align(Align.left);
        row().padBottom(4f);

        toolSizeSlider = new VisSlider(0f, 5f, 1f, false);
        toolSizeSlider.setValue(4f);
        toolSizeLabel = new VisLabel("Cell size: 16");
        toolSizeLabel.setAlignment(Align.left);
        add(toolSizeLabel).padLeft(4f).padRight(4f);
        add(toolSizeSlider).growX().padLeft(4f).padRight(4f);
        toolSizeListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int s = Math.round(toolSizeSlider.getValue());
                presenter.toolSizeChanged(s);
            }
        };
        toolSizeSlider.addListener(toolSizeListener);
        row().pad(4f);
        addSeparator().colspan(2);
    }

    public void updateToolSize(int size) {
        int i = Integer.numberOfTrailingZeros(Integer.highestOneBit(size));
        toolSizeSlider.removeListener(toolSizeListener);
        toolSizeSlider.setValue(i);
        toolSizeSlider.addListener(toolSizeListener);
        toolSizeLabel.setText("Tool size: " + size);
    }

    public void toolSizeChanged(int toolSize) {
        toolSizeLabel.setText("Cell size: " + toolSize);
    }
}
