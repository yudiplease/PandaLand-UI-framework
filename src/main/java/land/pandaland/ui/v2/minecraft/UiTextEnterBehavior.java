package land.pandaland.ui.v2.minecraft;

/**
 * Policy hook for Enter handling while a text input is focused.
 */
public enum UiTextEnterBehavior {
    /**
     * Commit text input using the existing enter action.
     */
    COMMIT,
    /**
     * Commit text input and then clear focus.
     */
    BLUR,
    /**
     * Do not handle Enter specially while text input is focused.
     */
    NONE
}
