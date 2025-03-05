package com.avp.core.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.core.AVPResources;
import com.avp.core.client.animation.DroneAnimator;
import com.avp.core.common.entity.living.alien.xenomorph.drone.Drone;

public class DroneRenderer extends AzEntityRenderer<Drone> {

    private static final String NAME = "drone";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    public DroneRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.<Drone>builder($ -> MODEL, DroneRenderer::textureLocation)
                .setAnimatorProvider(DroneAnimator::new)
                .build(),
            context
        );
        this.shadowRadius = 0.5F;
    }

    public static ResourceLocation textureLocation(Drone drone) {
        if (drone.isNetherAfflicted()) {
            return NETHER_TEXTURE;
        }

        return drone.isAberrant() ? ABERRANT_TEXTURE : TEXTURE;
    }

    @Override
    public void render(
        @NotNull Drone entity,
        float entityYaw,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight
    ) {
        entity.runPassiveAnimations();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
