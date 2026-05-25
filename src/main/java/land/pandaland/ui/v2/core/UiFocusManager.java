package land.pandaland.ui.v2.core;

public final class UiFocusManager {
    private UiNode focused;

    public UiNode focused() {
        return focused;
    }

    public void focus(UiNode node) {
        focused = node;
    }

    public void clear() {
        focused = null;
    }
}
