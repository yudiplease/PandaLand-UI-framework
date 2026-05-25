package land.pandaland.ui.v2.event;

import java.util.List;
import land.pandaland.ui.v2.core.UiFocusManager;
import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiRuntime;

public final class UiEventDispatcher {
    private final UiRuntime runtime;
    private final UiFocusManager focusManager;
    private UiNode pressed;
    private int pressedButton = -1;

    public UiEventDispatcher(UiRuntime runtime, UiFocusManager focusManager) {
        if (runtime == null) {
            throw new IllegalArgumentException("runtime cannot be null");
        }
        if (focusManager == null) {
            throw new IllegalArgumentException("focusManager cannot be null");
        }
        this.runtime = runtime;
        this.focusManager = focusManager;
    }

    public boolean pointerDown(int x, int y, int button) {
        UiNode inputRoot = runtime.activeInputRoot();
        UiNode target = hitTest(inputRoot, x, y);
        pressed = target;
        pressedButton = target == null ? -1 : button;
        if (target == null) {
            focusManager.clear();
            return inputRoot != runtime.screen().root();
        }
        if (target.focusable()) {
            focusManager.focus(target);
        } else {
            focusManager.clear();
        }
        if (target.type() == UiNode.Type.SLIDER) {
            updateSliderValue(target, x);
        }
        return true;
    }

    public boolean pointerUp(int x, int y, int button) {
        if (pressed == null || button != pressedButton) {
            clearPressed();
            return runtime.activeInputRoot() != runtime.screen().root();
        }
        UiNode target = pressed;
        boolean handled = false;
        if (target.visible() && target.enabled() && target.bounds().contains(x, y) && target.clickAction() != null) {
            target.clickAction().run();
            handled = true;
        }
        clearPressed();
        return handled;
    }

    public boolean pointerDrag(int x, int y, int button, long dragTimeMs) {
        if (pressed == null || button != pressedButton || !pressed.visible() || !pressed.enabled()) {
            return false;
        }
        if (pressed.dragAction() != null) {
            pressed.dragAction().run();
            return true;
        }
        if (pressed.type() == UiNode.Type.SLIDER) {
            updateSliderValue(pressed, x);
            return true;
        }
        return false;
    }

    public boolean keyTyped(char character, int keyCode) {
        return false;
    }

    private void clearPressed() {
        pressed = null;
        pressedButton = -1;
    }

    private static UiNode hitTest(UiNode node, int x, int y) {
        if (node == null || !node.visible() || !node.enabled()) {
            return null;
        }
        List<UiNode> children = node.children();
        for (int i = children.size() - 1; i >= 0; i--) {
            UiNode hit = hitTest(children.get(i), x, y);
            if (hit != null) {
                return hit;
            }
        }
        return node.bounds().contains(x, y) ? node : null;
    }

    private static void updateSliderValue(UiNode node, int x) {
        if (node.valueState() == null || node.bounds().width <= 0) {
            return;
        }
        float value = (float) (x - node.bounds().x) / (float) node.bounds().width;
        value = Math.max(0.0F, Math.min(1.0F, value));
        node.valueState().set(Float.valueOf(value));
    }
}
