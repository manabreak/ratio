package me.manabreak.ratio.plugins.toolbar;

public enum Tool {

    /**
     * Denotes the state when no tool is chosen
     */
    NONE(Category.NONE),

    /**
     * Draw voxel blocks
     */
    BLOCK(Category.VOXEL),

    /**
     * Draw horizontal quads
     */
    FLOOR(Category.VOXEL),

    /**
     * Paint existing voxel faces
     */
    PAINT(Category.VOXEL),

    /**
     * Erase blocks
     */
    ERASE(Category.VOXEL),

    /**
     * Select objects
     */
    SELECT(Category.OBJECT),

    /**
     * Create objects
     */
    CREATE(Category.OBJECT);

    private final Category category;

    Tool(Category category) {
        this.category = category;
    }

    public Category category() {
        return this.category;
    }

    public enum Category {
        NONE,
        VOXEL,
        OBJECT
    }
}
