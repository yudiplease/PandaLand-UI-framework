package land.pandaland.ui.v2.render;

import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.style.UiColor;

public final class UiRenderCommand {
    public enum Type {
        ROUNDED_RECT,
        TEXT,
        TEXTURE,
        CLIP_START,
        CLIP_END,
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

    public static UiRenderCommand roundedRect(UiRect rect, int radius, UiColor color) {
        return new UiRenderCommand(Type.ROUNDED_RECT, rect, color, "", "", radius, 0.0F);
    }

    public static UiRenderCommand text(String text, UiRect rect, UiColor color) {
        return new UiRenderCommand(Type.TEXT, rect, color, text, "", 0, 0.0F);
    }

    public static UiRenderCommand texture(String texture, UiRect rect) {
        return new UiRenderCommand(Type.TEXTURE, rect, null, "", texture, 0, 0.0F);
    }

    public static UiRenderCommand clipStart(UiRect rect) {
        return new UiRenderCommand(Type.CLIP_START, rect, null, "", "", 0, 0.0F);
    }

    public static UiRenderCommand clipEnd() {
        return new UiRenderCommand(Type.CLIP_END, null, null, "", "", 0, 0.0F);
    }

    public static UiRenderCommand progress(UiRect rect, UiColor color) {
        return progress(rect, color, 1.0F);
    }

    public static UiRenderCommand progress(UiRect rect, UiColor color, float amount) {
        return new UiRenderCommand(Type.PROGRESS, rect, color, "", "", 0, amount);
    }

    public Type type() {
        return type;
    }

    public UiRect rect() {
        return rect;
    }

    public UiColor color() {
        return color;
    }

    public String text() {
        return text;
    }

    public String texture() {
        return texture;
    }

    public int radius() {
        return radius;
    }

    public float amount() {
        return amount;
    }
}
