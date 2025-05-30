package com.human.common.gameplay.entity.living.human;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.common.config.AVPConfig;
import com.avp.common.util.MovementAnalyzer;

public abstract class AbstractHuman extends PathfinderMob {

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

    public static final EntityDataAccessor<Integer> BEARD_VARIANT = SynchedEntityData.defineId(
        AbstractHuman.class,
        EntityDataSerializers.INT
    );

    public static final EntityDataAccessor<Integer> EYE_COLOR = SynchedEntityData.defineId(
        AbstractHuman.class,
        EntityDataSerializers.INT
    );

    public static final EntityDataAccessor<Integer> HAIR_COLOR = SynchedEntityData.defineId(
        AbstractHuman.class,
        EntityDataSerializers.INT
    );

    public static final EntityDataAccessor<Integer> HAIR_VARIANT = SynchedEntityData.defineId(
        AbstractHuman.class,
        EntityDataSerializers.INT
    );

    public static final EntityDataAccessor<Boolean> IS_MALE = SynchedEntityData.defineId(
        AbstractHuman.class,
        EntityDataSerializers.BOOLEAN
    );

    public static final EntityDataAccessor<Integer> SKIN_COLOR = SynchedEntityData.defineId(
        AbstractHuman.class,
        EntityDataSerializers.INT
    );

    protected final MovementAnalyzer movementAnalyzer;

    private final HumanNavigationManager navigationManager;

    private final HumanFeatureManager humanFeatureManager;

    public AbstractHuman(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.movementAnalyzer = new MovementAnalyzer(this);
        this.navigationManager = new HumanNavigationManager(this, moveControl);
        this.humanFeatureManager = new HumanFeatureManager(this);
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
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    public abstract void runAttackAnimations();

    @Override
    public void tick() {
        super.tick();
        movementAnalyzer.tick();
    }

    @Override
    public void travel(@NotNull Vec3 vec3) {
        if (isControlledByLocalInstance() && isUnderWater()) {
            moveRelative(0.01F, vec3);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.8));
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

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BEARD_VARIANT, 0);
        builder.define(EYE_COLOR, 0xA1CAF1);
        builder.define(HAIR_COLOR, 0x86462C);
        builder.define(HAIR_VARIANT, 0);
        builder.define(IS_MALE, true);
        builder.define(SKIN_COLOR, 0xEED0B6);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        humanFeatureManager.load(compoundTag);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        humanFeatureManager.save(compoundTag);
    }

    public HumanFeatureManager getHumanFeatureManager() {
        return humanFeatureManager;
    }

    public Option<Integer> getBeardVariant() {
        return isMale()
            ? Option.some(entityData.get(BEARD_VARIANT))
            : Option.none();
    }

    public void setBeardVariant(int variantIndex) {
        if (!isMale()) {
            return;
        }

        entityData.set(BEARD_VARIANT, variantIndex);
    }

    public int getEyeColor() {
        return entityData.get(EYE_COLOR);
    }

    public void setEyeColor(int eyeColor) {
        entityData.set(EYE_COLOR, eyeColor);
    }

    public int getHairColor() {
        return entityData.get(HAIR_COLOR);
    }

    public void setHairColor(int hairColor) {
        entityData.set(HAIR_COLOR, hairColor);
    }

    public int getHairVariant() {
        return entityData.get(HAIR_VARIANT);
    }

    public void setHairVariant(int variantIndex) {
        entityData.set(HAIR_VARIANT, variantIndex);
    }

    public boolean isMale() {
        return entityData.get(IS_MALE);
    }

    public void setMale(boolean isMale) {
        entityData.set(IS_MALE, isMale);
    }

    public int getSkinColor() {
        return entityData.get(SKIN_COLOR);
    }

    public void setSkinColor(int skinColor) {
        entityData.set(SKIN_COLOR, skinColor);
    }
}
