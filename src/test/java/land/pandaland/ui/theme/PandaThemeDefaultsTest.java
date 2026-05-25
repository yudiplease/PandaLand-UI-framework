package land.pandaland.ui.theme;

import land.pandaland.ui.api.PandaTheme;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PandaThemeDefaultsTest {
    @Test
    public void defaultThemeHasLiquidGlassButtonMetrics() {
        PandaTheme theme = PandaThemeDefaults.create();

        assertEquals(12, theme.buttonRadius());
        assertEquals(16, theme.panelRadius());
        assertEquals(8, theme.spacing());
        assertTrue(theme.primaryAccent().alpha() > 0);
    }
}
