package land.pandaland.ui.v2.data;

/**
 * Immutable column descriptor for table and data-grid controls.
 */
public final class UiTableColumn {
    private final String id;
    private final String label;
    private final int width;
    private final boolean sortable;

    /**
     * Creates a non-sortable table column.
     *
     * @param id stable column id used to read row cells
     * @param label visible column heading
     * @param width preferred column width in scaled pixels
     */
    public UiTableColumn(String id, String label, int width) {
        this(id, label, width, false);
    }

    /**
     * Creates a table column.
     *
     * @param id stable column id used to read row cells
     * @param label visible column heading
     * @param width preferred column width in scaled pixels
     * @param sortable whether the column advertises sortable behavior
     */
    public UiTableColumn(String id, String label, int width, boolean sortable) {
        this.id = id == null ? "" : id;
        this.label = label == null ? "" : label;
        this.width = Math.max(0, width);
        this.sortable = sortable;
    }

    /**
     * Returns the stable column id.
     *
     * @return column id, never {@code null}
     */
    public String id() {
        return id;
    }

    /**
     * Returns the visible column heading.
     *
     * @return heading text, never {@code null}
     */
    public String label() {
        return label;
    }

    /**
     * Returns the preferred column width.
     *
     * @return width in scaled pixels
     */
    public int width() {
        return width;
    }

    /**
     * Reports whether this column can be sorted by future renderers/events.
     *
     * @return sortable flag
     */
    public boolean sortable() {
        return sortable;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UiTableColumn)) {
            return false;
        }
        UiTableColumn that = (UiTableColumn) other;
        return width == that.width && sortable == that.sortable && id.equals(that.id) && label.equals(that.label);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + label.hashCode();
        result = 31 * result + width;
        result = 31 * result + (sortable ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UiTableColumn{id='" + id + "', label='" + label + "', width=" + width + ", sortable=" + sortable + "}";
    }
}
