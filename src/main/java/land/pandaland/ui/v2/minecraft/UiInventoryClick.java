package land.pandaland.ui.v2.minecraft;

import java.util.Objects;

public final class UiInventoryClick {
    private final String slotId;
    private final int slotIndex;
    private final int mouseButton;
    private final int hotbarIndex;
    private final UiInventoryClickType type;
    private final UiInputModifiers modifiers;

    public UiInventoryClick(String slotId, int slotIndex, int mouseButton, int hotbarIndex, UiInventoryClickType type, UiInputModifiers modifiers) {
        this.slotId = slotId == null ? "" : slotId;
        this.slotIndex = slotIndex;
        this.mouseButton = mouseButton;
        this.hotbarIndex = hotbarIndex;
        this.type = type == null ? UiInventoryClickType.PICKUP : type;
        this.modifiers = modifiers == null ? UiInputModifiers.none() : modifiers;
    }

    public String slotId() {
        return slotId;
    }

    public int slotIndex() {
        return slotIndex;
    }

    public int mouseButton() {
        return mouseButton;
    }

    public int hotbarIndex() {
        return hotbarIndex;
    }

    public UiInventoryClickType type() {
        return type;
    }

    public UiInputModifiers modifiers() {
        return modifiers;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UiInventoryClick)) {
            return false;
        }
        UiInventoryClick that = (UiInventoryClick) other;
        return slotIndex == that.slotIndex
                && mouseButton == that.mouseButton
                && hotbarIndex == that.hotbarIndex
                && Objects.equals(slotId, that.slotId)
                && type == that.type
                && Objects.equals(modifiers, that.modifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotId, slotIndex, mouseButton, hotbarIndex, type, modifiers);
    }
}
