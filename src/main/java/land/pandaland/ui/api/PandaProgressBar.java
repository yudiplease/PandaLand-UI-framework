package land.pandaland.ui.api;

/**
 * Linear progress bar with animated display value.
 */
public final class PandaProgressBar extends PandaComponent {
    private float value;
    private String label = "";
    private final PandaAnimation displayValue;

    private PandaProgressBar(float value) {
        this.value = clamp(value);
        this.displayValue = PandaAnimations.progress(this.value);
    }

    /**
     * Creates a progress bar.
     *
     * @param value initial logical value, clamped to {@code 0..1}
     * @return progress bar component
     */
    public static PandaProgressBar of(float value) {
        return new PandaProgressBar(value);
    }

    /** Sets optional label text. */
    public PandaProgressBar label(String label) {
        this.label = label == null ? "" : label;
        return this;
    }

    /** Sets target progress value. */
    public PandaProgressBar value(float value) {
        this.value = clamp(value);
        displayValue.setTarget(this.value);
        return this;
    }

    /** @return target logical value */
    public float value() {
        return value;
    }

    /** @return current animated display value */
    public float displayValue() {
        return displayValue.value();
    }

    /** Enables or disables reduced motion for this bar. */
    public PandaProgressBar reducedMotion(boolean reducedMotion) {
        super.reducedMotion(reducedMotion);
        return this;
    }

    /** @return optional label text */
    public String label() {
        return label;
    }

    public int preferredWidth() {
        return 180;
    }

    public int preferredHeight() {
        return 14;
    }

    public void update(long deltaMs) {
        super.update(deltaMs);
        if (reducedMotion() || PandaUi.theme().reducedMotion()) {
            displayValue.update(Long.MAX_VALUE);
            return;
        }
        displayValue.update(deltaMs);
    }

    private static float clamp(float value) {
        if (Float.isNaN(value) || value < 0.0f) {
            return 0.0f;
        }
        if (value > 1.0f) {
            return 1.0f;
        }
        return value;
    }
}
