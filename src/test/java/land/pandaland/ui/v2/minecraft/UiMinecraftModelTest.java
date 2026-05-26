package land.pandaland.ui.v2.minecraft;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public final class UiMinecraftModelTest {
    @Test
    public void itemStackRefStoresRawStackAndOverrides() {
        Object raw = new Object();
        UiItemStackRef item = UiItemStackRef.of(raw).displayName("Diamond").count(3).damage(2, 10).enchanted(true);
        assertSame(raw, item.rawStack());
        assertFalse(item.isEmpty());
        assertEquals("Diamond", item.displayName());
        assertEquals(3, item.count());
        assertEquals(2, item.damage());
        assertEquals(10, item.maxDamage());
        assertTrue(item.enchanted());
    }

    @Test
    public void itemStackRefExposesPublicEmptyFactory() {
        UiItemStackRef item = UiItemStackRef.empty();
        assertTrue(item.isEmpty());
        assertSame(item, UiItemStackRef.of(null));
    }

    @Test
    public void slotBindingNormalizesNullsToSafeValues() {
        UiSlotBinding slot = new UiSlotBinding(null, -3, null, true, null, null);
        assertEquals("", slot.id());
        assertEquals(0, slot.index());
        assertTrue(slot.item().isEmpty());
        assertEquals("", slot.backgroundTexture());
    }

    @Test
    public void inventoryGridFiltersNullSlotsAndDefensivelyCopies() {
        UiSlotBinding slot = new UiSlotBinding("slot", 1, UiItemStackRef.empty(), true, "slot.png", null);
        List<UiSlotBinding> slots = new ArrayList<UiSlotBinding>();
        slots.add(null);
        slots.add(slot);
        UiInventoryGrid grid = new UiInventoryGrid(3, 3, 18, 1, slots);
        slots.clear();

        assertEquals(1, grid.slots().size());
        assertSame(slot, grid.slots().get(0));
        try {
            grid.slots().add(slot);
            throw new AssertionError("Expected immutable slots list");
        } catch (UnsupportedOperationException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void inventoryGridClampsInvalidDimensions() {
        UiInventoryGrid grid = new UiInventoryGrid(0, -1, 0, -4, Collections.<UiSlotBinding>emptyList());
        assertEquals(1, grid.columns());
        assertEquals(1, grid.rows());
        assertEquals(18, grid.slotSize());
        assertEquals(0, grid.gap());
    }

    @Test
    public void slotClickCarriesTypeAndModifiers() {
        UiInputModifiers modifiers = new UiInputModifiers(true, false, true);
        UiInventoryClick click = new UiInventoryClick("slot-1", 4, 1, 2, UiInventoryClickType.SWAP, modifiers);
        assertEquals("slot-1", click.slotId());
        assertEquals(4, click.slotIndex());
        assertEquals(1, click.mouseButton());
        assertEquals(2, click.hotbarIndex());
        assertEquals(UiInventoryClickType.SWAP, click.type());
        assertTrue(click.modifiers().shift());
        assertTrue(click.modifiers().alt());
    }

    @Test
    public void valueEqualityUsesStableReferenceSafeContracts() {
        Object raw = new Object();
        UiItemStackRef firstItem = UiItemStackRef.of(raw).displayName("Stone").count(4);
        UiItemStackRef sameRawItem = UiItemStackRef.of(raw).displayName("Stone").count(4);
        UiItemStackRef differentRawItem = UiItemStackRef.of(new Object()).displayName("Stone").count(4);

        assertEquals(firstItem, sameRawItem);
        assertEquals(firstItem.hashCode(), sameRawItem.hashCode());
        assertNotEquals(firstItem, differentRawItem);

        UiSlotClickHandler firstHandler = new UiSlotClickHandler() {
            @Override
            public boolean handle(UiInventoryClick click) {
                return true;
            }
        };
        UiSlotClickHandler secondHandler = new UiSlotClickHandler() {
            @Override
            public boolean handle(UiInventoryClick click) {
                return false;
            }
        };
        UiSlotBinding firstSlot = new UiSlotBinding("slot", 1, firstItem, true, "slot.png", firstHandler);
        UiSlotBinding sameSlotDifferentHandler = new UiSlotBinding("slot", 1, firstItem, true, "slot.png", secondHandler);
        assertEquals(firstSlot, sameSlotDifferentHandler);
        assertEquals(firstSlot.hashCode(), sameSlotDifferentHandler.hashCode());

        UiInventoryClick firstClick = new UiInventoryClick("slot", 1, 0, -1, UiInventoryClickType.PICKUP, UiInputModifiers.none());
        UiInventoryClick sameClick = new UiInventoryClick("slot", 1, 0, -1, UiInventoryClickType.PICKUP, UiInputModifiers.none());
        assertEquals(firstClick, sameClick);
        assertEquals(firstClick.hashCode(), sameClick.hashCode());
        assertEquals(new UiInventoryGrid(1, 1, 18, 0, Collections.singletonList(firstSlot)),
                new UiInventoryGrid(1, 1, 18, 0, Collections.singletonList(sameSlotDifferentHandler)));
    }
}
