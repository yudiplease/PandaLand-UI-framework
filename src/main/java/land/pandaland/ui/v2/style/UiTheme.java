package land.pandaland.ui.v2.style;

/**
 * Immutable visual theme used by v2 render traversal.
 */
public final class UiTheme {
    private final String font;
    private final int panelRadius;
    private final int buttonRadius;
    private final UiColor panelBase;
    private final UiColor buttonBase;
    private final UiColor primaryAccent;
    private final UiColor secondaryAccent;
    private final UiColor dangerAccent;
    private final UiColor textPrimary;
    private final UiColor textMuted;
    private final boolean reducedMotion;

    private UiTheme(Builder builder) {
        font = builder.font;
        panelRadius = builder.panelRadius;
        buttonRadius = builder.buttonRadius;
        panelBase = builder.panelBase;
        buttonBase = builder.buttonBase;
        primaryAccent = builder.primaryAccent;
        secondaryAccent = builder.secondaryAccent;
        dangerAccent = builder.dangerAccent;
        textPrimary = builder.textPrimary;
        textMuted = builder.textMuted;
        reducedMotion = builder.reducedMotion;
    }

    /**
     * Creates the default PandaLand liquid-glass inspired theme.
     *
     * @return default PandaLand theme
     */
    public static UiTheme pandalandDefault() {
        return new Builder()
            .font("pandaland_ui:fonts/minecraft-rus.ttf")
            .panelRadius(16)
            .buttonRadius(14)
            .panelBase(new UiColor(0xCC101820))
            .buttonBase(new UiColor(0xDD1D2730))
            .primaryAccent(new UiColor(0xFF24F0D3))
            .secondaryAccent(new UiColor(0xFFFF4EC7))
            .dangerAccent(new UiColor(0xFFFF506A))
            .textPrimary(new UiColor(0xFFF2F8FF))
            .textMuted(new UiColor(0xFF9DABB7))
            .reducedMotion(false)
            .build();
    }

    /**
     * Returns the logical font resource id.
     *
     * @return font resource path
     */
    public String font() {
        return font;
    }

    /**
     * Returns panel corner radius.
     *
     * @return radius in scaled GUI pixels
     */
    public int panelRadius() {
        return panelRadius;
    }

    /**
     * Returns button corner radius.
     *
     * @return radius in scaled GUI pixels
     */
    public int buttonRadius() {
        return buttonRadius;
    }

    /**
     * Returns the base color for panels.
     *
     * @return panel color
     */
    public UiColor panelBase() {
        return panelBase;
    }

    /**
     * Returns the base color for buttons and controls.
     *
     * @return button color
     */
    public UiColor buttonBase() {
        return buttonBase;
    }

    /**
     * Returns the primary accent color.
     *
     * @return primary accent
     */
    public UiColor primaryAccent() {
        return primaryAccent;
    }

    /**
     * Returns the secondary accent color.
     *
     * @return secondary accent
     */
    public UiColor secondaryAccent() {
        return secondaryAccent;
    }

    /**
     * Returns the danger/error accent color.
     *
     * @return danger accent
     */
    public UiColor dangerAccent() {
        return dangerAccent;
    }

    /**
     * Returns the primary text color.
     *
     * @return primary text color
     */
    public UiColor textPrimary() {
        return textPrimary;
    }

    /**
     * Returns the muted text color.
     *
     * @return muted text color
     */
    public UiColor textMuted() {
        return textMuted;
    }

    /**
     * Reports whether animation should be reduced.
     *
     * @return {@code true} when motion-sensitive animation should be disabled
     */
    public boolean reducedMotion() {
        return reducedMotion;
    }

    private static final class Builder {
        private String font = "";
        private int panelRadius;
        private int buttonRadius;
        private UiColor panelBase = new UiColor(0);
        private UiColor buttonBase = new UiColor(0);
        private UiColor primaryAccent = new UiColor(0);
        private UiColor secondaryAccent = new UiColor(0);
        private UiColor dangerAccent = new UiColor(0);
        private UiColor textPrimary = new UiColor(0);
        private UiColor textMuted = new UiColor(0);
        private boolean reducedMotion;

        private Builder font(String font) {
            this.font = font == null ? "" : font;
            return this;
        }

        private Builder panelRadius(int panelRadius) {
            this.panelRadius = Math.max(0, panelRadius);
            return this;
        }

        private Builder buttonRadius(int buttonRadius) {
            this.buttonRadius = Math.max(0, buttonRadius);
            return this;
        }

        private Builder panelBase(UiColor panelBase) {
            this.panelBase = panelBase;
            return this;
        }

        private Builder buttonBase(UiColor buttonBase) {
            this.buttonBase = buttonBase;
            return this;
        }

        private Builder primaryAccent(UiColor primaryAccent) {
            this.primaryAccent = primaryAccent;
            return this;
        }

        private Builder secondaryAccent(UiColor secondaryAccent) {
            this.secondaryAccent = secondaryAccent;
            return this;
        }

        private Builder dangerAccent(UiColor dangerAccent) {
            this.dangerAccent = dangerAccent;
            return this;
        }

        private Builder textPrimary(UiColor textPrimary) {
            this.textPrimary = textPrimary;
            return this;
        }

        private Builder textMuted(UiColor textMuted) {
            this.textMuted = textMuted;
            return this;
        }

        private Builder reducedMotion(boolean reducedMotion) {
            this.reducedMotion = reducedMotion;
            return this;
        }

        private UiTheme build() {
            return new UiTheme(this);
        }
    }
}
