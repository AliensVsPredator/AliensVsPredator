package com.avp.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DeskTerminalBlockEntity extends BlockEntity {

    public DeskTerminalBlockEntity(BlockPos pos, BlockState blockState) {
        super(AVPBlockEntityTypes.DESK_TERMINAL, pos, blockState);
    }
}
