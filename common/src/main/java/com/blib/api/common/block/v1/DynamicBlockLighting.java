package com.blib.api.common.block.v1;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import com.blib.mod.common.registry.init.BLibBlocks;

public class DynamicBlockLighting {

    private static final int DEFAULT_MAX_DISTANCE = 2;

    public static void emitTemporaryLight(Entity entity) {
        emitTemporaryLight(entity.level(), entity.blockPosition(), DEFAULT_MAX_DISTANCE);
    }

    public static void emitTemporaryLight(Entity entity, int maxDistance) {
        emitTemporaryLight(entity.level(), entity.blockPosition(), maxDistance);
    }

    public static void emitTemporaryLight(Level level, BlockPos blockPos) {
        emitTemporaryLight(level, blockPos, DEFAULT_MAX_DISTANCE);
    }

    public static void emitTemporaryLight(Level level, BlockPos blockPos, int maxDistance) {
        if (level.isClientSide) {
            return;
        }

        var lightBlockPos = findFreeSpace(level, blockPos, maxDistance);

        if (lightBlockPos != null) {
            level.setBlockAndUpdate(lightBlockPos, BLibBlocks.TICKING_LIGHT.get().defaultBlockState());
        }
    }

    private static BlockPos findFreeSpace(Level world, BlockPos blockPos, int maxDistance) {
        if (blockPos == null) {
            return null;
        }

        var offsets = new int[maxDistance * 2 + 1];
        offsets[0] = 0;

        for (var i = 2; i <= maxDistance * 2; i += 2) {
            offsets[i - 1] = i / 2;
            offsets[i] = -i / 2;
        }

        for (var x : offsets) {
            for (var y : offsets) {
                for (var z : offsets) {
                    var offsetPos = blockPos.offset(x, y, z);
                    var state = world.getBlockState(offsetPos);

                    if (state.isAir() || state.getBlock().equals(BLibBlocks.TICKING_LIGHT.get())) {
                        return offsetPos;
                    }
                }
            }
        }

        return null;
    }

    private DynamicBlockLighting() {
        throw new UnsupportedOperationException();
    }
}
