package land.pandaland.ui.api;

import land.pandaland.ui.api.PandaRect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PandaModalTest {
    @Test
    public void modalWrapsPanelAndCloseCallback() {
        final int[] closes = {0};
        PandaModal modal = PandaModal.titled("Confirm")
            .add(PandaLabel.text("Continue?"))
            .onClose(new Runnable() {
                public void run() {
                    closes[0]++;
                }
            });

        modal.close();

        assertEquals(1, closes[0]);
        assertEquals("Confirm", modal.panel().title());
        assertEquals(1, modal.panel().content().children().size());
    }

    @Test
    public void modalLaysOutWrappedPanel() {
        PandaModal modal = PandaModal.titled("Confirm");

        modal.layout(new PandaRect(20, 30, 200, 120));

        assertEquals(modal.bounds(), modal.panel().bounds());
        assertTrue(modal.preferredWidth() > 0);
        assertTrue(modal.preferredHeight() > 0);
    }

    @Test
    public void closeIsIdempotentAndMarksModalClosed() {
        final int[] closes = {0};
        PandaModal modal = PandaModal.titled("Confirm").onClose(new Runnable() {
            public void run() {
                closes[0]++;
            }
        });

        modal.close();
        modal.close();

        assertEquals(1, closes[0]);
        assertTrue(modal.closed());
        assertFalse(modal.open());
        assertFalse(modal.visible());
        assertFalse(modal.enabled());
    }

    @Test
    public void closedModalDoesNotDispatchInputToPanel() {
        CountingComponent child = new CountingComponent();
        PandaModal modal = PandaModal.titled("Confirm").add(child);
        modal.layout(new PandaRect(0, 0, 240, 180));

        modal.close();

        assertFalse(modal.mouseClicked(20, 40, 0));
        assertFalse(modal.keyTyped('x', 45));
        assertEquals(0, child.clicks);
        assertEquals(0, child.keys);
    }

    @Test
    public void hiddenModalDoesNotDispatchDirectInputToPanel() {
        CountingComponent child = new CountingComponent();
        PandaModal modal = PandaModal.titled("Confirm").add(child);
        modal.layout(new PandaRect(0, 0, 240, 180));
        modal.visible(false);

        assertFalse(modal.mouseClicked(20, 40, 0));
        assertFalse(modal.keyTyped('x', 45));
        assertEquals(0, child.clicks);
        assertEquals(0, child.keys);
    }

    @Test
    public void disabledModalDoesNotDispatchDirectInputToPanel() {
        CountingComponent child = new CountingComponent();
        PandaModal modal = PandaModal.titled("Confirm").add(child);
        modal.layout(new PandaRect(0, 0, 240, 180));
        modal.enabled(false);

        assertFalse(modal.mouseClicked(20, 40, 0));
        assertFalse(modal.keyTyped('x', 45));
        assertEquals(0, child.clicks);
        assertEquals(0, child.keys);
    }

    private static final class CountingComponent extends PandaComponent {
        private int clicks;
        private int keys;

        public int preferredWidth() {
            return 100;
        }

        public int preferredHeight() {
            return 32;
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
