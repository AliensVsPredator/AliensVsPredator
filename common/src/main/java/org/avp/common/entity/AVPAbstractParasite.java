package org.avp.common.entity;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.avp.api.entity.GOAPBrainUser;
import org.avp.api.entity.Parasite;
import org.avp.api.entity.ai.GOAPBrain;
import org.avp.api.entity.data.sync.SyncedDataHandle;
import org.avp.api.entity.data.sync.SyncedDataSerializer;
import org.avp.common.entity.ai.parasite.ParasiteBrain;
import org.avp.common.util.GravityUtils;
import org.jetbrains.annotations.NotNull;

public abstract class AVPAbstractParasite extends Monster implements GeoEntity, GOAPBrainUser, Parasite {

    private static final String TICKS_ATTACHED_TO_HOST_KEY = "TicksAttachedToHost";

    private static final EntityDataAccessor<Boolean> IS_FERTILE = SynchedEntityData.defineId(AVPAbstractParasite.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    private int ticksAttachedToHost;

    private final SyncedDataHandle<Boolean> isFertile;

    protected AVPAbstractParasite(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.isFertile = SyncedDataHandle.attach("IsFertile", true, this, IS_FERTILE, SyncedDataSerializer.BOOLEAN);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt(TICKS_ATTACHED_TO_HOST_KEY, ticksAttachedToHost);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        ticksAttachedToHost = compoundTag.getInt(TICKS_ATTACHED_TO_HOST_KEY);
    }

    @Override
    public void remove(@NotNull RemovalReason removalReason) {
        // We don't really care why the parasite is removed, we just need to restore the host's state.
        restoreHost();
        super.remove(removalReason);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        return true;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void incrementTicksAttachedToHost() {
        ticksAttachedToHost++;
    }

    @Override
    public int getTicksAttachedToHost() {
        return ticksAttachedToHost;
    }

    @Override
    public boolean isFertile() {
        return isFertile.get();
    }

    @Override
    public void setFertile(boolean isFertile) {
        this.isFertile.set(isFertile);
    }

    @Override
    public GOAPBrain createGOAPBrain() {
        return new ParasiteBrain(this);
    }

    public void smotherHost() {
        var vehicle = getVehicle();

        if (vehicle == null) return;

        if (vehicle instanceof Mob mob) {
            mob.setNoAi(true);
        }

        // Apply gravity while mob's "AI" is off.
        GravityUtils.apply(vehicle);
        // Facehuggers provide oxygen to their host.
        vehicle.setAirSupply(vehicle.getMaxAirSupply());
        // Parasites silence host.
        vehicle.setSilent(true);
    }

    public void restoreHost() {
        var vehicle = getVehicle();

        if (vehicle == null) return;

        // Reset state.
        if (vehicle instanceof Mob mob) {
            mob.setNoAi(false);
        }
        vehicle.setSilent(false);
    }
}
