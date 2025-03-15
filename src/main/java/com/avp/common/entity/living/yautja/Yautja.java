package com.avp.common.entity.living.yautja;

import com.avp.common.ai.goal.combat.DelayedAttackGoal;
import com.avp.common.ai.goal.combat.FleeFightGoal;
import com.avp.common.ai.goal.combat.UseItemGoal;
import com.avp.common.entity.living.human.marine.MarineAnimationDispatcher;
import com.avp.common.item.AVPItems;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.avp.AVP;
import com.avp.common.config.ConfigProperties;
import com.avp.common.entity.living.alien.Alien;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class Yautja extends Monster {

    private final YautjaAnimationDispatcher animationDispatcher;

    public static AttributeSupplier.Builder createYautjaAttributes() {
        var container = ConfigProperties.YAUTJA_ATTRIBUTES;
        return container.applyFrom(AVP.STATS_CONFIG, Monster.createMonsterAttributes());
    }

    public Yautja(EntityType<? extends Yautja> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new YautjaAnimationDispatcher(this);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FleeFightGoal(this));
        goalSelector.addGoal(1, new DelayedAttackGoal(this, 1.0, true, 5, this::runAttackAnimations));
        goalSelector.addGoal(1, new UseItemGoal(this,  this::runAttackAnimations));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Alien.class, true));
    }

    public void runAttackAnimations() {
        animationDispatcher.rightShoot();
    };

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (getVehicle() instanceof Boat || getVehicle() instanceof Minecart) {
                stopRiding();
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
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData) {
//        if (random.nextInt( 100 ) <= 10) {
//            setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(AVPItems.SHURIKEN));
//        } else if (random.nextInt( 100 ) <= 90) {
//            setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(AVPItems.SMART_DISC));
//        }
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(AVPItems.SHURIKEN));

        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
    }
}
