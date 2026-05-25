package land.pandaland.ui.api;

import net.minecraft.util.ResourceLocation;

/**
 * Square textured icon component.
 */
public final class PandaIcon extends PandaComponent {
    private final ResourceLocation texture;
    private final int size;

    private PandaIcon(ResourceLocation texture, int size) {
        if (texture == null) {
            throw new IllegalArgumentException("texture cannot be null");
        }
        this.texture = texture;
        this.size = Math.max(8, size);
    }

    /**
     * Creates a 16 pixel icon from a texture domain and path.
     *
     * @param domain resource domain
     * @param path texture path
     * @return icon component
     */
    public static PandaIcon texture(String domain, String path) {
        return texture(new ResourceLocation(domain, path), 16);
    }

    /**
     * Creates a square icon from a texture location.
     *
     * @param texture texture resource
     * @param size requested square size; values below 8 are clamped
     * @return icon component
     */
    public static PandaIcon texture(ResourceLocation texture, int size) {
        return new PandaIcon(texture, size);
    }

    /**
     * @return texture rendered by this icon
     */
    public ResourceLocation texture() {
        return texture;
    }

    public int preferredWidth() {
        return size;
    }

    public int preferredHeight() {
        return size;
    }
}
