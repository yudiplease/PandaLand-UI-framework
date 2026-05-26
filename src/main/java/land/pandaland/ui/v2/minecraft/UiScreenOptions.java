package land.pandaland.ui.v2.minecraft;

/**
 * Minecraft screen-level input and pause policy for v2 retained screens.
 */
public final class UiScreenOptions {
    private static final boolean GUI_SCREEN_PAUSES_GAME = true;

    private final boolean pauseGame;
    private final boolean closeOnEscape;
    private final boolean consumeInventoryKeys;
    private final boolean allowHotbarSwap;
    private final UiTextEnterBehavior textEnterBehavior;
    private final UiTextEscapeBehavior textEscapeBehavior;

    private UiScreenOptions(
            boolean pauseGame,
            boolean closeOnEscape,
            boolean consumeInventoryKeys,
            boolean allowHotbarSwap,
            UiTextEnterBehavior textEnterBehavior,
            UiTextEscapeBehavior textEscapeBehavior) {
        this.pauseGame = pauseGame;
        this.closeOnEscape = closeOnEscape;
        this.consumeInventoryKeys = consumeInventoryKeys;
        this.allowHotbarSwap = allowHotbarSwap;
        this.textEnterBehavior = textEnterBehavior == null ? UiTextEnterBehavior.COMMIT : textEnterBehavior;
        this.textEscapeBehavior = textEscapeBehavior == null ? UiTextEscapeBehavior.BLUR : textEscapeBehavior;
    }

    /**
     * Returns options matching vanilla {@code GuiScreen} behavior.
     *
     * @return default screen options
     */
    public static UiScreenOptions defaults() {
        return new UiScreenOptions(
                GUI_SCREEN_PAUSES_GAME,
                true,
                true,
                false,
                UiTextEnterBehavior.COMMIT,
                UiTextEscapeBehavior.BLUR);
    }

    /**
     * Reports whether opening this screen pauses the game.
     *
     * @return pause flag
     */
    public boolean pauseGame() {
        return pauseGame;
    }

    /**
     * Returns a copy with updated pause behavior.
     *
     * @param pauseGame pause flag
     * @return copied options
     */
    public UiScreenOptions pauseGame(boolean pauseGame) {
        return new UiScreenOptions(pauseGame, closeOnEscape, consumeInventoryKeys, allowHotbarSwap, textEnterBehavior, textEscapeBehavior);
    }

    /**
     * Reports whether Escape closes the screen when no focused input consumes it.
     *
     * @return close-on-escape flag
     */
    public boolean closeOnEscape() {
        return closeOnEscape;
    }

    /**
     * Returns a copy with updated Escape close policy.
     *
     * @param closeOnEscape close-on-escape flag
     * @return copied options
     */
    public UiScreenOptions closeOnEscape(boolean closeOnEscape) {
        return new UiScreenOptions(pauseGame, closeOnEscape, consumeInventoryKeys, allowHotbarSwap, textEnterBehavior, textEscapeBehavior);
    }

    /**
     * Reports whether inventory keys should be consumed by this screen.
     *
     * @return inventory-key policy
     */
    public boolean consumeInventoryKeys() {
        return consumeInventoryKeys;
    }

    /**
     * Returns a copy with updated inventory-key policy.
     *
     * @param consumeInventoryKeys inventory-key policy
     * @return copied options
     */
    public UiScreenOptions consumeInventoryKeys(boolean consumeInventoryKeys) {
        return new UiScreenOptions(pauseGame, closeOnEscape, consumeInventoryKeys, allowHotbarSwap, textEnterBehavior, textEscapeBehavior);
    }

    /**
     * Reports whether hotbar swap keys are allowed to pass through.
     *
     * @return hotbar swap policy
     */
    public boolean allowHotbarSwap() {
        return allowHotbarSwap;
    }

    /**
     * Returns a copy with updated hotbar swap policy.
     *
     * @param allowHotbarSwap hotbar swap policy
     * @return copied options
     */
    public UiScreenOptions allowHotbarSwap(boolean allowHotbarSwap) {
        return new UiScreenOptions(pauseGame, closeOnEscape, consumeInventoryKeys, allowHotbarSwap, textEnterBehavior, textEscapeBehavior);
    }

    /**
     * Returns focused text Enter behavior.
     *
     * @return text Enter behavior
     */
    public UiTextEnterBehavior textEnterBehavior() {
        return textEnterBehavior;
    }

    /**
     * Returns a copy with updated focused text Enter behavior.
     *
     * @param textEnterBehavior text Enter behavior
     * @return copied options
     */
    public UiScreenOptions textEnterBehavior(UiTextEnterBehavior textEnterBehavior) {
        return new UiScreenOptions(pauseGame, closeOnEscape, consumeInventoryKeys, allowHotbarSwap, textEnterBehavior, textEscapeBehavior);
    }

    /**
     * Returns focused text Escape behavior.
     *
     * @return text Escape behavior
     */
    public UiTextEscapeBehavior textEscapeBehavior() {
        return textEscapeBehavior;
    }

    /**
     * Returns a copy with updated focused text Escape behavior.
     *
     * @param textEscapeBehavior text Escape behavior
     * @return copied options
     */
    public UiScreenOptions textEscapeBehavior(UiTextEscapeBehavior textEscapeBehavior) {
        return new UiScreenOptions(pauseGame, closeOnEscape, consumeInventoryKeys, allowHotbarSwap, textEnterBehavior, textEscapeBehavior);
    }
}
