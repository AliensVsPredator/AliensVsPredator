package com.blib.azurelib.common.animation.dispatch.command.action.impl.root;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import com.blib.azurelib.AzureLib;
import com.blib.azurelib.common.animation.AzAnimator;
import com.blib.azurelib.common.animation.dispatch.AzDispatchSide;
import com.blib.azurelib.common.animation.dispatch.command.action.AzAction;

public class AzRootCancelAllAction implements AzAction {

    public static final AzRootCancelAllAction INSTANCE = new AzRootCancelAllAction();

    public static final StreamCodec<FriendlyByteBuf, AzRootCancelAllAction> CODEC = StreamCodec.unit(INSTANCE);

    public static final ResourceLocation RESOURCE_LOCATION = AzureLib.modResource("root/cancel_all");

    private AzRootCancelAllAction() {}

    @Override
    public void handle(AzDispatchSide originSide, AzAnimator<?, ?> animator) {
        var controllerContainer = animator.getAnimationControllerContainer();
        var controllers = controllerContainer.getAll();

        controllers.forEach(controller -> controller.setCurrentAnimation(null));
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
