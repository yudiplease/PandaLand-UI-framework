package land.pandaland.ui.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Horizontal tab selector.
 */
public final class PandaTabs extends PandaComponent {
    private final List<String> labels = new ArrayList<String>();
    private int selectedIndex;

    /**
     * Adds a tab label.
     *
     * @param label visible tab label
     * @return this tab selector
     */
    public PandaTabs addTab(String label) {
        labels.add(label == null ? "" : label);
        clampSelection();
        return this;
    }

    /**
     * @return immutable tab labels
     */
    public List<String> labels() {
        return Collections.unmodifiableList(labels);
    }

    /**
     * @return selected tab index
     */
    public int selectedIndex() {
        return selectedIndex;
    }

    /**
     * Selects a tab by index. Out-of-range values are clamped.
     *
     * @param selectedIndex requested selected index
     * @return this tab selector
     */
    public PandaTabs selectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        clampSelection();
        return this;
    }

    public int preferredWidth() {
        int width = 0;
        for (String label : labels) {
            width += Math.max(64, label.length() * 8 + 24);
        }
        return width;
    }

    public int preferredHeight() {
        return 28;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible() || !enabled()) {
            return false;
        }
        if (button != 0 || labels.isEmpty() || !bounds().contains(mouseX, mouseY)) {
            return false;
        }
        int tabWidth = Math.max(1, bounds().width / labels.size());
        selectedIndex = Math.min(labels.size() - 1, Math.max(0, (mouseX - bounds().x) / tabWidth));
        return true;
    }

    private void clampSelection() {
        if (labels.isEmpty()) {
            selectedIndex = 0;
            return;
        }
        selectedIndex = Math.max(0, Math.min(labels.size() - 1, selectedIndex));
    }
}
