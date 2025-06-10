package com.human.common.gameplay.entity.nuke;

import com.human.common.registry.init.entity_type.HumanEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MushroomCloudEntity extends Entity {

    protected MushroomCloudAnimDispatcher animDispatcher;

    public MushroomCloudEntity(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
        noCulling = true;
        animDispatcher = new MushroomCloudAnimDispatcher(this);
    }

    public MushroomCloudEntity(Level level, double x, double y, double z) {
        super(HumanEntityTypes.MUSHROOM_CLOUD.get(), level);
        animDispatcher = new MushroomCloudAnimDispatcher(this);
        setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount >= 180) {
            remove(RemovalReason.DISCARDED);
        }
        if (!level().isClientSide() && tickCount == 2) {
            animDispatcher.explode();
        }
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {}
}
