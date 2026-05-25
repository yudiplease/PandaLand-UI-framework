package land.pandaland.ui.demo;

import java.lang.reflect.Field;
import land.pandaland.ui.api.PandaComponent;
import land.pandaland.ui.api.PandaLayout;
import land.pandaland.ui.api.PandaModal;
import land.pandaland.ui.api.PandaScreen;
import land.pandaland.ui.runtime.PandaScreenRuntime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PandaUiDemoScreenTest {
    @Test
    public void modalPreviewUsesRuntimeModalStackAndEscapeClosesIt() throws Exception {
        PandaUiDemoScreen screen = new PandaUiDemoScreen();
        PandaLayout root = PandaLayout.vertical(8);

        screen.build(root);

        PandaScreenRuntime runtime = runtimeOf(screen);
        assertEquals(1, runtime.modalCount());
        assertFalse(rootContainsModal(root));

        assertTrue(runtime.keyTyped('\0', PandaScreenRuntime.ESCAPE_KEY_CODE));
        assertEquals(0, runtime.modalCount());
    }

    private static PandaScreenRuntime runtimeOf(PandaScreen screen) throws Exception {
        Field field = PandaScreen.class.getDeclaredField("runtime");
        field.setAccessible(true);
        return (PandaScreenRuntime) field.get(screen);
    }

    private static boolean rootContainsModal(PandaLayout root) {
        for (PandaComponent child : root.children()) {
            if (child instanceof PandaModal) {
                return true;
            }
        }
        return false;
    }
}
