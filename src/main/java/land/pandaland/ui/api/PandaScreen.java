package land.pandaland.ui.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import land.pandaland.ui.api.PandaRect;
import land.pandaland.ui.render.MinecraftPandaRenderer;
import land.pandaland.ui.render.PandaScreenRenderer;
import land.pandaland.ui.runtime.PandaScreenRuntime;
import net.minecraft.client.gui.GuiScreen;

/**
 * Base Minecraft GUI screen backed by a PandaLand component tree.
 *
 * <p>Subclasses implement {@link #build(PandaLayout)} to add controls to the
 * root layout. The base class owns layout, input dispatch, modal management,
 * rendering, and per-frame updates.</p>
 */
@SideOnly(Side.CLIENT)
public abstract class PandaScreen extends GuiScreen {
    private final PandaLayout root = PandaLayout.vertical(8);
    private final PandaScreenRuntime runtime = new PandaScreenRuntime(root);
    private long lastUpdateMs;

    /**
     * Builds the screen content after Minecraft initializes the GUI size.
     *
     * @param root root layout that should receive screen components
     */
    protected abstract void build(PandaLayout root);

    /**
     * Called after the screen content is built and laid out.
     */
    protected void onOpen() {
    }

    /**
     * Called before the screen is closed and modal state is cleared.
     */
    protected void onClose() {
    }

    /**
     * Called once per Minecraft screen update before component updates.
     *
     * @param deltaMs elapsed time in milliseconds
     */
    protected void tick(long deltaMs) {
    }

    /**
     * Shows a modal component above the root layout.
     *
     * @param modal modal to show
     */
    protected final void showModal(PandaModal modal) {
        runtime.showModal(modal);
    }

    /**
     * Closes the topmost modal, if one is open.
     *
     * @return {@code true} when a modal was closed
     */
    protected final boolean closeTopModal() {
        return runtime.closeTopModal();
    }

    /**
     * @return root layout for this screen
     */
    protected final PandaLayout root() {
        return root;
    }

    public void initGui() {
        root.clear();
        runtime.clearModals();
        build(root);
        runtime.layout(new PandaRect(0, 0, width, height));
        lastUpdateMs = currentTimeMs();
        onOpen();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        runtime.mouseMoved(mouseX, mouseY);
        MinecraftPandaRenderer renderer = new MinecraftPandaRenderer(mc);
        PandaScreenRenderer.render(renderer, runtime, mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void updateScreen() {
        super.updateScreen();
        long now = currentTimeMs();
        long deltaMs = lastUpdateMs == 0L ? 0L : Math.max(0L, now - lastUpdateMs);
        lastUpdateMs = now;
        tick(deltaMs);
        runtime.update(deltaMs);
    }

    public void onGuiClosed() {
        onClose();
        runtime.clearModals();
        super.onGuiClosed();
    }

    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (!runtime.mousePressed(mouseX, mouseY, button)) {
            super.mouseClicked(mouseX, mouseY, button);
        }
    }

    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        if (button >= 0) {
            if (!runtime.mouseReleased(mouseX, mouseY, button)) {
                super.mouseMovedOrUp(mouseX, mouseY, button);
            }
            return;
        }
        runtime.mouseMoved(mouseX, mouseY);
        super.mouseMovedOrUp(mouseX, mouseY, button);
    }

    protected void mouseClickMove(int mouseX, int mouseY, int button, long dragTimeMs) {
        if (!runtime.mouseDragged(mouseX, mouseY, button, dragTimeMs)) {
            super.mouseClickMove(mouseX, mouseY, button, dragTimeMs);
        }
    }

    protected void keyTyped(char character, int keyCode) {
        if (!runtime.keyTyped(character, keyCode)) {
            super.keyTyped(character, keyCode);
        }
    }

    /**
     * Provides the current clock value for update deltas.
     *
     * @return current time in milliseconds
     */
    protected long currentTimeMs() {
        return System.currentTimeMillis();
    }
}
