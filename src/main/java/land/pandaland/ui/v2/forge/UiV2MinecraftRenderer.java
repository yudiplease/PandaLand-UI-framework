package land.pandaland.ui.v2.forge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.render.UiRenderCommand;
import land.pandaland.ui.v2.render.UiRenderList;
import land.pandaland.ui.v2.render.UiRenderTraversal;
import land.pandaland.ui.v2.style.UiTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class UiV2MinecraftRenderer extends Gui {
    private final Minecraft minecraft;
    private final UiTheme theme = UiTheme.pandalandDefault();
    private int clipDepth;

    public UiV2MinecraftRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void render(UiRuntime runtime) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            UiRenderList commands = UiRenderTraversal.render(runtime, theme);
            for (UiRenderCommand command : commands.commands()) {
                draw(command);
            }
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private void draw(UiRenderCommand command) {
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
        } else if (command.type() == UiRenderCommand.Type.CLIP_START) {
            beginClip(command.rect());
        } else if (command.type() == UiRenderCommand.Type.CLIP_END) {
            endClip();
        } else if (command.type() == UiRenderCommand.Type.PROGRESS) {
            UiRect rect = command.rect();
            int fillWidth = Math.max(0, Math.round((rect.width - 8) * command.amount()));
            drawRect(rect.x + 4, rect.y + rect.height - 5, rect.x + 4 + fillWidth, rect.y + rect.height - 3, command.color().argb());
        }
    }

    private void beginClip(UiRect rect) {
        if (rect == null) {
            return;
        }
        ScaledResolution resolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
        int scale = resolution.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(rect.x * scale, minecraft.displayHeight - (rect.y + rect.height) * scale, rect.width * scale, rect.height * scale);
        clipDepth++;
    }

    private void endClip() {
        if (clipDepth > 0) {
            clipDepth--;
        }
        if (clipDepth == 0) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
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
