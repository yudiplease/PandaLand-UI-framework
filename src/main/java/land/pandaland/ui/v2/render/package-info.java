/**
 * Renderer-independent draw command generation.
 *
 * <p>The traversal converts laid-out retained nodes into an ordered
 * {@link land.pandaland.ui.v2.render.UiRenderList}. Platform adapters consume
 * that list and translate commands into Minecraft rendering calls.</p>
 */
package land.pandaland.ui.v2.render;
