package com.avp.client.animation;

import com.avp.AVPResources;
import com.avp.common.entity.living.alien.xenomorph.queen.Queen;
import com.avp.common.entity.living.alien.xenomorph.queen.QueenAnimationRefs;
import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class QueenAnimator extends AzEntityAnimator<Queen> {

    private static final String NAME = "queen";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public QueenAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Queen> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, QueenAnimationRefs.TAIL_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Queen animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Queen animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        var bakedModel = context().boneCache().getBakedModel();
        var eggSack = bakedModel.getBoneOrNull("root2");
        if (eggSack != null) {
            eggSack.setHidden(true);
        }
    }
}
