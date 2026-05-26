package land.pandaland.ui.v2.data;

/**
 * Immutable option descriptor for select-style controls.
 */
public final class UiOption {
    private final String id;
    private final String label;
    private final boolean disabled;

    /**
     * Creates an enabled option.
     *
     * @param id stable option id
     * @param label visible option label
     */
    public UiOption(String id, String label) {
        this(id, label, false);
    }

    /**
     * Creates an option.
     *
     * @param id stable option id
     * @param label visible option label
     * @param disabled whether the option should reject selection
     */
    public UiOption(String id, String label, boolean disabled) {
        this.id = normalize(id);
        this.label = label == null ? "" : label;
        this.disabled = disabled;
    }

    /**
     * Returns the stable option id.
     *
     * @return option id, never {@code null}
     */
    public String id() {
        return id;
    }

    /**
     * Returns the visible option label.
     *
     * @return option label, never {@code null}
     */
    public String label() {
        return label;
    }

    /**
     * Reports whether this option is disabled.
     *
     * @return disabled flag
     */
    public boolean disabled() {
        return disabled;
    }

    /**
     * Converts this option into generic list item metadata.
     *
     * @return equivalent list item
     */
    public UiListItem toListItem() {
        return new UiListItem(id, label, disabled);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UiOption)) {
            return false;
        }
        UiOption that = (UiOption) other;
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
        return "UiOption{id='" + id + "', label='" + label + "', disabled=" + disabled + "}";
    }

    private static String normalize(String value) {
        return value == null ? "" : value;
    }
}
