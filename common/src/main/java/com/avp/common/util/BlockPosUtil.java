package com.avp.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;

public class BlockPosUtil {

    public static boolean isFireAdjacent(Level level, BlockPos blockPos) {
        return Direction.stream().anyMatch(direction -> level.getBlockState(blockPos.relative(direction)).is(BlockTags.FIRE));
    }
}
