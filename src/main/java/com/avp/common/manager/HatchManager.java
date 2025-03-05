package com.avp.common.manager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.level.Level;

import com.avp.common.entity.living.alien.ovamorph.Ovamorph;
import com.avp.common.entity.type.AVPEntityTypes;

public class HatchManager {

    private static final String HATCH_DURATION_IN_TICKS_KEY = "hatchDurationInTicks";

    private static final String HATCHED_KEY = "hatched";

    private static final String MAXIMUM_SPAWN_COUNT_KEY = "maximumSpawnCount";

    private static final String REMAINING_SPAWN_DELAY_IN_TICKS_KEY = "remainingSpawnDelayInTicks";

    private static final String SPAWN_COUNT_KEY = "spawnCount";

    private final Ovamorph ovamorph;

    private final EntityDataAccessor<Boolean> hatchedEDA;

    private final EntityDataAccessor<Byte> maximumSpawnCountEDA;

    private final int hatchDurationInTicks;

    private final int spawnDelayInTicks;

    private int remainingHatchDurationInTicks;

    private int remainingSpawnDelayInTicks;

    private int spawnCount;

    public HatchManager(
        Ovamorph ovamorph,
        EntityDataAccessor<Boolean> hatchedEDA,
        EntityDataAccessor<Byte> maximumSpawnCountEDA,
        int hatchDurationInTicks,
        int spawnDelayInTicks
    ) {
        this.ovamorph = ovamorph;
        this.hatchedEDA = hatchedEDA;
        this.maximumSpawnCountEDA = maximumSpawnCountEDA;
        this.hatchDurationInTicks = hatchDurationInTicks;
        this.remainingHatchDurationInTicks = hatchDurationInTicks;
        this.spawnDelayInTicks = spawnDelayInTicks;
        this.remainingSpawnDelayInTicks = spawnDelayInTicks;
        this.spawnCount = 0;
    }

    public void tick() {
        if (hatched()) {
            remainingHatchDurationInTicks = Math.max(remainingHatchDurationInTicks - 1, 0);
        }

        var level = ovamorph.level();

        if (level.isClientSide) {
            return;
        }

        var hasFullyHatched = remainingHatchDurationInTicks <= 0;
        var canSpawn = spawnCount < maximumSpawnCount();

        if (!hasFullyHatched || !canSpawn) {
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

    public boolean hatched() {
        return ovamorph.getEntityData().get(hatchedEDA);
    }

    public void hatch() {
        ovamorph.getEntityData().set(hatchedEDA, true);
    }

    public byte maximumSpawnCount() {
        return ovamorph.getEntityData().get(maximumSpawnCountEDA);
    }

    public void restore() {
        this.spawnCount = 0;
        this.remainingHatchDurationInTicks = hatchDurationInTicks;
        this.remainingSpawnDelayInTicks = spawnDelayInTicks;
        ovamorph.getEntityData().set(hatchedEDA, false);
    }

    public void load(CompoundTag compoundTag) {
        this.remainingHatchDurationInTicks = compoundTag.getInt(HATCH_DURATION_IN_TICKS_KEY);
        this.remainingSpawnDelayInTicks = compoundTag.getInt(REMAINING_SPAWN_DELAY_IN_TICKS_KEY);
        this.spawnCount = compoundTag.getInt(SPAWN_COUNT_KEY);

        ovamorph.getEntityData().set(hatchedEDA, compoundTag.getBoolean(HATCHED_KEY));
        ovamorph.getEntityData().set(maximumSpawnCountEDA, compoundTag.getByte(MAXIMUM_SPAWN_COUNT_KEY));
    }

    public void save(CompoundTag compoundTag) {
        compoundTag.putInt(HATCH_DURATION_IN_TICKS_KEY, remainingHatchDurationInTicks);
        compoundTag.putInt(REMAINING_SPAWN_DELAY_IN_TICKS_KEY, remainingSpawnDelayInTicks);
        compoundTag.putInt(SPAWN_COUNT_KEY, spawnCount);

        compoundTag.putBoolean(HATCHED_KEY, ovamorph.getEntityData().get(hatchedEDA));
        compoundTag.putByte(MAXIMUM_SPAWN_COUNT_KEY, ovamorph.getEntityData().get(maximumSpawnCountEDA));
    }

    private void spawnFacehugger(Level level) {
        var facehugger = AVPEntityTypes.FACEHUGGER.create(level);

        if (facehugger == null) {
            // TODO: Log.
            return;
        }

        facehugger.geneManager().setAll(ovamorph.geneManager().getAll());
        facehugger.updateStateBasedOnGenetics();

        facehugger.moveTo(ovamorph.blockPosition(), ovamorph.getYRot(), ovamorph.getXRot());

        // Explicitly set the yaw and pitch to ensure accurate orientation
        facehugger.setYRot(ovamorph.getYRot());
        facehugger.setXRot(ovamorph.getXRot());

        // Synchronize the visual body rotation (if applicable for mobs)
        facehugger.yBodyRot = ovamorph.yBodyRot; // Body rotation
        facehugger.yHeadRot = ovamorph.yHeadRot; // Head rotation

        level.addFreshEntity(facehugger);
    }
}
