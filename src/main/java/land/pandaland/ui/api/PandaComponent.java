package land.pandaland.ui.api;

/**
 * Base class for every UI component.
 *
 * <p>Components own bounds, visibility, enabled state, focus state, hover and
 * press animation state, layout constraints, and low-level input hooks. Most
 * concrete controls extend this class and override preferred size or input
 * methods.</p>
 */
public class PandaComponent {
    private PandaRect bounds = new PandaRect(0, 0, 0, 0);
    private boolean visible = true;
    private boolean enabled = true;
    private boolean focusable;
    private boolean focused;
    private boolean hovered;
    private boolean pressed;
    private boolean reducedMotion;
    private final PandaAnimation hoverAnimation = PandaAnimations.hoverFade(0.0f);
    private final PandaAnimation pressAnimation = PandaAnimations.pressFeedback(0.0f);
    private int marginLeft;
    private int marginTop;
    private int marginRight;
    private int marginBottom;
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private int minWidth;
    private int minHeight;
    private int maxWidth = Integer.MAX_VALUE;
    private int maxHeight = Integer.MAX_VALUE;
    private int layoutX;
    private int layoutY;
    private PandaLayout.Anchor anchor = PandaLayout.Anchor.TOP_LEFT;

    /** @return current component bounds */
    public PandaRect bounds() {
        return bounds;
    }

    /**
     * Sets component bounds directly.
     *
     * @param bounds new bounds; {@code null} resets to an empty rectangle
     */
    public void setBounds(PandaRect bounds) {
        this.bounds = bounds == null ? new PandaRect(0, 0, 0, 0) : bounds;
    }

    /**
     * Lays out the component inside the supplied bounds.
     *
     * <p>Composite components override this method to lay out their children.</p>
     *
     * @param bounds allocated bounds
     */
    public void layout(PandaRect bounds) {
        setBounds(bounds);
    }

    /** @return whether the component should be shown and receive input */
    public boolean visible() {
        return visible;
    }

    /** Sets visibility and clears transient input state when hidden. */
    public PandaComponent visible(boolean visible) {
        this.visible = visible;
        if (!visible) {
            setFocused(false);
            setHovered(false);
            setPressed(false);
        }
        return this;
    }

    /** @return whether the component can receive input */
    public boolean enabled() {
        return enabled;
    }

    /** Sets enabled state and clears transient input state when disabled. */
    public PandaComponent enabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            setFocused(false);
            setHovered(false);
            setPressed(false);
        }
        return this;
    }

    /** @return whether this component can become focused */
    public boolean focusable() {
        return focusable;
    }

    /** Sets whether this component can become focused. */
    public PandaComponent focusable(boolean focusable) {
        this.focusable = focusable;
        if (!focusable) {
            setFocused(false);
        }
        return this;
    }

    /** @return whether this component is currently focused */
    public boolean focused() {
        return focused;
    }

    /** Requests focus for this component. */
    public void focus() {
        setFocused(true);
    }

    /** Clears focus from this component. */
    public void blur() {
        setFocused(false);
    }

    /** @return whether the mouse currently hovers over this component */
    public boolean hovered() {
        return hovered;
    }

    /** @return animated hover amount in the range usually used as {@code 0..1} */
    public float hoverAmount() {
        return hoverAnimation.value();
    }

    /** @return whether this component is currently pressed */
    public boolean pressed() {
        return pressed;
    }

    /** @return animated press amount in the range usually used as {@code 0..1} */
    public float pressAmount() {
        return pressAnimation.value();
    }

    /** @return whether this component snaps its own decorative animations */
    public boolean reducedMotion() {
        return reducedMotion;
    }

    /** Enables or disables reduced motion for this component. */
    public PandaComponent reducedMotion(boolean reducedMotion) {
        this.reducedMotion = reducedMotion;
        return this;
    }

    /** Sets equal margin on every side. */
    public PandaComponent margin(int margin) {
        return margin(margin, margin, margin, margin);
    }

    /** Sets horizontal and vertical margins. */
    public PandaComponent margin(int horizontal, int vertical) {
        return margin(horizontal, vertical, horizontal, vertical);
    }

    /** Sets individual side margins. */
    public PandaComponent margin(int left, int top, int right, int bottom) {
        marginLeft = Math.max(0, left);
        marginTop = Math.max(0, top);
        marginRight = Math.max(0, right);
        marginBottom = Math.max(0, bottom);
        return this;
    }

    /** @return left margin */
    public int marginLeft() {
        return marginLeft;
    }

    /** @return top margin */
    public int marginTop() {
        return marginTop;
    }

    /** @return right margin */
    public int marginRight() {
        return marginRight;
    }

    /** @return bottom margin */
    public int marginBottom() {
        return marginBottom;
    }

    /** Sets equal padding on every side. */
    public PandaComponent padding(int padding) {
        return padding(padding, padding, padding, padding);
    }

    /** Sets horizontal and vertical padding. */
    public PandaComponent padding(int horizontal, int vertical) {
        return padding(horizontal, vertical, horizontal, vertical);
    }

    /** Sets individual side padding. */
    public PandaComponent padding(int left, int top, int right, int bottom) {
        paddingLeft = Math.max(0, left);
        paddingTop = Math.max(0, top);
        paddingRight = Math.max(0, right);
        paddingBottom = Math.max(0, bottom);
        return this;
    }

    /** @return left padding */
    public int paddingLeft() {
        return paddingLeft;
    }

    /** @return top padding */
    public int paddingTop() {
        return paddingTop;
    }

    /** @return right padding */
    public int paddingRight() {
        return paddingRight;
    }

    /** @return bottom padding */
    public int paddingBottom() {
        return paddingBottom;
    }

    /** Sets minimum preferred size constraints. */
    public PandaComponent minSize(int width, int height) {
        minWidth = Math.max(0, width);
        minHeight = Math.max(0, height);
        return this;
    }

    /** @return minimum width constraint */
    public int minWidth() {
        return minWidth;
    }

    /** @return minimum height constraint */
    public int minHeight() {
        return minHeight;
    }

    /** Sets maximum preferred size constraints. */
    public PandaComponent maxSize(int width, int height) {
        maxWidth = Math.max(0, width);
        maxHeight = Math.max(0, height);
        return this;
    }

    /** @return maximum width constraint */
    public int maxWidth() {
        return maxWidth;
    }

    /** @return maximum height constraint */
    public int maxHeight() {
        return maxHeight;
    }

    /** Sets explicit layout position used by absolute layouts. */
    public PandaComponent position(int x, int y) {
        layoutX = x;
        layoutY = y;
        return this;
    }

    /** @return explicit layout x used by absolute layouts */
    public int layoutX() {
        return layoutX;
    }

    /** @return explicit layout y used by absolute layouts */
    public int layoutY() {
        return layoutY;
    }

    /** Sets anchor point used by anchor layouts. */
    public PandaComponent anchor(PandaLayout.Anchor anchor) {
        this.anchor = anchor == null ? PandaLayout.Anchor.TOP_LEFT : anchor;
        return this;
    }

    /** @return anchor point used by anchor layouts */
    public PandaLayout.Anchor anchorPoint() {
        return anchor;
    }

    /** @return preferred width before parent constraints are applied */
    public int preferredWidth() {
        return bounds.width;
    }

    /** @return preferred height before parent constraints are applied */
    public int preferredHeight() {
        return bounds.height;
    }

    /**
     * Updates animations and component state.
     *
     * @param deltaMs elapsed time in milliseconds
     */
    public void update(long deltaMs) {
        if (reducedMotion || PandaUi.theme().reducedMotion()) {
            hoverAnimation.setTarget(hovered ? 1.0f : 0.0f);
            pressAnimation.setTarget(pressed ? 1.0f : 0.0f);
            hoverAnimation.update(Long.MAX_VALUE);
            pressAnimation.update(Long.MAX_VALUE);
            return;
        }
        hoverAnimation.update(deltaMs);
        pressAnimation.update(deltaMs);
    }

    /** Updates hover state and invokes hover callbacks when it changes. */
    public void setHovered(boolean hovered) {
        if (this.hovered == hovered) {
            return;
        }
        this.hovered = hovered;
        hoverAnimation.setTarget(hovered ? 1.0f : 0.0f);
        if (hovered) {
            onHoverEnter();
        } else {
            onHoverExit();
        }
    }

    /** Updates pressed state. */
    public void setPressed(boolean pressed) {
        if (this.pressed == pressed) {
            return;
        }
        this.pressed = pressed;
        pressAnimation.setTarget(pressed ? 1.0f : 0.0f);
    }

    /** Handles mouse press input. */
    public boolean mousePressed(int mouseX, int mouseY, int button) {
        return false;
    }

    /** Handles mouse release input. */
    public boolean mouseReleased(int mouseX, int mouseY, int button) {
        return false;
    }

    /** Handles mouse drag input while this component is pressed. */
    public boolean mouseDragged(int mouseX, int mouseY, int button, long dragTimeMs) {
        return false;
    }

    /** Handles mouse click input. */
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return false;
    }

    /** Handles keyboard input. */
    public boolean keyTyped(char character, int keyCode) {
        return false;
    }

    /** Called when hover state becomes active. */
    protected void onHoverEnter() {
    }

    /** Called when hover state becomes inactive. */
    protected void onHoverExit() {
    }

    /** Called when focus state becomes active. */
    protected void onFocus() {
    }

    /** Called when focus state becomes inactive. */
    protected void onBlur() {
    }

    private void setFocused(boolean focused) {
        if (this.focused == focused) {
            return;
        }
        this.focused = focused;
        if (focused) {
            onFocus();
        } else {
            onBlur();
        }
    }
}
