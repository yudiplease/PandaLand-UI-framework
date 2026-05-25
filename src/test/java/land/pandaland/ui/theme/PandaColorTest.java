package land.pandaland.ui.theme;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PandaColorTest {
    @Test
    public void exposesRgbaChannelsAndConvertsToArgb() {
        PandaColor color = new PandaColor(0x11223344);

        assertEquals(0x11223344, color.rgba());
        assertEquals(0x44112233, color.toArgb());
        assertEquals(0x11, color.red());
        assertEquals(0x22, color.green());
        assertEquals(0x33, color.blue());
        assertEquals(0x44, color.alpha());
    }

    @Test
    public void valueEqualityUsesPackedRgba() {
        PandaColor first = new PandaColor(0x11223344);
        PandaColor second = new PandaColor(0x11223344);
        PandaColor different = new PandaColor(0x11223345);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
        assertEquals("PandaColor{rgba=0x11223344}", first.toString());
    }
}
