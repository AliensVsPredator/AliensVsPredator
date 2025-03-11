package com.avp.common.entity.living.human;

import com.avp.common.MoveAnalysis;
import com.avp.common.ai.goal.StrollAroundInWaterGoal;
import com.avp.common.entity.living.alien.Alien;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractHumanMob extends PathfinderMob {

    public static final EntityDataAccessor<Boolean> SET_GENDER = SynchedEntityData.defineId(
            AbstractHumanMob.class,
            EntityDataSerializers.BOOLEAN
    );

    private static final String GENDER_TAG_KEY = "gender";

    protected final MoveAnalysis moveAnalysis;

    private final HumanNavigationManager navigationManager;

    public AbstractHumanMob(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.moveAnalysis = new MoveAnalysis(this);
        this.navigationManager = new HumanNavigationManager(this, moveControl);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    protected abstract void runPassiveAnimations();

    public abstract void runAttackAnimations();

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(7, new StrollAroundInWaterGoal(this, 0.5));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5));
        targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(AbstractHumanMob.class));
        targetSelector.addGoal(
                2,
                new NearestAttackableTargetGoal<>(
                        this,
                        LivingEntity.class,
                        false,
                        target -> (this.getLastAttacker() != null && this.getLastAttacker().is(target)) || target instanceof Alien
                )
        );
    }

    @Override
    public void tick() {
        super.tick();
        moveAnalysis.tick();
    }

    @Override
    public void travel(Vec3 vec3) {
        if (isControlledByLocalInstance() && isUnderWater()) {
            moveRelative(0.01F, vec3);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.4));
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

    void setMoveControl(MoveControl moveControl) {
        this.moveControl = moveControl;
    }

    void setNavigation(PathNavigation navigation) {
        this.navigation = navigation;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        entityData.set(SET_GENDER, random.nextIntBetweenInclusive(0, 10) <= 6); // False = Female, True = Male
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SET_GENDER, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.getEntityData().set(SET_GENDER, compoundTag.getBoolean(GENDER_TAG_KEY));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean(GENDER_TAG_KEY, this.getEntityData().get(SET_GENDER));
    }
}
