package land.pandaland.ui.v2.layout;

public final class UiInsets {
    public final int left;
    public final int top;
    public final int right;
    public final int bottom;

    public UiInsets(int left, int top, int right, int bottom) {
        this.left = Math.max(0, left);
        this.top = Math.max(0, top);
        this.right = Math.max(0, right);
        this.bottom = Math.max(0, bottom);
    }

    public static UiInsets all(int value) {
        return new UiInsets(value, value, value, value);
    }
}
