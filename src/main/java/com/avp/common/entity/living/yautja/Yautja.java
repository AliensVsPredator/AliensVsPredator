package com.avp.common.entity.living.yautja;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
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
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.ai.goal.StrollAroundInWaterGoal;
import com.avp.common.ai.goal.combat.DelayedAttackGoal;
import com.avp.common.ai.goal.combat.UseItemGoal;
import com.avp.common.config.AVPConfig;
import com.avp.common.item.AVPItems;
import com.avp.common.util.YautjaPredicates;

public class Yautja extends Monster {

    private final YautjaAnimationDispatcher animationDispatcher;

    private final YautjaNavigationManager navigationManager;

    public static AttributeSupplier.Builder createYautjaAttributes() {
        return applyFrom(AVP.config.statsConfigs.YAUTJA_STATS, Monster.createMonsterAttributes());
    }

    public Yautja(EntityType<? extends Yautja> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new YautjaAnimationDispatcher(this);
        this.navigationManager = new YautjaNavigationManager(this, moveControl);
    }

    @Override
    protected void registerGoals() {
        // goalSelector.addGoal(1, new FleeFightGoal(this));
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new DelayedAttackGoal(this, 1.0, true, 5, this::runAttackAnimations));
        goalSelector.addGoal(1, new UseItemGoal(this, this::runAttackAnimations));
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

    public void runAttackAnimations() {
        animationDispatcher.rightShoot();
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && (getVehicle() instanceof Boat || getVehicle() instanceof Minecart)) {
            stopRiding();
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
    public @Nullable SpawnGroupData finalizeSpawn(
        ServerLevelAccessor serverLevelAccessor,
        DifficultyInstance difficultyInstance,
        MobSpawnType mobSpawnType,
        @Nullable SpawnGroupData spawnGroupData
    ) {
        if (random.nextInt(100) <= 10) {
            setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(AVPItems.SHURIKEN));
        } else if (random.nextInt(100) <= 90) {
            setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(AVPItems.SMART_DISC));
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

    void setMoveControl(MoveControl moveControl) {
        this.moveControl = moveControl;
    }

    void setNavigation(PathNavigation navigation) {
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
    public void travel(Vec3 vec3) {
        if (isControlledByLocalInstance() && isUnderWater()) {
            moveRelative(0.01F, vec3);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.8));
        } else {
            super.travel(vec3);
        }
    }
}
