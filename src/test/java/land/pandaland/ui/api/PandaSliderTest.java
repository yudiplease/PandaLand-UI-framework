package land.pandaland.ui.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PandaSliderTest {
    @Test
    public void dragUpdatesValueAndClampsToTrack() {
        final float[] changedValue = new float[] {-1.0F};
        PandaSlider slider = PandaSlider.value("Scale", "50%", 0.5F).onChange(new PandaSlider.ValueChange() {
            public void changed(float value) {
                changedValue[0] = value;
            }
        });
        slider.layout(new PandaRect(10, 20, 100, 22));

        assertTrue(slider.mousePressed(60, 25, 0));
        assertEquals(0.5F, slider.value(), 0.0001F);

        assertTrue(slider.mouseDragged(110, 25, 0, 16L));
        assertEquals(1.0F, slider.value(), 0.0001F);
        assertEquals(1.0F, changedValue[0], 0.0001F);

        assertTrue(slider.mouseDragged(-30, 25, 0, 16L));
        assertEquals(0.0F, slider.value(), 0.0001F);
        assertEquals(0.0F, changedValue[0], 0.0001F);
    }

    @Test
    public void valueTextCanBeUpdatedAfterValueChange() {
        PandaSlider slider = PandaSlider.value("Scale", "50%", 0.5F);

        slider.valueText("75%");

        assertEquals("75%", slider.valueText());
    }
}
