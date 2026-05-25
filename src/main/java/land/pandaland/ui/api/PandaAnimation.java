package land.pandaland.ui.api;

/**
 * Small linear animation helper used for hover, press, modal, and progress
 * transitions.
 */
public class PandaAnimation {
    private final long durationMs;
    private float current;
    private float start;
    private float target;
    private long elapsedMs;

    /**
     * Creates an animation at an initial value.
     *
     * @param initial initial and target value
     * @param durationMs duration in milliseconds; values below one are clamped
     */
    public PandaAnimation(float initial, long durationMs) {
        this.current = initial;
        this.start = initial;
        this.target = initial;
        this.durationMs = Math.max(1L, durationMs);
    }

    /**
     * Changes the target value and restarts interpolation from the current
     * value.
     *
     * @param target new target value
     */
    public void setTarget(float target) {
        if (this.target != target) {
            this.start = current;
            this.target = target;
            this.elapsedMs = 0L;
        }
    }

    /**
     * Advances the animation.
     *
     * @param deltaMs elapsed time in milliseconds; negative values are ignored
     */
    public void update(long deltaMs) {
        long safeDelta = Math.max(0L, deltaMs);
        if (safeDelta >= durationMs - elapsedMs) {
            elapsedMs = durationMs;
        } else {
            elapsedMs += safeDelta;
        }
        float progress = elapsedMs / (float) durationMs;
        current = start + (target - start) * progress;
    }

    /**
     * @return current interpolated value
     */
    public float value() {
        return current;
    }
}
