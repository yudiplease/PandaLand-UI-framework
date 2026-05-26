package land.pandaland.ui.v2.minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class UiInventoryGrid {
    private final int columns;
    private final int rows;
    private final int slotSize;
    private final int gap;
    private final List<UiSlotBinding> slots;

    public UiInventoryGrid(int columns, int rows, int slotSize, int gap, List<UiSlotBinding> slots) {
        this.columns = Math.max(1, columns);
        this.rows = Math.max(1, rows);
        this.slotSize = slotSize <= 0 ? 18 : slotSize;
        this.gap = Math.max(0, gap);
        if (slots == null || slots.isEmpty()) {
            this.slots = Collections.emptyList();
        } else {
            List<UiSlotBinding> copy = new ArrayList<UiSlotBinding>();
            for (UiSlotBinding slot : slots) {
                if (slot != null) {
                    copy.add(slot);
                }
            }
            this.slots = copy.isEmpty() ? Collections.<UiSlotBinding>emptyList() : Collections.unmodifiableList(copy);
        }
    }

    public int columns() {
        return columns;
    }

    public int rows() {
        return rows;
    }

    public int slotSize() {
        return slotSize;
    }

    public int gap() {
        return gap;
    }

    public List<UiSlotBinding> slots() {
        return slots;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UiInventoryGrid)) {
            return false;
        }
        UiInventoryGrid that = (UiInventoryGrid) other;
        return columns == that.columns
                && rows == that.rows
                && slotSize == that.slotSize
                && gap == that.gap
                && Objects.equals(slots, that.slots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columns, rows, slotSize, gap, slots);
    }
}
