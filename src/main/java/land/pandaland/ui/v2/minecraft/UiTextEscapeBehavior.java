package land.pandaland.ui.v2.minecraft;

/**
 * Policy hook for Escape handling while a text input is focused.
 */
public enum UiTextEscapeBehavior {
    /**
     * Clear focused text input focus.
     */
    BLUR,
    /**
     * Allow the screen-level close policy to run.
     */
    CLOSE_SCREEN,
    /**
     * Do not consume Escape while text input is focused.
     */
    NONE
}
