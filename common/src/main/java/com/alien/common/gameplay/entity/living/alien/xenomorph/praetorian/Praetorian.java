package com.alien.common.gameplay.entity.living.alien.xenomorph.praetorian;

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
import com.avp.common.registry.init.entity_type.AVPEntityTypes;

public class Praetorian extends Xenomorph {

    public static AttributeSupplier.Builder createPraetorianAttributes() {
        return applyFrom(AVP.config.statsConfigs.PRAETORIAN_STATS, Monster.createMonsterAttributes());
    }

    private final PraetorianAnimationDispatcher animationDispatcher;

    public Praetorian(EntityType<? extends Praetorian> entityType, Level level) {
        super(entityType, level);
        this.attackDelayTicks = 10;
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
        var isClawAttack = random.nextBoolean();

        if (isClawAttack) {
            animationDispatcher.clawAttack();
        } else {
            animationDispatcher.tailAttack();
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
    public int maxJellyToGrowth() {
        return 9;
    }

    public PraetorianAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }

    public static EntityType<? extends Alien> getType(AlienVariant alienVariant) {
        return switch (alienVariant) {
            case NORMAL -> AVPEntityTypes.PRAETORIAN.get();
            case NETHER -> AVPEntityTypes.NETHER_PRAETORIAN.get();
            case ABERRANT -> AVPEntityTypes.ABERRANT_PRAETORIAN.get();
            case IRRADIATED -> AVPEntityTypes.IRRADIATED_PRAETORIAN.get();
        };
    }
}
