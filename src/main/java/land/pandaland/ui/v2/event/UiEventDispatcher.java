package land.pandaland.ui.v2.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.lwjgl.input.Keyboard;

import land.pandaland.ui.v2.components.UiCustomComponent;
import land.pandaland.ui.v2.core.UiFocusManager;
import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.data.UiListItem;
import land.pandaland.ui.v2.data.UiTableRow;
import land.pandaland.ui.v2.data.UiTreeItem;
import land.pandaland.ui.v2.layout.UiRect;

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
    private int pressedTextSelectionAnchor = -1;

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
        if (closeContextMenusOutside(inputRoot, x, y)) {
            clearPressed();
            return true;
        }

        UiNode target = hitTestInteractive(inputRoot, x, y);

        pressed = target;
        pressedButton = target == null ? -1 : button;
        lastDragX = x;
        lastDragY = y;
        pressedTextSelectionAnchor = -1;

        if (target == null) {
            if (inputRoot != runtime.screen().root()) {
                trapFocus(inputRoot);
            } else {
                focusManager.clear();
            }
            return inputRoot != runtime.screen().root();
        }

        if (target.focusable()) {
            focusManager.focus(target);

            if (target.type() == UiNode.Type.TEXT_INPUT) {
                int cursor = cursorFromPointer(target, x);
                pressedTextSelectionAnchor = cursor;
                target.cursorPosition(cursor).selection(cursor, cursor);
            }
        } else {
            if (inputRoot != runtime.screen().root()) {
                trapFocus(inputRoot);
            } else {
                focusManager.clear();
            }
        }

        if (target.type() == UiNode.Type.SLIDER) {
            updateSliderValue(target, x);
        }

        if (dispatchCustomPointerDown(target, x, y, button)) {
            return true;
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

        try {
            UiNode target = pressed;
            boolean handled = false;

            if (target.visible()
                    && target.enabled()
                    && activationContains(target, x, y)) {
                if (target.type() == UiNode.Type.CHECKBOX) {
                    toggleCheckbox(target);
                    handled = true;
                }

                if (target.type() == UiNode.Type.SELECT) {
                    if (target.open() && selectMenuBounds(target).contains(x, y)) {
                        handled = updateSelection(target, x, y);
                        target.open(false);
                    } else {
                        target.open(!target.open());
                        handled = true;
                    }
                }

                UiNode selectionNode = selectionAncestor(target);
                if (selectionNode != null && selectionNode != target) {
                    handled = updateSelection(selectionNode, x, y) || handled;
                } else if (target.type() == UiNode.Type.LIST || target.type() == UiNode.Type.TABS
                        || target.type() == UiNode.Type.TABLE || target.type() == UiNode.Type.DATA_GRID || target.type() == UiNode.Type.TREE) {
                    handled = updateSelection(target, x, y) || handled;
                }

                if (target.type() == UiNode.Type.CONTEXT_MENU) {
                    handled = true;
                }

                handled = dispatchCustomPointerUp(target, x, y, button) || handled;

                if (target.clickAction() != null) {
                    target.clickAction().run();
                    handled = true;
                }
            }

            return handled;
        } finally {
            clearPressed();
        }
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

        if (dispatchCustomPointerDrag(pressed, x, y, button, deltaX, deltaY, dragTimeMs)) {
            return true;
        }

        if (pressed.type() == UiNode.Type.TEXT_INPUT) {
            int cursor = cursorFromPointer(pressed, x);
            int anchor = pressedTextSelectionAnchor < 0 ? cursor : pressedTextSelectionAnchor;
            pressed.selection(anchor, cursor).cursorPosition(cursor).selection(anchor, cursor);
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
        UiNode target = hitTestInteractive(runtime.activeInputRoot(), x, y);
        if (dispatchCustomPointerWheel(target, x, y, amount)) {
            return true;
        }
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
        if (keyCode == Keyboard.KEY_ESCAPE && handleModalEscape()) {
            return true;
        }

        UiNode focused = normalizeFocusedForActiveRoot();

        if (focused != null && focused.enabled() && focused.visible()
                && focused.type() == UiNode.Type.KEYBIND_INPUT) {
            return handleKeybindInputKey(focused, character, keyCode);
        }

        if (dispatchShortcut(character, keyCode, focused)) {
            return true;
        }

        if (keyCode == Keyboard.KEY_TAB) {
            boolean reverse = keyDown(Keyboard.KEY_LSHIFT) || keyDown(Keyboard.KEY_RSHIFT);
            return focusManager.focusNext(runtime.activeInputRoot(), reverse);
        }

        if (focused == null || !focused.enabled() || !focused.visible()) {
            return false;
        }

        if (dispatchCustomKeyTyped(focused, character, keyCode)) {
            return true;
        }

        if (focused.type() != UiNode.Type.TEXT_INPUT) {
            return false;
        }

        return handleTextInputKey(focused, character, keyCode);
    }

    private boolean handleModalEscape() {
        UiNode inputRoot = runtime.activeInputRoot();
        if (inputRoot == runtime.screen().root() || inputRoot.type() != UiNode.Type.MODAL) {
            return false;
        }
        if (inputRoot.modalOptions() != null && inputRoot.modalOptions().closeOnEscape()) {
            runtime.closeTopModal();
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean handleKeybindInputKey(UiNode node, char character, int keyCode) {
        String key = keyRepresentation(character, keyCode);
        if (node.valueState() != null) {
            node.valueState().set(key);
        }
        if (node.changeAction() != null) {
            node.changeAction().run();
        }
        node.invalidate();
        return true;
    }

    private boolean dispatchShortcut(char character, int keyCode, UiNode focused) {
        boolean ctrl = keyDown(Keyboard.KEY_LCONTROL) || keyDown(Keyboard.KEY_RCONTROL);
        boolean shift = keyDown(Keyboard.KEY_LSHIFT) || keyDown(Keyboard.KEY_RSHIFT);
        boolean alt = keyDown(Keyboard.KEY_LMENU) || keyDown(Keyboard.KEY_RMENU);
        boolean textInputPrintable = focused != null
                && focused.type() == UiNode.Type.TEXT_INPUT
                && isAllowedCharacter(character);

        if (dispatchShortcut(runtime.shortcuts(), keyCode, ctrl, shift, alt, textInputPrintable)) {
            return true;
        }

        return dispatchShortcut(runtime.activeInputRoot().shortcuts(), keyCode, ctrl, shift, alt, textInputPrintable);
    }

    private static boolean dispatchShortcut(List<UiShortcut> shortcuts, int keyCode, boolean ctrl, boolean shift, boolean alt, boolean textInputPrintable) {
        for (UiShortcut shortcut : shortcuts) {
            if (textInputPrintable && !shortcut.control() && !shortcut.shift() && !shortcut.alt()) {
                continue;
            }
            if (shortcut.matches(keyCode, ctrl, shift, alt)) {
                shortcut.run();
                return true;
            }
        }
        return false;
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

            pasted = filterAllowedText(pasted, node);
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
            if (node.textArea()) {
                replaceSelectionOrInsert(node, value, "\n");
                return true;
            }
            if (node.enterAction() != null) {
                node.enterAction().run();
            }
            return true;
        }

        if (keyCode == Keyboard.KEY_ESCAPE) {
            focusManager.clear();
            return true;
        }

        if (isAllowedCharacter(node, character)) {
            replaceSelectionOrInsert(node, value, String.valueOf(character));
            return true;
        }

        return false;
    }

    private void clearPressed() {
        pressed = null;
        pressedButton = -1;
        pressedTextSelectionAnchor = -1;
    }

    private static boolean dispatchCustomPointerDown(UiNode node, int x, int y, int button) {
        UiCustomComponent component = customComponent(node);
        if (component == null) {
            return false;
        }
        try {
            return component.pointerDown(new UiPointerEvent(x, y, button, 0L), node);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean dispatchCustomPointerUp(UiNode node, int x, int y, int button) {
        UiCustomComponent component = customComponent(node);
        if (component == null) {
            return false;
        }
        try {
            return component.pointerUp(new UiPointerEvent(x, y, button, 0L), node);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean dispatchCustomPointerDrag(UiNode node, int x, int y, int button, int deltaX, int deltaY, long dragTimeMs) {
        UiCustomComponent component = customComponent(node);
        if (component == null) {
            return false;
        }
        try {
            return component.pointerDrag(new UiPointerEvent(x, y, button, deltaX, deltaY, dragTimeMs), node);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean dispatchCustomPointerWheel(UiNode node, int x, int y, int amount) {
        UiCustomComponent component = customComponent(node);
        if (component == null) {
            return false;
        }
        try {
            return component.pointerWheel(new UiPointerEvent(x, y, amount, 0L), node);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean dispatchCustomKeyTyped(UiNode node, char character, int keyCode) {
        UiCustomComponent component = customComponent(node);
        if (component == null) {
            return false;
        }
        try {
            return component.keyTyped(new UiKeyEvent(character, keyCode), node);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static UiCustomComponent customComponent(UiNode node) {
        if (node == null || !(node.customComponent() instanceof UiCustomComponent)) {
            return null;
        }
        return (UiCustomComponent) node.customComponent();
    }

    private static UiNode hitTestInteractive(UiNode node, int x, int y) {
        UiNode openSelect = hitTestOpenSelect(node, x, y);
        if (openSelect != null) {
            return openSelect;
        }
        return hitTest(node, x, y);
    }

    private static UiNode hitTest(UiNode node, int x, int y) {
        if (node == null || !node.visible() || !node.enabled()) {
            return null;
        }
        if (node.type() == UiNode.Type.CONTEXT_MENU && !node.open()) {
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

    private static UiNode hitTestOpenSelect(UiNode node, int x, int y) {
        if (node == null || !node.visible() || !node.enabled()) {
            return null;
        }
        List<UiNode> children = node.children();
        for (int i = children.size() - 1; i >= 0; i--) {
            UiNode hit = hitTestOpenSelect(children.get(i), x, y);
            if (hit != null) {
                return hit;
            }
        }
        return node.type() == UiNode.Type.SELECT && node.open() && selectMenuBounds(node).contains(x, y) ? node : null;
    }

    private static boolean activationContains(UiNode node, int x, int y) {
        return node.bounds().contains(x, y)
                || (node.type() == UiNode.Type.SELECT && node.open() && selectMenuBounds(node).contains(x, y));
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
        if (node.numericInput()) {
            normalizedInsert = filterNumericInsert(value, start, end, normalizedInsert);
            if (normalizedInsert.length() == 0) {
                return;
            }
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

    private static boolean isAllowedCharacter(UiNode node, char character) {
        if (node.textArea() && character == '\n') {
            return true;
        }
        if (!isAllowedCharacter(character)) {
            return false;
        }
        if (!node.numericInput()) {
            return true;
        }
        return Character.isDigit(character) || character == '.' || character == '-' || character == '+';
    }

    private static String filterNumericInsert(String value, int start, int end, String insert) {
        StringBuilder accepted = new StringBuilder();
        String prefix = value.substring(0, start);
        String suffix = value.substring(end);
        for (int i = 0; i < insert.length(); i++) {
            String candidateInsert = accepted.toString() + insert.charAt(i);
            String candidate = prefix + candidateInsert + suffix;
            if (isValidNumericText(candidate)) {
                accepted.append(insert.charAt(i));
            }
        }
        return accepted.toString();
    }

    private static boolean isValidNumericText(String text) {
        if (text == null || text.length() == 0) {
            return true;
        }
        int index = 0;
        boolean decimalSeen = false;
        if (text.charAt(0) == '+' || text.charAt(0) == '-') {
            index = 1;
        }
        for (; index < text.length(); index++) {
            char c = text.charAt(index);
            if (Character.isDigit(c)) {
                continue;
            }
            if (c == '.' && !decimalSeen) {
                decimalSeen = true;
                continue;
            }
            return false;
        }
        return true;
    }

    private static String filterAllowedText(String text, UiNode node) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (isAllowedCharacter(node, c)) {
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
    private static boolean updateSelection(UiNode node, int x, int y) {
        Selection selection = selectionAt(node, x, y);
        if (selection == null) {
            return false;
        }
        if (selection.disabled) {
            return true;
        }

        if (node.multiSelect()) {
            List<String> selectedIds = new ArrayList<String>(node.selectedIds());
            if (selectedIds.contains(selection.id)) {
                selectedIds.remove(selection.id);
            } else {
                selectedIds.add(selection.id);
            }
            node.selectedIds(selectedIds);
        } else if (selection.id.length() > 0) {
            node.selectedIds(Collections.singletonList(selection.id));
        }

        if (node.valueState() != null) {
            Object current = node.valueState().get();
            if (current instanceof String) {
                node.valueState().set(selection.id);
            } else {
                node.valueState().set(Integer.valueOf(selection.index));
            }
        }

        if (node.changeAction() != null) {
            node.changeAction().run();
        }
        if (node.selectionHandler() != null) {
            node.selectionHandler().onSelect(selection.id, selection.index);
        }
        node.invalidate();
        return true;
    }

    private static Selection selectionAt(UiNode node, int x, int y) {
        if (node.type() == UiNode.Type.SELECT) {
            return selectSelectionAt(node, x, y);
        }
        if (node.type() == UiNode.Type.LIST) {
            return listSelectionAt(node, x, y);
        }
        if (node.type() == UiNode.Type.TABS) {
            return tabSelectionAt(node, x, y);
        }
        if (node.type() == UiNode.Type.TABLE || node.type() == UiNode.Type.DATA_GRID) {
            return tableSelectionAt(node, y);
        }
        if (node.type() == UiNode.Type.TREE) {
            return treeSelectionAt(node, y);
        }
        return null;
    }

    private static Selection selectSelectionAt(UiNode node, int x, int y) {
        UiRect menu = selectMenuBounds(node);
        if (!menu.contains(x, y)) {
            return null;
        }
        int index = (y - menu.y - 3) / 18;
        if (index < 0) {
            return null;
        }
        if (!node.items().isEmpty()) {
            if (index >= node.items().size()) {
                return null;
            }
            UiListItem item = node.items().get(index);
            return new Selection(item.id(), index, item.disabled());
        }
        if (index >= node.children().size()) {
            return null;
        }
        UiNode child = node.children().get(index);
        return new Selection(child.text(), index, !child.enabled());
    }

    private static Selection listSelectionAt(UiNode node, int x, int y) {
        if (!node.bounds().contains(x, y)) {
            return null;
        }
        if (!node.items().isEmpty()) {
            int rowHeight = Math.max(1, node.bounds().height / Math.max(1, node.items().size()));
            int index = (y - node.bounds().y) / rowHeight;
            if (index < 0 || index >= node.items().size()) {
                return null;
            }
            UiListItem item = node.items().get(index);
            return new Selection(item.id(), index, item.disabled());
        }
        return childSelectionAt(node, x, y);
    }

    private static Selection childSelectionAt(UiNode node, int x, int y) {
        for (int i = 0; i < node.children().size(); i++) {
            UiNode child = node.children().get(i);
            if (child.bounds().contains(x, y)) {
                return new Selection(child.text(), i, !child.enabled());
            }
        }
        return null;
    }

    private static Selection tabSelectionAt(UiNode node, int x, int y) {
        for (int i = 0; i < node.children().size(); i++) {
            UiNode child = node.children().get(i);
            if (child.bounds().contains(x, y)) {
                if (!node.items().isEmpty() && i < node.items().size()) {
                    UiListItem item = node.items().get(i);
                    return new Selection(item.id(), i, item.disabled() || !child.enabled());
                }
                return new Selection(child.text(), i, !child.enabled());
            }
        }
        return null;
    }

    private static String keyRepresentation(char character, int keyCode) {
        if (keyCode != Keyboard.KEY_NONE) {
            try {
                String name = Keyboard.getKeyName(keyCode);
                if (name != null && name.length() > 0) {
                    return "KEY_" + sanitizeKeyName(name);
                }
            } catch (Throwable ignored) {
            }
            return "KEY_" + keyCode;
        }
        if (isAllowedCharacter(character)) {
            return "CHAR_" + sanitizeKeyName(String.valueOf(character));
        }
        return "KEY_" + keyCode;
    }

    private static String sanitizeKeyName(String name) {
        String upper = name == null ? "" : name.toUpperCase(Locale.ROOT);
        StringBuilder builder = new StringBuilder();
        boolean previousUnderscore = false;
        for (int i = 0; i < upper.length(); i++) {
            char c = upper.charAt(i);
            boolean valid = (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
            if (valid) {
                builder.append(c);
                previousUnderscore = false;
            } else if (!previousUnderscore) {
                builder.append('_');
                previousUnderscore = true;
            }
        }
        int length = builder.length();
        if (length > 0 && builder.charAt(length - 1) == '_') {
            builder.setLength(length - 1);
        }
        return builder.length() == 0 ? "UNKNOWN" : builder.toString();
    }

    private static Selection tableSelectionAt(UiNode node, int y) {
        if (node.rows().isEmpty()) {
            return null;
        }
        int headerHeight = Math.min(18, Math.max(12, node.bounds().height / 3));
        int rowHeight = 18;
        int index = (y - (node.bounds().y + headerHeight + 5)) / rowHeight;
        if (index < 0 || index >= node.rows().size()) {
            return null;
        }
        UiTableRow row = node.rows().get(index);
        return new Selection(row.id(), index, row.disabled());
    }

    private static Selection treeSelectionAt(UiNode node, int y) {
        List<TreeEntry> entries = new ArrayList<TreeEntry>();
        collectTreeEntries(node.treeItems(), entries);
        int index = (y - node.bounds().y - 3) / 14;
        if (index < 0 || index >= entries.size()) {
            return null;
        }
        TreeEntry entry = entries.get(index);
        return new Selection(entry.item.id(), entry.index, entry.item.disabled());
    }

    private static void collectTreeEntries(List<UiTreeItem> items, List<TreeEntry> entries) {
        for (UiTreeItem item : items) {
            entries.add(new TreeEntry(item, entries.size()));
            if (item.expanded()) {
                collectTreeEntries(item.children(), entries);
            }
        }
    }

    private static UiNode selectionAncestor(UiNode node) {
        UiNode current = node;
        while (current != null) {
            if (current.type() == UiNode.Type.LIST || current.type() == UiNode.Type.TABS
                    || current.type() == UiNode.Type.TABLE || current.type() == UiNode.Type.DATA_GRID || current.type() == UiNode.Type.TREE) {
                return current;
            }
            current = current.parent();
        }
        return null;
    }

    private static UiRect selectMenuBounds(UiNode node) {
        int itemCount = node.items().isEmpty() ? node.children().size() : node.items().size();
        return new UiRect(node.bounds().x, node.bounds().y + node.bounds().height + 2, node.bounds().width, Math.max(18, itemCount * 18));
    }

    private static boolean closeContextMenusOutside(UiNode root, int x, int y) {
        if (!hasOpenContextMenu(root)) {
            return false;
        }
        if (containsOpenContextMenu(root, x, y)) {
            return false;
        }
        closeContextMenus(root);
        return true;
    }

    private static boolean hasOpenContextMenu(UiNode node) {
        if (node == null || !node.visible()) {
            return false;
        }
        if (node.type() == UiNode.Type.CONTEXT_MENU && node.open()) {
            return true;
        }
        for (UiNode child : node.children()) {
            if (hasOpenContextMenu(child)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsOpenContextMenu(UiNode node, int x, int y) {
        if (node == null || !node.visible()) {
            return false;
        }
        if (node.type() == UiNode.Type.CONTEXT_MENU && node.open() && node.bounds().contains(x, y)) {
            return true;
        }
        for (UiNode child : node.children()) {
            if (containsOpenContextMenu(child, x, y)) {
                return true;
            }
        }
        return false;
    }

    private static void closeContextMenus(UiNode node) {
        if (node == null) {
            return;
        }
        if (node.type() == UiNode.Type.CONTEXT_MENU && node.open()) {
            node.open(false);
        }
        for (UiNode child : node.children()) {
            closeContextMenus(child);
        }
    }

    private void trapFocus(UiNode root) {
        UiNode focused = focusManager.focused();
        if (isFocusableDescendant(focused, root)) {
            return;
        }
        focusManager.focusNext(root, false);
    }

    private UiNode normalizeFocusedForActiveRoot() {
        UiNode inputRoot = runtime.activeInputRoot();
        UiNode focused = focusManager.focused();
        if (isFocusableDescendant(focused, inputRoot)) {
            return focused;
        }
        if (inputRoot != runtime.screen().root()) {
            trapFocus(inputRoot);
        } else {
            focusManager.clear();
        }
        return focusManager.focused();
    }

    private static boolean isFocusableDescendant(UiNode node, UiNode root) {
        UiNode current = node;
        while (current != null) {
            if (current == root) {
                return node.visible() && node.enabled() && node.focusable();
            }
            current = current.parent();
        }
        return false;
    }

    private static final class Selection {
        private final String id;
        private final int index;
        private final boolean disabled;

        private Selection(String id, int index, boolean disabled) {
            this.id = id == null ? "" : id;
            this.index = index;
            this.disabled = disabled;
        }
    }

    private static final class TreeEntry {
        private final UiTreeItem item;
        private final int index;

        private TreeEntry(UiTreeItem item, int index) {
            this.item = item;
            this.index = index;
        }
    }
}
