package land.pandaland.ui.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PandaProgressBarTest {
    @Test
    public void progressValueIsClamped() {
        assertEquals(1.0f, PandaProgressBar.of(2.0f).value(), 0.001f);
        assertEquals(0.0f, PandaProgressBar.of(-1.0f).value(), 0.001f);
    }

    @Test
    public void progressValueCanBeUpdatedInPlaceAndClamped() {
        PandaProgressBar progress = PandaProgressBar.of(0.25f);

        assertEquals(progress, progress.value(2.0f));
        assertEquals(1.0f, progress.value(), 0.001f);

        progress.value(-1.0f);
        assertEquals(0.0f, progress.value(), 0.001f);
    }

    @Test
    public void progressLabelIsOptional() {
        assertEquals("", PandaProgressBar.of(0.5f).label(null).label());
        assertEquals("Loading", PandaProgressBar.of(0.5f).label("Loading").label());
    }

    @Test
    public void displayValueInterpolatesTowardTargetValue() {
        PandaProgressBar progress = PandaProgressBar.of(0.0f);

        progress.value(1.0f);
        assertEquals(0.0f, progress.displayValue(), 0.001f);

        progress.update(90L);
        assertTrue(progress.displayValue() > 0.0f);
        assertTrue(progress.displayValue() < 1.0f);

        progress.update(180L);
        assertEquals(1.0f, progress.displayValue(), 0.001f);
    }

    @Test
    public void reducedMotionSnapsDisplayValueToTarget() {
        PandaProgressBar progress = PandaProgressBar.of(0.0f).reducedMotion(true);

        progress.value(1.0f);
        progress.update(1L);

        assertEquals(1.0f, progress.displayValue(), 0.001f);
    }

    @Test
    public void progressUsesGlobalReducedMotionDefault() {
        PandaUi.setReducedMotion(true);
        try {
            PandaProgressBar progress = PandaProgressBar.of(0.0f);

            progress.value(1.0f);
            progress.update(1L);

            assertEquals(1.0f, progress.displayValue(), 0.001f);
        } finally {
            PandaUi.setReducedMotion(false);
        }
    }
}
