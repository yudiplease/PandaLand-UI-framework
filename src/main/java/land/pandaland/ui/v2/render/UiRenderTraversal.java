package land.pandaland.ui.v2.render;

import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.style.UiTheme;

/**
 * Converts a laid-out retained UI runtime into renderer-independent commands.
 */
public final class UiRenderTraversal {
    private UiRenderTraversal() {
    }

    /**
     * Builds the render command list for the screen root, modals, and toasts.
     *
     * @param runtime runtime to render
     * @param theme visual theme
     * @return render commands in draw order
     */
    public static UiRenderList render(UiRuntime runtime, UiTheme theme) {
        if (runtime == null) {
            throw new IllegalArgumentException("runtime cannot be null");
        }
        if (theme == null) {
            throw new IllegalArgumentException("theme cannot be null");
        }

        UiRenderList commands = new UiRenderList();
        UiNode focused = runtime.focus().focused();

        renderNode(commands, runtime.screen().root(), theme, focused, runtime.elapsedMs());

        for (UiNode modal : runtime.modals()) {
            renderNode(commands, modal, theme, focused, runtime.elapsedMs());
        }

        for (UiNode toast : runtime.toasts()) {
            renderNode(commands, toast, theme, focused, runtime.elapsedMs());
        }

        return commands;
    }

    private static void renderNode(UiRenderList commands, UiNode node, UiTheme theme, UiNode focused, long elapsedMs) {
        if (node == null || !node.visible()) {
            return;
        }

        boolean clipChildren = node.type() == UiNode.Type.SCROLL_CONTAINER;
        if (!node.texture().isEmpty()) {
            commands.add(UiRenderCommand.texture(node.texture(), node.bounds()));
        } else if (node.type() == UiNode.Type.PANEL) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.panelRadius(), theme.panelBase()));
        } else if (node.type() == UiNode.Type.BUTTON) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
            commands.add(UiRenderCommand.text(node.text(), inset(node.bounds(), 4, 4), node.enabled() ? theme.textPrimary() : theme.textMuted()));
        } else if (node.type() == UiNode.Type.LABEL) {
            commands.add(UiRenderCommand.text(node.text(), node.bounds(), theme.textPrimary()));
        } else if (node.type() == UiNode.Type.PROGRESS) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
            commands.add(UiRenderCommand.progress(node.bounds(), theme.primaryAccent(), valueAmount(node)));
        } else if (node.type() == UiNode.Type.SLIDER) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
            commands.add(UiRenderCommand.progress(node.bounds(), theme.primaryAccent(), valueAmount(node)));
            commands.add(UiRenderCommand.text(sliderText(node), inset(node.bounds(), 4, 4), theme.textPrimary()));
        } else if (node.type() == UiNode.Type.TEXT_INPUT) {
            renderTextInput(commands, node, theme, focused == node, elapsedMs);
        } else if (node.type() == UiNode.Type.MODAL) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.panelRadius(), theme.panelBase()));
            commands.add(UiRenderCommand.text(node.text(), inset(node.bounds(), 6, 6), theme.textPrimary()));
        } else if (node.type() == UiNode.Type.TOAST) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
            commands.add(UiRenderCommand.text(node.text(), inset(node.bounds(), 4, 4), theme.textPrimary()));
        } else if (node.type() == UiNode.Type.CHECKBOX) {
            renderCheckbox(commands, node, theme);
        } else if (node.type() == UiNode.Type.SELECT) {
            renderSelect(commands, node, theme);
        } else if (node.type() == UiNode.Type.LIST || node.type() == UiNode.Type.TABS) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
        } else if (node.type() == UiNode.Type.SCROLL_CONTAINER) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.panelRadius(), theme.panelBase()));
            commands.add(UiRenderCommand.clipStart(node.bounds()));
        } else if (node.type() == UiNode.Type.TOOLTIP || node.type() == UiNode.Type.CONTEXT_MENU || node.type() == UiNode.Type.FORM) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.panelRadius(), theme.panelBase()));
            if (!node.text().isEmpty()) {
                commands.add(UiRenderCommand.text(node.text(), inset(node.bounds(), 5, 5), theme.textPrimary()));
            }
        }

        for (UiNode child : node.children()) {
            renderNode(commands, child, theme, focused, elapsedMs);
        }

        if (clipChildren) {
            commands.add(UiRenderCommand.clipEnd());
        }
    }

    private static void renderSelect(UiRenderList commands, UiNode node, UiTheme theme) {
        commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
        commands.add(UiRenderCommand.text(selectText(node), inset(node.bounds(), 5, 4), theme.textPrimary()));
        if (node.open()) {
            UiRect menu = new UiRect(node.bounds().x, node.bounds().y + node.bounds().height + 2, node.bounds().width, Math.max(18, node.children().size() * 18));
            commands.add(UiRenderCommand.roundedRect(menu, theme.buttonRadius(), theme.panelBase()));
        }
    }

    private static void renderCheckbox(UiRenderList commands, UiNode node, UiTheme theme) {
        UiRect bounds = node.bounds();

        int boxSize = Math.min(14, Math.max(10, bounds.height - 4));
        int boxX = bounds.x;
        int boxY = bounds.y + Math.max(0, (bounds.height - boxSize) / 2);

        UiRect box = new UiRect(boxX, boxY, boxSize, boxSize);

        commands.add(UiRenderCommand.roundedRect(box, 2, theme.buttonBase()));

        if (checked(node)) {
            UiRect inner = new UiRect(
                    box.x + 3,
                    box.y + 3,
                    Math.max(0, box.width - 6),
                    Math.max(0, box.height - 6)
            );

            commands.add(UiRenderCommand.roundedRect(inner, 1, theme.primaryAccent()));
        }

        if (node.text() != null && !node.text().isEmpty()) {
            UiRect labelRect = new UiRect(
                    bounds.x + boxSize + 6,
                    bounds.y,
                    Math.max(0, bounds.width - boxSize - 6),
                    bounds.height
            );

            commands.add(UiRenderCommand.text(
                    node.text(),
                    labelRect,
                    node.enabled() ? theme.textPrimary() : theme.textMuted()
            ));
        }
    }

    private static boolean checked(UiNode node) {
        if (node.valueState() == null || node.valueState().get() == null) {
            return false;
        }

        Object raw = node.valueState().get();
        return raw instanceof Boolean && (Boolean) raw;
    }

    private static void renderTextInput(UiRenderList commands, UiNode node, UiTheme theme, boolean focused, long elapsedMs) {
        commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));

        if (focused) {
            commands.add(UiRenderCommand.progress(new UiRect(node.bounds().x, node.bounds().y, node.bounds().width, 2), theme.primaryAccent(), 1.0F));
        }

        String value = textValue(node);
        boolean empty = value.isEmpty();

        String visibleText;
        if (empty) {
            visibleText = node.placeholder();
        } else if (node.password()) {
            visibleText = mask(value);
        } else {
            visibleText = value;
        }

        if (focused) {
            int start = Math.min(visibleText.length(), Math.max(0, node.horizontalScroll() / 6));
            int maxChars = Math.max(1, (node.bounds().width - 10) / 6);
            int end = Math.min(visibleText.length(), start + maxChars);
            String clipped = visibleText.substring(start, end);
            int selectionStart = Math.max(start, Math.min(end, node.selectionMin()));
            int selectionEnd = Math.max(start, Math.min(end, node.selectionMax()));
            if (node.hasSelection() && selectionEnd > selectionStart) {
                commands.add(UiRenderCommand.roundedRect(new UiRect(
                        node.bounds().x + 5 + (selectionStart - start) * 6,
                        node.bounds().y + 5,
                        Math.max(1, (selectionEnd - selectionStart) * 6),
                        Math.max(8, node.bounds().height - 10)
                ), 1, theme.secondaryAccent()));
            }
            commands.add(UiRenderCommand.text(clipped, inset(node.bounds(), 5, 6), empty ? theme.textMuted() : theme.textPrimary()));
            if (caretVisible(elapsedMs)) {
                int cursor = Math.max(start, Math.min(end, clamp(node.cursorPosition(), visibleText.length())));
                commands.add(UiRenderCommand.roundedRect(new UiRect(
                        node.bounds().x + 5 + (cursor - start) * 6,
                        node.bounds().y + 5,
                        1,
                        Math.max(8, node.bounds().height - 10)
                ), 0, theme.primaryAccent()));
            }
            if (!node.valid()) {
                commands.add(UiRenderCommand.text(node.validationMessage(), new UiRect(node.bounds().x, node.bounds().y + node.bounds().height + 2, node.bounds().width, 10), theme.dangerAccent()));
            }
            return;
        }

        commands.add(UiRenderCommand.text(
                visibleText,
                inset(node.bounds(), 5, 6),
                empty && !focused ? theme.textMuted() : theme.textPrimary()
        ));
    }

    private static boolean caretVisible(long elapsedMs) {
        return (elapsedMs / 500L) % 2L == 0L;
    }

    private static String selectText(UiNode node) {
        String value = textValue(node);
        return node.text().isEmpty() ? value : node.text() + ": " + value;
    }

    private static String sliderText(UiNode node) {
        if (node.valueState() == null || node.valueState().get() == null) {
            return node.text();
        }

        Object raw = node.valueState().get();
        if (!(raw instanceof Number)) {
            return node.text();
        }

        return node.text() + ": " + Math.round(((Number) raw).floatValue() * 100.0F) + "%";
    }

    private static float valueAmount(UiNode node) {
        if (node.valueState() == null || node.valueState().get() == null) {
            return 1.0F;
        }

        Object raw = node.valueState().get();
        if (!(raw instanceof Number)) {
            return 1.0F;
        }

        return ((Number) raw).floatValue();
    }

    private static String textValue(UiNode node) {
        if (node.valueState() == null || node.valueState().get() == null) {
            return "";
        }

        return String.valueOf(node.valueState().get());
    }

    private static String withCursor(String text, int cursor) {
        cursor = clamp(cursor, text.length());
        return text.substring(0, cursor) + "|" + text.substring(cursor);
    }

    private static String mask(String text) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            builder.append('*');
        }

        return builder.toString();
    }

    private static UiRect inset(UiRect rect, int x, int y) {
        return new UiRect(
                rect.x + x,
                rect.y + y,
                Math.max(0, rect.width - x * 2),
                Math.max(0, rect.height - y * 2)
        );
    }

    private static int clamp(int value, int max) {
        return Math.max(0, Math.min(max, value));
    }
}
