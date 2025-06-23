package com.alien.common.gameplay.entity.living.alien;

import com.alien.common.gameplay.entity.living.alien.xenomorph.boiler.Boiler;
import com.alien.common.model.alien.GeneCarrier;
import com.alien.common.model.lifecycle.growth.GrowthStage;
import com.alien.common.registry.GrowthStageRegistry;
import com.lib.common.gameplay.NBTSerializable;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.Genes;
import com.lib.common.gameplay.util.spatial.block.BlockPosUtil;
import com.lib.common.util.GeneIntegrityUtil;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.avp.common.registry.tag.AVPEntityTypeTags;
import com.avp.common.util.AVPEntityTransitionUtil;

public class GrowthManager implements NBTSerializable {

    private static final String GROWTH_TIME_IN_TICKS_TAG_KEY = "growthTimeInTicks";

    private static final Set<String> TRANSITION_NBT_KEY_BLACKLIST = Util.make(() -> {
        var set = new HashSet<>(AVPEntityTransitionUtil.DEFAULT_NBT_KEY_BLACKLIST);
        set.add(GROWTH_TIME_IN_TICKS_TAG_KEY);
        return set;
    });

    private final Alien entity;

    private final @Nullable Consumer<Entity> onGrowUpCallback;

    private boolean growOverTime;

    private int growthTimeInTicks;

    private int growthRetryTimeInTicks = 0;

    private boolean readyToGrow;

    public GrowthManager(Alien entity) {
        this(entity, null);
    }

    public GrowthManager(Alien entity, @Nullable Consumer<Entity> onGrowUpCallback) {
        this.entity = entity;
        this.onGrowUpCallback = onGrowUpCallback;
        this.growOverTime = true;
        this.readyToGrow = false;
    }

    public void tick() {
        if (
            entity.level().isClientSide
                || canNeverGrow()
        ) {
            return;
        }

        var growthStage = getNextGrowthStage();

        if (growthStage == null) {
            return;
        }

        var canBypassGrowthTime = entity.getJellyCount() >= entity.getMaxJellyToGrowth();

        if (canBypassGrowthTime) {
            // If we can bypass growing over time thanks to royal jelly, then do so.
            this.readyToGrow = true;
        } else if (growOverTime) {
            // Otherwise if we can't bypass growth time, tick the entity's growth progress.
            growOverTime();
        }

        if (!readyToGrow) {
            // If the entity isn't ready to grow, then don't continue any further.
            return;
        }

        this.growthRetryTimeInTicks = Math.max(growthRetryTimeInTicks - 1, 0);

        if (growthRetryTimeInTicks > 0) {
            return;
        }

        if (!BlockPosUtil.canEntityTypeFit(entity.level(), entity.blockPosition(), growthStage.to())) {
            // if the next stage of the entity's growth can't fit at the entity's location, then the entity can't grow
            // up yet.
            // TODO: Add particles here maybe if the alien can't grow up, to indicate "frustration"?
            // Apply a buffer time period before we retry checking collision.
            this.growthRetryTimeInTicks = 20 * 10;
            return;
        }

        grow();
    }

    private @Nullable GrowthStage getNextGrowthStage() {
        var hostType = entity.getHostType().unwrapOr(null);
        return GrowthStageRegistry.getOrNull(hostType, entity.getType());
    }

    private void growOverTime() {
        this.growthTimeInTicks++;

        var growthStage = getNextGrowthStage();

        if (growthStage == null) {
            return;
        }

        var requiredGrowthTimeInTicks = growthStage.growthTimeInTicks();
        var growthTimeReductionMultiplier = 1F;

        if (growthTimeInTicks < requiredGrowthTimeInTicks * growthTimeReductionMultiplier) {
            return;
        }

        this.readyToGrow = true;
    }

    // TODO:
    // Make this return a sealed type result since there are checks here we want to do that might cause growth failure.
    public @Nullable Entity grow() {
        // Reset growth time at this point.
        this.growthTimeInTicks = 0;
        var growthStage = getNextGrowthStage();

        if (growthStage == null || canNeverGrow()) {
            return null;
        }

        var nextFormType = growthStage.to();

        var canBecomeBoiler = canBecomeBoiler(nextFormType);

        Entity nextForm;

        if (canBecomeBoiler) {
            nextFormType = Boiler.getType(entity.getVariant());
        }

        nextForm = AVPEntityTransitionUtil.transitionInto(entity, nextFormType, TRANSITION_NBT_KEY_BLACKLIST);

        if (nextForm == null) {
            return null;
        }

        if (nextForm instanceof Alien alien) {
            alien.setJellyCount(0);
        }

        if (onGrowUpCallback != null) {
            onGrowUpCallback.accept(nextForm);
        }

        return nextForm;
    }

    private boolean canNeverGrow() {
        return entity.isPoisoned()
            || entity.isIrradiated();
    }

    private boolean canBecomeBoiler(EntityType<?> nextFormType) {
        if (!isProperTransition(nextFormType)) {
            return false;
        }

        return shouldBecomeBoilerFromGeneDecay() || shouldBecomeBoilerFromAcidVolatility();
    }

    private boolean isProperTransition(EntityType<?> nextFormType) {
        var isCurrentlyAdolescent = entity.getType().is(AVPEntityTypeTags.ADOLESCENTS);
        var willGrowIntoAdult = nextFormType.is(AVPEntityTypeTags.XENOMORPHS);

        return isCurrentlyAdolescent
            && willGrowIntoAdult;
    }

    private boolean shouldBecomeBoilerFromAcidVolatility() {
        var geneContainer = entity.getGeneManager().getGeneContainer();
        var additiveAcidVolatility = geneContainer.getActiveGeneValue(Genes.ACID_VOLATILITY, GeneOperationType.ADDITIVE);
        var multiplicativeAcidVolatility = geneContainer.getActiveGeneValue(Genes.ACID_VOLATILITY, GeneOperationType.MULTIPLICATIVE);

        var totalAcidVolatility = additiveAcidVolatility + multiplicativeAcidVolatility;

        return entity.getRandom().nextDouble() < totalAcidVolatility;
    }

    private boolean shouldBecomeBoilerFromGeneDecay() {
        var geneCarrier = (GeneCarrier) entity;
        var geneDecayLevel = GeneIntegrityUtil.getGeneDecayLevel(geneCarrier);

        return switch (geneDecayLevel) {
            case FATAL -> true;
            case STABLE, UNSTABLE -> false;
            case VOLATILE -> {
                // Ex. -2.75 -> 2.75
                var totalGeneIntegrity = Math.abs(GeneIntegrityUtil.getTotalGeneticIntegrity(geneCarrier));
                // Ex. 2.75 - 2 = 0.75
                var chance = totalGeneIntegrity - Math.floor(totalGeneIntegrity);
                // Ex. 0.75 means 75% chance to be a boiler.
                yield entity.getRandom().nextDouble() < chance;
            }
        };
    }

    @Override
    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains(GROWTH_TIME_IN_TICKS_TAG_KEY)) {
            this.growthTimeInTicks = compoundTag.getInt(GROWTH_TIME_IN_TICKS_TAG_KEY);
        }
    }

    @Override
    public void save(CompoundTag compoundTag) {
        compoundTag.putInt(GROWTH_TIME_IN_TICKS_TAG_KEY, growthTimeInTicks);
    }

    public GrowthManager setGrowOverTime(boolean growOverTime) {
        this.growOverTime = growOverTime;
        return this;
    }
}
