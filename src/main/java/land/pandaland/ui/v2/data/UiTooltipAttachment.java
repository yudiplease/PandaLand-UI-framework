package land.pandaland.ui.v2.data;

/**
 * Immutable metadata describing which UI target a tooltip is attached to.
 */
public final class UiTooltipAttachment {
    private final String targetId;
    private final int offsetX;
    private final int offsetY;

    /**
     * Creates tooltip attachment metadata.
     *
     * @param targetId stable target id supplied by the feature mod or renderer
     * @param offsetX horizontal offset from the target
     * @param offsetY vertical offset from the target
     */
    public UiTooltipAttachment(String targetId, int offsetX, int offsetY) {
        this.targetId = targetId == null ? "" : targetId;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * Creates tooltip attachment metadata for a target anchor.
     *
     * @param targetId stable target id supplied by the feature mod or renderer
     * @param offsetX horizontal offset from the target
     * @param offsetY vertical offset from the target
     * @return attachment metadata
     */
    public static UiTooltipAttachment anchor(String targetId, int offsetX, int offsetY) {
        return new UiTooltipAttachment(targetId, offsetX, offsetY);
    }

    /**
     * Returns stable target id.
     *
     * @return target id, never {@code null}
     */
    public String targetId() {
        return targetId;
    }

    /**
     * Returns horizontal offset from the target.
     *
     * @return x offset
     */
    public int offsetX() {
        return offsetX;
    }

    /**
     * Returns vertical offset from the target.
     *
     * @return y offset
     */
    public int offsetY() {
        return offsetY;
    }
}
