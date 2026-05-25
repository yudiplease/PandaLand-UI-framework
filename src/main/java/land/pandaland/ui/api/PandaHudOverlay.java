package land.pandaland.ui.api;

/**
 * Base class for HUD overlays rendered outside of normal screens.
 *
 * <p>Register overlays through {@link PandaUi#registerHud(PandaHudOverlay)}.
 * Lower priority values are rendered first.</p>
 */
public abstract class PandaHudOverlay {
    /**
     * @return render ordering priority
     */
    public int priority() {
        return 100;
    }

    /**
     * @return whether this overlay should update and render
     */
    public boolean visible() {
        return true;
    }

    /**
     * Updates overlay state.
     *
     * @param deltaMs elapsed time in milliseconds
     */
    public void update(long deltaMs) {
    }

    /**
     * Renders the overlay.
     *
     * @param renderer renderer to use
     */
    public void render(PandaRenderer renderer) {
    }
}
