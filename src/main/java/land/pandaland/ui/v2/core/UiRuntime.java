package land.pandaland.ui.v2.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import land.pandaland.ui.v2.event.UiEventDispatcher;
import land.pandaland.ui.v2.layout.UiLayoutEngine;
import land.pandaland.ui.v2.layout.UiLayoutStyle;
import land.pandaland.ui.v2.layout.UiRect;

public final class UiRuntime {
    private final UiScreen screen;
    private final List<UiNode> modals = new ArrayList<UiNode>();
    private final List<UiNode> toasts = new ArrayList<UiNode>();
    private final UiFocusManager focusManager = new UiFocusManager();
    private final UiEventDispatcher eventDispatcher;
    private UiRect lastBounds = new UiRect(0, 0, 0, 0);

    public UiRuntime(UiScreen screen) {
        if (screen == null) {
            throw new IllegalArgumentException("screen cannot be null");
        }
        this.screen = screen;
        this.eventDispatcher = new UiEventDispatcher(this, focusManager);
    }

    public UiScreen screen() {
        return screen;
    }

    public boolean invalid() {
        return screen.root().invalid();
    }

    public UiEventDispatcher events() {
        return eventDispatcher;
    }

    public UiFocusManager focus() {
        return focusManager;
    }

    public void layout(UiRect bounds) {
        lastBounds = bounds == null ? new UiRect(0, 0, 0, 0) : bounds;
        layoutNode(screen.root(), lastBounds);
        for (UiNode modal : modals) {
            layoutNode(modal, centered(lastBounds));
        }
        int toastY = 8;
        for (UiNode toast : toasts) {
            layoutNode(toast, new UiRect(8, toastY, 160, 22));
            toastY += 26;
        }
        screen.root().clearInvalid();
    }

    public void update(long deltaMs) {
        Iterator<UiNode> iterator = toasts.iterator();
        while (iterator.hasNext()) {
            UiNode toast = iterator.next();
            toast.updateElapsed(deltaMs);
            if (toast.expired()) {
                iterator.remove();
            }
        }
    }

    public void showModal(UiNode modal) {
        if (modal == null) {
            throw new IllegalArgumentException("modal cannot be null");
        }
        modals.add(modal);
        layoutNode(modal, centered(lastBounds));
    }

    public boolean closeTopModal() {
        if (modals.isEmpty()) {
            return false;
        }
        modals.remove(modals.size() - 1);
        return true;
    }

    public int modalCount() {
        return modals.size();
    }

    public List<UiNode> modals() {
        return Collections.unmodifiableList(modals);
    }

    public UiNode activeInputRoot() {
        if (!modals.isEmpty()) {
            return modals.get(modals.size() - 1);
        }
        return screen.root();
    }

    public void toast(String message, long durationMs) {
        toasts.add(new UiNode(UiNode.Type.TOAST).layoutStyle(UiLayoutStyle.leaf().size(160, 22)).text(message).durationMs(durationMs));
    }

    public int toastCount() {
        return toasts.size();
    }

    public List<UiNode> toasts() {
        return Collections.unmodifiableList(toasts);
    }

    private void layoutNode(UiNode node, UiRect bounds) {
        node.bounds(bounds);
        List<UiNode> children = node.children();
        if (children.isEmpty()) {
            return;
        }
        List<UiLayoutStyle> childStyles = new ArrayList<UiLayoutStyle>();
        for (UiNode child : children) {
            childStyles.add(child.layoutStyle());
        }
        UiLayoutEngine.Result result = UiLayoutEngine.layout(node.layoutStyle(), bounds, childStyles);
        for (int i = 0; i < children.size(); i++) {
            layoutNode(children.get(i), result.children().get(i));
        }
    }

    private static UiRect centered(UiRect bounds) {
        UiRect safeBounds = bounds == null ? new UiRect(0, 0, 0, 0) : bounds;
        int width = Math.min(180, safeBounds.width);
        int height = Math.min(90, safeBounds.height);
        return new UiRect(safeBounds.x + (safeBounds.width - width) / 2, safeBounds.y + (safeBounds.height - height) / 2, width, height);
    }
}
