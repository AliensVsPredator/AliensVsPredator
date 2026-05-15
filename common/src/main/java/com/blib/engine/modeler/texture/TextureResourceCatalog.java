package com.blib.engine.modeler.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public final class TextureResourceCatalog {

    private static final String PNG_SUFFIX = ".png";

    private TextureResourceCatalog() {}

    public static List<ResourceLocation> itemTextures(ResourceLocation itemId) {
        return matchingTextures("textures/item", itemId);
    }

    public static List<ResourceLocation> blockTextures(ResourceLocation blockId) {
        return matchingTextures("textures/block", blockId);
    }

    public static List<ResourceLocation> entityTextures(ResourceLocation entityTypeId) {
        return matchingEntityTextures(entityTypeId);
    }

    public static String displayName(ResourceLocation textureResource) {
        var path = textureResource.getPath();
        if (path.startsWith("textures/")) {
            path = path.substring("textures/".length());
        }
        return textureResource.getNamespace() + ":" + path;
    }

    private static List<ResourceLocation> matchingTextures(String root, ResourceLocation ownerId) {
        var resources = Minecraft
            .getInstance()
            .getResourceManager()
            .listResources(root, resource -> resource.getPath().endsWith(PNG_SUFFIX));
        var out = new ArrayList<ResourceLocation>();
        for (var texture : resources.keySet()) {
            if (!texture.getNamespace().equals(ownerId.getNamespace())) {
                continue;
            }
            if (matchesOwner(texture.getPath(), root, ownerId.getPath())) {
                out.add(texture);
            }
        }
        out.sort((a, b) -> a.toString().compareToIgnoreCase(b.toString()));
        return out;
    }

    private static boolean matchesOwner(String texturePath, String root, String ownerPath) {
        var prefix = root + "/";
        if (!texturePath.startsWith(prefix) || !texturePath.endsWith(PNG_SUFFIX)) {
            return false;
        }
        var rel = texturePath.substring(prefix.length(), texturePath.length() - PNG_SUFFIX.length());
        if (rel.equals(ownerPath) || rel.startsWith(ownerPath + "_")) {
            return true;
        }

        var ownerName = lastSegment(ownerPath);
        var textureName = lastSegment(rel);
        return textureName.equals(ownerName) || textureName.startsWith(ownerName + "_");
    }

    private static List<ResourceLocation> matchingEntityTextures(ResourceLocation entityTypeId) {
        var root = "textures/entity";
        var resources = Minecraft
            .getInstance()
            .getResourceManager()
            .listResources(root, resource -> resource.getPath().endsWith(PNG_SUFFIX));
        var out = new ArrayList<ResourceLocation>();
        for (var texture : resources.keySet()) {
            if (!texture.getNamespace().equals(entityTypeId.getNamespace())) {
                continue;
            }
            if (matchesEntity(texture.getPath(), root, entityTypeId.getPath())) {
                out.add(texture);
            }
        }
        out.sort((a, b) -> a.toString().compareToIgnoreCase(b.toString()));
        return out;
    }

    private static boolean matchesEntity(String texturePath, String root, String entityPath) {
        var prefix = root + "/";
        if (!texturePath.startsWith(prefix) || !texturePath.endsWith(PNG_SUFFIX)) {
            return false;
        }

        var rel = texturePath.substring(prefix.length(), texturePath.length() - PNG_SUFFIX.length());
        if (matchesOwner(texturePath, root, entityPath)) {
            return true;
        }

        var normalizedEntity = normalize(entityPath);
        var folder = firstSegment(rel);
        var file = lastSegment(rel);
        return normalize(folder).equals(normalizedEntity) || normalize(file).equals(normalizedEntity);
    }

    private static String firstSegment(String value) {
        var slash = value.indexOf('/');
        return slash < 0 ? value : value.substring(0, slash);
    }

    private static String lastSegment(String value) {
        var slash = value.lastIndexOf('/');
        return slash < 0 ? value : value.substring(slash + 1);
    }

    private static String normalize(String value) {
        return value.replace("_", "").replace("-", "");
    }
}
