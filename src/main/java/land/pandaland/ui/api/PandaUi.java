package land.pandaland.ui.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import land.pandaland.ui.integration.PandaClientUiBridge;
import land.pandaland.ui.runtime.PandaHudRegistry;
import land.pandaland.ui.runtime.PandaToastOverlay;
import land.pandaland.ui.theme.PandaThemeDefaults;

/**
 * Static facade for framework-wide UI services.
 *
 * <p>Use this class to access the active theme, register HUD overlays, show
 * toasts, and open screens on the client side.</p>
 */
public final class PandaUi {
    private static PandaTheme theme = PandaThemeDefaults.create();
    private static final PandaHudRegistry HUD_REGISTRY = new PandaHudRegistry();
    private static final PandaToastOverlay TOAST_OVERLAY = new PandaToastOverlay();

    private PandaUi() {
    }

    /**
     * @return current framework theme
     */
    public static PandaTheme theme() {
        return theme;
    }

    /**
     * Enables or disables decorative motion globally.
     *
     * @param reducedMotion {@code true} to snap animations to their targets
     */
    public static void setReducedMotion(boolean reducedMotion) {
        theme = PandaThemeDefaults.create(reducedMotion);
    }

    /**
     * Registers a HUD overlay if it is not already registered.
     *
     * @param overlay overlay to register
     */
    public static void registerHud(PandaHudOverlay overlay) {
        HUD_REGISTRY.register(overlay);
    }

    /**
     * Removes a HUD overlay.
     *
     * @param overlay overlay to remove
     */
    public static void unregisterHud(PandaHudOverlay overlay) {
        HUD_REGISTRY.unregister(overlay);
    }

    /**
     * @return immutable snapshot of registered overlays, sorted by priority
     */
    public static List<PandaHudOverlay> hudOverlays() {
        return HUD_REGISTRY.overlays();
    }

    /**
     * Shows a toast with the default duration.
     *
     * @param message toast message
     * @return created toast
     */
    public static PandaToast toast(String message) {
        return showToast(PandaToast.message(message));
    }

    /**
     * Shows a toast with a custom duration.
     *
     * @param message toast message
     * @param durationMs requested duration in milliseconds
     * @return created toast
     */
    public static PandaToast toast(String message, long durationMs) {
        return showToast(PandaToast.message(message, durationMs));
    }

    /**
     * Adds an existing toast to the managed toast overlay.
     *
     * @param toast toast to show
     * @return the same toast instance
     */
    public static PandaToast showToast(PandaToast toast) {
        if (toast == null) {
            throw new IllegalArgumentException("toast cannot be null");
        }
        ensureToastOverlayRegistered();
        TOAST_OVERLAY.add(toast);
        return toast;
    }

    /**
     * @return immutable snapshot of active managed toasts
     */
    public static List<PandaToast> activeToasts() {
        return TOAST_OVERLAY.toasts();
    }

    /**
     * Clears all managed toasts.
     */
    public static void clearToasts() {
        TOAST_OVERLAY.clear();
    }

    /**
     * Updates all registered HUD overlays.
     *
     * @param deltaMs elapsed time in milliseconds
     */
    public static void updateHud(long deltaMs) {
        HUD_REGISTRY.updateVisible(deltaMs);
    }

    /**
     * Renders all registered HUD overlays.
     *
     * @param renderer renderer to use
     */
    public static void renderHud(PandaRenderer renderer) {
        HUD_REGISTRY.renderVisible(renderer);
    }

    /**
     * Opens a Panda screen on the Minecraft client.
     *
     * @param screen screen to open
     */
    @SideOnly(Side.CLIENT)
    public static void open(PandaScreen screen) {
        PandaClientUiBridge.open(screen);
    }

    private static void ensureToastOverlayRegistered() {
        HUD_REGISTRY.register(TOAST_OVERLAY);
    }
}
