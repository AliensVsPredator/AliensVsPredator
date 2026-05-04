package com.blib.internal.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mirrors hidden parts from the parent (entity) model onto the armor model so that when dismemberment hides a body part
 * (e.g. {@code head}), the armor draped over that part (e.g. a helmet) is hidden too. Without this hook the armor layer
 * keeps drawing because it operates on its own {@link HumanoidModel} instance and only consults the slot's expected
 * visibility, never the parent model's runtime overrides.
 * <p>
 * Injected at the tail of {@code setPartVisibility} so the slot-mask has already cleared all parts and re-enabled only
 * the slot's parts; we then turn off any of those that are also hidden on the parent.
 */
@Mixin(HumanoidArmorLayer.class)
public abstract class MixinHumanoidArmorLayer_Dismemberment<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {

    @Inject(method = "setPartVisibility", at = @At("RETURN"))
    private void blib$mirrorParentHiddenParts(A armorModel, EquipmentSlot slot, CallbackInfo ci) {
        // getParentModel is declared on RenderLayer (the superclass), not on HumanoidArmorLayer itself, so @Shadow
        // can't resolve it. Cast through the inheritance chain instead — at runtime `this` is the
        // HumanoidArmorLayer instance, which extends RenderLayer.
        @SuppressWarnings("unchecked")
        var parent = ((RenderLayer<T, M>) (Object) this).getParentModel();

        if (parent == null) {
            return;
        }

        if (!parent.head.visible) {
            armorModel.head.visible = false;
        }

        if (!parent.hat.visible) {
            armorModel.hat.visible = false;
        }

        if (!parent.body.visible) {
            armorModel.body.visible = false;
        }

        if (!parent.rightArm.visible) {
            armorModel.rightArm.visible = false;
        }

        if (!parent.leftArm.visible) {
            armorModel.leftArm.visible = false;
        }

        if (!parent.rightLeg.visible) {
            armorModel.rightLeg.visible = false;
        }

        if (!parent.leftLeg.visible) {
            armorModel.leftLeg.visible = false;
        }
    }
}
