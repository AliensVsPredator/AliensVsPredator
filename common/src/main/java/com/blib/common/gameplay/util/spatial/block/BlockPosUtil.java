package com.blib.common.gameplay.util.spatial.block;

import com.blib.common.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BlockPosUtil {

    public static List<BlockPos> getNeighborsMatching(Level level, BlockPos centerPos, Predicate<BlockState> predicate) {
        var matchingNeighbors = new ArrayList<BlockPos>();

        for (var direction : DirectionUtil.VALUES) {
            var neighborPos = centerPos.relative(direction);
            var state = level.getBlockState(neighborPos);

            if (predicate.test(state)) {
                matchingNeighbors.add(neighborPos);
            }
        }

        return matchingNeighbors;
    }

    public static boolean isFireAdjacent(Level level, BlockPos blockPos) {
        return Direction.stream().anyMatch(direction -> level.getBlockState(blockPos.relative(direction)).is(BlockTags.FIRE));
    }
}
