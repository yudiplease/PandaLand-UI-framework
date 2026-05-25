package land.pandaland.ui.v2.event;

import java.util.List;

import org.lwjgl.input.Keyboard;

import land.pandaland.ui.v2.core.UiFocusManager;
import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiRuntime;

/**
 * Dispatches raw pointer and keyboard input to retained UI nodes.
 *
 * <p>The dispatcher handles hit testing, focus changes, button clicks, checkbox
 * toggles, select/list/tab selection, slider dragging, scroll wheel movement,
 * and text input editing with selection, clipboard shortcuts, validation, and
 * caret scrolling.</p>
 */
public final class UiEventDispatcher {
    private final UiRuntime runtime;
    private final UiFocusManager focusManager;

    private UiNode pressed;
    private int pressedButton = -1;
    private int lastDragX;
    private int lastDragY;

    /**
     * Creates a dispatcher for one runtime and focus manager.
     *
     * @param runtime runtime that owns the active input tree
     * @param focusManager focus manager to update during input
     */
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

    /**
     * Handles mouse button press.
     *
     * @param x pointer x coordinate in scaled GUI pixels
     * @param y pointer y coordinate in scaled GUI pixels
     * @param button mouse button id
     * @return {@code true} when the event is consumed
     */
    public boolean pointerDown(int x, int y, int button) {
        UiNode inputRoot = runtime.activeInputRoot();
        UiNode target = hitTest(inputRoot, x, y);

        pressed = target;
        pressedButton = target == null ? -1 : button;
        lastDragX = x;
        lastDragY = y;

        if (target == null) {
            focusManager.clear();
            return inputRoot != runtime.screen().root();
        }

        if (target.focusable()) {
            focusManager.focus(target);

            if (target.type() == UiNode.Type.TEXT_INPUT) {
                int cursor = cursorFromPointer(target, x);
                target.cursorPosition(cursor).selection(cursor, cursor);
            }
        } else {
            focusManager.clear();
        }

        if (target.type() == UiNode.Type.SLIDER) {
            updateSliderValue(target, x);
        }

        return true;
    }

    /**
     * Handles mouse button release.
     *
     * @param x pointer x coordinate in scaled GUI pixels
     * @param y pointer y coordinate in scaled GUI pixels
     * @param button mouse button id
     * @return {@code true} when the event is consumed
     */
    public boolean pointerUp(int x, int y, int button) {
        if (pressed == null || button != pressedButton) {
            clearPressed();
            return runtime.activeInputRoot() != runtime.screen().root();
        }

        UiNode target = pressed;
        boolean handled = false;

        if (target.visible()
                && target.enabled()
                && target.bounds().contains(x, y)) {
            if (target.type() == UiNode.Type.CHECKBOX) {
                toggleCheckbox(target);
                handled = true;
            }

            if (target.type() == UiNode.Type.SELECT) {
                target.open(!target.open());
                handled = true;
            }

            if (target.type() == UiNode.Type.LIST || target.type() == UiNode.Type.TABS) {
                updateSelectedIndex(target, x, y);
                handled = true;
            }

            if (target.clickAction() != null) {
                target.clickAction().run();
                handled = true;
            }
        }

        clearPressed();
        return handled;
    }

    /**
     * Handles pointer dragging for the pressed node.
     *
     * @param x current pointer x coordinate
     * @param y current pointer y coordinate
     * @param button mouse button id
     * @param dragTimeMs drag duration in milliseconds
     * @return {@code true} when the event is consumed
     */
    public boolean pointerDrag(int x, int y, int button, long dragTimeMs) {
        if (pressed == null || button != pressedButton || !pressed.visible() || !pressed.enabled()) {
            return false;
        }

        int deltaX = x - lastDragX;
        int deltaY = y - lastDragY;
        lastDragX = x;
        lastDragY = y;

        if (pressed.type() == UiNode.Type.TEXT_INPUT) {
            int cursor = cursorFromPointer(pressed, x);
            pressed.cursorPosition(cursor).selectionEnd(cursor);
            return true;
        }

        if (pressed.dragHandler() != null) {
            pressed.dragHandler().onDrag(new UiPointerEvent(x, y, button, deltaX, deltaY, dragTimeMs));
            return true;
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

    /**
     * Handles mouse wheel scrolling over scroll containers.
     *
     * @param x pointer x coordinate
     * @param y pointer y coordinate
     * @param amount wheel delta reported by Minecraft
     * @return {@code true} when a scroll container consumed the event
     */
    public boolean pointerWheel(int x, int y, int amount) {
        UiNode target = hitTest(runtime.activeInputRoot(), x, y);
        UiNode scroll = scrollAncestor(target);
        if (scroll == null) {
            return false;
        }
        scroll.scrollY(scroll.scrollY() - amount / 12);
        scroll.invalidate();
        return true;
    }

    /**
     * Handles a keyboard event for focus traversal or focused text input.
     *
     * @param character typed character
     * @param keyCode LWJGL/Minecraft key code
     * @return {@code true} when the event is consumed
     */
    public boolean keyTyped(char character, int keyCode) {
        UiNode focused = focusManager.focused();

        if (keyCode == Keyboard.KEY_TAB) {
            boolean reverse = keyDown(Keyboard.KEY_LSHIFT) || keyDown(Keyboard.KEY_RSHIFT);
            return focusManager.focusNext(runtime.activeInputRoot(), reverse);
        }

        if (focused == null || !focused.enabled() || !focused.visible()) {
            return false;
        }

        if (focused.type() != UiNode.Type.TEXT_INPUT) {
            return false;
        }

        return handleTextInputKey(focused, character, keyCode);
    }

    private boolean handleTextInputKey(UiNode node, char character, int keyCode) {
        String value = textValue(node);
        int cursor = clamp(node.cursorPosition(), 0, value.length());
        node.cursorPosition(cursor);

        boolean ctrl = keyDown(Keyboard.KEY_LCONTROL) || keyDown(Keyboard.KEY_RCONTROL);
        boolean shift = keyDown(Keyboard.KEY_LSHIFT) || keyDown(Keyboard.KEY_RSHIFT);

        if (ctrl && keyCode == Keyboard.KEY_A) {
            node.cursorPosition(value.length()).selection(0, value.length());
            ensureCursorVisible(node);
            return true;
        }

        if (ctrl && keyCode == Keyboard.KEY_C) {
            if (node.hasSelection()) {
                setClipboard(value.substring(node.selectionMin(), node.selectionMax()));
            }
            return true;
        }

        if (ctrl && keyCode == Keyboard.KEY_V) {
            String pasted = clipboard();
            if (pasted == null || pasted.length() == 0) {
                return true;
            }

            pasted = filterAllowedText(pasted);
            if (pasted.length() == 0) {
                return true;
            }

            replaceSelectionOrInsert(node, value, pasted);
            return true;
        }

        if (keyCode == Keyboard.KEY_BACK) {
            if (deleteSelection(node, value)) {
                return true;
            }
            if (cursor <= 0) {
                return true;
            }

            String next = value.substring(0, cursor - 1) + value.substring(cursor);
            setTextValue(node, next);
            node.cursorPosition(cursor - 1);
            return true;
        }

        if (keyCode == Keyboard.KEY_DELETE) {
            if (deleteSelection(node, value)) {
                return true;
            }
            if (cursor >= value.length()) {
                return true;
            }

            String next = value.substring(0, cursor) + value.substring(cursor + 1);
            setTextValue(node, next);
            node.cursorPosition(cursor);
            return true;
        }

        if (keyCode == Keyboard.KEY_LEFT) {
            moveCursor(node, Math.max(0, cursor - 1), shift);
            return true;
        }

        if (keyCode == Keyboard.KEY_RIGHT) {
            moveCursor(node, Math.min(value.length(), cursor + 1), shift);
            return true;
        }

        if (keyCode == Keyboard.KEY_HOME) {
            moveCursor(node, 0, shift);
            return true;
        }

        if (keyCode == Keyboard.KEY_END) {
            moveCursor(node, value.length(), shift);
            return true;
        }

        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            if (node.enterAction() != null) {
                node.enterAction().run();
            }
            return true;
        }

        if (keyCode == Keyboard.KEY_ESCAPE) {
            focusManager.clear();
            return true;
        }

        if (isAllowedCharacter(character)) {
            replaceSelectionOrInsert(node, value, String.valueOf(character));
            return true;
        }

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

    private static UiNode scrollAncestor(UiNode node) {
        UiNode current = node;
        while (current != null) {
            if (current.type() == UiNode.Type.SCROLL_CONTAINER) {
                return current;
            }
            current = current.parent();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static void updateSliderValue(UiNode node, int x) {
        if (node.valueState() == null || node.bounds().width <= 0) {
            return;
        }

        float value = (float) (x - node.bounds().x) / (float) node.bounds().width;
        value = Math.max(0.0F, Math.min(1.0F, value));

        node.valueState().set(Float.valueOf(value));
    }

    private static String textValue(UiNode node) {
        if (node.valueState() == null || node.valueState().get() == null) {
            return "";
        }

        Object value = node.valueState().get();
        return value == null ? "" : String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    private static void setTextValue(UiNode node, String value) {
        String next = value == null ? "" : value;
        if (node.validator() != null) {
            UiValidationResult result = node.validator().validate(next);
            if (result != null && !result.valid()) {
                node.validationMessage(result.message());
                return;
            }
            node.validationMessage("");
        }
        if (node.valueState() != null) {
            node.valueState().set(next);
        }
        if (node.changeAction() != null) {
            node.changeAction().run();
        }
        node.invalidate();
    }

    private static void replaceSelectionOrInsert(UiNode node, String value, String insert) {
        int cursor = clamp(node.cursorPosition(), 0, value.length());
        int start = node.hasSelection() ? node.selectionMin() : cursor;
        int end = node.hasSelection() ? node.selectionMax() : cursor;
        int allowed = Math.max(0, node.maxLength() - (value.length() - (end - start)));
        String normalizedInsert = insert == null ? "" : insert;
        if (normalizedInsert.length() > allowed) {
            normalizedInsert = normalizedInsert.substring(0, allowed);
        }
        String next = value.substring(0, start) + normalizedInsert + value.substring(end);
        setTextValue(node, next);
        int nextCursor = start + normalizedInsert.length();
        node.cursorPosition(nextCursor).selection(nextCursor, nextCursor);
        ensureCursorVisible(node);
    }

    private static boolean deleteSelection(UiNode node, String value) {
        if (!node.hasSelection()) {
            return false;
        }
        int start = node.selectionMin();
        int end = node.selectionMax();
        String next = value.substring(0, start) + value.substring(end);
        setTextValue(node, next);
        node.cursorPosition(start).selection(start, start);
        ensureCursorVisible(node);
        return true;
    }

    private static void moveCursor(UiNode node, int nextCursor, boolean select) {
        int previous = node.cursorPosition();
        node.cursorPosition(nextCursor);
        if (select) {
            if (!node.hasSelection()) {
                node.selection(previous, nextCursor);
            } else {
                node.selectionEnd(nextCursor);
            }
        } else {
            node.selection(nextCursor, nextCursor);
        }
        ensureCursorVisible(node);
    }

    private static String insertText(String value, int cursor, String insert, int maxLength) {
        if (value == null) {
            value = "";
        }
        if (insert == null || insert.length() == 0) {
            return value;
        }

        cursor = clamp(cursor, 0, value.length());

        int allowed = Math.max(0, maxLength - value.length());
        if (allowed <= 0) {
            return value;
        }

        if (insert.length() > allowed) {
            insert = insert.substring(0, allowed);
        }

        return value.substring(0, cursor) + insert + value.substring(cursor);
    }

    private static boolean isAllowedCharacter(char character) {
        return character >= 32 && character != 127;
    }

    private static String filterAllowedText(String text) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (isAllowedCharacter(c)) {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    private static String clipboard() {
        try {
            return net.minecraft.client.gui.GuiScreen.getClipboardString();
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static void setClipboard(String text) {
        try {
            net.minecraft.client.gui.GuiScreen.setClipboardString(text == null ? "" : text);
        } catch (Throwable ignored) {
        }
    }

    private static int cursorFromPointer(UiNode node, int x) {
        String value = textValue(node);
        int local = Math.max(0, x - node.bounds().x - 5 + node.horizontalScroll());
        int cursor = Math.round(local / 6.0F);
        return clamp(cursor, 0, value.length());
    }

    private static void ensureCursorVisible(UiNode node) {
        int contentWidth = Math.max(1, node.bounds().width - 10);
        int cursorX = node.cursorPosition() * 6;
        if (cursorX - node.horizontalScroll() > contentWidth) {
            node.horizontalScroll(cursorX - contentWidth);
        } else if (cursorX < node.horizontalScroll()) {
            node.horizontalScroll(cursorX);
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static boolean keyDown(int keyCode) {
        try {
            return Keyboard.isCreated() && Keyboard.isKeyDown(keyCode);
        } catch (IllegalStateException ignored) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private static void toggleCheckbox(UiNode node) {
        if (node.valueState() == null) {
            return;
        }

        Object raw = node.valueState().get();
        boolean current = raw instanceof Boolean && ((Boolean) raw).booleanValue();

        node.valueState().set(Boolean.valueOf(!current));
        node.invalidate();
    }

    @SuppressWarnings("unchecked")
    private static void updateSelectedIndex(UiNode node, int x, int y) {
        if (node.valueState() == null || node.children().isEmpty()) {
            return;
        }
        int index = 0;
        for (int i = 0; i < node.children().size(); i++) {
            UiNode child = node.children().get(i);
            if (child.bounds().contains(x, y)) {
                index = i;
                break;
            }
        }
        node.valueState().set(Integer.valueOf(index));
        if (node.changeAction() != null) {
            node.changeAction().run();
        }
        node.invalidate();
    }
}
