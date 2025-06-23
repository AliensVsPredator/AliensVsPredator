package com.alien.client.animation.entity;

import com.alien.common.constant.animation.OvomorphAnimationRefs;
import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class OvomorphAnimator extends AzEntityAnimator<Ovomorph> {

    private static final String NAME = "ovomorph";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public OvomorphAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Ovomorph> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, OvomorphAnimationRefs.BASE_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Ovomorph animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Ovomorph ovomorph, float partialTicks) {
        var bakedModel = context().boneCache().getBakedModel();
        var gVeinBottom = bakedModel.getBoneOrNull("gVeinBottom");

        if (gVeinBottom != null) {
            gVeinBottom.setHidden(!ovomorph.isRooted.get());
        }

        runPassiveAnimations(ovomorph);
    }

    private void runPassiveAnimations(Ovomorph ovomorph) {
        if (ovomorph.getHatchManager().isHatching()) {
            ovomorph.getAnimationDispatcher().open();
        } else if (ovomorph.getHatchManager().isHatched()) {
            ovomorph.getAnimationDispatcher().openHold();
        } else {
            ovomorph.getAnimationDispatcher().closeHold();
        }
    }
}
