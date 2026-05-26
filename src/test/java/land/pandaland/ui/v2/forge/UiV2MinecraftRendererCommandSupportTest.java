package land.pandaland.ui.v2.forge;

import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.minecraft.UiItemStackRef;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UiV2MinecraftRendererCommandSupportTest {
    @Test
    public void textureRegionClampsNegativeUvAndSourceToTextureBounds() {
        UiV2RenderSupport.UvRegion region = UiV2RenderSupport.textureRegion(-4, -7, 40, 30, 32, 20);

        assertTrue(region.valid());
        assertEquals(0, region.u());
        assertEquals(0, region.v());
        assertEquals(32, region.width());
        assertEquals(20, region.height());
    }

    @Test
    public void textureRegionClipsNegativeUvInsteadOfExpandingSource() {
        UiV2RenderSupport.UvRegion partial = UiV2RenderSupport.textureRegion(-4, -2, 10, 8, 32, 20);

        assertTrue(partial.valid());
        assertEquals(0, partial.u());
        assertEquals(0, partial.v());
        assertEquals(6, partial.width());
        assertEquals(6, partial.height());
        assertFalse(UiV2RenderSupport.textureRegion(-20, 0, 10, 8, 32, 20).valid());
    }

    @Test
    public void textureRegionRejectsInvalidTextureOrRegionDimensions() {
        assertFalse(UiV2RenderSupport.textureRegion(0, 0, 16, 16, 0, 64).valid());
        assertFalse(UiV2RenderSupport.textureRegion(0, 0, 0, 16, 64, 64).valid());
    }

    @Test
    public void nineSliceClampsSourceBordersAndTargetBorders() {
        UiV2RenderSupport.NineSlice slice = UiV2RenderSupport.nineSlice(
                new UiRect(10, 20, 12, 8),
                -3, -5, 20, 10, 16, 8,
                -2, 9, 30, 8);

        assertTrue(slice.valid());
        assertEquals(0, slice.sourceU());
        assertEquals(0, slice.sourceV());
        assertEquals(16, slice.sourceWidth());
        assertEquals(5, slice.sourceHeight());
        assertEquals(0, slice.sourceLeft());
        assertEquals(2, slice.sourceTop());
        assertEquals(16, slice.sourceRight());
        assertEquals(3, slice.sourceBottom());
        assertEquals(0, slice.targetLeft());
        assertEquals(2, slice.targetTop());
        assertEquals(12, slice.targetRight());
        assertEquals(3, slice.targetBottom());
    }

    @Test
    public void nineSliceRejectsInvalidSourceOrTargetDimensions() {
        assertFalse(UiV2RenderSupport.nineSlice(new UiRect(0, 0, 10, 10), 0, 0, 0, 8, 16, 16, 1, 1, 1, 1).valid());
        assertFalse(UiV2RenderSupport.nineSlice(new UiRect(0, 0, 0, 10), 0, 0, 8, 8, 16, 16, 1, 1, 1, 1).valid());
    }

    @Test
    public void itemCommandClassificationDistinguishesEmptyNonStackAndItemStack() {
        assertEquals(UiV2RenderSupport.ItemCommandKind.EMPTY, UiV2RenderSupport.classifyItem(UiItemStackRef.empty()));
        assertEquals(UiV2RenderSupport.ItemCommandKind.NON_ITEMSTACK, UiV2RenderSupport.classifyItem(UiItemStackRef.of(new Object())));
        assertEquals(UiV2RenderSupport.ItemCommandKind.ITEMSTACK, UiV2RenderSupport.classifyItem(UiItemStackRef.of(new ItemStack((Item) null))));
    }
}
