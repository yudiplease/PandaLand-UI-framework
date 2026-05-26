package land.pandaland.ui.v2.render;

import land.pandaland.ui.v2.components.UiCustomComponent;
import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.data.UiListItem;
import land.pandaland.ui.v2.data.UiRichTextSpan;
import land.pandaland.ui.v2.data.UiTableColumn;
import land.pandaland.ui.v2.data.UiTableRow;
import land.pandaland.ui.v2.data.UiTreeItem;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.minecraft.UiInventoryGrid;
import land.pandaland.ui.v2.minecraft.UiItemStackRef;
import land.pandaland.ui.v2.minecraft.UiSlotBinding;
import land.pandaland.ui.v2.style.UiColor;
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
        return render(runtime, theme, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    /**
     * Builds the render command list using the current mouse position for hover-only commands.
     *
     * @param runtime runtime to render
     * @param theme visual theme
     * @param mouseX mouse x coordinate in scaled GUI pixels
     * @param mouseY mouse y coordinate in scaled GUI pixels
     * @return render commands in draw order
     */
    public static UiRenderList render(UiRuntime runtime, UiTheme theme, int mouseX, int mouseY) {
        if (runtime == null) {
            throw new IllegalArgumentException("runtime cannot be null");
        }
        if (theme == null) {
            throw new IllegalArgumentException("theme cannot be null");
        }

        UiRenderList commands = new UiRenderList();
        UiRenderList tooltips = new UiRenderList();
        UiNode focused = runtime.focus().focused();

        renderNode(commands, tooltips, runtime.screen().root(), theme, focused, runtime.elapsedMs(), mouseX, mouseY);

        int modalIndex = 0;
        for (UiNode modal : runtime.modals()) {
            if (coversMouse(modal.bounds(), mouseX, mouseY)) {
                tooltips = new UiRenderList();
            }
            commands.add(UiRenderCommand.layerStart("modal", 1000 + modalIndex));
            renderNode(commands, tooltips, modal, theme, focused, runtime.elapsedMs(), mouseX, mouseY);
            commands.add(UiRenderCommand.layerEnd("modal"));
            modalIndex++;
        }

        int toastIndex = 0;
        for (UiNode toast : runtime.toasts()) {
            if (coversMouse(toast.bounds(), mouseX, mouseY)) {
                tooltips = new UiRenderList();
            }
            commands.add(UiRenderCommand.layerStart("toast", 2000 + toastIndex));
            renderNode(commands, tooltips, toast, theme, focused, runtime.elapsedMs(), mouseX, mouseY);
            commands.add(UiRenderCommand.layerEnd("toast"));
            toastIndex++;
        }

        for (UiRenderCommand tooltip : tooltips.commands()) {
            commands.add(tooltip);
        }

        return commands;
    }

    private static void renderNode(UiRenderList commands, UiRenderList tooltips, UiNode node, UiTheme theme, UiNode focused, long elapsedMs, int mouseX, int mouseY) {
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
            commands.add(UiRenderCommand.shadow(node.bounds(), theme.panelRadius(), new UiColor(0x66000000), 8, 2, 3));
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.panelRadius(), theme.panelBase()));
            commands.add(UiRenderCommand.border(node.bounds(), theme.panelRadius(), theme.secondaryAccent(), 1));
            commands.add(UiRenderCommand.text(node.text(), inset(node.bounds(), 6, 6), theme.textPrimary()));
        } else if (node.type() == UiNode.Type.TOAST) {
            commands.add(UiRenderCommand.shadow(node.bounds(), theme.buttonRadius(), new UiColor(0x66000000), 4, 1, 2));
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
            commands.add(UiRenderCommand.border(node.bounds(), theme.buttonRadius(), theme.primaryAccent(), 1));
            commands.add(UiRenderCommand.text(node.text(), inset(node.bounds(), 4, 4), theme.textPrimary()));
        } else if (node.type() == UiNode.Type.CHECKBOX) {
            renderCheckbox(commands, node, theme);
        } else if (node.type() == UiNode.Type.SELECT) {
            renderSelect(commands, node, theme);
        } else if (node.type() == UiNode.Type.LIST) {
            renderList(commands, node, theme);
        } else if (node.type() == UiNode.Type.TABS) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
            commands.add(UiRenderCommand.border(node.bounds(), theme.buttonRadius(), theme.primaryAccent(), 1));
        } else if (node.type() == UiNode.Type.TABLE || node.type() == UiNode.Type.DATA_GRID) {
            renderTable(commands, node, theme);
        } else if (node.type() == UiNode.Type.TREE) {
            renderTree(commands, node, theme);
        } else if (node.type() == UiNode.Type.RICH_TEXT) {
            renderRichText(commands, node, theme);
        } else if (node.type() == UiNode.Type.COLOR_PICKER || node.type() == UiNode.Type.KEYBIND_INPUT) {
            renderTextualControl(commands, node, theme);
        } else if (node.type() == UiNode.Type.PAGE_STACK) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.panelRadius(), theme.panelBase()));
            commands.add(UiRenderCommand.border(node.bounds(), theme.panelRadius(), theme.primaryAccent(), 1));
        } else if (node.type() == UiNode.Type.CANVAS) {
            commands.add(UiRenderCommand.border(node.bounds(), 1, theme.textMuted(), 1));
        } else if (node.type() == UiNode.Type.ITEM) {
            renderItem(commands, tooltips, node.itemStack(), node.bounds(), mouseX, mouseY);
        } else if (node.type() == UiNode.Type.SLOT) {
            renderSlot(commands, tooltips, node.bounds(), slotItem(node), theme, mouseX, mouseY, false);
        } else if (node.type() == UiNode.Type.INVENTORY_GRID) {
            renderInventoryGrid(commands, tooltips, node, theme, mouseX, mouseY);
        } else if (node.type() == UiNode.Type.HOTBAR) {
            renderHotbar(commands, tooltips, node, theme, mouseX, mouseY);
        } else if (node.type() == UiNode.Type.CUSTOM_COMPONENT) {
            if (node.customComponent() instanceof UiCustomComponent) {
                UiCustomComponent component = (UiCustomComponent) node.customComponent();
                try {
                    component.render(commands, node, node.bounds());
                } catch (Throwable ignored) {
                }
            }
        } else if (node.type() == UiNode.Type.SCROLL_CONTAINER) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.panelRadius(), theme.panelBase()));
            commands.add(UiRenderCommand.clipStart(node.bounds()));
        } else if (node.type() == UiNode.Type.TOOLTIP || node.type() == UiNode.Type.CONTEXT_MENU || node.type() == UiNode.Type.FORM) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.panelRadius(), theme.panelBase()));
            if (!node.text().isEmpty()) {
                commands.add(UiRenderCommand.text(node.text(), inset(node.bounds(), 5, 5), theme.textPrimary()));
            }
        }

        if (node.customComponent() instanceof UiCustomDraw) {
            UiCustomDraw customDraw = (UiCustomDraw) node.customComponent();
            commands.add(UiRenderCommand.custom(node.bounds(), customDraw));
            try {
                customDraw.draw(commands, node.bounds());
            } catch (Throwable ignored) {
            }
        }

        if (node.customDraw() != null) {
            commands.add(UiRenderCommand.custom(node.bounds(), node.customDraw()));
            try {
                node.customDraw().draw(commands, node.bounds());
            } catch (Throwable ignored) {
            }
        }

        if (!rendersOwnChildren(node)) {
            for (UiNode child : node.children()) {
                renderNode(commands, tooltips, child, theme, focused, elapsedMs, mouseX, mouseY);
            }
        }

        if (clipChildren) {
            commands.add(UiRenderCommand.clipEnd());
        }
    }

    private static boolean rendersOwnChildren(UiNode node) {
        return node.type() == UiNode.Type.LIST || node.type() == UiNode.Type.SELECT;
    }

    private static boolean coversMouse(UiRect overlayBounds, int mouseX, int mouseY) {
        return overlayBounds != null && overlayBounds.contains(mouseX, mouseY);
    }

    private static void renderInventoryGrid(UiRenderList commands, UiRenderList tooltips, UiNode node, UiTheme theme, int mouseX, int mouseY) {
        UiInventoryGrid grid = node.inventoryGrid();
        if (grid == null) {
            return;
        }

        int maxSlots = grid.columns() * grid.rows();
        for (int index = 0; index < maxSlots; index++) {
            int column = index % grid.columns();
            int row = index / grid.columns();
            UiRect slotRect = new UiRect(
                    node.bounds().x + column * (grid.slotSize() + grid.gap()),
                    node.bounds().y + row * (grid.slotSize() + grid.gap()),
                    grid.slotSize(),
                    grid.slotSize()
            );
            UiItemStackRef item = index < grid.slots().size() ? grid.slots().get(index).item() : UiItemStackRef.empty();
            renderSlot(commands, tooltips, slotRect, item, theme, mouseX, mouseY, false);
        }
    }

    private static void renderHotbar(UiRenderList commands, UiRenderList tooltips, UiNode node, UiTheme theme, int mouseX, int mouseY) {
        int slotSize = node.bounds().height > 0 ? node.bounds().height : 18;
        int slotCount = node.slotBindings().isEmpty() ? Math.max(1, node.bounds().width / Math.max(1, slotSize)) : node.slotBindings().size();
        for (int index = 0; index < slotCount; index++) {
            UiSlotBinding slot = index < node.slotBindings().size() ? node.slotBindings().get(index) : null;
            UiRect slotRect = new UiRect(node.bounds().x + index * slotSize, node.bounds().y, slotSize, slotSize);
            UiItemStackRef item = slot == null ? UiItemStackRef.empty() : slot.item();
            renderSlot(commands, tooltips, slotRect, item, theme, mouseX, mouseY, index == node.selectedIndex());
        }
    }

    private static void renderSlot(UiRenderList commands, UiRenderList tooltips, UiRect bounds, UiItemStackRef item, UiTheme theme, int mouseX, int mouseY, boolean selected) {
        commands.add(UiRenderCommand.roundedRect(bounds, theme.buttonRadius(), theme.buttonBase()));
        if (selected) {
            commands.add(UiRenderCommand.border(bounds, theme.buttonRadius(), theme.primaryAccent(), 1));
        }
        renderItem(commands, tooltips, item, bounds, mouseX, mouseY);
    }

    private static void renderItem(UiRenderList commands, UiRenderList tooltips, UiItemStackRef item, UiRect bounds, int mouseX, int mouseY) {
        if (item == null || item.isEmpty()) {
            return;
        }
        commands.add(UiRenderCommand.itemStack(item, bounds));
        if (bounds.contains(mouseX, mouseY)) {
            tooltips.add(UiRenderCommand.itemTooltip(item, bounds, mouseX, mouseY));
        }
    }

    private static UiItemStackRef slotItem(UiNode node) {
        UiSlotBinding binding = node.slotBinding();
        if (binding != null) {
            return binding.item();
        }
        return node.itemStack();
    }

    private static void renderSelect(UiRenderList commands, UiNode node, UiTheme theme) {
        commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
        commands.add(UiRenderCommand.border(node.bounds(), theme.buttonRadius(), theme.primaryAccent(), 1));
        commands.add(UiRenderCommand.text(selectText(node), inset(node.bounds(), 5, 4), theme.textPrimary()));
        if (node.open()) {
            int itemCount = node.items().isEmpty() ? node.children().size() : node.items().size();
            UiRect menu = new UiRect(node.bounds().x, node.bounds().y + node.bounds().height + 2, node.bounds().width, Math.max(18, itemCount * 18));
            commands.add(UiRenderCommand.layerStart("select", 500));
            commands.add(UiRenderCommand.shadow(menu, theme.buttonRadius(), new UiColor(0x66000000), 4, 1, 2));
            commands.add(UiRenderCommand.roundedRect(menu, theme.buttonRadius(), theme.panelBase()));
            commands.add(UiRenderCommand.border(menu, theme.buttonRadius(), theme.primaryAccent(), 1));
            renderSelectItems(commands, node, menu, theme);
            commands.add(UiRenderCommand.layerEnd("select"));
        }
    }

    private static void renderList(UiRenderList commands, UiNode node, UiTheme theme) {
        commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
        commands.add(UiRenderCommand.border(node.bounds(), theme.buttonRadius(), theme.primaryAccent(), 1));
        int rowHeight = rowHeight(node, 18);
        int y = node.bounds().y + 2;
        if (!node.items().isEmpty()) {
            for (UiListItem item : node.items()) {
                renderListItem(commands, item.label(), item.id(), item.disabled(), node, new UiRect(node.bounds().x + 4, y, Math.max(0, node.bounds().width - 8), rowHeight), theme);
                y += rowHeight;
            }
            return;
        }
        for (UiNode child : node.children()) {
            renderListItem(commands, child.text(), "", !child.enabled(), node, new UiRect(node.bounds().x + 4, y, Math.max(0, node.bounds().width - 8), rowHeight), theme);
            y += rowHeight;
        }
    }

    private static void renderListItem(UiRenderList commands, String label, String id, boolean disabled, UiNode node, UiRect rect, UiTheme theme) {
        if (node.selectedIds().contains(id)) {
            commands.add(UiRenderCommand.gradientRect(rect, 2, theme.primaryAccent(), theme.secondaryAccent(), false));
        }
        commands.add(UiRenderCommand.textWrap(label, rect, disabled ? theme.textMuted() : theme.textPrimary(), 10));
    }

    private static void renderSelectItems(UiRenderList commands, UiNode node, UiRect menu, UiTheme theme) {
        int rowHeight = 18;
        int y = menu.y + 3;
        if (!node.items().isEmpty()) {
            for (UiListItem item : node.items()) {
                UiRect row = new UiRect(menu.x + 4, y, Math.max(0, menu.width - 8), rowHeight);
                renderListItem(commands, item.label(), item.id(), item.disabled(), node, row, theme);
                y += rowHeight;
            }
            return;
        }
        for (UiNode child : node.children()) {
            commands.add(UiRenderCommand.textWrap(child.text(), new UiRect(menu.x + 4, y, Math.max(0, menu.width - 8), rowHeight), theme.textPrimary(), 10));
            y += rowHeight;
        }
    }

    private static void renderTable(UiRenderList commands, UiNode node, UiTheme theme) {
        UiRect bounds = node.bounds();
        commands.add(UiRenderCommand.roundedRect(bounds, theme.buttonRadius(), theme.buttonBase()));
        commands.add(UiRenderCommand.border(bounds, theme.buttonRadius(), theme.primaryAccent(), 1));

        int headerHeight = Math.min(18, Math.max(12, bounds.height / 3));
        int rowHeight = 18;
        int x = bounds.x + 4;
        int headerY = bounds.y + 3;
        for (UiTableColumn column : node.columns()) {
            int width = columnWidth(column, bounds);
            commands.add(UiRenderCommand.textWrap(column.label(), new UiRect(x, headerY, width, headerHeight), theme.textPrimary(), 10));
            commands.add(UiRenderCommand.line(x + width, bounds.y + 3, x + width, bounds.y + bounds.height - 3, theme.textMuted(), 1));
            x += width;
        }
        commands.add(UiRenderCommand.line(bounds.x + 2, bounds.y + headerHeight + 3, bounds.x + bounds.width - 2, bounds.y + headerHeight + 3, theme.textMuted(), 1));

        int y = bounds.y + headerHeight + 5;
        for (UiTableRow row : node.rows()) {
            if (y + rowHeight > bounds.y + bounds.height) {
                break;
            }
            if (node.selectedIds().contains(row.id())) {
                commands.add(UiRenderCommand.gradientRect(new UiRect(bounds.x + 2, y, Math.max(0, bounds.width - 4), rowHeight), 1, theme.primaryAccent(), theme.secondaryAccent(), false));
            }
            x = bounds.x + 4;
            for (UiTableColumn column : node.columns()) {
                int width = columnWidth(column, bounds);
                commands.add(UiRenderCommand.textWrap(row.cell(column.id()), new UiRect(x, y, width, rowHeight), row.disabled() ? theme.textMuted() : theme.textPrimary(), 10));
                x += width;
            }
            commands.add(UiRenderCommand.line(bounds.x + 2, y + rowHeight, bounds.x + bounds.width - 2, y + rowHeight, theme.textMuted(), 1));
            y += rowHeight;
        }
    }

    private static void renderTree(UiRenderList commands, UiNode node, UiTheme theme) {
        UiRect bounds = node.bounds();
        commands.add(UiRenderCommand.roundedRect(bounds, theme.buttonRadius(), theme.buttonBase()));
        commands.add(UiRenderCommand.border(bounds, theme.buttonRadius(), theme.primaryAccent(), 1));
        renderTreeItems(commands, node, node.treeItems(), bounds.x + 4, bounds.y + 3, bounds, 0, theme);
    }

    private static int renderTreeItems(UiRenderList commands, UiNode node, java.util.List<UiTreeItem> items, int x, int y, UiRect bounds, int depth, UiTheme theme) {
        int rowHeight = 14;
        for (UiTreeItem item : items) {
            if (y + rowHeight > bounds.y + bounds.height) {
                return y;
            }
            int itemX = x + depth * 10;
            UiRect row = new UiRect(itemX, y, Math.max(0, bounds.x + bounds.width - itemX - 4), rowHeight);
            if (node.selectedIds().contains(item.id())) {
                commands.add(UiRenderCommand.gradientRect(new UiRect(bounds.x + 2, y, Math.max(0, bounds.width - 4), rowHeight), 1, theme.primaryAccent(), theme.secondaryAccent(), false));
            }
            if (!item.children().isEmpty()) {
                commands.add(UiRenderCommand.line(itemX - 4, y + 6, itemX - 1, y + 6, theme.textMuted(), 1));
            }
            commands.add(UiRenderCommand.textWrap(item.label(), row, item.disabled() ? theme.textMuted() : theme.textPrimary(), 10));
            y += rowHeight;
            if (item.expanded()) {
                y = renderTreeItems(commands, node, item.children(), x, y, bounds, depth + 1, theme);
            }
        }
        return y;
    }

    private static void renderRichText(UiRenderList commands, UiNode node, UiTheme theme) {
        commands.add(UiRenderCommand.border(node.bounds(), 1, theme.textMuted(), 1));
        int y = node.bounds().y;
        for (UiRichTextSpan span : node.spans()) {
            commands.add(UiRenderCommand.textWrap(span.text(), new UiRect(node.bounds().x, y, node.bounds().width, Math.max(10, node.bounds().height)), new UiColor(0xFF000000 | (span.color() & 0xFFFFFF)), 10));
            y += 10;
        }
    }

    private static void renderTextualControl(UiRenderList commands, UiNode node, UiTheme theme) {
        commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
        commands.add(UiRenderCommand.border(node.bounds(), theme.buttonRadius(), theme.primaryAccent(), 1));
        commands.add(UiRenderCommand.textWrap(selectText(node), inset(node.bounds(), 5, 5), theme.textPrimary(), 10));
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

        if (node.textArea()) {
            commands.add(UiRenderCommand.textWrap(visibleText, inset(node.bounds(), 5, 6), empty ? theme.textMuted() : theme.textPrimary(), 10));
            if (!node.valid()) {
                commands.add(UiRenderCommand.text(node.validationMessage(), new UiRect(node.bounds().x, node.bounds().y + node.bounds().height + 2, node.bounds().width, 10), theme.dangerAccent()));
            }
            return;
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

    private static int rowHeight(UiNode node, int fallback) {
        if (node.bounds().height <= 0) {
            return Math.max(1, fallback);
        }
        if (node.items().isEmpty()) {
            int childCount = Math.max(1, node.children().size());
            return Math.max(1, node.bounds().height / childCount);
        }
        return Math.max(1, node.bounds().height / Math.max(1, node.items().size()));
    }

    private static int columnWidth(UiTableColumn column, UiRect bounds) {
        int width = column.width();
        if (width > 0) {
            return width;
        }
        return Math.max(1, bounds.width / 2);
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
