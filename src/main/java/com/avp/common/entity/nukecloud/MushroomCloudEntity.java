package com.avp.common.entity.nukecloud;

import com.avp.common.entity.type.AVPEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MushroomCloudEntity extends Entity {

    protected MushroomCloudAnimDispatcher animDispatcher;

    public MushroomCloudEntity(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
        noCulling = true;
        animDispatcher = new MushroomCloudAnimDispatcher(this);
    }

    public MushroomCloudEntity(Level level, double x, double y, double z) {
        super(AVPEntityTypes.MUSHROOM_CLOUD, level);
        animDispatcher = new MushroomCloudAnimDispatcher(this);
        setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount >= 180) {
            remove(RemovalReason.DISCARDED);
        }
        if (!level().isClientSide() && tickCount== 2) {
            animDispatcher.explode();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}
}
