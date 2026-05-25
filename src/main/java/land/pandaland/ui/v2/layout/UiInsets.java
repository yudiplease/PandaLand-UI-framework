package land.pandaland.ui.v2.layout;

/**
 * Immutable non-negative inset values for layout padding.
 */
public final class UiInsets {
    /**
     * Left inset in scaled GUI pixels.
     */
    public final int left;

    /**
     * Top inset in scaled GUI pixels.
     */
    public final int top;

    /**
     * Right inset in scaled GUI pixels.
     */
    public final int right;

    /**
     * Bottom inset in scaled GUI pixels.
     */
    public final int bottom;

    /**
     * Creates insets. Negative values are clamped to zero.
     *
     * @param left left inset
     * @param top top inset
     * @param right right inset
     * @param bottom bottom inset
     */
    public UiInsets(int left, int top, int right, int bottom) {
        this.left = Math.max(0, left);
        this.top = Math.max(0, top);
        this.right = Math.max(0, right);
        this.bottom = Math.max(0, bottom);
    }

    /**
     * Creates equal insets for every side.
     *
     * @param value inset value
     * @return equal insets
     */
    public static UiInsets all(int value) {
        return new UiInsets(value, value, value, value);
    }
}
