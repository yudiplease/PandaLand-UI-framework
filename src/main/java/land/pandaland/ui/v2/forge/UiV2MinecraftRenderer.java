package land.pandaland.ui.v2.forge;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.render.UiRenderCommand;
import land.pandaland.ui.v2.render.UiRenderList;
import land.pandaland.ui.v2.render.UiRenderTraversal;
import land.pandaland.ui.v2.style.UiTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public final class UiV2MinecraftRenderer extends GuiScreen {
    private final Minecraft minecraft;
    private final UiTheme theme = UiTheme.pandalandDefault();
    private final List<UiRect> clipStack = new ArrayList<UiRect>();

    public UiV2MinecraftRenderer(Minecraft minecraft) {
        if (minecraft == null) {
            throw new IllegalArgumentException("minecraft cannot be null");
        }
        this.minecraft = minecraft;
        this.mc = minecraft;
        this.fontRendererObj = minecraft.fontRenderer;
        ScaledResolution resolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
        this.width = resolution.getScaledWidth();
        this.height = resolution.getScaledHeight();
    }

    public void render(UiRuntime runtime) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            resetClipState();
            UiRenderList commands = UiRenderTraversal.render(runtime, theme);
            for (UiRenderCommand command : commands.commands()) {
                draw(command);
            }
        } finally {
            resetClipState();
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private void draw(UiRenderCommand command) {
        if (command == null || command.type() == null) {
            return;
        }
        if (command.type() == UiRenderCommand.Type.ROUNDED_RECT) {
            UiRect rect = command.rect();
            drawRoundedRect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, command.radius(), command.color().argb());
        } else if (command.type() == UiRenderCommand.Type.TEXT) {
            UiRect rect = command.rect();
            minecraft.fontRenderer.drawString(command.text(), rect.x + 6, rect.y + Math.max(0, (rect.height - 8) / 2), command.color().argb());
        } else if (command.type() == UiRenderCommand.Type.TEXTURE) {
            UiRect rect = command.rect();
            minecraft.getTextureManager().bindTexture(new ResourceLocation(command.texture()));
            drawTexturedModalRect(rect.x, rect.y, 0, 0, rect.width, rect.height);
        } else if (command.type() == UiRenderCommand.Type.TEXTURE_REGION) {
            drawTextureRegion(command);
        } else if (command.type() == UiRenderCommand.Type.NINE_SLICE) {
            drawNineSlice(command);
        } else if (command.type() == UiRenderCommand.Type.ITEM_STACK) {
            drawItemStack(command);
        } else if (command.type() == UiRenderCommand.Type.ITEM_TOOLTIP) {
            drawItemTooltip(command);
        } else if (command.type() == UiRenderCommand.Type.CLIP_START) {
            beginClip(command.rect());
        } else if (command.type() == UiRenderCommand.Type.CLIP_END) {
            endClip();
        } else if (command.type() == UiRenderCommand.Type.PROGRESS) {
            UiRect rect = command.rect();
            int fillWidth = Math.max(0, Math.round((rect.width - 8) * command.amount()));
            drawRect(rect.x + 4, rect.y + rect.height - 5, rect.x + 4 + fillWidth, rect.y + rect.height - 3, command.color().argb());
        } else if (command.type() == UiRenderCommand.Type.BORDER) {
            drawBorder(command.rect(), command.thickness(), command.color().argb());
        } else if (command.type() == UiRenderCommand.Type.GRADIENT_RECT) {
            UiRect rect = command.rect();
            drawRoundedRect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, command.radius(), command.color().argb());
        } else if (command.type() == UiRenderCommand.Type.SHADOW) {
            drawShadow(command);
        } else if (command.type() == UiRenderCommand.Type.LINE) {
            drawLine(command);
        } else if (command.type() == UiRenderCommand.Type.TEXT_WRAP) {
            drawTextWrap(command);
        } else if (command.type() == UiRenderCommand.Type.CUSTOM
                || command.type() == UiRenderCommand.Type.LAYER_START
                || command.type() == UiRenderCommand.Type.LAYER_END) {
            // Forge 1.7.10 fallback renderer preserves these commands as safe no-ops.
        }
    }

    private void drawTextureRegion(UiRenderCommand command) {
        UiRect rect = command.rect();
        if (rect == null || rect.width <= 0 || rect.height <= 0 || command.texture().isEmpty()) {
            return;
        }
        UiV2RenderSupport.UvRegion region = UiV2RenderSupport.textureRegion(
                command.u(),
                command.v(),
                command.regionWidth(),
                command.regionHeight(),
                command.textureWidth(),
                command.textureHeight());
        if (!region.valid()) {
            return;
        }
        minecraft.getTextureManager().bindTexture(new ResourceLocation(command.texture()));
        drawTexturePart(rect.x, rect.y, rect.width, rect.height, region.u(), region.v(), region.width(), region.height(), region.textureWidth(), region.textureHeight());
    }

    private void drawNineSlice(UiRenderCommand command) {
        if (command.texture().isEmpty()) {
            return;
        }
        UiV2RenderSupport.NineSlice slice = UiV2RenderSupport.nineSlice(
                command.rect(),
                command.u(),
                command.v(),
                command.regionWidth(),
                command.regionHeight(),
                command.textureWidth(),
                command.textureHeight(),
                command.sliceLeft(),
                command.sliceTop(),
                command.sliceRight(),
                command.sliceBottom());
        if (!slice.valid()) {
            return;
        }
        minecraft.getTextureManager().bindTexture(new ResourceLocation(command.texture()));
        drawNineSlicePart(slice, 0, 0, slice.targetLeft(), slice.targetTop(), 0, 0, slice.sourceLeft(), slice.sourceTop());
        drawNineSlicePart(slice, slice.targetLeft(), 0, centerWidth(slice), slice.targetTop(), slice.sourceLeft(), 0, sourceCenterWidth(slice), slice.sourceTop());
        drawNineSlicePart(slice, slice.target().width - slice.targetRight(), 0, slice.targetRight(), slice.targetTop(), slice.sourceWidth() - slice.sourceRight(), 0, slice.sourceRight(), slice.sourceTop());
        drawNineSlicePart(slice, 0, slice.targetTop(), slice.targetLeft(), centerHeight(slice), 0, slice.sourceTop(), slice.sourceLeft(), sourceCenterHeight(slice));
        drawNineSlicePart(slice, slice.targetLeft(), slice.targetTop(), centerWidth(slice), centerHeight(slice), slice.sourceLeft(), slice.sourceTop(), sourceCenterWidth(slice), sourceCenterHeight(slice));
        drawNineSlicePart(slice, slice.target().width - slice.targetRight(), slice.targetTop(), slice.targetRight(), centerHeight(slice), slice.sourceWidth() - slice.sourceRight(), slice.sourceTop(), slice.sourceRight(), sourceCenterHeight(slice));
        drawNineSlicePart(slice, 0, slice.target().height - slice.targetBottom(), slice.targetLeft(), slice.targetBottom(), 0, slice.sourceHeight() - slice.sourceBottom(), slice.sourceLeft(), slice.sourceBottom());
        drawNineSlicePart(slice, slice.targetLeft(), slice.target().height - slice.targetBottom(), centerWidth(slice), slice.targetBottom(), slice.sourceLeft(), slice.sourceHeight() - slice.sourceBottom(), sourceCenterWidth(slice), slice.sourceBottom());
        drawNineSlicePart(slice, slice.target().width - slice.targetRight(), slice.target().height - slice.targetBottom(), slice.targetRight(), slice.targetBottom(), slice.sourceWidth() - slice.sourceRight(), slice.sourceHeight() - slice.sourceBottom(), slice.sourceRight(), slice.sourceBottom());
    }

    private void drawNineSlicePart(UiV2RenderSupport.NineSlice slice, int targetX, int targetY, int targetWidth, int targetHeight, int sourceX, int sourceY, int sourceWidth, int sourceHeight) {
        if (targetWidth <= 0 || targetHeight <= 0 || sourceWidth <= 0 || sourceHeight <= 0) {
            return;
        }
        drawTexturePart(
                slice.target().x + targetX,
                slice.target().y + targetY,
                targetWidth,
                targetHeight,
                slice.sourceU() + sourceX,
                slice.sourceV() + sourceY,
                sourceWidth,
                sourceHeight,
                slice.textureWidth(),
                slice.textureHeight());
    }

    private static int centerWidth(UiV2RenderSupport.NineSlice slice) {
        return Math.max(0, slice.target().width - slice.targetLeft() - slice.targetRight());
    }

    private static int centerHeight(UiV2RenderSupport.NineSlice slice) {
        return Math.max(0, slice.target().height - slice.targetTop() - slice.targetBottom());
    }

    private static int sourceCenterWidth(UiV2RenderSupport.NineSlice slice) {
        return Math.max(0, slice.sourceWidth() - slice.sourceLeft() - slice.sourceRight());
    }

    private static int sourceCenterHeight(UiV2RenderSupport.NineSlice slice) {
        return Math.max(0, slice.sourceHeight() - slice.sourceTop() - slice.sourceBottom());
    }

    private static void drawTexturePart(int x, int y, int targetWidth, int targetHeight, int u, int v, int sourceWidth, int sourceHeight, int textureWidth, int textureHeight) {
        func_152125_a(x, y, u, v, sourceWidth, sourceHeight, targetWidth, targetHeight, textureWidth, textureHeight);
    }

    private void drawItemStack(UiRenderCommand command) {
        if (UiV2RenderSupport.classifyItem(command.item()) != UiV2RenderSupport.ItemCommandKind.ITEMSTACK) {
            return;
        }
        UiRect rect = command.rect();
        if (rect == null || rect.width <= 0 || rect.height <= 0) {
            return;
        }
        ItemStack stack = (ItemStack) command.item().rawStack();
        if (stack == null || stack.getItem() == null || stack.stackSize <= 0) {
            return;
        }
        try {
            RenderItem itemRenderer = RenderItem.getInstance();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();
            itemRenderer.renderItemAndEffectIntoGUI(minecraft.fontRenderer, minecraft.getTextureManager(), stack, rect.x, rect.y);
            itemRenderer.renderItemOverlayIntoGUI(minecraft.fontRenderer, minecraft.getTextureManager(), stack, rect.x, rect.y);
        } finally {
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
    }

    private void drawItemTooltip(UiRenderCommand command) {
        if (UiV2RenderSupport.classifyItem(command.item()) != UiV2RenderSupport.ItemCommandKind.ITEMSTACK) {
            return;
        }
        ItemStack stack = (ItemStack) command.item().rawStack();
        if (stack == null || stack.getItem() == null || stack.stackSize <= 0) {
            return;
        }
        drawHoveringText(stack.getTooltip(minecraft.thePlayer, minecraft.gameSettings.advancedItemTooltips), command.mouseX(), command.mouseY(), minecraft.fontRenderer);
    }

    private static void drawBorder(UiRect rect, int thickness, int color) {
        if (rect == null || rect.width <= 0 || rect.height <= 0) {
            return;
        }
        int actual = Math.max(1, thickness);
        drawRect(rect.x, rect.y, rect.x + rect.width, rect.y + actual, color);
        drawRect(rect.x, rect.y + rect.height - actual, rect.x + rect.width, rect.y + rect.height, color);
        drawRect(rect.x, rect.y, rect.x + actual, rect.y + rect.height, color);
        drawRect(rect.x + rect.width - actual, rect.y, rect.x + rect.width, rect.y + rect.height, color);
    }

    private static void drawShadow(UiRenderCommand command) {
        UiRect rect = command.rect();
        if (rect == null || rect.width <= 0 || rect.height <= 0) {
            return;
        }
        int blur = Math.max(0, command.blur());
        int left = rect.x + command.offsetX() - blur / 2;
        int top = rect.y + command.offsetY() - blur / 2;
        int right = rect.x + rect.width + command.offsetX() + blur / 2;
        int bottom = rect.y + rect.height + command.offsetY() + blur / 2;
        drawRoundedRect(left, top, right, bottom, command.radius(), command.color().argb());
    }

    private static void drawLine(UiRenderCommand command) {
        int thickness = Math.max(1, command.thickness());
        int left = Math.min(command.x1(), command.x2());
        int right = Math.max(command.x1(), command.x2());
        int top = Math.min(command.y1(), command.y2());
        int bottom = Math.max(command.y1(), command.y2());
        if (left == right) {
            right = left + thickness;
        }
        if (top == bottom) {
            bottom = top + thickness;
        }
        drawRect(left, top, right, bottom, command.color().argb());
    }

    private void drawTextWrap(UiRenderCommand command) {
        UiRect rect = command.rect();
        if (rect == null || rect.width <= 0 || rect.height <= 0 || command.text().isEmpty()) {
            return;
        }
        int lineHeight = Math.max(8, command.lineHeight());
        int maxChars = Math.max(1, rect.width / 6);
        String text = command.text();
        int index = 0;
        int y = rect.y;
        while (index < text.length() && y + lineHeight <= rect.y + rect.height) {
            int end = Math.min(text.length(), index + maxChars);
            if (end < text.length()) {
                int space = text.lastIndexOf(' ', end);
                if (space > index) {
                    end = space;
                }
            }
            String line = text.substring(index, end).trim();
            minecraft.fontRenderer.drawString(line, rect.x + 6, y + Math.max(0, (lineHeight - 8) / 2), command.color().argb());
            index = end;
            while (index < text.length() && text.charAt(index) == ' ') {
                index++;
            }
            y += lineHeight;
        }
    }

    private void beginClip(UiRect rect) {
        if (rect == null) {
            return;
        }
        UiRect effective = clipStack.isEmpty() ? rect : intersect(clipStack.get(clipStack.size() - 1), rect);
        clipStack.add(effective);
        applyClip(effective);
    }

    private void endClip() {
        if (!clipStack.isEmpty()) {
            clipStack.remove(clipStack.size() - 1);
        }
        if (clipStack.isEmpty()) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            applyClip(clipStack.get(clipStack.size() - 1));
        }
    }

    private void resetClipState() {
        clipStack.clear();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void applyClip(UiRect rect) {
        ScaledResolution resolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
        int scale = resolution.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                rect.x * scale,
                minecraft.displayHeight - (rect.y + rect.height) * scale,
                Math.max(0, rect.width * scale),
                Math.max(0, rect.height * scale)
        );
    }

    private static UiRect intersect(UiRect first, UiRect second) {
        int left = Math.max(first.x, second.x);
        int top = Math.max(first.y, second.y);
        int right = Math.min(first.x + first.width, second.x + second.width);
        int bottom = Math.min(first.y + first.height, second.y + second.height);
        return new UiRect(left, top, Math.max(0, right - left), Math.max(0, bottom - top));
    }

    private static void drawRoundedRect(int left, int top, int right, int bottom, int radius, int color) {
        if (right <= left || bottom <= top) {
            return;
        }
        int width = right - left;
        int height = bottom - top;
        int actualRadius = Math.max(0, Math.min(radius, Math.min(width, height) / 2));
        drawRect(left + actualRadius, top, right - actualRadius, bottom, color);
        drawRect(left, top + actualRadius, right, bottom - actualRadius, color);
        for (int offset = 0; offset < actualRadius; offset++) {
            int inset = circleInset(actualRadius, offset);
            drawRect(left + inset, top + offset, right - inset, top + offset + 1, color);
            drawRect(left + inset, bottom - offset - 1, right - inset, bottom - offset, color);
        }
    }

    private static int circleInset(int radius, int y) {
        double distance = radius - y - 0.5D;
        return (int) Math.ceil(radius - Math.sqrt(Math.max(0.0D, radius * radius - distance * distance)));
    }
}
