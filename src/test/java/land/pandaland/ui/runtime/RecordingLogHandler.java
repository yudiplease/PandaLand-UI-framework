package land.pandaland.ui.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

final class RecordingLogHandler extends Handler {
    private static final Logger LOGGER = Logger.getLogger("land.pandaland.ui");

    private final List<String> messages = new ArrayList<String>();
    private final Level previousLevel;
    private final boolean previousUseParentHandlers;

    private RecordingLogHandler() {
        previousLevel = LOGGER.getLevel();
        previousUseParentHandlers = LOGGER.getUseParentHandlers();
    }

    static RecordingLogHandler attach() {
        RecordingLogHandler handler = new RecordingLogHandler();
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(handler);
        return handler;
    }

    boolean contains(String text) {
        for (String message : messages) {
            if (message.contains(text)) {
                return true;
            }
        }
        return false;
    }

    void detach() {
        LOGGER.removeHandler(this);
        LOGGER.setLevel(previousLevel);
        LOGGER.setUseParentHandlers(previousUseParentHandlers);
    }

    public void publish(LogRecord record) {
        if (record == null) {
            return;
        }
        messages.add(record.getMessage() == null ? "" : record.getMessage());
    }

    public void flush() {
    }

    public void close() {
    }
}
