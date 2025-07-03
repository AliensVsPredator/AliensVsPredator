package com.alien.common.gameplay.entity.living.alien.xenomorph.crusher;

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

public class Crusher extends Xenomorph {

    public static AttributeSupplier.Builder createCrusherAttributes() {
        return applyFrom(AVP.config.statsConfigs.CRUSHER_STATS, Monster.createMonsterAttributes());
    }

    private final CrusherAnimationDispatcher animationDispatcher;

    public Crusher(EntityType<? extends Crusher> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new CrusherAnimationDispatcher(this);
        this.config = AVP.config.statsConfigs.CRUSHER_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getTypeForVariant(AlienVariant alienVariant) {
        return getType(alienVariant);
    }

    @Override
    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 16, 1, AVP.config.statsConfigs.CRUSHER_STATS.nestTickrate);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(3, new LungeAtTargetGoal(this, 0.05F, 20 * 7, 6, 12).setOnLungeCallback(this::runLungeAnimation));
    }

    @Override
    protected double getPursuitSpeedModifier() {
        return 1.2;
    }

    @Override
    public void runAttackAnimations() {
        var isClawAttack = random.nextBoolean();

        playSound(AVPSoundEvents.ENTITY_XENOMORPH_ATTACK.get(), getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);

        if (isClawAttack) {
            animationDispatcher.biteAttack();
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
        return AVP.config.statsConfigs.CRUSHER_STATS.healthRegenPerSecond;
    }

    @Override
    public Integer getMaxJellyToGrowth() {
        return 2;
    }

    public CrusherAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }

    public static EntityType<? extends Alien> getType(AlienVariant alienVariant) {
        return switch (alienVariant) {
            case NORMAL -> AlienEntityTypes.CRUSHER.get();
            case NETHER -> AlienEntityTypes.NETHER_CRUSHER.get();
            case ABERRANT -> AlienEntityTypes.ABERRANT_CRUSHER.get();
            case IRRADIATED -> AlienEntityTypes.IRRADIATED_CRUSHER.get();
        };
    }
}
