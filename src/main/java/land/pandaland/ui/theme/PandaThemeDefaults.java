package land.pandaland.ui.theme;

import land.pandaland.ui.api.PandaColor;
import land.pandaland.ui.api.PandaTheme;

public final class PandaThemeDefaults {
    private PandaThemeDefaults() {
    }

    public static PandaTheme create() {
        return new DefaultPandaTheme(false);
    }

    public static PandaTheme create(boolean reducedMotion) {
        return new DefaultPandaTheme(reducedMotion);
    }

    private static final class DefaultPandaTheme implements PandaTheme {
        private final boolean reducedMotion;

        private DefaultPandaTheme(boolean reducedMotion) {
            this.reducedMotion = reducedMotion;
        }

        public PandaColor panelBase() {
            return new PandaColor(0x172026DD);
        }

        public PandaColor buttonBase() {
            return new PandaColor(0x1D2730E8);
        }

        public PandaColor primaryAccent() {
            return new PandaColor(0x27F3D6FF);
        }

        public PandaColor dangerAccent() {
            return new PandaColor(0xFF5E9BFF);
        }

        public PandaColor textPrimary() {
            return new PandaColor(0xEDF7FFFF);
        }

        public PandaColor textMuted() {
            return new PandaColor(0x9FB4B8FF);
        }

        public int buttonRadius() {
            return 12;
        }

        public int panelRadius() {
            return 16;
        }

        public int spacing() {
            return 8;
        }

        public boolean reducedMotion() {
            return reducedMotion;
        }
    }
}
