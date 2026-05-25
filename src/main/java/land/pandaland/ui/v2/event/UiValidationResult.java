package land.pandaland.ui.v2.event;

/**
 * Result returned by a text input validator.
 */
public final class UiValidationResult {
    private static final UiValidationResult VALID = new UiValidationResult(true, "");

    private final boolean valid;
    private final String message;

    private UiValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message == null ? "" : message;
    }

    /**
     * Returns a shared successful validation result.
     *
     * @return valid result
     */
    public static UiValidationResult ok() {
        return VALID;
    }

    /**
     * Creates a failed validation result.
     *
     * @param message user-facing validation message
     * @return invalid result
     */
    public static UiValidationResult invalid(String message) {
        return new UiValidationResult(false, message);
    }

    /**
     * Reports whether validation succeeded.
     *
     * @return {@code true} when the candidate is valid
     */
    public boolean valid() {
        return valid;
    }

    /**
     * Returns the validation message.
     *
     * @return message for invalid values, or an empty string
     */
    public String message() {
        return message;
    }
}
