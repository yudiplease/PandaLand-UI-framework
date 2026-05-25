package land.pandaland.ui.render;

import land.pandaland.ui.api.PandaLayout;
import land.pandaland.ui.runtime.PandaScreenRuntime;

public final class PandaScreenRenderer {
    private PandaScreenRenderer() {
    }

    public static void render(land.pandaland.ui.api.PandaRenderer renderer, PandaLayout layout, int mouseX, int mouseY) {
        PandaRenderTraversal.render(renderer, layout, mouseX, mouseY);
    }

    public static void render(land.pandaland.ui.api.PandaRenderer renderer, PandaScreenRuntime runtime, int mouseX, int mouseY) {
        PandaRenderTraversal.render(renderer, runtime, mouseX, mouseY);
    }
}
