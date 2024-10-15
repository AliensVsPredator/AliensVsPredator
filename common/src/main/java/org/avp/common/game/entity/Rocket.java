package org.avp.common.game.entity;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import org.avp.common.data.entity.RocketData;

public class Rocket extends ThrowableProjectile implements GeoEntity {

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public Rocket(EntityType<? extends Rocket> entityType, Level level) {
        super(entityType, level);
    }

    public Rocket(Level level, LivingEntity livingEntity) {
        super(RocketData.INSTANCE.getHolder().get(), livingEntity, level);
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        var level = level();

        if (!level.isClientSide) {
            level.explode(this, getX(), getY(), getZ(), 5F, false, Level.ExplosionInteraction.BLOCK);
            discard();
        }
    }

    @Override
    public void tick() {
        this.baseTick();
        var deltaMovement = this.getDeltaMovement();
        var nextX = this.getX() + deltaMovement.x;
        var nextY = this.getY() + deltaMovement.y;
        var nextZ = this.getZ() + deltaMovement.z;
        var hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        boolean portalOrGatewayHit = false;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            var blockPos = ((BlockHitResult) hitResult).getBlockPos();
            var blockState = this.level().getBlockState(blockPos);
            if (blockState.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal(blockPos);
                portalOrGatewayHit = true;
            } else if (blockState.is(Blocks.END_GATEWAY)) {
                if (this.level().getBlockEntity(blockPos) instanceof TheEndGatewayBlockEntity theEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this))
                    TheEndGatewayBlockEntity.teleportEntity(this.level(), blockPos, blockState, this, theEndGatewayBlockEntity);
                portalOrGatewayHit = true;
            }
        }
        if (hitResult.getType() != HitResult.Type.MISS && !portalOrGatewayHit)
            this.onHit(hitResult);
        this.checkInsideBlocks();
        this.updateRotation();
        this.setDeltaMovement(deltaMovement.scale(this.isInWater() ? 0.8F: 0.99F));
        if (!this.isNoGravity())
            this.setDeltaMovement(deltaMovement.x, deltaMovement.y - this.getGravity(), deltaMovement.z);
        this.setPos(nextX, nextY, nextZ);
        if (this.level().isClientSide() && this.isInWater())
            for (int k = 0; k < 4; ++k)
                this.level().addParticle(ParticleTypes.BUBBLE, nextX - deltaMovement.x * 0.25, nextY - deltaMovement.y * 0.25, nextZ - deltaMovement.z * 0.25, deltaMovement.x, deltaMovement.y, deltaMovement.z);
    }

    @Override
    protected void defineSynchedData() { /* NO-OP */ }

    @Override
    protected float getGravity() {
        return 0F;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        // TODO:
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
