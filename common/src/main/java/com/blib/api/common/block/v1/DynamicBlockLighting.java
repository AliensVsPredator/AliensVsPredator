package com.blib.api.common.block.v1;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import com.blib.azurelib.common.util.AzureLibUtil;
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

        var lightBlockPos = AzureLibUtil.findFreeSpace(level, blockPos, maxDistance);

        if (lightBlockPos != null) {
            level.setBlockAndUpdate(lightBlockPos, BLibBlocks.TICKING_LIGHT.get().defaultBlockState());
        }
    }

    private DynamicBlockLighting() {
        throw new UnsupportedOperationException();
    }
}
