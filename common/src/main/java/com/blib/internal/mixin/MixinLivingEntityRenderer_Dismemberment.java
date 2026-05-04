package com.blib.internal.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import com.blib.api.client.render.v1.dismemberment.VanillaLimbRenderer;
import com.blib.api.common.dismemberment.v1.Dismemberable;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;

/**
 * Hides {@link ModelPart}s that correspond to detached limbs while a vanilla {@code LivingEntityRenderer} renders the
 * entity. The detached set is read from the entity's {@link Dismemberable} manager, and the {@code rootBoneName} of
 * each detached limb's {@code LimbDefinition} is interpreted as a top-level child name on the model's root part. The
 * previous {@code visible} state is restored after the render so the same shared model instance is unaffected for other
 * entities of the same type.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer_Dismemberment {

    @Shadow
    protected EntityModel<?> model;

    @Unique
    private final List<ModelPart> blib$hiddenParts = new ArrayList<>();

    @Unique
    private final List<Boolean> blib$previousVisibility = new ArrayList<>();

    @Inject(method = "render", at = @At("HEAD"))
    private void blib$hideDetachedParts(
        LivingEntity entity,
        float entityYaw,
        float partialTick,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        CallbackInfo ci
    ) {
        if (!(entity instanceof Dismemberable dismemberable)) {
            return;
        }

        var manager = dismemberable.getDismembermentManager();

        if (manager == null || !manager.hasAnyDetached()) {
            return;
        }

        var definitions = LimbDefinitionRegistry.getDefinitions(entity.getType());

        if (definitions.isEmpty()) {
            return;
        }

        for (var definition : definitions) {
            if (!manager.isDetached(definition)) {
                continue;
            }

            blib$hidePartIfPresent(definition.rootBoneName());

            for (var companionBoneName : definition.companionBoneNames()) {
                blib$hidePartIfPresent(companionBoneName);
            }
        }
    }

    @Unique
    private void blib$hidePartIfPresent(String partName) {
        var part = VanillaLimbRenderer.findModelPart(model, partName);

        if (part == null) {
            return;
        }

        blib$hiddenParts.add(part);
        blib$previousVisibility.add(part.visible);
        part.visible = false;
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void blib$restoreParts(
        LivingEntity entity,
        float entityYaw,
        float partialTick,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        CallbackInfo ci
    ) {
        if (blib$hiddenParts.isEmpty()) {
            return;
        }

        for (var i = 0; i < blib$hiddenParts.size(); i++) {
            blib$hiddenParts.get(i).visible = blib$previousVisibility.get(i);
        }

        blib$hiddenParts.clear();
        blib$previousVisibility.clear();
    }
}
