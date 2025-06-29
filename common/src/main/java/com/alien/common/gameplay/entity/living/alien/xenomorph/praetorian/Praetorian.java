package com.alien.common.gameplay.entity.living.alien.xenomorph.praetorian;

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
import com.avp.common.registry.init.AVPSoundEvents;

public class Praetorian extends Xenomorph {

    public static AttributeSupplier.Builder createPraetorianAttributes() {
        return applyFrom(AVP.config.statsConfigs.PRAETORIAN_STATS, Monster.createMonsterAttributes());
    }

    private final PraetorianAnimationDispatcher animationDispatcher;

    public Praetorian(EntityType<? extends Praetorian> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new PraetorianAnimationDispatcher(this);
        this.config = AVP.config.statsConfigs.PRAETORIAN_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getTypeForVariant(AlienVariant alienVariant) {
        return getType(alienVariant);
    }

    @Override
    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 64, 1, AVP.config.statsConfigs.PRAETORIAN_STATS.nestTickrate);
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.config.statsConfigs.PRAETORIAN_STATS.healthRegenPerSecond;
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

    // Praetorians are too large to be pushed by fluids.
    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    // Praetorians are too large to be pushed.
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public int getMaxJellyToGrowth() {
        return 9;
    }

    public PraetorianAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }

    public static EntityType<? extends Alien> getType(AlienVariant alienVariant) {
        return switch (alienVariant) {
            case NORMAL -> AlienEntityTypes.PRAETORIAN.get();
            case NETHER -> AlienEntityTypes.NETHER_PRAETORIAN.get();
            case ABERRANT -> AlienEntityTypes.ABERRANT_PRAETORIAN.get();
            case IRRADIATED -> AlienEntityTypes.IRRADIATED_PRAETORIAN.get();
        };
    }
}
