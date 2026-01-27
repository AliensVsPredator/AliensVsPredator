package com.blib.mod.common.gameplay.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.blib.mod.common.gameplay.block.TickingLightBlock;
import com.blib.mod.common.registry.init.BLibBlockEntityTypes;

public class TickingLightBlockEntity extends BlockEntity {

    private int lifespan = 0;

    public TickingLightBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BLibBlockEntityTypes.TICKING_LIGHT.get(), blockPos, blockState);
    }

    public static void tick(
        Level world,
        BlockPos blockPos,
        BlockState blockState,
        TickingLightBlockEntity blockEntity
    ) {
        blockEntity.tick();
    }

    public void refresh(int lifeExtension) {
        lifespan = -lifeExtension;
    }

    private void tick() {
        if (level == null) {
            return;
        }

        if (lifespan++ < 5) {
            return;
        }

        var block = level.getBlockState(getBlockPos()).getBlock();

        if (block instanceof TickingLightBlock) {
            level.setBlockAndUpdate(getBlockPos(), Blocks.AIR.defaultBlockState());
        } else {
            setRemoved();
        }
    }
}
