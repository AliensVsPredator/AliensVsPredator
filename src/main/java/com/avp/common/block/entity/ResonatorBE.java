package com.avp.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ResonatorBE extends BlockEntity {

    public ResonatorBE(BlockPos pos, BlockState blockState) {
        super(BlockEntityTypes.RESONATOR_BE, pos, blockState);
    }
}
