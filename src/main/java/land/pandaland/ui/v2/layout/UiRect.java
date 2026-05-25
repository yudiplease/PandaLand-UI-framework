package land.pandaland.ui.v2.layout;

/**
 * Immutable rectangle in scaled Minecraft GUI pixels.
 */
public final class UiRect {
    /**
     * Left coordinate.
     */
    public final int x;

    /**
     * Top coordinate.
     */
    public final int y;

    /**
     * Non-negative width.
     */
    public final int width;

    /**
     * Non-negative height.
     */
    public final int height;

    /**
     * Creates a rectangle. Negative dimensions are clamped to zero.
     *
     * @param x left coordinate
     * @param y top coordinate
     * @param width width
     * @param height height
     */
    public UiRect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
    }

    /**
     * Checks whether a point is inside the rectangle.
     *
     * @param px point x coordinate
     * @param py point y coordinate
     * @return {@code true} when the point is inside the half-open rectangle
     */
    public boolean contains(int px, int py) {
        return px >= x && py >= y && px < x + width && py < y + height;
    }

    /**
     * Compares rectangles by coordinates and dimensions.
     *
     * @param other object to compare
     * @return {@code true} when the object is an equal rectangle
     */
    public boolean equals(Object other) {
        if (!(other instanceof UiRect)) {
            return false;
        }
        UiRect rect = (UiRect) other;
        return x == rect.x && y == rect.y && width == rect.width && height == rect.height;
    }

    /**
     * Returns the rectangle hash code.
     *
     * @return hash code
     */
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    /**
     * Returns a diagnostic string.
     *
     * @return rectangle description
     */
    public String toString() {
        return "UiRect{x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "}";
    }
}
