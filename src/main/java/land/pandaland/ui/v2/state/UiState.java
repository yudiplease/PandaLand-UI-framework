package land.pandaland.ui.v2.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Observable mutable value used by retained UI nodes.
 *
 * <p>State notifications are emitted only when the new value differs according
 * to {@link Objects#equals(Object, Object)}.</p>
 *
 * @param <T> value type
 */
public final class UiState<T> {
    private final List<UiSubscription<T>> subscriptions = new ArrayList<UiSubscription<T>>();
    private T value;

    private UiState(T value) {
        this.value = value;
    }

    /**
     * Creates observable state with an initial value.
     *
     * @param value initial value
     * @param <T> value type
     * @return new state instance
     */
    public static <T> UiState<T> of(T value) {
        return new UiState<T>(value);
    }

    /**
     * Returns the current value.
     *
     * @return current value
     */
    public T get() {
        return value;
    }

    /**
     * Sets a new value and notifies subscribers if it changed.
     *
     * @param value new value
     */
    public void set(T value) {
        if (Objects.equals(this.value, value)) {
            return;
        }

        this.value = value;
        List<UiSubscription<T>> snapshot = new ArrayList<UiSubscription<T>>(subscriptions);
        for (UiSubscription<T> subscription : snapshot) {
            subscription.onChanged(value);
        }
    }

    /**
     * Adds a subscriber.
     *
     * @param subscription subscription callback
     */
    public void subscribe(UiSubscription<T> subscription) {
        subscriptions.add(Objects.requireNonNull(subscription, "subscription"));
    }

    /**
     * Removes a subscriber.
     *
     * @param subscription subscription callback to remove
     */
    public void unsubscribe(UiSubscription<T> subscription) {
        subscriptions.remove(subscription);
    }
}
