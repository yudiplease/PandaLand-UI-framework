package land.pandaland.ui.api;

/**
 * Factory methods for standard PandaLand UI animation timings.
 */
public final class PandaAnimations {
    private PandaAnimations() {
    }

    /** @return hover fade animation with the framework default duration */
    public static PandaAnimation hoverFade(float initial) {
        return new PandaAnimation(initial, 150L);
    }

    /** @return press feedback animation with the framework default duration */
    public static PandaAnimation pressFeedback(float initial) {
        return new PandaAnimation(initial, 80L);
    }

    /** @return modal transition animation with the framework default duration */
    public static PandaAnimation modalTransition(float initial) {
        return new PandaAnimation(initial, 160L);
    }

    /** @return progress interpolation animation with the framework default duration */
    public static PandaAnimation progress(float initial) {
        return new PandaAnimation(initial, 180L);
    }
}
