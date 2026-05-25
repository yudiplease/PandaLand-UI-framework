package land.pandaland.ui.runtime;

import java.util.ArrayList;
import java.util.List;
import land.pandaland.ui.api.PandaComponent;

public final class PandaInputDispatcher {
    private final List<PandaComponent> components = new ArrayList<PandaComponent>();

    public void add(PandaComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("component cannot be null");
        }
        components.add(component);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return dispatch(components, mouseX, mouseY, button);
    }

    public static boolean dispatch(List<PandaComponent> components, int mouseX, int mouseY, int button) {
        List<PandaComponent> snapshot = new ArrayList<PandaComponent>(components);
        for (int i = snapshot.size() - 1; i >= 0; i--) {
            PandaComponent component = snapshot.get(i);
            if (component.visible() && component.enabled() && component.bounds().contains(mouseX, mouseY)) {
                if (PandaUiErrorHandler.mouseClicked(component, mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }
}
