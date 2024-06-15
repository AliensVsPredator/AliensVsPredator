package org.avp.common.game.entity;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
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
    protected void defineSynchedData() { /* NO-OP */ }

    @Override
    protected float getGravity() {
        return 0F;
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
