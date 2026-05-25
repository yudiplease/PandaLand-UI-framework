package land.pandaland.ui.v2.event;

/**
 * Validation callback for text input nodes.
 */
public interface UiTextValidator {
    /**
     * Validates a candidate text value before it is committed to state.
     *
     * @param candidate proposed input value
     * @return validation result; {@code null} is treated as valid by the dispatcher
     */
    UiValidationResult validate(String candidate);
}
