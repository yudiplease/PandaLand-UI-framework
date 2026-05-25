package land.pandaland.ui.v2.state;

import java.util.Objects;

public final class UiComputed<S, T> {
    private final Mapper<S, T> mapper;
    private T value;

    public interface Mapper<S, T> {
        T map(S value);
    }

    private UiComputed(UiState<S> source, Mapper<S, T> mapper) {
        this.mapper = Objects.requireNonNull(mapper, "mapper");
        this.value = this.mapper.map(source.get());
        source.subscribe(new UiSubscription<S>() {
            public void onChanged(S value) {
                UiComputed.this.value = UiComputed.this.mapper.map(value);
            }
        });
    }

    public static <S, T> UiComputed<S, T> from(UiState<S> source, Mapper<S, T> mapper) {
        return new UiComputed<S, T>(Objects.requireNonNull(source, "source"), mapper);
    }

    public T get() {
        return value;
    }
}
