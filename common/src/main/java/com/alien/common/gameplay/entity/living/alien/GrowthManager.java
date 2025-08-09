package com.alien.common.gameplay.entity.living.alien;

import com.alien.common.gameplay.entity.living.alien.xenomorph.boiler.Boiler;
import com.alien.common.model.lifecycle.growth.GrowthStage;
import com.alien.common.registry.GrowthStageRegistry;
import com.lib.common.gameplay.NBTSerializable;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.Genes;
import com.lib.common.model.GeneCarrier;
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

        var canBypassGrowthTime = entity.getMaxJellyToGrowth() != null && entity.getJellyCount() >= entity.getMaxJellyToGrowth();

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

        // Growth attempts can fail for a lot of reasons. This switch covers every possible reason.
        switch (grow()) {
            case GrowthResult.AlreadyFullyGrown $ -> {/* NO-OP */}
            case GrowthResult.CanNotGrow $ -> {/* NO-OP */}
            case GrowthResult.Success $ -> {/* NO-OP */}
            case GrowthResult.FailedTransitionResult failedTransitionResult -> {
                switch (failedTransitionResult.result) {
                    case AVPEntityTransitionUtil.EntityTransitionResult.ClientSide $1 -> {/* NO-OP */}
                    case AVPEntityTransitionUtil.EntityTransitionResult.EntityCreation $1 -> {/* NO-OP */}
                    case AVPEntityTransitionUtil.EntityTransitionResult.Obstructed $1 ->
                        // If the entity failed to grow, then retry in 10 seconds.
                        // TODO: Add particles here maybe if the alien can't grow up, to indicate "frustration"?
                        // Apply a buffer time period before we retry growing.
                        this.growthRetryTimeInTicks = 20 * 10;
                    case AVPEntityTransitionUtil.EntityTransitionResult.Success<?> $1 -> {/* NO-OP */ }
                }
            }
        }
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

    public GrowthResult grow() {
        // Reset growth time at this point.
        this.growthTimeInTicks = 0;
        var growthStage = getNextGrowthStage();

        if (growthStage == null) {
            return GrowthResult.AlreadyFullyGrown.INSTANCE;
        } else if (canNeverGrow()) {
            return GrowthResult.CanNotGrow.INSTANCE;
        }

        var nextFormType = growthStage.to();

        var canBecomeBoiler = canBecomeBoiler(nextFormType);

        Entity nextForm;

        if (canBecomeBoiler) {
            nextFormType = Boiler.getType(entity.getVariant());
        }

        var transitionResult = AVPEntityTransitionUtil.transitionInto(entity, nextFormType, TRANSITION_NBT_KEY_BLACKLIST);

        nextForm = switch (transitionResult) {
            case AVPEntityTransitionUtil.EntityTransitionResult.ClientSide ignored -> null;
            case AVPEntityTransitionUtil.EntityTransitionResult.EntityCreation ignored -> null;
            case AVPEntityTransitionUtil.EntityTransitionResult.Obstructed ignored -> null;
            case AVPEntityTransitionUtil.EntityTransitionResult.Success<?> success -> success.newEntity();
        };

        if (nextForm == null) {
            return new GrowthResult.FailedTransitionResult(transitionResult);
        }

        if (nextForm instanceof Alien alien) {
            var jellyCountToSubtract = entity.getMaxJellyToGrowth() == null
                ? 0
                : entity.getMaxJellyToGrowth();

            alien.setJellyCount(entity.getJellyCount() - jellyCountToSubtract);
        }

        if (onGrowUpCallback != null) {
            onGrowUpCallback.accept(nextForm);
        }

        return new GrowthResult.Success(nextForm);
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
        var additiveAcidVolatility = geneContainer.getActiveGeneMap()
            .getValue(Genes.ACID_VOLATILITY, GeneOperationType.ADDITIVE);
        var multiplicativeAcidVolatility = geneContainer.getActiveGeneMap()
            .getValue(Genes.ACID_VOLATILITY, GeneOperationType.MULTIPLICATIVE);

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

    public sealed interface GrowthResult {

        enum AlreadyFullyGrown implements GrowthResult {
            INSTANCE
        }

        enum CanNotGrow implements GrowthResult {
            INSTANCE
        }

        record FailedTransitionResult(AVPEntityTransitionUtil.EntityTransitionResult result) implements GrowthResult {}

        record Success(Entity newEntity) implements GrowthResult {}
    }
}
