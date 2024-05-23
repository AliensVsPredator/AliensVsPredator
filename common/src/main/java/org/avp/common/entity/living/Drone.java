package org.avp.common.entity.living;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.avp.common.entity.ai.AIUtils;
import org.avp.common.sound.AVPSoundEvents;
import org.jetbrains.annotations.NotNull;

public class Drone extends Monster implements GeoEntity {

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public Drone(EntityType<? extends Monster> entityType, Level level) {
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // TODO:
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
