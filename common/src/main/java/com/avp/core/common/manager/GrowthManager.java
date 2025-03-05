package com.avp.core.common.manager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.avp.core.common.lifecycle.growth.GrowthStage;
import com.avp.core.common.lifecycle.registry.AlienLifecycleRegistry;

public class GrowthManager {

    private static final String GROWTH_TIME_IN_TICKS_TAG_KEY = "growthTimeInTicks";

    private final LivingEntity entity;

    private final @Nullable Consumer<LivingEntity> onGrowUpCallback;

    private boolean growOverTime;

    private int growthTimeInTicks;

    private @Nullable Supplier<Float> growthTimeReductionMultiplierProvider;

    public GrowthManager(LivingEntity entity) {
        this(entity, null);
    }

    public GrowthManager(LivingEntity entity, @Nullable Consumer<LivingEntity> onGrowUpCallback) {
        this.entity = entity;
        this.onGrowUpCallback = onGrowUpCallback;
        this.growOverTime = true;
    }

    public void tick() {
        if (entity.level().isClientSide || !growOverTime) {
            return;
        }

        var type = entity.getType();
        var growthStage = AlienLifecycleRegistry.getOrNull(null, type);
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

        grow(growthStage);
    }

    public @Nullable LivingEntity grow(GrowthStage growthStage) {
        // Reset growth time at this point.
        this.growthTimeInTicks = 0;

        var level = entity.level();
        var nextFormType = growthStage.to().get();
        var nextForm = nextFormType.create(level);

        if (nextForm == null) {
            return nextForm;
        }

        swapOldStageWithNewStage(nextForm, level);

        if (onGrowUpCallback != null) {
            onGrowUpCallback.accept(nextForm);
        }

        return nextForm;
    }

    public void load(CompoundTag compoundTag) {
        this.growthTimeInTicks = compoundTag.getInt(GROWTH_TIME_IN_TICKS_TAG_KEY);
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

        // Move the next form to the entity's current position. Set rotation angles as well.
        nextForm.moveTo(entity.position(), entity.getYRot(), entity.getXRot());

        // Explicitly set the yaw and pitch to ensure accurate orientation
        nextForm.setYRot(entity.getYRot());
        nextForm.setXRot(entity.getXRot());

        // Synchronize the visual body rotation (if applicable for mobs)
        nextForm.yBodyRot = entity.yBodyRot; // Body rotation
        nextForm.yHeadRot = entity.yHeadRot; // Head rotation

        nextForm.setDeltaMovement(entity.getDeltaMovement());

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
