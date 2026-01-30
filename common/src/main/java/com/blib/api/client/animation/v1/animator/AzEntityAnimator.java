package com.blib.api.client.animation.v1.animator;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

import com.blib.internal.client.render.util.RenderUtil;
import com.blib.internal.common.molang.MolangParser;
import com.blib.internal.common.molang.MolangQueries;

public abstract class AzEntityAnimator<T extends Entity> extends AzAnimator<UUID, T> {

    protected AzEntityAnimator() {
        super();
    }

    protected AzEntityAnimator(AzAnimatorConfig config) {
        super(config);
    }

    @Override
    protected void applyMolangQueries(T entity, double animTime, float partialTicks) {
        super.applyMolangQueries(entity, animTime, partialTicks);

        var parser = MolangParser.INSTANCE;
        var minecraft = Minecraft.getInstance();

        parser.setMemoizedValue(
            MolangQueries.DISTANCE_FROM_CAMERA,
            () -> minecraft.gameRenderer.getMainCamera().getPosition().distanceTo(entity.position())
        );
        parser.setMemoizedValue(MolangQueries.IN_AIR, () -> RenderUtil.booleanToFloat(!entity.onGround()));
        parser.setMemoizedValue(MolangQueries.IS_ON_GROUND, () -> RenderUtil.booleanToFloat(entity.onGround()));
        parser.setMemoizedValue(MolangQueries.IS_IN_WATER, () -> RenderUtil.booleanToFloat(entity.isInWater()));
        parser.setMemoizedValue(
            MolangQueries.IS_IN_WATER_OR_RAIN,
            () -> RenderUtil.booleanToFloat(entity.isInWaterOrRain())
        );

        if (entity instanceof LivingEntity livingEntity) {
            parser.setMemoizedValue(
                MolangQueries.IS_BLOCKING,
                () -> RenderUtil.booleanToFloat(livingEntity.isBlocking())
            );
            parser.setMemoizedValue(
                MolangQueries.IS_USING_ITEM,
                () -> RenderUtil.booleanToFloat(livingEntity.isUsingItem())
            );
            parser.setMemoizedValue(MolangQueries.HEALTH, livingEntity::getHealth);
            parser.setMemoizedValue(MolangQueries.MAX_HEALTH, livingEntity::getMaxHealth);
            parser.setMemoizedValue(MolangQueries.GROUND_SPEED, () -> {
                var velocity = livingEntity.getDeltaMovement();
                return Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
            });
            parser.setMemoizedValue(MolangQueries.YAW_SPEED, () -> livingEntity.getYRot() - livingEntity.yRotO);
            parser.setValue(
                MolangQueries.HEAD_YAW,
                () -> livingEntity.getViewYRot(partialTicks) - Mth.lerp(
                    partialTicks,
                    livingEntity.yBodyRotO,
                    livingEntity.yBodyRot
                )
            );
            parser.setValue(MolangQueries.HEAD_PITCH, () -> livingEntity.getViewXRot(partialTicks));
            parser.setValue(
                MolangQueries.HURT_TIME,
                () -> livingEntity.hurtTime == 0 ? 0 : livingEntity.hurtTime - partialTicks
            );
            parser.setValue(MolangQueries.IS_BABY, () -> RenderUtil.booleanToFloat(livingEntity.isBaby()));
            parser.setValue(MolangQueries.LIMB_SWING, livingEntity.walkAnimation::position);
            parser.setValue(MolangQueries.LIMB_SWING_AMOUNT, () -> livingEntity.walkAnimation.speed(partialTicks));
        }
    }
}
