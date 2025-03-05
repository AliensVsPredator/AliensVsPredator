package com.avp.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.explosion.Explosion;
import com.avp.common.util.ExplosionUtil;

public class Rocket extends ThrowableProjectile {

    private static final String TICK_COUNT_KEY = "TickCount";

    public Rocket(EntityType<? extends Rocket> entityType, Level level) {
        super(entityType, level);
    }

    public Rocket(Level level, LivingEntity livingEntity) {
        super(AVPEntityTypes.ROCKET, livingEntity, level);
    }

    @Override
    public void tick() {
        var hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        if (hitResult.getType() != HitResult.Type.MISS) {
            hitTargetOrDeflectSelf(hitResult);
        }

        checkInsideBlocks();
        var vec3 = getDeltaMovement();
        var d = getX() + vec3.x;
        var e = getY() + vec3.y;
        var f = getZ() + vec3.z;

        updateRotation();

        if (isInWater()) {
            for (int i = 0; i < 4; i++) {
                level()
                    .addParticle(ParticleTypes.BUBBLE, d - vec3.x * 0.25, e - vec3.y * 0.25, f - vec3.z * 0.25, vec3.x, vec3.y, vec3.z);
            }
        }

        setPos(d, e, f);

        if (tickCount >= 20 * 15) {
            remove(Entity.RemovalReason.DISCARDED);
        }

        var level = level();

        if (level.isClientSide) {
            var x = getX() + (random.nextDouble()) * getBbWidth() * 0.5D;
            var y = getZ() + (random.nextDouble()) * getBbWidth() * 0.5D;
            level.addParticle(ParticleTypes.SMOKE, true, x, getY(0.8), y, 0, 0, 0);
            level.addParticle(ParticleTypes.FLAME, true, x, getY(0.8), y, 0, 0, 0);
        }
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        var level = level();

        if (!level.isClientSide) {
            explode((ServerLevel) level);
            discard();
        }
    }

    private void explode(ServerLevel level) {
        var center = position();
        var x = center.x;
        var y = center.y;
        var z = center.z;
        var radius = 5;
        var maxKnockback = 2;
        var minDamage = 10;
        var maxDamage = 40;

        var explosion = Explosion.builder(level, center)
            .withRadius(radius)
            .onBlockSample(($, pos) -> {
                var blockState = level.getBlockState(pos);

                if (!doesBlockSurviveExplosion(center, blockState, pos, 10F)) {
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            })
            .onCycleFinish(sampledBlockPositions -> {

                var entities = ExplosionUtil.getEntitiesInRadius(level, center, radius);

                entities.forEach(entity -> {
                    var distance = entity.distanceToSqr(center);
                    var damage = ExplosionUtil.computeDamage(radius, minDamage, maxDamage, distance);

                    entity.hurt(level.damageSources().explosion(null), (float) damage);
                    ExplosionUtil.applyKnockback(center, radius, entity, maxKnockback, distance);
                });

                for (var serverPlayer : level.players()) {
                    if (serverPlayer.distanceToSqr(x, y, z) >= 64 * 64) {
                        continue;
                    }

                    serverPlayer.connection
                        .send(
                            new ClientboundExplodePacket(
                                x,
                                y,
                                z,
                                5F,
                                sampledBlockPositions,
                                Vec3.ZERO,
                                net.minecraft.world.level.Explosion.BlockInteraction.DESTROY_WITH_DECAY,
                                ParticleTypes.EXPLOSION,
                                ParticleTypes.EXPLOSION_EMITTER,
                                SoundEvents.GENERIC_EXPLODE
                            )
                        );
                }
            })
            .onExplosionFinish(() -> playSound(SoundEvents.GENERIC_EXPLODE.value(), 1F, 1F))
            .build();

        explosion.explode();
    }

    public static boolean doesBlockSurviveExplosion(
        Vec3 explosionCenter,
        BlockState blockState,
        BlockPos blockPos,
        float explosionPower
    ) {
        var resistance = blockState.getBlock().getExplosionResistance();
        var distance = explosionCenter.distanceTo(blockPos.getBottomCenter());

        // Simplified blast force calculation (Minecraft uses a more complex method)
        var attenuationFactor = 0.5F; // Adjust this to fit Minecraft's attenuation model
        var appliedForce = explosionPower - (float) (distance * attenuationFactor);

        return appliedForce < resistance;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { /* NO-OP */ }

    @Override
    protected double getDefaultGravity() {
        return 0F;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        this.tickCount = tag.getShort(TICK_COUNT_KEY);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putShort(TICK_COUNT_KEY, (short) tickCount);
    }
}
