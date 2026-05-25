package land.pandaland.ui.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import land.pandaland.ui.runtime.PandaInputDispatcher;
import land.pandaland.ui.runtime.PandaUiErrorHandler;

/**
 * Container component that places child components using a small set of layout
 * modes.
 */
public final class PandaLayout extends PandaComponent {
    private final List<PandaComponent> children = new ArrayList<PandaComponent>();
    private final Mode mode;
    private final int spacing;

    private PandaLayout(Mode mode, int spacing) {
        this.mode = mode;
        this.spacing = Math.max(0, spacing);
    }

    /**
     * Creates a vertical stack layout.
     *
     * @param spacing vertical spacing between children
     * @return layout container
     */
    public static PandaLayout vertical(int spacing) {
        return new PandaLayout(Mode.VERTICAL, spacing);
    }

    /**
     * Creates a horizontal row layout.
     *
     * @param spacing horizontal spacing between children
     * @return layout container
     */
    public static PandaLayout row(int spacing) {
        return new PandaLayout(Mode.ROW, spacing);
    }

    /**
     * Creates an absolute layout using each child's explicit position.
     *
     * @return layout container
     */
    public static PandaLayout absolute() {
        return new PandaLayout(Mode.ABSOLUTE, 0);
    }

    /**
     * Creates an anchor layout using each child's anchor point.
     *
     * @return layout container
     */
    public static PandaLayout anchor() {
        return new PandaLayout(Mode.ANCHOR, 0);
    }

    /**
     * Adds a child component to this layout.
     *
     * @param component child component
     * @return this layout
     */
    public PandaLayout add(PandaComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("component cannot be null");
        }
        children.add(component);
        return this;
    }

    /**
     * @return immutable view of child components
     */
    public List<PandaComponent> children() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Removes all child components.
     */
    public void clear() {
        children.clear();
    }

    /** @return this layout after setting equal margin */
    public PandaLayout margin(int margin) {
        super.margin(margin);
        return this;
    }

    /** @return this layout after setting horizontal and vertical margins */
    public PandaLayout margin(int horizontal, int vertical) {
        super.margin(horizontal, vertical);
        return this;
    }

    /** @return this layout after setting individual margins */
    public PandaLayout margin(int left, int top, int right, int bottom) {
        super.margin(left, top, right, bottom);
        return this;
    }

    /** @return this layout after setting equal padding */
    public PandaLayout padding(int padding) {
        super.padding(padding);
        return this;
    }

    /** @return this layout after setting horizontal and vertical padding */
    public PandaLayout padding(int horizontal, int vertical) {
        super.padding(horizontal, vertical);
        return this;
    }

    /** @return this layout after setting individual padding */
    public PandaLayout padding(int left, int top, int right, int bottom) {
        super.padding(left, top, right, bottom);
        return this;
    }

    /** @return this layout after setting minimum size */
    public PandaLayout minSize(int width, int height) {
        super.minSize(width, height);
        return this;
    }

    /** @return this layout after setting maximum size */
    public PandaLayout maxSize(int width, int height) {
        super.maxSize(width, height);
        return this;
    }

    /** @return this layout after setting absolute position */
    public PandaLayout position(int x, int y) {
        super.position(x, y);
        return this;
    }

    /** @return this layout after setting anchor point */
    public PandaLayout anchor(Anchor anchor) {
        super.anchor(anchor);
        return this;
    }

    public int preferredWidth() {
        int width = paddingLeft() + paddingRight();
        if (mode == Mode.ROW) {
            int childrenWidth = 0;
            for (int i = 0; i < children.size(); i++) {
                PandaComponent child = children.get(i);
                childrenWidth += child.marginLeft() + constrainedWidth(child) + child.marginRight();
                if (i < children.size() - 1) {
                    childrenWidth += spacing;
                }
            }
            width += childrenWidth;
        } else {
            int childWidth = 0;
            for (PandaComponent child : children) {
                childWidth = Math.max(childWidth, child.marginLeft() + constrainedWidth(child) + child.marginRight());
            }
            width += childWidth;
        }
        return width;
    }

    public int preferredHeight() {
        int height = paddingTop() + paddingBottom();
        if (mode == Mode.ROW) {
            int childHeight = 0;
            for (PandaComponent child : children) {
                childHeight = Math.max(childHeight, child.marginTop() + constrainedHeight(child) + child.marginBottom());
            }
            height += childHeight;
        } else {
            int childrenHeight = 0;
            for (int i = 0; i < children.size(); i++) {
                PandaComponent child = children.get(i);
                childrenHeight += child.marginTop() + constrainedHeight(child) + child.marginBottom();
                if (i < children.size() - 1) {
                    childrenHeight += spacing;
                }
            }
            height += childrenHeight;
        }
        return height;
    }

    public void layout(PandaRect bounds) {
        setBounds(bounds);
        PandaRect content = contentBounds(bounds());
        if (mode == Mode.VERTICAL) {
            layoutVertical(content);
        } else if (mode == Mode.ROW) {
            layoutRow(content);
        } else if (mode == Mode.ABSOLUTE) {
            layoutAbsolute(content);
        } else if (mode == Mode.ANCHOR) {
            layoutAnchor(content);
        }
    }

    public void update(long deltaMs) {
        super.update(deltaMs);
        for (PandaComponent child : children) {
            if (child.visible()) {
                PandaUiErrorHandler.update(child, deltaMs);
            }
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return PandaInputDispatcher.dispatch(children, mouseX, mouseY, button);
    }

    public boolean keyTyped(char character, int keyCode) {
        List<PandaComponent> snapshot = new ArrayList<PandaComponent>(children);
        for (int i = snapshot.size() - 1; i >= 0; i--) {
            PandaComponent child = snapshot.get(i);
            if (child.visible() && child.enabled() && PandaUiErrorHandler.keyTyped(child, character, keyCode)) {
                return true;
            }
        }
        return false;
    }

    private void layoutVertical(PandaRect bounds) {
        int y = bounds.y;
        for (PandaComponent child : children) {
            y += child.marginTop();
            int childX = clamp(bounds.x + child.marginLeft(), bounds.x, right(bounds));
            int width = clampDimension(constrainedWidth(child), right(bounds) - child.marginRight() - childX);
            int childY = clamp(y, bounds.y, bottom(bounds));
            int height = clampDimension(constrainedHeight(child), bottom(bounds) - child.marginBottom() - childY);
            child.layout(new PandaRect(childX, childY, width, height));
            y = childY + height + child.marginBottom() + spacing;
        }
    }

    private void layoutRow(PandaRect bounds) {
        int x = bounds.x;
        for (PandaComponent child : children) {
            x += child.marginLeft();
            int childX = clamp(x, bounds.x, right(bounds));
            int childY = clamp(bounds.y + child.marginTop(), bounds.y, bottom(bounds));
            int width = clampDimension(constrainedWidth(child), right(bounds) - child.marginRight() - childX);
            int height = clampDimension(constrainedHeight(child), bottom(bounds) - child.marginBottom() - childY);
            child.layout(new PandaRect(childX, childY, width, height));
            x = childX + width + child.marginRight() + spacing;
        }
    }

    private void layoutAbsolute(PandaRect bounds) {
        for (PandaComponent child : children) {
            int childX = clamp(bounds.x + child.layoutX() + child.marginLeft(), bounds.x, right(bounds));
            int childY = clamp(bounds.y + child.layoutY() + child.marginTop(), bounds.y, bottom(bounds));
            int width = clampDimension(constrainedWidth(child), right(bounds) - child.marginRight() - childX);
            int height = clampDimension(constrainedHeight(child), bottom(bounds) - child.marginBottom() - childY);
            child.layout(new PandaRect(childX, childY, width, height));
        }
    }

    private void layoutAnchor(PandaRect bounds) {
        for (PandaComponent child : children) {
            int width = clampDimension(constrainedWidth(child), bounds.width - child.marginLeft() - child.marginRight());
            int height = clampDimension(constrainedHeight(child), bounds.height - child.marginTop() - child.marginBottom());
            int childX = anchoredX(bounds, child, width);
            int childY = anchoredY(bounds, child, height);
            child.layout(new PandaRect(childX, childY, width, height));
        }
    }

    private PandaRect contentBounds(PandaRect bounds) {
        int x = bounds.x + paddingLeft();
        int y = bounds.y + paddingTop();
        int width = Math.max(0, bounds.width - paddingLeft() - paddingRight());
        int height = Math.max(0, bounds.height - paddingTop() - paddingBottom());
        return new PandaRect(x, y, width, height);
    }

    private int anchoredX(PandaRect bounds, PandaComponent child, int width) {
        if (child.anchorPoint() == Anchor.TOP_RIGHT || child.anchorPoint() == Anchor.RIGHT || child.anchorPoint() == Anchor.BOTTOM_RIGHT) {
            return clamp(right(bounds) - child.marginRight() - width, bounds.x, right(bounds));
        }
        if (child.anchorPoint() == Anchor.TOP || child.anchorPoint() == Anchor.CENTER || child.anchorPoint() == Anchor.BOTTOM) {
            return clamp(bounds.x + (bounds.width - width) / 2 + child.marginLeft() - child.marginRight(), bounds.x, right(bounds));
        }
        return clamp(bounds.x + child.marginLeft(), bounds.x, right(bounds));
    }

    private int anchoredY(PandaRect bounds, PandaComponent child, int height) {
        if (child.anchorPoint() == Anchor.BOTTOM_LEFT || child.anchorPoint() == Anchor.BOTTOM || child.anchorPoint() == Anchor.BOTTOM_RIGHT) {
            return clamp(bottom(bounds) - child.marginBottom() - height, bounds.y, bottom(bounds));
        }
        if (child.anchorPoint() == Anchor.LEFT || child.anchorPoint() == Anchor.CENTER || child.anchorPoint() == Anchor.RIGHT) {
            return clamp(bounds.y + (bounds.height - height) / 2 + child.marginTop() - child.marginBottom(), bounds.y, bottom(bounds));
        }
        return clamp(bounds.y + child.marginTop(), bounds.y, bottom(bounds));
    }

    private static int constrainedWidth(PandaComponent child) {
        return constrain(Math.max(0, child.preferredWidth()), child.minWidth(), child.maxWidth());
    }

    private static int constrainedHeight(PandaComponent child) {
        return constrain(Math.max(0, child.preferredHeight()), child.minHeight(), child.maxHeight());
    }

    private static int constrain(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private static int clampDimension(int requested, int available) {
        return Math.min(Math.max(0, requested), Math.max(0, available));
    }

    private static int right(PandaRect bounds) {
        return bounds.x + bounds.width;
    }

    private static int bottom(PandaRect bounds) {
        return bounds.y + bounds.height;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private enum Mode {
        VERTICAL,
        ROW,
        ABSOLUTE,
        ANCHOR
    }

    /**
     * Anchor points supported by {@link #anchor()} layouts.
     */
    public enum Anchor {
        TOP_LEFT,
        TOP,
        TOP_RIGHT,
        LEFT,
        CENTER,
        RIGHT,
        BOTTOM_LEFT,
        BOTTOM,
        BOTTOM_RIGHT
    }
}
