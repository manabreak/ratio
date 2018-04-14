package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.utils.Array;
import io.reactivex.subjects.PublishSubject;
import me.manabreak.ratio.common.mvp.MvpPresenter;

public class LayerPresenter extends MvpPresenter<LayerUi> {

    private final PublishSubject<TileLayer> layerCreatedSubject = PublishSubject.create();
    private final PublishSubject<TileLayer> layerSelectedSubject = PublishSubject.create();
    private final PublishSubject<TileLayer> layerRemovedSubject = PublishSubject.create();
    private LayerListAdapter adapter;
    private Level level;
    private TileLayer selectedLayer;

    public LayerPresenter(Level level) {
        this.level = level;
        this.adapter = new LayerListAdapter(this, level.getLayers());
    }

    public void viewCreated() {
        view.createListView(adapter);
    }

    public void createLayer() {
        TileLayer layer = this.level.createLayer("Layer");
        adapter.itemsChanged();
        adapter.getSelectionManager().select(layer);
        layerCreatedSubject.onNext(layer);
        layerClicked(layer);
    }

    public void layerClicked(TileLayer layer) {
        System.out.println("Clicked layer " + layer.getName());
        this.selectedLayer = layer;
        layerSelectedSubject.onNext(layer);
    }

    public PublishSubject<TileLayer> getLayerSelectedSubject() {
        return layerSelectedSubject;
    }

    public PublishSubject<TileLayer> getLayerRemovedSubject() {
        return layerRemovedSubject;
    }

    public PublishSubject<TileLayer> getLayerCreatedSubject() {
        return layerCreatedSubject;
    }

    public void moveLayerUp() {
        final Array<TileLayer> selection = adapter.getSelection();
        if (selection.size == 0) return;

        final TileLayer layer = selection.get(0);
        int i = level.getLayers().indexOf(layer);
        if (i == 0) return;
        level.deleteLayer(layer);
        i--;
        level.addLayer(i, layer);
        adapter.itemsChanged();
        adapter.getSelectionManager().select(layer);
    }

    public void moveLayerDown() {
        final Array<TileLayer> selection = adapter.getSelection();
        if (selection.size == 0) return;

        final TileLayer layer = selection.get(0);
        int i = level.getLayers().indexOf(layer);
        if (i == level.getLayers().size() - 1) return;
        level.deleteLayer(layer);
        i++;
        level.addLayer(i, layer);
        adapter.itemsChanged();
        adapter.getSelectionManager().select(layer);
    }

    public void deleteLayer() {
        final Array<TileLayer> selection = adapter.getSelection();
        if (selection.size == 0) return;

        final TileLayer layer = selection.get(0);
        final int i = level.getLayers().indexOf(layer);
        level.deleteLayer(layer);
        adapter.itemsChanged();

        if (level.getLayers().size() > 0) {
            adapter.getSelectionManager().select(level.getLayers().get(Math.max(Math.min(i, level.getLayers().size() - 1), 0)));
        }
    }

    public TileLayer getSelectedLayer() {
        return selectedLayer;
    }

    public void levelLoaded(Level level) {
        this.level = level;
        this.adapter = new LayerListAdapter(this, level.getLayers());
        view.createListView(adapter);
    }
}
