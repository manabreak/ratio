package me.manabreak.ratio.plugins.tilesets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class TilesetFileAdapter extends ArrayAdapter<FileHandle, VisTable> {

    private final Drawable bg = VisUI.getSkin().getDrawable("window-bg");
    private final Drawable selection = VisUI.getSkin().getDrawable("list-selection");

    public TilesetFileAdapter(Array<FileHandle> array) {
        super(array);
        setSelectionMode(SelectionMode.SINGLE);
    }

    @Override
    protected VisTable createView(FileHandle item) {
        VisTable table = new VisTable(true);
        table.left().padLeft(4f);
        VisLabel name = new VisLabel(item.nameWithoutExtension());
        table.add(name).growX();
        return table;
    }

    @Override
    protected void selectView(VisTable view) {
        view.setBackground(selection);
    }

    @Override
    protected void deselectView(VisTable view) {
        view.setBackground(bg);
    }
}
