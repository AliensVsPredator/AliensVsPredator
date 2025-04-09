package com.avp.common.entity.living.alien;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class RoyalAlien extends Alien {

    private static final String IS_ROYAL_AFFLICTED_KEY = "isRoyalAfflicted";

    private static final EntityDataAccessor<Boolean> IS_ROYAL = SynchedEntityData.defineId(RoyalAlien.class, EntityDataSerializers.BOOLEAN);

    protected RoyalAlien(EntityType<? extends RoyalAlien> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_ROYAL, false);
    }

    public boolean isRoyal() {
        return entityData.get(IS_ROYAL);
    }

    public void setIsRoyal(boolean isRoyal) {
        entityData.set(IS_ROYAL, isRoyal);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        setIsRoyal(compoundTag.getBoolean(IS_ROYAL_AFFLICTED_KEY));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean(IS_ROYAL_AFFLICTED_KEY, isRoyal());
    }
}
