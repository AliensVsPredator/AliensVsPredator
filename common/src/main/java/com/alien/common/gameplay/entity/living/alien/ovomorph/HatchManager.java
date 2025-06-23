package com.alien.common.gameplay.entity.living.alien.ovomorph;

import com.alien.common.gameplay.entity.living.alien.parasite.facehugger.Facehugger;
import com.alien.common.model.alien.HatchState;
import com.lib.common.network.DataAccessor;
import com.mojang.serialization.Codec;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import com.avp.AVP;
import com.avp.common.registry.init.AVPSoundEvents;

public class HatchManager {

    private final HatchDesireManager hatchDesireManager;

    private final Ovomorph ovomorph;

    private final DataAccessor<Integer> remainingHatchDurationInTicks;

    private final DataAccessor<Integer> remainingSpawnDelayInTicks;

    private final DataAccessor<Integer> spawnCount;

    public HatchManager(Ovomorph ovomorph, int hatchDurationInTicks, int spawnDelayInTicks) {
        this.hatchDesireManager = new HatchDesireManager(ovomorph);
        this.ovomorph = ovomorph;

        this.remainingHatchDurationInTicks = ovomorph.getDataContainer()
            .<Integer>builder("hatchDurationInTicks")
            .persistent(Codec.INT)
            .build(hatchDurationInTicks);
        this.remainingSpawnDelayInTicks = ovomorph.getDataContainer()
            .<Integer>builder("remainingSpawnDelayInTicks")
            .persistent(Codec.INT)
            .build(spawnDelayInTicks);
        this.spawnCount = ovomorph.getDataContainer()
            .<Integer>builder("spawnCount")
            .persistent(Codec.INT)
            .build(0);
    }

    public void tick() {
        hatchDesireManager.tick();

        var level = ovomorph.level();

        if (
            // If the running code is client-side...
            level.isClientSide
                // OR the ovomorph is not alive...
                || !ovomorph.isAlive()
                // OR the ovomorph is dead or dying...
                || ovomorph.isDeadOrDying()
        ) {
            // then return, the ovomorph should never attempt to hatch under any of these conditions.
            return;
        }

        if (isHatching()) {
            remainingHatchDurationInTicks.set(Math.max(remainingHatchDurationInTicks.get() - 1, 0));
        }

        if (!isReadyToSpawnFacehuggers()) {
            return;
        }

        // The ovomorph has fully opened visually at this point, so set its state to hatched.
        ovomorph.setHatchState(HatchState.HATCHED);

        var canSpawnMoreFacehuggers = spawnCount.get() < ovomorph.maxSpawnCount.get();

        if (!canSpawnMoreFacehuggers) {
            return;
        }

        remainingSpawnDelayInTicks.set(Math.max(remainingSpawnDelayInTicks.get() - 1, 0));

        if (remainingSpawnDelayInTicks.get() == 0) {
            spawnFacehugger(level);
            // Reset spawn delay.
            remainingSpawnDelayInTicks.reset();
            // Increment the spawns created.
            spawnCount.set(spawnCount.get() + 1);
        }
    }

    public boolean isReadyToSpawnFacehuggers() {
        return remainingHatchDurationInTicks.get() <= 0;
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
        spawnCount.reset();
        remainingHatchDurationInTicks.reset();
        remainingSpawnDelayInTicks.reset();
        ovomorph.setHatchState(Ovomorph.DEFAULT_HATCH_STATE);
    }

    public HatchDesireManager getHatchDesireManager() {
        return hatchDesireManager;
    }

    private void spawnFacehugger(Level level) {
        var facehuggerType = Facehugger.getType(ovomorph.getVariant(), ovomorph.isRoyal());

        if (facehuggerType == null) {
            AVP.LOGGER.warn(
                "Failed to get a facehugger type for an ovomorph entity Ovomorph Variant: {}, IsRoyal: {}.",
                ovomorph.getVariant(),
                ovomorph.isRoyal()
            );
            return;
        }

        var facehugger = facehuggerType.create(level);

        if (facehugger == null) {
            AVP.LOGGER.warn("Failed to create facehugger entity.");
            return;
        }

        ovomorph.getGeneManager()
            .getGeneContainer()
            .transfer(facehugger.getGeneManager().getGeneContainer(), false);

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
