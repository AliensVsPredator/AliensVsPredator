package com.avp.common.entity.living.alien.manager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.alien.util.AlienVariantUtil;
import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.lifecycle.growth.GrowthStage;
import com.avp.common.lifecycle.registry.AlienLifecycleRegistry;
import com.avp.common.util.BlockPosUtil;

public class GrowthManager {

    private static final String GROWTH_TIME_IN_TICKS_TAG_KEY = "growthTimeInTicks";

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

        var type = AlienVariantUtil.getVariantTypeFor(entity);
        var growthStage = AlienLifecycleRegistry.getOrNull(null, type);

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

    // TODO: Make this return a sealed type result since there are checks here we want to do that might cause growth failure.
    public @Nullable LivingEntity grow(GrowthStage growthStage) {
        // Reset growth time at this point.
        this.growthTimeInTicks = 0;

        var level = entity.level();
        var nextFormType = growthStage.to();
        var nextForm = nextFormType.create(level);

        if (nextForm == null) {
            return null;
        }

        swapOldStageWithNewStage(nextForm, level);

        if (onGrowUpCallback != null) {
            onGrowUpCallback.accept(nextForm);
        }

        return nextForm;
    }

    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains(GROWTH_TIME_IN_TICKS_TAG_KEY)) {
            this.growthTimeInTicks = compoundTag.getInt(GROWTH_TIME_IN_TICKS_TAG_KEY);
        }
    }

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

    private void swapOldStageWithNewStage(LivingEntity nextForm, Level level) {
        copyEntityTagData(entity, nextForm);
        nextForm.getEntityData().set(Xenomorph.JELLY_COUNT, 0);

        // Move the next form to the entity's current position. Set rotation angles as well.
        nextForm.moveTo(entity.position(), entity.getYRot(), entity.getXRot());

        // Explicitly set the yaw and pitch to ensure accurate orientation
        nextForm.setYRot(entity.getYRot());
        nextForm.setXRot(entity.getXRot());

        // Synchronize the visual body rotation (if applicable for mobs)
        nextForm.yBodyRot = entity.yBodyRot; // Body rotation
        nextForm.yHeadRot = entity.yHeadRot; // Head rotation

        nextForm.setDeltaMovement(entity.getDeltaMovement());

        // Copies effects from previous entity to the next
        for (var effect : entity.getActiveEffects()) {
            nextForm.addEffect(new MobEffectInstance(effect));
        }

        if (entity.isPersistenceRequired() && nextForm instanceof Alien alien) {
            alien.setPersistenceRequired();
        }

        // Add the new form to the level.
        level.addFreshEntity(nextForm);

        // Remove the old form from the level *without* killing it.
        entity.discard();
    }

    private void copyEntityTagData(Entity oldEntity, Entity newEntity) {
        // Step 1: Save old entity's data to a CompoundTag
        var oldEntityData = new CompoundTag();
        oldEntity.save(oldEntityData);

        // Removes specific fields that shouldn't be copied.
        oldEntityData.remove("id"); // Entity id shouldn't carry over since we're creating a new entity.
        oldEntityData.remove("UUID"); // Each entity must have a unique UUID.
        oldEntityData.remove("Pos"); // Position is set separately.
        oldEntityData.remove("Motion"); // Velocity is handled separately.
        oldEntityData.remove("Rotation"); // Rotation is set separately.
        oldEntityData.remove("Health"); // Health should be whatever the new entity's health is.
        oldEntityData.remove("attributes"); // New entity shouldn't take on attributes of the old entity.
        oldEntityData.remove(GROWTH_TIME_IN_TICKS_TAG_KEY); // Growth should be reset.

        // Step 2: Load the data into the new entity
        newEntity.load(oldEntityData);
    }
}
