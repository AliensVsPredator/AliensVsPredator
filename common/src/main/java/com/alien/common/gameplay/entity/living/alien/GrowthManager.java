package com.alien.common.gameplay.entity.living.alien;

import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.model.lifecycle.growth.GrowthStage;
import com.alien.common.registry.AlienLifecycleRegistry;
import com.lib.common.gameplay.NBTSerializable;
import com.lib.common.gameplay.util.spatial.block.BlockPosUtil;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.avp.common.util.AVPEntityTransitionUtil;

public class GrowthManager implements NBTSerializable {

    private static final String GROWTH_TIME_IN_TICKS_TAG_KEY = "growthTimeInTicks";

    private static final Set<String> TRANSITION_NBT_KEY_BLACKLIST = Util.make(() -> {
        var set = new HashSet<>(AVPEntityTransitionUtil.DEFAULT_NBT_KEY_BLACKLIST);
        set.add(GROWTH_TIME_IN_TICKS_TAG_KEY);
        return set;
    });

    private final Alien entity;

    private final @Nullable Consumer<LivingEntity> onGrowUpCallback;

    private boolean growOverTime;

    private int growthTimeInTicks;

    private int growthRetryTimeInTicks = 0;

    private boolean readyToGrow;

    private @Nullable Supplier<Float> growthTimeReductionMultiplierProvider;

    public GrowthManager(Alien entity) {
        this(entity, null);
    }

    public GrowthManager(Alien entity, @Nullable Consumer<LivingEntity> onGrowUpCallback) {
        this.entity = entity;
        this.onGrowUpCallback = onGrowUpCallback;
        this.growOverTime = true;
        this.readyToGrow = false;
    }

    public void tick() {
        if (
            entity.level().isClientSide
                || entity.isPoisoned()
                || entity.isIrradiated()
        ) {
            return;
        }

        var growthStage = AlienLifecycleRegistry.getOrNull(null, entity.getType());

        if (growthStage == null) {
            return;
        }

        var canBypassGrowthTime = entity.getEntityData().get(Xenomorph.JELLY_COUNT) >= entity.maxJellyToGrowth();

        if (canBypassGrowthTime) {
            // If we can bypass growing over time thanks to royal jelly, then do so.
            this.readyToGrow = true;
        } else if (growOverTime) {
            // Otherwise if we can't bypass growth time, tick the entity's growth progress.
            growOverTime(growthStage);
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

        grow(growthStage);
    }

    private void growOverTime(GrowthStage growthStage) {
        this.growthTimeInTicks++;

        if (growthStage == null) {
            return;
        }

        var requiredGrowthTimeInTicks = growthStage.growthTimeInTicks();
        var growthTimeReductionMultiplier = 1F;

        if (growthTimeReductionMultiplierProvider != null) {
            var multiplier = growthTimeReductionMultiplierProvider.get();
            growthTimeReductionMultiplier = Math.clamp(multiplier, 0.2F, 1F);
        }

        if (growthTimeInTicks < requiredGrowthTimeInTicks * growthTimeReductionMultiplier) {
            return;
        }

        this.readyToGrow = true;
    }

    // TODO:
    // Make this return a sealed type result since there are checks here we want to do that might cause growth failure.
    public @Nullable LivingEntity grow(GrowthStage growthStage) {
        // Reset growth time at this point.
        this.growthTimeInTicks = 0;

        var nextFormType = growthStage.to();
        var nextForm = AVPEntityTransitionUtil.transitionInto(entity, nextFormType, TRANSITION_NBT_KEY_BLACKLIST);

        if (nextForm == null) {
            return null;
        }

        nextForm.getEntityData().set(Xenomorph.JELLY_COUNT, 0);

        if (onGrowUpCallback != null) {
            onGrowUpCallback.accept(nextForm);
        }

        return nextForm;
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

    public GrowthManager setGrowthTimeReductionMultiplierProvider(@Nullable Supplier<Float> growthTimeReductionMultiplierProvider) {
        this.growthTimeReductionMultiplierProvider = growthTimeReductionMultiplierProvider;
        return this;
    }
}
