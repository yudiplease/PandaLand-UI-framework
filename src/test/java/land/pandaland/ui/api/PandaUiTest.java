package land.pandaland.ui.api;

import java.lang.reflect.Method;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PandaUiTest {
    @Test
    public void exposesDefaultTheme() {
        PandaTheme theme = PandaUi.theme();

        assertNotNull(theme);
        assertEquals(12, theme.buttonRadius());
        assertEquals(16, theme.panelRadius());
    }

    @Test
    public void runtimeHudStoreIsNotPartOfPublicApi() {
        String hiddenMethodName = "hud" + "Registry";
        for (Method method : PandaUi.class.getMethods()) {
            if (hiddenMethodName.equals(method.getName())) {
                fail("PandaUi must not expose the runtime HUD registry");
            }
        }
    }

    @Test
    public void hudOverlayRenderUsesApiRendererBoundary() throws Exception {
        Class<?> apiRenderer = Class.forName("land.pandaland.ui.api.PandaRenderer");

        assertNotNull(PandaHudOverlay.class.getMethod("render", apiRenderer));
    }

    @Test
    public void componentGeometryUsesApiRectBoundary() throws Exception {
        Class<?> apiRect = Class.forName("land.pandaland.ui.api.PandaRect");

        assertEquals(apiRect, PandaComponent.class.getMethod("bounds").getReturnType());
        assertNotNull(PandaComponent.class.getMethod("setBounds", apiRect));
    }

    @Test
    public void reducedMotionCanBeConfiguredThroughPandaUi() throws Exception {
        Method setter = PandaUi.class.getMethod("setReducedMotion", boolean.class);

        setter.invoke(null, Boolean.TRUE);
        assertTrue(PandaUi.theme().reducedMotion());

        setter.invoke(null, Boolean.FALSE);
        assertFalse(PandaUi.theme().reducedMotion());
    }
}
