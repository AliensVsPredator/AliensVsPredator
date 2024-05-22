package org.avp.common.entity.living;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.avp.common.entity.ai.AIUtils;
import org.avp.common.sound.AVPSoundEvents;

public class Boiler extends Monster implements GeoEntity {

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public Boiler(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        this.playSound(AVPSoundEvents.INSTANCE.entityXenomorphAttack.get());
        return super.doHurtTarget(entity);
    }

    @Override
    protected void registerGoals() {
        AIUtils.addHiveAlienAI(this, goalSelector, targetSelector);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return AVPSoundEvents.INSTANCE.entityXenomorphAmbient.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return AVPSoundEvents.INSTANCE.entityXenomorphDeath.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return AVPSoundEvents.INSTANCE.entityXenomorphHurt.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // TODO:
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
