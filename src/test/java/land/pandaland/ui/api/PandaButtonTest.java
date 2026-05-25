package land.pandaland.ui.api;

import land.pandaland.ui.api.PandaRect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PandaButtonTest {
    @Test
    public void primaryButtonInvokesClickHandler() {
        final int[] clicks = {0};
        PandaButton button = PandaButton.primary("Play").onClick(new Runnable() {
            public void run() {
                clicks[0]++;
            }
        });

        button.setBounds(new PandaRect(0, 0, 100, 24));
        button.mouseClicked(10, 10, 0);

        assertEquals(1, clicks[0]);
    }

    @Test
    public void buttonOnlyHandlesLeftClickInsideBounds() {
        final int[] clicks = {0};
        PandaButton button = PandaButton.ghost("Cancel").onClick(new Runnable() {
            public void run() {
                clicks[0]++;
            }
        });
        button.setBounds(new PandaRect(10, 10, 100, 32));

        assertFalse(button.mouseClicked(20, 20, 1));
        assertFalse(button.mouseClicked(200, 200, 0));
        assertTrue(button.mouseClicked(20, 20, 0));
        assertEquals(1, clicks[0]);
    }

    @Test
    public void buttonKindsAndPreferredSizeAreExposed() {
        PandaButton button = PandaButton.danger("Delete");

        assertEquals(PandaButton.Kind.DANGER, button.kind());
        assertEquals("Delete", button.text());
        assertTrue(button.preferredWidth() >= 96);
        assertEquals(32, button.preferredHeight());
    }

    @Test
    public void directClickIgnoresDisabledButton() {
        final int[] clicks = {0};
        PandaButton button = PandaButton.primary("Play").onClick(new Runnable() {
            public void run() {
                clicks[0]++;
            }
        });
        button.setBounds(new PandaRect(0, 0, 100, 24));
        button.enabled(false);

        assertFalse(button.mouseClicked(10, 10, 0));
        assertEquals(0, clicks[0]);
    }

    @Test
    public void directClickIgnoresHiddenButton() {
        final int[] clicks = {0};
        PandaButton button = PandaButton.primary("Play").onClick(new Runnable() {
            public void run() {
                clicks[0]++;
            }
        });
        button.setBounds(new PandaRect(0, 0, 100, 24));
        button.visible(false);

        assertFalse(button.mouseClicked(10, 10, 0));
        assertEquals(0, clicks[0]);
    }
}
