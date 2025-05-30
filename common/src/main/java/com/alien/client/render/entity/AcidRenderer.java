package com.alien.client.render.entity;

import com.alien.common.gameplay.entity.acid.Acid;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class AcidRenderer extends EntityRenderer<Acid> {

    public AcidRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(
        Acid entity,
        float entityYaw,
        float partialTick,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight
    ) {
        /* Do nothing */
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Acid acid) {
        return null;
    }
}
