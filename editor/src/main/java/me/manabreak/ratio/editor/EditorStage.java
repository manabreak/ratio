package me.manabreak.ratio.editor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import me.manabreak.ratio.editor.menu.EditorMenuBar;
import me.manabreak.ratio.plugins.exporters.Exporter;
import me.manabreak.ratio.plugins.importers.Importer;

import java.util.List;

public class EditorStage extends Stage implements EditorView {

    private final VisTable root;
    private final VisTable topPanel;
    private final TabbedContainer leftTabs;
    private final VisTable rightPanel;
    private final VisTable rightPanelTop, rightPanelBottom;
    // private final VisTable bottomPanel;
    private final EditorController controller;
    private final VisTable leftPanel;
    private final VisTable toolContainer;

    EditorStage(EditorController controller) {
        super(new ScreenViewport());
        this.controller = controller;
        controller.setView(this);

        root = new VisTable();
        root.setFillParent(true);
        addActor(root);

        MenuBar menuBar = new EditorMenuBar(this, controller);
        root.add(menuBar.getTable()).growX();

        root.row();

        VisTable mainContent = new VisTable();
        root.add(mainContent).grow();

        topPanel = new VisTable();
        mainContent.add(topPanel).colspan(3).growX();

        mainContent.row();

        VisTable editorCameraTable = new VisTable(true);
        editorCameraTable.add("Editor Camera tab");

        leftPanel = new VisTable();
        toolContainer = new VisTable();
        leftTabs = new TabbedContainer();
        mainContent.add(leftPanel).top().growY().width(300f);
        leftPanel.add(toolContainer).growX().padBottom(16f);
        leftPanel.setBackground("default-table");
        leftPanel.row();
        leftPanel.add(leftTabs).grow().width(300f);

        VisTable centerPanel = new VisTable(true);
        mainContent.add(centerPanel).grow();

        rightPanel = new VisTable();
        mainContent.add(rightPanel).growY().width(300f);
        rightPanelTop = new VisTable();
        rightPanelBottom = new VisTable();
        rightPanel.add(rightPanelTop).grow().uniform();
        rightPanel.row();
        rightPanel.add(rightPanelBottom).grow().uniform();
        mainContent.row();

        // bottomPanel = new VisTable();
        // mainContent.add(bottomPanel).colspan(3).growX().height(32f);

        root.pack();
    }

    @Override
    public void setToolView(VisTable content) {
        toolContainer.add(content).grow();
    }

    @Override
    public void addLeftPanelTab(String tabTitle, VisTable content) {
        EditorTab tab = new EditorTab(tabTitle, content);
        leftTabs.addTab(tab);
    }

    @Override
    public void showSaveAsDialog(List<Exporter> exporters) {
        FileChooser fileChooser = new FileChooser(FileChooser.Mode.SAVE);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
        FileTypeFilter filter = new FileTypeFilter(false);
        for (Exporter exporter : exporters) {
            filter.addRule(exporter.getFormatName(), exporter.getFileExtension());
        }
        fileChooser.setFileTypeFilter(filter);

        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> files) {
                controller.onSaveFileSelected(files.get(0));
            }
        });

        addActor(fileChooser);
    }

    @Override
    public void showOpenDialog(List<Importer> importers) {
        FileChooser fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
        FileTypeFilter filter = new FileTypeFilter(false);
        for (Importer importer : importers) {
            filter.addRule(importer.getFormatName(), importer.getFileExtension());
        }
        fileChooser.setFileTypeFilter(filter);

        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> files) {
                controller.onOpenFileSelected(files.get(0));
            }
        });

        addActor(fileChooser);
    }

    @Override
    public void registerToolbar(Table table) {
        topPanel.add(table).center().growX();
    }

    @Override
    public void toggleLeftPanel() {
        leftPanel.setVisible(!leftPanel.isVisible());
    }

    @Override
    public void toggleRightPanel() {
        rightPanel.setVisible(!rightPanel.isVisible());
    }

    @Override
    public void setRightPanelTop(VisTable content) {
        rightPanelTop.add(content).grow();
    }

    @Override
    public void setRightPanelBottom(VisTable content) {
        rightPanelBottom.add(content).grow();
    }


    void resize(int width, int height) {
        super.getViewport().update(width, height, true);
        root.setPosition(0f, 0f);
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (keyCode == Input.Keys.ENTER || keyCode == Input.Keys.ESCAPE) {
            if (getKeyboardFocus() != null) {
                setKeyboardFocus(null);
            }
        }
        return super.keyDown(keyCode);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        final Vector2 coords = screenToStageCoordinates(new Vector2(screenX, screenY));
        final Actor actor = hit(coords.x, coords.y, true);
        setScrollFocus(actor);
        return super.mouseMoved(screenX, screenY);
    }
}
