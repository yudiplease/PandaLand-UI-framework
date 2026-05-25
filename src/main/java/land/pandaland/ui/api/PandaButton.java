package land.pandaland.ui.api;

/**
 * Clickable text button with PandaLand visual variants.
 */
public final class PandaButton extends PandaComponent {
    /**
     * Built-in button visual style.
     */
    public enum Kind {
        /** Primary action button. */
        PRIMARY,
        /** Secondary action button. */
        SECONDARY,
        /** Low-emphasis transparent button. */
        GHOST,
        /** Destructive or dangerous action button. */
        DANGER
    }

    private final String text;
    private final Kind kind;
    private Runnable onClick;

    private PandaButton(String text, Kind kind) {
        this.text = text == null ? "" : text;
        this.kind = kind;
        focusable(true);
    }

    /**
     * Creates a primary button.
     *
     * @param text visible text
     * @return button component
     */
    public static PandaButton primary(String text) {
        return new PandaButton(text, Kind.PRIMARY);
    }

    /** Creates a secondary button. */
    public static PandaButton secondary(String text) {
        return new PandaButton(text, Kind.SECONDARY);
    }

    /** Creates a low-emphasis ghost button. */
    public static PandaButton ghost(String text) {
        return new PandaButton(text, Kind.GHOST);
    }

    /** Creates a danger button. */
    public static PandaButton danger(String text) {
        return new PandaButton(text, Kind.DANGER);
    }

    /**
     * Registers a click handler.
     *
     * @param onClick handler invoked on left-click
     * @return this button
     */
    public PandaButton onClick(Runnable onClick) {
        this.onClick = onClick;
        return this;
    }

    /** @return visible button text */
    public String text() {
        return text;
    }

    /** @return button style kind */
    public Kind kind() {
        return kind;
    }

    public int preferredWidth() {
        return Math.max(96, text.length() * 8 + 32);
    }

    public int preferredHeight() {
        return 32;
    }

    public boolean mousePressed(int mouseX, int mouseY, int button) {
        return visible() && enabled() && button == 0 && bounds().contains(mouseX, mouseY);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible() || !enabled() || button != 0 || onClick == null || !bounds().contains(mouseX, mouseY)) {
            return false;
        }
        onClick.run();
        return true;
    }
}
