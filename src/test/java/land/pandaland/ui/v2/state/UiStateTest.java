package land.pandaland.ui.v2.state;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class UiStateTest {
    @Test
    public void notifiesSubscribersOnlyWhenValueChanges() {
        UiState<Integer> state = UiState.of(1);
        AtomicInteger observed = new AtomicInteger(0);
        AtomicInteger notifications = new AtomicInteger(0);

        state.subscribe(new UiSubscription<Integer>() {
            public void onChanged(Integer value) {
                observed.set(value.intValue());
                notifications.incrementAndGet();
            }
        });

        state.set(2);
        state.set(2);

        assertEquals(2, observed.get());
        assertEquals(1, notifications.get());
    }

    @Test
    public void computedValueUpdatesWhenSourceChanges() {
        UiState<Integer> source = UiState.of(10);
        UiComputed<Integer, String> text = UiComputed.from(source, new UiComputed.Mapper<Integer, String>() {
            public String map(Integer value) {
                return "Music: " + value + "%";
            }
        });

        assertEquals("Music: 10%", text.get());
        source.set(45);
        assertEquals("Music: 45%", text.get());
    }
}
