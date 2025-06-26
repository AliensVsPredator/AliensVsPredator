package com.alien.common.gameplay.entity.living.alien.xenomorph.runner;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.model.alien.variant.AlienVariant;
import com.alien.common.model.resin.ResinData;
import com.alien.common.registry.init.AlienEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.gameplay.ai.goal.combat.LungeAtTargetGoal;
import com.avp.common.registry.init.AVPSoundEvents;

public class Runner extends Xenomorph {

    public static AttributeSupplier.Builder createRunnerAttributes() {
        return applyFrom(AVP.config.statsConfigs.RUNNER_STATS, Monster.createMonsterAttributes());
    }

    private final RunnerAnimationDispatcher animationDispatcher;

    public Runner(EntityType<? extends Runner> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new RunnerAnimationDispatcher(this);
        this.config = AVP.config.statsConfigs.RUNNER_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getTypeForVariant(AlienVariant alienVariant) {
        return getType(alienVariant);
    }

    @Override
    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 16, 1, AVP.config.statsConfigs.RUNNER_STATS.nestTickrate);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(3, new LungeAtTargetGoal(this, 0.05F, 20 * 7, 6, 12).setOnLungeCallback(this::runLungeAnimation));
    }

    @Override
    public void runAttackAnimations() {
        var attackType = random.nextInt(0, 3);

        playSound(AVPSoundEvents.ENTITY_XENOMORPH_ATTACK.get(), getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);

        switch (attackType) {
            case 0 -> animationDispatcher.rightClawAttack();
            case 1 -> animationDispatcher.biteAttack();
            default -> animationDispatcher.tailAttack();
        }
    }

    private void runLungeAnimation() {
        playSound(AVPSoundEvents.ENTITY_XENOMORPH_LUNGE.get(), getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        animationDispatcher.lunge();
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.config.statsConfigs.RUNNER_STATS.healthRegenPerSecond;
    }

    @Override
    public int getMaxJellyToGrowth() {
        return 2;
    }

    public RunnerAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }

    public static EntityType<? extends Alien> getType(AlienVariant alienVariant) {
        return switch (alienVariant) {
            case NORMAL -> AlienEntityTypes.RUNNER.get();
            case NETHER -> AlienEntityTypes.NETHER_RUNNER.get();
            case ABERRANT -> AlienEntityTypes.ABERRANT_RUNNER.get();
            case IRRADIATED -> AlienEntityTypes.IRRADIATED_RUNNER.get();
        };
    }
}
