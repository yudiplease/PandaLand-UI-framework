package land.pandaland.ui.v2.render;

import land.pandaland.ui.v2.layout.UiRect;

/**
 * Renderer-independent custom draw hook for advanced or feature-owned widgets.
 *
 * <p>The hook receives the command list and logical widget bounds so it can
 * append framework render commands without depending on Minecraft classes.</p>
 */
public interface UiCustomDraw {
    /**
     * Appends custom renderer-independent commands.
     *
     * @param commands command list to append to
     * @param bounds logical widget bounds in scaled GUI pixels
     */
    void draw(UiRenderList commands, UiRect bounds);
}
