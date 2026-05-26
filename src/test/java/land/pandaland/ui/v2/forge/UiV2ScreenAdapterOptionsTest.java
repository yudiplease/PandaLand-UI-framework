package land.pandaland.ui.v2.forge;

import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.minecraft.UiScreenOptions;
import land.pandaland.ui.v2.minecraft.UiTextEnterBehavior;
import land.pandaland.ui.v2.minecraft.UiTextEscapeBehavior;
import land.pandaland.ui.v2.state.UiState;
import net.minecraft.client.gui.GuiScreen;
import org.junit.Test;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public final class UiV2ScreenAdapterOptionsTest {
    @Test
    public void defaultsExposeTextInputPoliciesRequiredByMinecraftScreens() {
        UiScreenOptions options = UiScreenOptions.defaults();

        assertEquals(UiTextEnterBehavior.COMMIT, options.textEnterBehavior());
        assertEquals(UiTextEscapeBehavior.BLUR, options.textEscapeBehavior());
        assertEquals(UiTextEnterBehavior.BLUR, UiTextEnterBehavior.valueOf("BLUR"));
        assertEquals(UiTextEnterBehavior.NONE, UiTextEnterBehavior.valueOf("NONE"));
        assertEquals(UiTextEscapeBehavior.CLOSE_SCREEN, UiTextEscapeBehavior.valueOf("CLOSE_SCREEN"));
        assertEquals(UiTextEscapeBehavior.NONE, UiTextEscapeBehavior.valueOf("NONE"));
    }

    @Test
    public void defaultConstructorPreservesGuiScreenPauseBehavior() {
        UiScreen screen = Ui.screen("pause").root(new Ui.RootBuilderConsumer() {
            public void build(Ui.NodeBuilder root) {
                root.label("x");
            }
        }).build();

        UiV2ScreenAdapter adapter = new UiV2ScreenAdapter(screen);

        assertEquals(new GuiScreen().doesGuiPauseGame(), adapter.doesGuiPauseGame());
    }

    @Test
    public void optionsConstructorControlsPauseBehavior() {
        UiScreen screen = Ui.screen("pause").root(new Ui.RootBuilderConsumer() {
            public void build(Ui.NodeBuilder root) {
                root.label("x");
            }
        }).build();

        assertEquals(false, new UiV2ScreenAdapter(screen, UiScreenOptions.defaults().pauseGame(false)).doesGuiPauseGame());
        assertEquals(true, new UiV2ScreenAdapter(screen, UiScreenOptions.defaults().pauseGame(true)).doesGuiPauseGame());
    }

    @Test
    public void closeOnEscapeFalseConsumesUnfocusedEscape() {
        UiScreen screen = Ui.screen("escape").root(new Ui.RootBuilderConsumer() {
            public void build(Ui.NodeBuilder root) {
                root.label("x");
            }
        }).build();
        RecordingScreenAdapter adapter = new RecordingScreenAdapter(screen, UiScreenOptions.defaults().closeOnEscape(false));
        adapter.initGui();

        adapter.keyTyped('\0', Keyboard.KEY_ESCAPE);

        assertEquals(0, adapter.unhandledKeys);
    }

    @Test
    public void closeOnEscapeTrueDelegatesUnfocusedEscapeToGuiScreenPath() {
        UiScreen screen = Ui.screen("escape").root(new Ui.RootBuilderConsumer() {
            public void build(Ui.NodeBuilder root) {
                root.label("x");
            }
        }).build();
        RecordingScreenAdapter adapter = new RecordingScreenAdapter(screen, UiScreenOptions.defaults().closeOnEscape(true));
        adapter.initGui();

        adapter.keyTyped('\0', Keyboard.KEY_ESCAPE);

        assertEquals(1, adapter.unhandledKeys);
    }

    @Test
    public void focusedTextInputEscapeTakesPrecedenceOverScreenClose() {
        final UiState<String> value = UiState.of("");
        UiScreen screen = Ui.screen("escape-input").root(new Ui.RootBuilderConsumer() {
            public void build(Ui.NodeBuilder root) {
                root.textInput(value, "Name", 100, 20);
            }
        }).build();
        RecordingScreenAdapter adapter = new RecordingScreenAdapter(screen, UiScreenOptions.defaults().closeOnEscape(true));
        adapter.initGui();
        adapter.runtime().layout(new UiRect(0, 0, 200, 120));
        adapter.runtime().events().pointerDown(5, 5, 0);

        adapter.keyTyped('\0', Keyboard.KEY_ESCAPE);

        assertEquals(0, adapter.unhandledKeys);
    }

    @Test
    public void focusedTextInputEscapeCanCloseScreenWithoutBlurringInputFirst() {
        final UiState<String> value = UiState.of("");
        UiScreen screen = Ui.screen("escape-input-close").root(new Ui.RootBuilderConsumer() {
            public void build(Ui.NodeBuilder root) {
                root.textInput(value, "Name", 100, 20);
            }
        }).build();
        RecordingScreenAdapter adapter = new RecordingScreenAdapter(screen,
                UiScreenOptions.defaults().textEscapeBehavior(UiTextEscapeBehavior.CLOSE_SCREEN));
        adapter.initGui();
        adapter.runtime().layout(new UiRect(0, 0, 200, 120));
        adapter.runtime().events().pointerDown(5, 5, 0);

        adapter.keyTyped('\0', Keyboard.KEY_ESCAPE);

        assertEquals(1, adapter.unhandledKeys);
        assertNotNull(adapter.runtime().focus().focused());
    }

    @Test
    public void focusedTextInputEscapeNoneBypassesDispatcherBlurAndHonorsScreenPolicy() {
        final UiState<String> value = UiState.of("");
        UiScreen screen = Ui.screen("escape-input-none").root(new Ui.RootBuilderConsumer() {
            public void build(Ui.NodeBuilder root) {
                root.textInput(value, "Name", 100, 20);
            }
        }).build();
        RecordingScreenAdapter adapter = new RecordingScreenAdapter(screen,
                UiScreenOptions.defaults()
                        .textEscapeBehavior(UiTextEscapeBehavior.NONE)
                        .closeOnEscape(false));
        adapter.initGui();
        adapter.runtime().layout(new UiRect(0, 0, 200, 120));
        adapter.runtime().events().pointerDown(5, 5, 0);

        adapter.keyTyped('\0', Keyboard.KEY_ESCAPE);

        assertEquals(0, adapter.unhandledKeys);
        assertNotNull(adapter.runtime().focus().focused());
    }

    @Test
    public void focusedTextInputEnterNoneBypassesCommitCallback() {
        final UiState<String> value = UiState.of("");
        final AtomicInteger enterCalls = new AtomicInteger();
        UiScreen screen = Ui.screen("enter-input-none").root(new Ui.RootBuilderConsumer() {
            public void build(Ui.NodeBuilder root) {
                root.textInput(value, "Name", 100, 20, 32, new Runnable() {
                    public void run() {
                        enterCalls.incrementAndGet();
                    }
                });
            }
        }).build();
        RecordingScreenAdapter adapter = new RecordingScreenAdapter(screen,
                UiScreenOptions.defaults().textEnterBehavior(UiTextEnterBehavior.NONE));
        adapter.initGui();
        adapter.runtime().layout(new UiRect(0, 0, 200, 120));
        adapter.runtime().events().pointerDown(5, 5, 0);

        adapter.keyTyped('\n', Keyboard.KEY_RETURN);

        assertEquals(1, adapter.unhandledKeys);
        assertEquals(0, enterCalls.get());
        assertNotNull(adapter.runtime().focus().focused());
    }

    private static final class RecordingScreenAdapter extends UiV2ScreenAdapter {
        private int unhandledKeys;

        private RecordingScreenAdapter(UiScreen screen, UiScreenOptions options) {
            super(screen, options);
        }

        protected void keyTypedUnhandled(char character, int keyCode) {
            unhandledKeys++;
        }
    }
}
