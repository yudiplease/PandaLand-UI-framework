package land.pandaland.ui.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import land.pandaland.ui.api.PandaComponent;
import land.pandaland.ui.api.PandaLayout;
import land.pandaland.ui.api.PandaModal;
import land.pandaland.ui.api.PandaPanel;
import land.pandaland.ui.api.PandaRect;

public final class PandaScreenRuntime {
    public static final int ESCAPE_KEY_CODE = 1;

    private final PandaLayout root;
    private final List<PandaModal> modals = new ArrayList<PandaModal>();
    private PandaRect bounds = new PandaRect(0, 0, 0, 0);
    private PandaComponent focused;
    private PandaComponent hovered;
    private PandaComponent pressed;
    private int pressedButton = -1;

    public PandaScreenRuntime(PandaLayout root) {
        if (root == null) {
            throw new IllegalArgumentException("root cannot be null");
        }
        this.root = root;
    }

    public PandaLayout root() {
        return root;
    }

    public List<PandaModal> modals() {
        return Collections.unmodifiableList(new ArrayList<PandaModal>(modals));
    }

    public int modalCount() {
        return modals.size();
    }

    public void layout(PandaRect bounds) {
        this.bounds = bounds == null ? new PandaRect(0, 0, 0, 0) : bounds;
        root.layout(this.bounds);
        for (PandaModal modal : modals) {
            layoutModal(modal);
        }
    }

    public void showModal(PandaModal modal) {
        if (modal == null) {
            throw new IllegalArgumentException("modal cannot be null");
        }
        if (modal.closed() || modals.contains(modal)) {
            return;
        }
        modals.add(modal);
        layoutModal(modal);
        setFocused(null);
        setHovered(null);
        setPressed(null);
    }

    public boolean closeTopModal() {
        while (!modals.isEmpty()) {
            PandaModal modal = modals.remove(modals.size() - 1);
            if (modal.open()) {
                modal.close();
                setFocused(null);
                setHovered(null);
                setPressed(null);
                return true;
            }
        }
        return false;
    }

    public void clearModals() {
        while (!modals.isEmpty()) {
            PandaModal modal = modals.remove(modals.size() - 1);
            if (modal.open()) {
                modal.close();
            }
        }
        setFocused(null);
        setHovered(null);
        setPressed(null);
    }

    public void update(long deltaMs) {
        PandaUiErrorHandler.update(root, deltaMs);
        for (PandaModal modal : new ArrayList<PandaModal>(modals)) {
            if (modal.open() && modal.visible()) {
                PandaUiErrorHandler.update(modal, deltaMs);
            }
        }
    }

    public void mouseMoved(int mouseX, int mouseY) {
        setHovered(hitTest(activeInputRoot(), mouseX, mouseY));
    }

    public boolean mousePressed(int mouseX, int mouseY, int button) {
        mouseMoved(mouseX, mouseY);
        PandaComponent target = hovered;
        if (target == null) {
            setFocused(null);
            setPressed(null);
            return hasActiveModal();
        }

        if (target.focusable()) {
            setFocused(target);
        } else {
            setFocused(null);
        }
        setPressed(target);
        pressedButton = button;
        return PandaUiErrorHandler.mousePressed(target, mouseX, mouseY, button) || hasActiveModal();
    }

    public boolean mouseReleased(int mouseX, int mouseY, int button) {
        PandaComponent target = pressed;
        if (target == null || button != pressedButton) {
            setPressed(null);
            return hasActiveModal();
        }

        boolean inside = target.visible() && target.enabled() && target.bounds().contains(mouseX, mouseY);
        boolean handled = PandaUiErrorHandler.mouseReleased(target, mouseX, mouseY, button);
        if (inside) {
            handled = PandaUiErrorHandler.mouseClicked(target, mouseX, mouseY, button) || handled;
        }
        setPressed(null);
        mouseMoved(mouseX, mouseY);
        return handled || hasActiveModal();
    }

    public boolean mouseDragged(int mouseX, int mouseY, int button, long dragTimeMs) {
        PandaComponent target = pressed;
        if (target == null || button != pressedButton || !target.visible() || !target.enabled()) {
            return hasActiveModal();
        }
        setHovered(hitTest(activeInputRoot(), mouseX, mouseY));
        return PandaUiErrorHandler.mouseDragged(target, mouseX, mouseY, button, dragTimeMs) || hasActiveModal();
    }

    public boolean keyTyped(char character, int keyCode) {
        if (keyCode == ESCAPE_KEY_CODE && closeTopModal()) {
            return true;
        }

        PandaComponent inputRoot = activeInputRoot();
        if (focused != null && focused.visible() && focused.enabled() && containsComponent(inputRoot, focused)) {
            if (PandaUiErrorHandler.keyTyped(focused, character, keyCode)) {
                return true;
            }
        }
        return inputRoot != null && PandaUiErrorHandler.keyTyped(inputRoot, character, keyCode);
    }

    private PandaComponent activeInputRoot() {
        for (int i = modals.size() - 1; i >= 0; i--) {
            PandaModal modal = modals.get(i);
            if (modal.open() && modal.visible() && modal.enabled()) {
                return modal;
            }
        }
        return root;
    }

    private boolean hasActiveModal() {
        return activeInputRoot() instanceof PandaModal;
    }

    private void layoutModal(PandaModal modal) {
        int width = Math.min(Math.max(1, modal.preferredWidth()), bounds.width);
        int height = Math.min(Math.max(1, modal.preferredHeight()), bounds.height);
        int x = bounds.x + (bounds.width - width) / 2;
        int y = bounds.y + (bounds.height - height) / 2;
        modal.layout(new PandaRect(x, y, width, height));
    }

    private void setHovered(PandaComponent component) {
        if (hovered == component) {
            return;
        }
        if (hovered != null) {
            hovered.setHovered(false);
        }
        hovered = component;
        if (hovered != null) {
            hovered.setHovered(true);
        }
    }

    private void setPressed(PandaComponent component) {
        if (pressed == component) {
            return;
        }
        if (pressed != null) {
            pressed.setPressed(false);
        }
        pressed = component;
        pressedButton = component == null ? -1 : pressedButton;
        if (pressed != null) {
            pressed.setPressed(true);
        }
    }

    private void setFocused(PandaComponent component) {
        if (focused == component) {
            return;
        }
        if (focused != null) {
            focused.blur();
        }
        focused = component;
        if (focused != null) {
            focused.focus();
        }
    }

    private static PandaComponent hitTest(PandaComponent component, int mouseX, int mouseY) {
        if (component == null || !component.visible() || !component.enabled()) {
            return null;
        }
        if (component instanceof PandaModal && !((PandaModal) component).open()) {
            return null;
        }

        List<PandaComponent> children = childrenOf(component);
        for (int i = children.size() - 1; i >= 0; i--) {
            PandaComponent hit = hitTest(children.get(i), mouseX, mouseY);
            if (hit != null) {
                return hit;
            }
        }
        return component.bounds().contains(mouseX, mouseY) ? component : null;
    }

    private static boolean containsComponent(PandaComponent root, PandaComponent candidate) {
        if (root == null || candidate == null || !root.visible()) {
            return false;
        }
        if (root == candidate) {
            return true;
        }
        List<PandaComponent> children = childrenOf(root);
        for (PandaComponent child : children) {
            if (containsComponent(child, candidate)) {
                return true;
            }
        }
        return false;
    }

    private static List<PandaComponent> childrenOf(PandaComponent component) {
        if (component instanceof PandaLayout) {
            return ((PandaLayout) component).children();
        }
        if (component instanceof PandaPanel) {
            return ((PandaPanel) component).content().children();
        }
        if (component instanceof PandaModal) {
            List<PandaComponent> children = new ArrayList<PandaComponent>(1);
            children.add(((PandaModal) component).panel());
            return children;
        }
        return Collections.emptyList();
    }
}
