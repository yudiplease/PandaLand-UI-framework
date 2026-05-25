package land.pandaland.ui.v2.event;

public final class UiKeyEvent {
    public final char character;
    public final int keyCode;

    public UiKeyEvent(char character, int keyCode) {
        this.character = character;
        this.keyCode = keyCode;
    }
}
