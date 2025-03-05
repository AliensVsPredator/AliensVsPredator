package com.avp.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.common.entity.acid.Acid;

public class AcidRenderer extends EntityRenderer<Acid> {

    public AcidRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Acid a, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        /* Do nothing */
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Acid acid) {
        return null;
    }
}
