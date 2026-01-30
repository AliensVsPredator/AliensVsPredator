package com.blib.internal.client.animation.dispatch.command.action.impl.controller;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.internal.client.animation.dispatch.AzDispatchSide;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;
import com.blib.internal.client.animation.dispatch.command.sequence.AzAnimationSequence;
import com.blib.mod.BLib;

public record AzControllerPlayAnimationSequenceAction(
    String controllerName,
    AzAnimationSequence sequence
) implements AzAction {

    public static final StreamCodec<FriendlyByteBuf, AzControllerPlayAnimationSequenceAction> CODEC = StreamCodec
        .composite(
            ByteBufCodecs.STRING_UTF8,
            AzControllerPlayAnimationSequenceAction::controllerName,
            AzAnimationSequence.CODEC,
            AzControllerPlayAnimationSequenceAction::sequence,
            AzControllerPlayAnimationSequenceAction::new
        );

    public static final ResourceLocation RESOURCE_LOCATION = BLib.MOD.resources().createLocation("controller/play_animation_sequence");

    @Override
    public void handle(AzDispatchSide originSide, AzAnimator<?, ?> animator) {
        var controller = animator.getAnimationControllerContainer().getOrNull(controllerName);

        if (controller != null) {
            controller.run(originSide, sequence);
        }
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
