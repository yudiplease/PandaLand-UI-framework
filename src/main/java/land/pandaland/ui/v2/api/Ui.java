package land.pandaland.ui.v2.api;

import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.layout.UiLayoutStyle;
import land.pandaland.ui.v2.layout.UiLayoutStyle.Align;
import land.pandaland.ui.v2.event.UiTextValidator;
import land.pandaland.ui.v2.state.UiState;

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
        return new UiNode(UiNode.Type.MODAL).layoutStyle(UiLayoutStyle.column().padding(8).gap(6)).text(title);
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
         * Adds a tab selector.
         *
         * @param tabs tab labels
         * @param selectedIndex bound selected index state
         * @param width tab strip width
         * @param height tab strip height
         * @return this builder
         */
        public NodeBuilder tabs(String[] tabs, UiState selectedIndex, int width, int height) {
            UiNode child = new UiNode(UiNode.Type.TABS)
                    .layoutStyle(UiLayoutStyle.row().size(width, height).gap(4))
                    .valueState(selectedIndex)
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
         * Adds a tooltip panel.
         *
         * @param text tooltip text
         * @param width tooltip width
         * @param height tooltip height
         * @return this builder
         */
        public NodeBuilder tooltip(String text, int width, int height) {
            node.add(new UiNode(UiNode.Type.TOOLTIP).layoutStyle(UiLayoutStyle.leaf().size(width, height)).text(text));
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
    }
}
