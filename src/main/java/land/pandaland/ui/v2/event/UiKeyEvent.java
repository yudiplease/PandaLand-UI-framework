package land.pandaland.ui.v2.event;

/**
 * Immutable keyboard event payload.
 *
 * <p>The current dispatcher uses raw Minecraft key callbacks directly, but this
 * value object is kept as the public event shape for adapters and tests.</p>
 */
public final class UiKeyEvent {
    /**
     * Typed character, or the null character for non-text keys.
     */
    public final char character;

    /**
     * LWJGL/Minecraft key code.
     */
    public final int keyCode;

    /**
     * Creates a keyboard event.
     *
     * @param character typed character
     * @param keyCode LWJGL/Minecraft key code
     */
    public UiKeyEvent(char character, int keyCode) {
        this.character = character;
        this.keyCode = keyCode;
    }
}
