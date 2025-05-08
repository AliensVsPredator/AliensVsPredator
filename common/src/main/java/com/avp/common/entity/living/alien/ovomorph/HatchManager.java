package com.avp.common.entity.living.alien.ovomorph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import com.avp.AVP;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.sound.AVPSoundEvents;

public class HatchManager {

    private static final String HATCH_DURATION_IN_TICKS_KEY = "hatchDurationInTicks";

    @Deprecated(forRemoval = true)
    // TODO: Remove this in 0.2.0.
    private static final String HATCHED_KEY = "hatched";

    private static final String HATCH_STATE_KEY = "hatchState";

    private static final String MAXIMUM_SPAWN_COUNT_KEY = "maximumSpawnCount";

    private static final String REMAINING_SPAWN_DELAY_IN_TICKS_KEY = "remainingSpawnDelayInTicks";

    private static final String SPAWN_COUNT_KEY = "spawnCount";

    private final Ovomorph ovomorph;

    private final EntityDataAccessor<Byte> hatchStateEDA;

    private final EntityDataAccessor<Byte> maximumSpawnCountEDA;

    private final int hatchDurationInTicks;

    private final int spawnDelayInTicks;

    private int remainingHatchDurationInTicks;

    private int remainingSpawnDelayInTicks;

    private int spawnCount;

    public HatchManager(
        Ovomorph ovomorph,
        EntityDataAccessor<Byte> hatchStateEDA,
        EntityDataAccessor<Byte> maximumSpawnCountEDA,
        int hatchDurationInTicks,
        int spawnDelayInTicks
    ) {
        this.ovomorph = ovomorph;
        this.hatchStateEDA = hatchStateEDA;
        this.maximumSpawnCountEDA = maximumSpawnCountEDA;
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
        ovomorph.getEntityData().set(hatchStateEDA, (byte) HatchState.HATCHED.getId());

        var canSpawnMoreFacehuggers = spawnCount < maximumSpawnCount();

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

    public boolean isSleeping() {
        return ovomorph.getEntityData().get(hatchStateEDA) == HatchState.SLEEPING.getId();
    }

    public boolean isHatching() {
        return ovomorph.getEntityData().get(hatchStateEDA) == HatchState.HATCHING.getId();
    }

    public boolean isHatched() {
        return ovomorph.getEntityData().get(hatchStateEDA) == HatchState.HATCHED.getId();
    }

    public void hatch() {
        if (isHatching() || isHatched()) {
            // If the ovomorph is hatching or has already hatched, then don't bother trying to hatch again.
            return;
        }

        ovomorph.getEntityData().set(hatchStateEDA, (byte) HatchState.HATCHING.getId());
        ovomorph.level().playSound(null, ovomorph, AVPSoundEvents.ENTITY_OVOMORPH_HATCH.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
    }

    public byte maximumSpawnCount() {
        return ovomorph.getEntityData().get(maximumSpawnCountEDA);
    }

    public void restore() {
        this.spawnCount = 0;
        this.remainingHatchDurationInTicks = hatchDurationInTicks;
        this.remainingSpawnDelayInTicks = spawnDelayInTicks;
        ovomorph.getEntityData().set(hatchStateEDA, (byte) HatchState.SLEEPING.getId());
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
            ovomorph.getEntityData()
                .set(hatchStateEDA, (byte) (compoundTag.getBoolean(HATCHED_KEY) ? HatchState.HATCHING : HatchState.SLEEPING).getId());
        }

        // New logic for hatch state bookkeeping.
        if (compoundTag.contains(HATCH_STATE_KEY)) {
            ovomorph.getEntityData().set(hatchStateEDA, (byte) compoundTag.getInt(HATCH_STATE_KEY));
        }

        if (compoundTag.contains(MAXIMUM_SPAWN_COUNT_KEY)) {
            ovomorph.getEntityData().set(maximumSpawnCountEDA, compoundTag.getByte(MAXIMUM_SPAWN_COUNT_KEY));
        }
    }

    public void save(CompoundTag compoundTag) {
        compoundTag.putInt(HATCH_DURATION_IN_TICKS_KEY, remainingHatchDurationInTicks);
        compoundTag.putInt(REMAINING_SPAWN_DELAY_IN_TICKS_KEY, remainingSpawnDelayInTicks);
        compoundTag.putInt(SPAWN_COUNT_KEY, spawnCount);

        compoundTag.putInt(HATCH_STATE_KEY, ovomorph.getEntityData().get(hatchStateEDA));
        compoundTag.putByte(MAXIMUM_SPAWN_COUNT_KEY, ovomorph.getEntityData().get(maximumSpawnCountEDA));
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
