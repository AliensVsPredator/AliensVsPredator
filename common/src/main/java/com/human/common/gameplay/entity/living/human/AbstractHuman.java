package com.human.common.gameplay.entity.living.human;

import com.lib.common.network.DataAccessor;
import com.lib.common.network.DataUser;
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
import com.avp.common.registry.init.AVPDataKeys;
import com.avp.common.util.MovementAnalyzer;

public abstract class AbstractHuman extends PathfinderMob implements DataUser {

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

    public final DataAccessor<Integer> beardVariant;

    public final DataAccessor<Integer> eyeColor;

    public final DataAccessor<Integer> hairColor;

    public final DataAccessor<Integer> hairVariant;

    public final DataAccessor<Boolean> isMale;

    public final DataAccessor<Integer> skinColor;

    protected final MovementAnalyzer movementAnalyzer;

    private final HumanNavigationManager navigationManager;

    private final HumanFeatureManager humanFeatureManager;

    public AbstractHuman(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);

        this.beardVariant = new DataAccessor<>(this, AVPDataKeys.MARINE_BEARD_VARIANT);
        this.eyeColor = new DataAccessor<>(this, AVPDataKeys.MARINE_EYE_COLOR);
        this.hairColor = new DataAccessor<>(this, AVPDataKeys.MARINE_HAIR_COLOR);
        this.hairVariant = new DataAccessor<>(this, AVPDataKeys.MARINE_HAIR_VARIANT);
        this.isMale = new DataAccessor<>(this, AVPDataKeys.MARINE_IS_MALE);
        this.skinColor = new DataAccessor<>(this, AVPDataKeys.MARINE_SKIN_COLOR);

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

    public HumanFeatureManager getHumanFeatureManager() {
        return humanFeatureManager;
    }

    public Integer getBeardVariantOrNull() {
        return isMale.get()
            ? beardVariant.get()
            : null;
    }

    public void setBeardVariant(int variantIndex) {
        if (!isMale.get()) {
            return;
        }

        beardVariant.set(variantIndex);
    }
}
