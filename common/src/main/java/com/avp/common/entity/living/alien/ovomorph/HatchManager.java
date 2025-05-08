package com.avp.common.entity.living.alien.ovomorph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import com.avp.AVP;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.sound.AVPSoundEvents;

public class HatchManager {

    private static final String HATCH_DURATION_IN_TICKS_KEY = "hatchDurationInTicks";

    // TODO: Remove this in 0.2.0.
    @Deprecated(forRemoval = true)
    private static final String HATCHED_KEY = "hatched";

    private static final String REMAINING_SPAWN_DELAY_IN_TICKS_KEY = "remainingSpawnDelayInTicks";

    private static final String SPAWN_COUNT_KEY = "spawnCount";

    private final Ovomorph ovomorph;

    private final int hatchDurationInTicks;

    private final int spawnDelayInTicks;

    private int remainingHatchDurationInTicks;

    private int remainingSpawnDelayInTicks;

    private int spawnCount;

    public HatchManager(Ovomorph ovomorph, int hatchDurationInTicks, int spawnDelayInTicks) {
        this.ovomorph = ovomorph;
        this.hatchDurationInTicks = hatchDurationInTicks;
        this.remainingHatchDurationInTicks = hatchDurationInTicks;
        this.spawnDelayInTicks = spawnDelayInTicks;
        this.remainingSpawnDelayInTicks = spawnDelayInTicks;
        this.spawnCount = 0;
    }

    public void tick() {
        var level = ovomorph.level();

        if (level.isClientSide) {
            return;
        }

        if (isHatching()) {
            remainingHatchDurationInTicks = Math.max(remainingHatchDurationInTicks - 1, 0);
        }

        if (!isReadyToSpawnFacehuggers()) {
            return;
        }

        // The ovomorph has fully opened visually at this point, so set its state to hatched.
        ovomorph.setHatchState(HatchState.HATCHED);

        var canSpawnMoreFacehuggers = spawnCount < ovomorph.getMaximumSpawnCount();

        if (!canSpawnMoreFacehuggers) {
            return;
        }

        remainingSpawnDelayInTicks = Math.max(remainingSpawnDelayInTicks - 1, 0);

        if (remainingSpawnDelayInTicks == 0) {
            spawnFacehugger(level);
            // Reset spawn delay.
            remainingSpawnDelayInTicks = spawnDelayInTicks;
            // Increment the spawns created.
            spawnCount++;
        }
    }

    public boolean isReadyToSpawnFacehuggers() {
        return remainingHatchDurationInTicks <= 0;
    }

    public boolean isHatching() {
        return ovomorph.getHatchState().contains(HatchState.HATCHING);
    }

    public boolean isHatched() {
        return ovomorph.getHatchState().contains(HatchState.HATCHED);
    }

    public void hatch() {
        if (isHatching() || isHatched()) {
            // If the ovomorph is hatching or has already hatched, then don't bother trying to hatch again.
            return;
        }

        ovomorph.setHatchState(HatchState.HATCHING);
        ovomorph.level().playSound(null, ovomorph, AVPSoundEvents.ENTITY_OVOMORPH_HATCH.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
    }

    public void restore() {
        this.spawnCount = 0;
        this.remainingHatchDurationInTicks = hatchDurationInTicks;
        this.remainingSpawnDelayInTicks = spawnDelayInTicks;
        ovomorph.setHatchState(Ovomorph.DEFAULT_HATCH_STATE);
    }

    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains(HATCH_DURATION_IN_TICKS_KEY)) {
            this.remainingHatchDurationInTicks = compoundTag.getInt(HATCH_DURATION_IN_TICKS_KEY);
        }

        if (compoundTag.contains(REMAINING_SPAWN_DELAY_IN_TICKS_KEY)) {
            this.remainingSpawnDelayInTicks = compoundTag.getInt(REMAINING_SPAWN_DELAY_IN_TICKS_KEY);
        }

        if (compoundTag.contains(SPAWN_COUNT_KEY)) {
            this.spawnCount = compoundTag.getInt(SPAWN_COUNT_KEY);
        }

        // Here for backwards compatibility.
        // TODO: Remove this in 0.2.0.
        if (compoundTag.contains(HATCHED_KEY)) {
            ovomorph.setHatchState(compoundTag.getBoolean(HATCHED_KEY) ? HatchState.HATCHING : Ovomorph.DEFAULT_HATCH_STATE);
        }
    }

    public void save(CompoundTag compoundTag) {
        compoundTag.putInt(HATCH_DURATION_IN_TICKS_KEY, remainingHatchDurationInTicks);
        compoundTag.putInt(REMAINING_SPAWN_DELAY_IN_TICKS_KEY, remainingSpawnDelayInTicks);
        compoundTag.putInt(SPAWN_COUNT_KEY, spawnCount);
    }

    private void spawnFacehugger(Level level) {
        var facehugger = (ovomorph.isRoyal() ? AVPEntityTypes.ROYAL_FACEHUGGER : AVPEntityTypes.FACEHUGGER).get().create(level);

        if (facehugger == null) {
            AVP.LOGGER.warn("Failed to create facehugger entity.");
            return;
        }

        facehugger.geneManager().setAll(ovomorph.geneManager().getAll());
        facehugger.updateStateBasedOnGenetics();

        var ovomorphAbovePos = ovomorph.blockPosition().above();
        var ovomorphSuffocatingAboveCheck = ovomorph.level()
            .getBlockState(ovomorphAbovePos)
            .isSuffocating(ovomorph.level(), ovomorphAbovePos);
        // Spawns it at the top of the ovomorph if the above block is not a suffocating block, else spawn at the bottom
        // of ovomorph.
        var ovomorphYPos = ovomorphSuffocatingAboveCheck ? ovomorph.position().y : ovomorph.position().y + ovomorph.getBbHeight();
        facehugger.setPos(ovomorph.position().x, ovomorphYPos, ovomorph.position().z);

        // Explicitly set the yaw and pitch to ensure accurate orientation
        facehugger.setYRot(ovomorph.getYRot());
        facehugger.setXRot(ovomorph.getXRot());

        // Synchronize the visual body rotation.
        facehugger.yBodyRot = ovomorph.yBodyRot; // Body rotation
        facehugger.yHeadRot = ovomorph.yHeadRot; // Head rotation

        // Gives the facehugger a jump like movement if the block above is not a suffocating block.
        if (!ovomorphSuffocatingAboveCheck) {
            facehugger.setDeltaMovement(
                Mth.nextFloat(facehugger.getRandom(), -0.5f, 0.5f),
                0.7,
                Mth.nextFloat(facehugger.getRandom(), -0.5f, 0.5f)
            );
        }

        level.addFreshEntity(facehugger);
    }
}
