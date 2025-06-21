package com.alien.common.gameplay.entity.living.alien.xenomorph.boiler;

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

public class Boiler extends Xenomorph {

    public static AttributeSupplier.Builder createBoilerAttributes() {
        return applyFrom(AVP.config.statsConfigs.BOILER_STATS, Monster.createMonsterAttributes());
    }

    private final BoilerAnimationDispatcher animationDispatcher;

    public Boiler(EntityType<? extends Boiler> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new BoilerAnimationDispatcher(this);
        this.config = AVP.config.statsConfigs.BOILER_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getTypeForVariant(AlienVariant alienVariant) {
        return getType(alienVariant);
    }

    @Override
    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 16, 1, AVP.config.statsConfigs.BOILER_STATS.nestTickrate);
    }

    @Override
    public void runAttackAnimations() {}

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.config.statsConfigs.BOILER_STATS.healthRegenPerSecond;
    }

    @Override
    public int getMaxJellyToGrowth() {
        return -1;
    }

    public BoilerAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }

    public static EntityType<? extends Alien> getType(AlienVariant alienVariant) {
        return switch (alienVariant) {
            case NORMAL -> AlienEntityTypes.BOILER.get();
            case NETHER -> AlienEntityTypes.NETHER_BOILER.get();
            case ABERRANT -> AlienEntityTypes.ABERRANT_BOILER.get();
            case IRRADIATED -> null;
        };
    }
}
