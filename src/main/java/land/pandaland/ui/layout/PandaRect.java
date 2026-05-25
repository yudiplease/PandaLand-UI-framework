package land.pandaland.ui.layout;

/**
 * @deprecated Use {@link land.pandaland.ui.api.PandaRect}. Public geometry
 * lives in the API package.
 */
@Deprecated
public final class PandaRect {
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public PandaRect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
    }

    public boolean contains(int px, int py) {
        long right = (long) x + width;
        long bottom = (long) y + height;
        return px >= x && py >= y && (long) px < right && (long) py < bottom;
    }

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
