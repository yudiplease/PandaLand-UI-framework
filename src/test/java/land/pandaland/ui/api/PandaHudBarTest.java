package land.pandaland.ui.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PandaHudBarTest {
    @Test
    public void valueIsClamped() {
        PandaHudBar bar = PandaHudBar.status("Energy", 2.0f);

        assertEquals(1.0f, bar.value(), 0.001f);

        bar.value(-1.0f);
        assertEquals(0.0f, bar.value(), 0.001f);
    }
}
