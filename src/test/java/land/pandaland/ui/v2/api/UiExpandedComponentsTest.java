package land.pandaland.ui.v2.api;

import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.data.UiListItem;
import land.pandaland.ui.v2.data.UiOption;
import land.pandaland.ui.v2.data.UiPage;
import land.pandaland.ui.v2.data.UiRichTextSpan;
import land.pandaland.ui.v2.data.UiTableColumn;
import land.pandaland.ui.v2.data.UiTableRow;
import land.pandaland.ui.v2.data.UiTooltipAttachment;
import land.pandaland.ui.v2.data.UiTreeItem;
import land.pandaland.ui.v2.state.UiState;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public final class UiExpandedComponentsTest {
    @Test
    public void tableCreatesTableNodeWithColumnsAndRows() {
        final List<UiTableColumn> columns = Arrays.asList(new UiTableColumn("name", "Name", 90));
        final List<UiTableRow> rows = Arrays.asList(new UiTableRow("row-1", cells("name", "Panda")));

        UiNode table = firstChild(Ui.screen("table")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.table(columns, rows, 220, 80);
                }
            })
            .build());

        assertEquals(UiNode.Type.TABLE, table.type());
        assertEquals(columns, table.columns());
        assertEquals(rows, table.rows());
        assertEquals(220, table.layoutStyle().preferredSize().width);
        assertEquals(80, table.layoutStyle().preferredSize().height);
    }

    @Test
    public void dataGridCreatesDataGridNodeWithSelectionMetadata() {
        final List<UiTableColumn> columns = Arrays.asList(new UiTableColumn("name", "Name", 90, true));
        final List<UiTableRow> rows = Arrays.asList(new UiTableRow("row-1", cells("name", "Panda"), false));
        final List<String> selectedIds = Arrays.asList("row-1");

        UiNode dataGrid = firstChild(Ui.screen("grid")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.dataGrid(columns, rows, selectedIds, 240, 100);
                }
            })
            .build());

        assertEquals(UiNode.Type.DATA_GRID, dataGrid.type());
        assertEquals(columns, dataGrid.columns());
        assertEquals(rows, dataGrid.rows());
        assertEquals(selectedIds, dataGrid.selectedIds());
        assertTrue(dataGrid.multiSelect());
    }

    @Test
    public void treeCreatesTreeNodeWithTreeItemsAndSelection() {
        final List<UiTreeItem> treeItems = Arrays.asList(new UiTreeItem("root", "Root",
            Arrays.asList(new UiTreeItem("child", "Child"))));

        UiNode tree = firstChild(Ui.screen("tree")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.tree(treeItems, Arrays.asList("child"), 180, 120);
                }
            })
            .build());

        assertEquals(UiNode.Type.TREE, tree.type());
        assertEquals(treeItems, tree.treeItems());
        assertEquals(Arrays.asList("child"), tree.selectedIds());
    }

    @Test
    public void virtualListCreatesListNodeWithItemsAndVirtualizedFlag() {
        final List<UiListItem> items = Arrays.asList(
            new UiListItem("one", "One"),
            new UiListItem("two", "Two", true));

        UiNode list = firstChild(Ui.screen("virtual-list")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.virtualList(items, Arrays.asList("one"), 160, 18);
                }
            })
            .build());

        assertEquals(UiNode.Type.LIST, list.type());
        assertEquals(items, list.items());
        assertEquals(Arrays.asList("one"), list.selectedIds());
        assertTrue(list.virtualized());
    }

    @Test
    public void richTextCreatesRichTextNodeWithSpans() {
        final List<UiRichTextSpan> spans = Arrays.asList(new UiRichTextSpan("Hello", 0xFFFFFF, true, false));

        UiNode richText = firstChild(Ui.screen("rich-text")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.richText(spans, 200, 40);
                }
            })
            .build());

        assertEquals(UiNode.Type.RICH_TEXT, richText.type());
        assertEquals(spans, richText.spans());
    }

    @Test
    public void searchableSelectCreatesSelectNodeWithSearchableOptions() {
        final UiState<String> value = UiState.of("hard");
        final List<UiOption> options = Arrays.asList(
            new UiOption("normal", "Normal"),
            new UiOption("hard", "Hard", true));

        UiNode select = firstChild(Ui.screen("searchable-select")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.searchableSelect("Difficulty", value, options, 180, 22);
                }
            })
            .build());

        assertEquals(UiNode.Type.SELECT, select.type());
        assertEquals("Difficulty", select.text());
        assertSame(value, select.valueState());
        assertTrue(select.searchable());
        assertFalse(select.multiSelect());
        assertEquals("hard", select.items().get(1).id());
        assertTrue(select.items().get(1).disabled());
    }

    @Test
    public void multiSelectCreatesSelectNodeWithSelectedIds() {
        final List<UiOption> options = Arrays.asList(
            new UiOption("a", "A"),
            new UiOption("b", "B"));

        UiNode select = firstChild(Ui.screen("multi-select")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.multiSelect("Tags", options, Arrays.asList("a", "b"), 180, 44);
                }
            })
            .build());

        assertEquals(UiNode.Type.SELECT, select.type());
        assertTrue(select.multiSelect());
        assertEquals(Arrays.asList("a", "b"), select.selectedIds());
        assertEquals("Tags", select.text());
    }

    @Test
    public void colorPickerCreatesFocusableColorPickerNode() {
        final UiState<Integer> color = UiState.of(Integer.valueOf(0x336699));

        UiNode picker = firstChild(Ui.screen("color")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.colorPicker("Accent", color, 120, 22);
                }
            })
            .build());

        assertEquals(UiNode.Type.COLOR_PICKER, picker.type());
        assertEquals("Accent", picker.text());
        assertSame(color, picker.valueState());
        assertTrue(picker.focusable());
    }

    @Test
    public void keybindInputCreatesFocusableKeybindNode() {
        final UiState<String> keybind = UiState.of("KEY_R");

        UiNode input = firstChild(Ui.screen("keybind")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.keybindInput("Reload", keybind, 140, 22);
                }
            })
            .build());

        assertEquals(UiNode.Type.KEYBIND_INPUT, input.type());
        assertEquals("Reload", input.text());
        assertSame(keybind, input.valueState());
        assertTrue(input.focusable());
    }

    @Test
    public void typedTabsCreateTabNodeWithOptionMetadata() {
        final List<UiOption> tabs = Arrays.asList(
            new UiOption("main", "Main"),
            new UiOption("advanced", "Advanced", true));
        final UiState<String> selected = UiState.of("main");

        UiNode tabNode = firstChild(Ui.screen("tabs")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.tabs(tabs, selected, 160, 20);
                }
            })
            .build());

        assertEquals(UiNode.Type.TABS, tabNode.type());
        assertEquals("main", tabNode.items().get(0).id());
        assertEquals("Advanced", tabNode.items().get(1).label());
        assertTrue(tabNode.items().get(1).disabled());
        assertEquals("Advanced", tabNode.children().get(1).text());
        assertFalse(tabNode.children().get(1).enabled());
        assertSame(selected, tabNode.valueState());
    }

    @Test
    public void tooltipAttachmentMetadataIsStoredOnTooltipNode() {
        final UiTooltipAttachment attachment = UiTooltipAttachment.anchor("settings", 4, 6);

        UiNode tooltip = firstChild(Ui.screen("tooltip")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.tooltip("Configure", 90, 18, attachment);
                }
            })
            .build());

        assertEquals(UiNode.Type.TOOLTIP, tooltip.type());
        assertSame(attachment, tooltip.tooltipAttachment());
        assertEquals("settings", tooltip.tooltipAttachment().targetId());
        assertEquals(4, tooltip.tooltipAttachment().offsetX());
        assertEquals(6, tooltip.tooltipAttachment().offsetY());
    }

    @Test
    public void pageStackCreatesPageStackNodeWithPagesAndSelection() {
        final List<UiPage> pages = Arrays.asList(
            new UiPage("general", "General"),
            new UiPage("advanced", "Advanced", true));

        UiNode pageStack = firstChild(Ui.screen("pages")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.pageStack(pages, "advanced", 260, 180);
                }
            })
            .build());

        assertEquals(UiNode.Type.PAGE_STACK, pageStack.type());
        assertEquals(pages, pageStack.pages());
        assertEquals(Collections.singletonList("advanced"), pageStack.selectedIds());
    }

    @Test
    public void customComponentStoresAndExposesMetadata() {
        Object customComponent = new Object();

        UiNode node = new UiNode(UiNode.Type.PANEL).customComponent(customComponent);

        assertSame(customComponent, node.customComponent());
        assertTrue(node.invalid());
    }

    @Test
    public void taskOneTypesDoNotChangeExistingEnumOrdinals() {
        assertEquals(14, UiNode.Type.SCROLL_CONTAINER.ordinal());
        assertEquals(15, UiNode.Type.TABS.ordinal());
        assertEquals(16, UiNode.Type.TOOLTIP.ordinal());
        assertEquals(17, UiNode.Type.CONTEXT_MENU.ordinal());
        assertEquals(18, UiNode.Type.FORM.ordinal());
        assertTrue(UiNode.Type.DATA_GRID.ordinal() > UiNode.Type.FORM.ordinal());
    }

    @Test
    public void metadataSettersMakeDefensiveCopies() {
        List<UiTableColumn> columns = new java.util.ArrayList<UiTableColumn>();
        columns.add(new UiTableColumn("name", "Name", 90));
        List<String> selectedIds = new java.util.ArrayList<String>();
        selectedIds.add("row-1");

        UiNode node = new UiNode(UiNode.Type.TABLE)
            .columns(columns)
            .selectedIds(selectedIds);
        columns.add(new UiTableColumn("age", "Age", 40));
        selectedIds.add("row-2");

        assertEquals(1, node.columns().size());
        assertEquals("name", node.columns().get(0).id());
        assertEquals(Collections.singletonList("row-1"), node.selectedIds());
    }

    @Test
    public void metadataGettersAreUnmodifiable() {
        UiNode node = new UiNode(UiNode.Type.TABLE)
            .columns(Arrays.asList(new UiTableColumn("name", "Name", 90)))
            .selectedIds(Collections.singletonList("row-1"));

        assertUnsupported(new Runnable() {
            public void run() {
                node.columns().add(new UiTableColumn("age", "Age", 40));
            }
        });
        assertUnsupported(new Runnable() {
            public void run() {
                node.selectedIds().add("row-2");
            }
        });
    }

    private static UiNode firstChild(UiScreen screen) {
        return screen.root().children().get(0);
    }

    private static Map<String, String> cells(String key, String value) {
        Map<String, String> cells = new LinkedHashMap<String, String>();
        cells.put(key, value);
        return cells;
    }

    private static void assertUnsupported(Runnable action) {
        try {
            action.run();
        } catch (UnsupportedOperationException expected) {
            return;
        }
        throw new AssertionError("Expected UnsupportedOperationException");
    }
}
