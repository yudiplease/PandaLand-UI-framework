package land.pandaland.ui.api;

import land.pandaland.ui.api.PandaRect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PandaTabsTest {
    @Test
    public void selectedIndexIsClampedToAvailableTabs() {
        PandaTabs tabs = new PandaTabs()
            .addTab("Main")
            .addTab("Profile");

        tabs.selectedIndex(5);
        assertEquals(1, tabs.selectedIndex());

        tabs.selectedIndex(-10);
        assertEquals(0, tabs.selectedIndex());
    }

    @Test
    public void addingFirstTabClampsPreviousSelection() {
        PandaTabs tabs = new PandaTabs();

        tabs.selectedIndex(4).addTab("Main");

        assertEquals(0, tabs.selectedIndex());
    }

    @Test
    public void leftClickSelectsTabByEqualWidthSlot() {
        PandaTabs tabs = new PandaTabs()
            .addTab("Main")
            .addTab("Profile")
            .addTab("Settings");
        tabs.setBounds(new PandaRect(10, 20, 300, 28));

        assertTrue(tabs.mouseClicked(150, 25, 0));

        assertEquals(1, tabs.selectedIndex());
    }

    @Test
    public void clickIgnoresOutsideBoundsAndRightButton() {
        PandaTabs tabs = new PandaTabs()
            .addTab("Main")
            .addTab("Profile");
        tabs.setBounds(new PandaRect(10, 20, 200, 28));

        assertFalse(tabs.mouseClicked(15, 25, 1));
        assertEquals(0, tabs.selectedIndex());

        assertFalse(tabs.mouseClicked(250, 25, 0));
        assertEquals(0, tabs.selectedIndex());
    }

    @Test
    public void hiddenTabsIgnoreDirectClicks() {
        PandaTabs tabs = new PandaTabs()
            .addTab("Main")
            .addTab("Profile");
        tabs.setBounds(new PandaRect(10, 20, 200, 28));
        tabs.visible(false);

        assertFalse(tabs.mouseClicked(150, 25, 0));
        assertEquals(0, tabs.selectedIndex());
    }

    @Test
    public void disabledTabsIgnoreDirectClicks() {
        PandaTabs tabs = new PandaTabs()
            .addTab("Main")
            .addTab("Profile");
        tabs.setBounds(new PandaRect(10, 20, 200, 28));
        tabs.enabled(false);

        assertFalse(tabs.mouseClicked(150, 25, 0));
        assertEquals(0, tabs.selectedIndex());
    }
}
