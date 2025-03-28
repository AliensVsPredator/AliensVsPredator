package com.avp.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TripMineBE extends BlockEntity {

    public TripMineBE(BlockPos pos, BlockState blockState) {
        super(BlockEntityTypes.TRIP_MINE_BE, pos, blockState);
    }
}
