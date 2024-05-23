package org.avp.common.entity.living;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.avp.api.entity.Boss;
import org.avp.api.entity.data.sync.SyncedDataHandle;
import org.avp.api.entity.data.sync.SyncedDataSerializer;
import org.avp.common.entity.ai.AIUtils;
import org.avp.common.sound.AVPSoundEvents;

public class Yautja extends Monster implements Boss, GeoEntity {

    private static final EntityDataAccessor<Boolean> HAS_HELMET = SynchedEntityData.defineId(Yautja.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> WRISTBLADES_VISIBLE = SynchedEntityData.defineId(
        Yautja.class,
        EntityDataSerializers.BOOLEAN
    );

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(
        this.getDisplayName(),
        BossEvent.BossBarColor.RED,
        BossEvent.BossBarOverlay.PROGRESS
    ).setDarkenScreen(false);

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public final SyncedDataHandle<Boolean> hasHelmet;

    public int lastSeenAttackTargetTimestamp;

    public final SyncedDataHandle<Boolean> wristbladesVisible;

    public Yautja(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setPersistenceRequired();
        this.hasHelmet = SyncedDataHandle.attach("HasHelmet", true, this, HAS_HELMET, SyncedDataSerializer.BOOLEAN);
        this.wristbladesVisible = SyncedDataHandle.attach(
            "WristbladesVisible",
            false,
            this,
            WRISTBLADES_VISIBLE,
            SyncedDataSerializer.BOOLEAN
        );
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
        @NotNull ServerLevelAccessor serverLevelAccessor,
        @NotNull DifficultyInstance difficultyInstance,
        @NotNull MobSpawnType mobSpawnType,
        @Nullable SpawnGroupData spawnGroupData,
        @Nullable CompoundTag compoundTag
    ) {
        hasHelmet.set(random.nextBoolean());
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    @Override
    protected void registerGoals() {
        AIUtils.addYautjaAI(this, goalSelector, targetSelector);
    }

    @Override
    public void tick() {
        super.tick();
        hideWristbladesIfOutOfCombat();
    }

    private void hideWristbladesIfOutOfCombat() {
        if (
            !level().isClientSide &&
                wristbladesVisible.get() &&
                this.getTarget() == null &&
                this.tickCount > lastSeenAttackTargetTimestamp + 3 * 20
        ) {
            playSound(AVPSoundEvents.INSTANCE.itemWeaponWristbladeClose.get());
            wristbladesVisible.set(false);
        }
    }

    @Override
    public void setTarget(@Nullable LivingEntity livingEntity) {
        if (livingEntity != null && !wristbladesVisible.get()) {
            playSound(AVPSoundEvents.INSTANCE.itemWeaponWristbladeOpen.get());
            wristbladesVisible.set(true);
        }

        if (livingEntity != null) {
            // If no target or changing target, growl.
            if (this.getTarget() == null || this.getTarget() != livingEntity) {
                playSound(AVPSoundEvents.INSTANCE.entityYautjaIntimidate.get());
            }
            this.lastSeenAttackTargetTimestamp = tickCount;
        }

        super.setTarget(livingEntity);
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
    public ServerBossEvent getBossEvent() {
        return bossEvent;
    }
}
