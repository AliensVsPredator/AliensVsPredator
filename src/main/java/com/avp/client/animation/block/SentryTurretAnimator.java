package com.avp.client.animation.block;

import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzBlockAnimator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.common.block.entity.SentryTurretBlockEntity;

public class SentryTurretAnimator extends AzBlockAnimator<SentryTurretBlockEntity> {

    private static final ResourceLocation ANIMATIONS = AVPResources.blockAnimationLocation("sentry_turret");

    public SentryTurretAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<SentryTurretBlockEntity> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, "base_controller")
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(SentryTurretBlockEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(SentryTurretBlockEntity animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        var monster = animatable.getTargetedMonster();
        var boneCache = this.context().boneCache();
        var turretBody = boneCache.getBakedModel().getBone("gRotationJoint");
        if (turretBody.isPresent() && monster != null) {
            var monsterPos = Vec3.atCenterOf(monster.blockPosition());
            var turretPos = Vec3.atCenterOf(animatable.getBlockPos());
            var direction = monsterPos.subtract(turretPos).normalize();
            var pitch = (float) Math.toDegrees(Math.asin(direction.y));
            var yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90;

            turretBody.get().setRotX(pitch);
            turretBody.get().setRotY(yaw);
        }
    }
}
