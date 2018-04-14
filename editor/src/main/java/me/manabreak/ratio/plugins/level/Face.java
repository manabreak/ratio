package me.manabreak.ratio.plugins.level;

import com.badlogic.gdx.math.Vector3;

public enum Face {
    FRONT(0f, 0f, 1f),
    BACK(0f, 0f, -1f),
    LEFT(1f, 0f, 0f),
    RIGHT(-1f, 0f, 0f),
    TOP(0f, 1f, 0f),
    BOTTOM(0f, -1f, 0f);

    private final Vector3 normal;

    Face(float nx, float ny, float nz) {
        normal = new Vector3(nx, ny, nz);
    }

    public Vector3 getNormal() {
        return normal;
    }
}
