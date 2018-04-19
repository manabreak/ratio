package me.manabreak.ratio.plugins.toolbar;

public enum Tool {
    /**
     * Denotes the state when no tool is chosen
     */
    NONE,

    /**
     * Draw voxel blocks
     */
    BLOCK,

    /**
     * Draw horizontal quads
     */
    FLOOR,

    /**
     * Paint existing voxel faces
     */
    PAINT,

    /**
     * Erase blocks
     */
    ERASE,

    /**
     * Select objects
     */
    SELECT,

    /**
     * Create objects
     */
    CREATE
}
