package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import me.manabreak.ratio.editor.EditorController;
import me.manabreak.ratio.editor.EditorPlugin;
import me.manabreak.ratio.editor.LoopListener;
import me.manabreak.ratio.plugins.camera.EditorCamera;
import me.manabreak.ratio.plugins.objects.ObjectEditorPlugin;
import me.manabreak.ratio.plugins.properties.LayerPropertiesPlugin;
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
    private LevelEditorMode editorMode = new DrawMode(this);
    private Vector3 lightDirection = new Vector3(-1f, -2f, -3f).nor();
    private float ambientIntensity = 0.2f;
    private EditorGrid grid;
    private Level level = new Level();
    private int xzPlane = 1;
    private int cellSize = 1;
    private float cf = 1f;
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
    private int createClicks = 0;
    private Coord createStart, createEnd;
    private int createStartY = 0;
    private int worldSize = Integer.MAX_VALUE;

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
        toolPresenter.getToolSizeObservable().subscribe(size -> {
            this.cellSize = size;
            this.cf = (float) size;
        });
        toolView = new ToolView(toolPresenter);
        editorView.setToolView(toolView);
    }

    @Override
    public void postInitialize() {
        grid = getPlugin(EditorGrid.class);
        tilesetPlugin = getPlugin(TilesetPlugin.class);

        LayerPropertiesPlugin layerPropertiesPlugin = getPlugin(LayerPropertiesPlugin.class);
        layerUi.getPresenter().getLayerSelectedSubject()
                .subscribe(layer -> layerPropertiesPlugin.getPresenter().layerSelected(layer));
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
            case CREATE:
                switch (createClicks) {
                    case 0:
                        renderer.renderWireCube(EditorCamera.main.getCamera(), cellCoord, cellSize, Color.SKY);
                        break;
                    case 1:
                        renderer.renderWireCube(EditorCamera.main.getCamera(), createStart, cellCoord, cellSize, Color.SKY);
                        break;
                    case 2:
                        renderer.renderWireCube(EditorCamera.main.getCamera(), createStart, createEnd, cellSize, Color.SKY);
                        break;
                }
                break;
        }
    }

    private void renderMeshes() {
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

                LayerProperties properties = layer.getProperties();
                int offsetX = properties.getOffsetX();
                int offsetY = properties.getOffsetY();
                int offsetZ = properties.getOffsetZ();

                if (offsetX != 0 || offsetY != 0 || offsetZ != 0) {
                    Matrix4 m = new Matrix4(new Vector3(offsetX, offsetY, offsetZ), new Quaternion(), new Vector3(1f, 1f, 1f));
                    levelShader.setUniformMatrix("u_world", m);
                } else {
                    levelShader.setUniformMatrix("u_world", new Matrix4());
                }

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

    public void disengageLineTool() {
        lineToolEngaged = false;
        lineStartSet = false;
    }

    void execute(Command command) {
        command.execute();
        commands.push(command);
        redoCommands.clear();
        if (commands.size() > MAX_UNDO) {
            commands.removeFirst();
        }
    }

    void undo() {
        System.out.println("Undo 1");
        if (commands.size() > 0) {
            System.out.println("Undo 2");
            Command command = commands.pop();
            command.undo();
            redoCommands.push(command);
        }
    }

    void redo() {
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
        return editorMode.touchUp(screenX, screenY, button);
    }

    void handleCreateClick(int x, int y) {
        createClicks++;

        if (createClicks == 1) {
            getPlugin(ObjectEditorPlugin.class).createAt(createClicks, cellCoord);
            createStart = new Coord(cellCoord);
        } else if (createClicks == 2) {
            getPlugin(ObjectEditorPlugin.class).createAt(createClicks, cellCoord);
            createEnd = new Coord(cellCoord);
            createStartY = y;
        } else if (createClicks == 3) {
            getPlugin(ObjectEditorPlugin.class).createAt(createClicks, createEnd);
            createClicks = 0;
        }
    }

    void handleSelectClick(int x, int y) {
        getPlugin(ObjectEditorPlugin.class).selectAt(x, y);
    }

    @Override
    public boolean mouseMoved(int x, int y) {

        return editorMode.mouseMoved(x, y);
    }

    void extractCellFromFace() {
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
    void adjustCellForTool() {
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

    void findCellOnGrid(Camera cam) {
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

    boolean findCellByFace(Ray ray, float[] vertices, short[] indices, int vertexSize, float minDist, boolean hit) {
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

    void recreateMeshes() {
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

        switch (this.tool) {
            case BLOCK:
                this.editorMode = new DrawMode(this);
                break;
            case APPEND:
                this.editorMode = new AppendMode(this);
                break;
        }
    }

    public ObjectRenderer getObjectRenderer() {
        return objectRenderer;
    }

    public void toggleLayerHighlighting() {
        this.highlightSelectedLayer = !this.highlightSelectedLayer;
    }

    public void nudgeSelectedLayerBy(int i) {
        layerUi.getPresenter().getSelectedLayer().nudge(i);
        recreateMeshes();
    }

    Coord getCellCoord() {
        return cellCoord;
    }

    boolean isLineToolEngaged() {
        return lineToolEngaged;
    }

    boolean isLineStartSet() {
        return lineStartSet;
    }

    void setLineToolStartX(int x) {
        this.lineToolStartX = x;
    }

    void setLineToolStartY(int y) {
        this.lineToolStartY = y;
    }

    void setLineToolStartZ(int z) {
        this.lineToolStartZ = z;
    }

    void setLineStartSet(boolean b) {
        lineStartSet = b;
    }

    EditorController getEditorController() {
        return editorController;
    }

    int getLineToolStartX() {
        return lineToolStartX;
    }

    int getLineToolStartY() {
        return lineToolStartY;
    }

    int getLineToolStartZ() {
        return lineToolStartZ;
    }

    int getCellSize() {
        return cellSize;
    }

    LayerUi getLayerUi() {
        return layerUi;
    }

    Tool getTool() {
        return tool;
    }

    TilesetPlugin getTilesetPlugin() {
        return tilesetPlugin;
    }

    void setLineToolEngaged(boolean engaged) {
        this.lineToolEngaged = engaged;
    }

    Face getCurrentFace() {
        return currentFace;
    }

    int getCreateClicks() {
        return createClicks;
    }

    Coord getCreateEnd() {
        return createEnd;
    }

    int getCreateStartY() {
        return createStart.y;
    }

    Map<TileLayer, Map<Tileset, Mesh>> getMeshes() {
        return meshes;
    }

    int getXzPlane() {
        return xzPlane;
    }

    Vector3 getTmp0() {
        return tmp0;
    }

    public void useAppendMode() {
        this.editorMode = new AppendMode(this);
    }

    public void resizeWorld(int size) {
        this.worldSize = size;
        level.setSize(worldSize);
    }

    public int getWorldSize() {
        return worldSize;
    }
}
