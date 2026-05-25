package land.pandaland.ui.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PandaAnimationsTest {
    @Test
    public void progressAnimationUsesFrameworkPrimitive() {
        PandaAnimation animation = PandaAnimations.progress(0.0f);

        animation.setTarget(1.0f);
        animation.update(1000L);

        assertEquals(1.0f, animation.value(), 0.001f);
    }
}
