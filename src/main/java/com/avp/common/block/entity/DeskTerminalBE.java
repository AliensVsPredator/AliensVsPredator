package com.avp.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DeskTerminalBE extends BlockEntity {

    public DeskTerminalBE(BlockPos pos, BlockState blockState) {
        super(BlockEntityTypes.DESK_TERMINAL_BE, pos, blockState);
    }
}
