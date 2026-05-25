package land.pandaland.ui.v2.style;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class UiThemeTest {
    @Test
    public void defaultThemeUsesMinecraftRusFontAndSoftRadii() {
        UiTheme theme = UiTheme.pandalandDefault();

        assertEquals("pandaland_ui:fonts/minecraft-rus.ttf", theme.font());
        assertTrue(theme.buttonRadius() >= 10);
        assertTrue(theme.panelRadius() >= 12);
    }

    @Test
    public void reducedMotionSnapsTransitionToTarget() {
        UiTransition transition = UiTransition.smooth(0.0F, 120);
        transition.setTarget(1.0F);
        transition.update(16L, true);

        assertEquals(1.0F, transition.value(), 0.001F);
    }
}
