package com.lib.common.gameplay.util.spatial.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BlockPosUtil {

    public static List<BlockPos> getNeighborsMatching(Level level, BlockPos centerPos, Predicate<BlockState> predicate) {
        var matchingNeighbors = new ArrayList<BlockPos>();

        for (var direction : Direction.values()) {
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

    public static boolean canEntityTypeFit(Level level, BlockPos pos, EntityType<?> entityType) {
        var dims = entityType.getDimensions();
        var width = dims.width();
        var height = dims.height();

        var x = pos.getX() + 0.5;
        var y = pos.getY();
        var z = pos.getZ() + 0.5;

        var halfWidth = width / 2.0;
        var box = new AABB(
            // minX, minY, minZ
            x - halfWidth,
            y,
            z - halfWidth,
            // maxX, maxY, maxZ
            x + halfWidth,
            y + height,
            z + halfWidth
        );

        return level.noCollision(box);
    }
}
