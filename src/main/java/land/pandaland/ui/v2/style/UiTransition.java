package land.pandaland.ui.v2.style;

/**
 * Small value interpolator for UI animations.
 */
public final class UiTransition {
    private final int durationMs;
    private float value;
    private float target;

    private UiTransition(float initial, int durationMs) {
        this.value = initial;
        this.target = initial;
        this.durationMs = Math.max(1, durationMs);
    }

    /**
     * Creates a smooth transition.
     *
     * @param initial initial and target value
     * @param durationMs approximate transition duration in milliseconds
     * @return transition instance
     */
    public static UiTransition smooth(float initial, int durationMs) {
        return new UiTransition(initial, durationMs);
    }

    /**
     * Sets the target value.
     *
     * @param target target value
     */
    public void setTarget(float target) {
        this.target = target;
    }

    /**
     * Advances the transition.
     *
     * @param deltaMs elapsed milliseconds since the previous frame
     * @param reducedMotion when {@code true}, jumps directly to the target
     */
    public void update(long deltaMs, boolean reducedMotion) {
        if (reducedMotion) {
            value = target;
            return;
        }
        float progress = Math.max(0.0F, Math.min(1.0F, (float) deltaMs / (float) durationMs));
        value += (target - value) * progress;
    }

    /**
     * Returns the current interpolated value.
     *
     * @return current value
     */
    public float value() {
        return value;
    }
}
