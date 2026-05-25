package land.pandaland.ui.runtime;

import land.pandaland.ui.api.PandaHudOverlay;
import land.pandaland.ui.api.PandaRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class PandaHudRegistry {
    private final List<PandaHudOverlay> overlays = new ArrayList<PandaHudOverlay>();

    public void register(PandaHudOverlay overlay) {
        if (overlay == null) {
            throw new IllegalArgumentException("overlay");
        }
        if (!overlays.contains(overlay)) {
            overlays.add(overlay);
            Collections.sort(overlays, new Comparator<PandaHudOverlay>() {
                public int compare(PandaHudOverlay left, PandaHudOverlay right) {
                    return Integer.valueOf(left.priority()).compareTo(Integer.valueOf(right.priority()));
                }
            });
        }
    }

    public void unregister(PandaHudOverlay overlay) {
        overlays.remove(overlay);
    }

    public List<PandaHudOverlay> overlays() {
        return Collections.unmodifiableList(new ArrayList<PandaHudOverlay>(overlays));
    }

    public void updateVisible(long deltaMs) {
        for (PandaHudOverlay overlay : overlays()) {
            if (overlay.visible()) {
                PandaUiErrorHandler.overlayUpdate(overlay, deltaMs);
            }
        }
    }

    public void renderVisible(PandaRenderer renderer) {
        for (PandaHudOverlay overlay : overlays()) {
            if (overlay.visible()) {
                PandaUiErrorHandler.overlayRender(overlay, renderer);
            }
        }
    }
}
