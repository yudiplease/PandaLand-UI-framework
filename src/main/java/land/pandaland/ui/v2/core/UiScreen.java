package land.pandaland.ui.v2.core;

/**
 * Immutable entry point for one retained UI screen.
 *
 * <p>A screen has a stable id for diagnostics and a root node that contains the
 * complete retained UI tree. The root invalidation flag is cleared once the
 * screen is constructed.</p>
 */
public final class UiScreen {
    private final String id;
    private final UiNode root;

    /**
     * Creates a screen from an id and root node.
     *
     * @param id stable non-empty screen id
     * @param root root node for the retained UI tree
     * @throws IllegalArgumentException when {@code id} is empty or {@code root} is {@code null}
     */
    public UiScreen(String id, UiNode root) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("id cannot be empty");
        }
        if (root == null) {
            throw new IllegalArgumentException("root cannot be null");
        }
        this.id = id;
        this.root = root;
        this.root.clearInvalid();
    }

    /**
     * Returns the stable screen id.
     *
     * @return screen id
     */
    public String id() {
        return id;
    }

    /**
     * Returns the root node of the retained UI tree.
     *
     * @return root node
     */
    public UiNode root() {
        return root;
    }
}
