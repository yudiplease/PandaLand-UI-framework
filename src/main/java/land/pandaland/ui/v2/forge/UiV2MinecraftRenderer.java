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
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class UiV2MinecraftRenderer extends Gui {
    private final Minecraft minecraft;
    private final UiTheme theme = UiTheme.pandalandDefault();
    private final List<UiRect> clipStack = new ArrayList<UiRect>();

    public UiV2MinecraftRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void render(UiRuntime runtime) {
        resetClipState();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
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
