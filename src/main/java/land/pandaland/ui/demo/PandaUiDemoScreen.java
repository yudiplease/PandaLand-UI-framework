package land.pandaland.ui.demo;

import land.pandaland.ui.api.PandaButton;
import land.pandaland.ui.api.PandaLabel;
import land.pandaland.ui.api.PandaLayout;
import land.pandaland.ui.api.PandaModal;
import land.pandaland.ui.api.PandaScreen;

public final class PandaUiDemoScreen extends PandaScreen {
    @Override
    public void build(PandaLayout root) {
        root.add(PandaLabel.text("PandaLand UI"));
        root.add(PandaButton.secondary("Open modal").onClick(new Runnable() {
            public void run() {
            }
        }));
        showModal(PandaModal.titled("Preview").add(PandaLabel.text("Modal preview")));
    }
}
