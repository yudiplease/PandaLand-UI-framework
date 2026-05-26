package land.pandaland.ui.v2.layout;

import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.minecraft.UiAnchor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class UiMinecraftAnchorLayoutTest {
    @Test
    public void centerAnchorCentersInsideScreenBounds() {
        UiScreen screen = screenWithPanel(UiAnchor.CENTER, 4, -2);
        UiRuntime runtime = new UiRuntime(screen);

        runtime.layout(new UiRect(0, 0, 200, 120));

        assertEquals(new UiRect(54, 38, 100, 40), anchoredPanel(screen).bounds());
    }

    @Test
    public void hotbarAnchorPlacesBottomCenterWithMinecraftMargin() {
        UiScreen screen = screenWithPanel(UiAnchor.HOTBAR, 0, 0);
        UiRuntime runtime = new UiRuntime(screen);

        runtime.layout(new UiRect(0, 0, 200, 120));

        assertEquals(new UiRect(50, 58, 100, 40), anchoredPanel(screen).bounds());
    }

    @Test
    public void hotbarAnchorUsesScreenBoundsWhenNestedInsidePanel() {
        UiScreen screen = Ui.screen("nested-hotbar").root(new Ui.RootBuilderConsumer() {
            public void build(Ui.NodeBuilder root) {
                root.absolute().panel(new Ui.PanelBuilderConsumer() {
                    public void build(Ui.NodeBuilder panel) {
                        panel.size(50, 50).offset(20, 20).panel(new Ui.PanelBuilderConsumer() {
                            public void build(Ui.NodeBuilder hotbar) {
                                hotbar.size(100, 20).anchor(UiAnchor.HOTBAR);
                            }
                        });
                    }
                });
            }
        }).build();
        UiRuntime runtime = new UiRuntime(screen);

        runtime.layout(new UiRect(0, 0, 200, 120));

        UiNode nestedHotbar = screen.root().children().get(0).children().get(0);
        assertEquals(new UiRect(50, 78, 100, 20), nestedHotbar.bounds());
    }

    @Test
    public void inventoryCenterFallsBackToCenterOutsideContainer() {
        UiScreen screen = screenWithPanel(UiAnchor.INVENTORY_CENTER, 0, 0);
        UiRuntime runtime = new UiRuntime(screen);

        runtime.layout(new UiRect(0, 0, 200, 120));

        assertEquals(new UiRect(50, 40, 100, 40), anchoredPanel(screen).bounds());
    }

    @Test
    public void nonAnchoredLayoutRemainsUnchanged() {
        UiScreen screen = screenWithPanel(null, 7, 9);
        UiRuntime runtime = new UiRuntime(screen);

        runtime.layout(new UiRect(0, 0, 200, 120));

        assertEquals(new UiRect(7, 9, 100, 40), anchoredPanel(screen).bounds());
    }

    private static UiScreen screenWithPanel(final UiAnchor anchor, final int offsetX, final int offsetY) {
        return Ui.screen("anchor").root(new Ui.RootBuilderConsumer() {
            public void build(Ui.NodeBuilder root) {
                root.absolute().panel(new Ui.PanelBuilderConsumer() {
                    public void build(Ui.NodeBuilder panel) {
                        panel.size(100, 40).offset(offsetX, offsetY);
                        if (anchor != null) {
                            panel.anchor(anchor).snapToPixel(true);
                        }
                    }
                });
            }
        }).build();
    }

    private static UiNode anchoredPanel(UiScreen screen) {
        return screen.root().children().get(0);
    }
}
