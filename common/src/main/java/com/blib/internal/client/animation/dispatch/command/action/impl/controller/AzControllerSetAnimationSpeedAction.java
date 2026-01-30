package com.blib.internal.client.animation.dispatch.command.action.impl.controller;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.internal.client.animation.dispatch.AzDispatchSide;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;
import com.blib.mod.BLib;

public record AzControllerSetAnimationSpeedAction(
    String controllerName,
    double animationSpeed
) implements AzAction {

    public static final StreamCodec<FriendlyByteBuf, AzControllerSetAnimationSpeedAction> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        AzControllerSetAnimationSpeedAction::controllerName,
        ByteBufCodecs.DOUBLE,
        AzControllerSetAnimationSpeedAction::animationSpeed,
        AzControllerSetAnimationSpeedAction::new
    );

    public static final ResourceLocation RESOURCE_LOCATION = BLib.MOD.resources().createLocation("controller/set_animation_speed");

    @Override
    public void handle(AzDispatchSide originSide, AzAnimator<?, ?> animator) {
        var controller = animator.getAnimationControllerContainer().getOrNull(controllerName);

        if (controller != null) {
            controller.setAnimationProperties(controller.animationProperties().withAnimationSpeed(animationSpeed));
        }
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
