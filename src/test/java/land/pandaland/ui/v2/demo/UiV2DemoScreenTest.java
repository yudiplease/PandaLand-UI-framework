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

    @Test
    public void demoContainsMinecraftInventoryShowcaseNodes() {
        UiRuntime runtime = new UiRuntime(UiV2DemoScreen.create());
        runtime.layout(new UiRect(0, 0, 960, 540));

        assertTrue("Expected ITEM node", contains(runtime.screen().root(), UiNode.Type.ITEM));
        assertTrue("Expected SLOT node", contains(runtime.screen().root(), UiNode.Type.SLOT));
        assertTrue("Expected INVENTORY_GRID node", contains(runtime.screen().root(), UiNode.Type.INVENTORY_GRID));
        assertTrue("Expected HOTBAR node", contains(runtime.screen().root(), UiNode.Type.HOTBAR));
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

    private static boolean contains(UiNode node, UiNode.Type type) {
        if (node == null) {
            return false;
        }
        if (node.type() == type) {
            return true;
        }
        for (UiNode child : node.children()) {
            if (contains(child, type)) {
                return true;
            }
        }
        return false;
    }
}
