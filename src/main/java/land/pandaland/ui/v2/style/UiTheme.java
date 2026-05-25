package land.pandaland.ui.v2.style;

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

    public String font() {
        return font;
    }

    public int panelRadius() {
        return panelRadius;
    }

    public int buttonRadius() {
        return buttonRadius;
    }

    public UiColor panelBase() {
        return panelBase;
    }

    public UiColor buttonBase() {
        return buttonBase;
    }

    public UiColor primaryAccent() {
        return primaryAccent;
    }

    public UiColor secondaryAccent() {
        return secondaryAccent;
    }

    public UiColor dangerAccent() {
        return dangerAccent;
    }

    public UiColor textPrimary() {
        return textPrimary;
    }

    public UiColor textMuted() {
        return textMuted;
    }

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
