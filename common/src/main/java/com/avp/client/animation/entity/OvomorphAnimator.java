package com.avp.client.animation.entity;

import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.common.entity.living.alien.ovomorph.Ovomorph;
import com.avp.common.entity.living.alien.ovomorph.OvomorphAnimationRefs;

public class OvomorphAnimator extends AzEntityAnimator<Ovomorph> {

    // TODO: Change this to "ovomorph" with 0.2.0.
    private static final String NAME = "ovamorph";

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
            gVeinBottom.setHidden(!ovomorph.isRooted());
        }

        runPassiveAnimations(ovomorph);
    }

    private void runPassiveAnimations(Ovomorph ovomorph) {
        if (ovomorph.hatchManager().isHatching()) {
            ovomorph.getAnimationDispatcher().open();
        } else if (ovomorph.hatchManager().isHatched()) {
            ovomorph.getAnimationDispatcher().openHold();
        } else {
            ovomorph.getAnimationDispatcher().closeHold();
        }
    }
}
