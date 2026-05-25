package land.pandaland.ui.api;

import java.lang.reflect.Method;
import org.junit.Test;

import static org.junit.Assert.assertSame;

public class PandaPublicApiTest {
    @Test
    public void themeReturnsApiColorTypes() throws Exception {
        assertReturnType(PandaTheme.class, "panelBase", PandaColor.class);
        assertReturnType(PandaTheme.class, "buttonBase", PandaColor.class);
        assertReturnType(PandaTheme.class, "primaryAccent", PandaColor.class);
        assertReturnType(PandaTheme.class, "dangerAccent", PandaColor.class);
        assertReturnType(PandaTheme.class, "textPrimary", PandaColor.class);
        assertReturnType(PandaTheme.class, "textMuted", PandaColor.class);
    }

    @Test
    public void animationsReturnApiAnimationTypes() throws Exception {
        assertReturnType(PandaAnimations.class, "hoverFade", PandaAnimation.class, float.class);
        assertReturnType(PandaAnimations.class, "pressFeedback", PandaAnimation.class, float.class);
        assertReturnType(PandaAnimations.class, "modalTransition", PandaAnimation.class, float.class);
        assertReturnType(PandaAnimations.class, "progress", PandaAnimation.class, float.class);
    }

    private static void assertReturnType(Class<?> owner, String methodName, Class<?> expected, Class<?>... parameters)
        throws Exception {
        Method method = owner.getMethod(methodName, parameters);
        assertSame(expected, method.getReturnType());
    }
}
