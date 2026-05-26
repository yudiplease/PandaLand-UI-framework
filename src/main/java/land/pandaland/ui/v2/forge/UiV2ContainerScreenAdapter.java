package land.pandaland.ui.v2.forge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.minecraft.UiContainerBinding;
import land.pandaland.ui.v2.minecraft.UiInputModifiers;
import land.pandaland.ui.v2.minecraft.UiInventoryClick;
import land.pandaland.ui.v2.minecraft.UiInventoryClickType;
import land.pandaland.ui.v2.minecraft.UiInventoryGrid;
import land.pandaland.ui.v2.minecraft.UiScreenOptions;
import land.pandaland.ui.v2.minecraft.UiSlotBinding;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;

/**
 * Minecraft {@link GuiContainer} adapter for retained v2 UI screens.
 *
 * <p>The adapter routes framework slot clicks before vanilla handling and uses
 * {@link UiContainerBinding} only as a delegation map. It deliberately does not
 * mutate server-authoritative inventory state itself.</p>
 */
@SideOnly(Side.CLIENT)
public class UiV2ContainerScreenAdapter extends GuiContainer {
    private final UiContainerBinding containerBinding;
    private final UiScreen initialScreen;
    private final UiScreenOptions options;
    private UiRuntime runtime;
    private long lastUpdateMs;

    public UiV2ContainerScreenAdapter(Container container, UiContainerBinding containerBinding, UiScreen screen) {
        this(container, containerBinding, screen, UiScreenOptions.defaults());
    }

    public UiV2ContainerScreenAdapter(Container container, UiContainerBinding containerBinding, UiScreen screen, UiScreenOptions options) {
        super(container);
        if (screen == null) {
            throw new IllegalArgumentException("screen cannot be null");
        }
        this.containerBinding = containerBinding == null ? UiContainerBinding.empty() : containerBinding;
        this.initialScreen = screen;
        this.options = options == null ? UiScreenOptions.defaults() : options;
    }

    public void initGui() {
        if (mc != null) {
            super.initGui();
        }
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
        if (mc == null) {
            return;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (runtime != null) {
            new UiV2MinecraftRenderer(mc).render(runtime);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawV2Background(mouseX, mouseY, partialTicks);
    }

    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (runtime == null) {
            passThroughMouseClicked(mouseX, mouseY, button);
            return;
        }

        HitSlot hitSlot = findSlotAt(mouseX, mouseY);
        if (hitSlot != null) {
            if (handleFrameworkSlotClick(hitSlot, button)) {
                return;
            }
            if (containerBinding.hasSlot(hitSlot.slot.id())) {
                passThroughVanillaSlotClick(hitSlot.slot, button, vanillaClickMode(button, hitSlot.hotbarIndex, currentModifiers()));
            }
            return;
        }

        if (!runtime.events().pointerDown(mouseX, mouseY, button)) {
            passThroughMouseClicked(mouseX, mouseY, button);
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
        if (runtime != null && runtime.events().keyTyped(character, keyCode)) {
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

    public void onGuiClosed() {
        onClose();
        super.onGuiClosed();
    }

    public boolean doesGuiPauseGame() {
        return options.pauseGame();
    }

    public UiContainerBinding containerBinding() {
        return containerBinding;
    }

    protected UiRuntime runtime() {
        return runtime;
    }

    protected UiScreenOptions options() {
        return options;
    }

    protected UiScreen createScreen() {
        return initialScreen;
    }

    protected void drawV2Background(int mouseX, int mouseY, float partialTicks) {
    }

    protected void passThroughMouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
    }

    protected void passThroughVanillaSlotClick(UiSlotBinding slot, int mouseButton, int clickMode) {
        int vanillaSlotIndex = slot == null ? -1 : containerBinding.vanillaSlotIndex(slot.id());
        if (vanillaSlotIndex < 0) {
            return;
        }
        Slot vanillaSlot = null;
        if (inventorySlots != null && vanillaSlotIndex < inventorySlots.inventorySlots.size()) {
            vanillaSlot = inventorySlots.getSlot(vanillaSlotIndex);
        }
        handleMouseClick(vanillaSlot, vanillaSlotIndex, mouseButton, clickMode);
    }

    protected void keyTypedUnhandled(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    protected void onOpen() {
    }

    protected void onClose() {
    }

    static UiInventoryClickType classifyClick(int button, int keyCode, int hotbarIndex, UiInputModifiers modifiers) {
        UiInputModifiers safeModifiers = modifiers == null ? UiInputModifiers.none() : modifiers;
        if (hotbarIndex >= 0 || (keyCode >= Keyboard.KEY_1 && keyCode <= Keyboard.KEY_9)) {
            return UiInventoryClickType.SWAP;
        }
        if (safeModifiers.shift()) {
            return UiInventoryClickType.QUICK_MOVE;
        }
        if (keyCode == Keyboard.KEY_Q) {
            return UiInventoryClickType.THROW;
        }
        return UiInventoryClickType.PICKUP;
    }

    static int vanillaClickMode(int mouseButton, int hotbarIndex, UiInputModifiers modifiers) {
        UiInventoryClickType type = classifyClick(mouseButton, currentEventKey(), hotbarIndex, modifiers);
        if (type == UiInventoryClickType.QUICK_MOVE) {
            return 1;
        }
        if (type == UiInventoryClickType.SWAP) {
            return 2;
        }
        if (type == UiInventoryClickType.CLONE) {
            return 3;
        }
        if (type == UiInventoryClickType.THROW) {
            return 4;
        }
        return 0;
    }

    private boolean handleFrameworkSlotClick(HitSlot hitSlot, int button) {
        UiSlotBinding slot = hitSlot == null ? null : hitSlot.slot;
        if (slot == null || !slot.enabled() || slot.clickHandler() == null) {
            return false;
        }
        UiInputModifiers modifiers = currentModifiers();
        int hotbarIndex = hotbarIndexFromKeyboard();
        if (hotbarIndex < 0) {
            hotbarIndex = hitSlot.hotbarIndex;
        }
        UiInventoryClickType type = classifyClick(button, currentEventKey(), hotbarIndex, modifiers);
        UiInventoryClick click = new UiInventoryClick(slot.id(), slot.index(), button, hotbarIndex, type, modifiers);
        return slot.clickHandler().handle(click);
    }

    private HitSlot findSlotAt(int mouseX, int mouseY) {
        return findSlotAt(runtime.screen().root(), mouseX, mouseY);
    }

    private static HitSlot findSlotAt(UiNode node, int mouseX, int mouseY) {
        if (node == null || !node.visible() || !node.enabled()) {
            return null;
        }
        List<UiNode> children = node.children();
        for (int i = children.size() - 1; i >= 0; i--) {
            HitSlot hit = findSlotAt(children.get(i), mouseX, mouseY);
            if (hit != null) {
                return hit;
            }
        }
        return hitOwnSlot(node, mouseX, mouseY);
    }

    private static HitSlot hitOwnSlot(UiNode node, int mouseX, int mouseY) {
        if (node == null || !node.bounds().contains(mouseX, mouseY)) {
            return null;
        }
        if (node.type() == UiNode.Type.SLOT) {
            return node.slotBinding() == null ? null : new HitSlot(node.slotBinding(), -1);
        }
        if (node.type() == UiNode.Type.INVENTORY_GRID) {
            return hitGridSlot(node, mouseX, mouseY);
        }
        if (node.type() == UiNode.Type.HOTBAR) {
            return hitHotbarSlot(node, mouseX, mouseY);
        }
        return null;
    }

    private static HitSlot hitGridSlot(UiNode node, int mouseX, int mouseY) {
        UiInventoryGrid grid = node.inventoryGrid();
        if (grid == null) {
            return null;
        }
        int cell = grid.slotSize() + grid.gap();
        int localX = mouseX - node.bounds().x;
        int localY = mouseY - node.bounds().y;
        int column = cell <= 0 ? 0 : localX / cell;
        int row = cell <= 0 ? 0 : localY / cell;
        int offsetX = cell <= 0 ? localX : localX % cell;
        int offsetY = cell <= 0 ? localY : localY % cell;
        if (column < 0 || column >= grid.columns() || row < 0 || row >= grid.rows()
                || offsetX >= grid.slotSize() || offsetY >= grid.slotSize()) {
            return null;
        }
        int index = row * grid.columns() + column;
        List<UiSlotBinding> slots = grid.slots();
        return index < slots.size() ? new HitSlot(slots.get(index), -1) : null;
    }

    private static HitSlot hitHotbarSlot(UiNode node, int mouseX, int mouseY) {
        List<UiSlotBinding> slots = node.slotBindings();
        if (slots == null || slots.isEmpty()) {
            return null;
        }
        int slotSize = slots.isEmpty() ? node.bounds().height : Math.max(1, node.bounds().width / slots.size());
        int index = (mouseX - node.bounds().x) / slotSize;
        if (index < 0 || index >= slots.size()) {
            return null;
        }
        return new HitSlot(slots.get(index), index);
    }

    private int hotbarIndexFromKeyboard() {
        for (int key = Keyboard.KEY_1; key <= Keyboard.KEY_9; key++) {
            if (isKeyDown(key)) {
                return key - Keyboard.KEY_1;
            }
        }
        return -1;
    }

    private static int currentEventKey() {
        return Keyboard.isCreated() ? Keyboard.getEventKey() : 0;
    }

    private static UiInputModifiers currentModifiers() {
        return new UiInputModifiers(
                isKeyDown(Keyboard.KEY_LSHIFT) || isKeyDown(Keyboard.KEY_RSHIFT),
                isKeyDown(Keyboard.KEY_LCONTROL) || isKeyDown(Keyboard.KEY_RCONTROL),
                isKeyDown(Keyboard.KEY_LMENU) || isKeyDown(Keyboard.KEY_RMENU));
    }

    private static boolean isKeyDown(int keyCode) {
        return Keyboard.isCreated() && Keyboard.isKeyDown(keyCode);
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

    private long currentTimeMs() {
        return System.currentTimeMillis();
    }

    private static final class HitSlot {
        private final UiSlotBinding slot;
        private final int hotbarIndex;

        private HitSlot(UiSlotBinding slot, int hotbarIndex) {
            this.slot = slot;
            this.hotbarIndex = hotbarIndex;
        }
    }
}
