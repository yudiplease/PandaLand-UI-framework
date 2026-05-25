package land.pandaland.ui.api;

/**
 * Simple text label.
 */
public final class PandaLabel extends PandaComponent {
    private final String text;

    private PandaLabel(String text) {
        this.text = text == null ? "" : text;
    }

    /**
     * Creates a text label.
     *
     * @param text label text
     * @return label component
     */
    public static PandaLabel text(String text) {
        return new PandaLabel(text);
    }

    /** @return label text */
    public String text() {
        return text;
    }

    public int preferredWidth() {
        return Math.max(20, text.length() * 6);
    }

    public int preferredHeight() {
        return 12;
    }
}
