package land.pandaland.ui.v2.render;

import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.state.UiState;
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

    @Test
    public void focusedTextInputRendersSelectionAndCaretCommands() {
        final UiState<String> value = UiState.of("PandaLand");
        UiScreen screen = Ui.screen("input")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().textInput(value, "", 80, 22);
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 100, 40));
        runtime.events().pointerDown(8, 8, 0);
        screen.root().children().get(0).selection(0, 5);

        UiRenderList commands = UiRenderTraversal.render(runtime, UiTheme.pandalandDefault());

        assertEquals(UiRenderCommand.Type.ROUNDED_RECT, commands.get(0).type());
        assertEquals(UiRenderCommand.Type.PROGRESS, commands.get(1).type());
        assertEquals(UiRenderCommand.Type.ROUNDED_RECT, commands.get(2).type());
    }
}
