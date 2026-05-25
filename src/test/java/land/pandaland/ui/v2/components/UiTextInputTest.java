package land.pandaland.ui.v2.components;

import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.event.UiTextValidator;
import land.pandaland.ui.v2.event.UiValidationResult;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.state.UiState;
import org.junit.Test;
import org.lwjgl.input.Keyboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class UiTextInputTest {
    @Test
    public void textInputReplacesSelectionAndMovesCursor() {
        final UiState<String> value = UiState.of("Panda");
        UiRuntime runtime = runtime(value, null);
        UiNode input = runtime.screen().root().children().get(0);
        runtime.layout(new UiRect(0, 0, 160, 22));
        runtime.events().pointerDown(8, 8, 0);

        input.selection(1, 4).cursorPosition(4);
        runtime.events().keyTyped('o', 24);

        assertEquals("Poa", value.get());
        assertEquals(2, input.cursorPosition());
        assertEquals(input.cursorPosition(), input.selectionStart());
        assertEquals(input.cursorPosition(), input.selectionEnd());
    }

    @Test
    public void validationRejectsInvalidCandidateAndKeepsValue() {
        final UiState<String> value = UiState.of("abc");
        UiRuntime runtime = runtime(value, new UiTextValidator() {
            public UiValidationResult validate(String candidate) {
                return candidate.indexOf('!') >= 0 ? UiValidationResult.invalid("No bang") : UiValidationResult.ok();
            }
        });
        UiNode input = runtime.screen().root().children().get(0);
        runtime.layout(new UiRect(0, 0, 160, 22));
        runtime.events().pointerDown(8, 8, 0);

        runtime.events().keyTyped('!', 2);

        assertEquals("abc", value.get());
        assertEquals("No bang", input.validationMessage());
    }

    @Test
    public void cursorMovementUpdatesHorizontalScroll() {
        final UiState<String> value = UiState.of("abcdefghijklmnop");
        UiRuntime runtime = runtime(value, null);
        UiNode input = runtime.screen().root().children().get(0);
        runtime.layout(new UiRect(0, 0, 40, 22));
        runtime.events().pointerDown(8, 8, 0);

        runtime.events().keyTyped('\0', Keyboard.KEY_END);

        assertEquals(value.get().length(), input.cursorPosition());
        assertTrue(input.horizontalScroll() > 0);
    }

    private static UiRuntime runtime(final UiState<String> value, final UiTextValidator validator) {
        UiScreen screen = Ui.screen("input")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().textInput(value, "Name", 160, 22, 32, null, null, validator);
                }
            })
            .build();
        return new UiRuntime(screen);
    }
}
