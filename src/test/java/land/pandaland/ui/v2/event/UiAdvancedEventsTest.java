package land.pandaland.ui.v2.event;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.data.UiListItem;
import land.pandaland.ui.v2.data.UiModalOptions;
import land.pandaland.ui.v2.data.UiOption;
import land.pandaland.ui.v2.data.UiTableColumn;
import land.pandaland.ui.v2.data.UiTableRow;
import land.pandaland.ui.v2.data.UiTreeItem;
import land.pandaland.ui.v2.layout.UiLayoutStyle;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.state.UiState;
import org.junit.Test;
import org.lwjgl.input.Keyboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public final class UiAdvancedEventsTest {
    @Test
    public void listSelectionReportsIdAndIndexAndSkipsDisabledItems() {
        final AtomicReference<String> selectedId = new AtomicReference<String>();
        final AtomicInteger selectedIndex = new AtomicInteger(-1);

        UiScreen screen = Ui.screen("list-selection")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.virtualList(
                        Arrays.asList(
                            new UiListItem("alpha", "Alpha"),
                            new UiListItem("beta", "Beta", true),
                            new UiListItem("gamma", "Gamma")
                        ),
                        Collections.singletonList("alpha"),
                        120,
                        12,
                        new UiSelectionHandler() {
                            public void onSelect(String id, int index) {
                                selectedId.set(id);
                                selectedIndex.set(index);
                            }
                        }
                    );
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 120, 36));

        runtime.events().pointerDown(6, 18, 0);
        runtime.events().pointerUp(6, 18, 0);

        UiNode list = screen.root().children().get(0);
        assertEquals(Collections.singletonList("alpha"), list.selectedIds());
        assertEquals(null, selectedId.get());

        runtime.events().pointerDown(6, 30, 0);
        runtime.events().pointerUp(6, 30, 0);

        assertEquals(Collections.singletonList("gamma"), list.selectedIds());
        assertEquals("gamma", selectedId.get());
        assertEquals(2, selectedIndex.get());
    }

    @Test
    public void plainRuntimeShortcutDoesNotStealFocusedTextInputCharacter() {
        final AtomicInteger shortcuts = new AtomicInteger(0);
        final UiState<String> value = UiState.of("");
        UiScreen screen = Ui.screen("shortcut")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.textInput(value, "Name", 100, 20);
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.registerShortcut(UiShortcut.key(Keyboard.KEY_R, new Runnable() {
            public void run() {
                shortcuts.incrementAndGet();
            }
        }));
        runtime.layout(new UiRect(0, 0, 120, 30));
        runtime.events().pointerDown(6, 6, 0);

        assertTrue(runtime.events().keyTyped('r', Keyboard.KEY_R));

        assertEquals(0, shortcuts.get());
        assertEquals("r", value.get());
    }

    @Test
    public void focusedKeybindInputCapturesKeyBeforeRuntimeShortcut() {
        final AtomicInteger shortcuts = new AtomicInteger(0);
        final AtomicInteger changes = new AtomicInteger(0);
        final UiState<String> value = UiState.of("");
        UiScreen screen = Ui.screen("keybind-shortcut")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.keybindInput("Reload", value, 120, 20, new Runnable() {
                        public void run() {
                            changes.incrementAndGet();
                        }
                    });
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.registerShortcut(UiShortcut.key(Keyboard.KEY_R, new Runnable() {
            public void run() {
                shortcuts.incrementAndGet();
            }
        }));
        runtime.layout(new UiRect(0, 0, 140, 40));
        runtime.events().pointerDown(6, 6, 0);

        assertTrue(runtime.events().keyTyped('r', Keyboard.KEY_R));

        assertEquals(0, shortcuts.get());
        assertEquals("KEY_R", value.get());
        assertEquals(1, changes.get());
    }

    @Test
    public void controlShortcutMatchesAndRunsAction() {
        final AtomicInteger shortcuts = new AtomicInteger(0);
        UiShortcut shortcut = new UiShortcut(Keyboard.KEY_R, true, false, false, new Runnable() {
            public void run() {
                shortcuts.incrementAndGet();
            }
        });

        if (shortcut.matches(Keyboard.KEY_R, true, false, false)) {
            shortcut.run();
        }

        assertEquals(1, shortcuts.get());
    }

    @Test
    public void rootShortcutCanBeRegisteredOnRootNode() {
        final AtomicInteger shortcuts = new AtomicInteger(0);
        UiScreen screen = Ui.screen("root-shortcut")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.label("Ready");
                }
            })
            .build();
        screen.root().registerShortcut(UiShortcut.key(Keyboard.KEY_G, new Runnable() {
            public void run() {
                shortcuts.incrementAndGet();
            }
        }));
        UiRuntime runtime = new UiRuntime(screen);

        assertTrue(runtime.events().keyTyped('\0', Keyboard.KEY_G));

        assertEquals(1, shortcuts.get());
    }

    @Test
    public void rootShortcutDoesNotFireBehindActiveModal() {
        final AtomicInteger shortcuts = new AtomicInteger(0);
        UiScreen screen = Ui.screen("root-shortcut-modal")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.label("Ready");
                }
            })
            .build();
        screen.root().registerShortcut(UiShortcut.key(Keyboard.KEY_F5, new Runnable() {
            public void run() {
                shortcuts.incrementAndGet();
            }
        }));
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 160, 100));
        runtime.showModal(Ui.modal("Modal"));

        runtime.events().keyTyped('\0', Keyboard.KEY_F5);

        assertEquals(0, shortcuts.get());
    }

    @Test
    public void runtimeShortcutStillFiresWithActiveModal() {
        final AtomicInteger shortcuts = new AtomicInteger(0);
        UiScreen screen = Ui.screen("runtime-shortcut-modal")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.label("Ready");
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.registerShortcut(UiShortcut.key(Keyboard.KEY_F5, new Runnable() {
            public void run() {
                shortcuts.incrementAndGet();
            }
        }));
        runtime.layout(new UiRect(0, 0, 160, 100));
        runtime.showModal(Ui.modal("Modal"));

        assertTrue(runtime.events().keyTyped('\0', Keyboard.KEY_F5));

        assertEquals(1, shortcuts.get());
    }

    @Test
    public void escapeClosesTopModalWhenCloseOnEscapeIsEnabled() {
        UiScreen screen = Ui.screen("modal-escape-enabled")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.label("Ready");
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 160, 100));
        runtime.showModal(Ui.modal(new UiModalOptions("First", 100, 60, true)));
        runtime.showModal(Ui.modal(new UiModalOptions("Second", 100, 60, true)));

        assertEquals(2, runtime.modalCount());
        assertTrue(runtime.events().keyTyped('\0', Keyboard.KEY_ESCAPE));

        assertEquals(1, runtime.modalCount());
    }

    @Test
    public void escapeIsConsumedWhenCloseOnEscapeIsDisabled() {
        UiScreen screen = Ui.screen("modal-escape-disabled")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.label("Ready");
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 160, 100));
        runtime.showModal(Ui.modal(new UiModalOptions("Blocking", 100, 60, false)));

        assertEquals(1, runtime.modalCount());
        assertTrue(runtime.events().keyTyped('\0', Keyboard.KEY_ESCAPE));

        assertEquals(1, runtime.modalCount());
    }

    @Test
    public void contextMenuStoresOpenPositionAndOutsideClickClosesIt() {
        UiScreen screen = Ui.screen("context")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.absolute()
                        .button("Base", new Runnable() {
                            public void run() {
                            }
                        }, 80, 20)
                        .contextMenu(80, 40, new Ui.PanelBuilderConsumer() {
                            public void build(Ui.NodeBuilder menu) {
                                menu.button("Action", new Runnable() {
                                    public void run() {
                                    }
                                }, 70, 16);
                            }
                        });
                }
            })
            .build();
        UiNode menu = screen.root().children().get(1);
        menu.openAt(30, 18);
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 160, 120));

        assertEquals(30, menu.openX());
        assertEquals(18, menu.openY());

        runtime.events().pointerDown(140, 100, 0);

        assertEquals(false, menu.open());
    }

    @Test
    public void activeModalKeepsFocusWhenPointerClicksOutsideIt() {
        UiScreen screen = Ui.screen("modal-focus")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.button("Root", new Runnable() {
                        public void run() {
                        }
                    });
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        UiNode modal = Ui.modal("Confirm")
            .layoutStyle(UiLayoutStyle.column().size(120, 60).padding(4).gap(4));
        modal.add(new UiNode(UiNode.Type.TEXT_INPUT)
            .layoutStyle(UiLayoutStyle.leaf().size(100, 20))
            .valueState(UiState.of(""))
            .focusable(true));
        runtime.layout(new UiRect(0, 0, 200, 120));
        runtime.showModal(modal);

        UiNode input = modal.children().get(0);
        runtime.focus().focus(input);
        runtime.events().pointerDown(2, 2, 0);

        assertSame(input, runtime.focus().focused());
    }

    @Test
    public void activeModalPreventsKeyboardEditingOutsideFocusedInput() {
        final UiState<String> rootValue = UiState.of("");
        final UiState<String> modalValue = UiState.of("");
        UiScreen screen = Ui.screen("modal-keyboard")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.textInput(rootValue, "Root", 100, 20);
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 200, 120));
        UiNode rootInput = screen.root().children().get(0);
        UiNode modal = Ui.modal("Modal")
            .layoutStyle(UiLayoutStyle.column().size(120, 60).padding(4).gap(4));
        UiNode modalInput = new UiNode(UiNode.Type.TEXT_INPUT)
            .layoutStyle(UiLayoutStyle.leaf().size(100, 20))
            .valueState(modalValue)
            .focusable(true);
        modal.add(modalInput);
        runtime.focus().focus(rootInput);
        runtime.showModal(modal);

        runtime.events().keyTyped('x', Keyboard.KEY_X);

        assertEquals("", rootValue.get());
        assertSame(modalInput, runtime.focus().focused());
        assertEquals("x", modalValue.get());
    }

    @Test
    public void selectionHandlerOverloadsAreAvailableForAdvancedSelectionApis() {
        final AtomicInteger callbackCount = new AtomicInteger(0);
        final UiSelectionHandler handler = new UiSelectionHandler() {
            public void onSelect(String id, int index) {
                callbackCount.incrementAndGet();
            }
        };

        UiScreen screen = Ui.screen("selection-overloads")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.dataGrid(
                            Arrays.asList(new UiTableColumn("name", "Name", 80)),
                            Arrays.asList(new UiTableRow("row-1", cells("name", "One"))),
                            Collections.<String>emptyList(),
                            120,
                            50,
                            handler)
                        .tree(
                            Arrays.asList(new UiTreeItem("tree-1", "Tree One")),
                            Collections.<String>emptyList(),
                            120,
                            40,
                            handler)
                        .multiSelect(
                            "Tags",
                            Arrays.asList(new UiOption("a", "A")),
                            Collections.<String>emptyList(),
                            120,
                            20,
                            handler)
                        .tabs(new String[] {"One", "Two"}, UiState.of(Integer.valueOf(0)), 120, 20, handler);
                }
            })
            .build();

        assertSame(handler, screen.root().children().get(0).selectionHandler());
        assertSame(handler, screen.root().children().get(1).selectionHandler());
        assertSame(handler, screen.root().children().get(2).selectionHandler());
        assertSame(handler, screen.root().children().get(3).selectionHandler());
        assertEquals(0, callbackCount.get());
    }

    @Test
    public void tableSelectionReportsRowIdAndIndexAndSkipsDisabledRows() {
        final AtomicReference<String> selectedId = new AtomicReference<String>();
        final AtomicInteger selectedIndex = new AtomicInteger(-1);
        UiScreen screen = Ui.screen("table-selection")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.table(
                        Arrays.asList(new UiTableColumn("name", "Name", 80)),
                        Arrays.asList(
                            new UiTableRow("row-1", cells("name", "One")),
                            new UiTableRow("row-2", cells("name", "Two"), true),
                            new UiTableRow("row-3", cells("name", "Three"))
                        ),
                        120,
                        80,
                        new UiSelectionHandler() {
                            public void onSelect(String id, int index) {
                                selectedId.set(id);
                                selectedIndex.set(index);
                            }
                        }
                    );
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 140, 100));
        UiNode table = screen.root().children().get(0);

        runtime.events().pointerDown(8, 48, 0);
        runtime.events().pointerUp(8, 48, 0);

        assertEquals(Collections.<String>emptyList(), table.selectedIds());
        assertEquals(null, selectedId.get());

        runtime.events().pointerDown(8, 66, 0);
        runtime.events().pointerUp(8, 66, 0);

        assertEquals(Collections.singletonList("row-3"), table.selectedIds());
        assertEquals("row-3", selectedId.get());
        assertEquals(2, selectedIndex.get());
    }

    @Test
    public void dataGridSelectionOverloadFiresCallback() {
        final AtomicReference<String> selectedId = new AtomicReference<String>();
        final AtomicInteger selectedIndex = new AtomicInteger(-1);
        UiScreen screen = Ui.screen("data-grid-selection")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.dataGrid(
                        Arrays.asList(new UiTableColumn("name", "Name", 80)),
                        Arrays.asList(
                            new UiTableRow("row-1", cells("name", "One")),
                            new UiTableRow("row-2", cells("name", "Two"))
                        ),
                        Collections.<String>emptyList(),
                        120,
                        62,
                        new UiSelectionHandler() {
                            public void onSelect(String id, int index) {
                                selectedId.set(id);
                                selectedIndex.set(index);
                            }
                        }
                    );
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 140, 80));

        runtime.events().pointerDown(8, 48, 0);
        runtime.events().pointerUp(8, 48, 0);

        assertEquals("row-2", selectedId.get());
        assertEquals(1, selectedIndex.get());
    }

    @Test
    public void tabsSelectionOverloadFiresCallback() {
        final AtomicReference<String> selectedId = new AtomicReference<String>();
        final AtomicInteger selectedIndex = new AtomicInteger(-1);
        UiScreen screen = Ui.screen("tabs-selection")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.tabs(new String[] {"One", "Two"}, UiState.of(Integer.valueOf(0)), 120, 20, new UiSelectionHandler() {
                        public void onSelect(String id, int index) {
                            selectedId.set(id);
                            selectedIndex.set(index);
                        }
                    });
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 140, 40));

        runtime.events().pointerDown(80, 8, 0);
        runtime.events().pointerUp(80, 8, 0);

        assertEquals("Two", selectedId.get());
        assertEquals(1, selectedIndex.get());
    }

    @Test
    public void typedTabsReportStableIdAndRejectDisabledTabs() {
        final AtomicReference<String> selectedId = new AtomicReference<String>();
        final AtomicInteger selectedIndex = new AtomicInteger(-1);
        final UiState<String> selected = UiState.of("overview");
        UiScreen screen = Ui.screen("typed-tabs-selection")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.tabs(
                        Arrays.asList(
                            new UiOption("overview", "Overview"),
                            new UiOption("logs", "Logs", true),
                            new UiOption("settings", "Settings")
                        ),
                        selected,
                        180,
                        20,
                        new UiSelectionHandler() {
                            public void onSelect(String id, int index) {
                                selectedId.set(id);
                                selectedIndex.set(index);
                            }
                        });
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 200, 40));
        UiNode tabs = screen.root().children().get(0);

        runtime.events().pointerDown(70, 8, 0);
        runtime.events().pointerUp(70, 8, 0);

        assertEquals("overview", selected.get());
        assertEquals(Collections.<String>emptyList(), tabs.selectedIds());
        assertEquals(null, selectedId.get());

        runtime.events().pointerDown(130, 8, 0);
        runtime.events().pointerUp(130, 8, 0);

        assertEquals("settings", selected.get());
        assertEquals(Collections.singletonList("settings"), tabs.selectedIds());
        assertEquals("settings", selectedId.get());
        assertEquals(2, selectedIndex.get());
    }

    private static Map<String, String> cells(String key, String value) {
        Map<String, String> cells = new LinkedHashMap<String, String>();
        cells.put(key, value);
        return cells;
    }
}
