package land.pandaland.ui.v2.style;

/**
 * Immutable ARGB color token.
 */
public final class UiColor {
    private final int argb;

    /**
     * Creates a color from a packed ARGB integer.
     *
     * @param argb packed alpha, red, green, and blue channels
     */
    public UiColor(int argb) {
        this.argb = argb;
    }

    /**
     * Returns the packed ARGB color value.
     *
     * @return packed ARGB integer
     */
    public int argb() {
        return argb;
    }
}
