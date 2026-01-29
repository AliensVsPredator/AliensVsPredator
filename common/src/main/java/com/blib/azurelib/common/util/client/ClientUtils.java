package com.blib.azurelib.common.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import com.blib.azurelib.AzureLib;
import com.blib.azurelib.common.animation.AzAnimatorAccessor;
import com.blib.azurelib.common.animation.controller.AzAnimationController;
import com.blib.azurelib.common.animation.primitive.AzQueuedAnimation;

public record ClientUtils() {

    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static Level getLevel() {
        return Minecraft.getInstance().level;
    }

    public static AzAnimationController<Object> getCurrentAnimationController(Object target, String controllerName) {
        var animator = AzAnimatorAccessor.getOrNull(target);

        if (animator == null) {
            AzureLib.LOGGER.warn("Could not find animator for target: {}", target);
            return null;
        }

        var animationController = animator.getAnimationControllerContainer().getOrNull(controllerName);

        if (animationController == null) {
            AzureLib.LOGGER.warn("No animation controller found with name '{}' for target: {}", controllerName, target);
        }

        return animationController;
    }

    public static double getCurrentAnimationTick(Object target, String controllerName) {
        if (target instanceof Entity entity && !entity.level().isClientSide) {
            AzureLib.LOGGER.warn("Animation tick can only be retrieved on the client side for target: {}", target);
            return 0D;
        }

        AzAnimationController<Object> animationController = ClientUtils.getCurrentAnimationController(
            target,
            controllerName
        );

        if (animationController == null) {
            AzureLib.LOGGER.warn(
                "No animation controller available for target: {} controller: {}",
                target,
                controllerName
            );
            return 0D;
        }

        return animationController.controllerTimer().getAdjustedTick();
    }

    public static double getCurrentAnimationLength(Object target, String controllerName) {
        AzAnimationController<Object> animationController = ClientUtils.getCurrentAnimationController(
            target,
            controllerName
        );

        if (animationController == null) {
            AzureLib.LOGGER.warn("No animation controller found for target: {} controller: {}", target, controllerName);
            return 0D;
        }

        AzQueuedAnimation currentAnimation = animationController.currentAnimation();
        if (currentAnimation == null) {
            AzureLib.LOGGER.warn("No current animation found for target: {} controller: {}", target, controllerName);
            return 0D;
        }

        return currentAnimation.animation().length();
    }
}
