package land.pandaland.ui.v2.event;

/**
 * Callback invoked when a selection-style control accepts a selected option.
 *
 * <p>The callback receives both the stable item id when metadata is available
 * and the resolved zero-based visual index. Legacy child-backed controls use
 * the child text as the id.</p>
 */
public interface UiSelectionHandler {
    /**
     * Handles a committed selection.
     *
     * @param id stable selected item id, or a best-effort legacy id
     * @param index zero-based selected item index
     */
    void onSelect(String id, int index);
}
