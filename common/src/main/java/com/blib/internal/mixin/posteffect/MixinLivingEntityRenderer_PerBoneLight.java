package com.blib.internal.mixin.posteffect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.api.common.tag.v1.BLibEntityTypeTags;
import com.blib.internal.client.posteffect.BLibIrisCompat;
import com.blib.internal.client.posteffect.BLibPerBoneLightContext;
import com.blib.internal.client.shader.BLibThermalState;

/**
 * Two-part hook into {@code LivingEntityRenderer.render}, both gated on thermal-vision-active:
 *
 * <p><b>Tag-based visibility filter.</b> If the entity's type isn't in
 * {@link BLibEntityTypeTags#THERMAL_VISIBLE}, the render call is cancelled — the entity isn't drawn into the
 * gbuffer, so its pixels keep the cold-world heat behind them and the entity reads as "invisible" in IR. Tag is
 * empty by default; modders/datapacks declare which mob types should be detectable.
 *
 * <p><b>Per-bone lighting context.</b> For visible entities, pushes a {@link BLibPerBoneLightContext} frame so the
 * downstream {@code ModelPart.compile} mixin can sample world block-light at each bone's pose-stack-derived
 * position. Popped on return. When thermal is off, both hooks no-op and vanilla render is untouched.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer_PerBoneLight {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void blib$thermalGateAndContextPush(
        LivingEntity entity,
        float entityYaw,
        float partialTicks,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        CallbackInfo ci
    ) {
        if (!BLibThermalState.isActive() || BLibIrisCompat.isShaderModActive()) {
            return;
        }

        // Tag-gate: skip rendering entities not in THERMAL_VISIBLE so their pixels show through to whatever heat
        // values the world geometry behind them wrote (= cold). Visually, the entity is invisible in IR mode.
        if (!entity.getType().is(BLibEntityTypeTags.THERMAL_VISIBLE)) {
            ci.cancel();
            return;
        }

        var mc = Minecraft.getInstance();
        var camera = mc.gameRenderer.getMainCamera();

        if (mc.level == null || !camera.isInitialized()) {
            return;
        }

        // Block-light floor for the per-bone packedLight mixin. Vanilla MC light coords are 0-15:
        //   - THERMAL_HOT entities (blazes, magma cubes, striders, ghasts): floor = 14 (bright orange/red).
        //   - Regular thermal-visible entities: floor = 7 (mid-warm — warm-blooded body baseline shows even
        //     when the entity is in a dark area, but still distinguishable from genuine heat sources).
        // The mixin takes max(world-block-light, floor), so entities standing near actual light still read
        // brighter than the floor and entities far from any light still register as living-warm.
        var blockLightFloor = entity.getType().is(BLibEntityTypeTags.THERMAL_HOT) ? 14 : 7;

        BLibPerBoneLightContext.push(mc.level, camera.getPosition(), blockLightFloor);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void blib$popPerBoneLightContext(
        LivingEntity entity,
        float entityYaw,
        float partialTicks,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        CallbackInfo ci
    ) {
        BLibPerBoneLightContext.pop();
    }
}
