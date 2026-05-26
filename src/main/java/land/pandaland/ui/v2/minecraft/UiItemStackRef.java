package land.pandaland.ui.v2.minecraft;

import java.util.Objects;

/**
 * Renderer-safe wrapper for an optional Minecraft item stack.
 */
public final class UiItemStackRef {
    private static final UiItemStackRef EMPTY = new UiItemStackRef(null, "", 0, 0, 0, false);

    private final Object rawStack;
    private final String displayName;
    private final int count;
    private final int damage;
    private final int maxDamage;
    private final boolean enchanted;

    private UiItemStackRef(Object rawStack, String displayName, int count, int damage, int maxDamage, boolean enchanted) {
        this.rawStack = rawStack;
        this.displayName = displayName == null ? "" : displayName;
        this.count = Math.max(0, count);
        this.damage = Math.max(0, damage);
        this.maxDamage = Math.max(0, maxDamage);
        this.enchanted = enchanted;
    }

    public static UiItemStackRef empty() {
        return EMPTY;
    }

    public static UiItemStackRef of(Object rawStack) {
        if (rawStack == null) {
            return EMPTY;
        }
        return new UiItemStackRef(rawStack, "", 1, 0, 0, false);
    }

    public Object rawStack() {
        return rawStack;
    }

    public boolean isEmpty() {
        return rawStack == null || count <= 0;
    }

    public String displayName() {
        return displayName;
    }

    public int count() {
        return count;
    }

    public int damage() {
        return damage;
    }

    public int maxDamage() {
        return maxDamage;
    }

    public boolean enchanted() {
        return enchanted;
    }

    public UiItemStackRef displayName(String displayName) {
        return new UiItemStackRef(rawStack, displayName, count, damage, maxDamage, enchanted);
    }

    public UiItemStackRef count(int count) {
        return new UiItemStackRef(rawStack, displayName, count, damage, maxDamage, enchanted);
    }

    public UiItemStackRef damage(int damage, int maxDamage) {
        return new UiItemStackRef(rawStack, displayName, count, damage, maxDamage, enchanted);
    }

    public UiItemStackRef enchanted(boolean enchanted) {
        return new UiItemStackRef(rawStack, displayName, count, damage, maxDamage, enchanted);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UiItemStackRef)) {
            return false;
        }
        UiItemStackRef that = (UiItemStackRef) other;
        return count == that.count
                && damage == that.damage
                && maxDamage == that.maxDamage
                && enchanted == that.enchanted
                && rawStack == that.rawStack
                && Objects.equals(displayName, that.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(System.identityHashCode(rawStack), displayName, count, damage, maxDamage, enchanted);
    }
}
