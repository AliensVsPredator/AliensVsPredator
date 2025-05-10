package com.avp.common.entity.living.alien.xenomorph.drone;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.ai.goal.combat.LungeAtTargetGoal;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.alien.manager.resin.ResinData;
import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.sound.AVPSoundEvents;

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
    public @Nullable EntityType<? extends Alien> getAberrantType() {
        return AVPEntityTypes.ABERRANT_DRONE.get();
    }

    @Override
    public @Nullable EntityType<? extends Alien> getIrradiatedType() {
        return AVPEntityTypes.IRRADIATED_DRONE.get();
    }

    @Override
    public @Nullable EntityType<? extends Alien> getNetherType() {
        return AVPEntityTypes.NETHER_DRONE.get();
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

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()) {
            becomeIrradiated();
        }
    }

    public DroneAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }
}
