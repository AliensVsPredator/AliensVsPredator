package org.avp.common.game.entity.living.alien.runner_line;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import org.avp.common.ai.AIUtils;
import org.avp.common.data.entity.living.alien.runner_line.DroneRunnerData;
import org.avp.common.game.entity.type.Morphable;
import org.avp.common.game.sound.AVPSoundEventRegistry;

public class WarriorRunner extends Monster implements Morphable, GeoEntity {

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public WarriorRunner(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        this.playSound(AVPSoundEventRegistry.INSTANCE.entityXenomorphAttack.get());
        return super.doHurtTarget(entity);
    }

    @Override
    protected void registerGoals() {
        AIUtils.addAlienAI(this, goalSelector, targetSelector);
        goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // TODO:
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public EntityType<?> getEntityTypeForPreviousForm() {
        return DroneRunnerData.INSTANCE.getHolder().get();
    }
}
