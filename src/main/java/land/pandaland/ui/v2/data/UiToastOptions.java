package land.pandaland.ui.v2.data;

/**
 * Immutable display options for toast notification nodes.
 */
public final class UiToastOptions {
    private final String message;
    private final long durationMs;
    private final int width;
    private final int height;
    private final String tone;

    /**
     * Creates toast options using the default toast size.
     *
     * @param message visible toast message
     * @param durationMs lifetime in milliseconds
     */
    public UiToastOptions(String message, long durationMs) {
        this(message, durationMs, 160, 22, "");
    }

    /**
     * Creates toast options.
     *
     * @param message visible toast message
     * @param durationMs lifetime in milliseconds
     * @param width preferred toast width
     * @param height preferred toast height
     * @param tone renderer-defined tone metadata, or empty for default
     */
    public UiToastOptions(String message, long durationMs, int width, int height, String tone) {
        this.message = message == null ? "" : message;
        this.durationMs = Math.max(0L, durationMs);
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
        this.tone = tone == null ? "" : tone;
    }

    /**
     * Returns visible toast message.
     *
     * @return message, never {@code null}
     */
    public String message() {
        return message;
    }

    /**
     * Returns toast lifetime.
     *
     * @return duration in milliseconds
     */
    public long durationMs() {
        return durationMs;
    }

    /**
     * Returns preferred toast width.
     *
     * @return width in scaled GUI pixels
     */
    public int width() {
        return width;
    }

    /**
     * Returns preferred toast height.
     *
     * @return height in scaled GUI pixels
     */
    public int height() {
        return height;
    }

    /**
     * Returns renderer-defined tone metadata.
     *
     * @return tone, never {@code null}
     */
    public String tone() {
        return tone;
    }
}
