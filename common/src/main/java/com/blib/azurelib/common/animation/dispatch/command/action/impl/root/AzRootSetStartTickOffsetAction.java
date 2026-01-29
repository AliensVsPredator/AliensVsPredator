package com.blib.azurelib.common.animation.dispatch.command.action.impl.root;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import com.blib.azurelib.AzureLib;
import com.blib.azurelib.common.animation.AzAnimator;
import com.blib.azurelib.common.animation.dispatch.AzDispatchSide;
import com.blib.azurelib.common.animation.dispatch.command.action.AzAction;

public record AzRootSetStartTickOffsetAction(
    double startTickOffset
) implements AzAction {

    public static final StreamCodec<FriendlyByteBuf, AzRootSetStartTickOffsetAction> CODEC = StreamCodec.composite(
        ByteBufCodecs.DOUBLE,
        AzRootSetStartTickOffsetAction::startTickOffset,
        AzRootSetStartTickOffsetAction::new
    );

    public static final ResourceLocation RESOURCE_LOCATION = AzureLib.modResource("root/set_start_tick_offset");

    @Override
    public void handle(AzDispatchSide originSide, AzAnimator<?, ?> animator) {
        animator.getAnimationControllerContainer()
            .getAll()
            .forEach(
                controller -> controller.setAnimationProperties(
                    controller.animationProperties().withStartTickOffset(startTickOffset)
                )
            );
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
