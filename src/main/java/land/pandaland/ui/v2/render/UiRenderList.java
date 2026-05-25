package land.pandaland.ui.v2.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UiRenderList {
    private final List<UiRenderCommand> commands = new ArrayList<UiRenderCommand>();

    public void add(UiRenderCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command cannot be null");
        }
        commands.add(command);
    }

    public UiRenderCommand get(int index) {
        return commands.get(index);
    }

    public int size() {
        return commands.size();
    }

    public List<UiRenderCommand> commands() {
        return Collections.unmodifiableList(commands);
    }
}
