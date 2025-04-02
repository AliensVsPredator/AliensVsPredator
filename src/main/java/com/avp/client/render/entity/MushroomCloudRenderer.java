package com.avp.client.render.entity;

import com.avp.AVPResources;
import com.avp.client.animation.entity.MushroomCloudAnimator;
import com.avp.common.entity.nukecloud.MushroomCloudEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MushroomCloudRenderer extends AzEntityRenderer<MushroomCloudEntity> {

    private static final String NAME = "mushroom_cloud";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    public MushroomCloudRenderer(EntityRendererProvider.Context context) {
        super(AzEntityRendererConfig.<MushroomCloudEntity>builder(MODEL, TEXTURE)
                .setAnimatorProvider(MushroomCloudAnimator::new).build(),
                context);
    }

    @Override
    public void render(@NotNull MushroomCloudEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        var progress = entity.tickCount / 300.0f;
        progress = Math.min(progress, 1.0f);
        var scale = 1.0f + (progress * 80.0f);

        poseStack.translate(0D, -30D, 0D);
        poseStack.scale(scale, scale, scale);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
