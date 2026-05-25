package land.pandaland.ui.v2.style;

public final class UiTransition {
    private final int durationMs;
    private float value;
    private float target;

    private UiTransition(float initial, int durationMs) {
        this.value = initial;
        this.target = initial;
        this.durationMs = Math.max(1, durationMs);
    }

    public static UiTransition smooth(float initial, int durationMs) {
        return new UiTransition(initial, durationMs);
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public void update(long deltaMs, boolean reducedMotion) {
        if (reducedMotion) {
            value = target;
            return;
        }
        float progress = Math.max(0.0F, Math.min(1.0F, (float) deltaMs / (float) durationMs));
        value += (target - value) * progress;
    }

    public float value() {
        return value;
    }
}
