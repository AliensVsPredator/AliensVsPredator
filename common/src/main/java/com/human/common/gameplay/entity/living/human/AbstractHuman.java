package com.human.common.gameplay.entity.living.human;

import com.bvanseg.just.functional.option.Option;
import com.lib.common.network.DataAccessor;
import com.lib.common.network.DataUser;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
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

    public final DataAccessor<Integer> beardVariant = getDataContainer().<Integer>builder("beardVariant")
        .networkSynchronized(ByteBufCodecs.INT)
        .persistent(Codec.INT)
        .build(0);

    public final DataAccessor<Integer> eyeColor = getDataContainer().<Integer>builder("eyeColor")
        .networkSynchronized(ByteBufCodecs.INT)
        .persistent(Codec.INT)
        .build(0xA1CAF1);

    public final DataAccessor<Integer> hairColor = getDataContainer().<Integer>builder("hairColor")
        .networkSynchronized(ByteBufCodecs.INT)
        .persistent(Codec.INT)
        .build(0x86462C);

    public final DataAccessor<Integer> hairVariant = getDataContainer().<Integer>builder("hairVariant")
        .networkSynchronized(ByteBufCodecs.INT)
        .persistent(Codec.INT)
        .build(0);

    public final DataAccessor<Boolean> isMale = getDataContainer().<Boolean>builder("isMale")
        .networkSynchronized(ByteBufCodecs.BOOL)
        .persistent(Codec.BOOL)
        .build(true);

    public final DataAccessor<Integer> skinColor = getDataContainer().<Integer>builder("skinColor")
        .networkSynchronized(ByteBufCodecs.INT)
        .persistent(Codec.INT)
        .build(0xEED0B6);

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

    public HumanFeatureManager getHumanFeatureManager() {
        return humanFeatureManager;
    }

    public Option<Integer> getBeardVariant() {
        return isMale.get()
            ? Option.some(beardVariant.get())
            : Option.none();
    }

    public void setBeardVariant(int variantIndex) {
        if (!isMale.get()) {
            return;
        }

        beardVariant.set(variantIndex);
    }
}
