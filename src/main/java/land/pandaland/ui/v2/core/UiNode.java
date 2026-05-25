package land.pandaland.ui.v2.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import land.pandaland.ui.v2.layout.UiLayoutStyle;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.state.UiState;

public final class UiNode {
    public enum Type {
        ROOT,
        STACK,
        LABEL,
        BUTTON,
        PANEL,
        SLIDER,
        PROGRESS,
        MODAL,
        TOAST
    }

    private final Type type;
    private final List<UiNode> children = new ArrayList<UiNode>();
    private UiNode parent;
    private String text = "";
    private Runnable clickAction;
    private Runnable dragAction;
    private UiState<Float> valueState;
    private String texture = "";
    private long durationMs;
    private long elapsedMs;
    private boolean invalid;
    private boolean visible = true;
    private boolean enabled = true;
    private boolean focusable;
    private UiRect bounds = new UiRect(0, 0, 0, 0);
    private UiLayoutStyle layoutStyle = UiLayoutStyle.leaf();

    public UiNode(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        this.type = type;
    }

    public Type type() {
        return type;
    }

    public UiNode text(String text) {
        this.text = text == null ? "" : text;
        invalidate();
        return this;
    }

    public String text() {
        return text;
    }

    public UiNode onClick(Runnable action) {
        this.clickAction = action;
        return this;
    }

    public Runnable clickAction() {
        return clickAction;
    }

    public UiNode onDrag(Runnable action) {
        this.dragAction = action;
        return this;
    }

    public Runnable dragAction() {
        return dragAction;
    }

    public UiNode valueState(UiState<Float> valueState) {
        this.valueState = valueState;
        return this;
    }

    public UiState<Float> valueState() {
        return valueState;
    }

    public UiNode texture(String texture) {
        this.texture = texture == null ? "" : texture;
        invalidate();
        return this;
    }

    public String texture() {
        return texture;
    }

    public UiNode durationMs(long durationMs) {
        this.durationMs = Math.max(0L, durationMs);
        return this;
    }

    public long durationMs() {
        return durationMs;
    }

    public void updateElapsed(long deltaMs) {
        elapsedMs += Math.max(0L, deltaMs);
    }

    public boolean expired() {
        return durationMs > 0L && elapsedMs >= durationMs;
    }

    public UiNode add(UiNode child) {
        if (child == null) {
            throw new IllegalArgumentException("child cannot be null");
        }
        if (child.parent != null) {
            throw new IllegalArgumentException("child already has parent");
        }
        child.parent = this;
        children.add(child);
        invalidate();
        return this;
    }

    public List<UiNode> children() {
        return Collections.unmodifiableList(children);
    }

    public UiNode parent() {
        return parent;
    }

    public void invalidate() {
        invalid = true;
        if (parent != null) {
            parent.invalidate();
        }
    }

    public boolean invalid() {
        return invalid;
    }

    public void clearInvalid() {
        invalid = false;
        for (UiNode child : children) {
            child.clearInvalid();
        }
    }

    public UiLayoutStyle layoutStyle() {
        return layoutStyle;
    }

    public UiNode layoutStyle(UiLayoutStyle layoutStyle) {
        if (layoutStyle == null) {
            throw new IllegalArgumentException("layoutStyle cannot be null");
        }
        this.layoutStyle = layoutStyle;
        invalidate();
        return this;
    }

    public UiRect bounds() {
        return bounds;
    }

    public UiNode bounds(UiRect bounds) {
        this.bounds = bounds == null ? new UiRect(0, 0, 0, 0) : bounds;
        return this;
    }

    public boolean visible() {
        return visible;
    }

    public UiNode visible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean enabled() {
        return enabled;
    }

    public UiNode enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean focusable() {
        return focusable;
    }

    public UiNode focusable(boolean focusable) {
        this.focusable = focusable;
        return this;
    }
}
