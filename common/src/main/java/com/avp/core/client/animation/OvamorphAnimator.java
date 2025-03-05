package com.avp.core.client.animation;

import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.core.AVPResources;
import com.avp.core.common.entity.living.alien.ovamorph.Ovamorph;
import com.avp.core.common.entity.living.alien.ovamorph.OvamorphAnimationRefs;

public class OvamorphAnimator extends AzEntityAnimator<Ovamorph> {

    private static final String NAME = "ovamorph";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public OvamorphAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Ovamorph> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, OvamorphAnimationRefs.BASE_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Ovamorph animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Ovamorph ovamorph, float partialTicks) {
        var bakedModel = context().boneCache().getBakedModel();
        var gVeinBottom = bakedModel.getBoneOrNull("gVeinBottom");

        if (gVeinBottom != null) {
            gVeinBottom.setHidden(!ovamorph.isRooted());
        }
    }
}
