package com.avp.common.entity.projectile;

import com.avp.common.entity.type.AVPEntityTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class BulletProjectile extends AbstractHurtingProjectile {

    public BulletProjectile(EntityType<BulletProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public BulletProjectile(Level level) {
        super(AVPEntityTypes.BULLET, level);
    }

    public BulletProjectile(Level level, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(AVPEntityTypes.BULLET, accelX, accelY, accelZ, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { /* NO-OP */ }

    @Override
    public void tick() {
        super.tick();
        if (tickCount >= 80) remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        var level = level();

        if (!level.isClientSide) {
            if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() != getOwner()) {
                entityHitResult.getEntity().hurt(level.damageSources().thrown(this, getOwner()), 2.0F);
            }
            discard();
        }
    }
}
