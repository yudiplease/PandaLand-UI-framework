package land.pandaland.ui.v2.forge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.minecraft.UiScreenOptions;
import land.pandaland.ui.v2.minecraft.UiTextEnterBehavior;
import land.pandaland.ui.v2.minecraft.UiTextEscapeBehavior;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

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
    private final UiScreenOptions options;
    private UiRuntime runtime;
    private long lastUpdateMs;

    protected UiV2ScreenAdapter() {
        this.initialScreen = null;
        this.options = UiScreenOptions.defaults();
    }

    public UiV2ScreenAdapter(UiScreen screen) {
        this(screen, UiScreenOptions.defaults());
    }

    public UiV2ScreenAdapter(UiScreen screen, UiScreenOptions options) {
        this.initialScreen = screen;
        this.options = options == null ? UiScreenOptions.defaults() : options;
    }

    public void initGui() {
        runtime = new UiRuntime(createScreen());
        runtime.layout(new UiRect(0, 0, width, height));
        lastUpdateMs = currentTimeMs();
        onOpen();
    }

    public void updateScreen() {
        super.updateScreen();
        if (runtime == null) {
            return;
        }
        long now = currentTimeMs();
        long deltaMs = lastUpdateMs == 0L ? 0L : Math.max(0L, now - lastUpdateMs);
        lastUpdateMs = now;
        runtime.update(deltaMs);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawV2Background(mouseX, mouseY, partialTicks);
        if (runtime != null) {
            new UiV2MinecraftRenderer(mc).render(runtime);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void onGuiClosed() {
        onClose();
        super.onGuiClosed();
    }

    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (runtime == null || !runtime.events().pointerDown(mouseX, mouseY, button)) {
            super.mouseClicked(mouseX, mouseY, button);
        }
    }

    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        if (button >= 0) {
            if (runtime == null || !runtime.events().pointerUp(mouseX, mouseY, button)) {
                super.mouseMovedOrUp(mouseX, mouseY, button);
            }
            return;
        }
        super.mouseMovedOrUp(mouseX, mouseY, button);
    }

    protected void mouseClickMove(int mouseX, int mouseY, int button, long dragTimeMs) {
        if (runtime == null || !runtime.events().pointerDrag(mouseX, mouseY, button, dragTimeMs)) {
            super.mouseClickMove(mouseX, mouseY, button, dragTimeMs);
        }
    }

    public void handleMouseInput() {
        super.handleMouseInput();
        int amount = Mouse.getEventDWheel();
        if (amount != 0 && runtime != null) {
            int mouseX = Mouse.getEventX() * width / mc.displayWidth;
            int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
            runtime.events().pointerWheel(mouseX, mouseY, amount);
        }
    }

    protected void keyTyped(char character, int keyCode) {
        TextPolicyResult textPolicy = handleFocusedTextPolicy(character, keyCode);
        if (textPolicy == TextPolicyResult.CONSUMED) {
            return;
        }
        if (textPolicy == TextPolicyResult.UNHANDLED) {
            keyTypedUnhandled(character, keyCode);
            return;
        }
        if (textPolicy != TextPolicyResult.SKIP_RUNTIME
                && runtime != null
                && runtime.events().keyTyped(character, keyCode)) {
            return;
        }
        if (keyCode == Keyboard.KEY_ESCAPE && !options.closeOnEscape()) {
            return;
        }
        if (consumeInventoryKey(keyCode)) {
            return;
        }
        if (keyCode >= Keyboard.KEY_1 && keyCode <= Keyboard.KEY_9 && !options.allowHotbarSwap()) {
            return;
        }
        keyTypedUnhandled(character, keyCode);
    }

    public boolean doesGuiPauseGame() {
        return options.pauseGame();
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

    protected UiScreenOptions options() {
        return options;
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

    private boolean consumeInventoryKey(int keyCode) {
        if (!options.consumeInventoryKeys()) {
            return false;
        }
        if (mc == null || mc.gameSettings == null || mc.gameSettings.keyBindInventory == null) {
            return false;
        }
        return keyCode == mc.gameSettings.keyBindInventory.getKeyCode();
    }

    private TextPolicyResult handleFocusedTextPolicy(char character, int keyCode) {
        if (runtime == null || runtime.focus().focused() == null
                || runtime.focus().focused().type() != UiNode.Type.TEXT_INPUT) {
            return TextPolicyResult.NOT_APPLICABLE;
        }
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            UiTextEnterBehavior behavior = options.textEnterBehavior();
            if (behavior == UiTextEnterBehavior.NONE) {
                return TextPolicyResult.UNHANDLED;
            }
            boolean handled = runtime.events().keyTyped(character, keyCode);
            if (behavior == UiTextEnterBehavior.BLUR) {
                runtime.focus().clear();
            }
            return handled ? TextPolicyResult.CONSUMED : TextPolicyResult.UNHANDLED;
        }
        if (keyCode == Keyboard.KEY_ESCAPE) {
            UiTextEscapeBehavior behavior = options.textEscapeBehavior();
            if (behavior == UiTextEscapeBehavior.BLUR) {
                runtime.focus().clear();
                return TextPolicyResult.CONSUMED;
            }
            return TextPolicyResult.SKIP_RUNTIME;
        }
        return TextPolicyResult.NOT_APPLICABLE;
    }

    private enum TextPolicyResult {
        NOT_APPLICABLE,
        CONSUMED,
        UNHANDLED,
        SKIP_RUNTIME
    }
}
