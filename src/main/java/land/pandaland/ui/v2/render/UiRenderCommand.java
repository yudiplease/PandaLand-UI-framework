package land.pandaland.ui.v2.render;

import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.style.UiColor;

/**
 * Immutable renderer-independent draw command.
 */
public final class UiRenderCommand {
    /**
     * Supported render command kinds.
     */
    public enum Type {
        /**
         * Filled rounded rectangle.
         */
        ROUNDED_RECT,
        /**
         * Text draw call.
         */
        TEXT,
        /**
         * Texture draw call.
         */
        TEXTURE,
        /**
         * Begin scissor/clip region.
         */
        CLIP_START,
        /**
         * End current scissor/clip region.
         */
        CLIP_END,
        /**
         * Progress bar fill.
         */
        PROGRESS,
        /**
         * Rectangle stroke or border.
         */
        BORDER,
        /**
         * Gradient rectangle fill.
         */
        GRADIENT_RECT,
        /**
         * Drop shadow primitive.
         */
        SHADOW,
        /**
         * Straight line segment.
         */
        LINE,
        /**
         * Wrapped text draw call.
         */
        TEXT_WRAP,
        /**
         * Custom draw hook marker.
         */
        CUSTOM,
        /**
         * Begin a logical render layer.
         */
        LAYER_START,
        /**
         * End a logical render layer.
         */
        LAYER_END
    }

    private final Type type;
    private final UiRect rect;
    private final UiColor color;
    private final UiColor endColor;
    private final String text;
    private final String texture;
    private final int radius;
    private final float amount;
    private final int thickness;
    private final int blur;
    private final int offsetX;
    private final int offsetY;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final int lineHeight;
    private final boolean vertical;
    private final UiCustomDraw customDraw;
    private final String layerName;
    private final int zIndex;

    private UiRenderCommand(
            Type type,
            UiRect rect,
            UiColor color,
            UiColor endColor,
            String text,
            String texture,
            int radius,
            float amount,
            int thickness,
            int blur,
            int offsetX,
            int offsetY,
            int x1,
            int y1,
            int x2,
            int y2,
            int lineHeight,
            boolean vertical,
            UiCustomDraw customDraw,
            String layerName,
            int zIndex) {
        this.type = type;
        this.rect = rect;
        this.color = color;
        this.endColor = endColor;
        this.text = text == null ? "" : text;
        this.texture = texture == null ? "" : texture;
        this.radius = Math.max(0, radius);
        this.amount = normalizeAmount(amount);
        this.thickness = Math.max(0, thickness);
        this.blur = Math.max(0, blur);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.lineHeight = Math.max(0, lineHeight);
        this.vertical = vertical;
        this.customDraw = customDraw;
        this.layerName = layerName == null ? "" : layerName;
        this.zIndex = zIndex;
    }

    /**
     * Creates a rounded rectangle command.
     *
     * @param rect target rectangle
     * @param radius corner radius
     * @param color fill color
     * @return render command
     */
    public static UiRenderCommand roundedRect(UiRect rect, int radius, UiColor color) {
        return command(Type.ROUNDED_RECT, rect, color, null, "", "", radius, 0.0F);
    }

    /**
     * Creates a text command.
     *
     * @param text text to draw
     * @param rect layout rectangle
     * @param color text color
     * @return render command
     */
    public static UiRenderCommand text(String text, UiRect rect, UiColor color) {
        return command(Type.TEXT, rect, color, null, text, "", 0, 0.0F);
    }

    /**
     * Creates a texture command.
     *
     * @param texture texture resource id
     * @param rect target rectangle
     * @return render command
     */
    public static UiRenderCommand texture(String texture, UiRect rect) {
        return command(Type.TEXTURE, rect, null, null, "", texture, 0, 0.0F);
    }

    /**
     * Creates a clip-start command.
     *
     * @param rect clip rectangle
     * @return render command
     */
    public static UiRenderCommand clipStart(UiRect rect) {
        return command(Type.CLIP_START, rect, null, null, "", "", 0, 0.0F);
    }

    /**
     * Creates a clip-end command.
     *
     * @return render command
     */
    public static UiRenderCommand clipEnd() {
        return command(Type.CLIP_END, null, null, null, "", "", 0, 0.0F);
    }

    /**
     * Creates a full progress command.
     *
     * @param rect target rectangle
     * @param color progress color
     * @return render command
     */
    public static UiRenderCommand progress(UiRect rect, UiColor color) {
        return progress(rect, color, 1.0F);
    }

    /**
     * Creates a progress command.
     *
     * @param rect target rectangle
     * @param color progress color
     * @param amount normalized progress amount
     * @return render command
     */
    public static UiRenderCommand progress(UiRect rect, UiColor color, float amount) {
        return command(Type.PROGRESS, rect, color, null, "", "", 0, amount);
    }

    /**
     * Creates a border command.
     *
     * @param rect target rectangle
     * @param radius corner radius
     * @param color border color
     * @param thickness border thickness in scaled GUI pixels
     * @return render command
     */
    public static UiRenderCommand border(UiRect rect, int radius, UiColor color, int thickness) {
        return new UiRenderCommand(Type.BORDER, rect, color, null, "", "", radius, 0.0F,
                thickness, 0, 0, 0, 0, 0, 0, 0, 0, true, null, "", 0);
    }

    /**
     * Creates a gradient rectangle command.
     *
     * @param rect target rectangle
     * @param radius corner radius
     * @param color starting color
     * @param endColor ending color
     * @param vertical {@code true} for top-to-bottom, {@code false} for left-to-right
     * @return render command
     */
    public static UiRenderCommand gradientRect(UiRect rect, int radius, UiColor color, UiColor endColor, boolean vertical) {
        return new UiRenderCommand(Type.GRADIENT_RECT, rect, color, endColor, "", "", radius, 0.0F,
                0, 0, 0, 0, 0, 0, 0, 0, 0, vertical, null, "", 0);
    }

    /**
     * Creates a shadow command.
     *
     * @param rect source rectangle casting the shadow
     * @param radius corner radius
     * @param color shadow color
     * @param blur blur radius metadata
     * @param offsetX horizontal shadow offset
     * @param offsetY vertical shadow offset
     * @return render command
     */
    public static UiRenderCommand shadow(UiRect rect, int radius, UiColor color, int blur, int offsetX, int offsetY) {
        return new UiRenderCommand(Type.SHADOW, rect, color, null, "", "", radius, 0.0F,
                0, blur, offsetX, offsetY, 0, 0, 0, 0, 0, true, null, "", 0);
    }

    /**
     * Creates a line command.
     *
     * @param x1 start x
     * @param y1 start y
     * @param x2 end x
     * @param y2 end y
     * @param color line color
     * @param thickness line thickness in scaled GUI pixels
     * @return render command
     */
    public static UiRenderCommand line(int x1, int y1, int x2, int y2, UiColor color, int thickness) {
        return new UiRenderCommand(Type.LINE, null, color, null, "", "", 0, 0.0F,
                thickness, 0, 0, 0, x1, y1, x2, y2, 0, true, null, "", 0);
    }

    /**
     * Creates a wrapped text command.
     *
     * @param text text to draw
     * @param rect layout rectangle
     * @param color text color
     * @param lineHeight preferred line height
     * @return render command
     */
    public static UiRenderCommand textWrap(String text, UiRect rect, UiColor color, int lineHeight) {
        return new UiRenderCommand(Type.TEXT_WRAP, rect, color, null, text, "", 0, 0.0F,
                0, 0, 0, 0, 0, 0, 0, 0, lineHeight, true, null, "", 0);
    }

    /**
     * Creates a custom draw command.
     *
     * @param rect custom draw bounds
     * @param customDraw renderer-independent draw hook
     * @return render command
     */
    public static UiRenderCommand custom(UiRect rect, UiCustomDraw customDraw) {
        return new UiRenderCommand(Type.CUSTOM, rect, null, null, "", "", 0, 0.0F,
                0, 0, 0, 0, 0, 0, 0, 0, 0, true, customDraw, "", 0);
    }

    /**
     * Creates a layer-start command.
     *
     * @param layerName logical layer name
     * @param zIndex layer stacking metadata
     * @return render command
     */
    public static UiRenderCommand layerStart(String layerName, int zIndex) {
        return new UiRenderCommand(Type.LAYER_START, null, null, null, "", "", 0, 0.0F,
                0, 0, 0, 0, 0, 0, 0, 0, 0, true, null, layerName, zIndex);
    }

    /**
     * Creates a layer-end command.
     *
     * @param layerName logical layer name
     * @return render command
     */
    public static UiRenderCommand layerEnd(String layerName) {
        return new UiRenderCommand(Type.LAYER_END, null, null, null, "", "", 0, 0.0F,
                0, 0, 0, 0, 0, 0, 0, 0, 0, true, null, layerName, 0);
    }

    private static UiRenderCommand command(Type type, UiRect rect, UiColor color, UiColor endColor, String text, String texture, int radius, float amount) {
        return new UiRenderCommand(type, rect, color, endColor, text, texture, radius, amount,
                0, 0, 0, 0, 0, 0, 0, 0, 0, true, null, "", 0);
    }

    private static float normalizeAmount(float amount) {
        if (Float.isNaN(amount)) {
            return 0.0F;
        }
        return Math.max(0.0F, Math.min(1.0F, amount));
    }

    /**
     * Returns command type.
     *
     * @return command type
     */
    public Type type() {
        return type;
    }

    /**
     * Returns command rectangle.
     *
     * @return rectangle, or {@code null} for commands without geometry
     */
    public UiRect rect() {
        return rect;
    }

    /**
     * Returns command color.
     *
     * @return color, or {@code null} when unused
     */
    public UiColor color() {
        return color;
    }

    /**
     * Returns secondary color for gradient commands.
     *
     * @return ending color, or {@code null} when unused
     */
    public UiColor endColor() {
        return endColor;
    }

    /**
     * Returns command text.
     *
     * @return text, or an empty string
     */
    public String text() {
        return text;
    }

    /**
     * Returns texture resource id.
     *
     * @return texture id, or an empty string
     */
    public String texture() {
        return texture;
    }

    /**
     * Returns rounded rectangle radius.
     *
     * @return radius in scaled GUI pixels
     */
    public int radius() {
        return radius;
    }

    /**
     * Returns progress amount.
     *
     * @return normalized value in range {@code 0.0} to {@code 1.0}; {@code NaN} inputs are stored as {@code 0.0}
     */
    public float amount() {
        return amount;
    }

    /**
     * Returns stroke or line thickness.
     *
     * @return thickness in scaled GUI pixels
     */
    public int thickness() {
        return thickness;
    }

    /**
     * Returns shadow blur metadata.
     *
     * @return blur radius in scaled GUI pixels
     */
    public int blur() {
        return blur;
    }

    /**
     * Returns horizontal shadow offset.
     *
     * @return offset in scaled GUI pixels
     */
    public int offsetX() {
        return offsetX;
    }

    /**
     * Returns vertical shadow offset.
     *
     * @return offset in scaled GUI pixels
     */
    public int offsetY() {
        return offsetY;
    }

    /**
     * Returns line start x coordinate.
     *
     * @return start x
     */
    public int x1() {
        return x1;
    }

    /**
     * Returns line start y coordinate.
     *
     * @return start y
     */
    public int y1() {
        return y1;
    }

    /**
     * Returns line end x coordinate.
     *
     * @return end x
     */
    public int x2() {
        return x2;
    }

    /**
     * Returns line end y coordinate.
     *
     * @return end y
     */
    public int y2() {
        return y2;
    }

    /**
     * Returns wrapped text line height.
     *
     * @return line height in scaled GUI pixels
     */
    public int lineHeight() {
        return lineHeight;
    }

    /**
     * Reports gradient orientation.
     *
     * @return {@code true} for vertical gradients
     */
    public boolean vertical() {
        return vertical;
    }

    /**
     * Returns custom draw hook.
     *
     * @return custom draw hook, or {@code null}
     */
    public UiCustomDraw customDraw() {
        return customDraw;
    }

    /**
     * Returns logical layer name.
     *
     * @return layer name, or an empty string
     */
    public String layerName() {
        return layerName;
    }

    /**
     * Returns layer stacking metadata.
     *
     * @return z-index value
     */
    public int zIndex() {
        return zIndex;
    }
}
