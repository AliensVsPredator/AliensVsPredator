package com.human.common.gameplay.entity.projectile;

import com.alien.common.data.AlienVariantTypes;
import com.human.common.registry.init.entity_type.HumanEntityTypes;
import com.just.core.traversal.BFS;
import com.lib.common.util.DirectionUtil;
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

import java.util.Arrays;

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
        super(HumanEntityTypes.FLAMETHROW.get(), livingEntity, level);
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
                    // The direction the fireball came from.
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

    private void firebomb(BlockPos originPos) {
        var radius = isEnhanced ? 2 : 1;
        var registry = registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        var damageSource = new DamageSource(registry.getHolderOrThrow(AVPDamageTypeKeys.FLAMETHROW), getOwner());
        var bottomCorner = originPos.offset(-radius, -radius, -radius);
        var topCorner = originPos.offset(radius, radius, radius);
        var entitiesToHurt = level().getEntities(this, AABB.encapsulatingFullBlocks(bottomCorner, topCorner));

        entitiesToHurt.forEach(entity -> {
            entity.hurt(damageSource, 1F);
            entity.igniteForTicks(10 * 20);
            entity.invulnerableTime = 0;
        });

        BFS.traverse(
            originPos,
            pos -> Arrays.stream(DirectionUtil.VALUES).map(pos::relative).filter(this::shouldPlaceFireAt).toList(),
            pos -> level().setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState()),
            pos -> originPos.distManhattan(pos) > radius + 1
        );
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
