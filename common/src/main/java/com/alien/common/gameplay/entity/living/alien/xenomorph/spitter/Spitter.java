package com.alien.common.gameplay.entity.living.alien.xenomorph.spitter;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.model.alien.variant.AlienVariant;
import com.alien.common.model.resin.ResinData;
import com.alien.common.registry.init.AlienEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.gameplay.ai.goal.combat.LungeAtTargetGoal;
import com.avp.common.registry.init.AVPSoundEvents;

public class Spitter extends Xenomorph {

    public static AttributeSupplier.Builder createSpitterAttributes() {
        return applyFrom(AVP.config.statsConfigs.SPITTER_STATS, Monster.createMonsterAttributes());
    }

    private final SpitterAnimationDispatcher animationDispatcher;

    public Spitter(EntityType<? extends Spitter> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new SpitterAnimationDispatcher(this);
        this.config = AVP.config.statsConfigs.SPITTER_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getTypeForVariant(AlienVariant alienVariant) {
        return getType(alienVariant);
    }

    @Override
    protected @Nullable ResinData createResinData() {
        return new ResinData(0, 16, 1, AVP.config.statsConfigs.SPITTER_STATS.nestTickrate);
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
        return AVP.config.statsConfigs.SPITTER_STATS.healthRegenPerSecond;
    }

    @Override
    public Integer getMaxJellyToGrowth() {
        return null;
    }

    public SpitterAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }

    public static EntityType<? extends Alien> getType(AlienVariant alienVariant) {
        return switch (alienVariant) {
            case NORMAL -> AlienEntityTypes.SPITTER.get();
            case NETHER -> AlienEntityTypes.NETHER_SPITTER.get();
            case ABERRANT -> AlienEntityTypes.ABERRANT_SPITTER.get();
            case IRRADIATED -> null;
        };
    }
}
