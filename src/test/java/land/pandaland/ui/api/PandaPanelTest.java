package land.pandaland.ui.api;

import land.pandaland.ui.api.PandaRect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PandaPanelTest {
    @Test
    public void panelLaysOutContentInsidePanelBounds() {
        PandaPanel panel = PandaPanel.glass().title("Menu");
        FixedComponent first = new FixedComponent(50, 10);
        FixedComponent second = new FixedComponent(60, 20);

        panel.add(first).add(second);
        panel.layout(new PandaRect(10, 20, 200, 120));

        assertEquals("Menu", panel.title());
        assertTrue(first.bounds().x > panel.bounds().x);
        assertTrue(first.bounds().y > panel.bounds().y);
        assertEquals(first.bounds().x, second.bounds().x);
        assertEquals(first.bounds().y + first.bounds().height + 8, second.bounds().y);
    }

    @Test
    public void panelDispatchesClicksToContent() {
        PandaPanel panel = PandaPanel.glass();
        ClickComponent child = new ClickComponent(50, 10);

        panel.add(child);
        panel.layout(new PandaRect(10, 20, 200, 120));

        assertTrue(panel.mouseClicked(child.bounds().x, child.bounds().y, 0));
        assertEquals(1, child.clicks);
    }

    @Test
    public void hiddenPanelDoesNotForwardDirectInputToContent() {
        PandaPanel panel = PandaPanel.glass();
        CountingComponent child = new CountingComponent();
        panel.add(child);
        panel.layout(new PandaRect(10, 20, 200, 120));
        panel.visible(false);

        assertFalse(panel.mouseClicked(child.bounds().x, child.bounds().y, 0));
        assertFalse(panel.keyTyped('x', 45));
        assertEquals(0, child.clicks);
        assertEquals(0, child.keys);
    }

    @Test
    public void disabledPanelDoesNotForwardDirectInputToContent() {
        PandaPanel panel = PandaPanel.glass();
        CountingComponent child = new CountingComponent();
        panel.add(child);
        panel.layout(new PandaRect(10, 20, 200, 120));
        panel.enabled(false);

        assertFalse(panel.mouseClicked(child.bounds().x, child.bounds().y, 0));
        assertFalse(panel.keyTyped('x', 45));
        assertEquals(0, child.clicks);
        assertEquals(0, child.keys);
    }

    private static final class FixedComponent extends PandaComponent {
        private final int preferredWidth;
        private final int preferredHeight;

        private FixedComponent(int preferredWidth, int preferredHeight) {
            this.preferredWidth = preferredWidth;
            this.preferredHeight = preferredHeight;
        }

        public int preferredWidth() {
            return preferredWidth;
        }

        public int preferredHeight() {
            return preferredHeight;
        }
    }

    private static final class ClickComponent extends PandaComponent {
        private final int preferredWidth;
        private final int preferredHeight;
        private int clicks;

        private ClickComponent(int preferredWidth, int preferredHeight) {
            this.preferredWidth = preferredWidth;
            this.preferredHeight = preferredHeight;
        }

        public int preferredWidth() {
            return preferredWidth;
        }

        public int preferredHeight() {
            return preferredHeight;
        }

        public boolean mouseClicked(int mouseX, int mouseY, int button) {
            clicks++;
            return true;
        }
    }

    private static final class CountingComponent extends PandaComponent {
        private int clicks;
        private int keys;

        public int preferredWidth() {
            return 50;
        }

        public int preferredHeight() {
            return 10;
        }

        public boolean mouseClicked(int mouseX, int mouseY, int button) {
            clicks++;
            return true;
        }

        public boolean keyTyped(char character, int keyCode) {
            keys++;
            return true;
        }
    }
}
