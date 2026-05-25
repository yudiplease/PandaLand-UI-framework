package land.pandaland.ui.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PandaIconTest {
    @Test
    public void iconKeepsTextureAndMinimumSize() {
        PandaIcon icon = PandaIcon.texture("pandaland_ui", "textures/icons/demo.png");

        assertEquals("pandaland_ui:textures/icons/demo.png", icon.texture().toString());
        assertEquals(16, icon.preferredWidth());
        assertEquals(16, icon.preferredHeight());
    }
}
