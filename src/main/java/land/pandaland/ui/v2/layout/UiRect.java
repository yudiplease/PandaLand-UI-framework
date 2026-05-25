package land.pandaland.ui.v2.layout;

public final class UiRect {
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public UiRect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
    }

    public boolean contains(int px, int py) {
        return px >= x && py >= y && px < x + width && py < y + height;
    }

    public boolean equals(Object other) {
        if (!(other instanceof UiRect)) {
            return false;
        }
        UiRect rect = (UiRect) other;
        return x == rect.x && y == rect.y && width == rect.width && height == rect.height;
    }

    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    public String toString() {
        return "UiRect{x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "}";
    }
}
