package me.manabreak.ratio.plugins.tilesets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import me.manabreak.ratio.utils.Action1;

public class TilesetLoaderDialog {

    public TilesetLoaderDialog(Stage stage, Action1<FileHandle> callback) {
        VisWindow root = new VisWindow("New Tileset", true);
        root.setKeepWithinParent(true);
        root.addCloseButton();
        root.setSize(500f, 300f);
        root.setMovable(true);
        root.centerWindow();
        root.setModal(true);
        root.closeOnEscape();
        stage.addActor(root);

        VisTable pathTable = new VisTable(true);

        VisTextField tfPath = new VisTextField("");
        tfPath.setTextFieldListener((tf, c) -> {
            FileHandle fh = Gdx.files.internal(tf.getText());
            tfPath.setInputValid(fh.exists() && !fh.isDirectory());
        });
        pathTable.add(tfPath).pad(4f).growX();

        VisTextButton btnBrowse = new VisTextButton("Browse", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                FileChooser fileChooser = new FileChooser(FileChooser.Mode.OPEN);
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
                FileTypeFilter filter = new FileTypeFilter(false);
                filter.addRule("PNG", "png");
                fileChooser.setFileTypeFilter(filter);
                fileChooser.setDirectory(Gdx.files.internal("assets/tilesets"));

                fileChooser.setListener(new FileChooserAdapter() {
                    @Override
                    public void selected(Array<FileHandle> files) {
                        tfPath.setText(files.get(0).path());
                    }
                });

                stage.addActor(fileChooser);
            }
        });
        pathTable.add(btnBrowse).pad(4f);
        root.add(pathTable).growX().row();

        VisTable mainButtonTable = new VisTable(true);
        VisTextButton btnCancel = new VisTextButton("Cancel", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                root.remove();
            }
        });
        mainButtonTable.add(btnCancel).pad(4f);

        VisTextButton btnOk = new VisTextButton("OK", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                callback.call(Gdx.files.absolute(tfPath.getText()));
                root.remove();
            }
        });
        mainButtonTable.add(btnOk).pad(4f);
        root.add(mainButtonTable).grow();
    }
}
