package land.pandaland.ui.v2.layout;

public final class UiSize {
    public final int width;
    public final int height;

    public UiSize(int width, int height) {
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
    }
}
