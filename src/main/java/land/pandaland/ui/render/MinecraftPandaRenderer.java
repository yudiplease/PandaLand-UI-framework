package land.pandaland.ui.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import land.pandaland.ui.api.PandaButton;
import land.pandaland.ui.api.PandaColor;
import land.pandaland.ui.api.PandaHudBar;
import land.pandaland.ui.api.PandaIcon;
import land.pandaland.ui.api.PandaLabel;
import land.pandaland.ui.api.PandaList;
import land.pandaland.ui.api.PandaModal;
import land.pandaland.ui.api.PandaPanel;
import land.pandaland.ui.api.PandaProgressBar;
import land.pandaland.ui.api.PandaSlider;
import land.pandaland.ui.api.PandaTabs;
import land.pandaland.ui.api.PandaTheme;
import land.pandaland.ui.api.PandaToast;
import land.pandaland.ui.api.PandaUi;
import land.pandaland.ui.api.PandaRect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class MinecraftPandaRenderer extends Gui implements land.pandaland.ui.api.PandaRenderer {
    private final Minecraft minecraft;
    private final PandaTheme theme;

    public MinecraftPandaRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.theme = PandaUi.theme();
    }

    public void panel(PandaPanel panel) {
        PandaRect b = panel.bounds();
        int radius = Math.max(4, Math.min(theme.panelRadius(), Math.min(b.width, b.height) / 8));
        drawRoundedRect(b.x, b.y, b.x + b.width, b.y + b.height, radius, alpha(theme.primaryAccent().toArgb(), 102));
        drawRoundedRect(b.x + 1, b.y + 1, b.x + b.width - 1, b.y + b.height - 1, Math.max(3, radius - 1), theme.panelBase().toArgb());
        if (b.width > 12 && b.height > 10) {
            int washBottom = Math.min(b.y + b.height - 2, b.y + Math.max(4, b.height / 3));
            drawRoundedRect(b.x + 3, b.y + 2, b.x + b.width - 3, washBottom, Math.max(2, radius - 3), 0x2219F3E2);
        }
        if (!panel.title().isEmpty()) {
            minecraft.fontRenderer.drawStringWithShadow(panel.title(), b.x + 12, b.y + 10, theme.textPrimary().toArgb());
        }
    }

    public void button(PandaButton button, boolean hovered) {
        PandaRect b = button.bounds();
        int base = button.kind() == PandaButton.Kind.DANGER ? theme.dangerAccent().toArgb() : theme.buttonBase().toArgb();
        float hoverAmount = Math.max(hovered ? 1.0f : 0.0f, button.hoverAmount());
        float pressAmount = button.pressAmount();
        int fill = blend(base, 0xEE27323C, hoverAmount);
        fill = blend(fill, 0xCC1A2027, pressAmount);
        int accent = blend(theme.primaryAccent().toArgb(), 0xFFFF4EC7, hoverAmount);
        int border = blend(0xFF65727D, accent, hoverAmount);
        int radius = Math.max(4, Math.min(theme.buttonRadius(), b.height / 2));
        int glowAlpha = button.enabled() ? (int) (hoverAmount * 70.0F) : 0;
        int borderAlpha = button.enabled() ? 42 + (int) (hoverAmount * 92.0F) : 32;
        int topWash = button.enabled() ? 18 + (int) (hoverAmount * 22.0F) : 10;

        if (glowAlpha > 0) {
            drawRoundedRect(b.x - 2, b.y - 2, b.x + b.width + 2, b.y + b.height + 2, radius + 2, alpha(accent, glowAlpha));
        }
        drawRoundedRect(b.x, b.y, b.x + b.width, b.y + b.height, radius, alpha(border, borderAlpha));
        drawRoundedRect(b.x + 1, b.y + 1, b.x + b.width - 1, b.y + b.height - 1, Math.max(2, radius - 1), fill);
        drawRoundedRect(
            b.x + 3,
            b.y + 2,
            b.x + b.width - 3,
            b.y + Math.max(4, b.height / 2),
            Math.max(2, radius - 3),
            alpha(theme.textPrimary().toArgb(), topWash)
        );

        int textColor = button.enabled() ? theme.textPrimary().toArgb() : theme.textMuted().toArgb();
        int centerX = b.x + b.width / 2;
        int centerY = b.y + b.height / 2;
        int textWidth = Math.max(1, b.width - 18);
        PandaSmoothText.drawCenteredFit(minecraft, button.text(), centerX + 1, centerY + 1, textWidth, alpha(0xFF000000, button.enabled() ? 90 : 70));
        PandaSmoothText.drawCenteredFit(minecraft, button.text(), centerX, centerY, textWidth, textColor);
    }

    public void label(PandaLabel label) {
        PandaRect b = label.bounds();
        minecraft.fontRenderer.drawStringWithShadow(label.text(), b.x, b.y, theme.textPrimary().toArgb());
    }

    public void icon(PandaIcon icon) {
        PandaRect b = icon.bounds();
        minecraft.getTextureManager().bindTexture(icon.texture());
        drawFullTexture(b.x, b.y, b.width, b.height);
    }

    public void list(PandaList list, int hoveredIndex) {
        PandaRect b = list.bounds();
        drawRect(b.x, b.y, b.x + b.width, b.y + b.height, new PandaColor(0x111820AA).toArgb());
        List<String> rows = list.rows();
        int rowHeight = rows.isEmpty() ? b.height : Math.max(1, b.height / rows.size());
        for (int i = 0; i < rows.size(); i++) {
            int y = b.y + i * rowHeight;
            boolean selected = i == list.selectedIndex();
            boolean hovered = i == hoveredIndex;
            if (selected || hovered) {
                drawRect(b.x, y, b.x + b.width, y + rowHeight, selected ? theme.primaryAccent().toArgb() : brighten(theme.buttonBase()).toArgb());
            }
            minecraft.fontRenderer.drawStringWithShadow(rows.get(i), b.x + 6, y + (rowHeight - 8) / 2, theme.textPrimary().toArgb());
        }
    }

    public void progress(PandaProgressBar progressBar) {
        PandaRect b = progressBar.bounds();
        drawRect(b.x, b.y, b.x + b.width, b.y + b.height, new PandaColor(0x111820AA).toArgb());
        int filled = (int) (b.width * progressBar.displayValue());
        drawRect(b.x, b.y, b.x + filled, b.y + b.height, theme.primaryAccent().toArgb());
        if (!progressBar.label().isEmpty()) {
            minecraft.fontRenderer.drawStringWithShadow(progressBar.label(), b.x, b.y - 10, theme.textMuted().toArgb());
        }
    }

    public void slider(PandaSlider slider, boolean hovered) {
        PandaRect b = slider.bounds();
        float hoverAmount = Math.max(hovered ? 1.0F : 0.0F, slider.hoverAmount());
        int radius = Math.max(4, Math.min(theme.buttonRadius(), b.height / 2));
        int accent = blend(theme.primaryAccent().toArgb(), 0xFFFF4EC7, hoverAmount);
        int border = blend(0xFF65727D, accent, hoverAmount);
        int base = blend(theme.buttonBase().toArgb(), 0xEE27323C, hoverAmount);
        int glowAlpha = slider.enabled() ? (int) (hoverAmount * 62.0F) : 0;

        if (glowAlpha > 0) {
            drawRoundedRect(b.x - 2, b.y - 2, b.x + b.width + 2, b.y + b.height + 2, radius + 2, alpha(accent, glowAlpha));
        }
        drawRoundedRect(b.x, b.y, b.x + b.width, b.y + b.height, radius, alpha(border, slider.enabled() ? 52 + (int) (hoverAmount * 76.0F) : 34));
        drawRoundedRect(b.x + 1, b.y + 1, b.x + b.width - 1, b.y + b.height - 1, Math.max(2, radius - 1), base);

        int trackLeft = b.x + 9;
        int trackRight = b.x + b.width - 9;
        int trackTop = b.y + b.height - 7;
        int trackBottom = b.y + b.height - 4;
        drawRoundedRect(trackLeft, trackTop, trackRight, trackBottom, 2, 0x66091418);
        int fillRight = trackLeft + Math.round((trackRight - trackLeft) * slider.value());
        drawRoundedRect(trackLeft, trackTop, fillRight, trackBottom, 2, accent);

        int thumbX = fillRight;
        drawRoundedRect(thumbX - 3, b.y + 4, thumbX + 3, b.y + b.height - 3, 3, alpha(accent, slider.enabled() ? 230 : 120));

        String text = slider.label() + ": " + slider.valueText();
        int textColor = slider.enabled() ? theme.textPrimary().toArgb() : theme.textMuted().toArgb();
        int textWidth = Math.max(1, b.width - 22);
        PandaSmoothText.drawCenteredFit(minecraft, text, b.x + b.width / 2 + 1, b.y + b.height / 2 - 1, textWidth, alpha(0xFF000000, slider.enabled() ? 90 : 70));
        PandaSmoothText.drawCenteredFit(minecraft, text, b.x + b.width / 2, b.y + b.height / 2 - 2, textWidth, textColor);
    }

    public void hudBar(PandaHudBar hudBar) {
        PandaRect b = hudBar.bounds();
        drawRect(b.x, b.y, b.x + b.width, b.y + b.height, new PandaColor(0x111820AA).toArgb());
        int filled = (int) (b.width * hudBar.value());
        drawRect(b.x, b.y, b.x + filled, b.y + b.height, theme.primaryAccent().toArgb());
        if (!hudBar.label().isEmpty()) {
            minecraft.fontRenderer.drawStringWithShadow(hudBar.label(), b.x + 5, b.y + 5, theme.textPrimary().toArgb());
        }
    }

    public void tabs(PandaTabs tabs, int hoveredIndex) {
        PandaRect b = tabs.bounds();
        List<String> labels = tabs.labels();
        if (labels.isEmpty()) {
            return;
        }

        int tabWidth = Math.max(1, b.width / labels.size());
        for (int i = 0; i < labels.size(); i++) {
            int x = b.x + i * tabWidth;
            int right = i == labels.size() - 1 ? b.x + b.width : x + tabWidth;
            boolean selected = i == tabs.selectedIndex();
            boolean hovered = i == hoveredIndex;
            int color = selected ? theme.primaryAccent().toArgb() : (hovered ? brighten(theme.buttonBase()).toArgb() : theme.buttonBase().toArgb());
            drawRect(x, b.y, right, b.y + b.height, color);

            String label = labels.get(i);
            int textWidth = minecraft.fontRenderer.getStringWidth(label);
            int textX = x + (right - x - textWidth) / 2;
            int textY = b.y + (b.height - 8) / 2;
            minecraft.fontRenderer.drawStringWithShadow(label, textX, textY, theme.textPrimary().toArgb());
        }
    }

    public void modal(PandaModal modal) {
        panel(modal.panel());
    }

    public void toast(PandaToast toast) {
        PandaRect b = toast.bounds();
        drawRect(b.x, b.y, b.x + b.width, b.y + b.height, new PandaColor(0x111820DD).toArgb());
        drawHorizontalLine(b.x + 6, b.x + Math.max(6, b.width - 6), b.y + 1, theme.primaryAccent().toArgb());
        minecraft.fontRenderer.drawStringWithShadow(toast.message(), b.x + 12, b.y + (b.height - 8) / 2, theme.textPrimary().toArgb());
    }

    private void drawFullTexture(int x, int y, int width, int height) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, zLevel, 0.0D, 1.0D);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, 1.0D, 1.0D);
        tessellator.addVertexWithUV(x + width, y, zLevel, 1.0D, 0.0D);
        tessellator.addVertexWithUV(x, y, zLevel, 0.0D, 0.0D);
        tessellator.draw();
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

    private static int blend(int from, int to, float progress) {
        float clamped = Math.max(0.0F, Math.min(1.0F, progress));
        int a = lerp((from >>> 24) & 0xFF, (to >>> 24) & 0xFF, clamped);
        int r = lerp((from >>> 16) & 0xFF, (to >>> 16) & 0xFF, clamped);
        int g = lerp((from >>> 8) & 0xFF, (to >>> 8) & 0xFF, clamped);
        int b = lerp(from & 0xFF, to & 0xFF, clamped);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int lerp(int from, int to, float progress) {
        return from + Math.round((to - from) * progress);
    }

    private static int alpha(int color, int alpha) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (color & 0x00FFFFFF);
    }

    private static PandaColor brighten(PandaColor color) {
        int red = Math.min(255, color.red() + 24);
        int green = Math.min(255, color.green() + 24);
        int blue = Math.min(255, color.blue() + 24);
        return new PandaColor((red << 24) | (green << 16) | (blue << 8) | color.alpha());
    }

    private static PandaColor darken(PandaColor color) {
        int red = Math.max(0, color.red() - 18);
        int green = Math.max(0, color.green() - 18);
        int blue = Math.max(0, color.blue() - 18);
        return new PandaColor((red << 24) | (green << 16) | (blue << 8) | color.alpha());
    }

    private static PandaColor mix(PandaColor start, PandaColor end, float amount) {
        float safe = Math.max(0.0f, Math.min(1.0f, amount));
        int red = (int) (start.red() + (end.red() - start.red()) * safe);
        int green = (int) (start.green() + (end.green() - start.green()) * safe);
        int blue = (int) (start.blue() + (end.blue() - start.blue()) * safe);
        int alpha = (int) (start.alpha() + (end.alpha() - start.alpha()) * safe);
        return new PandaColor((red << 24) | (green << 16) | (blue << 8) | alpha);
    }
}
