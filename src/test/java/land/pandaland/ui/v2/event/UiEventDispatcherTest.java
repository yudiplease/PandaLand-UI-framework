package land.pandaland.ui.v2.event;

import java.util.concurrent.atomic.AtomicInteger;
import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.layout.UiRect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class UiEventDispatcherTest {
    @Test
    public void clickRunsButtonActionOnce() {
        AtomicInteger clicks = new AtomicInteger(0);
        UiScreen screen = Ui.screen("main")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().button("Play", new Runnable() {
                        public void run() {
                            clicks.incrementAndGet();
                        }
                    });
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 120, 40));

        runtime.events().pointerDown(4, 4, 0);
        runtime.events().pointerUp(4, 4, 0);

        assertEquals(1, clicks.get());
    }

    @Test
    public void dragIsDeliveredToPressedNodeEvenWhenPointerLeavesBounds() {
        AtomicInteger drags = new AtomicInteger(0);
        UiScreen screen = Ui.screen("main")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().button("Drag", new Runnable() {
                        public void run() {
                        }
                    });
                }
            })
            .build();
        screen.root().children().get(0).onDrag(new Runnable() {
            public void run() {
                drags.incrementAndGet();
            }
        });
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 120, 40));

        runtime.events().pointerDown(4, 4, 0);
        runtime.events().pointerDrag(90, 90, 0, 16L);

        assertEquals(1, drags.get());
    }
}
