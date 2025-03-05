package com.avp.common.entity.living.alien.xenomorph;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

import com.avp.common.MoveAnalysis;
import com.avp.common.ai.goal.DigToTargetGoal;
import com.avp.common.ai.goal.StrollAroundInWaterGoal;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.gene.GeneKeys;
import com.avp.common.gene.behavior.GeneDecoders;
import com.avp.common.manager.CrawlingManager;
import com.avp.common.manager.GrowthManager;
import com.avp.common.manager.VibrationSystemManager;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.common.util.AlienPredicates;
import com.avp.common.util.XenomorphGrowthUtil;
import com.avp.common.util.resin.ResinData;
import com.avp.common.util.resin.ResinManager;
import com.avp.common.util.resin.ResinProducer;

public abstract class Xenomorph extends Alien implements ResinProducer {

    protected static final EntityDataAccessor<Integer> CLIENT_ANGER_LEVEL = SynchedEntityData.defineId(
        Xenomorph.class,
        EntityDataSerializers.INT
    );

    private static final EntityDataAccessor<Boolean> IS_CRAWLING = SynchedEntityData.defineId(
        Xenomorph.class,
        EntityDataSerializers.BOOLEAN
    );

    protected final CrawlingManager crawlingManager;

    protected final MoveAnalysis moveAnalysis;

    private final XenomorphNavigationManager navigationManager;

    private final GrowthManager growthManager;

    private final ResinManager resinManager;

    private final VibrationSystemManager vibrationSystemManager;

    public Xenomorph(EntityType<? extends Xenomorph> entityType, Level level) {
        super(entityType, level);
        this.crawlingManager = new CrawlingManager(this, IS_CRAWLING);
        this.growthManager = new GrowthManager(this, XenomorphGrowthUtil.GROW_UP_CALLBACK)
            .setGrowOverTime(false)
            .setGrowthTimeReductionMultiplierProvider(
                () -> geneManager.get(GeneKeys.GROWTH_SPEED, GeneDecoders.GROWTH_SPEED)
            );
        this.moveAnalysis = new MoveAnalysis(this);
        this.navigationManager = new XenomorphNavigationManager(this, moveControl);
        this.resinManager = new ResinManager(this, createResinData())
            .setBonusResinProvider(
                () -> geneManager.get(GeneKeys.BONUS_RESIN_PRODUCTION, GeneDecoders.BONUS_RESIN_PRODUCTION).intValue()
            );
        this.vibrationSystemManager = new VibrationSystemManager(this)
            .setAngerManagementTickCallback(this::syncClientAngerLevel);
    }

    protected abstract @NotNull ResinData createResinData();

    protected abstract void runPassiveAnimations();

    public abstract void runAttackAnimations();

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CLIENT_ANGER_LEVEL, 0);
        builder.define(IS_CRAWLING, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(5, new DigToTargetGoal(this, 32));
        goalSelector.addGoal(7, new StrollAroundInWaterGoal(this, 0.5));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5));
        targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(Xenomorph.class));
        targetSelector.addGoal(
            2,
            new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                false,
                target -> AlienPredicates.isThreateningTarget(this, target)
            )
        );
    }

    @Override
    public void tick() {
        super.tick();
        moveAnalysis.tick();
        crawlingManager.tick();
        growthManager.tick();
        resinManager.tick();
        vibrationSystemManager.tick();

        if (!level().isClientSide) {
            if (getVehicle() instanceof Boat || getVehicle() instanceof Minecart) {
                stopRiding();
            }

            var target = getTarget();

            if (target != null && !AlienPredicates.isThreateningTarget(this, target)) {
                // If the target is no longer valid, stop targeting them.
                setTarget(null);
            }
        }
    }

    @Override
    public void travel(Vec3 vec3) {
        if (isControlledByLocalInstance() && isUnderWater()) {
            moveRelative(0.01F, vec3);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.9));
        } else {
            super.travel(vec3);
        }
    }

    @Override
    public void updateSwimming() {
        if (!level().isClientSide) {
            if (isEffectiveAi() && isUnderWater()) {
                navigationManager.switchToWater(this, 4, goalSelector);
                setSwimming(true);
            } else {
                navigationManager.switchToGround(this, 4, goalSelector);
                setSwimming(false);
            }
        }
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        if (entity instanceof Boat || entity instanceof Minecart) {
            return false;
        }

        return super.startRiding(entity, force);
    }

    @Override
    public @NotNull EntityDimensions getDefaultDimensions(@NotNull Pose pose) {
        var defaultDimensions = getType().getDimensions();
        return defaultDimensions.scale(1, crawlingManager.isCrawling() ? 0.4f : 1);
    }

    @Override
    public void updateDynamicGameEventListener(@NotNull BiConsumer<DynamicGameEventListener<?>, ServerLevel> biConsumer) {
        resinManager.updateDynamicGameEventListener(biConsumer);
        vibrationSystemManager.updateDynamicGameEventListener(biConsumer);
    }

    // Allows the xenomorph to disable shields on attack.
    @Override
    public boolean canDisableShield() {
        return true;
    }

    // Prevents the xenomorph from having a bias towards pathing in darker areas.
    @Override
    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        return 0.0F;
    }

    // Reduces how much FLOWING water slows down xenomorphs.
    @Override
    public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tagKey, double d) {
        var modifier = d;

        if (Objects.equals(tagKey, FluidTags.WATER)) {
            modifier = 0;
        }

        if (isNetherAfflicted() && Objects.equals(tagKey, FluidTags.LAVA)) {
            modifier = 0;
        }

        return super.updateFluidHeightAndDoFluidPushing(tagKey, modifier);
    }

    @Override
    public void setTarget(@Nullable LivingEntity livingEntity) {
        if (livingEntity != null && !livingEntity.equals(getTarget()) && ambientSoundTime > getAmbientSoundInterval()) {
            playSound(AVPSoundEvents.ENTITY_XENOMORPH_HISS, getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        }

        super.setTarget(livingEntity);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return getTarget() != null ? AVPSoundEvents.ENTITY_XENOMORPH_HISS : AVPSoundEvents.ENTITY_XENOMORPH_IDLE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return AVPSoundEvents.ENTITY_XENOMORPH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return AVPSoundEvents.ENTITY_XENOMORPH_HURT;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        crawlingManager.load(compoundTag);
        growthManager.load(compoundTag);
        resinManager.load(compoundTag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        crawlingManager.save(compoundTag);
        growthManager.save(compoundTag);
        resinManager.save(compoundTag);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);

        if (entityDataAccessor.equals(IS_CRAWLING)) {
            refreshDimensions();
        }
    }

    protected void syncClientAngerLevel() {
        this.entityData.set(CLIENT_ANGER_LEVEL, vibrationSystemManager.getActiveAnger());
    }

    public GrowthManager growthManager() {
        return growthManager;
    }

    @Override
    public ResinManager resinManager() {
        return resinManager;
    }

    void setMoveControl(MoveControl moveControl) {
        this.moveControl = moveControl;
    }

    void setNavigation(PathNavigation navigation) {
        this.navigation = navigation;
    }
}
