package org.avp.client.render.entity.living.alien.base_line.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.common.api.client.renderer.layer.GeoRenderLayer;
import mod.azure.azurelib.common.internal.client.renderer.GeoRenderer;
import mod.azure.azurelib.common.internal.common.cache.object.BakedGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.avp.common.AVPResources;
import org.avp.common.game.entity.living.alien.base_line.Spitter;

public class SpitterGlowLayer extends GeoRenderLayer<Spitter> {

    private static final ResourceLocation GLOW_TEXTURE = AVPResources.entityTextureLocation("spitter_glow");

    public SpitterGlowLayer(GeoRenderer<Spitter> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(
        PoseStack poseStack,
        Spitter entity,
        BakedGeoModel bakedModel,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        var glowRenderType = RenderType.entityCutout(GLOW_TEXTURE);

        renderer.reRender(
            getDefaultBakedModel(entity),
            poseStack,
            bufferSource,
            entity,
            glowRenderType,
            bufferSource.getBuffer(glowRenderType),
            partialTick,
            15728640,
            packedOverlay,
            1F,
            1F,
            1F,
            1F
        );
    }
}
