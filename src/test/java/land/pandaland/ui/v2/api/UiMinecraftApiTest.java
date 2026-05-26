package land.pandaland.ui.v2.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.minecraft.UiAnchor;
import land.pandaland.ui.v2.minecraft.UiInventoryGrid;
import land.pandaland.ui.v2.minecraft.UiItemStackRef;
import land.pandaland.ui.v2.minecraft.UiSlotBinding;

import org.junit.Test;

public class UiMinecraftApiTest {
    @Test
    public void buildersCreateMinecraftNodes() {
        UiSlotBinding slot = new UiSlotBinding("slot-1", 0, UiItemStackRef.empty(), true, "", null);
        UiInventoryGrid grid = new UiInventoryGrid(3, 3, 18, 2, Collections.singletonList(slot));

        UiScreen screen = Ui.screen("minecraft-api").root(root -> root
                .item(UiItemStackRef.of(new Object()).displayName("Stone"), 18, 18)
                .slot(slot, 18)
                .inventoryGrid(grid)
                .hotbar(Collections.singletonList(slot), 0, 18)
                .anchor(UiAnchor.CENTER)
                .snapToPixel(true)
        ).build();

        assertEquals(UiNode.Type.ITEM, screen.root().children().get(0).type());
        assertEquals(UiNode.Type.SLOT, screen.root().children().get(1).type());
        assertEquals(UiNode.Type.INVENTORY_GRID, screen.root().children().get(2).type());
        assertEquals(UiNode.Type.HOTBAR, screen.root().children().get(3).type());
        assertEquals("Stone", screen.root().children().get(0).itemStack().displayName());
        assertEquals(slot, screen.root().children().get(1).slotBinding());
        assertEquals(grid, screen.root().children().get(2).inventoryGrid());
        assertEquals(Collections.singletonList(slot), screen.root().children().get(3).slotBindings());
        assertEquals(0, screen.root().children().get(3).selectedIndex());
        assertEquals(UiAnchor.CENTER, screen.root().anchor());
        assertTrue(screen.root().snapToPixel());
    }

    @Test
    public void hotbarSlotBindingsAreImmutableDefensiveCopies() {
        UiSlotBinding slot = new UiSlotBinding("slot-1", 0, UiItemStackRef.empty(), true, "", null);
        List<UiSlotBinding> slots = new ArrayList<UiSlotBinding>();
        slots.add(slot);

        UiScreen screen = Ui.screen("hotbar-copy").root(root -> root.hotbar(slots, 0, 18)).build();
        slots.clear();

        List<UiSlotBinding> storedSlots = screen.root().children().get(0).slotBindings();
        assertEquals(1, storedSlots.size());
        assertEquals(slot, storedSlots.get(0));
        try {
            storedSlots.add(slot);
        } catch (UnsupportedOperationException expected) {
            return;
        }
        throw new AssertionError("slot bindings should be immutable");
    }

    @Test
    public void hotbarSlotBindingsAreNullSafe() {
        UiScreen screen = Ui.screen("hotbar-null").root(root -> root.hotbar(null, 0, 18)
                .hotbar(Collections.<UiSlotBinding>singletonList(null), 0, 18)).build();

        assertTrue(screen.root().children().get(0).slotBindings().isEmpty());
        assertTrue(screen.root().children().get(1).slotBindings().isEmpty());
        assertEquals(9 * 18, screen.root().children().get(1).layoutStyle().preferredSize().width);
    }

    @Test
    public void hotbarWidthUsesFilteredNonNullSlots() {
        UiSlotBinding first = new UiSlotBinding("slot-1", 0, UiItemStackRef.empty(), true, "", null);
        UiSlotBinding second = new UiSlotBinding("slot-2", 1, UiItemStackRef.empty(), true, "", null);
        List<UiSlotBinding> slots = new ArrayList<UiSlotBinding>();
        slots.add(first);
        slots.add(null);
        slots.add(second);

        UiScreen screen = Ui.screen("hotbar-filtered-width").root(root -> root.hotbar(slots, 0, 18)).build();

        assertEquals(2, screen.root().children().get(0).slotBindings().size());
        assertEquals(2 * 18, screen.root().children().get(0).layoutStyle().preferredSize().width);
    }

    @Test
    public void anchorEnumExposesAllMinecraftAwareAnchors() {
        assertEquals(UiAnchor.TOP_LEFT, UiAnchor.valueOf("TOP_LEFT"));
        assertEquals(UiAnchor.TOP_CENTER, UiAnchor.valueOf("TOP_CENTER"));
        assertEquals(UiAnchor.CENTER, UiAnchor.valueOf("CENTER"));
        assertEquals(UiAnchor.BOTTOM_CENTER, UiAnchor.valueOf("BOTTOM_CENTER"));
        assertEquals(UiAnchor.INVENTORY_CENTER, UiAnchor.valueOf("INVENTORY_CENTER"));
        assertEquals(UiAnchor.HOTBAR, UiAnchor.valueOf("HOTBAR"));
    }
}
