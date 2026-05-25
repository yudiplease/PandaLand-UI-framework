package land.pandaland.ui.api;

/**
 * Immutable integer rectangle for layout, hit testing, and rendering bounds.
 */
public final class PandaRect {
    /** Left coordinate. */
    public final int x;
    /** Top coordinate. */
    public final int y;
    /** Non-negative width. */
    public final int width;
    /** Non-negative height. */
    public final int height;

    /**
     * Creates a rectangle. Negative width or height values are clamped to zero.
     *
     * @param x left coordinate
     * @param y top coordinate
     * @param width requested width
     * @param height requested height
     */
    public PandaRect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
    }

    /**
     * Tests whether a point lies inside this rectangle.
     *
     * <p>The left and top edges are inclusive; the right and bottom edges are
     * exclusive.</p>
     *
     * @param px point x
     * @param py point y
     * @return {@code true} when the point is inside the rectangle
     */
    public boolean contains(int px, int py) {
        long right = (long) x + width;
        long bottom = (long) y + height;
        return px >= x && py >= y && (long) px < right && (long) py < bottom;
    }

    /**
     * Returns a rectangle inset from every edge by the supplied amount.
     *
     * @param amount inset amount; negative values are treated as zero
     * @return inset rectangle with non-negative dimensions
     */
    public PandaRect inset(int amount) {
        int safe = Math.max(0, amount);
        return new PandaRect(
            clampToInt((long) x + safe),
            clampToInt((long) y + safe),
            clampToInt(Math.max(0L, (long) width - safe * 2L)),
            clampToInt(Math.max(0L, (long) height - safe * 2L))
        );
    }

    private static int clampToInt(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PandaRect)) {
            return false;
        }
        PandaRect that = (PandaRect) other;
        return x == that.x && y == that.y && width == that.width && height == that.height;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return "PandaRect{x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "}";
    }
}
