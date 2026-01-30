package com.blib.api.client.render.v1.entity.pipeline;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import com.blib.api.client.render.v1.AzRendererPipeline;
import com.blib.api.client.render.v1.AzRendererPipelineContext;

public class AzEntityRendererPipelineContext<T extends Entity> extends AzRendererPipelineContext<UUID, T> {

    public AzEntityRendererPipelineContext(AzRendererPipeline<UUID, T> rendererPipeline) {
        super(rendererPipeline);
    }

    @Override
    public RenderType getDefaultRenderType(
        T animatable,
        ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource,
        float partialTick,
        RenderType defaultRenderType,
        float alpha
    ) {
        var player = Minecraft.getInstance().player;
        var translucent = animatable.isInvisible() && !animatable.isInvisibleTo(player);
        var visibleBody = !animatable.isInvisible(); // strictly “visible flag”
        var glowing = Minecraft.getInstance().shouldEntityAppearGlowing(animatable);
        var hurtOrDead = animatable instanceof LivingEntity living && (living.hurtTime > 1 || living.isDeadOrDying());

        // Handle entity damage/death state
        if (visibleBody && !glowing && hurtOrDead) {
            if (
                defaultRenderType == RenderType.entityTranslucentCull(texture) || defaultRenderType == RenderType
                    .entityTranslucent(texture)
            ) {
                return RenderType.entityCutoutNoCull(texture);
            }
            return defaultRenderType;
        }

        // Handle transparency
        if (visibleBody && alpha < 1.0F) {
            return RenderType.entityTranslucent(texture);
        }

        // --- Vanilla-style fallback ---
        if (translucent) {
            return RenderType.entityTranslucent(texture);
        } else if (visibleBody) {
            return defaultRenderType;
        } else if (glowing) {
            return RenderType.outline(texture);
        } else {
            return null;
        }
    }

    @Override
    public int getPackedOverlay(T entity, float u, float partialTick) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return OverlayTexture.NO_OVERLAY;
        }

        return OverlayTexture.pack(
            OverlayTexture.u(u),
            OverlayTexture.v(livingEntity.hurtTime > 0 || livingEntity.deathTime > 0)
        );
    }
}
