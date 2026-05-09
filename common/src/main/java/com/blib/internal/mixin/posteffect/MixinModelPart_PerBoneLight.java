package com.blib.internal.mixin.posteffect;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.blib.api.client.posteffect.v1.BLibPerBoneLightContext;

/**
 * Replaces vanilla MC's per-entity {@code packedLight} with a per-bone value sampled at each cube's actual world
 * position when thermal vision is active. Vanilla's entity {@link PoseStack} is freshly created per-frame in
 * {@code LevelRenderer.renderLevel} and is NOT pre-loaded with the camera-rotation matrix (that goes onto the separate
 * {@code RenderSystem.getModelViewStack()} which only the shader's {@code ModelViewMat} uniform sees). So at
 * {@code compile()} time the PoseStack accumulates only:
 *
 * <pre>
 *   [translate(entity_pos − camera_pos)] [entity yaw] [entity rotation animations] [bone hierarchy]
 * </pre>
 *
 * Transforming the local origin {@code (0, 0, 0)} by this matrix gives the bone's <em>world-relative-to-camera</em>
 * position directly — no view-rotation undo needed. Adding the captured camera position lands at absolute world
 * coordinates we can pass to {@code level.getBrightness()} for fresh per-bone block + sky light coords.
 * <p>
 * The new {@code packedLight} flows through the existing MRT pipeline naturally: vanilla bakes it into the vertex
 * {@code UV2} attribute via {@code VertexConsumer.uv2(packedLight)}, the patcher extracts {@code UV2.x/240} into
 * {@code blib_lightCoord.x}, and the fragment shader writes that into {@code entityDrawData.g}. The thermal post shader
 * reads {@code entityDrawData.g} for the heat formula — which now varies per bone.
 * <p>
 * No-op outside thermal mode (gated by null context).
 */
@Mixin(ModelPart.class)
public abstract class MixinModelPart_PerBoneLight {

    @ModifyVariable(method = "compile", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int blib$overridePackedLight(int packedLight, @Local(argsOnly = true) PoseStack.Pose pose) {
        var ctx = BLibPerBoneLightContext.current();

        if (ctx == null) {
            return packedLight;
        }

        // pose.pose() at compile time is [translate(entity_pos − camera_pos)] * [yaw] * [rotations] * [bones], with
        // NO camera rotation in it (that's on the separate GL modelview stack). Transforming the bone's local
        // origin by this matrix gives world-relative-to-camera position directly.
        var localOrigin = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        pose.pose().transform(localOrigin);

        var cam = ctx.cameraPos();
        var bonePos = BlockPos.containing(cam.x + localOrigin.x, cam.y + localOrigin.y, cam.z + localOrigin.z);

        var level = ctx.level();
        int blockLight = Math.max(level.getBrightness(LightLayer.BLOCK, bonePos), ctx.blockLightFloor());
        int skyLight = level.getBrightness(LightLayer.SKY, bonePos);

        return LightTexture.pack(blockLight, skyLight);
    }
}
