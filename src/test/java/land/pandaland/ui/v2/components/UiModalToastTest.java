package land.pandaland.ui.v2.components;

import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.layout.UiRect;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class UiModalToastTest {
    @Test
    public void runtimeCanShowAndCloseModal() {
        UiScreen screen = Ui.screen("main")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().label("Main");
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);

        runtime.showModal(Ui.modal("Confirm"));
        assertEquals(1, runtime.modalCount());
        runtime.closeTopModal();
        assertEquals(0, runtime.modalCount());
    }

    @Test
    public void toastExpiresAfterDuration() {
        UiScreen screen = Ui.screen("main")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().label("Main");
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);

        runtime.toast("Saved", 100L);
        runtime.update(120L);

        assertEquals(0, runtime.toastCount());
    }

    @Test
    public void activeModalCapturesPointerBeforeRoot() {
        final AtomicInteger clicks = new AtomicInteger(0);
        UiScreen screen = Ui.screen("main")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().button("Root", new Runnable() {
                        public void run() {
                            clicks.incrementAndGet();
                        }
                    });
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 120, 80));
        runtime.showModal(Ui.modal("Confirm"));

        runtime.events().pointerDown(4, 4, 0);
        runtime.events().pointerUp(4, 4, 0);

        assertEquals(0, clicks.get());
    }
}
