package land.pandaland.ui.v2.state;

public interface UiSubscription<T> {
    void onChanged(T value);
}
