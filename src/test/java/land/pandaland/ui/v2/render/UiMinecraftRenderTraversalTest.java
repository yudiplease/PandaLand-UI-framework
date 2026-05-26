package land.pandaland.ui.v2.render;

import java.util.Arrays;

import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.minecraft.UiInventoryGrid;
import land.pandaland.ui.v2.minecraft.UiItemStackRef;
import land.pandaland.ui.v2.minecraft.UiSlotBinding;
import land.pandaland.ui.v2.style.UiTheme;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public final class UiMinecraftRenderTraversalTest {
    @Test
    public void itemNodeEmitsItemStackCommand() {
        UiItemStackRef item = item("Stone", 3);
        UiRect bounds = new UiRect(4, 5, 18, 18);
        UiNode root = new UiNode(UiNode.Type.ROOT)
                .add(new UiNode(UiNode.Type.ITEM).bounds(bounds).itemStack(item));

        UiRenderList commands = render(root);

        UiRenderCommand stack = first(commands, UiRenderCommand.Type.ITEM_STACK);
        assertSame(item, stack.item());
        assertEquals(bounds, stack.rect());
    }

    @Test
    public void itemNodeEmitsTooltipOnlyWhenHoveredAndAfterStack() {
        UiItemStackRef item = item("Stone", 3);
        UiRect bounds = new UiRect(4, 5, 18, 18);
        UiNode root = new UiNode(UiNode.Type.ROOT)
                .add(new UiNode(UiNode.Type.ITEM).bounds(bounds).itemStack(item));

        UiRenderList notHovered = render(root, 0, 0);
        assertEquals(0, count(notHovered, UiRenderCommand.Type.ITEM_TOOLTIP));

        UiRenderList hovered = render(root, 5, 6);
        assertEquals(1, count(hovered, UiRenderCommand.Type.ITEM_TOOLTIP));
        assertTrue(indexOf(hovered, UiRenderCommand.Type.ITEM_STACK) < indexOf(hovered, UiRenderCommand.Type.ITEM_TOOLTIP));
        assertSame(item, first(hovered, UiRenderCommand.Type.ITEM_TOOLTIP).item());
        assertEquals(bounds, first(hovered, UiRenderCommand.Type.ITEM_TOOLTIP).anchorRect());
        assertEquals(5, first(hovered, UiRenderCommand.Type.ITEM_TOOLTIP).mouseX());
        assertEquals(6, first(hovered, UiRenderCommand.Type.ITEM_TOOLTIP).mouseY());
    }

    @Test
    public void slotNodeEmitsBackgroundItemAndTooltipOnlyWhenHovered() {
        UiItemStackRef item = item("Diamond", 1);
        UiRect bounds = new UiRect(10, 12, 18, 18);
        UiNode root = new UiNode(UiNode.Type.ROOT)
                .add(new UiNode(UiNode.Type.SLOT)
                        .bounds(bounds)
                        .slotBinding(slot("slot", 0, item)));

        UiRenderList notHovered = render(root, 0, 0);
        assertEquals(1, count(notHovered, UiRenderCommand.Type.ROUNDED_RECT));
        assertEquals(1, count(notHovered, UiRenderCommand.Type.ITEM_STACK));
        assertEquals(0, count(notHovered, UiRenderCommand.Type.ITEM_TOOLTIP));

        UiRenderList hovered = render(root, 11, 13);
        assertEquals(1, count(hovered, UiRenderCommand.Type.ITEM_TOOLTIP));
        assertTrue(indexOf(hovered, UiRenderCommand.Type.ITEM_STACK) < indexOf(hovered, UiRenderCommand.Type.ITEM_TOOLTIP));
        assertSame(item, first(hovered, UiRenderCommand.Type.ITEM_TOOLTIP).item());
        assertEquals(bounds, first(hovered, UiRenderCommand.Type.ITEM_TOOLTIP).anchorRect());
        assertEquals(11, first(hovered, UiRenderCommand.Type.ITEM_TOOLTIP).mouseX());
        assertEquals(13, first(hovered, UiRenderCommand.Type.ITEM_TOOLTIP).mouseY());
    }

    @Test
    public void inventoryGridEmitsComputedSlotBackgroundsAndItems() {
        UiItemStackRef first = item("Stone", 1);
        UiItemStackRef second = item("Dirt", 2);
        UiNode root = new UiNode(UiNode.Type.ROOT)
                .add(new UiNode(UiNode.Type.INVENTORY_GRID)
                        .bounds(new UiRect(20, 30, 100, 60))
                        .inventoryGrid(new UiInventoryGrid(2, 2, 18, 2, Arrays.asList(
                                slot("a", 0, first),
                                slot("b", 1, second)
                        ))));

        UiRenderList commands = render(root);

        assertEquals(4, count(commands, UiRenderCommand.Type.ROUNDED_RECT));
        assertEquals(2, count(commands, UiRenderCommand.Type.ITEM_STACK));
        assertTrue(containsRect(commands, UiRenderCommand.Type.ROUNDED_RECT, new UiRect(20, 30, 18, 18)));
        assertTrue(containsRect(commands, UiRenderCommand.Type.ROUNDED_RECT, new UiRect(40, 30, 18, 18)));
        assertTrue(containsRect(commands, UiRenderCommand.Type.ROUNDED_RECT, new UiRect(20, 50, 18, 18)));
        assertTrue(containsRect(commands, UiRenderCommand.Type.ROUNDED_RECT, new UiRect(40, 50, 18, 18)));
        assertTrue(containsItem(commands, first, new UiRect(20, 30, 18, 18)));
        assertTrue(containsItem(commands, second, new UiRect(40, 30, 18, 18)));
    }

    @Test
    public void hotbarEmitsSlotBackgroundsItemsAndSelectedHighlight() {
        UiItemStackRef item = item("Apple", 4);
        UiNode root = new UiNode(UiNode.Type.ROOT)
                .add(new UiNode(UiNode.Type.HOTBAR)
                        .bounds(new UiRect(5, 6, 60, 18))
                        .selectedIndex(1)
                        .slotBindings(Arrays.asList(
                                slot("h0", 0, UiItemStackRef.empty()),
                                slot("h1", 1, item),
                                slot("h2", 2, UiItemStackRef.empty())
                        )));

        UiRenderList commands = render(root);

        assertEquals(3, count(commands, UiRenderCommand.Type.ROUNDED_RECT));
        assertEquals(1, count(commands, UiRenderCommand.Type.ITEM_STACK));
        assertEquals(1, count(commands, UiRenderCommand.Type.BORDER));
        assertTrue(containsRect(commands, UiRenderCommand.Type.BORDER, new UiRect(23, 6, 18, 18)));
        assertTrue(containsItem(commands, item, new UiRect(23, 6, 18, 18)));
    }

    @Test
    public void emptyHotbarEmitsPlaceholderSlotsFromBounds() {
        UiNode root = new UiNode(UiNode.Type.ROOT)
                .add(new UiNode(UiNode.Type.HOTBAR)
                        .bounds(new UiRect(5, 6, 54, 18))
                        .selectedIndex(1));

        UiRenderList commands = render(root);

        assertEquals(3, count(commands, UiRenderCommand.Type.ROUNDED_RECT));
        assertEquals(1, count(commands, UiRenderCommand.Type.BORDER));
        assertTrue(containsRect(commands, UiRenderCommand.Type.BORDER, new UiRect(23, 6, 18, 18)));
    }

    @Test
    public void overlayUnderMouseSuppressesRootItemTooltip() {
        UiItemStackRef item = item("Hidden", 1);
        UiNode root = new UiNode(UiNode.Type.ROOT)
                .add(new UiNode(UiNode.Type.ITEM)
                        .bounds(new UiRect(10, 10, 18, 18))
                        .itemStack(item));
        UiRuntime runtime = new UiRuntime(new UiScreen("minecraft-render", root));
        UiNode modal = new UiNode(UiNode.Type.MODAL).bounds(new UiRect(0, 0, 80, 80)).text("modal");
        runtime.showModal(modal);
        modal.bounds(new UiRect(0, 0, 80, 80));

        UiRenderList commands = UiRenderTraversal.render(runtime, UiTheme.pandalandDefault(), 11, 11);

        assertEquals(0, count(commands, UiRenderCommand.Type.ITEM_TOOLTIP));
    }

    private static UiRenderList render(UiNode root) {
        return render(root, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    private static UiRenderList render(UiNode root, int mouseX, int mouseY) {
        return UiRenderTraversal.render(new UiRuntime(new UiScreen("minecraft-render", root)), UiTheme.pandalandDefault(), mouseX, mouseY);
    }

    private static UiItemStackRef item(String name, int count) {
        return UiItemStackRef.of(new Object()).displayName(name).count(count);
    }

    private static UiSlotBinding slot(String id, int index, UiItemStackRef item) {
        return new UiSlotBinding(id, index, item, true, "", null);
    }

    private static UiRenderCommand first(UiRenderList commands, UiRenderCommand.Type type) {
        for (UiRenderCommand command : commands.commands()) {
            if (command.type() == type) {
                return command;
            }
        }
        throw new AssertionError("Missing command type " + type);
    }

    private static int count(UiRenderList commands, UiRenderCommand.Type type) {
        int count = 0;
        for (UiRenderCommand command : commands.commands()) {
            if (command.type() == type) {
                count++;
            }
        }
        return count;
    }

    private static int indexOf(UiRenderList commands, UiRenderCommand.Type type) {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).type() == type) {
                return i;
            }
        }
        return -1;
    }

    private static boolean containsRect(UiRenderList commands, UiRenderCommand.Type type, UiRect rect) {
        for (UiRenderCommand command : commands.commands()) {
            if (command.type() == type && rect.equals(command.rect())) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsItem(UiRenderList commands, UiItemStackRef item, UiRect rect) {
        for (UiRenderCommand command : commands.commands()) {
            if (command.type() == UiRenderCommand.Type.ITEM_STACK
                    && command.item() == item
                    && rect.equals(command.rect())) {
                return true;
            }
        }
        return false;
    }
}
