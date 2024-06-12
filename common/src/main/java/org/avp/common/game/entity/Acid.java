package org.avp.common.game.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.avp.api.util.BLPredicates;
import org.avp.api.util.phys.GravityUtil;
import org.avp.client.render.particle.AVPParticleTypeRegistry;
import org.avp.common.game.sound.AVPSoundEventRegistry;
import org.jetbrains.annotations.NotNull;

import org.avp.common.config.AVPConfig;
import org.avp.common.game.damage.AVPDamageSources;
import org.avp.common.data.tag.AVPBlockTags;
import org.avp.common.data.tag.AVPEntityTypeTags;
import org.avp.common.data.tag.AVPItemTags;
import org.avp.api.server.BlockBreakProgressManager;

public class Acid extends Entity {

    private static final int DEFAULT_MAX_LIFE_IN_TICKS = 20 * 20; // 10 seconds.

    private static final int MAX_MULTIPLIER = 10;

    private static final int MIN_TICKS_UNTIL_PARTICLES = 5;

    private static final String MULTIPLIER_KEY = "Multiplier";

    private static final String TICK_COUNT_FOR_CURRENT_MULTIPLIER = "TickCountForMultiplier";

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
    protected void defineSynchedData() {
        entityData.define(MULTIPLIER, 1);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        setMultiplier(compoundTag.getInt(MULTIPLIER_KEY));
        tickCountForCurrentMultiplier = compoundTag.getInt(TICK_COUNT_FOR_CURRENT_MULTIPLIER);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        compoundTag.putInt(MULTIPLIER_KEY, getMultiplier());
        compoundTag.putInt(TICK_COUNT_FOR_CURRENT_MULTIPLIER, tickCountForCurrentMultiplier);
    }

    @Override
    public void tick() {
        super.tick();

        var level = level();

        applyGravity(level);

        damageBlock(level);
        damageEntities(level);

        particleTickCounter += getMultiplier();

        createParticlesAndSounds(level);


        if (!level.isClientSide) {
            // Acid disappears twice as fast when in water.
            tickCountForCurrentMultiplier += getMultiplier() * (isInWater() ? 2 : 1);

            if (tickCountForCurrentMultiplier > DEFAULT_MAX_LIFE_IN_TICKS) {
                decreaseMultiplier();
                tickCountForCurrentMultiplier = 0;

                if (getMultiplier() == 0) {
                    kill();
                }
            }
        }
    }

    public void decreaseMultiplier() {
        this.setMultiplier(this.getMultiplier() - 1);
    }

    private void damageBlock(Level level) {
        if (level.isClientSide || isInWater() || !onGround())
            return;

        var blockPos = blockPosition();
        var blockState = level.getBlockState(blockPos);

        if (blockState.getBlock() == Blocks.AIR) {
            blockPos = blockPos.below();
            blockState = level.getBlockState(blockPos);
        }

        if (!blockState.is(AVPBlockTags.ACID_IMMUNE)) {
            // TODO: Make this break speed configurable.
            BlockBreakProgressManager.damage(level(), blockPos, (2F * getMultiplier()) / 20F);
        }
    }

    private void createParticlesAndSounds(Level level) {
        // Both particles and fizzing sounds should only play client-side.
        if (!level.isClientSide)
            return;

        if (tickCount % (random.nextInt(100) + 10) == 0) {
            level.playLocalSound(this, AVPSoundEventRegistry.INSTANCE.blockAcidBurn.get(), SoundSource.NEUTRAL, 1F, 1F);
        }

        if (particleTickCounter < MIN_TICKS_UNTIL_PARTICLES) {
            return;
        }

        particleTickCounter = 0;

        for (int i = 0; i < getMultiplier(); i++) {
            level.addAlwaysVisibleParticle(ParticleTypes.SMOKE, getRandomX(0.5), getRandomY(), getRandomZ(0.5), 0, 0, 0);
            level.addAlwaysVisibleParticle(
                AVPParticleTypeRegistry.INSTANCE.acid.get(),
                getRandomX(0.5),
                getRandomY(),
                getRandomZ(0.5),
                0,
                0,
                0
            );
        }
    }

    private void applyGravity(Level level) {
        // Gravity should only be applied server-side.
        if (level.isClientSide)
            return;

        GravityUtil.apply(this);
    }

    private void damageEntities(Level level) {
        // Entity damage should only be done server-side.
        if (level.isClientSide)
            return;

        var entities = level.getEntities(
            this,
            getBoundingBox(),
            entity -> BLPredicates.IS_LIVING.test(entity) || entity instanceof Acid
        );
        entities.forEach(this::damageEntity);
    }

    private void damageEntity(Entity entity) {
        if (entity instanceof Acid otherAcid) {
            if (otherAcid.isAlive()) {
                otherAcid.remove(RemovalReason.DISCARDED);
                setMultiplier(getMultiplier() + otherAcid.getMultiplier());
            }
            return;
        }

        if (isInWater() || tickCount % 10 != 0) {
            return;
        }

        // Ignore immortal players.
        if (entity instanceof Player player && BLPredicates.IS_IMMORTAL.test(player)) {
            return;
        }

        if (entity instanceof LivingEntity livingEntity) {
            var itemStack = livingEntity.getItemBySlot(EquipmentSlot.FEET);

            if (itemStack.is(AVPItemTags.ACID_IMMUNE)) {
                return;
            } else if (!itemStack.isEmpty()) {
                // Damage feet item if present.
                var damage = (random.nextInt(3) + 3) * getMultiplier();
                itemStack.setDamageValue(itemStack.getDamageValue() + damage);
                if (itemStack.getDamageValue() > itemStack.getMaxDamage()) {
                    itemStack.setCount(0);
                }
                return;
            }
        }

        if (!entity.getType().is(AVPEntityTypeTags.ACID_IMMUNE)) {
            AVPDamageSources.INSTANCE.acid.get().hurt(entity, AVPConfig.General.ACID_DAMAGE);
        }
    }

    private int getMultiplier() {
        return entityData.get(MULTIPLIER);
    }

    public void setMultiplier(int multiplier) {
        entityData.set(MULTIPLIER, Mth.clamp(multiplier, 0 ,MAX_MULTIPLIER));
        tickCountForCurrentMultiplier = 0;
        refreshDimensions();
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> entityDataAccessor) {
        if (MULTIPLIER.equals(entityDataAccessor)) {
            refreshDimensions();
        }

        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        var originalDimensions = super.getDimensions(pose);
        var originalWidth = originalDimensions.width;
        var maxScale = 1F / originalWidth;
        var scaleStep = Mth.map(getMultiplier(), 0, MAX_MULTIPLIER, 1F, maxScale);
        return originalDimensions.scale(scaleStep, 1);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }
}
