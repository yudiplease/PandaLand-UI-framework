package land.pandaland.ui.v2.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import land.pandaland.ui.v2.components.UiCustomComponent;
import land.pandaland.ui.v2.data.UiListItem;
import land.pandaland.ui.v2.data.UiModalOptions;
import land.pandaland.ui.v2.data.UiPage;
import land.pandaland.ui.v2.data.UiRichTextSpan;
import land.pandaland.ui.v2.data.UiTableColumn;
import land.pandaland.ui.v2.data.UiTableRow;
import land.pandaland.ui.v2.data.UiToastOptions;
import land.pandaland.ui.v2.data.UiTooltipAttachment;
import land.pandaland.ui.v2.data.UiTreeItem;
import land.pandaland.ui.v2.event.UiDragHandler;
import land.pandaland.ui.v2.event.UiSelectionHandler;
import land.pandaland.ui.v2.event.UiShortcut;
import land.pandaland.ui.v2.event.UiTextValidator;
import land.pandaland.ui.v2.layout.UiLayoutStyle;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.render.UiCustomDraw;
import land.pandaland.ui.v2.state.UiState;

/**
 * Retained node in a PandaLand UI Framework v2 tree.
 *
 * <p>A node stores widget type, layout style, bounds, children, bound state,
 * callbacks, focus flags, text input state, scroll offsets, and visibility
 * state. Mutator methods are fluent so low-level code and the public builder can
 * construct trees consistently.</p>
 */
public final class UiNode {
    /**
     * Widget or structural role of a node.
     */
    public enum Type {
        /** Root node of a screen tree. */
        ROOT,
        /** Generic structural stack used for row/column nesting. */
        STACK,
        /** Text or texture-only leaf. */
        LABEL,
        /** Clickable button. */
        BUTTON,
        /** Visual panel container. */
        PANEL,
        /** Numeric slider bound to a {@link Float} state in range {@code 0..1}. */
        SLIDER,
        /** Progress bar bound to a numeric state. */
        PROGRESS,
        /** Modal panel node shown by {@link UiRuntime}. */
        MODAL,
        /** Toast notification node shown by {@link UiRuntime}. */
        TOAST,

        /** Editable text input. */
        TEXT_INPUT,
        /** Boolean checkbox. */
        CHECKBOX,
        /** Select/dropdown control. */
        SELECT,
        /** List selection control. */
        LIST,
        /** Table-like collection node reserved for structured lists. */
        TABLE,
        /** Scrollable container. */
        SCROLL_CONTAINER,
        /** Tab selector control. */
        TABS,
        /** Tooltip panel. */
        TOOLTIP,
        /** Context menu panel. */
        CONTEXT_MENU,
        /** Form container used to group validated inputs. */
        FORM,
        /** Editable table-like collection with richer selection metadata. */
        DATA_GRID,
        /** Hierarchical tree selection control. */
        TREE,
        /** Rich text display control. */
        RICH_TEXT,
        /** Color value picker control. */
        COLOR_PICKER,
        /** Keyboard shortcut capture input. */
        KEYBIND_INPUT,
        /** Page stack container controlled by stable page ids. */
        PAGE_STACK,
        /** Renderer-independent canvas node backed by a custom draw hook. */
        CANVAS,
        /** Feature-owned custom component node. */
        CUSTOM_COMPONENT
    }

    private final Type type;
    private final List<UiNode> children = new ArrayList<UiNode>();

    private UiNode parent;
    private String text = "";
    private String placeholder = "";

    private Runnable clickAction;
    private Runnable dragAction;
    private UiDragHandler dragHandler;
    private Runnable enterAction;
    private Runnable changeAction;
    private UiSelectionHandler selectionHandler;
    private final List<UiShortcut> shortcuts = new ArrayList<UiShortcut>();

    @SuppressWarnings("rawtypes")
    private UiState valueState;

    private String texture = "";

    private long durationMs;
    private long elapsedMs;

    private boolean invalid;
    private boolean visible = true;
    private boolean enabled = true;
    private boolean focusable;

    private UiRect bounds = new UiRect(0, 0, 0, 0);
    private UiLayoutStyle layoutStyle = UiLayoutStyle.leaf();

    // Text input state
    private int cursorPosition;
    private int selectionStart;
    private int selectionEnd;
    private int horizontalScroll;
    private int maxLength = 256;
    private boolean password;
    private boolean textArea;
    private boolean numericInput;
    private boolean searchInput;
    private String inputMask = "";
    private UiTextValidator validator;
    private String validationMessage = "";
    private int scrollX;
    private int scrollY;
    private boolean open;
    private int openX;
    private int openY;
    private List<UiListItem> items = Collections.emptyList();
    private List<UiTableColumn> columns = Collections.emptyList();
    private List<UiTableRow> rows = Collections.emptyList();
    private List<UiTreeItem> treeItems = Collections.emptyList();
    private List<UiRichTextSpan> spans = Collections.emptyList();
    private List<UiPage> pages = Collections.emptyList();
    private List<String> selectedIds = Collections.emptyList();
    private boolean searchable;
    private boolean multiSelect;
    private boolean virtualized;
    private UiModalOptions modalOptions;
    private UiTooltipAttachment tooltipAttachment;
    private UiToastOptions toastOptions;
    private Object customComponent;
    private UiCustomDraw customDraw;

    /**
     * Creates a node of the specified type.
     *
     * @param type node type
     */
    public UiNode(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        this.type = type;
    }

    /**
     * Returns the node type.
     *
     * @return node type
     */
    public Type type() {
        return type;
    }

    /**
     * Sets node text.
     *
     * @param text visible text, or {@code null} for empty
     * @return this node
     */
    public UiNode text(String text) {
        this.text = text == null ? "" : text;
        invalidate();
        return this;
    }

    /**
     * Returns node text.
     *
     * @return text, never {@code null}
     */
    public String text() {
        return text;
    }

    /**
     * Sets placeholder text used by text input nodes.
     *
     * @param placeholder placeholder text, or {@code null} for empty
     * @return this node
     */
    public UiNode placeholder(String placeholder) {
        this.placeholder = placeholder == null ? "" : placeholder;
        invalidate();
        return this;
    }

    /**
     * Returns placeholder text.
     *
     * @return placeholder, never {@code null}
     */
    public String placeholder() {
        return placeholder;
    }

    /**
     * Sets click action.
     *
     * @param action action to run on activation
     * @return this node
     */
    public UiNode onClick(Runnable action) {
        this.clickAction = action;
        return this;
    }

    /**
     * Returns click action.
     *
     * @return action or {@code null}
     */
    public Runnable clickAction() {
        return clickAction;
    }

    /**
     * Sets simple drag action without coordinate details.
     *
     * @param action action to run during drag
     * @return this node
     */
    public UiNode onDrag(Runnable action) {
        this.dragAction = action;
        return this;
    }

    /**
     * Returns simple drag action.
     *
     * @return action or {@code null}
     */
    public Runnable dragAction() {
        return dragAction;
    }

    /**
     * Sets detailed drag handler.
     *
     * @param handler drag handler
     * @return this node
     */
    public UiNode onDrag(UiDragHandler handler) {
        this.dragHandler = handler;
        return this;
    }

    /**
     * Returns detailed drag handler.
     *
     * @return drag handler or {@code null}
     */
    public UiDragHandler dragHandler() {
        return dragHandler;
    }

    /**
     * Sets action invoked by Enter inside a text input.
     *
     * @param action enter action
     * @return this node
     */
    public UiNode onEnter(Runnable action) {
        this.enterAction = action;
        return this;
    }

    /**
     * Returns enter action.
     *
     * @return action or {@code null}
     */
    public Runnable enterAction() {
        return enterAction;
    }

    /**
     * Sets action invoked after a value changes.
     *
     * @param action change action
     * @return this node
     */
    public UiNode onChange(Runnable action) {
        this.changeAction = action;
        return this;
    }

    /**
     * Returns change action.
     *
     * @return action or {@code null}
     */
    public Runnable changeAction() {
        return changeAction;
    }

    /**
     * Sets the callback invoked after a selection-style control accepts a
     * selected item.
     *
     * @param handler selection callback
     * @return this node
     */
    public UiNode onSelection(UiSelectionHandler handler) {
        this.selectionHandler = handler;
        return this;
    }

    /**
     * Returns the selection callback.
     *
     * @return selection callback or {@code null}
     */
    public UiSelectionHandler selectionHandler() {
        return selectionHandler;
    }

    /**
     * Registers a keyboard shortcut on this node.
     *
     * <p>Root-node shortcuts are checked by the runtime dispatcher after
     * runtime shortcuts and before focused text input editing.</p>
     *
     * @param shortcut shortcut to register
     * @return this node
     */
    public UiNode registerShortcut(UiShortcut shortcut) {
        if (shortcut == null) {
            throw new IllegalArgumentException("shortcut cannot be null");
        }
        shortcuts.add(shortcut);
        return this;
    }

    /**
     * Returns immutable keyboard shortcuts registered on this node.
     *
     * @return shortcut registrations
     */
    public List<UiShortcut> shortcuts() {
        return Collections.unmodifiableList(shortcuts);
    }

    @SuppressWarnings("rawtypes")
    /**
     * Binds a mutable value state to this node.
     *
     * @param valueState state object
     * @return this node
     */
    public UiNode valueState(UiState valueState) {
        this.valueState = valueState;
        return this;
    }

    @SuppressWarnings("rawtypes")
    /**
     * Returns bound value state.
     *
     * @return state object or {@code null}
     */
    public UiState valueState() {
        return valueState;
    }

    /**
     * Sets texture resource id for texture rendering.
     *
     * @param texture texture resource id
     * @return this node
     */
    public UiNode texture(String texture) {
        this.texture = texture == null ? "" : texture;
        invalidate();
        return this;
    }

    /**
     * Returns texture resource id.
     *
     * @return texture id, never {@code null}
     */
    public String texture() {
        return texture;
    }

    /**
     * Sets toast duration.
     *
     * @param durationMs duration in milliseconds
     * @return this node
     */
    public UiNode durationMs(long durationMs) {
        this.durationMs = Math.max(0L, durationMs);
        return this;
    }

    /**
     * Returns toast duration.
     *
     * @return duration in milliseconds
     */
    public long durationMs() {
        return durationMs;
    }

    /**
     * Advances elapsed lifetime for expiring nodes.
     *
     * @param deltaMs elapsed milliseconds
     */
    public void updateElapsed(long deltaMs) {
        elapsedMs += Math.max(0L, deltaMs);
    }

    /**
     * Reports whether the node lifetime has elapsed.
     *
     * @return {@code true} when the node has expired
     */
    public boolean expired() {
        return durationMs > 0L && elapsedMs >= durationMs;
    }

    /**
     * Adds a child node.
     *
     * @param child child to add
     * @return this node
     */
    public UiNode add(UiNode child) {
        if (child == null) {
            throw new IllegalArgumentException("child cannot be null");
        }
        if (child.parent != null) {
            throw new IllegalArgumentException("child already has parent");
        }
        child.parent = this;
        children.add(child);
        invalidate();
        return this;
    }

    /**
     * Returns immutable child list.
     *
     * @return children in draw/layout order
     */
    public List<UiNode> children() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Returns parent node.
     *
     * @return parent or {@code null}
     */
    public UiNode parent() {
        return parent;
    }

    /**
     * Marks this node and ancestors as needing refresh.
     */
    public void invalidate() {
        invalid = true;
        if (parent != null) {
            parent.invalidate();
        }
    }

    /**
     * Reports whether this node is invalid.
     *
     * @return invalid flag
     */
    public boolean invalid() {
        return invalid;
    }

    /**
     * Clears invalid flags on this node and descendants.
     */
    public void clearInvalid() {
        invalid = false;
        for (UiNode child : children) {
            child.clearInvalid();
        }
    }

    /**
     * Returns this node's layout style.
     *
     * @return layout style
     */
    public UiLayoutStyle layoutStyle() {
        return layoutStyle;
    }

    /**
     * Replaces this node's layout style.
     *
     * @param layoutStyle new layout style
     * @return this node
     */
    public UiNode layoutStyle(UiLayoutStyle layoutStyle) {
        if (layoutStyle == null) {
            throw new IllegalArgumentException("layoutStyle cannot be null");
        }
        this.layoutStyle = layoutStyle;
        invalidate();
        return this;
    }

    /**
     * Returns the last applied layout bounds.
     *
     * @return bounds in scaled GUI pixels
     */
    public UiRect bounds() {
        return bounds;
    }

    /**
     * Sets layout bounds.
     *
     * @param bounds bounds in scaled GUI pixels
     * @return this node
     */
    public UiNode bounds(UiRect bounds) {
        this.bounds = bounds == null ? new UiRect(0, 0, 0, 0) : bounds;
        return this;
    }

    /**
     * Reports whether this node should be rendered and hit-tested.
     *
     * @return visible flag
     */
    public boolean visible() {
        return visible;
    }

    /**
     * Sets visibility.
     *
     * @param visible visible flag
     * @return this node
     */
    public UiNode visible(boolean visible) {
        this.visible = visible;
        return this;
    }

    /**
     * Reports whether this node accepts input.
     *
     * @return enabled flag
     */
    public boolean enabled() {
        return enabled;
    }

    /**
     * Sets enabled state.
     *
     * @param enabled enabled flag
     * @return this node
     */
    public UiNode enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Reports whether this node can receive keyboard focus.
     *
     * @return focusable flag
     */
    public boolean focusable() {
        return focusable;
    }

    /**
     * Sets focusable state.
     *
     * @param focusable focusable flag
     * @return this node
     */
    public UiNode focusable(boolean focusable) {
        this.focusable = focusable;
        return this;
    }

    /**
     * Returns text input cursor position.
     *
     * @return cursor index
     */
    public int cursorPosition() {
        return cursorPosition;
    }

    /**
     * Sets text input cursor position.
     *
     * @param cursorPosition cursor index
     * @return this node
     */
    public UiNode cursorPosition(int cursorPosition) {
        this.cursorPosition = Math.max(0, cursorPosition);
        if (!hasSelection()) {
            selectionStart = this.cursorPosition;
            selectionEnd = this.cursorPosition;
        }
        return this;
    }

    /**
     * Returns raw selection start index.
     *
     * @return selection start
     */
    public int selectionStart() {
        return selectionStart;
    }

    /**
     * Sets raw selection start index.
     *
     * @param selectionStart selection start
     * @return this node
     */
    public UiNode selectionStart(int selectionStart) {
        this.selectionStart = Math.max(0, selectionStart);
        return this;
    }

    /**
     * Returns raw selection end index.
     *
     * @return selection end
     */
    public int selectionEnd() {
        return selectionEnd;
    }

    /**
     * Sets raw selection end index.
     *
     * @param selectionEnd selection end
     * @return this node
     */
    public UiNode selectionEnd(int selectionEnd) {
        this.selectionEnd = Math.max(0, selectionEnd);
        return this;
    }

    /**
     * Sets text selection range.
     *
     * @param selectionStart selection anchor
     * @param selectionEnd selection active end
     * @return this node
     */
    public UiNode selection(int selectionStart, int selectionEnd) {
        this.selectionStart = Math.max(0, selectionStart);
        this.selectionEnd = Math.max(0, selectionEnd);
        return this;
    }

    /**
     * Clears text selection at the cursor.
     *
     * @return this node
     */
    public UiNode clearSelection() {
        selectionStart = cursorPosition;
        selectionEnd = cursorPosition;
        return this;
    }

    /**
     * Reports whether a non-empty text selection exists.
     *
     * @return {@code true} when selection start and end differ
     */
    public boolean hasSelection() {
        return selectionStart != selectionEnd;
    }

    /**
     * Returns the lower selection boundary.
     *
     * @return minimum selected index
     */
    public int selectionMin() {
        return Math.min(selectionStart, selectionEnd);
    }

    /**
     * Returns the upper selection boundary.
     *
     * @return maximum selected index
     */
    public int selectionMax() {
        return Math.max(selectionStart, selectionEnd);
    }

    /**
     * Returns horizontal text input scroll offset.
     *
     * @return scroll offset in pixels
     */
    public int horizontalScroll() {
        return horizontalScroll;
    }

    /**
     * Sets horizontal text input scroll offset.
     *
     * @param horizontalScroll scroll offset in pixels
     * @return this node
     */
    public UiNode horizontalScroll(int horizontalScroll) {
        this.horizontalScroll = Math.max(0, horizontalScroll);
        return this;
    }

    /**
     * Returns maximum text input length.
     *
     * @return maximum number of characters
     */
    public int maxLength() {
        return maxLength;
    }

    /**
     * Sets maximum text input length.
     *
     * @param maxLength maximum number of characters
     * @return this node
     */
    public UiNode maxLength(int maxLength) {
        this.maxLength = Math.max(0, maxLength);
        return this;
    }

    /**
     * Reports whether text input should be masked.
     *
     * @return password flag
     */
    public boolean password() {
        return password;
    }

    /**
     * Sets password masking for text input.
     *
     * @param password password flag
     * @return this node
     */
    public UiNode password(boolean password) {
        this.password = password;
        invalidate();
        return this;
    }

    /**
     * Reports whether this text input accepts multiple lines.
     *
     * @return textarea flag
     */
    public boolean textArea() {
        return textArea;
    }

    /**
     * Sets textarea metadata for a text input node.
     *
     * @param textArea textarea flag
     * @return this node
     */
    public UiNode textArea(boolean textArea) {
        this.textArea = textArea;
        invalidate();
        return this;
    }

    /**
     * Reports whether this text input should accept strict signed decimal text.
     *
     * @return numeric input flag
     */
    public boolean numericInput() {
        return numericInput;
    }

    /**
     * Sets numeric-input metadata for a text input node.
     *
     * <p>When enabled, the dispatcher accepts an optional leading sign, at most
     * one decimal point, and digits in all other positions.</p>
     *
     * @param numericInput numeric input flag
     * @return this node
     */
    public UiNode numericInput(boolean numericInput) {
        this.numericInput = numericInput;
        invalidate();
        return this;
    }

    /**
     * Reports whether this text input represents a search field.
     *
     * @return search input flag
     */
    public boolean searchInput() {
        return searchInput;
    }

    /**
     * Sets search-input metadata for a text input node.
     *
     * @param searchInput search input flag
     * @return this node
     */
    public UiNode searchInput(boolean searchInput) {
        this.searchInput = searchInput;
        invalidate();
        return this;
    }

    /**
     * Returns optional input mask metadata for renderer or feature validation.
     *
     * @return input mask, never {@code null}
     */
    public String inputMask() {
        return inputMask;
    }

    /**
     * Sets optional input mask metadata for a text input node.
     *
     * <p>The core dispatcher stores this value but keeps existing text-input
     * editing behavior unless another flag, such as numeric input, applies.</p>
     *
     * @param inputMask mask metadata, or {@code null} for no mask
     * @return this node
     */
    public UiNode inputMask(String inputMask) {
        this.inputMask = inputMask == null ? "" : inputMask;
        invalidate();
        return this;
    }

    /**
     * Returns text input validator.
     *
     * @return validator or {@code null}
     */
    public UiTextValidator validator() {
        return validator;
    }

    /**
     * Sets text input validator.
     *
     * @param validator validator callback
     * @return this node
     */
    public UiNode validator(UiTextValidator validator) {
        this.validator = validator;
        return this;
    }

    /**
     * Returns current validation message.
     *
     * @return validation message or an empty string
     */
    public String validationMessage() {
        return validationMessage;
    }

    /**
     * Sets validation message.
     *
     * @param validationMessage validation message
     * @return this node
     */
    public UiNode validationMessage(String validationMessage) {
        this.validationMessage = validationMessage == null ? "" : validationMessage;
        invalidate();
        return this;
    }

    /**
     * Reports whether the node currently has no validation message.
     *
     * @return {@code true} when valid
     */
    public boolean valid() {
        return validationMessage.length() == 0;
    }

    /**
     * Returns horizontal scroll offset for scroll containers.
     *
     * @return scroll x offset
     */
    public int scrollX() {
        return scrollX;
    }

    /**
     * Sets horizontal scroll offset for scroll containers.
     *
     * @param scrollX scroll x offset
     * @return this node
     */
    public UiNode scrollX(int scrollX) {
        this.scrollX = Math.max(0, scrollX);
        return this;
    }

    /**
     * Returns vertical scroll offset for scroll containers.
     *
     * @return scroll y offset
     */
    public int scrollY() {
        return scrollY;
    }

    /**
     * Sets vertical scroll offset for scroll containers.
     *
     * @param scrollY scroll y offset
     * @return this node
     */
    public UiNode scrollY(int scrollY) {
        this.scrollY = Math.max(0, scrollY);
        return this;
    }

    /**
     * Reports whether dropdown-like node state is open.
     *
     * @return open flag
     */
    public boolean open() {
        return open;
    }

    /**
     * Sets dropdown-like open state.
     *
     * @param open open flag
     * @return this node
     */
    public UiNode open(boolean open) {
        this.open = open;
        invalidate();
        return this;
    }

    /**
     * Opens this overlay-like node at a pointer position.
     *
     * <p>The position is renderer-independent metadata used by layout and event
     * code for context menus and similar popups.</p>
     *
     * @param x open x coordinate in scaled GUI pixels
     * @param y open y coordinate in scaled GUI pixels
     * @return this node
     */
    public UiNode openAt(int x, int y) {
        this.open = true;
        this.openX = x;
        this.openY = y;
        invalidate();
        return this;
    }

    /**
     * Returns the x coordinate captured when this node was opened.
     *
     * @return open x coordinate
     */
    public int openX() {
        return openX;
    }

    /**
     * Returns the y coordinate captured when this node was opened.
     *
     * @return open y coordinate
     */
    public int openY() {
        return openY;
    }

    /**
     * Returns immutable item metadata for list and select-like controls.
     *
     * @return item metadata, never {@code null}
     */
    public List<UiListItem> items() {
        return items;
    }

    /**
     * Sets item metadata for list and select-like controls.
     *
     * @param items item metadata
     * @return this node
     */
    public UiNode items(List<UiListItem> items) {
        this.items = copy(items);
        invalidate();
        return this;
    }

    /**
     * Returns immutable table column metadata.
     *
     * @return column metadata, never {@code null}
     */
    public List<UiTableColumn> columns() {
        return columns;
    }

    /**
     * Sets table column metadata.
     *
     * @param columns column metadata
     * @return this node
     */
    public UiNode columns(List<UiTableColumn> columns) {
        this.columns = copy(columns);
        invalidate();
        return this;
    }

    /**
     * Returns immutable table row metadata.
     *
     * @return row metadata, never {@code null}
     */
    public List<UiTableRow> rows() {
        return rows;
    }

    /**
     * Sets table row metadata.
     *
     * @param rows row metadata
     * @return this node
     */
    public UiNode rows(List<UiTableRow> rows) {
        this.rows = copy(rows);
        invalidate();
        return this;
    }

    /**
     * Returns immutable tree item metadata.
     *
     * @return tree item metadata, never {@code null}
     */
    public List<UiTreeItem> treeItems() {
        return treeItems;
    }

    /**
     * Sets tree item metadata.
     *
     * @param treeItems tree item metadata
     * @return this node
     */
    public UiNode treeItems(List<UiTreeItem> treeItems) {
        this.treeItems = copy(treeItems);
        invalidate();
        return this;
    }

    /**
     * Returns immutable rich text spans.
     *
     * @return rich text spans, never {@code null}
     */
    public List<UiRichTextSpan> spans() {
        return spans;
    }

    /**
     * Sets rich text spans.
     *
     * @param spans rich text spans
     * @return this node
     */
    public UiNode spans(List<UiRichTextSpan> spans) {
        this.spans = copy(spans);
        invalidate();
        return this;
    }

    /**
     * Returns immutable page descriptors.
     *
     * @return page descriptors, never {@code null}
     */
    public List<UiPage> pages() {
        return pages;
    }

    /**
     * Sets page descriptors.
     *
     * @param pages page descriptors
     * @return this node
     */
    public UiNode pages(List<UiPage> pages) {
        this.pages = copy(pages);
        invalidate();
        return this;
    }

    /**
     * Returns immutable selected ids for id-addressed controls.
     *
     * @return selected ids, never {@code null}
     */
    public List<String> selectedIds() {
        return selectedIds;
    }

    /**
     * Sets selected ids for id-addressed controls.
     *
     * @param selectedIds selected ids
     * @return this node
     */
    public UiNode selectedIds(List<String> selectedIds) {
        if (selectedIds == null || selectedIds.isEmpty()) {
            this.selectedIds = Collections.emptyList();
        } else {
            List<String> copy = new ArrayList<String>(selectedIds.size());
            for (String selectedId : selectedIds) {
                copy.add(selectedId == null ? "" : selectedId);
            }
            this.selectedIds = Collections.unmodifiableList(copy);
        }
        invalidate();
        return this;
    }

    /**
     * Reports whether a select-like control should expose searching.
     *
     * @return searchable flag
     */
    public boolean searchable() {
        return searchable;
    }

    /**
     * Sets searchable metadata.
     *
     * @param searchable searchable flag
     * @return this node
     */
    public UiNode searchable(boolean searchable) {
        this.searchable = searchable;
        invalidate();
        return this;
    }

    /**
     * Reports whether the control accepts multiple selected ids.
     *
     * @return multi-select flag
     */
    public boolean multiSelect() {
        return multiSelect;
    }

    /**
     * Sets multi-select metadata.
     *
     * @param multiSelect multi-select flag
     * @return this node
     */
    public UiNode multiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
        invalidate();
        return this;
    }

    /**
     * Reports whether a collection node should be rendered virtually.
     *
     * @return virtualized flag
     */
    public boolean virtualized() {
        return virtualized;
    }

    /**
     * Sets virtualized metadata.
     *
     * @param virtualized virtualized flag
     * @return this node
     */
    public UiNode virtualized(boolean virtualized) {
        this.virtualized = virtualized;
        invalidate();
        return this;
    }

    /**
     * Returns modal overlay options.
     *
     * @return modal options or {@code null}
     */
    public UiModalOptions modalOptions() {
        return modalOptions;
    }

    /**
     * Stores modal overlay options.
     *
     * @param modalOptions modal options
     * @return this node
     */
    public UiNode modalOptions(UiModalOptions modalOptions) {
        this.modalOptions = modalOptions;
        invalidate();
        return this;
    }

    /**
     * Returns tooltip attachment metadata.
     *
     * @return attachment metadata or {@code null}
     */
    public UiTooltipAttachment tooltipAttachment() {
        return tooltipAttachment;
    }

    /**
     * Stores tooltip attachment metadata.
     *
     * @param tooltipAttachment tooltip attachment metadata
     * @return this node
     */
    public UiNode tooltipAttachment(UiTooltipAttachment tooltipAttachment) {
        this.tooltipAttachment = tooltipAttachment;
        invalidate();
        return this;
    }

    /**
     * Returns toast display options.
     *
     * @return toast options or {@code null}
     */
    public UiToastOptions toastOptions() {
        return toastOptions;
    }

    /**
     * Stores toast display options.
     *
     * @param toastOptions toast options
     * @return this node
     */
    public UiNode toastOptions(UiToastOptions toastOptions) {
        this.toastOptions = toastOptions;
        invalidate();
        return this;
    }

    /**
     * Returns an optional custom component placeholder.
     *
     * @return custom component object or {@code null}
     */
    public Object customComponent() {
        return customComponent;
    }

    /**
     * Returns the custom component hook when the raw compatibility slot stores one.
     *
     * @return typed custom component hook or {@code null}
     */
    public UiCustomComponent uiCustomComponent() {
        return customComponent instanceof UiCustomComponent ? (UiCustomComponent) customComponent : null;
    }

    /**
     * Stores an optional custom component hook.
     *
     * <p>Use {@link UiCustomComponent} for first-class build, render, and input
     * callbacks. Existing {@link UiCustomDraw} instances are still accepted as a
     * custom draw shorthand for compatibility.</p>
     *
     * @param customComponent custom component hook or draw placeholder
     * @return this node
     */
    public UiNode customComponent(Object customComponent) {
        this.customComponent = customComponent;
        invalidate();
        return this;
    }

    /**
     * Returns an optional custom draw hook.
     *
     * @return custom draw hook or {@code null}
     */
    public UiCustomDraw customDraw() {
        return customDraw;
    }

    /**
     * Stores an optional custom draw hook for canvas-like nodes.
     *
     * @param customDraw custom draw hook
     * @return this node
     */
    public UiNode customDraw(UiCustomDraw customDraw) {
        this.customDraw = customDraw;
        invalidate();
        return this;
    }

    private static <T> List<T> copy(List<T> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<T>(source));
    }
}
