package land.pandaland.ui.v2.forge;

import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.minecraft.UiItemStackRef;
import net.minecraft.item.ItemStack;

final class UiV2RenderSupport {
    private UiV2RenderSupport() {
    }

    enum ItemCommandKind {
        EMPTY,
        NON_ITEMSTACK,
        ITEMSTACK
    }

    static UvRegion textureRegion(int u, int v, int width, int height, int textureWidth, int textureHeight) {
        if (textureWidth <= 0 || textureHeight <= 0 || width <= 0 || height <= 0) {
            return UvRegion.invalid();
        }
        int sourceLeft = clamp(u, 0, textureWidth);
        int sourceTop = clamp(v, 0, textureHeight);
        int sourceRight = clamp(u + width, 0, textureWidth);
        int sourceBottom = clamp(v + height, 0, textureHeight);
        int sourceWidth = sourceRight - sourceLeft;
        int sourceHeight = sourceBottom - sourceTop;
        if (sourceWidth <= 0 || sourceHeight <= 0) {
            return UvRegion.invalid();
        }
        return new UvRegion(true, sourceLeft, sourceTop, sourceWidth, sourceHeight, textureWidth, textureHeight);
    }

    static NineSlice nineSlice(UiRect target, int u, int v, int width, int height, int textureWidth, int textureHeight, int left, int top, int right, int bottom) {
        if (target == null || target.width <= 0 || target.height <= 0) {
            return NineSlice.invalid();
        }
        UvRegion region = textureRegion(u, v, width, height, textureWidth, textureHeight);
        if (!region.valid()) {
            return NineSlice.invalid();
        }

        int[] horizontalSource = clampPair(Math.max(0, left), Math.max(0, right), region.width());
        int[] verticalSource = clampPair(Math.max(0, top), Math.max(0, bottom), region.height());
        int[] horizontalTarget = clampPair(horizontalSource[0], horizontalSource[1], target.width);
        int[] verticalTarget = clampPair(verticalSource[0], verticalSource[1], target.height);

        return new NineSlice(
                true,
                target,
                region.u(),
                region.v(),
                region.width(),
                region.height(),
                region.textureWidth(),
                region.textureHeight(),
                horizontalSource[0],
                verticalSource[0],
                horizontalSource[1],
                verticalSource[1],
                horizontalTarget[0],
                verticalTarget[0],
                horizontalTarget[1],
                verticalTarget[1]);
    }

    static ItemCommandKind classifyItem(UiItemStackRef item) {
        if (item == null || item.isEmpty()) {
            return ItemCommandKind.EMPTY;
        }
        return item.rawStack() instanceof ItemStack ? ItemCommandKind.ITEMSTACK : ItemCommandKind.NON_ITEMSTACK;
    }

    private static int[] clampPair(int first, int second, int total) {
        if (total <= 0) {
            return new int[] {0, 0};
        }
        int actualFirst = Math.min(Math.max(0, first), total);
        int actualSecond = Math.min(Math.max(0, second), total);
        if (actualFirst + actualSecond <= total) {
            return new int[] {actualFirst, actualSecond};
        }
        if (actualFirst == 0) {
            return new int[] {0, total};
        }
        if (actualSecond == 0) {
            return new int[] {total, 0};
        }
        double scale = (double) total / (double) (actualFirst + actualSecond);
        actualFirst = (int) Math.floor(actualFirst * scale);
        actualSecond = total - actualFirst;
        return new int[] {actualFirst, actualSecond};
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    static final class UvRegion {
        private static final UvRegion INVALID = new UvRegion(false, 0, 0, 0, 0, 0, 0);

        private final boolean valid;
        private final int u;
        private final int v;
        private final int width;
        private final int height;
        private final int textureWidth;
        private final int textureHeight;

        private UvRegion(boolean valid, int u, int v, int width, int height, int textureWidth, int textureHeight) {
            this.valid = valid;
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }

        static UvRegion invalid() {
            return INVALID;
        }

        boolean valid() {
            return valid;
        }

        int u() {
            return u;
        }

        int v() {
            return v;
        }

        int width() {
            return width;
        }

        int height() {
            return height;
        }

        int textureWidth() {
            return textureWidth;
        }

        int textureHeight() {
            return textureHeight;
        }
    }

    static final class NineSlice {
        private static final NineSlice INVALID = new NineSlice(false, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        private final boolean valid;
        private final UiRect target;
        private final int sourceU;
        private final int sourceV;
        private final int sourceWidth;
        private final int sourceHeight;
        private final int textureWidth;
        private final int textureHeight;
        private final int sourceLeft;
        private final int sourceTop;
        private final int sourceRight;
        private final int sourceBottom;
        private final int targetLeft;
        private final int targetTop;
        private final int targetRight;
        private final int targetBottom;

        private NineSlice(boolean valid, UiRect target, int sourceU, int sourceV, int sourceWidth, int sourceHeight, int textureWidth, int textureHeight, int sourceLeft, int sourceTop, int sourceRight, int sourceBottom, int targetLeft, int targetTop, int targetRight, int targetBottom) {
            this.valid = valid;
            this.target = target;
            this.sourceU = sourceU;
            this.sourceV = sourceV;
            this.sourceWidth = sourceWidth;
            this.sourceHeight = sourceHeight;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.sourceLeft = sourceLeft;
            this.sourceTop = sourceTop;
            this.sourceRight = sourceRight;
            this.sourceBottom = sourceBottom;
            this.targetLeft = targetLeft;
            this.targetTop = targetTop;
            this.targetRight = targetRight;
            this.targetBottom = targetBottom;
        }

        static NineSlice invalid() {
            return INVALID;
        }

        boolean valid() {
            return valid;
        }

        UiRect target() {
            return target;
        }

        int sourceU() {
            return sourceU;
        }

        int sourceV() {
            return sourceV;
        }

        int sourceWidth() {
            return sourceWidth;
        }

        int sourceHeight() {
            return sourceHeight;
        }

        int textureWidth() {
            return textureWidth;
        }

        int textureHeight() {
            return textureHeight;
        }

        int sourceLeft() {
            return sourceLeft;
        }

        int sourceTop() {
            return sourceTop;
        }

        int sourceRight() {
            return sourceRight;
        }

        int sourceBottom() {
            return sourceBottom;
        }

        int targetLeft() {
            return targetLeft;
        }

        int targetTop() {
            return targetTop;
        }

        int targetRight() {
            return targetRight;
        }

        int targetBottom() {
            return targetBottom;
        }
    }
}
