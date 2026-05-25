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
        PROGRESS
    }

    private final Type type;
    private final UiRect rect;
    private final UiColor color;
    private final String text;
    private final String texture;
    private final int radius;
    private final float amount;

    private UiRenderCommand(Type type, UiRect rect, UiColor color, String text, String texture, int radius, float amount) {
        this.type = type;
        this.rect = rect;
        this.color = color;
        this.text = text == null ? "" : text;
        this.texture = texture == null ? "" : texture;
        this.radius = Math.max(0, radius);
        this.amount = Math.max(0.0F, Math.min(1.0F, amount));
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
        return new UiRenderCommand(Type.ROUNDED_RECT, rect, color, "", "", radius, 0.0F);
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
        return new UiRenderCommand(Type.TEXT, rect, color, text, "", 0, 0.0F);
    }

    /**
     * Creates a texture command.
     *
     * @param texture texture resource id
     * @param rect target rectangle
     * @return render command
     */
    public static UiRenderCommand texture(String texture, UiRect rect) {
        return new UiRenderCommand(Type.TEXTURE, rect, null, "", texture, 0, 0.0F);
    }

    /**
     * Creates a clip-start command.
     *
     * @param rect clip rectangle
     * @return render command
     */
    public static UiRenderCommand clipStart(UiRect rect) {
        return new UiRenderCommand(Type.CLIP_START, rect, null, "", "", 0, 0.0F);
    }

    /**
     * Creates a clip-end command.
     *
     * @return render command
     */
    public static UiRenderCommand clipEnd() {
        return new UiRenderCommand(Type.CLIP_END, null, null, "", "", 0, 0.0F);
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
        return new UiRenderCommand(Type.PROGRESS, rect, color, "", "", 0, amount);
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
     * @return normalized value in range {@code 0.0} to {@code 1.0}
     */
    public float amount() {
        return amount;
    }
}
