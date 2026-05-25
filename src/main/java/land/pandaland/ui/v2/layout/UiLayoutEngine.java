package land.pandaland.ui.v2.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UiLayoutEngine {
    private UiLayoutEngine() {
    }

    public static Result layout(UiLayoutStyle parent, UiRect bounds, List<UiLayoutStyle> children) {
        if (parent == null) {
            throw new IllegalArgumentException("parent cannot be null");
        }
        if (bounds == null) {
            throw new IllegalArgumentException("bounds cannot be null");
        }
        List<UiLayoutStyle> safeChildren = children == null ? Collections.<UiLayoutStyle>emptyList() : children;
        if (parent.direction() == UiLayoutStyle.Direction.ROW) {
            return layoutRow(parent, bounds, safeChildren);
        }
        if (parent.direction() == UiLayoutStyle.Direction.OVERLAY) {
            return layoutOverlay(parent, bounds, safeChildren);
        }
        return layoutColumn(parent, bounds, safeChildren);
    }

    private static Result layoutColumn(UiLayoutStyle parent, UiRect bounds, List<UiLayoutStyle> children) {
        UiInsets padding = parent.padding();
        int x = bounds.x + padding.left;
        int y = bounds.y + padding.top;
        int maxRight = bounds.x + bounds.width - padding.right;
        int maxBottom = bounds.y + bounds.height - padding.bottom;
        List<UiRect> rects = new ArrayList<UiRect>();
        for (UiLayoutStyle child : children) {
            UiSize size = child.preferredSize();
            int width = Math.min(size.width, Math.max(0, maxRight - x));
            int height = Math.min(size.height, Math.max(0, maxBottom - y));
            rects.add(new UiRect(x, y, width, height));
            y += height + parent.gap();
        }
        return new Result(rects);
    }

    private static Result layoutRow(UiLayoutStyle parent, UiRect bounds, List<UiLayoutStyle> children) {
        UiInsets padding = parent.padding();
        int x = bounds.x + padding.left;
        int y = bounds.y + padding.top;
        int maxRight = bounds.x + bounds.width - padding.right;
        int maxBottom = bounds.y + bounds.height - padding.bottom;
        List<UiRect> rects = new ArrayList<UiRect>();
        for (UiLayoutStyle child : children) {
            UiSize size = child.preferredSize();
            int width = Math.min(size.width, Math.max(0, maxRight - x));
            int height = Math.min(size.height, Math.max(0, maxBottom - y));
            rects.add(new UiRect(x, y, width, height));
            x += width + parent.gap();
        }
        return new Result(rects);
    }

    private static Result layoutOverlay(UiLayoutStyle parent, UiRect bounds, List<UiLayoutStyle> children) {
        UiInsets padding = parent.padding();
        UiRect content = new UiRect(
            bounds.x + padding.left,
            bounds.y + padding.top,
            Math.max(0, bounds.width - padding.left - padding.right),
            Math.max(0, bounds.height - padding.top - padding.bottom)
        );
        List<UiRect> rects = new ArrayList<UiRect>();
        for (int i = 0; i < children.size(); i++) {
            rects.add(content);
        }
        return new Result(rects);
    }

    public static final class Result {
        private final List<UiRect> children;

        private Result(List<UiRect> children) {
            this.children = Collections.unmodifiableList(new ArrayList<UiRect>(children));
        }

        public List<UiRect> children() {
            return children;
        }
    }
}
