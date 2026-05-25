package land.pandaland.ui.v2.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class UiState<T> {
    private final List<UiSubscription<T>> subscriptions = new ArrayList<UiSubscription<T>>();
    private T value;

    private UiState(T value) {
        this.value = value;
    }

    public static <T> UiState<T> of(T value) {
        return new UiState<T>(value);
    }

    public T get() {
        return value;
    }

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

    public void subscribe(UiSubscription<T> subscription) {
        subscriptions.add(Objects.requireNonNull(subscription, "subscription"));
    }

    public void unsubscribe(UiSubscription<T> subscription) {
        subscriptions.remove(subscription);
    }
}
