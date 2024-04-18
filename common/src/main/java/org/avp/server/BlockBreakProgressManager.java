package org.avp.server;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.avp.api.Tuple;
import org.avp.common.AVPConstants;
import org.avp.common.util.TimeUtils;

public class BlockBreakProgressManager {

    private static final Map<BlockPos, Tuple<Long, Float>> BLOCK_BREAK_PROGRESS_MAP = new HashMap<>();

    public static void tick(Level level) {
        var gameTime = level.getGameTime();
        if (gameTime % TimeUtils.ONE_MINUTE_IN_TICKS != 0) {
            return;
        }

        AVPConstants.LOGGER.debug(
            "Cleaning block break progress map ({} entries)",
            BlockBreakProgressManager.BLOCK_BREAK_PROGRESS_MAP.size()
        );
        BlockBreakProgressManager.BLOCK_BREAK_PROGRESS_MAP.entrySet().removeIf(entry -> {
            var lastUpdateTimeMillis = entry.getValue().first();
            return System.currentTimeMillis() > lastUpdateTimeMillis;
        });
        AVPConstants.LOGGER.debug(
            "Finished cleaning block break progress map ({} entries)",
            BlockBreakProgressManager.BLOCK_BREAK_PROGRESS_MAP.size()
        );
    }

    public static void damage(Level level, BlockPos blockPos, float damage) {
        BlockBreakProgressManager.BLOCK_BREAK_PROGRESS_MAP.compute(blockPos, (key, tuple) -> {
            var blockState = level.getBlockState(blockPos);
            var block = blockState.getBlock();
            var cachedValue = tuple == null ? 0 : tuple.second();
            var newValue = cachedValue + (damage / (2F + block.defaultDestroyTime() / 2F));
            var progress = (int) Mth.clamp(newValue, 0F, 9F);
            level.destroyBlockProgress(Objects.hash(blockPos), blockPos, progress);

            if (progress >= 9) {
                level.destroyBlock(blockPos, false);
                return null;
            }
            return new Tuple<>(System.currentTimeMillis() + TimeUtils.FIVE_MINUTES_IN_MILLIS, newValue);
        });
    }

    private BlockBreakProgressManager() {
        throw new UnsupportedOperationException();
    }
}
