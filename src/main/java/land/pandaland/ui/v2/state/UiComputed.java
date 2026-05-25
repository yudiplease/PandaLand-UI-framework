package land.pandaland.ui.v2.state;

import java.util.Objects;

/**
 * Read-only value derived from another {@link UiState}.
 *
 * @param <S> source value type
 * @param <T> computed value type
 */
public final class UiComputed<S, T> {
    private final Mapper<S, T> mapper;
    private T value;

    /**
     * Maps source values to computed values.
     *
     * @param <S> source value type
     * @param <T> computed value type
     */
    public interface Mapper<S, T> {
        /**
         * Computes a value from the source state value.
         *
         * @param value source value
         * @return computed value
         */
        T map(S value);
    }

    private UiComputed(UiState<S> source, Mapper<S, T> mapper) {
        this.mapper = Objects.requireNonNull(mapper, "mapper");
        this.value = this.mapper.map(source.get());
        source.subscribe(new UiSubscription<S>() {
            /**
             * Recomputes the derived value when the source changes.
             *
             * @param value new source value
             */
            public void onChanged(S value) {
                UiComputed.this.value = UiComputed.this.mapper.map(value);
            }
        });
    }

    /**
     * Creates a computed value that updates when the source state changes.
     *
     * @param source source state
     * @param mapper mapping callback
     * @param <S> source value type
     * @param <T> computed value type
     * @return computed value
     */
    public static <S, T> UiComputed<S, T> from(UiState<S> source, Mapper<S, T> mapper) {
        return new UiComputed<S, T>(Objects.requireNonNull(source, "source"), mapper);
    }

    /**
     * Returns the current computed value.
     *
     * @return computed value
     */
    public T get() {
        return value;
    }
}
