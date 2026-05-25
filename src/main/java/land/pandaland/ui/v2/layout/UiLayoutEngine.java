package land.pandaland.ui.v2.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stateless layout engine for retained UI node styles.
 *
 * <p>The engine receives a parent style, parent bounds, and child styles, then
 * returns immutable child rectangles. It does not mutate nodes directly.</p>
 */
public final class UiLayoutEngine {
    private UiLayoutEngine() {
    }

    /**
     * Calculates child rectangles for a parent layout style.
     *
     * @param parent parent layout style
     * @param bounds available parent bounds
     * @param children child layout styles in tree order
     * @return layout result containing one rectangle per child
     */
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
        if (parent.direction() == UiLayoutStyle.Direction.SCROLL) {
            return layoutScroll(parent, bounds, safeChildren);
        }
        return layoutColumn(parent, bounds, safeChildren);
    }

    private static Result layoutColumn(UiLayoutStyle parent, UiRect bounds, List<UiLayoutStyle> children) {
        UiInsets padding = parent.padding();
        int x = bounds.x + padding.left;
        int y = bounds.y + padding.top;
        int contentWidth = Math.max(0, bounds.width - padding.left - padding.right);
        int contentHeight = Math.max(0, bounds.height - padding.top - padding.bottom);
        int maxBottom = bounds.y + bounds.height - padding.bottom;
        List<UiRect> rects = new ArrayList<UiRect>();
        List<Integer> heights = distribute(children, contentHeight, true, parent.gap());
        for (int i = 0; i < children.size(); i++) {
            UiLayoutStyle child = children.get(i);
            int height = Math.min(heights.get(i), Math.max(0, maxBottom - y));
            int width = crossSize(child, contentWidth, false);
            int childX = alignedX(parent, child, x, contentWidth, width);
            rects.add(new UiRect(childX, y, width, height));
            y += height + parent.gap();
        }
        return new Result(rects);
    }

    private static Result layoutRow(UiLayoutStyle parent, UiRect bounds, List<UiLayoutStyle> children) {
        UiInsets padding = parent.padding();
        int x = bounds.x + padding.left;
        int y = bounds.y + padding.top;
        int contentWidth = Math.max(0, bounds.width - padding.left - padding.right);
        int contentHeight = Math.max(0, bounds.height - padding.top - padding.bottom);
        int maxRight = bounds.x + bounds.width - padding.right;
        List<UiRect> rects = new ArrayList<UiRect>();
        List<Integer> widths = distribute(children, contentWidth, false, parent.gap());
        for (int i = 0; i < children.size(); i++) {
            UiLayoutStyle child = children.get(i);
            int width = Math.min(widths.get(i), Math.max(0, maxRight - x));
            int height = crossSize(child, contentHeight, true);
            int childY = alignedY(parent, child, y, contentHeight, height);
            rects.add(new UiRect(x, childY, width, height));
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

    private static Result layoutScroll(UiLayoutStyle parent, UiRect bounds, List<UiLayoutStyle> children) {
        UiInsets padding = parent.padding();
        int x = bounds.x + padding.left;
        int y = bounds.y + padding.top;
        int contentWidth = Math.max(0, bounds.width - padding.left - padding.right);
        List<UiRect> rects = new ArrayList<UiRect>();
        for (UiLayoutStyle child : children) {
            UiSize size = child.preferredSize();
            int width = child.widthPercent() >= 0.0F ? Math.round(contentWidth * child.widthPercent()) : Math.max(size.width, contentWidth);
            int height = Math.max(0, size.height);
            rects.add(new UiRect(x, y, width, height));
            y += height + parent.gap();
        }
        return new Result(rects);
    }

    private static List<Integer> distribute(List<UiLayoutStyle> children, int available, boolean vertical, int gap) {
        List<Integer> sizes = new ArrayList<Integer>();
        if (children.isEmpty()) {
            return sizes;
        }

        int gapTotal = Math.max(0, children.size() - 1) * Math.max(0, gap);
        int space = Math.max(0, available - gapTotal);
        int preferred = 0;
        float growTotal = 0.0F;
        float shrinkTotal = 0.0F;
        for (UiLayoutStyle child : children) {
            int size = mainSize(child, available, vertical);
            sizes.add(Integer.valueOf(size));
            preferred += size;
            growTotal += child.grow();
            shrinkTotal += child.shrink();
        }

        if (preferred < space && growTotal > 0.0F) {
            int extra = space - preferred;
            for (int i = 0; i < children.size(); i++) {
                UiLayoutStyle child = children.get(i);
                if (child.grow() > 0.0F) {
                    sizes.set(i, Integer.valueOf(sizes.get(i).intValue() + Math.round(extra * child.grow() / growTotal)));
                }
            }
        } else if (preferred > space && shrinkTotal > 0.0F) {
            int overflow = preferred - space;
            for (int i = 0; i < children.size(); i++) {
                UiLayoutStyle child = children.get(i);
                if (child.shrink() > 0.0F) {
                    sizes.set(i, Integer.valueOf(Math.max(0, sizes.get(i).intValue() - Math.round(overflow * child.shrink() / shrinkTotal))));
                }
            }
        }
        return sizes;
    }

    private static int mainSize(UiLayoutStyle child, int available, boolean vertical) {
        UiSize size = child.preferredSize();
        float percent = vertical ? child.heightPercent() : child.widthPercent();
        if (percent >= 0.0F) {
            return Math.round(available * percent);
        }
        return Math.max(0, vertical ? size.height : size.width);
    }

    private static int crossSize(UiLayoutStyle child, int available, boolean verticalCross) {
        UiSize size = child.preferredSize();
        float percent = verticalCross ? child.heightPercent() : child.widthPercent();
        if (child.crossAlign() == UiLayoutStyle.Align.STRETCH) {
            return available;
        }
        if (percent >= 0.0F) {
            return Math.round(available * percent);
        }
        int preferred = verticalCross ? size.height : size.width;
        return Math.min(Math.max(0, preferred), available);
    }

    private static int alignedX(UiLayoutStyle parent, UiLayoutStyle child, int x, int contentWidth, int width) {
        UiLayoutStyle.Align align = child.crossAlign() == UiLayoutStyle.Align.START ? parent.crossAlign() : child.crossAlign();
        if (align == UiLayoutStyle.Align.CENTER) {
            return x + Math.max(0, (contentWidth - width) / 2);
        }
        if (align == UiLayoutStyle.Align.END) {
            return x + Math.max(0, contentWidth - width);
        }
        return x;
    }

    private static int alignedY(UiLayoutStyle parent, UiLayoutStyle child, int y, int contentHeight, int height) {
        UiLayoutStyle.Align align = child.crossAlign() == UiLayoutStyle.Align.START ? parent.crossAlign() : child.crossAlign();
        if (align == UiLayoutStyle.Align.CENTER) {
            return y + Math.max(0, (contentHeight - height) / 2);
        }
        if (align == UiLayoutStyle.Align.END) {
            return y + Math.max(0, contentHeight - height);
        }
        return y;
    }

    /**
     * Immutable result of one layout pass.
     */
    public static final class Result {
        private final List<UiRect> children;

        private Result(List<UiRect> children) {
            this.children = Collections.unmodifiableList(new ArrayList<UiRect>(children));
        }

        /**
         * Returns child rectangles in the same order as input child styles.
         *
         * @return immutable child rectangle list
         */
        public List<UiRect> children() {
            return children;
        }
    }
}
