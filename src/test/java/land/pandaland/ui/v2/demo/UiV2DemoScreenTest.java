package land.pandaland.ui.v2.demo;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.layout.UiRect;

/**
 * Regression checks for the bundled v2 demo screen layout.
 */
public class UiV2DemoScreenTest {
    /**
     * Ensures demo controls do not collapse into zero-height rows.
     */
    @Test
    public void demoNodesReceiveVisibleBounds() {
        UiRuntime runtime = new UiRuntime(UiV2DemoScreen.create());
        runtime.layout(new UiRect(0, 0, 960, 540));

        assertVisible(runtime.screen().root());
    }

    private static void assertVisible(UiNode node) {
        if (node != null && node.type() != UiNode.Type.ROOT) {
            assertTrue("Expected positive width for " + node.type(), node.bounds().width > 0);
            assertTrue("Expected positive height for " + node.type(), node.bounds().height > 0);
        }
        for (UiNode child : node.children()) {
            assertVisible(child);
        }
    }
}
