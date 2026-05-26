package land.pandaland.ui.v2.components;

import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.event.UiKeyEvent;
import land.pandaland.ui.v2.event.UiPointerEvent;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.render.UiRenderList;

/**
 * Renderer-independent custom component hook for feature-owned controls.
 *
 * <p>Implementations receive retained-tree nodes, framework event payloads, and
 * renderer-independent command lists. They must not depend on Minecraft or
 * Forge classes so the core UI tree remains adapter-independent. Render and
 * input callback failures are isolated by the framework and treated as
 * unhandled events; build callback failures are reported to the caller because
 * they occur during explicit screen construction.</p>
 */
public interface UiCustomComponent {
    /**
     * Initializes the retained node created for this custom component.
     *
     * <p>This callback is invoked once by the fluent builder before the node is
     * attached to its parent. Implementations may set node metadata or add child
     * nodes using framework APIs. Exceptions thrown from this method propagate
     * to the builder caller.</p>
     *
     * @param node retained node owned by this component
     */
    default void build(UiNode node) {
    }

    /**
     * Appends renderer-independent commands for this component.
     *
     * @param commands command list to append to
     * @param node retained node owned by this component
     * @param bounds current layout bounds in scaled GUI pixels
     */
    default void render(UiRenderList commands, UiNode node, UiRect bounds) {
    }

    /**
     * Handles pointer press events delivered to this component.
     *
     * @param event pointer event payload
     * @param node retained node owned by this component
     * @return {@code true} when the event was consumed
     */
    default boolean pointerDown(UiPointerEvent event, UiNode node) {
        return false;
    }

    /**
     * Handles pointer release events delivered to this component.
     *
     * @param event pointer event payload
     * @param node retained node owned by this component
     * @return {@code true} when the event was consumed
     */
    default boolean pointerUp(UiPointerEvent event, UiNode node) {
        return false;
    }

    /**
     * Handles pointer drag events delivered to this component.
     *
     * @param event pointer event payload
     * @param node retained node owned by this component
     * @return {@code true} when the event was consumed
     */
    default boolean pointerDrag(UiPointerEvent event, UiNode node) {
        return false;
    }

    /**
     * Handles pointer wheel events delivered to this component.
     *
     * @param event pointer event payload
     * @param node retained node owned by this component
     * @return {@code true} when the event was consumed
     */
    default boolean pointerWheel(UiPointerEvent event, UiNode node) {
        return false;
    }

    /**
     * Handles keyboard events delivered while this component is focused.
     *
     * @param event keyboard event payload
     * @param node retained node owned by this component
     * @return {@code true} when the event was consumed
     */
    default boolean keyTyped(UiKeyEvent event, UiNode node) {
        return false;
    }
}
