package land.pandaland.ui.v2.components;

import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.state.UiState;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public final class UiSliderTest {
    @Test
    public void sliderUpdatesBoundStateWhileDragged() {
        final UiState<Float> value = UiState.of(0.0F);
        UiScreen screen = Ui.screen("settings")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column().slider("Music", value);
                }
            })
            .build();
        UiRuntime runtime = new UiRuntime(screen);
        runtime.layout(new UiRect(0, 0, 100, 24));

        runtime.events().pointerDown(1, 6, 0);
        runtime.events().pointerDrag(80, 6, 0, 16L);

        assertTrue(value.get().floatValue() > 0.70F);
    }
}
