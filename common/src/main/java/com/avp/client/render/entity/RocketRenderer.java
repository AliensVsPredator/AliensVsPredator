package com.avp.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.common.entity.projectile.Rocket;

public class RocketRenderer extends AzEntityRenderer<Rocket> {

    private static final String NAME = "rocket";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    public RocketRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.<Rocket>builder(MODEL, TEXTURE)
                .build(),
            context
        );
    }

    @Override
    public void render(
        @NotNull Rocket entity,
        float entityYaw,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight
    ) {
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(-entity.getXRot()));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
