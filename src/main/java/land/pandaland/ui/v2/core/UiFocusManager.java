package land.pandaland.ui.v2.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks keyboard focus inside a retained UI tree.
 *
 * <p>The manager stores one focused node and supports tab traversal over visible,
 * enabled, focusable nodes in tree order.</p>
 */
public final class UiFocusManager {
    private UiNode focused;

    /**
     * Returns the currently focused node.
     *
     * @return focused node or {@code null}
     */
    public UiNode focused() {
        return focused;
    }

    /**
     * Sets the focused node.
     *
     * @param node node to focus, or {@code null} to clear focus
     */
    public void focus(UiNode node) {
        focused = node;
    }

    /**
     * Clears the current focus.
     */
    public void clear() {
        focused = null;
    }

    /**
     * Moves focus to the next or previous focusable node.
     *
     * @param root root node used as traversal boundary
     * @param reverse when {@code true}, traverse backward
     * @return {@code true} when focus was moved
     */
    public boolean focusNext(UiNode root, boolean reverse) {
        List<UiNode> focusable = new ArrayList<UiNode>();
        collect(root, focusable);
        if (focusable.isEmpty()) {
            clear();
            return false;
        }

        int index = focused == null ? -1 : focusable.indexOf(focused);
        int next = reverse
            ? (index <= 0 ? focusable.size() - 1 : index - 1)
            : (index < 0 || index >= focusable.size() - 1 ? 0 : index + 1);
        focus(focusable.get(next));
        return true;
    }

    private static void collect(UiNode node, List<UiNode> focusable) {
        if (node == null || !node.visible() || !node.enabled()) {
            return;
        }
        if (node.focusable()) {
            focusable.add(node);
        }
        for (UiNode child : node.children()) {
            collect(child, focusable);
        }
    }
}
