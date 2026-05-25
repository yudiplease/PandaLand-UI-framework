package land.pandaland.ui.api;

/**
 * Immutable packed color used by PandaLand UI theme and render APIs.
 *
 * <p>The stored integer uses RGBA byte order: red in the highest byte and
 * alpha in the lowest byte. Use {@link #toArgb()} when passing the color to
 * Minecraft methods that expect ARGB values.</p>
 */
public class PandaColor {
    private final int rgba;

    /**
     * Creates a color from a packed RGBA integer.
     *
     * @param rgba packed RGBA value, for example {@code 0x27F3D6FF}
     */
    public PandaColor(int rgba) {
        this.rgba = rgba;
    }

    /**
     * @return the raw packed RGBA value
     */
    public int rgba() {
        return rgba;
    }

    /**
     * Converts this color to the ARGB byte order expected by Minecraft GUI
     * drawing helpers.
     *
     * @return packed ARGB color
     */
    public int toArgb() {
        return (alpha() << 24) | (red() << 16) | (green() << 8) | blue();
    }

    /** @return red channel in the range {@code 0..255} */
    public int red() {
        return (rgba >>> 24) & 0xFF;
    }

    /** @return green channel in the range {@code 0..255} */
    public int green() {
        return (rgba >>> 16) & 0xFF;
    }

    /** @return blue channel in the range {@code 0..255} */
    public int blue() {
        return (rgba >>> 8) & 0xFF;
    }

    /** @return alpha channel in the range {@code 0..255} */
    public int alpha() {
        return rgba & 0xFF;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PandaColor)) {
            return false;
        }
        PandaColor that = (PandaColor) other;
        return rgba == that.rgba;
    }

    @Override
    public int hashCode() {
        return rgba;
    }

    @Override
    public String toString() {
        return String.format("PandaColor{rgba=0x%08X}", rgba);
    }
}
