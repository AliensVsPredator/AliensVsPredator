package com.blib.internal.mixin.posteffect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.client.posteffect.BLibHeldItemRenderState;

/**
 * Pushes the held-item flag around {@link ItemInHandLayer#render}, so third-person held items (other players, mobs,
 * your own player when in third-person) bypass thermal recoloring just like the first-person hand.
 * <p>
 * <b>Force-flush at the boundary.</b> Vanilla MC queues entity-render vertices into a single shared
 * {@code ByteBufferBuilder} via {@code BufferSource.getBuffer}, and {@code getBuffer} for a non-fixed render type
 * triggers an implicit {@code endBatch(lastSharedType)} so the previous render type's buffer can reuse the shared
 * builder. That means a held item that internally calls {@code getBuffer} for a non-fixed render type — banner-pattern
 * layers (banners and shields), skull/head materials, etc. — will silently flush <em>whatever the previous shared
 * render type was</em> in the middle of {@code ItemInHandLayer.render}. Without flushing here at HEAD, that previous
 * shared render type is typically the entity's body or armor (queued just before the layer fired), so the body/armor
 * vertices end up flushing with {@code BlibHeldItem = 1} — they get tagged as held items and rendered through the
 * held-item branch of the post-effect, turning bright armor pixels into "held item" colors (e.g. pitch-black or
 * otherwise out of place).
 * <p>
 * The fix: explicitly {@code endBatch()} the buffer source at HEAD before pushing the flag (so any pending body/armor
 * vertices flush under {@code BlibHeldItem = 0}), and again at RETURN before popping (so the held item's vertices flush
 * under {@code BlibHeldItem = 1}). The cost is a couple of extra GL draw calls per entity-with-a-held-item, which is
 * negligible.
 */
@Mixin(ItemInHandLayer.class)
public abstract class MixinItemInHandLayer_HeldItemFlag {

    @Inject(method = "render", at = @At("HEAD"))
    private void blib$pushHeldItemFlag(
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        LivingEntity livingEntity,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch,
        CallbackInfo ci
    ) {
        if (buffer instanceof MultiBufferSource.BufferSource bufferSource) {
            bufferSource.endBatch();
        }

        BLibHeldItemRenderState.push();
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void blib$popHeldItemFlag(
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        LivingEntity livingEntity,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch,
        CallbackInfo ci
    ) {
        if (buffer instanceof MultiBufferSource.BufferSource bufferSource) {
            bufferSource.endBatch();
        }

        BLibHeldItemRenderState.pop();
    }
}
