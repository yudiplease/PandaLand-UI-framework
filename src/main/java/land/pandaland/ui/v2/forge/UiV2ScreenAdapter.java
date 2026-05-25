package land.pandaland.ui.v2.forge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.layout.UiRect;
import net.minecraft.client.gui.GuiScreen;

/**
 * Minecraft {@link GuiScreen} adapter for retained v2 UI screens.
 *
 * <p>Subclasses may override {@link #createScreen()}, background, lifecycle,
 * and unhandled key hooks to build dynamic screens using current Minecraft
 * dimensions.</p>
 */
@SideOnly(Side.CLIENT)
public class UiV2ScreenAdapter extends GuiScreen {
    private final UiScreen initialScreen;
    private UiRuntime runtime;
    private long lastUpdateMs;

    protected UiV2ScreenAdapter() {
        this.initialScreen = null;
    }

    public UiV2ScreenAdapter(UiScreen screen) {
        this.initialScreen = screen;
    }

    public void initGui() {
        runtime = new UiRuntime(createScreen());
        runtime.layout(new UiRect(0, 0, width, height));
        lastUpdateMs = currentTimeMs();
        onOpen();
    }

    public void updateScreen() {
        super.updateScreen();
        long now = currentTimeMs();
        long deltaMs = lastUpdateMs == 0L ? 0L : Math.max(0L, now - lastUpdateMs);
        lastUpdateMs = now;
        runtime.update(deltaMs);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawV2Background(mouseX, mouseY, partialTicks);
        new UiV2MinecraftRenderer(mc).render(runtime);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void onGuiClosed() {
        onClose();
        super.onGuiClosed();
    }

    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (!runtime.events().pointerDown(mouseX, mouseY, button)) {
            super.mouseClicked(mouseX, mouseY, button);
        }
    }

    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        if (button >= 0) {
            if (!runtime.events().pointerUp(mouseX, mouseY, button)) {
                super.mouseMovedOrUp(mouseX, mouseY, button);
            }
            return;
        }
        super.mouseMovedOrUp(mouseX, mouseY, button);
    }

    protected void mouseClickMove(int mouseX, int mouseY, int button, long dragTimeMs) {
        if (!runtime.events().pointerDrag(mouseX, mouseY, button, dragTimeMs)) {
            super.mouseClickMove(mouseX, mouseY, button, dragTimeMs);
        }
    }

    protected void keyTyped(char character, int keyCode) {
        if (!runtime.events().keyTyped(character, keyCode)) {
            keyTypedUnhandled(character, keyCode);
        }
    }

    protected UiScreen createScreen() {
        if (initialScreen == null) {
            throw new IllegalStateException("createScreen must be overridden when no initial screen is supplied");
        }
        return initialScreen;
    }

    protected UiRuntime runtime() {
        return runtime;
    }

    protected void drawV2Background(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
    }

    protected void keyTypedUnhandled(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    protected void onOpen() {
    }

    protected void onClose() {
    }

    private long currentTimeMs() {
        return System.currentTimeMillis();
    }
}
