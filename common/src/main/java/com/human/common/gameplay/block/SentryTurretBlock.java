package com.human.common.gameplay.block;

import com.human.common.registry.init.entity_type.HumanEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.common.gameplay.block.property.BlockProperties;

public class SentryTurretBlock extends Block {

    public SentryTurretBlock() {
        super(BlockProperties.STEEL.build().noOcclusion());
    }

    @Override
    public void setPlacedBy(
        Level level,
        @NotNull BlockPos pos,
        @NotNull BlockState state,
        @Nullable LivingEntity placer,
        @NotNull ItemStack stack
    ) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            super.setPlacedBy(level, pos, state, placer, stack);
            return;
        }

        var sentryTurret = HumanEntityTypes.SENTRY_TURRET.get().create(serverLevel);

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
