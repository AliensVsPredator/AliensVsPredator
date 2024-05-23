package org.avp.common.entity.living;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import org.avp.common.entity.ai.AIUtils;
import org.avp.common.tag.AVPEntityTypeTags;

public class Engineer extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Integer> ENGINEER_TYPE = SynchedEntityData.defineId(
        Engineer.class,
        EntityDataSerializers.INT
    );

    private static final EntityDataAccessor<Boolean> HAS_HELMET = SynchedEntityData.defineId(
        Engineer.class,
        EntityDataSerializers.BOOLEAN
    );

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public Engineer(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        AIUtils.addBasicAI(this, goalSelector);

        this.targetSelector.addGoal(
            2,
            new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                true,
                livingEntity -> !livingEntity.getType().is(AVPEntityTypeTags.ENGINEERS)
            )
        );
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ENGINEER_TYPE, this.random.nextInt(2));
        this.entityData.define(HAS_HELMET, this.random.nextBoolean());
    }

    public boolean hasHelmet() {
        return this.entityData.get(HAS_HELMET);
    }

    public int getSuitType() {
        return this.entityData.get(ENGINEER_TYPE);
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
