package land.pandaland.ui.v2.render;

import java.util.EnumSet;

import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.minecraft.UiItemStackRef;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public final class UiMinecraftRenderCommandTest {
    @Test
    public void textureRegionCommandCarriesUvAndTextureMetadata() {
        UiRect rect = new UiRect(3, 4, 32, 16);

        UiRenderCommand command = UiRenderCommand.textureRegion("textures/gui/widgets.png", rect, 10, 20, 30, 40, 256, 128);

        assertEquals(UiRenderCommand.Type.TEXTURE_REGION, command.type());
        assertSame(rect, command.rect());
        assertEquals("textures/gui/widgets.png", command.texture());
        assertEquals(10, command.u());
        assertEquals(20, command.v());
        assertEquals(30, command.regionWidth());
        assertEquals(40, command.regionHeight());
        assertEquals(256, command.textureWidth());
        assertEquals(128, command.textureHeight());
    }

    @Test
    public void nineSliceCommandCarriesUvTextureAndBorderMetadata() {
        UiRect rect = new UiRect(1, 2, 80, 50);

        UiRenderCommand command = UiRenderCommand.nineSlice("textures/gui/panel.png", rect, 5, 6, 70, 30, 128, 64, 4, 7, 9, 11);

        assertEquals(UiRenderCommand.Type.NINE_SLICE, command.type());
        assertSame(rect, command.rect());
        assertEquals("textures/gui/panel.png", command.texture());
        assertEquals(5, command.u());
        assertEquals(6, command.v());
        assertEquals(70, command.regionWidth());
        assertEquals(30, command.regionHeight());
        assertEquals(128, command.textureWidth());
        assertEquals(64, command.textureHeight());
        assertEquals(4, command.sliceLeft());
        assertEquals(7, command.sliceTop());
        assertEquals(9, command.sliceRight());
        assertEquals(11, command.sliceBottom());
    }

    @Test
    public void itemCommandsCarryItemTargetAnchorAndMouseMetadata() {
        UiItemStackRef item = UiItemStackRef.of(new Object()).displayName("Stone").count(2);
        UiRect rect = new UiRect(8, 9, 18, 18);
        UiRect anchor = new UiRect(10, 11, 18, 18);

        UiRenderCommand stack = UiRenderCommand.itemStack(item, rect);
        UiRenderCommand tooltip = UiRenderCommand.itemTooltip(item, anchor, 44, 55);

        assertEquals(UiRenderCommand.Type.ITEM_STACK, stack.type());
        assertSame(item, stack.item());
        assertSame(rect, stack.rect());

        assertEquals(UiRenderCommand.Type.ITEM_TOOLTIP, tooltip.type());
        assertSame(item, tooltip.item());
        assertSame(anchor, tooltip.anchorRect());
        assertEquals(44, tooltip.mouseX());
        assertEquals(55, tooltip.mouseY());
    }

    @Test
    public void commandTypesDoNotAddParallelScissorOrZLayerCommands() {
        EnumSet<UiRenderCommand.Type> structuralTypes = EnumSet.noneOf(UiRenderCommand.Type.class);
        for (UiRenderCommand.Type type : UiRenderCommand.Type.values()) {
            String name = type.name();
            if (name.indexOf("CLIP") >= 0
                    || name.indexOf("SCISSOR") >= 0
                    || name.indexOf("LAYER") >= 0
                    || name.indexOf("Z_") >= 0) {
                structuralTypes.add(type);
            }
        }

        assertEquals(EnumSet.of(
                UiRenderCommand.Type.CLIP_START,
                UiRenderCommand.Type.CLIP_END,
                UiRenderCommand.Type.LAYER_START,
                UiRenderCommand.Type.LAYER_END), structuralTypes);
    }

    @Test
    public void minecraftCommandTypesDoNotShiftExistingTypeOrdinals() {
        assertEquals(0, UiRenderCommand.Type.ROUNDED_RECT.ordinal());
        assertEquals(1, UiRenderCommand.Type.TEXT.ordinal());
        assertEquals(2, UiRenderCommand.Type.TEXTURE.ordinal());
        assertEquals(3, UiRenderCommand.Type.CLIP_START.ordinal());
        assertEquals(4, UiRenderCommand.Type.CLIP_END.ordinal());
        assertEquals(5, UiRenderCommand.Type.PROGRESS.ordinal());
        assertEquals(6, UiRenderCommand.Type.BORDER.ordinal());
        assertEquals(7, UiRenderCommand.Type.GRADIENT_RECT.ordinal());
        assertEquals(8, UiRenderCommand.Type.SHADOW.ordinal());
        assertEquals(9, UiRenderCommand.Type.LINE.ordinal());
        assertEquals(10, UiRenderCommand.Type.TEXT_WRAP.ordinal());
        assertEquals(11, UiRenderCommand.Type.CUSTOM.ordinal());
        assertEquals(12, UiRenderCommand.Type.LAYER_START.ordinal());
        assertEquals(13, UiRenderCommand.Type.LAYER_END.ordinal());
    }
}
