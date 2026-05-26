package land.pandaland.ui.v2.layout;

import java.util.Arrays;

import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiScreen;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class UiLayoutEngineAdvancedTest {
    @Test
    public void rowWrapMovesOverflowingChildrenToNextLine() {
        UiLayoutStyle parent = UiLayoutStyle.row().wrap(true).gap(2);
        UiLayoutStyle first = UiLayoutStyle.leaf().size(30, 10);
        UiLayoutStyle second = UiLayoutStyle.leaf().size(30, 12);
        UiLayoutStyle third = UiLayoutStyle.leaf().size(30, 8);

        UiLayoutEngine.Result result = UiLayoutEngine.layout(parent, new UiRect(0, 0, 70, 40), Arrays.asList(first, second, third));

        assertEquals(new UiRect(0, 0, 30, 10), result.children().get(0));
        assertEquals(new UiRect(32, 0, 30, 12), result.children().get(1));
        assertEquals(new UiRect(0, 14, 30, 8), result.children().get(2));
    }

    @Test
    public void gridPlacesChildrenInFixedColumnsAndRows() {
        UiLayoutStyle parent = UiLayoutStyle.grid(3, 10).gap(2);

        UiLayoutEngine.Result result = UiLayoutEngine.layout(parent, new UiRect(0, 0, 100, 40), Arrays.asList(
            UiLayoutStyle.leaf(),
            UiLayoutStyle.leaf(),
            UiLayoutStyle.leaf(),
            UiLayoutStyle.leaf()
        ));

        assertEquals(new UiRect(0, 0, 32, 10), result.children().get(0));
        assertEquals(new UiRect(34, 0, 32, 10), result.children().get(1));
        assertEquals(new UiRect(68, 0, 32, 10), result.children().get(2));
        assertEquals(new UiRect(0, 12, 32, 10), result.children().get(3));
    }

    @Test
    public void absoluteLayoutUsesChildOffsets() {
        UiLayoutStyle parent = UiLayoutStyle.absolute().padding(4);
        UiLayoutStyle first = UiLayoutStyle.leaf().size(20, 10).offset(6, 8);
        UiLayoutStyle second = UiLayoutStyle.leaf().size(12, 14).x(25).y(3);

        UiLayoutEngine.Result result = UiLayoutEngine.layout(parent, new UiRect(10, 20, 80, 60), Arrays.asList(first, second));

        assertEquals(new UiRect(20, 32, 20, 10), result.children().get(0));
        assertEquals(new UiRect(39, 27, 12, 14), result.children().get(1));
    }

    @Test
    public void minAndMaxClampResolvedSizes() {
        UiLayoutStyle parent = UiLayoutStyle.row().gap(2);
        UiLayoutStyle minWidth = UiLayoutStyle.leaf().size(10, 4).minWidth(18).minHeight(9);
        UiLayoutStyle maxWidth = UiLayoutStyle.leaf().size(40, 30).maxWidth(16).maxHeight(12);

        UiLayoutEngine.Result result = UiLayoutEngine.layout(parent, new UiRect(0, 0, 80, 40), Arrays.asList(minWidth, maxWidth));

        assertEquals(new UiRect(0, 0, 18, 9), result.children().get(0));
        assertEquals(new UiRect(20, 0, 16, 12), result.children().get(1));
    }

    @Test
    public void rowAndColumnReportMinimumSizeEvenWhenParentIsSmaller() {
        UiLayoutEngine.Result rowResult = UiLayoutEngine.layout(UiLayoutStyle.row(), new UiRect(0, 0, 40, 20), Arrays.asList(
            UiLayoutStyle.leaf().size(10, 8).minWidth(70).minHeight(25)
        ));

        assertEquals(new UiRect(0, 0, 70, 25), rowResult.children().get(0));

        UiLayoutEngine.Result columnResult = UiLayoutEngine.layout(UiLayoutStyle.column(), new UiRect(0, 0, 40, 20), Arrays.asList(
            UiLayoutStyle.leaf().size(10, 8).minWidth(65).minHeight(30)
        ));

        assertEquals(new UiRect(0, 0, 65, 30), columnResult.children().get(0));
    }

    @Test
    public void gridReportsMinimumSizeEvenWhenCellIsSmaller() {
        UiLayoutStyle parent = UiLayoutStyle.grid(2, 12).gap(2);
        UiLayoutStyle child = UiLayoutStyle.leaf().minWidth(80).minHeight(25);

        UiLayoutEngine.Result result = UiLayoutEngine.layout(parent, new UiRect(0, 0, 50, 20), Arrays.asList(child));

        assertEquals(new UiRect(0, 0, 80, 25), result.children().get(0));
    }

    @Test
    public void rowAndColumnStillCapCrossAxisWhenNoMinimumRequestsOverflow() {
        UiLayoutEngine.Result columnResult = UiLayoutEngine.layout(UiLayoutStyle.column(), new UiRect(0, 0, 50, 50), Arrays.asList(
            UiLayoutStyle.leaf().size(200, 10)
        ));
        UiLayoutEngine.Result rowResult = UiLayoutEngine.layout(UiLayoutStyle.row(), new UiRect(0, 0, 50, 50), Arrays.asList(
            UiLayoutStyle.leaf().size(10, 200)
        ));

        assertEquals(new UiRect(0, 0, 50, 10), columnResult.children().get(0));
        assertEquals(new UiRect(0, 0, 10, 50), rowResult.children().get(0));
    }

    @Test
    public void rowAndColumnStillCapMainAxisRemainderWhenShrinkIsDisabledUnlessMinimumRequiresOverflow() {
        UiLayoutEngine.Result rowResult = UiLayoutEngine.layout(UiLayoutStyle.row(), new UiRect(0, 0, 50, 20), Arrays.asList(
            UiLayoutStyle.leaf().size(200, 10).shrink(0.0F)
        ));
        UiLayoutEngine.Result rowMinResult = UiLayoutEngine.layout(UiLayoutStyle.row(), new UiRect(0, 0, 50, 20), Arrays.asList(
            UiLayoutStyle.leaf().size(200, 10).shrink(0.0F).minWidth(75)
        ));
        UiLayoutEngine.Result columnResult = UiLayoutEngine.layout(UiLayoutStyle.column(), new UiRect(0, 0, 20, 50), Arrays.asList(
            UiLayoutStyle.leaf().size(10, 200).shrink(0.0F)
        ));
        UiLayoutEngine.Result columnMinResult = UiLayoutEngine.layout(UiLayoutStyle.column(), new UiRect(0, 0, 20, 50), Arrays.asList(
            UiLayoutStyle.leaf().size(10, 200).shrink(0.0F).minHeight(80)
        ));

        assertEquals(new UiRect(0, 0, 50, 10), rowResult.children().get(0));
        assertEquals(new UiRect(0, 0, 75, 10), rowMinResult.children().get(0));
        assertEquals(new UiRect(0, 0, 10, 50), columnResult.children().get(0));
        assertEquals(new UiRect(0, 0, 10, 80), columnMinResult.children().get(0));
    }

    @Test
    public void directionOrdinalsPreserveExistingValues() {
        assertEquals(0, UiLayoutStyle.Direction.LEAF.ordinal());
        assertEquals(1, UiLayoutStyle.Direction.COLUMN.ordinal());
        assertEquals(2, UiLayoutStyle.Direction.ROW.ordinal());
        assertEquals(3, UiLayoutStyle.Direction.OVERLAY.ordinal());
        assertEquals(4, UiLayoutStyle.Direction.SCROLL.ordinal());
    }

    @Test
    public void gridClampsInvalidConfigurationDefaults() {
        UiLayoutStyle grid = UiLayoutStyle.grid(0, -5);

        assertEquals(UiLayoutStyle.Direction.GRID, grid.direction());
        assertEquals(1, grid.gridColumns());
        assertEquals(0, grid.gridRowHeight());
    }

    @Test
    public void wrappingHandlesZeroWidthAndOversizedChildren() {
        UiLayoutEngine.Result zeroWidthResult = UiLayoutEngine.layout(UiLayoutStyle.row().wrap(true), new UiRect(0, 0, 0, 20), Arrays.asList(
            UiLayoutStyle.leaf().size(10, 5)
        ));
        UiLayoutEngine.Result oversizedResult = UiLayoutEngine.layout(UiLayoutStyle.row().wrap(true).gap(2), new UiRect(0, 0, 30, 30), Arrays.asList(
            UiLayoutStyle.leaf().size(45, 5),
            UiLayoutStyle.leaf().size(8, 6)
        ));

        assertEquals(new UiRect(0, 0, 0, 5), zeroWidthResult.children().get(0));
        assertEquals(new UiRect(0, 0, 30, 5), oversizedResult.children().get(0));
        assertEquals(new UiRect(0, 7, 8, 6), oversizedResult.children().get(1));
    }

    @Test
    public void compatibilityBaselineKeepsExistingRowAndColumnBehavior() {
        UiLayoutStyle row = UiLayoutStyle.row().gap(2);
        UiLayoutEngine.Result rowResult = UiLayoutEngine.layout(row, new UiRect(0, 0, 80, 20), Arrays.asList(
            UiLayoutStyle.leaf().size(20, 10).grow(1.0F),
            UiLayoutStyle.leaf().size(20, 10)
        ));

        assertEquals(new UiRect(0, 0, 58, 10), rowResult.children().get(0));
        assertEquals(new UiRect(60, 0, 20, 10), rowResult.children().get(1));

        UiLayoutStyle column = UiLayoutStyle.column().padding(4).gap(3);
        UiLayoutEngine.Result columnResult = UiLayoutEngine.layout(column, new UiRect(0, 0, 100, 80), Arrays.asList(
            UiLayoutStyle.leaf().size(20, 10),
            UiLayoutStyle.leaf().size(30, 12)
        ));

        assertEquals(new UiRect(4, 4, 20, 10), columnResult.children().get(0));
        assertEquals(new UiRect(4, 17, 30, 12), columnResult.children().get(1));
    }

    @Test
    public void publicBuilderExposesNewLayoutFluentMethods() {
        UiScreen screen = Ui.screen("advanced-layout")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.grid(2, 16)
                        .padding(3)
                        .minSize(50, 30)
                        .maxSize(200, 120)
                        .zIndex(5)
                        .panel(new Ui.PanelBuilderConsumer() {
                            public void build(Ui.NodeBuilder panel) {
                                panel.absolute().offset(7, 9);
                            }
                        });
                }
            })
            .build();

        UiLayoutStyle rootStyle = screen.root().layoutStyle();
        UiNode panel = screen.root().children().get(0);

        assertEquals(UiLayoutStyle.Direction.GRID, rootStyle.direction());
        assertEquals(2, rootStyle.gridColumns());
        assertEquals(16, rootStyle.gridRowHeight());
        assertEquals(50, rootStyle.minWidth());
        assertEquals(30, rootStyle.minHeight());
        assertEquals(200, rootStyle.maxWidth());
        assertEquals(120, rootStyle.maxHeight());
        assertEquals(5, rootStyle.zIndex());
        assertEquals(UiLayoutStyle.Direction.ABSOLUTE, panel.layoutStyle().direction());
        assertEquals(7, panel.layoutStyle().x());
        assertEquals(9, panel.layoutStyle().y());
    }
}
