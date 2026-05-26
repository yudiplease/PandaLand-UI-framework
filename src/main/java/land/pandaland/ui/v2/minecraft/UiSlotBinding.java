package land.pandaland.ui.v2.minecraft;

import java.util.Objects;

public final class UiSlotBinding {
    private final String id;
    private final int index;
    private final UiItemStackRef item;
    private final boolean enabled;
    private final String backgroundTexture;
    private final UiSlotClickHandler clickHandler;

    public UiSlotBinding(String id, int index, UiItemStackRef item, boolean enabled, String backgroundTexture, UiSlotClickHandler clickHandler) {
        this.id = id == null ? "" : id;
        this.index = Math.max(0, index);
        this.item = item == null ? UiItemStackRef.empty() : item;
        this.enabled = enabled;
        this.backgroundTexture = backgroundTexture == null ? "" : backgroundTexture;
        this.clickHandler = clickHandler;
    }

    public String id() {
        return id;
    }

    public int index() {
        return index;
    }

    public UiItemStackRef item() {
        return item;
    }

    public boolean enabled() {
        return enabled;
    }

    public String backgroundTexture() {
        return backgroundTexture;
    }

    public UiSlotClickHandler clickHandler() {
        return clickHandler;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UiSlotBinding)) {
            return false;
        }
        UiSlotBinding that = (UiSlotBinding) other;
        return index == that.index
                && enabled == that.enabled
                && Objects.equals(id, that.id)
                && Objects.equals(item, that.item)
                && Objects.equals(backgroundTexture, that.backgroundTexture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, index, item, enabled, backgroundTexture);
    }
}
