package land.pandaland.ui.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PandaScreenLifecycleTest {
    @Test
    public void minecraftLifecycleHooksOpenCloseAndTickWithDelta() {
        TestScreen screen = new TestScreen();
        screen.size(320, 240);
        screen.now = 100L;

        screen.initGui();
        screen.now = 125L;
        screen.updateScreen();
        screen.onGuiClosed();

        assertEquals(1, screen.opens);
        assertEquals(1, screen.closes);
        assertEquals(1, screen.ticks);
        assertEquals(25L, screen.lastDeltaMs);
        assertEquals(25L, screen.component.updatedMs);
    }

    private static final class TestScreen extends PandaScreen {
        private final CountingComponent component = new CountingComponent();
        private int opens;
        private int closes;
        private int ticks;
        private long now;
        private long lastDeltaMs;

        private void size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        protected void build(PandaLayout root) {
            root.add(component);
        }

        protected void onOpen() {
            opens++;
        }

        protected void onClose() {
            closes++;
        }

        protected void tick(long deltaMs) {
            ticks++;
            lastDeltaMs = deltaMs;
        }

        protected long currentTimeMs() {
            return now;
        }
    }

    private static final class CountingComponent extends PandaComponent {
        private long updatedMs;

        public void update(long deltaMs) {
            super.update(deltaMs);
            updatedMs += deltaMs;
        }
    }
}
