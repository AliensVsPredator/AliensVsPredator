package com.alien.common.gameplay.entity.living.alien.xenomorph.boiler;

import com.alien.common.gameplay.ai.InvestigateVibrationGoal;
import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.model.alien.variant.AlienVariant;
import com.alien.common.model.resin.ResinData;
import com.alien.common.registry.init.AlienEntityTypes;
import com.alien.common.util.AcidBleedUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
    protected @Nullable ResinData createResinData() {
        return null;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(3, new InvestigateVibrationGoal(this));
    }

    @Override
    protected boolean canTargetInitially(LivingEntity target) {
        return target.distanceToSqr(this) <= 4 * 4
            && super.canTargetInitially(target);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        var radius = 2F;
        level().explode(this, getX(), getY(), getZ(), radius, Level.ExplosionInteraction.MOB);
        // TODO:
        // this.spawnLingeringCloud();
        triggerOnDeathMobEffects(RemovalReason.KILLED);
        discard();

        // TODO: Redo the way acid strength is determined here, it isn't the greatest.
        getBlockArea(blockPosition(), (int) radius, (int) radius, (int) radius)
            .stream()
            .filter(blockPos -> {
                var blockState = level().getBlockState(blockPos);
                return blockState.isAir() || blockState.canBeReplaced();
            })
            .forEach(blockPos -> AcidBleedUtil.spawnAcid(this, 3, blockPos.getCenter()));

        return true;
    }

    // TODO: Move this to a util class.
    private List<BlockPos> getBlockArea(BlockPos center, int radiusX, int radiusY, int radiusZ) {
        var positions = new ArrayList<BlockPos>();

        for (var dx = -radiusX; dx <= radiusX; dx++) {
            for (var dy = -radiusY; dy <= radiusY; dy++) {
                for (var dz = -radiusZ; dz <= radiusZ; dz++) {
                    positions.add(center.offset(dx, dy, dz));
                }
            }
        }

        return positions;
    }

    @Override
    protected void addDigToTargetGoal() {}

    @Override
    public void runAttackAnimations() {}

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.config.statsConfigs.BOILER_STATS.healthRegenPerSecond;
    }

    @Override
    public Integer getMaxJellyToGrowth() {
        return null;
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
