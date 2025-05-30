package com.human.common.gameplay.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.avp.common.registry.init.AVPBlockEntityTypes;

public class DeskTerminalBlockEntity extends BlockEntity {

    public DeskTerminalBlockEntity(BlockPos pos, BlockState blockState) {
        super(AVPBlockEntityTypes.DESK_TERMINAL.get(), pos, blockState);
    }
}
