package land.pandaland.ui.v2.data;

/**
 * Immutable page descriptor for page-stack controls.
 */
public final class UiPage {
    private final String id;
    private final String label;
    private final boolean disabled;

    /**
     * Creates an enabled page descriptor.
     *
     * @param id stable page id
     * @param label visible page label
     */
    public UiPage(String id, String label) {
        this(id, label, false);
    }

    /**
     * Creates a page descriptor.
     *
     * @param id stable page id
     * @param label visible page label
     * @param disabled whether the page should reject navigation
     */
    public UiPage(String id, String label, boolean disabled) {
        this.id = id == null ? "" : id;
        this.label = label == null ? "" : label;
        this.disabled = disabled;
    }

    /**
     * Returns the stable page id.
     *
     * @return page id, never {@code null}
     */
    public String id() {
        return id;
    }

    /**
     * Returns the visible page label.
     *
     * @return page label, never {@code null}
     */
    public String label() {
        return label;
    }

    /**
     * Reports whether this page is disabled.
     *
     * @return disabled flag
     */
    public boolean disabled() {
        return disabled;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UiPage)) {
            return false;
        }
        UiPage that = (UiPage) other;
        return disabled == that.disabled && id.equals(that.id) && label.equals(that.label);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + label.hashCode();
        result = 31 * result + (disabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UiPage{id='" + id + "', label='" + label + "', disabled=" + disabled + "}";
    }
}
