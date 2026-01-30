package com.blib.internal.client.animation.dispatch.command.action;

import net.minecraft.resources.ResourceLocation;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.internal.client.animation.dispatch.AzDispatchSide;
import com.blib.internal.client.animation.dispatch.command.action.codec.AzActionCodec;

public interface AzAction {

    AzActionCodec CODEC = new AzActionCodec();

    void handle(AzDispatchSide originSide, AzAnimator<?, ?> animator);

    ResourceLocation getResourceLocation();
}
