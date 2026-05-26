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
        if (parent.direction() == UiLayoutStyle.Direction.GRID) {
            return layoutGrid(parent, bounds, safeChildren);
        }
        if (parent.direction() == UiLayoutStyle.Direction.ABSOLUTE) {
            return layoutAbsolute(parent, bounds, safeChildren);
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
            int height = fitHeight(child, heights.get(i).intValue(), Math.max(0, maxBottom - y));
            int width = crossSize(child, contentWidth, false);
            int childX = alignedX(parent, child, x, contentWidth, width);
            rects.add(new UiRect(childX, y, width, height));
            y += height + parent.gap();
        }
        return new Result(rects);
    }

    private static Result layoutRow(UiLayoutStyle parent, UiRect bounds, List<UiLayoutStyle> children) {
        if (parent.wrap()) {
            return layoutWrappedRow(parent, bounds, children);
        }
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
            int width = fitWidth(child, widths.get(i).intValue(), Math.max(0, maxRight - x));
            int height = crossSize(child, contentHeight, true);
            int childY = alignedY(parent, child, y, contentHeight, height);
            rects.add(new UiRect(x, childY, width, height));
            x += width + parent.gap();
        }
        return new Result(rects);
    }

    private static Result layoutWrappedRow(UiLayoutStyle parent, UiRect bounds, List<UiLayoutStyle> children) {
        UiInsets padding = parent.padding();
        int startX = bounds.x + padding.left;
        int x = startX;
        int y = bounds.y + padding.top;
        int contentWidth = Math.max(0, bounds.width - padding.left - padding.right);
        int contentHeight = Math.max(0, bounds.height - padding.top - padding.bottom);
        int maxRight = bounds.x + bounds.width - padding.right;
        List<UiRect> rects = new ArrayList<UiRect>();
        int lineHeight = 0;
        for (UiLayoutStyle child : children) {
            int width = fitWidth(child, mainSize(child, contentWidth, false), contentWidth);
            int height = crossSize(child, contentHeight, true);
            if (x > startX && x + width > maxRight) {
                x = startX;
                y += lineHeight + parent.gap();
                lineHeight = 0;
            }
            rects.add(new UiRect(x, y, width, height));
            x += width + parent.gap();
            lineHeight = Math.max(lineHeight, height);
        }
        return new Result(rects);
    }

    private static Result layoutGrid(UiLayoutStyle parent, UiRect bounds, List<UiLayoutStyle> children) {
        UiInsets padding = parent.padding();
        int contentX = bounds.x + padding.left;
        int contentY = bounds.y + padding.top;
        int contentWidth = Math.max(0, bounds.width - padding.left - padding.right);
        int contentHeight = Math.max(0, bounds.height - padding.top - padding.bottom);
        int columns = Math.max(1, parent.gridColumns());
        int gap = parent.gap();
        int cellWidth = Math.max(0, (contentWidth - Math.max(0, columns - 1) * gap) / columns);
        int rowHeight = parent.gridRowHeight();
        List<UiRect> rects = new ArrayList<UiRect>();
        for (int i = 0; i < children.size(); i++) {
            UiLayoutStyle child = children.get(i);
            int column = i % columns;
            int row = i / columns;
            int cellX = contentX + column * (cellWidth + gap);
            int cellY = contentY + row * (rowHeight + gap);
            int width = clampWidth(child, cellWidth);
            int height = clampHeight(child, rowHeight);
            rects.add(new UiRect(cellX, cellY, width, height));
        }
        return new Result(rects);
    }

    private static Result layoutAbsolute(UiLayoutStyle parent, UiRect bounds, List<UiLayoutStyle> children) {
        UiInsets padding = parent.padding();
        int contentX = bounds.x + padding.left;
        int contentY = bounds.y + padding.top;
        int contentWidth = Math.max(0, bounds.width - padding.left - padding.right);
        int contentHeight = Math.max(0, bounds.height - padding.top - padding.bottom);
        List<UiRect> rects = new ArrayList<UiRect>();
        for (UiLayoutStyle child : children) {
            UiSize size = child.preferredSize();
            int width = child.widthPercent() >= 0.0F ? Math.round(contentWidth * child.widthPercent()) : size.width;
            int height = child.heightPercent() >= 0.0F ? Math.round(contentHeight * child.heightPercent()) : size.height;
            rects.add(new UiRect(contentX + child.x(), contentY + child.y(), clampWidth(child, width), clampHeight(child, height)));
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
        for (UiLayoutStyle child : children) {
            rects.add(new UiRect(content.x, content.y, clampWidth(child, content.width), clampHeight(child, content.height)));
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
            int width = clampWidth(child, child.widthPercent() >= 0.0F ? Math.round(contentWidth * child.widthPercent()) : Math.max(size.width, contentWidth));
            int height = clampHeight(child, size.height);
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
        for (int i = 0; i < children.size(); i++) {
            UiLayoutStyle child = children.get(i);
            sizes.set(i, Integer.valueOf(vertical ? clampHeight(child, sizes.get(i).intValue()) : clampWidth(child, sizes.get(i).intValue())));
        }
        return sizes;
    }

    private static int mainSize(UiLayoutStyle child, int available, boolean vertical) {
        UiSize size = child.preferredSize();
        float percent = vertical ? child.heightPercent() : child.widthPercent();
        if (percent >= 0.0F) {
            return vertical ? clampHeight(child, Math.round(available * percent)) : clampWidth(child, Math.round(available * percent));
        }
        return vertical ? clampHeight(child, size.height) : clampWidth(child, size.width);
    }

    private static int crossSize(UiLayoutStyle child, int available, boolean verticalCross) {
        UiSize size = child.preferredSize();
        float percent = verticalCross ? child.heightPercent() : child.widthPercent();
        if (child.crossAlign() == UiLayoutStyle.Align.STRETCH) {
            return verticalCross ? fitHeight(child, available, available) : fitWidth(child, available, available);
        }
        if (percent >= 0.0F) {
            return verticalCross ? fitHeight(child, Math.round(available * percent), available) : fitWidth(child, Math.round(available * percent), available);
        }
        int preferred = verticalCross ? size.height : size.width;
        return verticalCross ? fitHeight(child, preferred, available) : fitWidth(child, preferred, available);
    }

    private static int fitWidth(UiLayoutStyle style, int width, int available) {
        return fit(width, Math.max(0, available), style.minWidth(), style.maxWidth());
    }

    private static int fitHeight(UiLayoutStyle style, int height, int available) {
        return fit(height, Math.max(0, available), style.minHeight(), style.maxHeight());
    }

    private static int fit(int value, int available, int min, int max) {
        int safeMin = Math.max(0, min);
        if (safeMin > available) {
            return clamp(safeMin, safeMin, max);
        }
        return Math.min(clamp(value, safeMin, max), available);
    }

    private static int clampWidth(UiLayoutStyle style, int width) {
        return clamp(width, style.minWidth(), style.maxWidth());
    }

    private static int clampHeight(UiLayoutStyle style, int height) {
        return clamp(height, style.minHeight(), style.maxHeight());
    }

    private static int clamp(int value, int min, int max) {
        int safeMin = Math.max(0, min);
        int safeMax = Math.max(safeMin, max);
        return Math.max(safeMin, Math.min(safeMax, Math.max(0, value)));
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
