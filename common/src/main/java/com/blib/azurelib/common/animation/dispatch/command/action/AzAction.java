package com.blib.azurelib.common.animation.dispatch.command.action;

import net.minecraft.resources.ResourceLocation;

import com.blib.azurelib.common.animation.AzAnimator;
import com.blib.azurelib.common.animation.dispatch.AzDispatchSide;
import com.blib.azurelib.common.animation.dispatch.command.action.codec.AzActionCodec;

public interface AzAction {

    AzActionCodec CODEC = new AzActionCodec();

    void handle(AzDispatchSide originSide, AzAnimator<?, ?> animator);

    ResourceLocation getResourceLocation();
}
