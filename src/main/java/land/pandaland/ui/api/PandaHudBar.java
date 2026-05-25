package land.pandaland.ui.api;

/**
 * Compact HUD status bar with a text label and normalized progress value.
 */
public final class PandaHudBar extends PandaComponent {
    private final String label;
    private float value;

    private PandaHudBar(String label, float value) {
        this.label = label == null ? "" : label;
        this.value = clamp(value);
    }

    /**
     * Creates a HUD status bar.
     *
     * @param label visible label
     * @param value initial value, clamped to {@code 0..1}
     * @return HUD bar component
     */
    public static PandaHudBar status(String label, float value) {
        return new PandaHudBar(label, value);
    }

    /**
     * Updates the logical value.
     *
     * @param value value clamped to {@code 0..1}
     * @return this HUD bar
     */
    public PandaHudBar value(float value) {
        this.value = clamp(value);
        return this;
    }

    /**
     * @return visible label text
     */
    public String label() {
        return label;
    }

    /**
     * @return normalized value in the range {@code 0..1}
     */
    public float value() {
        return value;
    }

    public int preferredWidth() {
        return Math.max(120, label.length() * 6 + 40);
    }

    public int preferredHeight() {
        return 18;
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
