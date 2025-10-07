package net.minecraft.resources;

public final class ResourceLocation {
    private final String namespace;
    private final String path;

    private ResourceLocation(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public static ResourceLocation fromNamespaceAndPath(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }
}
