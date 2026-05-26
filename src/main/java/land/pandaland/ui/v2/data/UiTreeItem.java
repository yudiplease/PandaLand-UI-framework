package land.pandaland.ui.v2.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable tree item descriptor with nested children.
 */
public final class UiTreeItem {
    private final String id;
    private final String label;
    private final List<UiTreeItem> children;
    private final boolean expanded;
    private final boolean disabled;

    /**
     * Creates an enabled collapsed tree item without children.
     *
     * @param id stable item id
     * @param label visible item label
     */
    public UiTreeItem(String id, String label) {
        this(id, label, Collections.<UiTreeItem>emptyList(), false, false);
    }

    /**
     * Creates an enabled expanded tree item with children.
     *
     * @param id stable item id
     * @param label visible item label
     * @param children child items
     */
    public UiTreeItem(String id, String label, List<UiTreeItem> children) {
        this(id, label, children, true, false);
    }

    /**
     * Creates a tree item.
     *
     * @param id stable item id
     * @param label visible item label
     * @param children child items
     * @param expanded whether children should initially be visible
     * @param disabled whether the item should reject selection
     */
    public UiTreeItem(String id, String label, List<UiTreeItem> children, boolean expanded, boolean disabled) {
        this.id = id == null ? "" : id;
        this.label = label == null ? "" : label;
        this.children = copy(children);
        this.expanded = expanded;
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
     * Returns immutable child items.
     *
     * @return child items, never {@code null}
     */
    public List<UiTreeItem> children() {
        return children;
    }

    /**
     * Reports whether children should initially be visible.
     *
     * @return expanded flag
     */
    public boolean expanded() {
        return expanded;
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
        if (!(other instanceof UiTreeItem)) {
            return false;
        }
        UiTreeItem that = (UiTreeItem) other;
        return expanded == that.expanded && disabled == that.disabled
            && id.equals(that.id) && label.equals(that.label) && children.equals(that.children);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + label.hashCode();
        result = 31 * result + children.hashCode();
        result = 31 * result + (expanded ? 1 : 0);
        result = 31 * result + (disabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UiTreeItem{id='" + id + "', label='" + label + "', children=" + children
            + ", expanded=" + expanded + ", disabled=" + disabled + "}";
    }

    private static List<UiTreeItem> copy(List<UiTreeItem> children) {
        if (children == null || children.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<UiTreeItem>(children));
    }
}
