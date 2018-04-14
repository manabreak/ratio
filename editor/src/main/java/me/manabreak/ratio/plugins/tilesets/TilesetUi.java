package me.manabreak.ratio.plugins.tilesets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import me.manabreak.ratio.common.mvp.MvpView;
import me.manabreak.ratio.editor.TilesetTab;
import me.manabreak.ratio.editor.TilesetTabContainer;

public class TilesetUi extends MvpView<TilesetPresenter> {
    private final Drawable bg = VisUI.getSkin().getDrawable("window-bg");
    private final Drawable selection = VisUI.getSkin().getDrawable("list-selection");

    private final VisTable toolRegion;
    private final TilesetTabContainer tabbedContainer;
    private TextureRegion currentRegion;
    private Table selectedRegionActor;

    public TilesetUi(TilesetPresenter presenter) {
        super(presenter);

        top();

        addSeparator();
        add("Tilesets").left().padLeft(4f).row();

        toolRegion = new VisTable();
        add(toolRegion).growX().padTop(8f);

        row();

        addSeparator();

        tabbedContainer = new TilesetTabContainer(presenter);
        add(tabbedContainer).growX().maxHeight(200f);

        VisTextButton btnLoad = new VisTextButton("Load", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new TilesetLoaderDialog(getStage(), presenter::loadTileset);
            }
        });
        toolRegion.add(btnLoad).pad(4f);

        VisTextButton btnCreatePalette = new VisTextButton("Create Palette", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                presenter.createPalette();
            }
        });
        toolRegion.add(btnCreatePalette).pad(4f);

        toolRegion.row().pad(2f);

        final IntSpinnerModel tileWidthModel = new IntSpinnerModel(16, 1, 32, 1);
        Spinner tileWidthSpinner = new Spinner("Tile Width", tileWidthModel);
        tileWidthSpinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int tileWidth = tileWidthModel.getValue();
                presenter.tileWidthChanged(tileWidth);
            }
        });
        toolRegion.add(tileWidthSpinner).pad(4f);

        final IntSpinnerModel tileHeightModel = new IntSpinnerModel(16, 1, 32, 1);
        Spinner tileHeightSpinner = new Spinner("Tile Height", tileHeightModel);
        tileHeightSpinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int tileHeight = tileHeightModel.getValue();
                presenter.tileHeightChanged(tileHeight);
            }
        });
        toolRegion.add(tileHeightSpinner).pad(4f);

        pack();
    }

    public void levelLoaded() {

        presenter.levelLoaded();
    }

    void createTab(Tileset tileset) {
        VisTable root = new VisTable();
        TilesetTab tab = new TilesetTab(tileset, root);
        tabbedContainer.addTab(tab);
    }

    public void createPicker(Tileset tileset, int tileWidth, int tileHeight) {
        Table root = tabbedContainer.getTabs().getActiveTab().getContentTable();
        root.clear();

        boolean palette = tileset instanceof PaletteTileset;

        Table container = new Table();
        ScrollPane pane = new ScrollPane(container);
        pane.setOverscroll(false, false);
        pane.setScrollingDisabled(false, false);

        TextureRegion[][] regions = TextureRegion.split(tileset.getTexture(), tileWidth, tileHeight);
        for (TextureRegion[] row : regions) {
            for (TextureRegion region : row) {
                Table a = new Table();
                a.setBackground(bg);
                a.add(new VisImage(region)).grow();
                a.addListener(new ClickListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        System.out.println("Clicked region " + region.getRegionX() + ", " + region.getRegionY() + ", " + region.getRegionWidth() + ", " + region.getRegionHeight());
                        if (selectedRegionActor != null) {
                            selectedRegionActor.setBackground(bg);
                        }
                        selectedRegionActor = a;
                        a.setBackground(selection);

                        presenter.selectRegion(region);
                        currentRegion = region;

                        if (button == Input.Buttons.RIGHT && palette) {
                            showColorPicker((PaletteTileset) tileset, a, region);
                        }

                        if (palette) {
                            PaletteTileset pt = (PaletteTileset) tileset;
                            Color color = pt.getColorAt(region.getRegionX(), region.getRegionY());
                            System.out.println("Selected color: " + color.toString());
                        }
                        return true;
                    }
                });
                float ratio = region.getRegionWidth() / region.getRegionHeight();
                container.add(a).width(48f).height(48f / ratio).pad(1f);
            }
            container.row();
        }


        root.add(pane).expand();
        currentRegion = regions[0][0];
        presenter.selectRegion(regions[0][0]);
    }

    private void showColorPicker(PaletteTileset tileset, Table a, TextureRegion region) {
        ColorPicker cp = new ColorPicker("Choose Color");
        cp.setAllowAlphaEdit(false);
        Color color = tileset.getColorAt(region.getRegionX(), region.getRegionY());
        cp.setColor(color);
        ColorPickerListener listener = new ColorPickerListener() {
            @Override
            public void canceled(Color oldColor) {
                tileset.draw(region.getRegionX(), region.getRegionY(), oldColor);
            }

            @Override
            public void changed(Color newColor) {
                tileset.draw(region.getRegionX(), region.getRegionY(), newColor);
            }

            @Override
            public void reset(Color previousColor, Color newColor) {
                tileset.draw(region.getRegionX(), region.getRegionY(), newColor);
            }

            @Override
            public void finished(Color newColor) {
                tileset.draw(region.getRegionX(), region.getRegionY(), newColor);
            }
        };
        cp.setListener(listener);
        getStage().addActor(cp);
        cp.setCenterOnAdd(false);
        cp.setPosition(a.getX() + 24f, 100f);
    }

    public TextureRegion getCurrentRegion() {
        return currentRegion;
    }

    public void promptPaletteName() {
        Dialogs.InputDialog dialog = new Dialogs.InputDialog("New Palette", "Name", true, input -> !input.isEmpty(), new InputDialogAdapter() {
            @Override
            public void finished(String input) {
                presenter.createPalette(input);
            }
        });
        getStage().addActor(dialog);
    }
}
