package land.pandaland.ui.api;

import land.pandaland.ui.api.PandaRect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PandaListTest {
    @Test
    public void rowsAreExposedReadOnlyAndSelectionIsClamped() {
        PandaList list = new PandaList()
            .addRow("Alpha")
            .addRow("Beta");

        list.selectedIndex(9);

        assertEquals(2, list.rows().size());
        assertEquals(1, list.selectedIndex());
    }

    @Test
    public void clickSelectsRowByVerticalSlot() {
        PandaList list = new PandaList()
            .addRow("Alpha")
            .addRow("Beta")
            .addRow("Gamma");
        list.setBounds(new PandaRect(10, 20, 120, 54));

        assertTrue(list.mouseClicked(20, 43, 0));

        assertEquals(1, list.selectedIndex());
    }

    @Test
    public void hiddenListIgnoresClicks() {
        PandaList list = new PandaList().addRow("Alpha");
        list.setBounds(new PandaRect(10, 20, 120, 18));
        list.visible(false);

        assertFalse(list.mouseClicked(20, 25, 0));
        assertEquals(0, list.selectedIndex());
    }
}
