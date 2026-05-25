package land.pandaland.ui.v2.event;

public final class UiPointerEvent {
    public final int x;
    public final int y;
    public final int button;
    public final long dragTimeMs;

    public UiPointerEvent(int x, int y, int button, long dragTimeMs) {
        this.x = x;
        this.y = y;
        this.button = button;
        this.dragTimeMs = Math.max(0L, dragTimeMs);
    }
}
