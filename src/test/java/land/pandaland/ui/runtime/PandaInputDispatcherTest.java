package land.pandaland.ui.runtime;

import java.util.ArrayList;
import java.util.List;
import land.pandaland.ui.api.PandaComponent;
import land.pandaland.ui.api.PandaRect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PandaInputDispatcherTest {
    @Test
    public void clickGoesToTopmostEnabledComponent() {
        CountingComponent bottom = new CountingComponent();
        CountingComponent top = new CountingComponent();
        bottom.setBounds(new PandaRect(0, 0, 100, 100));
        top.setBounds(new PandaRect(0, 0, 100, 100));

        PandaInputDispatcher dispatcher = new PandaInputDispatcher();
        dispatcher.add(bottom);
        dispatcher.add(top);
        boolean handled = dispatcher.mouseClicked(20, 20, 0);

        assertTrue(handled);
        assertEquals(0, bottom.clicks);
        assertEquals(1, top.clicks);
    }

    @Test
    public void clickSkipsHiddenAndDisabledComponents() {
        CountingComponent bottom = new CountingComponent();
        CountingComponent hidden = new CountingComponent();
        CountingComponent disabled = new CountingComponent();
        bottom.setBounds(new PandaRect(0, 0, 100, 100));
        hidden.setBounds(new PandaRect(0, 0, 100, 100));
        disabled.setBounds(new PandaRect(0, 0, 100, 100));
        hidden.visible(false);
        disabled.enabled(false);

        PandaInputDispatcher dispatcher = new PandaInputDispatcher();
        dispatcher.add(bottom);
        dispatcher.add(hidden);
        dispatcher.add(disabled);
        boolean handled = dispatcher.mouseClicked(20, 20, 0);

        assertTrue(handled);
        assertEquals(1, bottom.clicks);
        assertEquals(0, hidden.clicks);
        assertEquals(0, disabled.clicks);
    }

    @Test
    public void clickOutsideComponentsIsNotHandled() {
        CountingComponent component = new CountingComponent();
        component.setBounds(new PandaRect(0, 0, 100, 100));

        PandaInputDispatcher dispatcher = new PandaInputDispatcher();
        dispatcher.add(component);
        boolean handled = dispatcher.mouseClicked(200, 200, 0);

        assertFalse(handled);
        assertEquals(0, component.clicks);
    }

    @Test
    public void dispatchUsesSnapshotWhenComponentListMutatesDuringClick() {
        List<PandaComponent> components = new ArrayList<PandaComponent>();
        CountingComponent bottom = new CountingComponent();
        MutatingComponent top = new MutatingComponent(components);
        bottom.setBounds(new PandaRect(0, 0, 100, 100));
        top.setBounds(new PandaRect(0, 0, 100, 100));
        components.add(bottom);
        components.add(top);

        boolean handled = PandaInputDispatcher.dispatch(components, 20, 20, 0);

        assertTrue(handled);
        assertEquals(1, top.clicks);
        assertEquals(1, bottom.clicks);
        assertEquals(0, components.size());
    }

    @Test
    public void clickErrorInTopComponentDoesNotStopLowerComponents() {
        List<PandaComponent> components = new ArrayList<PandaComponent>();
        CountingComponent bottom = new CountingComponent();
        ThrowingClickComponent top = new ThrowingClickComponent();
        bottom.setBounds(new PandaRect(0, 0, 100, 100));
        top.setBounds(new PandaRect(0, 0, 100, 100));
        components.add(bottom);
        components.add(top);

        boolean handled = PandaInputDispatcher.dispatch(components, 20, 20, 0);

        assertTrue(handled);
        assertEquals(1, bottom.clicks);
    }

    @Test
    public void clickErrorIsLoggedWithComponentContext() {
        RecordingLogHandler logs = RecordingLogHandler.attach();
        List<PandaComponent> components = new ArrayList<PandaComponent>();
        ThrowingClickComponent component = new ThrowingClickComponent();
        component.setBounds(new PandaRect(0, 0, 100, 100));
        components.add(component);

        PandaInputDispatcher.dispatch(components, 20, 20, 0);

        assertTrue(logs.contains("mouseClicked"));
        assertTrue(logs.contains(ThrowingClickComponent.class.getName()));
        logs.detach();
    }

    private static final class CountingComponent extends PandaComponent {
        private int clicks;

        public boolean mouseClicked(int mouseX, int mouseY, int button) {
            clicks++;
            return true;
        }
    }

    private static final class MutatingComponent extends PandaComponent {
        private final List<PandaComponent> components;
        private int clicks;

        private MutatingComponent(List<PandaComponent> components) {
            this.components = components;
        }

        public boolean mouseClicked(int mouseX, int mouseY, int button) {
            clicks++;
            components.clear();
            return false;
        }
    }

    private static final class ThrowingClickComponent extends PandaComponent {
        public boolean mouseClicked(int mouseX, int mouseY, int button) {
            throw new RuntimeException("click failed");
        }
    }
}
