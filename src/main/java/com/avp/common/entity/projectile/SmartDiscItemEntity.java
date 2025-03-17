package com.avp.common.entity.projectile;

import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.item.AVPItems;
import com.avp.common.util.ItemGoalUtil;
import com.avp.server.BlockBreakProgressManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

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
        if (this.tickCount > 300)
            this.kill();
        if (!this.dealtDamage)
            ItemGoalUtil.trackToLivingEntity(this, 0.5, false);
        if (this.dealtDamage && this.getOwner() != null) {
            var vec3 = this.getOwner().getEyePosition().subtract(this.position());
            this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015, this.getZ());
            if (this.level().isClientSide)
                this.yOld = this.getY();
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(0.5)));
            if (this.getOwner() instanceof Player player && this.getBoundingBox().intersects(this.getOwner().getBoundingBox())) {
                player.getInventory().add(AVPItems.SMART_DISC.getDefaultInstance());
                this.kill();
            } else if (this.getBoundingBox().intersects(this.getOwner().getBoundingBox()))
                this.kill();
        }
        if (this.getOwner() == null)
            this.kill();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        if (this.level().isClientSide) {
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