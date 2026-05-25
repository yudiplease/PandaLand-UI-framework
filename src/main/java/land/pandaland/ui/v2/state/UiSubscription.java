package land.pandaland.ui.v2.state;

/**
 * Observer callback for {@link UiState} value changes.
 *
 * @param <T> observed value type
 */
public interface UiSubscription<T> {
    /**
     * Called after the observed state value changes.
     *
     * @param value new state value
     */
    void onChanged(T value);
}
