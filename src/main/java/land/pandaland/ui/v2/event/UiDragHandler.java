package land.pandaland.ui.v2.event;

/**
 * Callback for pointer drag events on a retained UI node.
 */
public interface UiDragHandler {
    /**
     * Handles a drag event.
     *
     * @param event pointer event with absolute coordinates and movement delta
     */
    void onDrag(UiPointerEvent event);
}
