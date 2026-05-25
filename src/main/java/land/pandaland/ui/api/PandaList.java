package land.pandaland.ui.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple selectable list of text rows.
 */
public final class PandaList extends PandaComponent {
    private static final int ROW_HEIGHT = 18;

    private final List<String> rows = new ArrayList<String>();
    private int selectedIndex = -1;

    /**
     * Adds a selectable row.
     *
     * @param row row text
     * @return this list
     */
    public PandaList addRow(String row) {
        rows.add(row == null ? "" : row);
        clampSelection();
        return this;
    }

    /**
     * @return immutable list of row labels
     */
    public List<String> rows() {
        return Collections.unmodifiableList(rows);
    }

    /**
     * @return selected row index, or {@code -1} when the list is empty
     */
    public int selectedIndex() {
        return selectedIndex;
    }

    /**
     * Selects a row by index. Out-of-range values are clamped.
     *
     * @param selectedIndex requested selected index
     * @return this list
     */
    public PandaList selectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        clampSelection();
        return this;
    }

    public int preferredWidth() {
        int width = 120;
        for (String row : rows) {
            width = Math.max(width, row.length() * 7 + 24);
        }
        return width;
    }

    public int preferredHeight() {
        return Math.max(ROW_HEIGHT, rows.size() * ROW_HEIGHT);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible() || !enabled() || button != 0 || rows.isEmpty() || !bounds().contains(mouseX, mouseY)) {
            return false;
        }
        selectedIndex = Math.min(rows.size() - 1, Math.max(0, (mouseY - bounds().y) / ROW_HEIGHT));
        return true;
    }

    private void clampSelection() {
        if (rows.isEmpty()) {
            selectedIndex = -1;
            return;
        }
        if (selectedIndex < 0) {
            selectedIndex = 0;
            return;
        }
        selectedIndex = Math.min(rows.size() - 1, selectedIndex);
    }
}
