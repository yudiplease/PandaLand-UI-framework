package land.pandaland.ui.v2.layout;

import java.util.Arrays;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class UiLayoutEngineTest {
    @Test
    public void columnAppliesPaddingGapAndChildSizes() {
        UiLayoutStyle parent = UiLayoutStyle.column().padding(4).gap(3);
        UiLayoutStyle first = UiLayoutStyle.leaf().size(20, 10);
        UiLayoutStyle second = UiLayoutStyle.leaf().size(30, 12);

        UiLayoutEngine.Result result = UiLayoutEngine.layout(parent, new UiRect(0, 0, 100, 80), Arrays.asList(first, second));

        assertEquals(new UiRect(4, 4, 20, 10), result.children().get(0));
        assertEquals(new UiRect(4, 17, 30, 12), result.children().get(1));
    }

    @Test
    public void rowClampsChildToAvailableWidth() {
        UiLayoutStyle parent = UiLayoutStyle.row().padding(2).gap(2);
        UiLayoutStyle child = UiLayoutStyle.leaf().size(200, 10);

        UiLayoutEngine.Result result = UiLayoutEngine.layout(parent, new UiRect(0, 0, 50, 20), Arrays.asList(child));

        assertEquals(new UiRect(2, 2, 46, 10), result.children().get(0));
    }

    @Test
    public void rowDistributesExtraSpaceToGrowChildren() {
        UiLayoutStyle parent = UiLayoutStyle.row().gap(2);
        UiLayoutStyle first = UiLayoutStyle.leaf().size(20, 10).grow(1.0F);
        UiLayoutStyle second = UiLayoutStyle.leaf().size(20, 10);

        UiLayoutEngine.Result result = UiLayoutEngine.layout(parent, new UiRect(0, 0, 80, 20), Arrays.asList(first, second));

        assertEquals(new UiRect(0, 0, 58, 10), result.children().get(0));
        assertEquals(new UiRect(60, 0, 20, 10), result.children().get(1));
    }

    @Test
    public void columnAlignsChildToCenter() {
        UiLayoutStyle parent = UiLayoutStyle.column().align(UiLayoutStyle.Align.CENTER);
        UiLayoutStyle child = UiLayoutStyle.leaf().size(20, 10);

        UiLayoutEngine.Result result = UiLayoutEngine.layout(parent, new UiRect(0, 0, 80, 20), Arrays.asList(child));

        assertEquals(new UiRect(30, 0, 20, 10), result.children().get(0));
    }
}
