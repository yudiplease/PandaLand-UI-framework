package land.pandaland.ui.layout;

import land.pandaland.ui.api.PandaComponent;
import land.pandaland.ui.api.PandaLayout;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PandaLayoutTest {
    @Test
    public void verticalStackPlacesChildrenWithSpacing() {
        PandaLayout root = PandaLayout.vertical(4);
        FixedComponent first = new FixedComponent(50, 10);
        FixedComponent second = new FixedComponent(40, 20);

        root.add(first);
        root.add(second);
        root.layout(new land.pandaland.ui.api.PandaRect(10, 20, 100, 100));

        assertEquals(10, first.bounds().x);
        assertEquals(20, first.bounds().y);
        assertEquals(50, first.bounds().width);
        assertEquals(10, first.bounds().height);
        assertEquals(10, second.bounds().x);
        assertEquals(34, second.bounds().y);
        assertEquals(40, second.bounds().width);
        assertEquals(20, second.bounds().height);
    }

    @Test
    public void clearRemovesChildren() {
        PandaLayout root = PandaLayout.vertical(4);
        root.add(new FixedComponent(50, 10));

        root.clear();

        assertEquals(0, root.children().size());
    }

    @Test
    public void verticalStackLaysOutNestedLayouts() {
        PandaLayout root = PandaLayout.vertical(4);
        PandaLayout nested = PandaLayout.vertical(2);
        FixedComponent nestedChild = new FixedComponent(30, 12);

        nested.add(nestedChild);
        root.add(nested);
        root.layout(new land.pandaland.ui.api.PandaRect(10, 20, 100, 100));

        assertEquals(10, nestedChild.bounds().x);
        assertEquals(20, nestedChild.bounds().y);
        assertEquals(30, nestedChild.bounds().width);
        assertEquals(12, nestedChild.bounds().height);
    }

    @Test
    public void verticalStackUsesComponentLayoutHook() {
        PandaLayout root = PandaLayout.vertical(4);
        TrackingLayoutComponent child = new TrackingLayoutComponent(50, 10);

        root.add(child);
        root.layout(new land.pandaland.ui.api.PandaRect(10, 20, 100, 100));

        assertEquals(1, child.layoutCalls);
        assertEquals(new land.pandaland.ui.api.PandaRect(10, 20, 50, 10), child.lastLayoutBounds);
    }

    @Test
    public void nestedLayoutDispatchesClicksToChild() {
        PandaLayout root = PandaLayout.vertical(4);
        PandaLayout nested = PandaLayout.vertical(2);
        ClickComponent nestedChild = new ClickComponent(30, 12);

        nested.add(nestedChild);
        root.add(nested);
        root.layout(new land.pandaland.ui.api.PandaRect(10, 20, 100, 100));

        assertTrue(root.mouseClicked(15, 25, 0));
        assertEquals(1, nestedChild.clicks);
    }

    @Test
    public void keyTypedDispatchUsesSnapshotWhenChildClearsLayout() {
        PandaLayout root = PandaLayout.vertical(4);
        KeyComponent bottom = new KeyComponent(true);
        KeyComponent top = new KeyComponent(false) {
            public boolean keyTyped(char character, int keyCode) {
                root.clear();
                return super.keyTyped(character, keyCode);
            }
        };

        root.add(bottom);
        root.add(top);

        assertTrue(root.keyTyped('a', 30));
        assertEquals(1, top.keys);
        assertEquals(1, bottom.keys);
        assertEquals(0, root.children().size());
    }

    @Test
    public void updateErrorInOneChildDoesNotStopSiblings() {
        PandaLayout root = PandaLayout.vertical(4);
        UpdatingComponent first = new UpdatingComponent();
        UpdatingComponent second = new UpdatingComponent();

        root.add(new ThrowingUpdateComponent());
        root.add(first);
        root.add(second);
        root.update(50L);

        assertEquals(50L, first.updatedMs);
        assertEquals(50L, second.updatedMs);
    }

    @Test
    public void keyErrorInOneChildDoesNotStopSiblings() {
        PandaLayout root = PandaLayout.vertical(4);
        KeyComponent bottom = new KeyComponent(true);

        root.add(bottom);
        root.add(new ThrowingKeyComponent());

        assertTrue(root.keyTyped('a', 30));
        assertEquals(1, bottom.keys);
    }

    @Test
    public void rowPlacesChildrenLeftToRightWithSpacing() {
        PandaLayout root = PandaLayout.row(3);
        FixedComponent first = new FixedComponent(20, 10);
        FixedComponent second = new FixedComponent(30, 12);

        root.add(first);
        root.add(second);
        root.layout(new land.pandaland.ui.api.PandaRect(5, 7, 100, 40));

        assertEquals(new land.pandaland.ui.api.PandaRect(5, 7, 20, 10), first.bounds());
        assertEquals(new land.pandaland.ui.api.PandaRect(28, 7, 30, 12), second.bounds());
    }

    @Test
    public void absolutePlacesChildrenAtRequestedOffsets() {
        PandaLayout root = PandaLayout.absolute();
        FixedComponent child = new FixedComponent(25, 10);

        child.position(12, 8);
        root.add(child);
        root.layout(new land.pandaland.ui.api.PandaRect(10, 20, 100, 50));

        assertEquals(new land.pandaland.ui.api.PandaRect(22, 28, 25, 10), child.bounds());
    }

    @Test
    public void anchorPlacesChildAgainstSelectedParentEdge() {
        PandaLayout root = PandaLayout.anchor();
        FixedComponent child = new FixedComponent(20, 10);

        child.anchor(PandaLayout.Anchor.BOTTOM_RIGHT).margin(0, 0, 4, 6);
        root.add(child);
        root.layout(new land.pandaland.ui.api.PandaRect(10, 20, 100, 50));

        assertEquals(new land.pandaland.ui.api.PandaRect(86, 54, 20, 10), child.bounds());
    }

    @Test
    public void paddingAndMarginsOffsetStandardLayoutChildren() {
        PandaLayout root = PandaLayout.vertical(4).padding(2, 3, 4, 5);
        FixedComponent first = new FixedComponent(20, 10);
        FixedComponent second = new FixedComponent(30, 12);

        first.margin(1, 2, 3, 4);
        second.margin(5);
        root.add(first);
        root.add(second);
        root.layout(new land.pandaland.ui.api.PandaRect(10, 20, 100, 80));

        assertEquals(new land.pandaland.ui.api.PandaRect(13, 25, 20, 10), first.bounds());
        assertEquals(new land.pandaland.ui.api.PandaRect(17, 48, 30, 12), second.bounds());
    }

    @Test
    public void minAndMaxSizeConstrainChildBeforeParentClamp() {
        PandaLayout root = PandaLayout.row(0);
        FixedComponent first = new FixedComponent(10, 10);
        FixedComponent second = new FixedComponent(80, 30);

        first.minSize(20, 15);
        second.maxSize(25, 12);
        root.add(first);
        root.add(second);
        root.layout(new land.pandaland.ui.api.PandaRect(0, 0, 100, 100));

        assertEquals(new land.pandaland.ui.api.PandaRect(0, 0, 20, 15), first.bounds());
        assertEquals(new land.pandaland.ui.api.PandaRect(20, 0, 25, 12), second.bounds());
    }

    @Test
    public void verticalLayoutClampsChildrenToParentBounds() {
        PandaLayout root = PandaLayout.vertical(4);
        FixedComponent first = new FixedComponent(80, 30);
        FixedComponent second = new FixedComponent(80, 30);
        FixedComponent third = new FixedComponent(80, 30);

        root.add(first);
        root.add(second);
        root.add(third);
        root.layout(new land.pandaland.ui.api.PandaRect(10, 20, 50, 50));

        assertEquals(new land.pandaland.ui.api.PandaRect(10, 20, 50, 30), first.bounds());
        assertEquals(new land.pandaland.ui.api.PandaRect(10, 54, 50, 16), second.bounds());
        assertEquals(new land.pandaland.ui.api.PandaRect(10, 70, 50, 0), third.bounds());
    }

    @Test
    public void absoluteLayoutClampsChildrenToParentBounds() {
        PandaLayout root = PandaLayout.absolute();
        FixedComponent child = new FixedComponent(80, 40);

        child.position(30, 20);
        root.add(child);
        root.layout(new land.pandaland.ui.api.PandaRect(10, 20, 50, 30));

        assertEquals(new land.pandaland.ui.api.PandaRect(40, 40, 20, 10), child.bounds());
    }

    private static final class FixedComponent extends PandaComponent {
        private final int preferredWidth;
        private final int preferredHeight;

        private FixedComponent(int preferredWidth, int preferredHeight) {
            this.preferredWidth = preferredWidth;
            this.preferredHeight = preferredHeight;
        }

        public int preferredWidth() {
            return preferredWidth;
        }

        public int preferredHeight() {
            return preferredHeight;
        }
    }

    private static final class TrackingLayoutComponent extends PandaComponent {
        private final int preferredWidth;
        private final int preferredHeight;
        private int layoutCalls;
        private land.pandaland.ui.api.PandaRect lastLayoutBounds;

        private TrackingLayoutComponent(int preferredWidth, int preferredHeight) {
            this.preferredWidth = preferredWidth;
            this.preferredHeight = preferredHeight;
        }

        public int preferredWidth() {
            return preferredWidth;
        }

        public int preferredHeight() {
            return preferredHeight;
        }

        public void layout(land.pandaland.ui.api.PandaRect bounds) {
            layoutCalls++;
            lastLayoutBounds = bounds;
            super.layout(bounds);
        }
    }

    private static final class ClickComponent extends PandaComponent {
        private final int preferredWidth;
        private final int preferredHeight;
        private int clicks;

        private ClickComponent(int preferredWidth, int preferredHeight) {
            this.preferredWidth = preferredWidth;
            this.preferredHeight = preferredHeight;
        }

        public int preferredWidth() {
            return preferredWidth;
        }

        public int preferredHeight() {
            return preferredHeight;
        }

        public boolean mouseClicked(int mouseX, int mouseY, int button) {
            clicks++;
            return true;
        }
    }

    private static class KeyComponent extends PandaComponent {
        private final boolean handled;
        private int keys;

        private KeyComponent(boolean handled) {
            this.handled = handled;
        }

        public boolean keyTyped(char character, int keyCode) {
            keys++;
            return handled;
        }
    }

    private static final class UpdatingComponent extends PandaComponent {
        private long updatedMs;

        public void update(long deltaMs) {
            updatedMs += deltaMs;
        }
    }

    private static final class ThrowingUpdateComponent extends PandaComponent {
        public void update(long deltaMs) {
            throw new RuntimeException("update failed");
        }
    }

    private static final class ThrowingKeyComponent extends PandaComponent {
        public boolean keyTyped(char character, int keyCode) {
            throw new RuntimeException("key failed");
        }
    }
}
