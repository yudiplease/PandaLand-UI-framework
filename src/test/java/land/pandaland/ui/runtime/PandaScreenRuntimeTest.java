package land.pandaland.ui.runtime;

import land.pandaland.ui.api.PandaComponent;
import land.pandaland.ui.api.PandaLayout;
import land.pandaland.ui.api.PandaModal;
import land.pandaland.ui.api.PandaRect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PandaScreenRuntimeTest {
    @Test
    public void updateAdvancesRootAndActiveModalComponents() {
        CountingComponent rootChild = new CountingComponent();
        CountingComponent modalChild = new CountingComponent();
        PandaLayout root = PandaLayout.absolute().add(rootChild);
        PandaScreenRuntime runtime = new PandaScreenRuntime(root);
        runtime.layout(new PandaRect(0, 0, 400, 300));
        runtime.showModal(PandaModal.titled("Modal").add(modalChild));

        runtime.update(50L);

        assertEquals(50L, rootChild.updatedMs);
        assertEquals(50L, modalChild.updatedMs);
    }

    @Test
    public void escapeClosesTopModalBeforeRootReceivesKey() {
        CountingComponent rootChild = new CountingComponent();
        PandaLayout root = PandaLayout.absolute().add(rootChild);
        PandaScreenRuntime runtime = new PandaScreenRuntime(root);
        PandaModal modal = PandaModal.titled("Modal");
        runtime.layout(new PandaRect(0, 0, 400, 300));
        runtime.showModal(modal);

        assertTrue(runtime.keyTyped('\0', PandaScreenRuntime.ESCAPE_KEY_CODE));

        assertTrue(modal.closed());
        assertEquals(0, runtime.modalCount());
        assertEquals(0, rootChild.keys);
    }

    @Test
    public void focusedComponentReceivesKeyBeforeOtherComponents() {
        CountingComponent first = new CountingComponent().focusableComponent();
        CountingComponent second = new CountingComponent().focusableComponent();
        PandaLayout root = PandaLayout.absolute()
            .add(first.position(0, 0).minSize(50, 50))
            .add(second.position(60, 0).minSize(50, 50));
        PandaScreenRuntime runtime = new PandaScreenRuntime(root);
        runtime.layout(new PandaRect(0, 0, 200, 100));

        assertTrue(runtime.mousePressed(65, 10, 0));
        assertTrue(second.focused());
        assertFalse(first.focused());

        assertTrue(runtime.keyTyped('x', 45));

        assertEquals(0, first.keys);
        assertEquals(1, second.keys);
    }

    @Test
    public void hoverAndPressedStatesAnimateOverUpdates() {
        CountingComponent component = new CountingComponent().focusableComponent();
        PandaLayout root = PandaLayout.absolute().add(component.position(0, 0).minSize(50, 50));
        PandaScreenRuntime runtime = new PandaScreenRuntime(root);
        runtime.layout(new PandaRect(0, 0, 200, 100));

        runtime.mouseMoved(10, 10);
        runtime.update(75L);

        assertTrue(component.hovered());
        assertTrue(component.hoverAmount() > 0.0f);
        assertTrue(component.hoverAmount() < 1.0f);

        runtime.mousePressed(10, 10, 0);
        runtime.update(40L);

        assertTrue(component.pressed());
        assertTrue(component.pressAmount() > 0.0f);
        assertTrue(component.pressAmount() < 1.0f);

        runtime.mouseReleased(10, 10, 0);

        assertFalse(component.pressed());
        assertEquals(1, component.clicks);
    }

    @Test
    public void dragIsDeliveredToPressedComponentOutsideBounds() {
        CountingComponent component = new CountingComponent().focusableComponent();
        PandaLayout root = PandaLayout.absolute().add(component.position(0, 0).minSize(50, 50));
        PandaScreenRuntime runtime = new PandaScreenRuntime(root);
        runtime.layout(new PandaRect(0, 0, 200, 100));

        assertTrue(runtime.mousePressed(10, 10, 0));
        assertTrue(runtime.mouseDragged(160, 80, 0, 25L));

        assertEquals(1, component.drags);
        assertEquals(160, component.lastDragX);
        assertEquals(80, component.lastDragY);
    }

    private static final class CountingComponent extends PandaComponent {
        private int clicks;
        private int drags;
        private int keys;
        private int lastDragX;
        private int lastDragY;
        private long updatedMs;

        private CountingComponent focusableComponent() {
            focusable(true);
            return this;
        }

        public void update(long deltaMs) {
            super.update(deltaMs);
            updatedMs += deltaMs;
        }

        public boolean mousePressed(int mouseX, int mouseY, int button) {
            return button == 0;
        }

        public boolean mouseClicked(int mouseX, int mouseY, int button) {
            clicks++;
            return true;
        }

        public boolean mouseDragged(int mouseX, int mouseY, int button, long dragTimeMs) {
            drags++;
            lastDragX = mouseX;
            lastDragY = mouseY;
            return button == 0;
        }

        public boolean keyTyped(char character, int keyCode) {
            keys++;
            return true;
        }
    }
}
