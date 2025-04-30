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
import com.avp.fabric.common.util.ItemGoalUtil;
import com.avp.server.BlockBreakProgressManager;

public class SmartDiscItemEntity extends ThrowableItemProjectile {

    private boolean dealtDamage;

    public SmartDiscItemEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public SmartDiscItemEntity(Level level, LivingEntity livingEntity) {
        super(AVPEntityTypes.SMART_DISC, livingEntity, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return AVPItems.SMART_DISC;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getOwner() == null)
            this.kill();
        if (this.tickCount > 300)
            this.kill();
        if (!this.dealtDamage) {
            ItemGoalUtil.trackToLivingEntity(this, 0.5, false);
        } else {
            ItemGoalUtil.trackToOwnerEntity(this);
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        if (!this.level().isClientSide) {
            BlockBreakProgressManager.damage(
                level(),
                result.getBlockPos(),
                2.0F
            );
            this.dealtDamage = true;
        }
        super.onHitBlock(result);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (result.getEntity() instanceof LivingEntity livingEntity && getOwner() != null && livingEntity != getOwner()) {
            livingEntity.hurt(
                damageSources().thrown(getOwner(), livingEntity),
                5.0F
            );
            this.dealtDamage = true;
        }
        super.onHitEntity(result);
    }
}
