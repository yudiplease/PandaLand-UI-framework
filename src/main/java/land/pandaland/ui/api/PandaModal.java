package land.pandaland.ui.api;

import land.pandaland.ui.api.PandaRect;

/**
 * Modal panel shown above a {@link PandaScreen} root layout.
 */
public final class PandaModal extends PandaComponent {
    private final PandaPanel panel;
    private Runnable onClose;
    private boolean closed;

    private PandaModal(String title) {
        this.panel = PandaPanel.glass().title(title);
    }

    /**
     * Creates a titled modal.
     *
     * @param title modal title
     * @return modal component
     */
    public static PandaModal titled(String title) {
        return new PandaModal(title);
    }

    /**
     * Adds content to the modal panel.
     *
     * @param component child component
     * @return this modal
     */
    public PandaModal add(PandaComponent component) {
        panel.add(component);
        return this;
    }

    /**
     * Registers a close callback.
     *
     * @param onClose callback invoked once when the modal closes
     * @return this modal
     */
    public PandaModal onClose(Runnable onClose) {
        this.onClose = onClose;
        return this;
    }

    /**
     * Closes the modal and disables further input.
     */
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        visible(false);
        enabled(false);
        if (onClose != null) {
            onClose.run();
        }
    }

    /**
     * @return whether this modal has been closed
     */
    public boolean closed() {
        return closed;
    }

    /**
     * @return whether this modal is still open
     */
    public boolean open() {
        return !closed;
    }

    /**
     * @return backing glass panel containing modal content
     */
    public PandaPanel panel() {
        return panel;
    }

    public int preferredWidth() {
        return panel.preferredWidth();
    }

    public int preferredHeight() {
        return panel.preferredHeight();
    }

    public void layout(PandaRect bounds) {
        setBounds(bounds);
        panel.layout(bounds());
    }

    public void update(long deltaMs) {
        if (closed) {
            return;
        }
        super.update(deltaMs);
        panel.update(deltaMs);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (closed || !visible() || !enabled()) {
            return false;
        }
        return panel.mouseClicked(mouseX, mouseY, button);
    }

    public boolean keyTyped(char character, int keyCode) {
        if (closed || !visible() || !enabled()) {
            return false;
        }
        return panel.keyTyped(character, keyCode);
    }
}
