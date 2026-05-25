package land.pandaland.ui.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import land.pandaland.ui.api.PandaHudOverlay;
import land.pandaland.ui.api.PandaLayout;
import land.pandaland.ui.api.PandaRenderer;
import land.pandaland.ui.api.PandaToast;
import land.pandaland.ui.api.PandaRect;
import land.pandaland.ui.render.PandaScreenRenderer;

public final class PandaToastOverlay extends PandaHudOverlay {
    private static final int TOAST_X = 8;
    private static final int TOAST_Y = 8;
    private static final int TOAST_WIDTH = 260;
    private static final int TOAST_SPACING = 4;

    private final List<PandaToast> toasts = new ArrayList<PandaToast>();

    public int priority() {
        return Integer.MAX_VALUE;
    }

    public boolean visible() {
        return !toasts.isEmpty();
    }

    public void add(PandaToast toast) {
        if (toast == null) {
            throw new IllegalArgumentException("toast cannot be null");
        }
        toasts.add(toast);
    }

    public List<PandaToast> toasts() {
        return Collections.unmodifiableList(new ArrayList<PandaToast>(toasts));
    }

    public void clear() {
        toasts.clear();
    }

    public void update(long deltaMs) {
        for (PandaToast toast : new ArrayList<PandaToast>(toasts)) {
            PandaUiErrorHandler.update(toast, deltaMs);
        }
        for (Iterator<PandaToast> iterator = toasts.iterator(); iterator.hasNext();) {
            if (iterator.next().expired()) {
                iterator.remove();
            }
        }
    }

    public void render(PandaRenderer renderer) {
        PandaLayout stack = PandaLayout.vertical(TOAST_SPACING);
        for (PandaToast toast : toasts()) {
            stack.add(toast);
        }
        int height = Math.max(1, stack.preferredHeight());
        stack.layout(new PandaRect(TOAST_X, TOAST_Y, TOAST_WIDTH, height));
        PandaScreenRenderer.render(renderer, stack, -1, -1);
    }
}
