package land.pandaland.ui.v2.components;

import java.util.concurrent.atomic.AtomicInteger;

import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.event.UiKeyEvent;
import land.pandaland.ui.v2.event.UiPointerEvent;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.render.UiCustomDraw;
import land.pandaland.ui.v2.render.UiRenderCommand;
import land.pandaland.ui.v2.render.UiRenderList;
import land.pandaland.ui.v2.render.UiRenderTraversal;
import land.pandaland.ui.v2.state.UiState;
import land.pandaland.ui.v2.style.UiColor;
import land.pandaland.ui.v2.style.UiTheme;
import org.junit.Test;
import org.lwjgl.input.Keyboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public final class UiInputFamilyTest {
    @Test
    public void inputFamilyBuildersStoreTypedMetadata() {
        UiState<String> text = UiState.of("");
        UiState<String> number = UiState.of("");
        UiState<String> search = UiState.of("");
        UiState<String> masked = UiState.of("");

        UiScreen screen = Ui.screen("input-family")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column()
                        .textarea(text, "Notes", 180, 64, 512, "****")
                        .numericInput(number, "Count", 90, 22, 8, "###")
                        .searchInput(search, "Search", 140, 22, 64)
                        .textInput(masked, "Masked", 120, 22, 16, "AA-##");
                }
            })
            .build();

        UiNode textarea = screen.root().children().get(0);
        UiNode numeric = screen.root().children().get(1);
        UiNode searchInput = screen.root().children().get(2);
        UiNode maskedInput = screen.root().children().get(3);

        assertEquals(UiNode.Type.TEXT_INPUT, textarea.type());
        assertTrue(textarea.textArea());
        assertEquals("****", textarea.inputMask());

        assertEquals(UiNode.Type.TEXT_INPUT, numeric.type());
        assertTrue(numeric.numericInput());
        assertEquals("###", numeric.inputMask());

        assertEquals(UiNode.Type.TEXT_INPUT, searchInput.type());
        assertTrue(searchInput.searchInput());
        assertTrue(searchInput.searchable());

        assertEquals(UiNode.Type.TEXT_INPUT, maskedInput.type());
        assertEquals("AA-##", maskedInput.inputMask());
    }

    @Test
    public void textareaAcceptsNewlineButTextInputEnterBehaviorIsUnchanged() {
        UiState<String> textareaValue = UiState.of("A");
        UiState<String> textValue = UiState.of("B");
        AtomicInteger enterCalls = new AtomicInteger();

        UiScreen screen = Ui.screen("textarea")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column()
                        .textarea(textareaValue, "", 120, 44, 32)
                        .textInput(textValue, "", 120, 22, 32, new Runnable() {
                            public void run() {
                                enterCalls.incrementAndGet();
                            }
                        });
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 140, 80));

        runtime.events().pointerDown(8, 8, 0);
        runtime.events().keyTyped('\n', Keyboard.KEY_RETURN);
        assertEquals("A\n", textareaValue.get());

        runtime.events().pointerDown(8, 52, 0);
        runtime.events().keyTyped('\n', Keyboard.KEY_RETURN);
        assertEquals("B", textValue.get());
        assertEquals(1, enterCalls.get());
    }

    @Test
    public void numericInputRejectsNonNumericCharacters() {
        UiState<String> value = UiState.of("");
        UiRuntime runtime = new UiRuntime(Ui.screen("numeric")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().numericInput(value, "Count", 100, 22, 8);
                }
            })
            .build());
        runtime.layout(new UiRect(0, 0, 120, 30));

        runtime.events().pointerDown(8, 8, 0);
        runtime.events().keyTyped('1', 2);
        runtime.events().keyTyped('a', 30);
        runtime.events().keyTyped('.', 52);

        assertEquals("1.", value.get());
    }

    @Test
    public void numericInputAcceptsOnlyStrictSignedDecimalText() {
        UiState<String> value = UiState.of("");
        UiRuntime runtime = numericRuntime(value);
        runtime.layout(new UiRect(0, 0, 120, 30));
        runtime.events().pointerDown(8, 8, 0);

        type(runtime, "1-+..2");

        assertEquals("1.2", value.get());

        runtime = numericRuntime(UiState.of(""));
        runtime.layout(new UiRect(0, 0, 120, 30));
        runtime.events().pointerDown(8, 8, 0);
        type(runtime, "-12.3");

        assertEquals("-12.3", runtime.screen().root().children().get(0).valueState().get());
    }

    @Test
    public void numericSelectionReplacementFiltersWholeCandidate() {
        UiState<String> value = UiState.of("12.3");
        UiRuntime runtime = numericRuntime(value);
        runtime.layout(new UiRect(0, 0, 120, 30));
        runtime.events().pointerDown(8, 8, 0);

        UiNode input = runtime.screen().root().children().get(0);
        input.selection(0, value.get().length()).cursorPosition(value.get().length());
        type(runtime, "+4.5");

        assertEquals("+4.5", value.get());
    }

    @Test
    public void dragSelectionPreservesMouseDownAnchor() {
        UiState<String> value = UiState.of("abcdef");
        UiRuntime runtime = new UiRuntime(Ui.screen("drag")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().textInput(value, "", 120, 22, 32);
                }
            })
            .build());
        runtime.layout(new UiRect(0, 0, 140, 30));

        runtime.events().pointerDown(11, 8, 0);
        runtime.events().pointerDrag(23, 8, 0, 50L);

        UiNode input = runtime.screen().root().children().get(0);
        assertEquals(3, input.cursorPosition());
        assertEquals(3, input.selectionEnd());
        assertEquals(1, input.selectionStart());
    }

    @Test
    public void canvasUsesCustomDrawHook() {
        final UiCustomDraw draw = new UiCustomDraw() {
            public void draw(UiRenderList commands, UiRect bounds) {
                commands.add(UiRenderCommand.text("canvas", bounds, new UiColor(0xFFFFFFFF)));
            }
        };
        UiRuntime runtime = new UiRuntime(Ui.screen("canvas")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().canvas(draw, 90, 30);
                }
            })
            .build());
        runtime.layout(new UiRect(0, 0, 120, 50));

        UiNode canvas = runtime.screen().root().children().get(0);
        UiRenderList commands = UiRenderTraversal.render(runtime, UiTheme.pandalandDefault());

        assertEquals(UiNode.Type.CANVAS, canvas.type());
        assertSame(draw, canvas.customDraw());
        assertTrue(contains(commands, UiRenderCommand.Type.CUSTOM));
        assertTrue(containsText(commands, "canvas"));
    }

    @Test
    public void customComponentReceivesBuildRenderAndInputCallbacks() {
        final AtomicInteger buildCalls = new AtomicInteger();
        final AtomicInteger renderCalls = new AtomicInteger();
        final AtomicInteger pointerCalls = new AtomicInteger();
        final AtomicInteger keyCalls = new AtomicInteger();

        final UiCustomComponent component = new UiCustomComponent() {
            public void build(UiNode node) {
                buildCalls.incrementAndGet();
                node.text("built");
            }

            public void render(UiRenderList commands, UiNode node, UiRect bounds) {
                renderCalls.incrementAndGet();
                commands.add(UiRenderCommand.text(node.text(), bounds, new UiColor(0xFFFFFFFF)));
            }

            public boolean pointerDown(UiPointerEvent event, UiNode node) {
                pointerCalls.incrementAndGet();
                return true;
            }

            public boolean keyTyped(UiKeyEvent event, UiNode node) {
                keyCalls.incrementAndGet();
                return true;
            }
        };

        UiRuntime runtime = new UiRuntime(Ui.screen("custom")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().customComponent(component, 100, 30);
                }
            })
            .build());
        runtime.layout(new UiRect(0, 0, 120, 50));

        UiNode custom = runtime.screen().root().children().get(0);
        UiRenderList commands = UiRenderTraversal.render(runtime, UiTheme.pandalandDefault());
        runtime.events().pointerDown(8, 8, 0);
        runtime.events().keyTyped('x', 45);

        assertEquals(UiNode.Type.CUSTOM_COMPONENT, custom.type());
        assertSame(component, custom.customComponent());
        assertEquals(1, buildCalls.get());
        assertEquals(1, renderCalls.get());
        assertEquals(1, pointerCalls.get());
        assertEquals(1, keyCalls.get());
        assertTrue(containsText(commands, "built"));
    }

    @Test
    public void customComponentDefaultCallbacksDoNotConsumeInput() {
        final UiCustomComponent component = new UiCustomComponent() {
        };
        UiRuntime runtime = new UiRuntime(Ui.screen("custom-default")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().customComponent(component, 100, 30);
                }
            })
            .build());
        runtime.layout(new UiRect(0, 0, 120, 50));
        runtime.events().pointerDown(8, 8, 0);

        assertFalse(runtime.events().pointerDrag(10, 8, 0, 10L));
        assertFalse(runtime.events().keyTyped('x', 45));
    }

    @Test
    public void throwingCustomPointerUpDoesNotLeavePressedStateStuck() {
        final UiCustomComponent component = new UiCustomComponent() {
            public boolean pointerUp(UiPointerEvent event, UiNode node) {
                throw new RuntimeException("boom");
            }
        };
        UiRuntime runtime = new UiRuntime(Ui.screen("custom-throw")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().customComponent(component, 100, 30);
                }
            })
            .build());
        runtime.layout(new UiRect(0, 0, 120, 50));

        runtime.events().pointerDown(8, 8, 0);
        assertFalse(runtime.events().pointerUp(8, 8, 0));
        assertFalse(runtime.events().pointerDrag(10, 8, 0, 10L));
    }

    @Test
    public void throwingCustomRenderCallbackIsIgnored() {
        final UiCustomComponent component = new UiCustomComponent() {
            public void render(UiRenderList commands, UiNode node, UiRect bounds) {
                throw new RuntimeException("boom");
            }
        };
        UiRuntime runtime = new UiRuntime(Ui.screen("custom-render-throw")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().customComponent(component, 100, 30);
                }
            })
            .build());
        runtime.layout(new UiRect(0, 0, 120, 50));

        UiRenderList commands = UiRenderTraversal.render(runtime, UiTheme.pandalandDefault());

        assertTrue(commands.size() >= 0);
    }

    private static boolean contains(UiRenderList commands, UiRenderCommand.Type type) {
        for (UiRenderCommand command : commands.commands()) {
            if (command.type() == type) {
                return true;
            }
        }
        return false;
    }

    private static UiRuntime numericRuntime(final UiState<String> value) {
        return new UiRuntime(Ui.screen("numeric")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().numericInput(value, "Count", 100, 22, 16);
                }
            })
            .build());
    }

    private static void type(UiRuntime runtime, String text) {
        for (int i = 0; i < text.length(); i++) {
            runtime.events().keyTyped(text.charAt(i), 0);
        }
    }

    private static boolean containsText(UiRenderList commands, String text) {
        for (UiRenderCommand command : commands.commands()) {
            if ((command.type() == UiRenderCommand.Type.TEXT || command.type() == UiRenderCommand.Type.TEXT_WRAP)
                    && text.equals(command.text())) {
                return true;
            }
        }
        return false;
    }
}
