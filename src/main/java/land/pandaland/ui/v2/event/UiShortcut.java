package land.pandaland.ui.v2.event;

/**
 * Immutable keyboard shortcut registration for runtime-level or root-node
 * actions.
 *
 * <p>Shortcuts are matched against LWJGL/Minecraft key codes and optional
 * modifier requirements. Modified shortcuts may run before focused text input
 * editing. Plain printable shortcuts are skipped while a text input is focused
 * so normal typing is not intercepted.</p>
 */
public final class UiShortcut {
    private final int keyCode;
    private final boolean control;
    private final boolean shift;
    private final boolean alt;
    private final Runnable action;

    /**
     * Creates a shortcut without modifier requirements.
     *
     * <p>Plain printable shortcuts do not consume focused text input typing;
     * use a modified shortcut for commands that must preempt text editing.</p>
     *
     * @param keyCode LWJGL/Minecraft key code
     * @param action action to run when matched
     * @return shortcut registration
     */
    public static UiShortcut key(int keyCode, Runnable action) {
        return new UiShortcut(keyCode, false, false, false, action);
    }

    /**
     * Creates a shortcut.
     *
     * @param keyCode LWJGL/Minecraft key code
     * @param control whether either control key must be held
     * @param shift whether either shift key must be held
     * @param alt whether either alt key must be held
     * @param action action to run when matched
     */
    public UiShortcut(int keyCode, boolean control, boolean shift, boolean alt, Runnable action) {
        if (action == null) {
            throw new IllegalArgumentException("action cannot be null");
        }
        this.keyCode = keyCode;
        this.control = control;
        this.shift = shift;
        this.alt = alt;
        this.action = action;
    }

    /**
     * Returns the LWJGL/Minecraft key code.
     *
     * @return key code
     */
    public int keyCode() {
        return keyCode;
    }

    /**
     * Reports whether control is required.
     *
     * @return control requirement
     */
    public boolean control() {
        return control;
    }

    /**
     * Reports whether shift is required.
     *
     * @return shift requirement
     */
    public boolean shift() {
        return shift;
    }

    /**
     * Reports whether alt is required.
     *
     * @return alt requirement
     */
    public boolean alt() {
        return alt;
    }

    /**
     * Returns the shortcut action.
     *
     * @return action
     */
    public Runnable action() {
        return action;
    }

    /**
     * Reports whether the key and modifier state match this shortcut.
     *
     * @param keyCode typed key code
     * @param control whether control is currently held
     * @param shift whether shift is currently held
     * @param alt whether alt is currently held
     * @return {@code true} when this shortcut should run
     */
    public boolean matches(int keyCode, boolean control, boolean shift, boolean alt) {
        return this.keyCode == keyCode
                && this.control == control
                && this.shift == shift
                && this.alt == alt;
    }

    /**
     * Runs the shortcut action.
     */
    public void run() {
        action.run();
    }
}
