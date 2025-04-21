package com.avp.common.block;

import com.avp.common.entity.type.AVPEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SentryTurretBlock extends Block {

    protected SentryTurretBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            var sentryTurret = AVPEntityTypes.SENTRY_TURRET.create(serverLevel);
            if (sentryTurret != null) {
                sentryTurret.setPos(pos.getCenter());
                if (placer != null) {
                    sentryTurret.setOwner(placer);
                }
                serverLevel.addFreshEntity(sentryTurret);
                level.removeBlock(pos, false);
            }
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

}
