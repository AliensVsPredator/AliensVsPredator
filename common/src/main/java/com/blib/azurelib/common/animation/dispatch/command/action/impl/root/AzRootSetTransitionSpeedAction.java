package com.blib.azurelib.common.animation.dispatch.command.action.impl.root;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import com.blib.azurelib.AzureLib;
import com.blib.azurelib.common.animation.AzAnimator;
import com.blib.azurelib.common.animation.dispatch.AzDispatchSide;
import com.blib.azurelib.common.animation.dispatch.command.action.AzAction;

public record AzRootSetTransitionSpeedAction(
    float transitionSpeed
) implements AzAction {

    public static final StreamCodec<FriendlyByteBuf, AzRootSetTransitionSpeedAction> CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        AzRootSetTransitionSpeedAction::transitionSpeed,
        AzRootSetTransitionSpeedAction::new
    );

    public static final ResourceLocation RESOURCE_LOCATION = AzureLib.modResource("root/set_transition_speed");

    @Override
    public void handle(AzDispatchSide originSide, AzAnimator<?, ?> animator) {
        animator.getAnimationControllerContainer()
            .getAll()
            .forEach(
                controller -> controller.setAnimationProperties(
                    controller.animationProperties().withTransitionLength(transitionSpeed)
                )
            );
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
