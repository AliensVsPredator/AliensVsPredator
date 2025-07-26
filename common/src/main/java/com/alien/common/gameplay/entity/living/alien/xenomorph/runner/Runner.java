package com.alien.common.gameplay.entity.living.alien.xenomorph.runner;

import com.alien.common.gameplay.ai.CreateVentGoal;
import com.alien.common.gameplay.ai.DropOffEggGoal;
import com.alien.common.gameplay.ai.PickUpEggGoal;
import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.EggCarrier;
import com.alien.common.gameplay.entity.living.alien.xenomorph.EggPickupManager;
import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.model.alien.variant.AlienVariant;
import com.alien.common.model.resin.ResinData;
import com.alien.common.registry.init.AlienEntityTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import com.avp.AVP;
import com.avp.common.gameplay.ai.goal.combat.LungeAtTargetGoal;
import com.avp.common.registry.init.AVPSoundEvents;
import com.avp.common.registry.tag.AVPEntityTypeTags;
import com.avp.common.util.EntityUtil;

public class Runner extends Xenomorph implements EggCarrier {

    public static AttributeSupplier.Builder createRunnerAttributes() {
        return applyFrom(AVP.config.statsConfigs.RUNNER_STATS, Monster.createMonsterAttributes());
    }

    private final RunnerAnimationDispatcher animationDispatcher;

    private final EggPickupManager eggPickupManager;

    public Runner(EntityType<? extends Runner> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new RunnerAnimationDispatcher(this);
        this.config = AVP.config.statsConfigs.RUNNER_STATS;
        this.eggPickupManager = new EggPickupManager(this);
    }

    @Override
    public @Nullable EntityType<? extends Alien> getTypeForVariant(AlienVariant alienVariant) {
        return getType(alienVariant);
    }

    @Override
    protected @Nullable ResinData createResinData() {
        return new ResinData(0, 16, 1, AVP.config.statsConfigs.RUNNER_STATS.nestTickrate);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(3, new LungeAtTargetGoal(this, 0.05F, 20 * 7, 6, 12).setOnLungeCallback(this::runLungeAnimation));
        goalSelector.addGoal(4, new PickUpEggGoal<>(this));
        goalSelector.addGoal(5, new DropOffEggGoal<>(this));
        goalSelector.addGoal(6, new CreateVentGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        eggPickupManager.tick();
    }

    @Override
    protected boolean canEntityRideAlien(@NotNull Entity passenger) {
        return super.canEntityRideAlien(passenger)
            || passenger.getType().is(AVPEntityTypeTags.OVOMORPHS);
    }

    @Override
    protected void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        if (passenger.getType().is(AVPEntityTypeTags.OVOMORPHS)) {
            var relativePos = EntityUtil.getRelativePosition(this, 0, 0.8, -1);
            callback.accept(passenger, relativePos.x, relativePos.y, relativePos.z);
            return;
        }

        super.positionRider(passenger, callback);
    }

    @Override
    public void runAttackAnimations() {
        var attackType = random.nextInt(0, 3);

        playSound(AVPSoundEvents.ENTITY_XENOMORPH_ATTACK.get(), getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);

        switch (attackType) {
            case 0 -> animationDispatcher.rightClawAttack();
            case 1 -> animationDispatcher.biteAttack();
            default -> animationDispatcher.tailAttackQuad();
        }
    }

    private void runLungeAnimation() {
        playSound(AVPSoundEvents.ENTITY_XENOMORPH_LUNGE.get(), getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        animationDispatcher.lunge();
    }

    @Override
    public void updateDynamicGameEventListener(@NotNull BiConsumer<DynamicGameEventListener<?>, ServerLevel> biConsumer) {
        super.updateDynamicGameEventListener(biConsumer);
        eggPickupManager.updateDynamicGameEventListener(biConsumer);
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.config.statsConfigs.RUNNER_STATS.healthRegenPerSecond;
    }

    @Override
    public Integer getMaxJellyToGrowth() {
        return 2;
    }

    @Override
    protected double getPursuitSpeedModifier() {
        return 1.2;
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

    @Override
    public EggPickupManager getEggPickupManager() {
        return eggPickupManager;
    }
}
