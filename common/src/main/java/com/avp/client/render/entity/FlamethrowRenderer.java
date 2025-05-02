package com.avp.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.common.entity.projectile.Flamethrow;

public class FlamethrowRenderer extends AzEntityRenderer<Flamethrow> {

    private static final String NAME = "flamethrow";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    public FlamethrowRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.<Flamethrow>builder(MODEL, TEXTURE)
                .build(),
            context
        );
    }

    @Override
    public void render(
        @NotNull Flamethrow entity,
        float entityYaw,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight
    ) {}
}
