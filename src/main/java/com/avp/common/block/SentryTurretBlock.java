package com.avp.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import com.avp.common.entity.type.AVPEntityTypes;

public class SentryTurretBlock extends Block {

    protected SentryTurretBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            super.setPlacedBy(level, pos, state, placer, stack);
            return;
        }

        var sentryTurret = AVPEntityTypes.SENTRY_TURRET.create(serverLevel);

        if (sentryTurret == null) {
            return;
        }

        sentryTurret.setPos(pos.getCenter());

        if (placer != null) {
            sentryTurret.setOwner(placer);

            // Orient the turret opposite of where the player is looking.
            var placerYaw = placer.getYRot();
            var turretYaw = (placerYaw + 180.0F) % 360.0F;

            sentryTurret.setYRot(turretYaw);
            sentryTurret.setYHeadRot(turretYaw);
            sentryTurret.setYBodyRot(turretYaw);
        }

        serverLevel.addFreshEntity(sentryTurret);
        level.removeBlock(pos, false);
    }

}
