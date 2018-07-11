package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import me.manabreak.ratio.plugins.camera.EditorCamera;
import me.manabreak.ratio.plugins.tilesets.Tileset;
import me.manabreak.ratio.plugins.toolbar.Tool;
import me.manabreak.ratio.utils.LineUtils;

import java.util.List;
import java.util.Map;

class DrawMode extends LevelEditorMode {
    private final LevelEditorPlugin plugin;

    DrawMode(LevelEditorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int button) {
        int x = plugin.getCellCoord().x;
        int y = plugin.getCellCoord().y;
        int z = plugin.getCellCoord().z;
        int size = plugin.getCellSize();
        int worldSize = plugin.getWorldSize();
        if (button == Input.Buttons.LEFT &&
                x >= 0 && y >= 0 && z >= 0 &&
                x + size <= worldSize &&
                y + size <= worldSize &&
                z + size <= worldSize) {
            if (plugin.isLineToolEngaged()) {
                if (!plugin.isLineStartSet()) {
                    System.out.println("Setting line start to " + plugin.getCellCoord());
                    plugin.setLineToolStartX(x);
                    plugin.setLineToolStartY(y);
                    plugin.setLineToolStartZ(z);
                    plugin.setLineStartSet(true);
                } else {
                    Tileset tileset = plugin.getEditorController().getTilesetManager().getCurrentTileset();
                    if (tileset != null) {
                        List<Integer> line = LineUtils.bresenham(plugin.getLineToolStartX() / size, plugin.getLineToolStartZ() / size, x / size, z / size);
                        TileLayer layer = plugin.getLayerUi().getPresenter().getSelectedLayer();
                        if (plugin.getTool() == Tool.BLOCK) {
                            for (int i = 0; i < line.size(); i += 2) {
                                plugin.execute(new DrawBlockCommand(plugin, layer, plugin.getTilesetPlugin().getCurrentRegion(), tileset, line.get(i) * size, y, line.get(i + 1) * size, size));
                            }
                        } else if (plugin.getTool() == Tool.FLOOR) {
                            for (int i = 0; i < line.size(); i += 2) {
                                plugin.execute(new DrawFloorCommand(plugin, layer, plugin.getTilesetPlugin().getCurrentRegion(), tileset, line.get(i) * size, y, line.get(i + 1) * size, size));
                            }
                        }
                    }
                    plugin.setLineToolEngaged(false);
                }
            } else {
                return handleNormalClick(screenX, screenY);
            }
        }
        return false;
    }

    private boolean handleNormalClick(int screenX, int screenY) {
        Tileset tileset = plugin.getEditorController().getTilesetManager().getCurrentTileset();
        if (tileset != null) {
            switch (plugin.getTool()) {
                case BLOCK:
                    plugin.execute(new DrawBlockCommand(plugin, plugin.getLayerUi().getPresenter().getSelectedLayer(), plugin.getTilesetPlugin().getCurrentRegion(), tileset, plugin.getCellCoord().x, plugin.getCellCoord().y, plugin.getCellCoord().z, plugin.getCellSize()));
                    plugin.mouseMoved(screenX, screenY);
                    break;
                case FLOOR:
                    plugin.execute(new DrawFloorCommand(plugin, plugin.getLayerUi().getPresenter().getSelectedLayer(), plugin.getTilesetPlugin().getCurrentRegion(), tileset, plugin.getCellCoord().x, plugin.getCellCoord().y, plugin.getCellCoord().z, plugin.getCellSize()));
                    break;
                case PAINT:
                    plugin.execute(new PaintCommand(plugin, plugin.getLayerUi().getPresenter().getSelectedLayer(), plugin.getTilesetPlugin().getCurrentRegion(), tileset, plugin.getCellCoord().x, plugin.getCellCoord().y, plugin.getCellCoord().z, plugin.getCellSize(), plugin.getCurrentFace()));
                    break;
                case ERASE:
                    plugin.execute(new EraseBlockCommand(plugin, plugin.getLayerUi().getPresenter().getSelectedLayer(), plugin.getTilesetPlugin().getCurrentRegion(), tileset, plugin.getCellCoord().x, plugin.getCellCoord().y, plugin.getCellCoord().z, plugin.getCellSize()));
                    plugin.mouseMoved(screenX, screenY);
                    break;
                case SELECT:
                    plugin.handleSelectClick(screenX, screenY);
                    break;
                case CREATE:
                    plugin.handleCreateClick(screenX, screenY);
                    break;
            }
        } else {
            switch (plugin.getTool()) {
                case SELECT:
                    plugin.handleSelectClick(screenX, screenY);
                    break;
                case CREATE:
                    plugin.handleCreateClick(screenX, screenY);
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        if (plugin.getTool() == Tool.NONE) return false;

        if (plugin.getTool() == Tool.CREATE && plugin.getCreateClicks() == 2) {
            plugin.getCreateEnd().y = ((plugin.getCreateStartY() - y) + 1);
            System.out.println("Height: " + plugin.getCreateEnd().y);
            return false;
        }

        Camera cam = EditorCamera.main.getCamera();
        Ray ray = cam.getPickRay(x, y);

        boolean hit = false;

        if (plugin.getTool().category() != Tool.Category.OBJECT) {
            Tileset tileset = plugin.getEditorController().getTilesetManager().getCurrentTileset();
            if (tileset != null) {
                if (plugin.getMeshes().size() > 0) {
                    for (Map.Entry<TileLayer, Map<Tileset, Mesh>> layerMapEntry : plugin.getMeshes().entrySet()) {
                        if (!layerMapEntry.getKey().isVisible()) continue;

                        for (Mesh mesh : layerMapEntry.getValue().values()) {
                            if (mesh.getNumVertices() == 0) continue;

                            float[] vertices = new float[mesh.getNumVertices() * 8];
                            short[] indices = new short[mesh.getNumIndices()];
                            mesh.getVertices(vertices);
                            mesh.getIndices(indices);
                            int vertexSize = 8;
                            float minDist = Float.MAX_VALUE;

                            if (plugin.findCellByFace(ray, vertices, indices, vertexSize, minDist, hit)) {
                                plugin.extractCellFromFace();
                                plugin.adjustCellForTool();
                                hit = true;
                            }
                        }
                    }
                }
            }
        }

        if (!hit && plugin.getTool() != Tool.PAINT && Intersector.intersectRayPlane(ray, new Plane(Vector3.Y, -plugin.getXzPlane()), plugin.getTmp0())) {
            plugin.findCellOnGrid(cam);
        }

        return false;
    }
}