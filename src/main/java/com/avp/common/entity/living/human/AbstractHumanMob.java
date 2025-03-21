package com.avp.common.entity.living.human;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.common.MoveAnalysis;
import com.avp.common.ai.goal.StrollAroundInWaterGoal;
import com.avp.common.config.AVPConfig;
import com.avp.common.manager.*;

public abstract class AbstractHumanMob extends PathfinderMob {

    public static final EntityDataAccessor<Boolean> SET_GENDER = SynchedEntityData.defineId(
        AbstractHumanMob.class,
        EntityDataSerializers.BOOLEAN
    );

    protected Integer cachedSecondRandomValue;

    protected final MoveAnalysis moveAnalysis;

    private final HumanNavigationManager navigationManager;

    protected final GenderManager genderManager;

    public OutfitManager outfitManager;

    public HairManager hairManager;

    public BeardManager beardManager;

    public EyeManager eyeManager;

    public SkinManager skinManager;

    public AbstractHumanMob(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.moveAnalysis = new MoveAnalysis(this);
        this.navigationManager = new HumanNavigationManager(this, moveControl);
        this.genderManager = new GenderManager(this, SET_GENDER);
    }

    public OutfitManager getOutfitManager() {
        return outfitManager;
    }

    public HairManager getHairManager() {
        return hairManager;
    }

    public BeardManager getBeardManager() {
        return beardManager;
    }

    public EyeManager getEyeManager() {
        return eyeManager;
    }

    public SkinManager getSkinManager() {
        return skinManager;
    }

    public GenderManager getGenderManager() {
        return genderManager;
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
//        goalSelector.addGoal(1, new FleeFightGoal(this));
        goalSelector.addGoal(7, new StrollAroundInWaterGoal(this, 0.5));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        goalSelector.addGoal(10, new LookAtPlayerGoal(this, LivingEntity.class, 15.0F));
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
    public SpawnGroupData finalizeSpawn(
        @NotNull ServerLevelAccessor level,
        @NotNull DifficultyInstance difficulty,
        @NotNull MobSpawnType spawnType,
        @Nullable SpawnGroupData spawnGroupData
    ) {
        this.getGenderManager().tick();
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
        this.getGenderManager().load(compoundTag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.getGenderManager().save(compoundTag);
    }

    public int getSharedSecondRandomValue(int maxValue) {
        if (cachedSecondRandomValue == null) {
            cachedSecondRandomValue = getRandom().nextIntBetweenInclusive(1, maxValue);
        }
        return cachedSecondRandomValue;
    }

    public static AttributeSupplier.Builder applyFrom(AVPConfig.StatsConfigs.AdvancedStats config, AttributeSupplier.Builder builder) {
        builder.add(Attributes.ARMOR, config.armor);
        builder.add(Attributes.ARMOR_TOUGHNESS, config.armorToughness);
        builder.add(Attributes.ATTACK_DAMAGE, config.attackDamage);
        builder.add(Attributes.FOLLOW_RANGE, config.followRange);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, config.knockbackResistance);
        builder.add(Attributes.MAX_HEALTH, config.health);
        builder.add(Attributes.MOVEMENT_SPEED, config.moveSpeed);

        return builder;
    }

}
