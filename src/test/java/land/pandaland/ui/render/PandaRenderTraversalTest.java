package land.pandaland.ui.render;

import java.util.ArrayList;
import java.util.List;
import land.pandaland.ui.api.PandaButton;
import land.pandaland.ui.api.PandaHudBar;
import land.pandaland.ui.api.PandaIcon;
import land.pandaland.ui.api.PandaLabel;
import land.pandaland.ui.api.PandaLayout;
import land.pandaland.ui.api.PandaList;
import land.pandaland.ui.api.PandaModal;
import land.pandaland.ui.api.PandaPanel;
import land.pandaland.ui.api.PandaProgressBar;
import land.pandaland.ui.api.PandaSlider;
import land.pandaland.ui.api.PandaTabs;
import land.pandaland.ui.api.PandaToast;
import land.pandaland.ui.api.PandaRect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PandaRenderTraversalTest {
    @Test
    public void rendersPanelAndModalChildren() {
        RecordingRenderer renderer = new RecordingRenderer();
        PandaLayout root = PandaLayout.vertical(0)
            .add(PandaPanel.glass().title("Panel").add(PandaLabel.text("Panel child")))
            .add(PandaModal.titled("Modal").add(PandaButton.primary("Modal child")));
        root.layout(new PandaRect(0, 0, 400, 400));

        PandaRenderTraversal.render(renderer, root, 10, 10);

        assertEquals("panel,label,modal,button:false", renderer.calls());
    }

    @Test
    public void skipsClosedAndHiddenModals() {
        RecordingRenderer renderer = new RecordingRenderer();
        PandaModal closed = PandaModal.titled("Closed").add(PandaLabel.text("Closed child"));
        PandaModal hidden = PandaModal.titled("Hidden").add(PandaLabel.text("Hidden child"));
        closed.close();
        hidden.visible(false);
        PandaLayout root = PandaLayout.vertical(0).add(closed).add(hidden);
        root.layout(new PandaRect(0, 0, 400, 400));

        PandaRenderTraversal.render(renderer, root, 10, 10);

        assertEquals("", renderer.calls());
    }

    @Test
    public void rendersToastComponents() {
        RecordingRenderer renderer = new RecordingRenderer();
        PandaLayout root = PandaLayout.vertical(0).add(PandaToast.message("Saved"));
        root.layout(new PandaRect(0, 0, 400, 400));

        PandaRenderTraversal.render(renderer, root, 10, 10);

        assertEquals("toast:Saved", renderer.calls());
    }

    @Test
    public void renderErrorInOneComponentDoesNotStopSiblings() {
        RecordingRenderer renderer = new RecordingRenderer();
        renderer.throwOnLabel = true;
        PandaLayout root = PandaLayout.vertical(0)
            .add(PandaLabel.text("Broken"))
            .add(PandaButton.primary("Still renders"));
        root.layout(new PandaRect(0, 0, 400, 400));

        PandaRenderTraversal.render(renderer, root, 10, 10);

        assertEquals("button:false", renderer.calls());
    }

    private static final class RecordingRenderer implements PandaRenderer {
        private final List<String> calls = new ArrayList<String>();
        private boolean throwOnLabel;

        public void panel(PandaPanel panel) {
            calls.add("panel");
        }

        public void button(PandaButton button, boolean hovered) {
            calls.add("button:" + hovered);
        }

        public void label(PandaLabel label) {
            if (throwOnLabel) {
                throw new RuntimeException("label render failed");
            }
            calls.add("label");
        }

        public void icon(PandaIcon icon) {
            calls.add("icon");
        }

        public void list(PandaList list, int hoveredIndex) {
            calls.add("list:" + hoveredIndex);
        }

        public void progress(PandaProgressBar progressBar) {
            calls.add("progress");
        }

        public void slider(PandaSlider slider, boolean hovered) {
            calls.add("slider:" + hovered);
        }

        public void hudBar(PandaHudBar hudBar) {
            calls.add("hudBar");
        }

        public void tabs(PandaTabs tabs, int hoveredIndex) {
            calls.add("tabs:" + hoveredIndex);
        }

        public void modal(PandaModal modal) {
            calls.add("modal");
        }

        public void toast(PandaToast toast) {
            calls.add("toast:" + toast.message());
        }

        private String calls() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < calls.size(); i++) {
                if (i > 0) {
                    builder.append(',');
                }
                builder.append(calls.get(i));
            }
            return builder.toString();
        }
    }
}
