package org.avp.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.avp.api.entity.data.SyncedDataHandle;
import org.avp.api.entity.data.SyncedDataHandleUser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_SyncedDataHandleUser extends Entity implements SyncedDataHandleUser {

    protected List<SyncedDataHandle<?>> syncedDataHandleList = new ArrayList<>();

    protected MixinLivingEntity_SyncedDataHandleUser(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("RETURN"), method = "addAdditionalSaveData")
    void addAdditionalSaveData(CompoundTag nbt, CallbackInfo callbackInfo) {
        getSyncedDataHandleList().forEach(syncedDataHandle -> syncedDataHandle.write(nbt));
    }

    @Inject(at = @At("RETURN"), method = "readAdditionalSaveData")
    void readAdditionalSaveData(CompoundTag nbt, CallbackInfo callbackInfo) {
        getSyncedDataHandleList().forEach(syncedDataHandle -> syncedDataHandle.read(nbt));
    }

    @Override
    public <V> void addSyncedDataHandle(SyncedDataHandle<V> syncedDataHandle) {
        getSyncedDataHandleList().add(syncedDataHandle);
    }

    @Override
    public List<SyncedDataHandle<?>> getSyncedDataHandleList() {
        if (syncedDataHandleList == null) {
            syncedDataHandleList = new ArrayList<>();
        }
        return syncedDataHandleList;
    }
}
