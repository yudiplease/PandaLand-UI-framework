package land.pandaland.ui.v2.api;

import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.layout.UiLayoutStyle;
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
        void build(NodeBuilder root);
    }

    /**
     * Callback used to populate a panel node.
     */
    public interface PanelBuilderConsumer {
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
         * Uses horizontal row layout for this node.
         *
         * @return this builder
         */
        public NodeBuilder row() {
            node.layoutStyle(UiLayoutStyle.row());
            return this;
        }

        /**
         * Sets preferred size for this node.
         *
         * @param width preferred width
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

        public NodeBuilder row(PanelBuilderConsumer consumer) {
            if (consumer == null) {
                throw new IllegalArgumentException("consumer cannot be null");
            }
            UiNode child = new UiNode(UiNode.Type.STACK).layoutStyle(UiLayoutStyle.row());
            consumer.build(new NodeBuilder(child));
            node.add(child);
            return this;
        }

        public NodeBuilder column(PanelBuilderConsumer consumer) {
            if (consumer == null) {
                throw new IllegalArgumentException("consumer cannot be null");
            }
            UiNode child = new UiNode(UiNode.Type.STACK).layoutStyle(UiLayoutStyle.column());
            consumer.build(new NodeBuilder(child));
            node.add(child);
            return this;
        }

        public NodeBuilder label(String text) {
            node.add(new UiNode(UiNode.Type.LABEL).layoutStyle(UiLayoutStyle.leaf().size(80, 12)).text(text));
            return this;
        }

        public NodeBuilder label(String text, int width, int height) {
            node.add(new UiNode(UiNode.Type.LABEL).layoutStyle(UiLayoutStyle.leaf().size(width, height)).text(text));
            return this;
        }

        public NodeBuilder button(String text, Runnable action) {
            node.add(new UiNode(UiNode.Type.BUTTON).layoutStyle(UiLayoutStyle.leaf().size(120, 22)).text(text).onClick(action).focusable(true));
            return this;
        }

        public NodeBuilder button(String text, Runnable action, int width, int height) {
            node.add(new UiNode(UiNode.Type.BUTTON).layoutStyle(UiLayoutStyle.leaf().size(width, height)).text(text).onClick(action).focusable(true));
            return this;
        }

        public NodeBuilder slider(String label, UiState<Float> value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            node.add(new UiNode(UiNode.Type.SLIDER).layoutStyle(UiLayoutStyle.leaf().size(140, 24)).text(label).valueState(value).focusable(true));
            return this;
        }

        public NodeBuilder slider(String label, UiState<Float> value, int width, int height) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            node.add(new UiNode(UiNode.Type.SLIDER).layoutStyle(UiLayoutStyle.leaf().size(width, height)).text(label).valueState(value).focusable(true));
            return this;
        }

        public NodeBuilder icon(String texture, int width, int height) {
            node.add(new UiNode(UiNode.Type.LABEL).layoutStyle(UiLayoutStyle.leaf().size(width, height)).texture(texture));
            return this;
        }
    }
}
