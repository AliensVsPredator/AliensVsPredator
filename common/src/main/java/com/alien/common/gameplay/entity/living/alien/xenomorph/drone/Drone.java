package com.alien.common.gameplay.entity.living.alien.xenomorph.drone;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.model.alien.variant.AlienVariant;
import com.alien.common.model.resin.ResinData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.gameplay.ai.goal.combat.LungeAtTargetGoal;
import com.avp.common.registry.init.AVPSoundEvents;
import com.avp.common.registry.init.entity_type.AVPEntityTypes;

public class Drone extends Xenomorph {

    public static AttributeSupplier.Builder createDroneAttributes() {
        return applyFrom(AVP.config.statsConfigs.DRONE_STATS, Monster.createMonsterAttributes());
    }

    private final DroneAnimationDispatcher animationDispatcher;

    public Drone(EntityType<? extends Drone> entityType, Level level) {
        super(entityType, level);
        this.attackDelayTicks = 7;
        this.animationDispatcher = new DroneAnimationDispatcher(this);
        this.config = AVP.config.statsConfigs.DRONE_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getTypeForVariant(AlienVariant alienVariant) {
        return getType(alienVariant);
    }

    @Override
    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 16, 1, AVP.config.statsConfigs.DRONE_STATS.nestTickrate);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(3, new LungeAtTargetGoal(this, 0.05F, 20 * 7, 6, 12).setOnLungeCallback(this::runLungeAnimation));
    }

    @Override
    public void runAttackAnimations() {
        var isClawAttack = random.nextBoolean();

        playSound(AVPSoundEvents.ENTITY_XENOMORPH_ATTACK.get(), getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);

        if (isClawAttack) {
            animationDispatcher.clawAttack();
        } else {
            animationDispatcher.tailAttack();
        }
    }

    private void runLungeAnimation() {
        playSound(AVPSoundEvents.ENTITY_XENOMORPH_LUNGE.get(), getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        animationDispatcher.lunge();
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.config.statsConfigs.DRONE_STATS.healthRegenPerSecond;
    }

    @Override
    public int maxJellyToGrowth() {
        return 2;
    }

    public DroneAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }

    public static EntityType<? extends Alien> getType(AlienVariant alienVariant) {
        return switch (alienVariant) {
            case NORMAL -> AVPEntityTypes.DRONE.get();
            case NETHER -> AVPEntityTypes.NETHER_DRONE.get();
            case ABERRANT -> AVPEntityTypes.ABERRANT_DRONE.get();
            case IRRADIATED -> AVPEntityTypes.IRRADIATED_DRONE.get();
        };
    }
}
