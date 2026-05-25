package land.pandaland.ui.v2.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ordered list of render commands produced by traversal.
 */
public final class UiRenderList {
    private final List<UiRenderCommand> commands = new ArrayList<UiRenderCommand>();

    /**
     * Adds a render command to the end of the list.
     *
     * @param command command to add
     */
    public void add(UiRenderCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command cannot be null");
        }
        commands.add(command);
    }

    /**
     * Returns a command by index.
     *
     * @param index command index
     * @return render command
     */
    public UiRenderCommand get(int index) {
        return commands.get(index);
    }

    /**
     * Returns command count.
     *
     * @return number of commands
     */
    public int size() {
        return commands.size();
    }

    /**
     * Returns immutable command list.
     *
     * @return commands in draw order
     */
    public List<UiRenderCommand> commands() {
        return Collections.unmodifiableList(commands);
    }
}
