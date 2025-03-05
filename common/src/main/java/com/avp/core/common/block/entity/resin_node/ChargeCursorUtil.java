package com.avp.core.common.block.entity.resin_node;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SculkVeinBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import com.avp.core.common.block.entity.resin_node.behavior.ResinSpreadBehavior;
import com.avp.core.common.block.entity.resin_node.behavior.SpreadBehavior;

public class ChargeCursorUtil {

    private static final BlockPos BOTTOM_LEFT = new BlockPos(-1, -1, -1);

    private static final BlockPos TOP_RIGHT = new BlockPos(1, 1, 1);

    private static final ObjectArrayList<Vec3i> NON_CORNER_NEIGHBOURS = Util.make(
        new ObjectArrayList<>(18),
        objectArrayList -> BlockPos.betweenClosedStream(BOTTOM_LEFT, TOP_RIGHT)
            .filter(ChargeCursorUtil::isZeroAxis)
            .map(BlockPos::immutable)
            .forEach(objectArrayList::add)
    );

    static SpreadBehavior getSpreadBehavior(BlockState blockState) {
        return blockState.getBlock() instanceof SpreadBehavior spreadBehavior ? spreadBehavior : ResinSpreadBehavior.INSTANCE;
    }

    @Nullable
    static BlockPos getValidMovementPos(LevelAccessor levelAccessor, BlockPos blockPos, RandomSource randomSource) {
        var mutableBlockPos = blockPos.mutable();
        var mutableBlockPos2 = blockPos.mutable();

        for (var vec3i : getRandomizedNonCornerNeighbourOffsets(randomSource)) {
            mutableBlockPos2.setWithOffset(blockPos, vec3i);
            var blockState = levelAccessor.getBlockState(mutableBlockPos2);

            if (blockState.getBlock() instanceof SpreadBehavior && isMovementUnobstructed(levelAccessor, blockPos, mutableBlockPos2)) {
                mutableBlockPos.set(mutableBlockPos2);

                if (SculkVeinBlock.hasSubstrateAccess(levelAccessor, blockState, mutableBlockPos2)) {
                    break;
                }
            }
        }

        return mutableBlockPos.equals(blockPos) ? null : mutableBlockPos;
    }

    private static boolean isZeroAxis(BlockPos blockPos) {
        return (blockPos.getX() == 0 || blockPos.getY() == 0 || blockPos.getZ() == 0) && !blockPos.equals(BlockPos.ZERO);
    }

    private static List<Vec3i> getRandomizedNonCornerNeighbourOffsets(RandomSource randomSource) {
        return Util.shuffledCopy(NON_CORNER_NEIGHBOURS, randomSource);
    }

    private static boolean isMovementUnobstructed(LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (blockPos.distManhattan(blockPos2) == 1) {
            return true;
        } else {
            BlockPos blockPos3 = blockPos2.subtract(blockPos);
            Direction direction = Direction.fromAxisAndDirection(
                Direction.Axis.X,
                blockPos3.getX() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE
            );
            Direction direction2 = Direction.fromAxisAndDirection(
                Direction.Axis.Y,
                blockPos3.getY() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE
            );
            Direction direction3 = Direction.fromAxisAndDirection(
                Direction.Axis.Z,
                blockPos3.getZ() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE
            );
            if (blockPos3.getX() == 0) {
                return isUnobstructed(levelAccessor, blockPos, direction2) || isUnobstructed(levelAccessor, blockPos, direction3);
            } else {
                return blockPos3.getY() == 0
                    ? isUnobstructed(levelAccessor, blockPos, direction) || isUnobstructed(levelAccessor, blockPos, direction3)
                    : isUnobstructed(levelAccessor, blockPos, direction) || isUnobstructed(levelAccessor, blockPos, direction2);
            }
        }
    }

    private static boolean isUnobstructed(LevelAccessor levelAccessor, BlockPos blockPos, Direction direction) {
        var relativePos = blockPos.relative(direction);
        var opposite = direction.getOpposite();
        var blockState = levelAccessor.getBlockState(relativePos);
        return !blockState.isFaceSturdy(levelAccessor, relativePos, opposite);
    }
}
