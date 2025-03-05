package com.avp.core.common.entity.living.yautja;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;

import com.avp.core.AVP;
import com.avp.core.common.config.ConfigProperties;
import com.avp.core.common.entity.living.alien.Alien;

public class Yautja extends Monster {

    public static AttributeSupplier.Builder createYautjaAttributes() {
        var container = ConfigProperties.YAUTJA_ATTRIBUTES;
        return container.applyFrom(AVP.STATS_CONFIG, Monster.createMonsterAttributes());
    }

    public Yautja(EntityType<? extends Yautja> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Alien.class, true));
    }

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
}
