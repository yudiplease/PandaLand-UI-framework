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
        SCROLL
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
     * Stores whether row/column wrapping is requested.
     *
     * <p>The current engine preserves the flag for API compatibility; wrapping
     * behavior can be expanded without changing node declarations.</p>
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
