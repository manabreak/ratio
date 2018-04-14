package me.manabreak.ratio.plugins.toolbar;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisTextButton;
import me.manabreak.ratio.common.mvp.MvpView;
import me.manabreak.ratio.ui.Res;

public class ToolbarUi extends MvpView<ToolbarPresenter> {

    private final ButtonGroup<VisTextButton> group;
    private final VisTextButton btnBlock;
    private final VisTextButton btnFloor;
    private final VisTextButton btnPaint;
    private final VisTextButton btnErase;
    private final VisTextButton btnSelect;

    protected ToolbarUi(ToolbarPresenter presenter) {
        super(presenter);

        defaults().width(32f).height(32f).pad(2f).padBottom(4f).padTop(4f).align(Align.center);

        VisTextButton btnHideLeftPanel = new VisTextButton(Res.ICON_BOX_CARET_LEFT, Res.ICONS_SMALL, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.toggleLeftPanel();
            }
        });
        new Tooltip.Builder("Hide / show properties panel").target(btnHideLeftPanel).build();
        add(btnHideLeftPanel);

        add().expandX();

        btnBlock = new VisTextButton(Res.ICON_BLOCK, Res.ICONS_SMALL_TOGGLE, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.blockToolSelected();
            }
        });
        new Tooltip.Builder("Draw blocks").target(btnBlock).build();
        add(btnBlock);

        btnFloor = new VisTextButton(Res.ICON_FLOOR, Res.ICONS_SMALL_TOGGLE, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.floorToolSelected();
            }
        });
        new Tooltip.Builder("Draw horizontal quads").target(btnFloor).build();
        add(btnFloor);

        btnPaint = new VisTextButton(Res.ICON_PAINT, Res.ICONS_SMALL_TOGGLE, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.paintToolSelected();
            }
        });
        new Tooltip.Builder("Paint faces").target(btnPaint).build();
        add(btnPaint);

        btnErase = new VisTextButton(Res.ICON_ERASE, Res.ICONS_SMALL_TOGGLE, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.eraseToolSelected();
            }
        });
        new Tooltip.Builder("Erase blocks").target(btnErase).build();
        add(btnErase);

        addSeparator(true).width(2f);

        /*
        VisTextButton btnCreate = new VisTextButton("New Object", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.createObjectClicked();
            }
        });
        add(btnCreate);
        */

        btnSelect = new VisTextButton(Res.ICON_SELECT, Res.ICONS_SMALL_TOGGLE, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.selectToolSelected();
            }
        });
        new Tooltip.Builder("Select objects").target(btnSelect).build();
        add(btnSelect);

        add().expandX();

        VisTextButton btnHideRightPanel = new VisTextButton(Res.ICON_BOX_CARET_RIGHT, Res.ICONS_SMALL, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.toggleRightPanel();
            }
        });
        new Tooltip.Builder("Hide / show panel").target(btnHideRightPanel).build();
        add(btnHideRightPanel);

        group = new ButtonGroup<>(btnBlock, btnFloor, btnPaint, btnErase, btnSelect);
        group.setMinCheckCount(0);
        group.setMaxCheckCount(1);
    }

    public void onBlockToolClicked() {
        group.uncheckAll();
        btnBlock.setChecked(true);
    }

    public void onPaintToolClicked() {
        group.uncheckAll();
        btnPaint.setChecked(true);
    }

    public void onEraseToolClicked() {
        group.uncheckAll();
        btnErase.setChecked(true);
    }

    public void onSelectToolClicked() {
        group.uncheckAll();
        btnSelect.setChecked(true);
    }

    public void onFloorToolClicked() {
        group.uncheckAll();
        btnFloor.setChecked(true);
    }
}
