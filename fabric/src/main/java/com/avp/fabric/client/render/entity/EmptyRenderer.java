package com.avp.fabric.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EmptyRenderer<T extends Entity> extends EntityRenderer<T> {

    public EmptyRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
