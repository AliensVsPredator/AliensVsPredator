package org.avp.common.game.entity.living.alien.base_line;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.avp.common.game.entity.type.Morphable;
import org.avp.common.data.entity.living.alien.base_line.ChestbursterData;
import org.avp.common.game.sound.AVPSoundEventRegistry;
import org.jetbrains.annotations.NotNull;

import org.avp.common.ai.AIUtils;

public class Nauticomorph extends Monster implements Morphable, GeoEntity {

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public Nauticomorph(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        this.playSound(AVPSoundEventRegistry.INSTANCE.entityXenomorphAttack.get());
        return super.doHurtTarget(entity);
    }

    @Override
    protected void registerGoals() {
        AIUtils.addAlienAI(this, goalSelector, targetSelector);
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
        return ChestbursterData.INSTANCE.getHolder().get();
    }
}
