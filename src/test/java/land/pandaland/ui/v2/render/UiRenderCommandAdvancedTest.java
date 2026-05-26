package land.pandaland.ui.v2.render;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.data.UiRichTextSpan;
import land.pandaland.ui.v2.data.UiTableColumn;
import land.pandaland.ui.v2.data.UiTableRow;
import land.pandaland.ui.v2.data.UiTreeItem;
import land.pandaland.ui.v2.layout.UiLayoutStyle;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.state.UiState;
import land.pandaland.ui.v2.style.UiColor;
import land.pandaland.ui.v2.style.UiTheme;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public final class UiRenderCommandAdvancedTest {
    @Test
    public void advancedFactoriesExposeMetadata() {
        UiRect rect = new UiRect(1, 2, 30, 40);
        UiColor first = new UiColor(0xFF010203);
        UiColor second = new UiColor(0xFF102030);
        UiCustomDraw customDraw = new UiCustomDraw() {
            public void draw(UiRenderList commands, UiRect bounds) {
                commands.add(UiRenderCommand.text("custom", bounds, first));
            }
        };

        UiRenderCommand border = UiRenderCommand.border(rect, 3, first, 2);
        assertEquals(UiRenderCommand.Type.BORDER, border.type());
        assertSame(rect, border.rect());
        assertSame(first, border.color());
        assertEquals(3, border.radius());
        assertEquals(2, border.thickness());

        UiRenderCommand gradient = UiRenderCommand.gradientRect(rect, 4, first, second, false);
        assertEquals(UiRenderCommand.Type.GRADIENT_RECT, gradient.type());
        assertSame(first, gradient.color());
        assertSame(second, gradient.endColor());
        assertEquals(4, gradient.radius());
        assertEquals(false, gradient.vertical());

        UiRenderCommand shadow = UiRenderCommand.shadow(rect, 5, second, 6, 7, 8);
        assertEquals(UiRenderCommand.Type.SHADOW, shadow.type());
        assertEquals(5, shadow.radius());
        assertEquals(6, shadow.blur());
        assertEquals(7, shadow.offsetX());
        assertEquals(8, shadow.offsetY());

        UiRenderCommand line = UiRenderCommand.line(1, 2, 11, 12, first, 3);
        assertEquals(UiRenderCommand.Type.LINE, line.type());
        assertEquals(1, line.x1());
        assertEquals(2, line.y1());
        assertEquals(11, line.x2());
        assertEquals(12, line.y2());
        assertEquals(3, line.thickness());

        UiRenderCommand wrapped = UiRenderCommand.textWrap("wrapped text", rect, second, 9);
        assertEquals(UiRenderCommand.Type.TEXT_WRAP, wrapped.type());
        assertEquals("wrapped text", wrapped.text());
        assertEquals(9, wrapped.lineHeight());

        UiRenderCommand custom = UiRenderCommand.custom(rect, customDraw);
        assertEquals(UiRenderCommand.Type.CUSTOM, custom.type());
        assertSame(customDraw, custom.customDraw());

        UiRenderCommand layerStart = UiRenderCommand.layerStart("popup", 100);
        assertEquals(UiRenderCommand.Type.LAYER_START, layerStart.type());
        assertEquals("popup", layerStart.layerName());
        assertEquals(100, layerStart.zIndex());

        UiRenderCommand layerEnd = UiRenderCommand.layerEnd("popup");
        assertEquals(UiRenderCommand.Type.LAYER_END, layerEnd.type());
        assertEquals("popup", layerEnd.layerName());

        UiRenderCommand nanProgress = UiRenderCommand.progress(rect, first, Float.NaN);
        assertEquals(0.0F, nanProgress.amount(), 0.0F);
    }

    @Test
    public void traversalEmitsRicherCommandsForExpandedControls() {
        UiScreen screen = Ui.screen("advanced-render")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().gap(2)
                        .table(
                            Arrays.asList(new UiTableColumn("name", "Name", 60), new UiTableColumn("role", "Role", 60)),
                            Arrays.asList(row("one", "Panda", "Admin"), row("two", "Bamboo", "Builder")),
                            140,
                            64
                        )
                        .tree(
                            Arrays.asList(new UiTreeItem("root", "Root", Arrays.asList(new UiTreeItem("child", "Child")))),
                            Collections.singletonList("child"),
                            140,
                            42
                        )
                        .richText(Arrays.asList(new UiRichTextSpan("Long rendered rich text span", 0x24F0D3, true, false)), 140, 28);
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 160, 160));

        UiRenderList commands = UiRenderTraversal.render(runtime, UiTheme.pandalandDefault());

        assertTrue(contains(commands, UiRenderCommand.Type.BORDER));
        assertTrue(contains(commands, UiRenderCommand.Type.GRADIENT_RECT));
        assertTrue(contains(commands, UiRenderCommand.Type.LINE));
        assertTrue(contains(commands, UiRenderCommand.Type.TEXT_WRAP));
    }

    @Test
    public void traversalEmitsLayerShadowAndCustomCommands() {
        final UiCustomDraw customDraw = new UiCustomDraw() {
            public void draw(UiRenderList commands, UiRect bounds) {
                commands.add(UiRenderCommand.text("custom", bounds, new UiColor(0xFFFFFFFF)));
            }
        };
        UiScreen screen = Ui.screen("custom-render")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column();
                }
            })
            .build();
        screen.root().add(new UiNode(UiNode.Type.PANEL)
                .layoutStyle(UiLayoutStyle.leaf().size(40, 20))
                .customComponent(customDraw));
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 120, 80));
        runtime.showModal(Ui.modal("Modal"));

        UiRenderList commands = UiRenderTraversal.render(runtime, UiTheme.pandalandDefault());

        assertTrue(contains(commands, UiRenderCommand.Type.CUSTOM));
        assertTrue(containsText(commands, "custom"));
        assertTrue(contains(commands, UiRenderCommand.Type.SHADOW));
        assertTrue(contains(commands, UiRenderCommand.Type.LAYER_START));
        assertTrue(contains(commands, UiRenderCommand.Type.LAYER_END));
    }

    @Test
    public void childBackedListAndSelectOptionsRenderOnce() {
        UiScreen screen = Ui.screen("child-backed-controls")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column()
                        .list(new String[] {"Alpha", "Beta"}, UiState.of(0), 120, 16)
                        .select("Color", UiState.of(""), new String[] {"Red", "Blue"}, 120, 20);
                }
            })
            .build();
        screen.root().children().get(1).open(true);
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 140, 80));

        UiRenderList commands = UiRenderTraversal.render(runtime, UiTheme.pandalandDefault());

        assertEquals(1, countTextLike(commands, "Alpha"));
        assertEquals(1, countTextLike(commands, "Red"));
    }

    private static UiTableRow row(String id, String name, String role) {
        Map<String, String> cells = new LinkedHashMap<String, String>();
        cells.put("name", name);
        cells.put("role", role);
        return new UiTableRow(id, cells);
    }

    private static boolean contains(UiRenderList commands, UiRenderCommand.Type type) {
        for (UiRenderCommand command : commands.commands()) {
            if (command.type() == type) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsText(UiRenderList commands, String text) {
        for (UiRenderCommand command : commands.commands()) {
            if (command.type() == UiRenderCommand.Type.TEXT && text.equals(command.text())) {
                return true;
            }
        }
        return false;
    }

    private static int countTextLike(UiRenderList commands, String text) {
        int count = 0;
        for (UiRenderCommand command : commands.commands()) {
            if ((command.type() == UiRenderCommand.Type.TEXT || command.type() == UiRenderCommand.Type.TEXT_WRAP)
                    && text.equals(command.text())) {
                count++;
            }
        }
        return count;
    }
}
