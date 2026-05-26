package land.pandaland.ui.v2.minecraft;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class UiContainerBindingTest {
    @Test
    public void mapsSlotIdsToVanillaIndices() {
        UiContainerBinding binding = UiContainerBinding.builder()
                .map("slot-1", 10)
                .map("slot-2", 11)
                .build();

        assertEquals(10, binding.vanillaSlotIndex("slot-1"));
        assertEquals(-1, binding.vanillaSlotIndex("missing"));
        assertTrue(binding.hasSlot("slot-2"));
        assertFalse(binding.hasSlot("missing"));
    }

    @Test
    public void emptyBindingReturnsMissingIndex() {
        UiContainerBinding binding = UiContainerBinding.empty();

        assertEquals(-1, binding.vanillaSlotIndex("slot-1"));
        assertFalse(binding.hasSlot("slot-1"));
        assertTrue(binding.mappings().isEmpty());
    }

    @Test
    public void ignoresInvalidSlotIdsAndIndices() {
        UiContainerBinding binding = UiContainerBinding.builder()
                .map(null, 1)
                .map("", 2)
                .map("negative", -5)
                .map("valid", 3)
                .build();

        assertEquals(-1, binding.vanillaSlotIndex(null));
        assertEquals(-1, binding.vanillaSlotIndex(""));
        assertEquals(-1, binding.vanillaSlotIndex("negative"));
        assertEquals(3, binding.vanillaSlotIndex("valid"));
        assertEquals(1, binding.mappings().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void mappingsAreImmutable() {
        UiContainerBinding binding = UiContainerBinding.builder()
                .map("slot-1", 10)
                .build();
        Map<String, Integer> mappings = binding.mappings();

        mappings.put("slot-2", 11);
    }
}
