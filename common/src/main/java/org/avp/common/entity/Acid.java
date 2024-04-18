package org.avp.common.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.avp.client.render.particle.AVPParticleTypes;
import org.avp.common.damage.AVPDamageSources;
import org.avp.common.tag.AVPBlockTags;
import org.avp.common.tag.AVPEntityTags;
import org.avp.common.tag.AVPItemTags;
import org.avp.server.BlockBreakProgressManager;
import org.jetbrains.annotations.NotNull;

public class Acid extends Entity {

    private static final int MAX_LIFE_IN_TICKS = 20 * 20; // 20 seconds.

    public Acid(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(false);
    }

    @Override
    protected void defineSynchedData() { /* Do nothing */ }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) { /* Do nothing */ }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) { /* Do nothing */ }

    @Override
    public void tick() {
        super.tick();

        applyGravity();

        if (tickCount > MAX_LIFE_IN_TICKS) {
            this.kill();
        }

        var level = level();

        if (!level.isClientSide) {
            if (tickCount % 20 == 0) {
                var below = blockPosition().below();
                var blockState = level.getBlockState(below);

                if (!blockState.is(AVPBlockTags.ACID_IMMUNE)) {
                    // TODO: Make this break speed configurable.
                    BlockBreakProgressManager.damage(level(), below, 2F);
                }
            }

            if (tickCount % 10 == 0) {
                damageEntities(level);
            }
        }

        if (level.isClientSide)
        {
            if (tickCount % (random.nextInt(100) + 10) == 0) {
                level.playLocalSound(this, SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 1F, 1F);
            }
            for (int i = 0; i < 2; i++) {
                level.addAlwaysVisibleParticle(ParticleTypes.SMOKE, getRandomX(0.5), getRandomY(), getRandomZ(0.5), 0.0, 0.0, 0.0);
                level.addAlwaysVisibleParticle(AVPParticleTypes.INSTANCE.acid.get(), getRandomX(0.5), getRandomY(), getRandomZ(0.5), 0.0, 0.0, 0.0);
            }
        }
    }

    private void applyGravity() {
        this.setDeltaMovement(0, this.getDeltaMovement().y - 0.03999999910593033D, 0);
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(0, this.getDeltaMovement().y * 0.9800000190734863D, 0);
    }

    private void damageEntities(Level level) {
        var entities = level.getEntities(this, this.getBoundingBox(), LivingEntity.class::isInstance);
        entities.forEach(entity -> {
            if (entity instanceof Player player && player.isCreative()) {
                return;
            }

            if (entity instanceof LivingEntity livingEntity) {
                var itemStack = livingEntity.getItemBySlot(EquipmentSlot.FEET);

                if (itemStack.is(AVPItemTags.ACID_IMMUNE)) {
                    return;
                } else if (!itemStack.isEmpty()) {
                    itemStack.setDamageValue(itemStack.getDamageValue() + random.nextInt(3) + 3);
                    if (itemStack.getDamageValue() > itemStack.getMaxDamage()) {
                        itemStack.setCount(0);
                    }
                    return;
                }
            }

            if (!entity.getType().is(AVPEntityTags.ACID_IMMUNE)) {
                // TODO: Make damage amount configurable.
                AVPDamageSources.INSTANCE.acid.get().hurt(entity, 2F);
            }
        });
    }
}
