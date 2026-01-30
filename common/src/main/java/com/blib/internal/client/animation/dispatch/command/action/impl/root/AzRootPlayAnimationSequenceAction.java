package com.blib.internal.client.animation.dispatch.command.action.impl.root;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.sequence.AzAnimationSequence;
import com.blib.internal.client.animation.dispatch.AzDispatchSide;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;
import com.blib.mod.BLib;

public record AzRootPlayAnimationSequenceAction(
    AzAnimationSequence sequence
) implements AzAction {

    public static final StreamCodec<FriendlyByteBuf, AzRootPlayAnimationSequenceAction> CODEC = StreamCodec.composite(
        AzAnimationSequence.CODEC,
        AzRootPlayAnimationSequenceAction::sequence,
        AzRootPlayAnimationSequenceAction::new
    );

    public static final ResourceLocation RESOURCE_LOCATION = BLib.MOD.resources().createLocation("root/play_animation_sequence");

    @Override
    public void handle(AzDispatchSide originSide, AzAnimator<?, ?> animator) {
        var controllerContainer = animator.getAnimationControllerContainer();
        var controllers = controllerContainer.getAll();

        controllers.forEach(controller -> controller.run(originSide, sequence));
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
