package land.pandaland.ui.api;

/**
 * Theme tokens used by framework renderers and components.
 */
public interface PandaTheme {
    /** @return translucent panel base color */
    PandaColor panelBase();

    /** @return default button base color */
    PandaColor buttonBase();

    /** @return primary cyan/jade accent color */
    PandaColor primaryAccent();

    /** @return danger/action warning accent color */
    PandaColor dangerAccent();

    /** @return primary readable text color */
    PandaColor textPrimary();

    /** @return muted secondary text color */
    PandaColor textMuted();

    /** @return default visual button radius in UI pixels */
    int buttonRadius();

    /** @return default visual panel radius in UI pixels */
    int panelRadius();

    /** @return base spacing unit in UI pixels */
    int spacing();

    /** @return whether decorative motion should be reduced */
    boolean reducedMotion();
}
