package land.pandaland.ui.api;

import land.pandaland.ui.api.PandaRect;

/**
 * Glass-style container with optional title and vertical content layout.
 */
public final class PandaPanel extends PandaComponent {
    private static final int PADDING = 12;
    private static final int TITLE_HEIGHT = 20;

    private final PandaLayout content = PandaLayout.vertical(8);
    private String title = "";

    private PandaPanel() {
    }

    /**
     * Creates an empty glass panel.
     *
     * @return panel component
     */
    public static PandaPanel glass() {
        return new PandaPanel();
    }

    /**
     * Sets the optional panel title.
     *
     * @param title title text
     * @return this panel
     */
    public PandaPanel title(String title) {
        this.title = title == null ? "" : title;
        return this;
    }

    /**
     * Adds content to the panel body.
     *
     * @param component child component
     * @return this panel
     */
    public PandaPanel add(PandaComponent component) {
        content.add(component);
        return this;
    }

    /**
     * @return panel title text
     */
    public String title() {
        return title;
    }

    /**
     * @return content layout used by this panel
     */
    public PandaLayout content() {
        return content;
    }

    public int preferredWidth() {
        return Math.max(240, content.preferredWidth() + PADDING * 2);
    }

    public int preferredHeight() {
        return Math.max(180, content.preferredHeight() + PADDING * 2 + titleOffset());
    }

    public void layout(PandaRect bounds) {
        setBounds(bounds);
        PandaRect inner = bounds().inset(PADDING);
        if (!title.isEmpty()) {
            inner = new PandaRect(inner.x, inner.y + TITLE_HEIGHT, inner.width, Math.max(0, inner.height - TITLE_HEIGHT));
        }
        content.layout(inner);
    }

    public void update(long deltaMs) {
        super.update(deltaMs);
        content.update(deltaMs);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible() || !enabled()) {
            return false;
        }
        return content.mouseClicked(mouseX, mouseY, button);
    }

    public boolean keyTyped(char character, int keyCode) {
        if (!visible() || !enabled()) {
            return false;
        }
        return content.keyTyped(character, keyCode);
    }

    private int titleOffset() {
        return title.isEmpty() ? 0 : TITLE_HEIGHT;
    }
}
