package land.pandaland.ui.api;

/**
 * Rendering contract used by the framework traversal layer.
 *
 * <p>Feature mods normally do not implement this interface unless they need a
 * custom renderer for tests or tooling. Minecraft rendering is provided by the
 * framework implementation.</p>
 */
public interface PandaRenderer {
    /** Renders a glass panel. */
    void panel(PandaPanel panel);

    /** Renders a button. */
    void button(PandaButton button, boolean hovered);

    /** Renders a text label. */
    void label(PandaLabel label);

    /** Renders a textured icon. */
    void icon(PandaIcon icon);

    /** Renders a list control. */
    void list(PandaList list, int hoveredIndex);

    /** Renders a progress bar. */
    void progress(PandaProgressBar progressBar);

    /** Renders a horizontal slider. */
    void slider(PandaSlider slider, boolean hovered);

    /** Renders a compact HUD bar. */
    void hudBar(PandaHudBar hudBar);

    /** Renders a tab control. */
    void tabs(PandaTabs tabs, int hoveredIndex);

    /** Renders a modal frame. */
    void modal(PandaModal modal);

    /** Renders a toast notification. */
    void toast(PandaToast toast);
}
