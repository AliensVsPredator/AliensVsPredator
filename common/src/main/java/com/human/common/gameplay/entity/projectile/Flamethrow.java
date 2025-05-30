package com.human.common.gameplay.entity.projectile;

import com.alien.common.data.AlienVariantTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import java.util.ArrayDeque;
import java.util.HashSet;

import com.avp.common.registry.init.entity_type.AVPEntityTypes;
import com.avp.common.registry.key.AVPDamageTypeKeys;

public class Flamethrow extends ThrowableProjectile {

    private static final String IS_ENHANCED_ENHANCED = "IsEnhanced";

    private static final String TICK_COUNT_KEY = "TickCount";

    private boolean isEnhanced;

    public Flamethrow(EntityType<? extends Flamethrow> entityType, Level level) {
        super(entityType, level);
        this.isEnhanced = false;
    }

    public Flamethrow(Level level, LivingEntity livingEntity) {
        super(AVPEntityTypes.FLAMETHROW.get(), livingEntity, level);
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
                case BlockHitResult blockHitResult -> {
                    var hit = blockHitResult.getBlockPos();
                    // Direction the fireball came from.
                    var impactSide = blockHitResult.getDirection();

                    // Start fire on the "outside" of the block that was hit.
                    var airStart = hit.relative(impactSide);

                    firebomb(airStart);
                }
                default -> { /* NO-OP */ }
            }

            discard();
        }
    }

    private void firebomb(BlockPos blockPos) {
        var radius = isEnhanced ? 2 : 1;
        var registry = registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        var damageSource = new DamageSource(registry.getHolderOrThrow(AVPDamageTypeKeys.FLAMETHROW), getOwner());
        var bottomCorner = blockPos.offset(-radius, -radius, -radius);
        var topCorner = blockPos.offset(radius, radius, radius);
        var entitiesToHurt = level().getEntities(this, AABB.encapsulatingFullBlocks(bottomCorner, topCorner));

        entitiesToHurt.forEach(entity -> {
            entity.hurt(damageSource, 0.01F);
            entity.igniteForTicks(10 * 20);
        });

        bfsFireSpread(blockPos, radius);
    }

    public void bfsFireSpread(BlockPos origin, int maxRadius) {
        var visited = new HashSet<BlockPos>();
        var queue = new ArrayDeque<BlockPos>();

        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty()) {
            var current = queue.poll();

            if (shouldPlaceFireAt(current)) {
                level().setBlock(current, Blocks.FIRE.defaultBlockState(), 3);
            }

            for (var direction : Direction.values()) {
                var neighbor = current.relative(direction);

                if (visited.contains(neighbor)) {
                    continue;
                }

                if (origin.distManhattan(neighbor) > maxRadius + 1) {
                    continue;
                }

                if (shouldPlaceFireAt(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    }

    private boolean shouldPlaceFireAt(BlockPos pos) {
        var state = level().getBlockState(pos);
        var alienVariantType = AlienVariantTypes.getForOrNull(state);

        if (alienVariantType == AlienVariantTypes.NETHER) {
            // If the state being replaced is a nether variant type (nether resin), then don't replace it with fire.
            return false;
        }

        return state.canBeReplaced() && state.getFluidState().isEmpty();
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) { /* NO-OP */ }

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
