package land.pandaland.ui.v2.core;

import land.pandaland.ui.v2.api.Ui;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class UiNodeTest {
    @Test
    public void fluentApiCreatesNamedScreenWithRootAndChildren() {
        UiScreen screen = Ui.screen("main")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column()
                        .label("PandaLand")
                        .button("Play", new Runnable() {
                            public void run() {
                            }
                        });
                }
            })
            .build();

        assertEquals("main", screen.id());
        assertEquals(2, screen.root().children().size());
    }

    @Test
    public void invalidatingChildInvalidatesParentRuntime() {
        UiScreen screen = Ui.screen("main")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().label("A");
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);

        screen.root().children().get(0).invalidate();

        assertTrue(runtime.invalid());
    }
}
