package land.pandaland.ui.v2.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import land.pandaland.ui.v2.data.UiModalOptions;
import land.pandaland.ui.v2.data.UiToastOptions;
import land.pandaland.ui.v2.event.UiEventDispatcher;
import land.pandaland.ui.v2.event.UiShortcut;
import land.pandaland.ui.v2.layout.UiLayoutEngine;
import land.pandaland.ui.v2.layout.UiLayoutStyle;
import land.pandaland.ui.v2.layout.UiRect;

/**
 * Runtime state for an opened retained UI screen.
 *
 * <p>The runtime owns focus, event dispatching, modal/toast stacks, elapsed
 * animation time, and the last applied layout bounds. It is intentionally
 * renderer-independent so Forge adapters can reuse the same model.</p>
 */
public final class UiRuntime {
    private final UiScreen screen;
    private final List<UiNode> modals = new ArrayList<UiNode>();
    private final List<UiNode> toasts = new ArrayList<UiNode>();
    private final List<UiShortcut> shortcuts = new ArrayList<UiShortcut>();
    private final UiFocusManager focusManager = new UiFocusManager();
    private final UiEventDispatcher eventDispatcher;
    private UiRect lastBounds = new UiRect(0, 0, 0, 0);
    private long elapsedMs;

    /**
     * Creates runtime state for a screen.
     *
     * @param screen screen to run
     * @throws IllegalArgumentException when {@code screen} is {@code null}
     */
    public UiRuntime(UiScreen screen) {
        if (screen == null) {
            throw new IllegalArgumentException("screen cannot be null");
        }
        this.screen = screen;
        this.eventDispatcher = new UiEventDispatcher(this, focusManager);
    }

    /**
     * Returns the screen controlled by this runtime.
     *
     * @return screen instance
     */
    public UiScreen screen() {
        return screen;
    }

    /**
     * Reports whether the root tree needs layout/render refresh.
     *
     * @return {@code true} when the root node is invalid
     */
    public boolean invalid() {
        return screen.root().invalid();
    }

    /**
     * Returns the event dispatcher bound to this runtime.
     *
     * @return event dispatcher
     */
    public UiEventDispatcher events() {
        return eventDispatcher;
    }

    /**
     * Returns the focus manager bound to this runtime.
     *
     * @return focus manager
     */
    public UiFocusManager focus() {
        return focusManager;
    }

    /**
     * Registers a runtime-wide keyboard shortcut.
     *
     * <p>Runtime shortcuts are evaluated before root-node shortcuts and focused
     * text input editing.</p>
     *
     * @param shortcut shortcut to register
     * @return this runtime
     */
    public UiRuntime registerShortcut(UiShortcut shortcut) {
        if (shortcut == null) {
            throw new IllegalArgumentException("shortcut cannot be null");
        }
        shortcuts.add(shortcut);
        return this;
    }

    /**
     * Returns immutable runtime-wide shortcut registrations.
     *
     * @return registered shortcuts
     */
    public List<UiShortcut> shortcuts() {
        return Collections.unmodifiableList(shortcuts);
    }

    /**
     * Applies layout to the root tree, active modals, and toasts.
     *
     * @param bounds available screen bounds in scaled GUI pixels
     */
    public void layout(UiRect bounds) {
        lastBounds = bounds == null ? new UiRect(0, 0, 0, 0) : bounds;
        layoutNode(screen.root(), lastBounds);
        for (UiNode modal : modals) {
            layoutNode(modal, centered(lastBounds, modal));
        }
        int toastY = 8;
        for (UiNode toast : toasts) {
            int width = toast.toastOptions() == null ? 160 : toast.toastOptions().width();
            int height = toast.toastOptions() == null ? 22 : toast.toastOptions().height();
            layoutNode(toast, new UiRect(8, toastY, width, height));
            toastY += height + 4;
        }
        screen.root().clearInvalid();
    }

    /**
     * Advances runtime time and expires finished toasts.
     *
     * @param deltaMs elapsed milliseconds since the previous update
     */
    public void update(long deltaMs) {
        elapsedMs += Math.max(0L, deltaMs);
        Iterator<UiNode> iterator = toasts.iterator();
        while (iterator.hasNext()) {
            UiNode toast = iterator.next();
            toast.updateElapsed(deltaMs);
            if (toast.expired()) {
                iterator.remove();
            }
        }
    }

    /**
     * Returns accumulated runtime time in milliseconds.
     *
     * @return elapsed milliseconds
     */
    public long elapsedMs() {
        return elapsedMs;
    }

    /**
     * Shows a modal node above the screen root.
     *
     * @param modal modal node to add
     */
    public void showModal(UiNode modal) {
        if (modal == null) {
            throw new IllegalArgumentException("modal cannot be null");
        }
        modals.add(modal);
        layoutNode(modal, centered(lastBounds, modal));
    }

    /**
     * Closes the top-most modal.
     *
     * @return {@code true} when a modal was removed
     */
    public boolean closeTopModal() {
        if (modals.isEmpty()) {
            return false;
        }
        modals.remove(modals.size() - 1);
        return true;
    }

    /**
     * Returns the number of active modals.
     *
     * @return modal count
     */
    public int modalCount() {
        return modals.size();
    }

    /**
     * Returns active modals in bottom-to-top order.
     *
     * @return immutable modal list
     */
    public List<UiNode> modals() {
        return Collections.unmodifiableList(modals);
    }

    /**
     * Returns the tree that should receive input.
     *
     * @return top modal when present, otherwise the screen root
     */
    public UiNode activeInputRoot() {
        if (!modals.isEmpty()) {
            return modals.get(modals.size() - 1);
        }
        return screen.root();
    }

    /**
     * Adds a toast notification.
     *
     * @param message visible toast text
     * @param durationMs lifetime in milliseconds
     */
    public void toast(String message, long durationMs) {
        toast(new UiToastOptions(message, durationMs));
    }

    /**
     * Adds a toast notification with public display options.
     *
     * @param options toast options
     */
    public void toast(UiToastOptions options) {
        UiToastOptions safeOptions = options == null ? new UiToastOptions("", 0L) : options;
        toasts.add(new UiNode(UiNode.Type.TOAST)
                .layoutStyle(UiLayoutStyle.leaf().size(safeOptions.width(), safeOptions.height()))
                .text(safeOptions.message())
                .durationMs(safeOptions.durationMs())
                .toastOptions(safeOptions));
    }

    /**
     * Returns the number of visible toast nodes.
     *
     * @return toast count
     */
    public int toastCount() {
        return toasts.size();
    }

    /**
     * Returns active toast nodes.
     *
     * @return immutable toast list
     */
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
            UiRect childBounds = result.children().get(i);
            if (node.type() == UiNode.Type.SCROLL_CONTAINER) {
                childBounds = new UiRect(childBounds.x - node.scrollX(), childBounds.y - node.scrollY(), childBounds.width, childBounds.height);
            }
            UiNode child = children.get(i);
            if (child.type() == UiNode.Type.CONTEXT_MENU && child.open()) {
                childBounds = new UiRect(child.openX(), child.openY(), childBounds.width, childBounds.height);
            }
            layoutNode(child, childBounds);
        }
    }

    private static UiRect centered(UiRect bounds, UiNode modal) {
        UiRect safeBounds = bounds == null ? new UiRect(0, 0, 0, 0) : bounds;
        UiModalOptions options = modal == null ? null : modal.modalOptions();
        int preferredWidth = options == null ? 180 : options.width();
        int preferredHeight = options == null ? 90 : options.height();
        int width = Math.min(preferredWidth, safeBounds.width);
        int height = Math.min(preferredHeight, safeBounds.height);
        return new UiRect(safeBounds.x + (safeBounds.width - width) / 2, safeBounds.y + (safeBounds.height - height) / 2, width, height);
    }
}
