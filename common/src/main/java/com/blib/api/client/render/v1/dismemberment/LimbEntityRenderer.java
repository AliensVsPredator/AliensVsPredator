package com.blib.api.client.render.v1.dismemberment;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.blib.api.client.render.v1.entity.AzEntityRenderer;
import com.blib.api.client.render.v1.entity.AzEntityRendererConfig;
import com.blib.api.client.render.v1.entity.pipeline.AzEntityRendererPipeline;
import com.blib.api.common.dismemberment.v1.entity.DismemberedLimbEntity;
import com.blib.mod.BLib;

/**
 * Generic renderer for {@link DismemberedLimbEntity}.
 * <p>
 * Pulls the model and texture from the entity's synced data so it can faithfully reuse whatever source entity dropped
 * the limb. The model renderer is swapped to {@link LimbEntityModelRenderer} which renders only the bone subtree for
 * this limb.
 */
public class LimbEntityRenderer extends AzEntityRenderer<DismemberedLimbEntity> {

    private static final ResourceLocation FALLBACK_MODEL = BLib.MOD.resources()
        .createLocation("geo/entity/dismembered_limb_fallback.geo.json");

    private static final ResourceLocation FALLBACK_TEXTURE = BLib.MOD.resources()
        .createLocation("textures/entity/dismembered_limb_fallback.png");

    @SuppressWarnings("unchecked")
    public LimbEntityRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.<DismemberedLimbEntity>builder(
                LimbEntityRenderer::resolveModelLocation,
                LimbEntityRenderer::resolveTextureLocation
            )
                .setModelRenderer(
                    (pipeline, layer) -> new LimbEntityModelRenderer(
                        (AzEntityRendererPipeline<DismemberedLimbEntity>) pipeline,
                        layer
                    )
                )
                .setShadowRadius(0.2F)
                .build(),
            context
        );
    }

    private static ResourceLocation resolveModelLocation(DismemberedLimbEntity entity) {
        var model = entity.getModelLocation();
        return model != null ? model : FALLBACK_MODEL;
    }

    private static ResourceLocation resolveTextureLocation(DismemberedLimbEntity entity) {
        var texture = entity.getTextureLocation();
        return texture != null ? texture : FALLBACK_TEXTURE;
    }
}
