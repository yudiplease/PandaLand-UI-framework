package land.pandaland.ui.v2.layout;

/**
 * Mutable layout style attached to a retained UI node.
 *
 * <p>Styles describe how a node should place its children and how the node
 * itself should be measured by its parent. Builder methods are fluent and clamp
 * invalid numeric values to safe ranges.</p>
 */
public final class UiLayoutStyle {
    /**
     * Layout algorithm used by a node.
     */
    public enum Direction {
        /**
         * Leaf node with no child layout.
         */
        LEAF,
        /**
         * Children are placed vertically.
         */
        COLUMN,
        /**
         * Children are placed horizontally.
         */
        ROW,
        /**
         * Children are stacked over the same content rectangle.
         */
        OVERLAY,
        /**
         * Children are placed vertically in scrollable content.
         */
        SCROLL,
        /**
         * Children are placed in a fixed-column grid.
         */
        GRID,
        /**
         * Children are placed at explicit offsets inside the parent content area.
         */
        ABSOLUTE
    }

    /**
     * Cross-axis alignment for children.
     */
    public enum Align {
        /**
         * Align to the start edge.
         */
        START,
        /**
         * Center inside available space.
         */
        CENTER,
        /**
         * Align to the end edge.
         */
        END,
        /**
         * Fill available cross-axis space.
         */
        STRETCH
    }

    private final Direction direction;
    private UiInsets padding = UiInsets.all(0);
    private int gap;
    private int width;
    private int height;
    private int minWidth;
    private int minHeight;
    private int maxWidth = Integer.MAX_VALUE;
    private int maxHeight = Integer.MAX_VALUE;
    private int x;
    private int y;
    private int gridColumns = 1;
    private int gridRowHeight;
    private int zIndex;
    private float grow;
    private float shrink = 1.0F;
    private float widthPercent = -1.0F;
    private float heightPercent = -1.0F;
    private Align crossAlign = Align.START;
    private boolean wrap;

    private UiLayoutStyle(Direction direction) {
        this.direction = direction;
    }

    /**
     * Creates a leaf style.
     *
     * @return leaf style
     */
    public static UiLayoutStyle leaf() {
        return new UiLayoutStyle(Direction.LEAF);
    }

    /**
     * Creates a vertical column style.
     *
     * @return column style
     */
    public static UiLayoutStyle column() {
        return new UiLayoutStyle(Direction.COLUMN);
    }

    /**
     * Creates a horizontal row style.
     *
     * @return row style
     */
    public static UiLayoutStyle row() {
        return new UiLayoutStyle(Direction.ROW);
    }

    /**
     * Creates an overlay style.
     *
     * @return overlay style
     */
    public static UiLayoutStyle overlay() {
        return new UiLayoutStyle(Direction.OVERLAY);
    }

    /**
     * Creates a scroll content style.
     *
     * @return scroll style
     */
    public static UiLayoutStyle scroll() {
        return new UiLayoutStyle(Direction.SCROLL);
    }

    /**
     * Creates a fixed-column grid style.
     *
     * @param columns number of columns; values below one are clamped to one
     * @param rowHeight fixed row height in scaled GUI pixels
     * @return grid style
     */
    public static UiLayoutStyle grid(int columns, int rowHeight) {
        return new UiLayoutStyle(Direction.GRID).gridColumns(columns).gridRowHeight(rowHeight);
    }

    /**
     * Creates an absolute-positioning style.
     *
     * @return absolute style
     */
    public static UiLayoutStyle absolute() {
        return new UiLayoutStyle(Direction.ABSOLUTE);
    }

    /**
     * Sets equal padding on every side.
     *
     * @param padding padding in scaled GUI pixels
     * @return this style
     */
    public UiLayoutStyle padding(int padding) {
        this.padding = UiInsets.all(padding);
        return this;
    }

    /**
     * Sets per-side padding.
     *
     * @param left left padding
     * @param top top padding
     * @param right right padding
     * @param bottom bottom padding
     * @return this style
     */
    public UiLayoutStyle padding(int left, int top, int right, int bottom) {
        this.padding = new UiInsets(left, top, right, bottom);
        return this;
    }

    /**
     * Sets the gap between children.
     *
     * @param gap gap in scaled GUI pixels
     * @return this style
     */
    public UiLayoutStyle gap(int gap) {
        this.gap = Math.max(0, gap);
        return this;
    }

    /**
     * Sets preferred size for this node.
     *
     * @param width preferred width
     * @param height preferred height
     * @return this style
     */
    public UiLayoutStyle size(int width, int height) {
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
        return this;
    }

    /**
     * Sets the minimum size for this node.
     *
     * @param width minimum width
     * @param height minimum height
     * @return this style
     */
    public UiLayoutStyle minSize(int width, int height) {
        return minWidth(width).minHeight(height);
    }

    /**
     * Sets the maximum size for this node.
     *
     * @param width maximum width
     * @param height maximum height
     * @return this style
     */
    public UiLayoutStyle maxSize(int width, int height) {
        return maxWidth(width).maxHeight(height);
    }

    /**
     * Sets the minimum resolved width for this node.
     *
     * @param minWidth minimum width
     * @return this style
     */
    public UiLayoutStyle minWidth(int minWidth) {
        this.minWidth = Math.max(0, minWidth);
        return this;
    }

    /**
     * Sets the minimum resolved height for this node.
     *
     * @param minHeight minimum height
     * @return this style
     */
    public UiLayoutStyle minHeight(int minHeight) {
        this.minHeight = Math.max(0, minHeight);
        return this;
    }

    /**
     * Sets the maximum resolved width for this node.
     *
     * @param maxWidth maximum width
     * @return this style
     */
    public UiLayoutStyle maxWidth(int maxWidth) {
        this.maxWidth = Math.max(0, maxWidth);
        return this;
    }

    /**
     * Sets the maximum resolved height for this node.
     *
     * @param maxHeight maximum height
     * @return this style
     */
    public UiLayoutStyle maxHeight(int maxHeight) {
        this.maxHeight = Math.max(0, maxHeight);
        return this;
    }

    /**
     * Sets this node's offset from the parent content origin.
     *
     * <p>Absolute layout uses these coordinates directly. Other layout modes
     * keep the metadata for render and hit-test layers.</p>
     *
     * @param x x offset
     * @param y y offset
     * @return this style
     */
    public UiLayoutStyle offset(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Sets this node's x offset from the parent content origin.
     *
     * @param x x offset
     * @return this style
     */
    public UiLayoutStyle x(int x) {
        this.x = x;
        return this;
    }

    /**
     * Sets this node's y offset from the parent content origin.
     *
     * @param y y offset
     * @return this style
     */
    public UiLayoutStyle y(int y) {
        this.y = y;
        return this;
    }

    /**
     * Sets the fixed column count used by grid layout.
     *
     * @param gridColumns number of columns; values below one are clamped to one
     * @return this style
     */
    public UiLayoutStyle gridColumns(int gridColumns) {
        this.gridColumns = Math.max(1, gridColumns);
        return this;
    }

    /**
     * Sets the fixed row height used by grid layout.
     *
     * @param gridRowHeight row height in scaled GUI pixels
     * @return this style
     */
    public UiLayoutStyle gridRowHeight(int gridRowHeight) {
        this.gridRowHeight = Math.max(0, gridRowHeight);
        return this;
    }

    /**
     * Stores stacking index metadata for future render ordering support.
     *
     * <p>The layout engine preserves this value but does not apply ordering.</p>
     *
     * @param zIndex stacking index metadata
     * @return this style
     */
    public UiLayoutStyle zIndex(int zIndex) {
        this.zIndex = zIndex;
        return this;
    }

    /**
     * Sets grow weight used when parent has extra main-axis space.
     *
     * @param grow non-negative grow weight
     * @return this style
     */
    public UiLayoutStyle grow(float grow) {
        this.grow = Math.max(0.0F, grow);
        return this;
    }

    /**
     * Sets shrink weight used when children exceed available main-axis space.
     *
     * @param shrink non-negative shrink weight
     * @return this style
     */
    public UiLayoutStyle shrink(float shrink) {
        this.shrink = Math.max(0.0F, shrink);
        return this;
    }

    /**
     * Sets width as a percentage of parent content width.
     *
     * @param widthPercent value in range {@code 0.0} to {@code 1.0}, or negative to unset
     * @return this style
     */
    public UiLayoutStyle widthPercent(float widthPercent) {
        this.widthPercent = clampPercent(widthPercent);
        return this;
    }

    /**
     * Sets height as a percentage of parent content height.
     *
     * @param heightPercent value in range {@code 0.0} to {@code 1.0}, or negative to unset
     * @return this style
     */
    public UiLayoutStyle heightPercent(float heightPercent) {
        this.heightPercent = clampPercent(heightPercent);
        return this;
    }

    /**
     * Sets cross-axis alignment.
     *
     * @param crossAlign alignment value; {@code null} resets to {@link Align#START}
     * @return this style
     */
    public UiLayoutStyle align(Align crossAlign) {
        this.crossAlign = crossAlign == null ? Align.START : crossAlign;
        return this;
    }

    /**
     * Makes width fill the parent content width.
     *
     * @return this style
     */
    public UiLayoutStyle fillWidth() {
        widthPercent = 1.0F;
        return this;
    }

    /**
     * Makes height fill the parent content height.
     *
     * @return this style
     */
    public UiLayoutStyle fillHeight() {
        heightPercent = 1.0F;
        return this;
    }

    /**
     * Enables row wrapping for horizontal child layout.
     *
     * <p>When this flag is set on a row layout, children that would exceed the
     * parent content width move to the next line.</p>
     *
     * @param wrap wrapping flag
     * @return this style
     */
    public UiLayoutStyle wrap(boolean wrap) {
        this.wrap = wrap;
        return this;
    }

    /**
     * Returns layout direction.
     *
     * @return direction value
     */
    public Direction direction() {
        return direction;
    }

    /**
     * Returns padding values for this style.
     *
     * @return padding insets
     */
    public UiInsets padding() {
        return padding;
    }

    /**
     * Returns the child gap.
     *
     * @return gap in scaled GUI pixels
     */
    public int gap() {
        return gap;
    }

    /**
     * Returns preferred node size.
     *
     * @return preferred size
     */
    public UiSize preferredSize() {
        return new UiSize(width, height);
    }

    /**
     * Returns the minimum resolved width.
     *
     * @return minimum width
     */
    public int minWidth() {
        return minWidth;
    }

    /**
     * Returns the minimum resolved height.
     *
     * @return minimum height
     */
    public int minHeight() {
        return minHeight;
    }

    /**
     * Returns the maximum resolved width.
     *
     * @return maximum width
     */
    public int maxWidth() {
        return maxWidth;
    }

    /**
     * Returns the maximum resolved height.
     *
     * @return maximum height
     */
    public int maxHeight() {
        return maxHeight;
    }

    /**
     * Returns the x offset from the parent content origin.
     *
     * @return x offset
     */
    public int x() {
        return x;
    }

    /**
     * Returns the y offset from the parent content origin.
     *
     * @return y offset
     */
    public int y() {
        return y;
    }

    /**
     * Returns the fixed grid column count.
     *
     * @return grid column count
     */
    public int gridColumns() {
        return gridColumns;
    }

    /**
     * Returns the fixed grid row height.
     *
     * @return grid row height
     */
    public int gridRowHeight() {
        return gridRowHeight;
    }

    /**
     * Returns stored stacking index metadata.
     *
     * @return z-index metadata
     */
    public int zIndex() {
        return zIndex;
    }

    /**
     * Returns the grow weight.
     *
     * @return grow weight
     */
    public float grow() {
        return grow;
    }

    /**
     * Returns the shrink weight.
     *
     * @return shrink weight
     */
    public float shrink() {
        return shrink;
    }

    /**
     * Returns width percentage.
     *
     * @return percentage in range {@code 0.0} to {@code 1.0}, or negative when unset
     */
    public float widthPercent() {
        return widthPercent;
    }

    /**
     * Returns height percentage.
     *
     * @return percentage in range {@code 0.0} to {@code 1.0}, or negative when unset
     */
    public float heightPercent() {
        return heightPercent;
    }

    /**
     * Returns cross-axis alignment.
     *
     * @return alignment value
     */
    public Align crossAlign() {
        return crossAlign;
    }

    /**
     * Returns the requested wrapping flag.
     *
     * @return {@code true} when wrapping was requested
     */
    public boolean wrap() {
        return wrap;
    }

    private static float clampPercent(float value) {
        if (value < 0.0F) {
            return -1.0F;
        }
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
