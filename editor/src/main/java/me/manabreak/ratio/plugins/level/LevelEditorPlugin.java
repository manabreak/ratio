package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import me.manabreak.ratio.editor.EditorPlugin;
import me.manabreak.ratio.editor.LoopListener;
import me.manabreak.ratio.plugins.camera.EditorCamera;
import me.manabreak.ratio.plugins.objects.GameObject;
import me.manabreak.ratio.plugins.objects.ObjectEditorPlugin;
import me.manabreak.ratio.plugins.scene.EditorGrid;
import me.manabreak.ratio.plugins.tilesets.Tileset;
import me.manabreak.ratio.plugins.tilesets.TilesetPlugin;
import me.manabreak.ratio.plugins.toolbar.Tool;
import me.manabreak.ratio.utils.LineUtils;

import java.util.*;

/**
 * Host plugin for all the level-editing (octree) stuff
 */
public class LevelEditorPlugin extends EditorPlugin implements LoopListener {

    /**
     * Maximum number of commands stored for undo / redo
     */
    private static final int MAX_UNDO = 50;

    private static final float NORM = 0.99f;
    private static final Color GREEN = new Color(0f, 1f, 0f, 0.5f);
    private static final Color RED = new Color(1f, 0f, 0f, 0.5f);

    private final LevelShader levelShader;
    private final WireframeRenderer wireframeRenderer;
    private final ObjectRenderer objectRenderer;
    private final ToolRenderer renderer;
    private final Vector3 tmp0 = new Vector3();
    private final Vector3 tmp1 = new Vector3();
    private final Vector3 tmp2 = new Vector3();
    private final Vector3 tmp3 = new Vector3();
    private final Vector3 best = new Vector3();
    private final Vector3 bestNormal = new Vector3();
    private final Coord cellCoord = new Coord();
    private final LevelMeshCreator creator = new LevelMeshCreator();
    private final Deque<Command> commands = new ArrayDeque<>();
    private final Deque<Command> redoCommands = new ArrayDeque<>();
    private Vector3 lightDirection = new Vector3(-1f, -2f, -3f).nor();
    private float ambientIntensity = 0.2f;
    private EditorGrid grid;
    private Level level = new Level();
    private int xzPlane = 1;
    private int cellSize = 16;
    private float cf = cellSize / 16f;
    private Tool tool = Tool.NONE;
    private Face currentFace = Face.FRONT;
    private boolean wireframe;
    private Map<TileLayer, Map<Tileset, Mesh>> meshes = new HashMap<>();
    private boolean lineToolEngaged;
    private boolean lineStartSet;
    private int lineToolStartX;
    private int lineToolStartY;
    private int lineToolStartZ;

    private LayerUi layerUi;
    private TilesetPlugin tilesetPlugin;
    private ToolView toolView;
    private boolean highlightSelectedLayer = true;

    public LevelEditorPlugin(LevelShader levelShader,
                             WireframeRenderer wireframeRenderer,
                             ObjectRenderer objectRenderer,
                             ToolRenderer toolRenderer) {
        this.levelShader = levelShader;
        this.wireframeRenderer = wireframeRenderer;
        this.objectRenderer = objectRenderer;
        this.renderer = toolRenderer;
    }

    @Override
    public void initialize() {
        if (!levelShader.isCompiled()) {
            System.out.println("ERROR: " + levelShader.getLog());
        }
        editorController.addLoopListener(this);
        editorController.registerInputProcessor(this);

        final LayerPresenter presenter = new LayerPresenter(level);
        layerUi = new LayerUi(presenter);
        editorView.setRightPanelTop(layerUi);

        final ToolPresenter toolPresenter = new ToolPresenter();
        toolView = new ToolView(toolPresenter);
        editorView.setToolView(toolView);
    }

    @Override
    public void postInitialize() {
        grid = getPlugin(EditorGrid.class);
        tilesetPlugin = getPlugin(TilesetPlugin.class);
    }

    @Override
    public void onUpdate(float dt) {
        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glCullFace(GL20.GL_BACK);
        renderMeshes();
        renderObjects();

        switch (tool) {
            case BLOCK:
                if (lineToolEngaged && lineStartSet) {
                    List<Integer> line = LineUtils.bresenham(lineToolStartX / cellSize, lineToolStartZ / cellSize, cellCoord.x / cellSize, cellCoord.z / cellSize);
                    Coord c = new Coord();
                    for (int i = 0; i < line.size(); i += 2) {
                        c.x = line.get(i) * cellSize;
                        c.y = cellCoord.y;
                        c.z = line.get(i + 1) * cellSize;
                        renderer.renderCube(EditorCamera.main.getCamera(), c, cellSize, GREEN);
                    }
                } else {
                    renderer.renderCube(EditorCamera.main.getCamera(), cellCoord, cellSize, GREEN);
                }
                break;
            case FLOOR:
                if (lineToolEngaged && lineStartSet) {
                    List<Integer> line = LineUtils.bresenham(lineToolStartX / cellSize, lineToolStartZ / cellSize, cellCoord.x / cellSize, cellCoord.z / cellSize);
                    Coord c = new Coord();
                    for (int i = 0; i < line.size(); i += 2) {
                        c.x = line.get(i) * cellSize;
                        c.y = cellCoord.y;
                        c.z = line.get(i + 1) * cellSize;
                        renderer.renderFace(EditorCamera.main.getCamera(), c, cellSize, Face.BOTTOM);
                    }
                } else {
                    renderer.renderFace(EditorCamera.main.getCamera(), cellCoord, cellSize, Face.BOTTOM);
                }
                break;
            case PAINT:
                renderer.renderFace(EditorCamera.main.getCamera(), cellCoord, cellSize, currentFace);
                break;
            case ERASE:
                renderer.renderCube(EditorCamera.main.getCamera(), cellCoord, cellSize, RED);
                break;
        }
    }

    private void renderMeshes() {
        Gdx.gl20.glDepthMask(true);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDisable(GL20.GL_BLEND);

        if (wireframe) {
            for (Map<Tileset, Mesh> meshMap : meshes.values()) {
                wireframeRenderer.render(EditorCamera.main.getCamera(), meshMap.values());
            }
        } else {
            levelShader.begin();
            levelShader.setUniformMatrix("u_projTrans", EditorCamera.main.getCamera().combined);
            levelShader.setUniformi("u_texture", 0);
            levelShader.setUniformf("u_lightDirection", lightDirection);
            levelShader.setUniformf("u_ambientIntensity", ambientIntensity);

            if (!highlightSelectedLayer) {
                levelShader.setUniformf("u_level", 1f);
            }

            for (TileLayer layer : level.getLayers()) {
                if (!layer.isVisible()) continue;

                if (highlightSelectedLayer) {
                    if (layerUi.getPresenter().getSelectedLayer() == layer) {
                        levelShader.setUniformf("u_level", 1f);
                    } else {
                        levelShader.setUniformf("u_level", 0.3f);
                    }
                }

                final Map<Tileset, Mesh> map = meshes.get(layer);
                if (map != null) {
                    for (Map.Entry<Tileset, Mesh> entry : map.entrySet()) {
                        entry.getKey().getTexture().bind(0);
                        entry.getValue().render(levelShader, GL20.GL_TRIANGLES);
                    }
                }
            }
            levelShader.end();
        }

        Gdx.gl20.glEnable(GL20.GL_BLEND);
    }

    private void renderObjects() {
        Gdx.gl20.glDepthMask(true);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        objectRenderer.render(EditorCamera.main.getCamera(), getPlugin(ObjectEditorPlugin.class).getPresenter().getObjects());
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.Z:
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                        System.out.println("Redo!");
                        redo();
                    } else {
                        System.out.println("Undo!");
                        undo();
                    }
                }
                break;
            case Input.Keys.F1:
                wireframe = !wireframe;
                return true;
            case Input.Keys.PLUS:
                xzPlane++;
                grid.raiseBy(cf);
                return true;
            case Input.Keys.MINUS:
                xzPlane--;
                grid.lowerBy(cf);
                return true;
        }
        return super.keyUp(keycode);
    }

    public void engageLineTool() {
        lineToolEngaged = true;
        lineStartSet = false;
    }

    public void unengageLineTool() {
        lineToolEngaged = false;
        lineStartSet = false;
    }

    public void execute(Command command) {
        command.execute();
        commands.push(command);
        redoCommands.clear();
        if (commands.size() > MAX_UNDO) {
            commands.removeFirst();
        }
    }

    public void undo() {
        System.out.println("Undo 1");
        if (commands.size() > 0) {
            System.out.println("Undo 2");
            Command command = commands.pop();
            command.undo();
            redoCommands.push(command);
        }
    }

    public void redo() {
        System.out.println("Redo 1");
        if (redoCommands.size() > 0) {
            System.out.println("Redo 2");
            Command command = redoCommands.pop();
            command.execute();
            commands.push(command);
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && cellCoord.x >= 0 && cellCoord.y >= 0 && cellCoord.z >= 0) {
            if (lineToolEngaged) {
                if (!lineStartSet) {
                    System.out.println("Setting line start to " + cellCoord);
                    lineToolStartX = cellCoord.x;
                    lineToolStartY = cellCoord.y;
                    lineToolStartZ = cellCoord.z;
                    lineStartSet = true;
                } else {
                    Tileset tileset = editorController.getTilesetManager().getCurrentTileset();
                    if (tileset != null) {
                        List<Integer> line = LineUtils.bresenham(lineToolStartX / cellSize, lineToolStartZ / cellSize, cellCoord.x / cellSize, cellCoord.z / cellSize);
                        if (tool == Tool.BLOCK) {
                            for (int i = 0; i < line.size(); i += 2) {
                                execute(new DrawBlockCommand(this, layerUi.getPresenter().getSelectedLayer(), tilesetPlugin.getCurrentRegion(), tileset, line.get(i) * cellSize, cellCoord.y, line.get(i + 1) * cellSize, cellSize));
                            }
                        } else if (tool == Tool.FLOOR) {
                            for (int i = 0; i < line.size(); i += 2) {
                                // FIXME
                                // execute(new DrawFloorCommand(this, level, currentRegion, tileset, line.get(i) * cellSize, cellCoord.y, line.get(i + 1) * cellSize, cellSize));
                            }
                        }
                    }
                    lineToolEngaged = false;
                }
            } else {
                return handleNormalClick(screenX, screenY);
            }
        }
        return false;
    }

    private boolean handleNormalClick(int screenX, int screenY) {
        Tileset tileset = editorController.getTilesetManager().getCurrentTileset();
        if (tileset != null) {
            switch (tool) {
                case BLOCK:
                    execute(new DrawBlockCommand(this, layerUi.getPresenter().getSelectedLayer(), tilesetPlugin.getCurrentRegion(), tileset, cellCoord.x, cellCoord.y, cellCoord.z, cellSize));
                    break;
                case FLOOR:
                    execute(new DrawFloorCommand(this, layerUi.getPresenter().getSelectedLayer(), tilesetPlugin.getCurrentRegion(), tileset, cellCoord.x, cellCoord.y, cellCoord.z, cellSize));
                    break;
                case PAINT:
                    execute(new PaintCommand(this, layerUi.getPresenter().getSelectedLayer(), tilesetPlugin.getCurrentRegion(), tileset, cellCoord.x, cellCoord.y, cellCoord.z, cellSize, currentFace));
                    break;
                case ERASE:
                    execute(new EraseBlockCommand(this, layerUi.getPresenter().getSelectedLayer(), tilesetPlugin.getCurrentRegion(), tileset, cellCoord.x, cellCoord.y, cellCoord.z, cellSize));
                    break;
                case SELECT:
                    handleSelectClick(screenX, screenY);
                    break;
            }
        } else {
            switch (tool) {
                case SELECT:
                    handleSelectClick(screenX, screenY);
                    break;
            }
        }
        mouseMoved(screenX, screenY);
        return true;
    }

    private void handleSelectClick(int x, int y) {
        getPlugin(ObjectEditorPlugin.class).selectAt(x, y);
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        if (tool == Tool.NONE) return false;

        Camera cam = EditorCamera.main.getCamera();
        Ray ray = cam.getPickRay(x, y);

        Tileset tileset = editorController.getTilesetManager().getCurrentTileset();
        if (tileset == null) return false;

        boolean hit = false;
        if (meshes.size() > 0) {
            for (Map.Entry<TileLayer, Map<Tileset, Mesh>> layerMapEntry : meshes.entrySet()) {
                if (!layerMapEntry.getKey().isVisible()) continue;

                for (Mesh mesh : layerMapEntry.getValue().values()) {
                    if (mesh.getNumVertices() == 0) continue;

                    float[] vertices = new float[mesh.getNumVertices() * 8];
                    short[] indices = new short[mesh.getNumIndices()];
                    mesh.getVertices(vertices);
                    mesh.getIndices(indices);
                    int vertexSize = 8;
                    float minDist = Float.MAX_VALUE;

                    if (findCellByFace(ray, vertices, indices, vertexSize, minDist, hit)) {
                        extractCellFromFace();
                        adjustCellForTool();
                        hit = true;
                    }
                }
            }
        }

        if (!hit && tool != Tool.PAINT && Intersector.intersectRayPlane(ray, new Plane(Vector3.Y, -xzPlane), tmp0)) {
            findCellOnGrid(cam);
        }

        return false;
    }

    private void extractCellFromFace() {
        float v = cf / 2f - 0.0001f;
        cellCoord.x = Math.round((best.x - v) / cf) * cellSize;
        cellCoord.y = Math.round((best.y - v) / cf) * cellSize;
        cellCoord.z = Math.round((best.z - v) / cf) * cellSize;
        if (bestNormal.x > NORM) {
            currentFace = Face.RIGHT;
            cellCoord.x -= cellSize;
        } else if (bestNormal.x < -NORM) {
            currentFace = Face.LEFT;
        } else if (bestNormal.y > NORM) {
            currentFace = Face.TOP;
            cellCoord.y -= cellSize;
        } else if (bestNormal.z > NORM) {
            currentFace = Face.FRONT;
            cellCoord.z -= cellSize;
        } else if (bestNormal.z < -NORM) {
            currentFace = Face.BACK;
        }
    }

    /**
     * Adjusts the newly found cell as per the current tool.
     * The paint tool needs to use the cell as-is, whereas
     * drawing and erasing tool needs to take the next cell along
     * the targeted face's normal.
     */
    private void adjustCellForTool() {
        switch (tool) {
            case BLOCK:
                switch (currentFace) {
                    case FRONT:
                        cellCoord.z += cellSize;
                        break;
                    case BACK:
                        cellCoord.z -= cellSize;
                        break;
                    case LEFT:
                        cellCoord.x -= cellSize;
                        break;
                    case RIGHT:
                        cellCoord.x += cellSize;
                        break;
                    case TOP:
                        cellCoord.y += cellSize;
                        break;
                }
                break;
        }
    }

    private void findCellOnGrid(Camera cam) {
        cellCoord.x = Math.round((tmp0.x - cf / 2f - 0.0001f) / cf) * cellSize;
        cellCoord.y = Math.round((tmp0.y - cf / 2f) / cf) * cellSize;
        cellCoord.z = Math.round((tmp0.z - cf / 2f - 0.0001f) / cf) * cellSize;

        if (tool == Tool.FLOOR) cellCoord.y--;

        float dirx = cam.direction.x;
        float diry = cam.direction.z;

        if (Math.abs(dirx) > Math.abs(diry)) {
            if (dirx > 0f) {
                currentFace = Face.LEFT;
            } else {
                currentFace = Face.RIGHT;
            }
        } else {
            if (diry > 0f) {
                currentFace = Face.BACK;
            } else {
                currentFace = Face.FRONT;
            }
        }
    }

    private boolean findCellByFace(Ray ray, float[] vertices, short[] indices, int vertexSize, float minDist, boolean hit) {
        for (int i = 0; i < indices.length; i += 3) {
            int i1 = indices[i] * vertexSize;
            int i2 = indices[i + 1] * vertexSize;
            int i3 = indices[i + 2] * vertexSize;

            boolean result = Intersector.intersectRayTriangle(ray, tmp1.set(vertices[i1], vertices[i1 + 1], vertices[i1 + 2]),
                    tmp2.set(vertices[i2], vertices[i2 + 1], vertices[i2 + 2]),
                    tmp3.set(vertices[i3], vertices[i3 + 1], vertices[i3 + 2]), tmp0);

            if (result) {
                float dist = ray.origin.dst2(tmp0);
                if (dist < minDist) {
                    minDist = dist;
                    best.set(tmp0);
                    bestNormal.set(vertices[i1 + 5], vertices[i1 + 6], vertices[i1 + 7]);
                    hit = true;
                }
            }
        }
        return hit;
    }

    public void setToolSize(int size) {
        this.cellSize = size;
        this.cf = cellSize / 16f;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
        meshes = creator.create(level);
        // FIXME
        tilesetPlugin.levelLoaded(level);
        layerUi.getPresenter().levelLoaded(level);
        commands.clear();
        redoCommands.clear();
    }

    public void setLightDirection(Vector3 d) {
        this.lightDirection = d.nor();
    }

    public void setAmbientIntensity(float f) {
        this.ambientIntensity = MathUtils.clamp(f, 0f, 1f);
    }

    public void recreateMeshes() {
        meshes = creator.create(level);
    }

    public void setCellCoord(int x, int y, int z) {
        this.cellCoord.x = x;
        this.cellCoord.y = y;
        this.cellCoord.z = z;
    }

    public void setTool(Tool tool) {
        if (this.tool == tool) {
            this.tool = Tool.NONE;
        } else {
            this.tool = tool;
        }
    }

    public ObjectRenderer getObjectRenderer() {
        return objectRenderer;
    }

    public void toggleLayerHighlighting() {
        this.highlightSelectedLayer = !this.highlightSelectedLayer;
    }
}
