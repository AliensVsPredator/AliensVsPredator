package org.avp.common.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import org.avp.client.render.particle.AVPParticleTypes;
import org.avp.common.config.AVPConfig;
import org.avp.common.damage.AVPDamageSources;
import org.avp.common.tag.AVPBlockTags;
import org.avp.common.tag.AVPEntityTags;
import org.avp.common.tag.AVPItemTags;
import org.avp.common.util.AVPPredicates;
import org.avp.server.BlockBreakProgressManager;

public class Acid extends Entity {

    private static final int MAX_LIFE_IN_TICKS = 20 * 20; // 20 seconds.

    private static final String MULTIPLIER_KEY = "multiplier";

    private static final EntityDataAccessor<Integer> MULTIPLIER = SynchedEntityData.defineId(
        Acid.class,
        EntityDataSerializers.INT
    );

    public Acid(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(false);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(MULTIPLIER, 1);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        this.setMultiplier(compoundTag.getInt(MULTIPLIER_KEY));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        compoundTag.putInt(MULTIPLIER_KEY, this.getMultiplier());
    }

    @Override
    public void tick() {
        super.tick();

        var level = level();

        applyGravity(level);
        damageBlock(level);
        damageEntities(level);
        createParticlesAndSounds(level);

        if (tickCount > MAX_LIFE_IN_TICKS * this.getMultiplier()) {
            this.kill();
        }
    }

    private void damageBlock(Level level) {
        if (level.isClientSide || tickCount % 20 != 0)
            return;

        var below = blockPosition().below();
        var blockState = level.getBlockState(below);

        if (!blockState.is(AVPBlockTags.ACID_IMMUNE)) {
            // TODO: Make this break speed configurable.
            BlockBreakProgressManager.damage(level(), below, 2F * this.getMultiplier());
        }
    }

    private void createParticlesAndSounds(Level level) {
        // Both particles and fizzing sounds should only play client-side.
        if (!level.isClientSide)
            return;

        if (tickCount % (random.nextInt(100) + 10) == 0) {
            level.playLocalSound(this, SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 1F, 1F);
        }

        for (int i = 0; i < 2 * this.getMultiplier(); i++) {
            level.addAlwaysVisibleParticle(ParticleTypes.SMOKE, getRandomX(0.5), getRandomY(), getRandomZ(0.5), 0.0, 0.0, 0.0);
            level.addAlwaysVisibleParticle(
                AVPParticleTypes.INSTANCE.acid.get(),
                getRandomX(0.5),
                getRandomY(),
                getRandomZ(0.5),
                0.0,
                0.0,
                0.0
            );
        }
    }

    private void applyGravity(Level level) {
        // Gravity should only be applied server-side.
        if (level.isClientSide)
            return;

        this.setDeltaMovement(0, this.getDeltaMovement().y - 0.03999999910593033D, 0);
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(0, this.getDeltaMovement().y * 0.9800000190734863D, 0);
    }

    private void damageEntities(Level level) {
        // Entity damage should only be done server-side.
        if (level.isClientSide || tickCount % 10 != 0)
            return;

        var entities = level.getEntities(this, this.getBoundingBox(), entity -> AVPPredicates.IS_LIVING.test(entity) || entity instanceof Acid);
        entities.forEach(entity -> {
            // Ignore immortal players.
            if (entity instanceof Player player && AVPPredicates.IS_IMMORTAL.test(player)) {
                return;
            }

            if (entity instanceof LivingEntity livingEntity) {
                var itemStack = livingEntity.getItemBySlot(EquipmentSlot.FEET);

                if (itemStack.is(AVPItemTags.ACID_IMMUNE)) {
                    return;
                } else if (!itemStack.isEmpty()) {
                    // Damage feet item if present.
                    var damage = (random.nextInt(3) + 3) * this.getMultiplier();
                    itemStack.setDamageValue(itemStack.getDamageValue() + damage);
                    if (itemStack.getDamageValue() > itemStack.getMaxDamage()) {
                        itemStack.setCount(0);
                    }
                    return;
                }
            }

            if (entity instanceof Acid) {
                if (entity.isAlive()) {
                    entity.remove(RemovalReason.DISCARDED);
                    this.setMultiplier(this.getMultiplier() + 1);
                }
                return;
            }

            if (!entity.getType().is(AVPEntityTags.ACID_IMMUNE)) {
                AVPDamageSources.INSTANCE.acid.get().hurt(entity, AVPConfig.General.ACID_DAMAGE);
            }
        });
    }

    private int getMultiplier() {
        return this.entityData.get(MULTIPLIER);
    }

    private void setMultiplier(int multiplier) {
        this.entityData.set(MULTIPLIER, multiplier);
    }
}
