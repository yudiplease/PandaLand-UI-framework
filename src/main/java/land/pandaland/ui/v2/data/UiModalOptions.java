package land.pandaland.ui.v2.data;

/**
 * Immutable display options for modal overlay nodes.
 */
public final class UiModalOptions {
    private final String title;
    private final int width;
    private final int height;
    private final boolean closeOnEscape;

    /**
     * Creates modal options using the default modal size.
     *
     * @param title modal title text
     */
    public UiModalOptions(String title) {
        this(title, 180, 90, true);
    }

    /**
     * Creates modal options.
     *
     * @param title modal title text
     * @param width preferred modal width
     * @param height preferred modal height
     */
    public UiModalOptions(String title, int width, int height) {
        this(title, width, height, true);
    }

    /**
     * Creates modal options.
     *
     * @param title modal title text
     * @param width preferred modal width
     * @param height preferred modal height
     * @param closeOnEscape whether Escape should close the modal
     */
    public UiModalOptions(String title, int width, int height, boolean closeOnEscape) {
        this.title = title == null ? "" : title;
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
        this.closeOnEscape = closeOnEscape;
    }

    /**
     * Returns modal title text.
     *
     * @return title, never {@code null}
     */
    public String title() {
        return title;
    }

    /**
     * Returns preferred modal width.
     *
     * @return width in scaled GUI pixels
     */
    public int width() {
        return width;
    }

    /**
     * Returns preferred modal height.
     *
     * @return height in scaled GUI pixels
     */
    public int height() {
        return height;
    }

    /**
     * Reports whether Escape should close this modal.
     *
     * @return close-on-escape flag
     */
    public boolean closeOnEscape() {
        return closeOnEscape;
    }
}
