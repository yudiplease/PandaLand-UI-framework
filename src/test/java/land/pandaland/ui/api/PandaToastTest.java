package land.pandaland.ui.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PandaToastTest {
    @Test
    public void toastStoresMessageAndDefaultDuration() {
        PandaToast toast = PandaToast.message("Saved");

        assertEquals("Saved", toast.message());
        assertEquals(3000L, toast.durationMs());
    }

    @Test
    public void toastDurationHasMinimum() {
        assertEquals(1000L, PandaToast.message("Saved", 20L).durationMs());
    }

    @Test
    public void toastExpiresAfterDurationUpdates() {
        PandaToast toast = PandaToast.message("Saved", 1000L);

        toast.update(999L);
        assertFalse(toast.expired());

        toast.update(1L);
        assertTrue(toast.expired());
    }

    @Test
    public void pandaUiToastFacadeOwnsLifecycle() {
        PandaUi.clearToasts();
        PandaToast toast = PandaUi.toast("Saved", 1000L);

        assertEquals(1, PandaUi.activeToasts().size());
        assertEquals(toast, PandaUi.activeToasts().get(0));

        PandaUi.updateHud(1000L);

        assertTrue(toast.expired());
        assertTrue(PandaUi.activeToasts().isEmpty());
    }
}
