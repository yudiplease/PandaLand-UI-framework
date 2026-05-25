package land.pandaland.ui.v2.event;

/**
 * Immutable pointer event payload used by mouse drag callbacks.
 */
public final class UiPointerEvent {
    /**
     * Current pointer x coordinate in scaled GUI pixels.
     */
    public final int x;

    /**
     * Current pointer y coordinate in scaled GUI pixels.
     */
    public final int y;

    /**
     * Mouse button id reported by Minecraft.
     */
    public final int button;

    /**
     * Horizontal movement since the previous drag callback.
     */
    public final int deltaX;

    /**
     * Vertical movement since the previous drag callback.
     */
    public final int deltaY;

    /**
     * Drag duration reported by Minecraft in milliseconds.
     */
    public final long dragTimeMs;

    /**
     * Creates a pointer event without movement delta.
     *
     * @param x pointer x coordinate
     * @param y pointer y coordinate
     * @param button mouse button id
     * @param dragTimeMs drag duration in milliseconds
     */
    public UiPointerEvent(int x, int y, int button, long dragTimeMs) {
        this(x, y, button, 0, 0, dragTimeMs);
    }

    /**
     * Creates a pointer event with movement delta.
     *
     * @param x pointer x coordinate
     * @param y pointer y coordinate
     * @param button mouse button id
     * @param deltaX horizontal movement since the previous drag callback
     * @param deltaY vertical movement since the previous drag callback
     * @param dragTimeMs drag duration in milliseconds
     */
    public UiPointerEvent(int x, int y, int button, int deltaX, int deltaY, long dragTimeMs) {
        this.x = x;
        this.y = y;
        this.button = button;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.dragTimeMs = Math.max(0L, dragTimeMs);
    }
}
