package com.avp.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import com.avp.common.damage.AVPDamageTypes;
import com.avp.common.entity.type.AVPEntityTypes;

public class Flamethrow extends ThrowableProjectile {

    private static final String IS_ENHANCED_ENHANCED = "IsEnhanced";

    private static final String TICK_COUNT_KEY = "TickCount";

    private boolean isEnhanced;

    public Flamethrow(EntityType<? extends Flamethrow> entityType, Level level) {
        super(entityType, level);
        this.isEnhanced = false;
    }

    public Flamethrow(Level level, LivingEntity livingEntity) {
        super(AVPEntityTypes.FLAMETHROW, livingEntity, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (isInWater() || tickCount >= 20 * 15) {
            remove(RemovalReason.DISCARDED);
            return;
        }

        var level = level();

        if (level.isClientSide) {
            for (int i = 0; i < 2; i++) {
                var x = getX() + (random.nextDouble()) * getBbWidth() * 0.5D;
                var y = getZ() + (random.nextDouble()) * getBbWidth() * 0.5D;
                level.addParticle(ParticleTypes.SMOKE, true, x, getY(0.8), y, 0, 0, 0);
                level.addParticle(ParticleTypes.FLAME, true, x, getY(0.8), y, 0, 0, 0);
            }
        }
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        var level = level();

        if (!level.isClientSide) {
            switch (hitResult) {
                case EntityHitResult entityHitResult -> firebomb(entityHitResult.getEntity().blockPosition());
                case BlockHitResult blockHitResult -> firebomb(blockHitResult.getBlockPos());
                default -> { /* NO-OP */ }
            }

            discard();
        }
    }

    private void firebomb(BlockPos blockPos) {
        var radius = isEnhanced ? 2 : 1;
        var registry = registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        var damageSource = new DamageSource(registry.getHolderOrThrow(AVPDamageTypes.FLAMETHROW), getOwner());
        var bottomCorner = blockPos.offset(-radius, -radius, -radius);
        var topCorner = blockPos.offset(radius, radius, radius);
        var entitiesToHurt = level().getEntities(this, AABB.encapsulatingFullBlocks(bottomCorner, topCorner));

        entitiesToHurt.forEach(entity -> {
            entity.hurt(damageSource, 0.01F);
            entity.igniteForTicks(10 * 20);
        });

        for (var x = -radius; x <= radius; x++) {
            for (var y = -radius; y <= radius; y++) {
                for (var z = -radius; z <= radius; z++) {
                    var firePos = blockPos.offset(x, y, z);
                    var state = level().getBlockState(firePos);

                    if (state.canBeReplaced() && state.getFluidState().isEmpty()) {
                        level().setBlock(firePos, Blocks.FIRE.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { /* NO-OP */ }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        this.isEnhanced = tag.getBoolean(IS_ENHANCED_ENHANCED);
        this.tickCount = tag.getShort(TICK_COUNT_KEY);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean(IS_ENHANCED_ENHANCED, isEnhanced);
        tag.putShort(TICK_COUNT_KEY, (short) tickCount);
    }

    public void setEnhanced(boolean enhanced) {
        isEnhanced = enhanced;
    }
}
