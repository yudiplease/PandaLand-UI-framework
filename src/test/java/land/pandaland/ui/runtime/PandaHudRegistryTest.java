package land.pandaland.ui.runtime;

import land.pandaland.ui.api.PandaHudOverlay;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PandaHudRegistryTest {
    @Test
    public void overlaysAreSortedByPriority() {
        PandaHudRegistry registry = new PandaHudRegistry();
        registry.register(new TestOverlay(10));
        registry.register(new TestOverlay(1));
        registry.register(new TestOverlay(5));

        assertEquals(1, registry.overlays().get(0).priority());
        assertEquals(5, registry.overlays().get(1).priority());
        assertEquals(10, registry.overlays().get(2).priority());
    }

    @Test
    public void duplicateOverlayIsRegisteredOnlyOnce() {
        PandaHudRegistry registry = new PandaHudRegistry();
        PandaHudOverlay overlay = new TestOverlay(10);

        registry.register(overlay);
        registry.register(overlay);

        assertEquals(1, registry.overlays().size());
    }

    @Test
    public void unregisterRemovesOverlay() {
        PandaHudRegistry registry = new PandaHudRegistry();
        PandaHudOverlay overlay = new TestOverlay(10);

        registry.register(overlay);
        registry.unregister(overlay);

        assertTrue(registry.overlays().isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void overlaySnapshotIsUnmodifiable() {
        PandaHudRegistry registry = new PandaHudRegistry();
        registry.register(new TestOverlay(10));

        registry.overlays().add(new TestOverlay(1));
    }

    @Test
    public void overlaySnapshotIsNotChangedByLaterRegistryUpdates() {
        PandaHudRegistry registry = new PandaHudRegistry();
        PandaHudOverlay first = new TestOverlay(10);
        PandaHudOverlay second = new TestOverlay(1);
        registry.register(first);

        List<PandaHudOverlay> snapshot = registry.overlays();
        registry.register(second);
        registry.unregister(first);

        assertEquals(1, snapshot.size());
        assertTrue(snapshot.contains(first));
        assertFalse(snapshot.contains(second));
    }

    private static final class TestOverlay extends PandaHudOverlay {
        private final int priority;

        private TestOverlay(int priority) {
            this.priority = priority;
        }

        public int priority() {
            return priority;
        }
    }
}
