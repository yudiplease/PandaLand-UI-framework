package land.pandaland.ui.v2.minecraft;

import java.util.Objects;

public final class UiInputModifiers {
    private static final UiInputModifiers NONE = new UiInputModifiers(false, false, false);

    private final boolean shift;
    private final boolean ctrl;
    private final boolean alt;

    public UiInputModifiers(boolean shift, boolean ctrl, boolean alt) {
        this.shift = shift;
        this.ctrl = ctrl;
        this.alt = alt;
    }

    public static UiInputModifiers none() {
        return NONE;
    }

    public boolean shift() {
        return shift;
    }

    public boolean ctrl() {
        return ctrl;
    }

    public boolean alt() {
        return alt;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UiInputModifiers)) {
            return false;
        }
        UiInputModifiers that = (UiInputModifiers) other;
        return shift == that.shift && ctrl == that.ctrl && alt == that.alt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shift, ctrl, alt);
    }
}
