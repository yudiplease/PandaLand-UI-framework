package land.pandaland.ui.v2.core;

public final class UiScreen {
    private final String id;
    private final UiNode root;

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

    public String id() {
        return id;
    }

    public UiNode root() {
        return root;
    }
}
