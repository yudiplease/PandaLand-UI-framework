package land.pandaland.ui.v2.render;

import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.style.UiTheme;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class UiRenderTraversalTest {
    @Test
    public void buttonProducesPanelAndTextCommands() {
        UiScreen screen = Ui.screen("main")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().button("Play", new Runnable() {
                        public void run() {
                        }
                    });
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 120, 40));

        UiRenderList commands = UiRenderTraversal.render(runtime, UiTheme.pandalandDefault());

        assertEquals(UiRenderCommand.Type.ROUNDED_RECT, commands.get(0).type());
        assertEquals(UiRenderCommand.Type.TEXT, commands.get(1).type());
    }
}
