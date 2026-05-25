package land.pandaland.ui.runtime;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PandaAnimationTest {
    @Test
    public void movesTowardTargetWithClampedLinearProgress() {
        PandaAnimation animation = new PandaAnimation(0.0f, 100);

        animation.setTarget(1.0f);
        animation.update(50);
        assertEquals(0.5f, animation.value(), 0.001f);

        animation.update(500);
        assertEquals(1.0f, animation.value(), 0.001f);
    }

    @Test
    public void zeroDurationCompletesAfterFirstPositiveUpdate() {
        PandaAnimation animation = new PandaAnimation(0.0f, 0);

        animation.setTarget(1.0f);
        animation.update(1);

        assertEquals(1.0f, animation.value(), 0.001f);
    }

    @Test
    public void negativeDurationCompletesAfterFirstPositiveUpdate() {
        PandaAnimation animation = new PandaAnimation(0.0f, -100);

        animation.setTarget(1.0f);
        animation.update(1);

        assertEquals(1.0f, animation.value(), 0.001f);
    }

    @Test
    public void negativeDeltaDoesNotMoveAnimation() {
        PandaAnimation animation = new PandaAnimation(0.0f, 100);

        animation.setTarget(1.0f);
        animation.update(50);
        animation.update(-100);

        assertEquals(0.5f, animation.value(), 0.001f);
    }

    @Test
    public void retargetingStartsFromCurrentValue() {
        PandaAnimation animation = new PandaAnimation(0.0f, 100);

        animation.setTarget(1.0f);
        animation.update(50);
        animation.setTarget(0.0f);
        animation.update(50);

        assertEquals(0.25f, animation.value(), 0.001f);
    }
}
