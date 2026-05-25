package land.pandaland.ui.runtime;

import java.util.logging.Level;
import java.util.logging.Logger;
import land.pandaland.ui.api.PandaComponent;
import land.pandaland.ui.api.PandaHudOverlay;
import land.pandaland.ui.api.PandaRenderer;

public final class PandaUiErrorHandler {
    public static final Logger LOGGER = Logger.getLogger("land.pandaland.ui");

    private PandaUiErrorHandler() {
    }

    public static void update(PandaComponent component, long deltaMs) {
        try {
            component.update(deltaMs);
        } catch (RuntimeException error) {
            log("update", component, error);
        }
    }

    public static boolean mousePressed(PandaComponent component, int mouseX, int mouseY, int button) {
        try {
            return component.mousePressed(mouseX, mouseY, button);
        } catch (RuntimeException error) {
            log("mousePressed", component, error);
            return false;
        }
    }

    public static boolean mouseReleased(PandaComponent component, int mouseX, int mouseY, int button) {
        try {
            return component.mouseReleased(mouseX, mouseY, button);
        } catch (RuntimeException error) {
            log("mouseReleased", component, error);
            return false;
        }
    }

    public static boolean mouseDragged(PandaComponent component, int mouseX, int mouseY, int button, long dragTimeMs) {
        try {
            return component.mouseDragged(mouseX, mouseY, button, dragTimeMs);
        } catch (RuntimeException error) {
            log("mouseDragged", component, error);
            return false;
        }
    }

    public static boolean mouseClicked(PandaComponent component, int mouseX, int mouseY, int button) {
        try {
            return component.mouseClicked(mouseX, mouseY, button);
        } catch (RuntimeException error) {
            log("mouseClicked", component, error);
            return false;
        }
    }

    public static boolean keyTyped(PandaComponent component, char character, int keyCode) {
        try {
            return component.keyTyped(character, keyCode);
        } catch (RuntimeException error) {
            log("keyTyped", component, error);
            return false;
        }
    }

    public static void overlayUpdate(PandaHudOverlay overlay, long deltaMs) {
        try {
            overlay.update(deltaMs);
        } catch (RuntimeException error) {
            log("hud update", overlay, error);
        }
    }

    public static void overlayRender(PandaHudOverlay overlay, PandaRenderer renderer) {
        try {
            overlay.render(renderer);
        } catch (RuntimeException error) {
            log("hud render", overlay, error);
        }
    }

    public static void log(String operation, Object context, RuntimeException error) {
        String contextName = context == null ? "unknown" : context.getClass().getName();
        LOGGER.log(Level.WARNING, "Panda UI " + operation + " failed for " + contextName, error);
    }
}
