package land.pandaland.ui.v2.data;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable row descriptor for table and data-grid controls.
 */
public final class UiTableRow {
    private final String id;
    private final Map<String, String> cells;
    private final boolean disabled;

    /**
     * Creates an enabled table row.
     *
     * @param id stable row id
     * @param cells cell values keyed by column id
     */
    public UiTableRow(String id, Map<String, String> cells) {
        this(id, cells, false);
    }

    /**
     * Creates a table row.
     *
     * @param id stable row id
     * @param cells cell values keyed by column id
     * @param disabled whether the row should reject selection
     */
    public UiTableRow(String id, Map<String, String> cells, boolean disabled) {
        this.id = id == null ? "" : id;
        this.cells = copyCells(cells);
        this.disabled = disabled;
    }

    /**
     * Returns the stable row id.
     *
     * @return row id, never {@code null}
     */
    public String id() {
        return id;
    }

    /**
     * Returns immutable cell values keyed by column id.
     *
     * @return cell map, never {@code null}
     */
    public Map<String, String> cells() {
        return cells;
    }

    /**
     * Returns a single cell value.
     *
     * @param columnId column id
     * @return cell value, or an empty string when absent
     */
    public String cell(String columnId) {
        String value = cells.get(columnId);
        return value == null ? "" : value;
    }

    /**
     * Reports whether this row is disabled.
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
        if (!(other instanceof UiTableRow)) {
            return false;
        }
        UiTableRow that = (UiTableRow) other;
        return disabled == that.disabled && id.equals(that.id) && cells.equals(that.cells);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + cells.hashCode();
        result = 31 * result + (disabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UiTableRow{id='" + id + "', cells=" + cells + ", disabled=" + disabled + "}";
    }

    private static Map<String, String> copyCells(Map<String, String> cells) {
        if (cells == null || cells.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> copy = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : cells.entrySet()) {
            String key = entry.getKey() == null ? "" : entry.getKey();
            String value = entry.getValue() == null ? "" : entry.getValue();
            copy.put(key, value);
        }
        return Collections.unmodifiableMap(copy);
    }
}
