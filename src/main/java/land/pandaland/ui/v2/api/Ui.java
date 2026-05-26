package land.pandaland.ui.v2.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import land.pandaland.ui.v2.components.UiCustomComponent;
import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.data.UiListItem;
import land.pandaland.ui.v2.data.UiModalOptions;
import land.pandaland.ui.v2.data.UiOption;
import land.pandaland.ui.v2.data.UiPage;
import land.pandaland.ui.v2.data.UiRichTextSpan;
import land.pandaland.ui.v2.data.UiTableColumn;
import land.pandaland.ui.v2.data.UiTableRow;
import land.pandaland.ui.v2.data.UiTooltipAttachment;
import land.pandaland.ui.v2.data.UiTreeItem;
import land.pandaland.ui.v2.layout.UiLayoutStyle;
import land.pandaland.ui.v2.layout.UiLayoutStyle.Align;
import land.pandaland.ui.v2.event.UiSelectionHandler;
import land.pandaland.ui.v2.event.UiTextValidator;
import land.pandaland.ui.v2.state.UiState;
import land.pandaland.ui.v2.render.UiCustomDraw;

/**
 * Public fluent entry point for PandaLand UI Framework v2 screens.
 *
 * <p>The API builds a retained {@link UiScreen} tree. Feature mods should use
 * this class to describe screens and open them through the Forge adapter rather
 * than extending Minecraft GUI classes directly.</p>
 */
public final class Ui {
    private Ui() {
    }

    /**
     * Starts building a new retained UI screen.
     *
     * @param id stable screen id used for diagnostics
     * @return screen builder
     */
    public static ScreenBuilder screen(String id) {
        return new ScreenBuilder(id);
    }

    /**
     * Creates a modal node that can be shown through {@code UiRuntime}.
     *
     * @param title modal title text
     * @return modal node
     */
    public static UiNode modal(String title) {
        return modal(new UiModalOptions(title));
    }

    /**
     * Creates a modal node with public overlay options.
     *
     * @param options modal options
     * @return modal node
     */
    public static UiNode modal(UiModalOptions options) {
        UiModalOptions safeOptions = options == null ? new UiModalOptions("") : options;
        return new UiNode(UiNode.Type.MODAL)
                .layoutStyle(UiLayoutStyle.column().size(safeOptions.width(), safeOptions.height()).padding(8).gap(6))
                .text(safeOptions.title())
                .modalOptions(safeOptions);
    }

    /**
     * Fluent builder for a single screen.
     */
    public static final class ScreenBuilder {
        private final String id;
        private UiNode root;

        private ScreenBuilder(String id) {
            this.id = id;
        }

        /**
         * Defines the root tree for this screen.
         *
         * @param consumer root builder callback
         * @return this builder
         */
        public ScreenBuilder root(RootBuilderConsumer consumer) {
            if (consumer == null) {
                throw new IllegalArgumentException("consumer cannot be null");
            }
            UiNode node = new UiNode(UiNode.Type.ROOT).layoutStyle(UiLayoutStyle.column());
            consumer.build(new NodeBuilder(node));
            root = node;
            return this;
        }

        /**
         * Builds the retained screen.
         *
         * @return screen instance
         */
        public UiScreen build() {
            UiNode screenRoot = root == null ? new UiNode(UiNode.Type.ROOT).layoutStyle(UiLayoutStyle.column()) : root;
            return new UiScreen(id, screenRoot);
        }
    }

    /**
     * Callback used to populate a screen root.
     */
    public interface RootBuilderConsumer {
        /**
         * Populates the root builder.
         *
         * @param root root node builder
         */
        void build(NodeBuilder root);
    }

    /**
     * Callback used to populate a panel node.
     */
    public interface PanelBuilderConsumer {
        /**
         * Populates the child builder.
         *
         * @param panel child node builder
         */
        void build(NodeBuilder panel);
    }

    /**
     * Fluent builder bound to a single node.
     */
    public static final class NodeBuilder {
        private final UiNode node;

        private NodeBuilder(UiNode node) {
            this.node = node;
        }

        /**
         * Uses vertical column layout for this node.
         *
         * @return this builder
         */
        public NodeBuilder column() {
            node.layoutStyle(UiLayoutStyle.column());
            return this;
        }

        /**
         * Adds a text input with default placeholder, size, and maximum length.
         *
         * @param value bound text state
         * @return this builder
         */
        public NodeBuilder textInput(UiState value) {
            return textInput(value, "", 160, 22, 256);
        }

        /**
         * Adds a text input with default size and maximum length.
         *
         * @param value bound text state
         * @param placeholder placeholder shown when empty
         * @return this builder
         */
        public NodeBuilder textInput(UiState value, String placeholder) {
            return textInput(value, placeholder, 160, 22, 256);
        }

        /**
         * Adds a text input with explicit size and default maximum length.
         *
         * @param value bound text state
         * @param placeholder placeholder shown when empty
         * @param width input width
         * @param height input height
         * @return this builder
         */
        public NodeBuilder textInput(UiState value, String placeholder, int width, int height) {
            return textInput(value, placeholder, width, height, 256);
        }

        /**
         * Adds a text input.
         *
         * @param value bound text state
         * @param placeholder placeholder shown when empty
         * @param width input width
         * @param height input height
         * @param maxLength maximum character count
         * @return this builder
         */
        public NodeBuilder textInput(UiState value, String placeholder, int width, int height, int maxLength) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }

            node.add(new UiNode(UiNode.Type.TEXT_INPUT)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .valueState(value)
                    .placeholder(placeholder)
                    .maxLength(maxLength)
                    .focusable(true));

            return this;
        }

        /**
         * Adds a text input with optional input mask metadata.
         *
         * @param value bound text state
         * @param placeholder placeholder shown when empty
         * @param width input width
         * @param height input height
         * @param maxLength maximum character count
         * @param inputMask optional input mask metadata
         * @return this builder
         */
        public NodeBuilder textInput(UiState value, String placeholder, int width, int height, int maxLength, String inputMask) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }

            node.add(new UiNode(UiNode.Type.TEXT_INPUT)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .valueState(value)
                    .placeholder(placeholder)
                    .maxLength(maxLength)
                    .inputMask(inputMask)
                    .focusable(true));

            return this;
        }

        /**
         * Adds a password input that masks visible characters.
         *
         * @param value bound text state
         * @param placeholder placeholder shown when empty
         * @param width input width
         * @param height input height
         * @param maxLength maximum character count
         * @return this builder
         */
        public NodeBuilder passwordInput(UiState value, String placeholder, int width, int height, int maxLength) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }

            node.add(new UiNode(UiNode.Type.TEXT_INPUT)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .valueState(value)
                    .placeholder(placeholder)
                    .maxLength(maxLength)
                    .password(true)
                    .focusable(true));

            return this;
        }

        /**
         * Adds a text input with an Enter callback.
         *
         * @param value bound text state
         * @param placeholder placeholder shown when empty
         * @param width input width
         * @param height input height
         * @param maxLength maximum character count
         * @param onEnter callback invoked when Enter is pressed
         * @return this builder
         */
        public NodeBuilder textInput(UiState value, String placeholder, int width, int height, int maxLength, Runnable onEnter) {
            return textInput(value, placeholder, width, height, maxLength, onEnter, null, null);
        }

        /**
         * Adds a fully configured text input.
         *
         * @param value bound text state
         * @param placeholder placeholder shown when empty
         * @param width input width
         * @param height input height
         * @param maxLength maximum character count
         * @param onEnter callback invoked when Enter is pressed
         * @param onChange callback invoked after committed value changes
         * @param validator validation callback
         * @return this builder
         */
        public NodeBuilder textInput(UiState value, String placeholder, int width, int height, int maxLength, Runnable onEnter, Runnable onChange, UiTextValidator validator) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }

            node.add(new UiNode(UiNode.Type.TEXT_INPUT)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .valueState(value)
                    .placeholder(placeholder)
                    .maxLength(maxLength)
                    .onEnter(onEnter)
                    .onChange(onChange)
                    .validator(validator)
                    .focusable(true));

            return this;
        }

        /**
         * Adds a multi-line text input with default input mask metadata.
         *
         * @param value bound text state
         * @param placeholder placeholder shown when empty
         * @param width input width
         * @param height input height
         * @param maxLength maximum character count
         * @return this builder
         */
        public NodeBuilder textarea(UiState value, String placeholder, int width, int height, int maxLength) {
            return textarea(value, placeholder, width, height, maxLength, "");
        }

        /**
         * Adds a multi-line text input.
         *
         * @param value bound text state
         * @param placeholder placeholder shown when empty
         * @param width input width
         * @param height input height
         * @param maxLength maximum character count
         * @param inputMask optional input mask metadata
         * @return this builder
         */
        public NodeBuilder textarea(UiState value, String placeholder, int width, int height, int maxLength, String inputMask) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }

            node.add(new UiNode(UiNode.Type.TEXT_INPUT)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .valueState(value)
                    .placeholder(placeholder)
                    .maxLength(maxLength)
                    .inputMask(inputMask)
                    .textArea(true)
                    .focusable(true));
            return this;
        }

        /**
         * Adds a numeric text input with default input mask metadata.
         *
         * <p>Numeric inputs accept strict signed decimal text: an optional
         * leading sign, at most one decimal point, and digits in all other
         * positions.</p>
         *
         * @param value bound text state
         * @param placeholder placeholder shown when empty
         * @param width input width
         * @param height input height
         * @param maxLength maximum character count
         * @return this builder
         */
        public NodeBuilder numericInput(UiState value, String placeholder, int width, int height, int maxLength) {
            return numericInput(value, placeholder, width, height, maxLength, "");
        }

        /**
         * Adds a numeric text input.
         *
         * <p>Numeric inputs accept strict signed decimal text: an optional
         * leading sign, at most one decimal point, and digits in all other
         * positions.</p>
         *
         * @param value bound text state
         * @param placeholder placeholder shown when empty
         * @param width input width
         * @param height input height
         * @param maxLength maximum character count
         * @param inputMask optional input mask metadata
         * @return this builder
         */
        public NodeBuilder numericInput(UiState value, String placeholder, int width, int height, int maxLength, String inputMask) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }

            node.add(new UiNode(UiNode.Type.TEXT_INPUT)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .valueState(value)
                    .placeholder(placeholder)
                    .maxLength(maxLength)
                    .inputMask(inputMask)
                    .numericInput(true)
                    .focusable(true));
            return this;
        }

        /**
         * Adds a search text input.
         *
         * @param value bound text state
         * @param placeholder placeholder shown when empty
         * @param width input width
         * @param height input height
         * @param maxLength maximum character count
         * @return this builder
         */
        public NodeBuilder searchInput(UiState value, String placeholder, int width, int height, int maxLength) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }

            node.add(new UiNode(UiNode.Type.TEXT_INPUT)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .valueState(value)
                    .placeholder(placeholder)
                    .maxLength(maxLength)
                    .searchInput(true)
                    .searchable(true)
                    .focusable(true));
            return this;
        }

        /**
         * Adds a checkbox with default size.
         *
         * @param label checkbox label
         * @param value bound boolean state
         * @return this builder
         */
        public NodeBuilder checkbox(String label, UiState value) {
            return checkbox(label, value, 140, 18, null);
        }

        /**
         * Adds a checkbox with explicit size.
         *
         * @param label checkbox label
         * @param value bound boolean state
         * @param width checkbox width
         * @param height checkbox height
         * @return this builder
         */
        public NodeBuilder checkbox(String label, UiState value, int width, int height) {
            return checkbox(label, value, width, height, null);
        }

        /**
         * Adds a checkbox with default size and change callback.
         *
         * @param label checkbox label
         * @param value bound boolean state
         * @param onChange callback invoked on click
         * @return this builder
         */
        public NodeBuilder checkbox(String label, UiState value, Runnable onChange) {
            return checkbox(label, value, 140, 18, onChange);
        }

        /**
         * Adds a checkbox.
         *
         * @param label checkbox label
         * @param value bound boolean state
         * @param width checkbox width
         * @param height checkbox height
         * @param onChange callback invoked on click
         * @return this builder
         */
        public NodeBuilder checkbox(String label, UiState value, int width, int height, Runnable onChange) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }

            UiNode checkbox = new UiNode(UiNode.Type.CHECKBOX)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .text(label)
                    .valueState(value)
                    .focusable(true);

            if (onChange != null) {
                checkbox.onClick(onChange);
            }

            node.add(checkbox);
            return this;
        }
        /**
         * Uses horizontal row layout for this node.
         *
         * @return this builder
         */
        public NodeBuilder row() {
            node.layoutStyle(UiLayoutStyle.row());
            return this;
        }

        /**
         * Uses overlay layout for this node.
         *
         * @return this builder
         */
        public NodeBuilder overlay() {
            node.layoutStyle(UiLayoutStyle.overlay());
            return this;
        }

        /**
         * Uses scroll layout for this node.
         *
         * @return this builder
         */
        public NodeBuilder scroll() {
            node.layoutStyle(UiLayoutStyle.scroll());
            return this;
        }

        /**
         * Uses fixed-column grid layout for this node.
         *
         * @param columns number of columns; values below one are clamped to one
         * @param rowHeight fixed row height in scaled pixels
         * @return this builder
         */
        public NodeBuilder grid(int columns, int rowHeight) {
            node.layoutStyle(UiLayoutStyle.grid(columns, rowHeight));
            return this;
        }

        /**
         * Uses absolute layout for this node's children.
         *
         * @return this builder
         */
        public NodeBuilder absolute() {
            node.layoutStyle(UiLayoutStyle.absolute());
            return this;
        }

        /**
         * Sets preferred size for this node.
         *
         * @param width  preferred width
         * @param height preferred height
         * @return this builder
         */
        public NodeBuilder size(int width, int height) {
            node.layoutStyle().size(width, height);
            return this;
        }

        /**
         * Sets minimum size for this node.
         *
         * @param width minimum width
         * @param height minimum height
         * @return this builder
         */
        public NodeBuilder minSize(int width, int height) {
            node.layoutStyle().minSize(width, height);
            return this;
        }

        /**
         * Sets maximum size for this node.
         *
         * @param width maximum width
         * @param height maximum height
         * @return this builder
         */
        public NodeBuilder maxSize(int width, int height) {
            node.layoutStyle().maxSize(width, height);
            return this;
        }

        /**
         * Sets minimum resolved width for this node.
         *
         * @param width minimum width
         * @return this builder
         */
        public NodeBuilder minWidth(int width) {
            node.layoutStyle().minWidth(width);
            return this;
        }

        /**
         * Sets minimum resolved height for this node.
         *
         * @param height minimum height
         * @return this builder
         */
        public NodeBuilder minHeight(int height) {
            node.layoutStyle().minHeight(height);
            return this;
        }

        /**
         * Sets maximum resolved width for this node.
         *
         * @param width maximum width
         * @return this builder
         */
        public NodeBuilder maxWidth(int width) {
            node.layoutStyle().maxWidth(width);
            return this;
        }

        /**
         * Sets maximum resolved height for this node.
         *
         * @param height maximum height
         * @return this builder
         */
        public NodeBuilder maxHeight(int height) {
            node.layoutStyle().maxHeight(height);
            return this;
        }

        /**
         * Sets this node's offset from the parent content origin.
         *
         * @param x x offset
         * @param y y offset
         * @return this builder
         */
        public NodeBuilder offset(int x, int y) {
            node.layoutStyle().offset(x, y);
            return this;
        }

        /**
         * Sets this node's x offset from the parent content origin.
         *
         * @param x x offset
         * @return this builder
         */
        public NodeBuilder x(int x) {
            node.layoutStyle().x(x);
            return this;
        }

        /**
         * Sets this node's y offset from the parent content origin.
         *
         * @param y y offset
         * @return this builder
         */
        public NodeBuilder y(int y) {
            node.layoutStyle().y(y);
            return this;
        }

        /**
         * Sets fixed grid column count for this node.
         *
         * @param columns number of columns; values below one are clamped to one
         * @return this builder
         */
        public NodeBuilder gridColumns(int columns) {
            node.layoutStyle().gridColumns(columns);
            return this;
        }

        /**
         * Sets fixed grid row height for this node.
         *
         * @param rowHeight row height in scaled pixels
         * @return this builder
         */
        public NodeBuilder gridRowHeight(int rowHeight) {
            node.layoutStyle().gridRowHeight(rowHeight);
            return this;
        }

        /**
         * Stores stacking index metadata for future render ordering support.
         *
         * <p>The current layout pass records this value but does not reorder nodes.</p>
         *
         * @param zIndex stacking index metadata
         * @return this builder
         */
        public NodeBuilder zIndex(int zIndex) {
            node.layoutStyle().zIndex(zIndex);
            return this;
        }

        /**
         * Sets equal padding on every side.
         *
         * @param padding padding in scaled pixels
         * @return this builder
         */
        public NodeBuilder padding(int padding) {
            node.layoutStyle().padding(padding);
            return this;
        }

        /**
         * Sets per-side padding.
         *
         * @return this builder
         */
        public NodeBuilder padding(int left, int top, int right, int bottom) {
            node.layoutStyle().padding(left, top, right, bottom);
            return this;
        }

        /**
         * Sets child gap for row/column layout.
         *
         * @param gap gap in scaled pixels
         * @return this builder
         */
        public NodeBuilder gap(int gap) {
            node.layoutStyle().gap(gap);
            return this;
        }

        /**
         * Sets grow weight for this node.
         *
         * @param grow grow weight
         * @return this builder
         */
        public NodeBuilder grow(float grow) {
            node.layoutStyle().grow(grow);
            return this;
        }

        /**
         * Sets shrink weight for this node.
         *
         * @param shrink shrink weight
         * @return this builder
         */
        public NodeBuilder shrink(float shrink) {
            node.layoutStyle().shrink(shrink);
            return this;
        }

        /**
         * Makes this node fill parent width.
         *
         * @return this builder
         */
        public NodeBuilder fillWidth() {
            node.layoutStyle().fillWidth();
            return this;
        }

        /**
         * Makes this node fill parent height.
         *
         * @return this builder
         */
        public NodeBuilder fillHeight() {
            node.layoutStyle().fillHeight();
            return this;
        }

        /**
         * Sets this node width as a percentage of parent content width.
         *
         * @param percent width percentage
         * @return this builder
         */
        public NodeBuilder widthPercent(float percent) {
            node.layoutStyle().widthPercent(percent);
            return this;
        }

        /**
         * Sets this node height as a percentage of parent content height.
         *
         * @param percent height percentage
         * @return this builder
         */
        public NodeBuilder heightPercent(float percent) {
            node.layoutStyle().heightPercent(percent);
            return this;
        }

        /**
         * Sets cross-axis alignment for this node.
         *
         * @param align alignment value
         * @return this builder
         */
        public NodeBuilder align(Align align) {
            node.layoutStyle().align(align);
            return this;
        }

        /**
         * Stores a wrapping preference on this node.
         *
         * @param wrap wrapping flag
         * @return this builder
         */
        public NodeBuilder wrap(boolean wrap) {
            node.layoutStyle().wrap(wrap);
            return this;
        }

        /**
         * Adds a panel child.
         *
         * @param consumer child builder callback
         * @return this builder
         */
        public NodeBuilder panel(PanelBuilderConsumer consumer) {
            if (consumer == null) {
                throw new IllegalArgumentException("consumer cannot be null");
            }
            UiNode child = new UiNode(UiNode.Type.PANEL).layoutStyle(UiLayoutStyle.column());
            consumer.build(new NodeBuilder(child));
            node.add(child);
            return this;
        }

        /**
         * Adds a row stack child.
         *
         * @param consumer child builder callback
         * @return this builder
         */
        public NodeBuilder row(PanelBuilderConsumer consumer) {
            if (consumer == null) {
                throw new IllegalArgumentException("consumer cannot be null");
            }
            UiNode child = new UiNode(UiNode.Type.STACK).layoutStyle(UiLayoutStyle.row());
            consumer.build(new NodeBuilder(child));
            node.add(child);
            return this;
        }

        /**
         * Adds a column stack child.
         *
         * @param consumer child builder callback
         * @return this builder
         */
        public NodeBuilder column(PanelBuilderConsumer consumer) {
            if (consumer == null) {
                throw new IllegalArgumentException("consumer cannot be null");
            }
            UiNode child = new UiNode(UiNode.Type.STACK).layoutStyle(UiLayoutStyle.column());
            consumer.build(new NodeBuilder(child));
            node.add(child);
            return this;
        }

        /**
         * Adds a scroll container child.
         *
         * @param width container width
         * @param height container height
         * @param consumer child builder callback
         * @return this builder
         */
        public NodeBuilder scrollContainer(int width, int height, PanelBuilderConsumer consumer) {
            if (consumer == null) {
                throw new IllegalArgumentException("consumer cannot be null");
            }
            UiNode child = new UiNode(UiNode.Type.SCROLL_CONTAINER).layoutStyle(UiLayoutStyle.scroll().size(width, height));
            consumer.build(new NodeBuilder(child));
            node.add(child);
            return this;
        }

        /**
         * Adds a select/dropdown control.
         *
         * @param label visible label
         * @param value bound selected value state
         * @param options option labels
         * @param width control width
         * @param height control height
         * @return this builder
         */
        public NodeBuilder select(String label, UiState value, String[] options, int width, int height) {
            UiNode child = new UiNode(UiNode.Type.SELECT)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .text(label)
                    .valueState(value)
                    .focusable(true);
            if (options != null) {
                for (String option : options) {
                    child.add(new UiNode(UiNode.Type.LABEL).layoutStyle(UiLayoutStyle.leaf().size(width, height)).text(option));
                }
            }
            node.add(child);
            return this;
        }

        /**
         * Adds a vertical list control.
         *
         * @param rows row labels
         * @param selectedIndex bound selected index state
         * @param width list width
         * @param rowHeight row height
         * @return this builder
         */
        public NodeBuilder list(String[] rows, UiState selectedIndex, int width, int rowHeight) {
            UiNode child = new UiNode(UiNode.Type.LIST)
                    .layoutStyle(UiLayoutStyle.column().size(width, Math.max(rowHeight, 1) * Math.max(1, rows == null ? 1 : rows.length)))
                    .valueState(selectedIndex)
                    .focusable(true);
            if (rows != null) {
                for (String row : rows) {
                    child.add(new UiNode(UiNode.Type.LABEL).layoutStyle(UiLayoutStyle.leaf().size(width, rowHeight)).text(row));
                }
            }
            node.add(child);
            return this;
        }

        /**
         * Adds a table control with typed column and row metadata.
         *
         * @param columns table column descriptors
         * @param rows table row descriptors
         * @param width table width
         * @param height table height
         * @return this builder
         */
        public NodeBuilder table(List<UiTableColumn> columns, List<UiTableRow> rows, int width, int height) {
            return table(columns, rows, width, height, null);
        }

        /**
         * Adds a table control with typed column and row metadata and a selection callback.
         *
         * @param columns table column descriptors
         * @param rows table row descriptors
         * @param width table width
         * @param height table height
         * @param onSelection callback invoked with selected row id and index
         * @return this builder
         */
        public NodeBuilder table(List<UiTableColumn> columns, List<UiTableRow> rows, int width, int height, UiSelectionHandler onSelection) {
            node.add(new UiNode(UiNode.Type.TABLE)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .columns(columns)
                    .rows(rows)
                    .onSelection(onSelection)
                    .focusable(true));
            return this;
        }

        /**
         * Adds a data-grid control with typed table metadata and selected row ids.
         *
         * @param columns grid column descriptors
         * @param rows grid row descriptors
         * @param selectedIds selected row ids
         * @param width grid width
         * @param height grid height
         * @return this builder
         */
        public NodeBuilder dataGrid(List<UiTableColumn> columns, List<UiTableRow> rows, List<String> selectedIds, int width, int height) {
            return dataGrid(columns, rows, selectedIds, width, height, null);
        }

        /**
         * Adds a data-grid control with typed table metadata, selected row ids, and a selection callback.
         *
         * @param columns grid column descriptors
         * @param rows grid row descriptors
         * @param selectedIds selected row ids
         * @param width grid width
         * @param height grid height
         * @param onSelection callback invoked with selected row id and index
         * @return this builder
         */
        public NodeBuilder dataGrid(List<UiTableColumn> columns, List<UiTableRow> rows, List<String> selectedIds, int width, int height, UiSelectionHandler onSelection) {
            node.add(new UiNode(UiNode.Type.DATA_GRID)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .columns(columns)
                    .rows(rows)
                    .selectedIds(selectedIds)
                    .multiSelect(true)
                    .onSelection(onSelection)
                    .focusable(true));
            return this;
        }

        /**
         * Adds a tree control with typed tree item metadata.
         *
         * @param treeItems root tree item descriptors
         * @param selectedIds selected tree item ids
         * @param width tree width
         * @param height tree height
         * @return this builder
         */
        public NodeBuilder tree(List<UiTreeItem> treeItems, List<String> selectedIds, int width, int height) {
            return tree(treeItems, selectedIds, width, height, null);
        }

        /**
         * Adds a tree control with typed tree item metadata and a selection callback.
         *
         * @param treeItems root tree item descriptors
         * @param selectedIds selected tree item ids
         * @param width tree width
         * @param height tree height
         * @param onSelection callback invoked with selected item id and index
         * @return this builder
         */
        public NodeBuilder tree(List<UiTreeItem> treeItems, List<String> selectedIds, int width, int height, UiSelectionHandler onSelection) {
            node.add(new UiNode(UiNode.Type.TREE)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .treeItems(treeItems)
                    .selectedIds(selectedIds)
                    .onSelection(onSelection)
                    .focusable(true));
            return this;
        }

        /**
         * Adds a virtualized list control with stable item ids.
         *
         * @param items list item descriptors
         * @param selectedIds selected item ids
         * @param width list width
         * @param rowHeight row height used to estimate total virtual content
         * @return this builder
         */
        public NodeBuilder virtualList(List<UiListItem> items, List<String> selectedIds, int width, int rowHeight) {
            return virtualList(items, selectedIds, width, rowHeight, null);
        }

        /**
         * Adds a virtualized list control with stable item ids and a selection callback.
         *
         * @param items list item descriptors
         * @param selectedIds selected item ids
         * @param width list width
         * @param rowHeight row height used to estimate total virtual content
         * @param onSelection callback invoked with selected id and index
         * @return this builder
         */
        public NodeBuilder virtualList(List<UiListItem> items, List<String> selectedIds, int width, int rowHeight, UiSelectionHandler onSelection) {
            int count = items == null ? 1 : Math.max(1, items.size());
            node.add(new UiNode(UiNode.Type.LIST)
                    .layoutStyle(UiLayoutStyle.column().size(width, Math.max(rowHeight, 1) * count))
                    .items(items)
                    .selectedIds(selectedIds)
                    .virtualized(true)
                    .onSelection(onSelection)
                    .focusable(true));
            return this;
        }

        /**
         * Adds a rich text display control with styled spans.
         *
         * @param spans rich text spans
         * @param width rich text width
         * @param height rich text height
         * @return this builder
         */
        public NodeBuilder richText(List<UiRichTextSpan> spans, int width, int height) {
            node.add(new UiNode(UiNode.Type.RICH_TEXT)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .spans(spans));
            return this;
        }

        /**
         * Adds a canvas node backed by a renderer-independent custom draw hook.
         *
         * @param draw custom draw hook that appends render commands
         * @param width canvas width
         * @param height canvas height
         * @return this builder
         */
        public NodeBuilder canvas(UiCustomDraw draw, int width, int height) {
            if (draw == null) {
                throw new IllegalArgumentException("draw cannot be null");
            }
            node.add(new UiNode(UiNode.Type.CANVAS)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .customDraw(draw));
            return this;
        }

        /**
         * Adds a feature-owned custom component node.
         *
         * <p>The component receives a build callback immediately, render
         * callbacks during traversal, and pointer/key callbacks from the event
         * dispatcher. The component API uses framework event and render types
         * instead of Minecraft classes.</p>
         *
         * @param component custom component hook
         * @param width component width
         * @param height component height
         * @return this builder
         */
        public NodeBuilder customComponent(UiCustomComponent component, int width, int height) {
            if (component == null) {
                throw new IllegalArgumentException("component cannot be null");
            }
            UiNode child = new UiNode(UiNode.Type.CUSTOM_COMPONENT)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .customComponent(component)
                    .focusable(true);
            component.build(child);
            node.add(child);
            return this;
        }

        /**
         * Adds a searchable select control with typed option metadata.
         *
         * @param label visible select label
         * @param value bound selected id state
         * @param options selectable option descriptors
         * @param width control width
         * @param height control height
         * @return this builder
         */
        public NodeBuilder searchableSelect(String label, UiState<String> value, List<UiOption> options, int width, int height) {
            return searchableSelect(label, value, options, width, height, null);
        }

        /**
         * Adds a searchable select control with typed option metadata and a selection callback.
         *
         * @param label visible select label
         * @param value bound selected id state
         * @param options selectable option descriptors
         * @param width control width
         * @param height control height
         * @param onSelection callback invoked with selected id and index
         * @return this builder
         */
        public NodeBuilder searchableSelect(String label, UiState<String> value, List<UiOption> options, int width, int height, UiSelectionHandler onSelection) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            node.add(new UiNode(UiNode.Type.SELECT)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .text(label)
                    .valueState(value)
                    .items(optionItems(options))
                    .searchable(true)
                    .onSelection(onSelection)
                    .focusable(true));
            return this;
        }

        /**
         * Adds a multi-select control with typed option metadata and selected ids.
         *
         * @param label visible select label
         * @param options selectable option descriptors
         * @param selectedIds selected option ids
         * @param width control width
         * @param height control height
         * @return this builder
         */
        public NodeBuilder multiSelect(String label, List<UiOption> options, List<String> selectedIds, int width, int height) {
            return multiSelect(label, options, selectedIds, width, height, null);
        }

        /**
         * Adds a multi-select control with typed option metadata, selected ids, and a selection callback.
         *
         * @param label visible select label
         * @param options selectable option descriptors
         * @param selectedIds selected option ids
         * @param width control width
         * @param height control height
         * @param onSelection callback invoked with selected option id and index
         * @return this builder
         */
        public NodeBuilder multiSelect(String label, List<UiOption> options, List<String> selectedIds, int width, int height, UiSelectionHandler onSelection) {
            node.add(new UiNode(UiNode.Type.SELECT)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .text(label)
                    .items(optionItems(options))
                    .selectedIds(selectedIds)
                    .multiSelect(true)
                    .onSelection(onSelection)
                    .focusable(true));
            return this;
        }

        /**
         * Adds a color picker control bound to an RGB integer state.
         *
         * @param label visible control label
         * @param value bound RGB color state
         * @param width control width
         * @param height control height
         * @return this builder
         */
        public NodeBuilder colorPicker(String label, UiState<Integer> value, int width, int height) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            node.add(new UiNode(UiNode.Type.COLOR_PICKER)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .text(label)
                    .valueState(value)
                    .focusable(true));
            return this;
        }

        /**
         * Adds a keybind capture input bound to a key identifier state.
         *
         * @param label visible input label
         * @param value bound key identifier state
         * @param width input width
         * @param height input height
         * @return this builder
         */
        public NodeBuilder keybindInput(String label, UiState<String> value, int width, int height) {
            return keybindInput(label, value, width, height, null);
        }

        /**
         * Adds a keybind capture input with a change callback.
         *
         * @param label visible input label
         * @param value bound key identifier state
         * @param width input width
         * @param height input height
         * @param onChange callback invoked after key capture
         * @return this builder
         */
        public NodeBuilder keybindInput(String label, UiState<String> value, int width, int height, Runnable onChange) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            node.add(new UiNode(UiNode.Type.KEYBIND_INPUT)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .text(label)
                    .valueState(value)
                    .onChange(onChange)
                    .focusable(true));
            return this;
        }

        /**
         * Adds a page stack control with typed page metadata.
         *
         * @param pages page descriptors
         * @param selectedId initially selected page id
         * @param width stack width
         * @param height stack height
         * @return this builder
         */
        public NodeBuilder pageStack(List<UiPage> pages, String selectedId, int width, int height) {
            node.add(new UiNode(UiNode.Type.PAGE_STACK)
                    .layoutStyle(UiLayoutStyle.column().size(width, height))
                    .pages(pages)
                    .selectedIds(selectedId == null ? Collections.<String>emptyList() : Collections.singletonList(selectedId)));
            return this;
        }

        /**
         * Adds a tab selector.
         *
         * @param tabs tab labels
         * @param selectedIndex bound selected index state
         * @param width tab strip width
         * @param height tab strip height
         * @return this builder
         */
        public NodeBuilder tabs(String[] tabs, UiState selectedIndex, int width, int height) {
            return tabs(tabs, selectedIndex, width, height, null);
        }

        /**
         * Adds a tab selector with a selection callback.
         *
         * @param tabs tab labels
         * @param selectedIndex bound selected index state
         * @param width tab strip width
         * @param height tab strip height
         * @param onSelection callback invoked with selected tab id and index
         * @return this builder
         */
        public NodeBuilder tabs(String[] tabs, UiState selectedIndex, int width, int height, UiSelectionHandler onSelection) {
            UiNode child = new UiNode(UiNode.Type.TABS)
                    .layoutStyle(UiLayoutStyle.row().size(width, height).gap(4))
                    .valueState(selectedIndex)
                    .onSelection(onSelection)
                    .focusable(true);
            if (tabs != null) {
                int tabWidth = tabs.length == 0 ? width : Math.max(1, width / tabs.length);
                for (String tab : tabs) {
                    child.add(new UiNode(UiNode.Type.BUTTON).layoutStyle(UiLayoutStyle.leaf().size(tabWidth, height)).text(tab));
                }
            }
            node.add(child);
            return this;
        }

        /**
         * Adds a typed tab selector with stable ids and disabled state.
         *
         * @param tabs tab option descriptors
         * @param selectedId bound selected tab id state
         * @param width tab strip width
         * @param height tab strip height
         * @return this builder
         */
        public NodeBuilder tabs(List<UiOption> tabs, UiState<String> selectedId, int width, int height) {
            return tabs(tabs, selectedId, width, height, null);
        }

        /**
         * Adds a typed tab selector with stable ids, disabled state, and a selection callback.
         *
         * @param tabs tab option descriptors
         * @param selectedId bound selected tab id state
         * @param width tab strip width
         * @param height tab strip height
         * @param onSelection callback invoked with selected tab id and index
         * @return this builder
         */
        public NodeBuilder tabs(List<UiOption> tabs, UiState<String> selectedId, int width, int height, UiSelectionHandler onSelection) {
            if (selectedId == null) {
                throw new IllegalArgumentException("selectedId cannot be null");
            }
            List<UiListItem> items = optionItems(tabs);
            UiNode child = new UiNode(UiNode.Type.TABS)
                    .layoutStyle(UiLayoutStyle.row().size(width, height).gap(4))
                    .valueState(selectedId)
                    .items(items)
                    .onSelection(onSelection)
                    .focusable(true);
            int tabWidth = items.isEmpty() ? width : Math.max(1, width / items.size());
            for (UiListItem item : items) {
                child.add(new UiNode(UiNode.Type.BUTTON)
                        .layoutStyle(UiLayoutStyle.leaf().size(tabWidth, height))
                        .text(item.label())
                        .enabled(!item.disabled()));
            }
            node.add(child);
            return this;
        }

        /**
         * Adds a tooltip panel.
         *
         * @param text tooltip text
         * @param width tooltip width
         * @param height tooltip height
         * @return this builder
         */
        public NodeBuilder tooltip(String text, int width, int height) {
            return tooltip(text, width, height, null);
        }

        /**
         * Adds a tooltip panel with attachment metadata.
         *
         * @param text tooltip text
         * @param width tooltip width
         * @param height tooltip height
         * @param attachment tooltip attachment metadata
         * @return this builder
         */
        public NodeBuilder tooltip(String text, int width, int height, UiTooltipAttachment attachment) {
            node.add(new UiNode(UiNode.Type.TOOLTIP)
                    .layoutStyle(UiLayoutStyle.leaf().size(width, height))
                    .text(text)
                    .tooltipAttachment(attachment));
            return this;
        }

        /**
         * Adds a context menu panel.
         *
         * @param width menu width
         * @param height menu height
         * @param consumer child builder callback
         * @return this builder
         */
        public NodeBuilder contextMenu(int width, int height, PanelBuilderConsumer consumer) {
            if (consumer == null) {
                throw new IllegalArgumentException("consumer cannot be null");
            }
            UiNode child = new UiNode(UiNode.Type.CONTEXT_MENU).layoutStyle(UiLayoutStyle.column().size(width, height).padding(4).gap(2));
            consumer.build(new NodeBuilder(child));
            node.add(child);
            return this;
        }

        /**
         * Adds a form container.
         *
         * @param consumer child builder callback
         * @return this builder
         */
        public NodeBuilder form(PanelBuilderConsumer consumer) {
            if (consumer == null) {
                throw new IllegalArgumentException("consumer cannot be null");
            }
            UiNode child = new UiNode(UiNode.Type.FORM).layoutStyle(UiLayoutStyle.column().gap(6));
            consumer.build(new NodeBuilder(child));
            node.add(child);
            return this;
        }

        /**
         * Adds a label with default size.
         *
         * @param text label text
         * @return this builder
         */
        public NodeBuilder label(String text) {
            node.add(new UiNode(UiNode.Type.LABEL).layoutStyle(UiLayoutStyle.leaf().size(80, 12)).text(text));
            return this;
        }

        /**
         * Adds a label.
         *
         * @param text label text
         * @param width label width
         * @param height label height
         * @return this builder
         */
        public NodeBuilder label(String text, int width, int height) {
            node.add(new UiNode(UiNode.Type.LABEL).layoutStyle(UiLayoutStyle.leaf().size(width, height)).text(text));
            return this;
        }

        /**
         * Adds a button with default size.
         *
         * @param text button text
         * @param action click action
         * @return this builder
         */
        public NodeBuilder button(String text, Runnable action) {
            node.add(new UiNode(UiNode.Type.BUTTON).layoutStyle(UiLayoutStyle.leaf().size(120, 22)).text(text).onClick(action).focusable(true));
            return this;
        }

        /**
         * Adds a button.
         *
         * @param text button text
         * @param action click action
         * @param width button width
         * @param height button height
         * @return this builder
         */
        public NodeBuilder button(String text, Runnable action, int width, int height) {
            node.add(new UiNode(UiNode.Type.BUTTON).layoutStyle(UiLayoutStyle.leaf().size(width, height)).text(text).onClick(action).focusable(true));
            return this;
        }

        /**
         * Adds a slider with default size.
         *
         * @param label slider label
         * @param value bound normalized float state
         * @return this builder
         */
        public NodeBuilder slider(String label, UiState<Float> value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            node.add(new UiNode(UiNode.Type.SLIDER).layoutStyle(UiLayoutStyle.leaf().size(140, 24)).text(label).valueState(value).focusable(true));
            return this;
        }

        /**
         * Adds a slider.
         *
         * @param label slider label
         * @param value bound normalized float state
         * @param width slider width
         * @param height slider height
         * @return this builder
         */
        public NodeBuilder slider(String label, UiState<Float> value, int width, int height) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            node.add(new UiNode(UiNode.Type.SLIDER).layoutStyle(UiLayoutStyle.leaf().size(width, height)).text(label).valueState(value).focusable(true));
            return this;
        }

        /**
         * Adds a texture icon.
         *
         * @param texture texture resource id
         * @param width icon width
         * @param height icon height
         * @return this builder
         */
        public NodeBuilder icon(String texture, int width, int height) {
            node.add(new UiNode(UiNode.Type.LABEL).layoutStyle(UiLayoutStyle.leaf().size(width, height)).texture(texture));
            return this;
        }

        private static List<UiListItem> optionItems(List<UiOption> options) {
            if (options == null || options.isEmpty()) {
                return Collections.emptyList();
            }
            List<UiListItem> items = new ArrayList<UiListItem>(options.size());
            for (UiOption option : options) {
                if (option != null) {
                    items.add(option.toListItem());
                }
            }
            return items;
        }
    }
}
