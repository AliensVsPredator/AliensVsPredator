package org.avp.common.entity;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.avp.api.entity.GOAPBrainUser;
import org.avp.api.entity.ai.GOAPBrain;
import org.avp.api.entity.data.sync.SyncedDataHandle;
import org.avp.api.entity.data.sync.SyncedDataSerializer;
import org.avp.common.entity.ai.ovamorph.OvamorphBrain;
import org.jetbrains.annotations.NotNull;

public abstract class AVPAbstractOvamorph extends Monster implements GeoEntity, GOAPBrainUser {

    private static final int MAX_OPEN_PROGRESS_IN_TICKS = 40;

    private static final int MIN_DISTURB_COOLDOWN_IN_TICKS = 20 * 10;

    private static final String DISTURB_COOLDOWN_IN_TICKS_KEY = "DisturbCooldownInTicks";

    private static final String HAS_FACEHUGGER_KEY = "HasFacehugger";

    private static final String OPEN_PROGRESS_IN_TICKS_KEY = "OpenProgressInTicks";

    private static final EntityDataAccessor<Boolean> IS_OPEN = SynchedEntityData.defineId(AVPAbstractOvamorph.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> IS_OPENING = SynchedEntityData.defineId(AVPAbstractOvamorph.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    private int disturbCooldown = MIN_DISTURB_COOLDOWN_IN_TICKS + random.nextInt(MIN_DISTURB_COOLDOWN_IN_TICKS);

    private boolean hasFacehugger = true;

    private int openProgress = 0;

    public final SyncedDataHandle<Boolean> isOpen;

    public final SyncedDataHandle<Boolean> isOpening;

    protected AVPAbstractOvamorph(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.isOpen = SyncedDataHandle.attach("IsOpen", false, this, IS_OPEN, SyncedDataSerializer.BOOLEAN);
        this.isOpening = SyncedDataHandle.attach("IsOpening", false, this, IS_OPENING, SyncedDataSerializer.BOOLEAN);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        disturbCooldown = compoundTag.getInt(DISTURB_COOLDOWN_IN_TICKS_KEY);
        openProgress = compoundTag.getInt(OPEN_PROGRESS_IN_TICKS_KEY);
        hasFacehugger = compoundTag.getBoolean(HAS_FACEHUGGER_KEY);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt(DISTURB_COOLDOWN_IN_TICKS_KEY, disturbCooldown);
        compoundTag.putInt(OPEN_PROGRESS_IN_TICKS_KEY, openProgress);
        compoundTag.putBoolean(HAS_FACEHUGGER_KEY, hasFacehugger);
    }

    @Override
    public void tick() {
        super.tick();

        if (isOpening.get() && openProgress < MAX_OPEN_PROGRESS_IN_TICKS) {
            openProgress++;
        }

        if (!level().isClientSide && openProgress >= MAX_OPEN_PROGRESS_IN_TICKS) {
            isOpening.set(false);
            isOpen.set(true);
        }
    }

    public void disturb() {
        disturbCooldown--;
    }

    public boolean isFullyDisturbed() {
        return disturbCooldown <= 0;
    }

    public int getOpenProgress() {
        return openProgress;
    }

    public boolean hasFacehugger() {
        return hasFacehugger;
    }

    public void setHasFacehugger(boolean hasFacehugger) {
        this.hasFacehugger = hasFacehugger;
    }

    @Override
    public GOAPBrain createGOAPBrain() {
        return new OvamorphBrain(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
