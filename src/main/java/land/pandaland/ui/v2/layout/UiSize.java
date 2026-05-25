package land.pandaland.ui.v2.layout;

/**
 * Immutable non-negative two-dimensional size.
 */
public final class UiSize {
    /**
     * Width in scaled GUI pixels.
     */
    public final int width;

    /**
     * Height in scaled GUI pixels.
     */
    public final int height;

    /**
     * Creates a size. Negative values are clamped to zero.
     *
     * @param width width in scaled GUI pixels
     * @param height height in scaled GUI pixels
     */
    public UiSize(int width, int height) {
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
    }
}
