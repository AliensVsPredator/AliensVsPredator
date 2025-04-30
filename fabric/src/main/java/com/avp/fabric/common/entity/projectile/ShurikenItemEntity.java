package com.avp.fabric.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import com.avp.fabric.common.entity.type.AVPEntityTypes;
import com.avp.fabric.common.item.AVPItems;
import com.avp.fabric.server.BlockBreakProgressManager;

public class ShurikenItemEntity extends ThrowableItemProjectile {

    public ShurikenItemEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ShurikenItemEntity(Level level, LivingEntity livingEntity) {
        super(AVPEntityTypes.SHURIKEN, livingEntity, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return AVPItems.SHURIKEN;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > 300)
            this.kill();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        if (!this.level().isClientSide) {
            BlockBreakProgressManager.damage(
                level(),
                result.getBlockPos(),
                2.0F
            );
            this.discard();
        }
        super.onHitBlock(result);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (result.getEntity() instanceof LivingEntity livingEntity && getOwner() != null)
            livingEntity.hurt(damageSources().thrown(getOwner(), livingEntity), 5.0F);
        super.onHitEntity(result);
    }
}
