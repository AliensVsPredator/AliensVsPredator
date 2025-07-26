package com.predator.common.gameplay.entity.living.yautja;

import com.lib.common.network.DataUser;
import com.predator.common.gameplay.entity.living.yautja.manager.YautjaNavigationManager;
import com.predator.common.gameplay.entity.living.yautja.util.YautjaPredicates;
import com.predator.common.registry.init.item.PredatorArmorItems;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.config.AVPConfig;
import com.avp.common.gameplay.ai.goal.StrollAroundInWaterGoal;
import com.avp.common.gameplay.ai.goal.combat.DelayedAttackGoal;
import com.avp.common.gameplay.ai.goal.combat.UseItemGoal;
import com.avp.common.registry.init.item.AVPItems;

public class Yautja extends Monster implements DataUser {

    private final YautjaAnimationDispatcher animationDispatcher;

    private final YautjaNavigationManager navigationManager;

    public Yautja(EntityType<? extends Yautja> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new YautjaAnimationDispatcher(this);
        this.navigationManager = new YautjaNavigationManager(this, moveControl);
    }

    public static AttributeSupplier.Builder createYautjaAttributes() {
        return applyFrom(AVP.config.statsConfigs.YAUTJA_STATS, Monster.createMonsterAttributes());
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new DelayedAttackGoal(this, 1.0, true, 5, () -> {}));
        goalSelector.addGoal(1, new UseItemGoal(this, () -> {}));
        goalSelector.addGoal(7, new StrollAroundInWaterGoal(this, 1.0));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(Yautja.class));
        targetSelector.addGoal(
            2,
            new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                false,
                target -> YautjaPredicates.isThreateningTarget(this, target)
            )
        );
    }

    @Override
    public void tick() {
        super.tick();

        checkMask();

        if (!level().isClientSide && (getVehicle() instanceof Boat || getVehicle() instanceof Minecart)) {
            stopRiding();
        }
    }

    public void checkMask() {
        if (level().isClientSide || !hasMask()) {
            return;
        }

        var overHalfHealth = getHealth() > getMaxHealth() / 2;

        if (!overHalfHealth) {
            setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        }
    }

    public boolean hasMask() {
        return getItemBySlot(EquipmentSlot.HEAD).getItem() == PredatorArmorItems.JUNGLE_PREDATOR_HELMET.get();
    }

    @Override
    public boolean startRiding(@NotNull Entity entity, boolean force) {
        if (entity instanceof Boat || entity instanceof Minecart) {
            return false;
        }

        return super.startRiding(entity, force);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(
        @NotNull ServerLevelAccessor serverLevelAccessor,
        @NotNull DifficultyInstance difficultyInstance,
        @NotNull MobSpawnType mobSpawnType,
        @Nullable SpawnGroupData spawnGroupData
    ) {
        setItemSlot(EquipmentSlot.HEAD, new ItemStack(PredatorArmorItems.JUNGLE_PREDATOR_HELMET.get()));
        setItemSlot(EquipmentSlot.CHEST, new ItemStack(PredatorArmorItems.JUNGLE_PREDATOR_CHESTPLATE.get()));
        setItemSlot(EquipmentSlot.LEGS, new ItemStack(PredatorArmorItems.JUNGLE_PREDATOR_LEGGINGS.get()));
        setItemSlot(EquipmentSlot.FEET, new ItemStack(PredatorArmorItems.JUNGLE_PREDATOR_BOOTS.get()));

        if (random.nextDouble() <= 0.5) {
            if (random.nextDouble() <= 0.7) {
                setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(AVPItems.SHURIKEN.get()));
            } else {
                setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(AVPItems.SMART_DISC.get()));
            }
        }

        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
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

    public void setMoveControl(MoveControl moveControl) {
        this.moveControl = moveControl;
    }

    public void setNavigation(PathNavigation navigation) {
        this.navigation = navigation;
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
    public void travel(@NotNull Vec3 vec3) {
        if (isControlledByLocalInstance() && isUnderWater()) {
            moveRelative(0.01F, vec3);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.8));
        } else {
            super.travel(vec3);
        }
    }
}
