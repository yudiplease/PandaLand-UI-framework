package land.pandaland.ui.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

final class PandaSmoothText {
    private static final int FONT_SIZE = 9;
    private static final String FONT_RESOURCE = "/assets/pandaland_ui/fonts/minecraft-rus.ttf";
    private static final Font BUTTON_FONT = loadFont();
    private static final Map<TextKey, TextTexture> CACHE = new HashMap<TextKey, TextTexture>();
    private static int nextTextureId;

    private PandaSmoothText() {
    }

    private static Font loadFont() {
        InputStream stream = PandaSmoothText.class.getResourceAsStream(FONT_RESOURCE);
        if (stream == null) {
            return new Font("Segoe UI Semibold", Font.BOLD, FONT_SIZE);
        }
        try {
            return Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(Font.PLAIN, (float) FONT_SIZE);
        } catch (FontFormatException e) {
            return new Font("Segoe UI Semibold", Font.BOLD, FONT_SIZE);
        } catch (IOException e) {
            return new Font("Segoe UI Semibold", Font.BOLD, FONT_SIZE);
        } finally {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }
    }

    static void drawCentered(Minecraft minecraft, String text, int centerX, int centerY, int color) {
        TextTexture texture = textureFor(minecraft, text == null ? "" : text, color);
        double left = snapToGuiPixel(centerX - texture.logicalWidth / 2.0D, texture.scale);
        double top = snapToGuiPixel(centerY - texture.logicalHeight / 2.0D, texture.scale);
        drawTexture(minecraft, texture, left, top);
    }

    static void drawCenteredFit(Minecraft minecraft, String text, int centerX, int centerY, int maxWidth, int color) {
        String safeText = text == null ? "" : text;
        TextTexture texture = textureFor(minecraft, safeText, color);
        int safeWidth = Math.max(1, maxWidth);
        if (texture.logicalWidth > safeWidth) {
            texture = textureFor(minecraft, fitText(minecraft, safeText, safeWidth, color), color);
        }
        double left = snapToGuiPixel(centerX - texture.logicalWidth / 2.0D, texture.scale);
        double top = snapToGuiPixel(centerY - texture.logicalHeight / 2.0D, texture.scale);
        drawTexture(minecraft, texture, left, top);
    }

    private static TextTexture textureFor(Minecraft minecraft, String text, int color) {
        int scale = guiScale(minecraft);
        TextKey key = new TextKey(text, color, scale);
        TextTexture cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        BufferedImage image = renderText(text, color, scale);
        ResourceLocation location = minecraft.getTextureManager().getDynamicTextureLocation("pandaland_ui_text_" + nextTextureId++, new DynamicTexture(image));
        TextTexture texture = new TextTexture(location, image.getWidth() / (double) scale, image.getHeight() / (double) scale, scale);
        CACHE.put(key, texture);
        return texture;
    }

    private static String fitText(Minecraft minecraft, String text, int maxWidth, int color) {
        if (text.isEmpty()) {
            return text;
        }
        String ellipsis = "...";
        if (textureFor(minecraft, ellipsis, color).logicalWidth > maxWidth) {
            return "";
        }
        int low = 0;
        int high = text.length();
        while (low < high) {
            int mid = (low + high + 1) / 2;
            String candidate = text.substring(0, mid) + ellipsis;
            if (textureFor(minecraft, candidate, color).logicalWidth <= maxWidth) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }
        return text.substring(0, low) + ellipsis;
    }

    private static int guiScale(Minecraft minecraft) {
        ScaledResolution resolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
        return Math.max(1, resolution.getScaleFactor());
    }

    private static double snapToGuiPixel(double value, int scale) {
        return Math.round(value * scale) / (double) scale;
    }

    private static BufferedImage renderText(String text, int color, int scale) {
        BufferedImage probe = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D probeGraphics = probe.createGraphics();
        configure(probeGraphics);
        probeGraphics.setFont(BUTTON_FONT.deriveFont((float) (FONT_SIZE * scale)));
        FontMetrics metrics = probeGraphics.getFontMetrics();
        int width = Math.max(1, metrics.stringWidth(text) + 4 * scale);
        int height = Math.max(1, metrics.getAscent() + metrics.getDescent() + 2 * scale);
        int baseline = scale + metrics.getAscent();
        probeGraphics.dispose();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        configure(graphics);
        graphics.setFont(BUTTON_FONT.deriveFont((float) (FONT_SIZE * scale)));
        graphics.setColor(new Color(color, true));
        graphics.drawString(text, 2 * scale, baseline);
        graphics.dispose();
        return image;
    }

    private static void configure(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    private static void drawTexture(Minecraft minecraft, TextTexture texture, double left, double top) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_TEXTURE_BIT);
        try {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            minecraft.getTextureManager().bindTexture(texture.location);

            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(left, top + texture.logicalHeight, 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV(left + texture.logicalWidth, top + texture.logicalHeight, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV(left + texture.logicalWidth, top, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV(left, top, 0.0D, 0.0D, 0.0D);
            tessellator.draw();
        } finally {
            GL11.glPopAttrib();
        }
    }

    private static final class TextTexture {
        private final ResourceLocation location;
        private final double logicalWidth;
        private final double logicalHeight;
        private final int scale;

        private TextTexture(ResourceLocation location, double logicalWidth, double logicalHeight, int scale) {
            this.location = location;
            this.logicalWidth = logicalWidth;
            this.logicalHeight = logicalHeight;
            this.scale = scale;
        }
    }

    private static final class TextKey {
        private final String text;
        private final int color;
        private final int scale;

        private TextKey(String text, int color, int scale) {
            this.text = text;
            this.color = color;
            this.scale = scale;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TextKey)) {
                return false;
            }
            TextKey other = (TextKey) obj;
            return color == other.color && scale == other.scale && text.equals(other.text);
        }

        public int hashCode() {
            int result = text.hashCode();
            result = 31 * result + color;
            result = 31 * result + scale;
            return result;
        }
    }
}
