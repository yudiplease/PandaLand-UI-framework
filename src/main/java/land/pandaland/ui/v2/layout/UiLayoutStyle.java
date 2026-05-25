package land.pandaland.ui.v2.layout;

public final class UiLayoutStyle {
    public enum Direction {
        LEAF,
        COLUMN,
        ROW,
        OVERLAY
    }

    private final Direction direction;
    private UiInsets padding = UiInsets.all(0);
    private int gap;
    private int width;
    private int height;

    private UiLayoutStyle(Direction direction) {
        this.direction = direction;
    }

    public static UiLayoutStyle leaf() {
        return new UiLayoutStyle(Direction.LEAF);
    }

    public static UiLayoutStyle column() {
        return new UiLayoutStyle(Direction.COLUMN);
    }

    public static UiLayoutStyle row() {
        return new UiLayoutStyle(Direction.ROW);
    }

    public static UiLayoutStyle overlay() {
        return new UiLayoutStyle(Direction.OVERLAY);
    }

    public UiLayoutStyle padding(int padding) {
        this.padding = UiInsets.all(padding);
        return this;
    }

    public UiLayoutStyle padding(int left, int top, int right, int bottom) {
        this.padding = new UiInsets(left, top, right, bottom);
        return this;
    }

    public UiLayoutStyle gap(int gap) {
        this.gap = Math.max(0, gap);
        return this;
    }

    public UiLayoutStyle size(int width, int height) {
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
        return this;
    }

    public Direction direction() {
        return direction;
    }

    public UiInsets padding() {
        return padding;
    }

    public int gap() {
        return gap;
    }

    public UiSize preferredSize() {
        return new UiSize(width, height);
    }
}
