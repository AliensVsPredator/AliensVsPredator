package com.blib.internal.mixin.posteffect;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blib.internal.client.posteffect.BLibIrisCompat;
import com.blib.internal.client.shader.BLibThermalState;

/**
 * When thermal vision is active, substitute alpha-blended entity render types ({@code entityTranslucent},
 * {@code itemEntityTranslucentCull}) with their non-blended cutout equivalents. Reason: alpha blending applies to
 * every color attachment of the MainTarget MRT, and on some drivers the {@code SRC_ALPHA} blend factor for our
 * single-channel mask attachment evaluates to 0 — silently zeroing the mask write and making the entity
 * invisible-to-thermal. {@code PlayerModel}-derived entities (players, piglins, zombie piglins) hit this path.
 *
 * <p>Visual cost is essentially zero: {@code rendertype_entity_cutout_no_cull} uses {@code discard} for transparent
 * pixels rather than alpha blending; for player/piglin textures the body is fully opaque so no pixels are affected,
 * and any sub-100% alpha pixels (rare on entity skins) become hard cutoffs at the 0.1 threshold rather than
 * blended. Only matters during the thermal post-effect, which doesn't care about edge alpha precision anyway.
 *
 * <p>Returns from the original method via {@code @Inject(cancellable=true)} when the substitution applies.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer_ThermalRenderType {

    @Inject(method = "getRenderType", at = @At("RETURN"), cancellable = true)
    private void blib$forceCutoutForThermal(
        LivingEntity livingEntity,
        boolean bodyVisible,
        boolean translucent,
        boolean glowing,
        CallbackInfoReturnable<RenderType> cir
    ) {
        if (!BLibThermalState.isActive() || BLibIrisCompat.isShaderModActive()) {
            return;
        }

        var rt = cir.getReturnValue();

        if (rt == null) {
            return;
        }

        var name = rt.toString();

        // Match by RenderType.toString output. Format is "minecraft:CompositeRenderType[entity_translucent...]".
        // We catch both entityTranslucent (PlayerModel-derived) and itemEntityTranslucentCull (used when entity is
        // visible-to-self, e.g. while spectating). entityCutoutNoCull keeps NEW_ENTITY vertex format compatibility
        // and just swaps the shader + blend state.
        if (name.contains("entity_translucent") || name.contains("item_entity_translucent_cull")) {
            //noinspection unchecked
            var self = (LivingEntityRenderer<LivingEntity, ?>) (Object) this;
            cir.setReturnValue(RenderType.entityCutoutNoCull(self.getTextureLocation(livingEntity)));
        }
    }
}
