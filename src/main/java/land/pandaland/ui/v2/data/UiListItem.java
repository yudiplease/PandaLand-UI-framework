package land.pandaland.ui.v2.data;

/**
 * Immutable item descriptor for list-style controls.
 */
public final class UiListItem {
    private final String id;
    private final String label;
    private final boolean disabled;

    /**
     * Creates an enabled list item.
     *
     * @param id stable item id
     * @param label visible item label
     */
    public UiListItem(String id, String label) {
        this(id, label, false);
    }

    /**
     * Creates a list item.
     *
     * @param id stable item id
     * @param label visible item label
     * @param disabled whether the item should reject selection
     */
    public UiListItem(String id, String label, boolean disabled) {
        this.id = id == null ? "" : id;
        this.label = label == null ? "" : label;
        this.disabled = disabled;
    }

    /**
     * Returns the stable item id.
     *
     * @return item id, never {@code null}
     */
    public String id() {
        return id;
    }

    /**
     * Returns the visible item label.
     *
     * @return item label, never {@code null}
     */
    public String label() {
        return label;
    }

    /**
     * Reports whether this item is disabled.
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
        if (!(other instanceof UiListItem)) {
            return false;
        }
        UiListItem that = (UiListItem) other;
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
        return "UiListItem{id='" + id + "', label='" + label + "', disabled=" + disabled + "}";
    }
}
