package com.avp.core.common.entity.acid;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import com.avp.core.common.particle.AVPParticleTypes;
import com.avp.core.common.util.GravityUtil;

public class Acid extends Entity {

    public static final int MAX_MULTIPLIER = 5;

    private static final int DEFAULT_MAX_LIFE_IN_TICKS = 20 * 20; // 10 seconds.

    private static final int MIN_TICKS_UNTIL_PARTICLES = 5;

    private static final String IS_NETHER_AFFLICTED_KEY = "isNetherAfflicted";

    private static final String MULTIPLIER_KEY = "Multiplier";

    private static final String TICK_COUNT_FOR_CURRENT_MULTIPLIER = "TickCountForMultiplier";

    private static final EntityDataAccessor<Boolean> IS_NETHER_AFFLICTED = SynchedEntityData.defineId(
        Acid.class,
        EntityDataSerializers.BOOLEAN
    );

    private static final EntityDataAccessor<Integer> MULTIPLIER = SynchedEntityData.defineId(
        Acid.class,
        EntityDataSerializers.INT
    );

    private int particleTickCounter = 0;

    private int tickCountForCurrentMultiplier = 0;

    public Acid(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
        setNoGravity(false);
        refreshDimensions();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(MULTIPLIER, 1);
        builder.define(IS_NETHER_AFFLICTED, false);
    }

    @Override
    public void tick() {
        super.tick();

        var level = level();

        if (!level.isClientSide && !horizontalCollision) {
            GravityUtil.apply(this);
        }

        AcidBlockDamageUtil.damageBlocks(this);
        AcidEntityDamageUtil.damageEntities(this);

        particleTickCounter += getMultiplier();

        createParticlesAndSounds(level);

        if (!level.isClientSide) {
            // Acid disappears twice as fast when in water.
            tickCountForCurrentMultiplier += getMultiplier() * (isInWater() ? 2 : 1);

            if (tickCountForCurrentMultiplier > DEFAULT_MAX_LIFE_IN_TICKS) {
                decreaseMultiplier();
                tickCountForCurrentMultiplier = 0;
            }

            if (getMultiplier() == 0) {
                kill();
            }
        }
    }

    public boolean isNetherAfflicted() {
        return entityData.get(IS_NETHER_AFFLICTED);
    }

    public void setNetherAfflicted(boolean isNetherAfflicted) {
        entityData.set(IS_NETHER_AFFLICTED, isNetherAfflicted);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        if (compoundTag.contains(MULTIPLIER_KEY)) {
            setMultiplier(compoundTag.getInt(MULTIPLIER_KEY));
        }

        tickCountForCurrentMultiplier = compoundTag.getInt(TICK_COUNT_FOR_CURRENT_MULTIPLIER);

        setNetherAfflicted(compoundTag.getBoolean(IS_NETHER_AFFLICTED_KEY));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        compoundTag.putInt(MULTIPLIER_KEY, getMultiplier());
        compoundTag.putInt(TICK_COUNT_FOR_CURRENT_MULTIPLIER, tickCountForCurrentMultiplier);
        compoundTag.putBoolean(IS_NETHER_AFFLICTED_KEY, isNetherAfflicted());
    }

    public void decreaseMultiplier() {
        this.setMultiplier(this.getMultiplier() - 1);
    }

    private void createParticlesAndSounds(Level level) {
        // Both particles and fizzing sounds should only play client-side.
        if (!level.isClientSide)
            return;

        if (particleTickCounter < MIN_TICKS_UNTIL_PARTICLES) {
            return;
        }

        particleTickCounter = 0;

        for (int i = 0; i < getMultiplier(); i++) {
            if (isInWater()) {
                level.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, getRandomX(0.5), getRandomY(), getRandomZ(0.5), 0, 0, 0);
            }

            level.addAlwaysVisibleParticle(
                (isNetherAfflicted() ? AVPParticleTypes.BLUE_ACID : AVPParticleTypes.ACID).get(),
                getRandomX(0.5),
                getRandomY(),
                getRandomZ(0.5),
                0,
                0,
                0
            );
        }
    }

    public int getMultiplier() {
        return entityData.get(MULTIPLIER);
    }

    public void setMultiplier(int multiplier) {
        entityData.set(MULTIPLIER, Mth.clamp(multiplier, 0, MAX_MULTIPLIER));
        tickCountForCurrentMultiplier = 0;
        refreshDimensions();
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);

        if (MULTIPLIER.equals(entityDataAccessor)) {
            refreshDimensions();
        }
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        var originalDimensions = super.getDimensions(pose);
        var originalWidth = originalDimensions.width();
        var maxScale = 1F / originalWidth;
        var scaleStep = Mth.map(getMultiplier(), 0, MAX_MULTIPLIER, 1F, maxScale);
        return originalDimensions.scale(scaleStep, 1);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    public void age() {
        tickCountForCurrentMultiplier += getMultiplier();
    }
}
