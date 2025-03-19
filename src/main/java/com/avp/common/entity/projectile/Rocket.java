package com.avp.common.entity.projectile;

import com.avp.AVP;
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
        var explosionInteraction = AVP.config.weaponConfigs.BULLETS_DAMAGE_BLOCKS_ENABLED ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE;

        if (!level.isClientSide) {
            level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 5.0F, false, explosionInteraction);
            discard();
        }
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
