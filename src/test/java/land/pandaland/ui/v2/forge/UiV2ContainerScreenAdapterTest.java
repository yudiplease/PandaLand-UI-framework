package land.pandaland.ui.v2.forge;

import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.minecraft.UiContainerBinding;
import land.pandaland.ui.v2.minecraft.UiInputModifiers;
import land.pandaland.ui.v2.minecraft.UiInventoryClick;
import land.pandaland.ui.v2.minecraft.UiInventoryClickType;
import land.pandaland.ui.v2.minecraft.UiInventoryGrid;
import land.pandaland.ui.v2.minecraft.UiItemStackRef;
import land.pandaland.ui.v2.minecraft.UiScreenOptions;
import land.pandaland.ui.v2.minecraft.UiSlotBinding;
import land.pandaland.ui.v2.minecraft.UiSlotClickHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.junit.Test;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public final class UiV2ContainerScreenAdapterTest {
    @Test
    public void canInstantiateWithContainerBindingScreenAndOptions() {
        UiV2ContainerScreenAdapter adapter = new UiV2ContainerScreenAdapter(
                new TestContainer(),
                UiContainerBinding.empty(),
                simpleScreen(null),
                UiScreenOptions.defaults().pauseGame(false));

        assertFalse(adapter.doesGuiPauseGame());
        assertNotNull(adapter.containerBinding());
        assertEquals(UiContainerBinding.empty(), adapter.containerBinding());
    }

    @Test
    public void drawScreenIsHeadlessSafeAfterInitGui() {
        UiV2ContainerScreenAdapter adapter = new UiV2ContainerScreenAdapter(
                new TestContainer(),
                UiContainerBinding.empty(),
                simpleScreen(null),
                UiScreenOptions.defaults());
        adapter.initGui();

        adapter.drawScreen(0, 0, 0.0F);
    }

    @Test
    public void frameworkSlotHandlerConsumesClickBeforeVanillaPassThrough() {
        final AtomicReference<UiInventoryClick> captured = new AtomicReference<UiInventoryClick>();
        UiSlotBinding slot = new UiSlotBinding("slot-1", 0, UiItemStackRef.empty(), true, "", new UiSlotClickHandler() {
            public boolean handle(UiInventoryClick click) {
                captured.set(click);
                return true;
            }
        });
        RecordingContainerScreenAdapter adapter = new RecordingContainerScreenAdapter(
                new TestContainer(),
                UiContainerBinding.builder().map("slot-1", 3).build(),
                simpleScreen(slot),
                UiScreenOptions.defaults());
        adapter.initGui();
        adapter.runtime().layout(new UiRect(0, 0, 120, 80));

        adapter.mouseClicked(5, 5, 0);

        assertEquals(0, adapter.vanillaClicks);
        assertNotNull(captured.get());
        assertEquals("slot-1", captured.get().slotId());
        assertEquals(UiInventoryClickType.PICKUP, captured.get().type());
    }

    @Test
    public void mappedSlotFallsThroughToVanillaWhenFrameworkDoesNotHandle() {
        UiSlotBinding slot = new UiSlotBinding("slot-1", 0, UiItemStackRef.empty(), true, "", null);
        RecordingContainerScreenAdapter adapter = new RecordingContainerScreenAdapter(
                new TestContainer(),
                UiContainerBinding.builder().map("slot-1", 3).build(),
                simpleScreen(slot),
                UiScreenOptions.defaults());
        adapter.initGui();
        adapter.runtime().layout(new UiRect(0, 0, 120, 80));

        adapter.mouseClicked(5, 5, 0);

        assertEquals(1, adapter.vanillaClicks);
        assertEquals(3, adapter.lastVanillaSlotIndex);
    }

    @Test
    public void unmappedFrameworkSlotConsumesClickToProtectVanillaSlots() {
        UiSlotBinding slot = new UiSlotBinding("slot-1", 0, UiItemStackRef.empty(), true, "", null);
        RecordingContainerScreenAdapter adapter = new RecordingContainerScreenAdapter(
                new TestContainer(),
                UiContainerBinding.empty(),
                simpleScreen(slot),
                UiScreenOptions.defaults());
        adapter.initGui();
        adapter.runtime().layout(new UiRect(0, 0, 120, 80));

        adapter.mouseClicked(5, 5, 0);

        assertEquals(0, adapter.vanillaClicks);
    }

    @Test
    public void inventoryGridRoutesConcreteClickedSlotInsteadOfFirstSlot() {
        UiSlotBinding first = new UiSlotBinding("grid-0", 0, UiItemStackRef.empty(), true, "", null);
        UiSlotBinding second = new UiSlotBinding("grid-1", 1, UiItemStackRef.empty(), true, "", null);
        final UiInventoryGrid grid = new UiInventoryGrid(2, 1, 18, 2, Arrays.asList(first, second));
        UiScreen screen = Ui.screen("grid-container").root(new Ui.RootBuilderConsumer() {
            public void build(Ui.NodeBuilder root) {
                root.absolute().inventoryGrid(grid);
            }
        }).build();
        RecordingContainerScreenAdapter adapter = new RecordingContainerScreenAdapter(
                new TestContainer(),
                UiContainerBinding.builder().map("grid-1", 7).build(),
                screen,
                UiScreenOptions.defaults());
        adapter.initGui();
        adapter.runtime().layout(new UiRect(0, 0, 120, 80));

        adapter.mouseClicked(22, 5, 0);

        assertEquals(1, adapter.vanillaClicks);
        assertEquals(7, adapter.lastVanillaSlotIndex);
    }

    @Test
    public void hotbarRoutesConcreteClickedSlotInsteadOfFirstSlot() {
        final UiSlotBinding first = new UiSlotBinding("hotbar-0", 0, UiItemStackRef.empty(), true, "", null);
        final UiSlotBinding second = new UiSlotBinding("hotbar-1", 1, UiItemStackRef.empty(), true, "", null);
        UiScreen screen = Ui.screen("hotbar-container").root(new Ui.RootBuilderConsumer() {
            public void build(Ui.NodeBuilder root) {
                root.absolute().hotbar(Arrays.asList(first, second), 0, 18);
            }
        }).build();
        RecordingContainerScreenAdapter adapter = new RecordingContainerScreenAdapter(
                new TestContainer(),
                UiContainerBinding.builder().map("hotbar-1", 8).build(),
                screen,
                UiScreenOptions.defaults());
        adapter.initGui();
        adapter.runtime().layout(new UiRect(0, 0, 120, 80));

        adapter.mouseClicked(20, 5, 0);

        assertEquals(1, adapter.vanillaClicks);
        assertEquals(8, adapter.lastVanillaSlotIndex);
    }

    @Test
    public void classifiesMinecraftInventoryClickTypes() {
        assertEquals(UiInventoryClickType.QUICK_MOVE,
                UiV2ContainerScreenAdapter.classifyClick(0, Keyboard.KEY_LSHIFT, -1, new UiInputModifiers(true, false, false)));
        assertEquals(UiInventoryClickType.SWAP,
                UiV2ContainerScreenAdapter.classifyClick(0, Keyboard.KEY_3, 2, UiInputModifiers.none()));
        assertEquals(UiInventoryClickType.THROW,
                UiV2ContainerScreenAdapter.classifyClick(1, Keyboard.KEY_Q, -1, UiInputModifiers.none()));
        assertEquals(UiInventoryClickType.PICKUP,
                UiV2ContainerScreenAdapter.classifyClick(0, 0, -1, UiInputModifiers.none()));
    }

    @Test
    public void vanillaClickModeUsesShiftForQuickMove() {
        assertEquals(1, UiV2ContainerScreenAdapter.vanillaClickMode(0, -1, new UiInputModifiers(true, false, false)));
        assertEquals(2, UiV2ContainerScreenAdapter.vanillaClickMode(0, 2, UiInputModifiers.none()));
        assertEquals(0, UiV2ContainerScreenAdapter.vanillaClickMode(0, -1, UiInputModifiers.none()));
    }

    private static UiScreen simpleScreen(final UiSlotBinding slot) {
        return Ui.screen("container").root(new Ui.RootBuilderConsumer() {
            public void build(Ui.NodeBuilder root) {
                root.absolute();
                if (slot == null) {
                    root.label("x");
                } else {
                    root.slot(slot, 18);
                }
            }
        }).build();
    }

    private static final class RecordingContainerScreenAdapter extends UiV2ContainerScreenAdapter {
        private int vanillaClicks;
        private int lastVanillaSlotIndex = -1;

        private RecordingContainerScreenAdapter(Container container, UiContainerBinding binding, UiScreen screen, UiScreenOptions options) {
            super(container, binding, screen, options);
        }

        protected void passThroughMouseClicked(int mouseX, int mouseY, int button) {
            vanillaClicks++;
        }

        protected void passThroughVanillaSlotClick(UiSlotBinding slot, int mouseButton, int clickMode) {
            vanillaClicks++;
            lastVanillaSlotIndex = containerBinding().vanillaSlotIndex(slot.id());
        }
    }

    private static final class TestContainer extends Container {
        public boolean canInteractWith(EntityPlayer player) {
            return true;
        }

        protected Slot addSlotToContainer(Slot slot) {
            return super.addSlotToContainer(slot);
        }
    }
}
