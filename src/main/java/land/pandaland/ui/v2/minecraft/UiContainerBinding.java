package land.pandaland.ui.v2.minecraft;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable mapping between retained UI slot ids and vanilla Minecraft
 * container slot indices.
 *
 * <p>The framework never mutates server-authoritative containers directly from
 * this model. It is only a lookup table used by Forge adapters to decide
 * whether a UI slot can be delegated to vanilla {@code GuiContainer}
 * handling.</p>
 */
public final class UiContainerBinding {
    private static final UiContainerBinding EMPTY = new UiContainerBinding(Collections.<String, Integer>emptyMap());

    private final Map<String, Integer> mappings;

    private UiContainerBinding(Map<String, Integer> mappings) {
        this.mappings = Collections.unmodifiableMap(new LinkedHashMap<String, Integer>(mappings));
    }

    /**
     * Creates an empty container binding.
     *
     * @return empty binding
     */
    public static UiContainerBinding empty() {
        return EMPTY;
    }

    /**
     * Creates a mutable builder for a container binding.
     *
     * @return binding builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Looks up the vanilla slot index for a retained UI slot id.
     *
     * @param slotId retained UI slot id
     * @return vanilla slot index, or {@code -1} when missing
     */
    public int vanillaSlotIndex(String slotId) {
        if (slotId == null || slotId.length() == 0) {
            return -1;
        }
        Integer index = mappings.get(slotId);
        return index == null ? -1 : index.intValue();
    }

    /**
     * Reports whether the binding contains a valid vanilla slot mapping.
     *
     * @param slotId retained UI slot id
     * @return {@code true} when a mapping exists
     */
    public boolean hasSlot(String slotId) {
        return vanillaSlotIndex(slotId) >= 0;
    }

    /**
     * Returns immutable slot id to vanilla index mappings.
     *
     * @return immutable mapping view
     */
    public Map<String, Integer> mappings() {
        return mappings;
    }

    /**
     * Mutable builder for immutable {@link UiContainerBinding} values.
     */
    public static final class Builder {
        private final Map<String, Integer> mappings = new LinkedHashMap<String, Integer>();

        private Builder() {
        }

        /**
         * Adds a valid slot mapping. Null/empty ids and negative indices are
         * ignored so callers can safely construct mappings from optional data.
         *
         * @param slotId retained UI slot id
         * @param vanillaSlotIndex vanilla container slot index
         * @return this builder
         */
        public Builder map(String slotId, int vanillaSlotIndex) {
            if (slotId != null && slotId.length() > 0 && vanillaSlotIndex >= 0) {
                mappings.put(slotId, Integer.valueOf(vanillaSlotIndex));
            }
            return this;
        }

        /**
         * Builds an immutable binding.
         *
         * @return immutable binding
         */
        public UiContainerBinding build() {
            if (mappings.isEmpty()) {
                return UiContainerBinding.empty();
            }
            return new UiContainerBinding(mappings);
        }
    }
}
