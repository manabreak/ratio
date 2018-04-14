package me.manabreak.ratio.editor;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisTable;
import me.manabreak.ratio.plugins.exporters.Exporter;
import me.manabreak.ratio.plugins.importers.Importer;

import java.util.List;

public interface EditorView extends InputProcessor {

    void setToolView(VisTable content);

    void addLeftPanelTab(String title, VisTable content);

    void showSaveAsDialog(List<Exporter> exporters);

    void showOpenDialog(List<Importer> importers);

    void registerToolbar(Table table);

    void toggleLeftPanel();

    void toggleRightPanel();

    void setRightPanelTop(VisTable content);

    void setRightPanelBottom(VisTable content);
}
