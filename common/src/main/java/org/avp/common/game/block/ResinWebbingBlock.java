package org.avp.common.game.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.avp.api.util.BLPredicates;
import org.avp.common.data.tag.AVPEntityTypeTags;
import org.jetbrains.annotations.NotNull;

public class ResinWebbingBlock extends Block {
    private int standingTick = 0;

    public ResinWebbingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (entity.getType().is(AVPEntityTypeTags.ALIENS)) return;
        if (entity instanceof LivingEntity livingEntity && BLPredicates.IS_IMMORTAL.test(livingEntity)) return;
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.makeStuckInBlock(state, new Vec3(0.25, 0.05F, 0.25));
            if (!world.isClientSide())
                standingTick++;
            if (standingTick >= 100) {
                if (!world.getBlockState(pos.below()).is(this))
                    livingEntity.setPos(pos.getCenter().x, pos.getY(), pos.getCenter().z);
                if (world.getBlockState(pos.below()).is(this))
                    livingEntity.setPos(pos.getCenter().x, pos.below().getY(), pos.getCenter().z);
                livingEntity.makeStuckInBlock(state, new Vec3(0.25, 0.0F, 0.25));
                standingTick = 0;
            }
        } else {
            standingTick = 0;
        }
    }
}
