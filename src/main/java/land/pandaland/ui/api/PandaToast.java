package land.pandaland.ui.api;

/**
 * Timed toast notification component managed by {@link PandaUi}.
 */
public final class PandaToast extends PandaComponent {
    private static final long MIN_DURATION_MS = 1000L;
    private static final long DEFAULT_DURATION_MS = 3000L;

    private final String message;
    private final long durationMs;
    private long remainingMs;

    private PandaToast(String message, long durationMs) {
        this.message = message == null ? "" : message;
        this.durationMs = Math.max(MIN_DURATION_MS, durationMs);
        this.remainingMs = this.durationMs;
    }

    /**
     * Creates a toast with the default duration.
     *
     * @param message visible message
     * @return toast component
     */
    public static PandaToast message(String message) {
        return new PandaToast(message, DEFAULT_DURATION_MS);
    }

    /**
     * Creates a toast with a custom duration.
     *
     * @param message visible message
     * @param durationMs duration in milliseconds; values below the minimum are clamped
     * @return toast component
     */
    public static PandaToast message(String message, long durationMs) {
        return new PandaToast(message, durationMs);
    }

    /**
     * @return visible message text
     */
    public String message() {
        return message;
    }

    /**
     * @return configured duration in milliseconds
     */
    public long durationMs() {
        return durationMs;
    }

    /**
     * @return remaining lifetime in milliseconds
     */
    public long remainingMs() {
        return remainingMs;
    }

    /**
     * @return whether the toast lifetime has elapsed
     */
    public boolean expired() {
        return remainingMs <= 0L;
    }

    public int preferredWidth() {
        return Math.max(120, message.length() * 6 + 32);
    }

    public int preferredHeight() {
        return 32;
    }

    public void update(long deltaMs) {
        super.update(deltaMs);
        remainingMs = Math.max(0L, remainingMs - Math.max(0L, deltaMs));
    }
}
