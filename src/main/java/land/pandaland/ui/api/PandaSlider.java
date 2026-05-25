package land.pandaland.ui.api;

/**
 * Horizontal value slider for numeric settings.
 */
public final class PandaSlider extends PandaComponent {
    private final String label;
    private String valueText;
    private float value;
    private boolean dragging;
    private ValueChange onChange;

    private PandaSlider(String label, String valueText, float value) {
        this.label = label == null ? "" : label;
        this.valueText = valueText == null ? "" : valueText;
        this.value = clamp01(value);
        focusable(true);
    }

    public static PandaSlider value(String label, String valueText, float value) {
        return new PandaSlider(label, valueText, value);
    }

    public PandaSlider onChange(ValueChange onChange) {
        this.onChange = onChange;
        return this;
    }

    public PandaSlider valueText(String valueText) {
        this.valueText = valueText == null ? "" : valueText;
        return this;
    }

    public String label() {
        return label;
    }

    public String valueText() {
        return valueText;
    }

    public float value() {
        return value;
    }

    public int preferredWidth() {
        return Math.max(150, label.length() * 7 + valueText.length() * 7 + 34);
    }

    public int preferredHeight() {
        return 22;
    }

    public boolean mousePressed(int mouseX, int mouseY, int button) {
        if (!visible() || !enabled() || button != 0 || !bounds().contains(mouseX, mouseY)) {
            return false;
        }
        dragging = true;
        updateFromMouse(mouseX);
        return true;
    }

    public boolean mouseReleased(int mouseX, int mouseY, int button) {
        if (!dragging || button != 0) {
            return false;
        }
        dragging = false;
        return true;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible() || !enabled() || button != 0 || !bounds().contains(mouseX, mouseY)) {
            return false;
        }
        updateFromMouse(mouseX);
        return true;
    }

    public boolean mouseDragged(int mouseX, int mouseY, int button, long dragTimeMs) {
        if (!visible() || !enabled() || !dragging || button != 0) {
            return false;
        }
        updateFromMouse(mouseX);
        return true;
    }

    private void updateFromMouse(int mouseX) {
        float next = clamp01((mouseX - bounds().x) / (float) Math.max(1, bounds().width));
        if (Math.abs(next - value) < 0.0001F) {
            return;
        }
        value = next;
        if (onChange != null) {
            onChange.changed(value);
        }
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    public interface ValueChange {
        void changed(float value);
    }
}
