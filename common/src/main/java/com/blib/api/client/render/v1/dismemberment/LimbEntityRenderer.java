package com.blib.api.client.render.v1.dismemberment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import com.blib.api.client.render.v1.entity.AzEntityRenderer;
import com.blib.api.client.render.v1.entity.AzEntityRendererConfig;
import com.blib.api.client.render.v1.entity.pipeline.AzEntityRendererPipeline;
import com.blib.api.common.dismemberment.v1.entity.DismemberedLimbEntity;
import com.blib.internal.mixin.MixinEntityRenderDispatcher_Accessor;
import com.blib.mod.BLib;

/**
 * Generic renderer for {@link DismemberedLimbEntity}.
 * <p>
 * Resolves model/texture by looking up the source mob's own {@link net.minecraft.client.renderer.entity.EntityRenderer}
 * via the limb's resolved ghost — that way variant-specific textures and any other state captured in the source NBT
 * carry through. The model renderer is swapped to {@link LimbEntityModelRenderer} which dispatches to a vanilla
 * {@code ModelPart} render path or to the geo subtree path depending on whether the source's renderer is BLib geo or
 * vanilla {@code LivingEntityRenderer}.
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
                .setShadowRadius(0F)
                .build(),
            context
        );
    }

    private static ResourceLocation resolveModelLocation(DismemberedLimbEntity limb) {
        var ghost = limb.getOrCreateGhost();

        if (ghost == null) {
            return FALLBACK_MODEL;
        }

        var renderer = lookupSourceRenderer(ghost);

        // Only the geo path needs a model location bake. Vanilla limbs render through ModelPart
        // directly inside LimbEntityModelRenderer and never consult this value.
        if (renderer instanceof AzEntityRenderer<?> geoRenderer) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            var rawConfig = (AzEntityRendererConfig) geoRenderer.config();
            return rawConfig.modelLocation(ghost, ghost);
        }

        return FALLBACK_MODEL;
    }

    private static ResourceLocation resolveTextureLocation(DismemberedLimbEntity limb) {
        var ghost = limb.getOrCreateGhost();

        if (ghost == null) {
            return FALLBACK_TEXTURE;
        }

        var renderer = lookupSourceRenderer(ghost);

        if (renderer == null) {
            return FALLBACK_TEXTURE;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        var raw = (EntityRenderer) renderer;
        return raw.getTextureLocation(ghost);
    }

    private static @Nullable EntityRenderer<?> lookupSourceRenderer(LivingEntity ghost) {
        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        return ((MixinEntityRenderDispatcher_Accessor) dispatcher).blib$getRenderers().get(ghost.getType());
    }
}
