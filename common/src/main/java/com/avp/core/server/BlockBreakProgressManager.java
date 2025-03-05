package com.avp.core.server;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BlockBreakProgressManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockBreakProgressManager.class);

    private static final Map<BlockPos, Map.Entry<Long, Float>> BLOCK_BREAK_PROGRESS_MAP = new HashMap<>();

    public static void tick(Level level) {
        var gameTime = level.getGameTime();

        if (gameTime % (20 * 20) != 0) {
            return;
        }

        LOGGER.debug(
            "Cleaning block break progress map ({} entries)",
            BlockBreakProgressManager.BLOCK_BREAK_PROGRESS_MAP.size()
        );
        BlockBreakProgressManager.BLOCK_BREAK_PROGRESS_MAP.entrySet().removeIf(entry -> {
            var lastUpdateTimeMillis = entry.getValue().getKey();
            return System.currentTimeMillis() > lastUpdateTimeMillis;
        });
        LOGGER.debug(
            "Finished cleaning block break progress map ({} entries)",
            BlockBreakProgressManager.BLOCK_BREAK_PROGRESS_MAP.size()
        );
    }

    // Damage progress is a value ranging from 0 to 9 (both ends inclusive).
    // All blocks also have a destroy time (in seconds).
    public static void damage(Level level, BlockPos blockPos, float damage) {
        var immutableBlockPos = blockPos.immutable();

        BlockBreakProgressManager.BLOCK_BREAK_PROGRESS_MAP.compute(immutableBlockPos, (key, entry) -> {
            var blockState = level.getBlockState(immutableBlockPos);
            var block = blockState.getBlock();
            var currentDestroyProgress = entry == null ? 0 : entry.getValue();
            var defaultDestroyTimeInSeconds = block.defaultDestroyTime();

            if (defaultDestroyTimeInSeconds < 0) {
                // This block cannot be destroyed, so abort.
                return null;
            }

            var destroyTimeInTicks = block.defaultDestroyTime() * 20;
            var weight = Math.max(destroyTimeInTicks, 1);

            var newDestroyProgress = currentDestroyProgress + (damage / weight);
            var progress = (int) Mth.clamp(newDestroyProgress, 0F, 9F);
            var hash = Objects.hash(immutableBlockPos);

            if (progress >= 9) {
                level.destroyBlockProgress(hash, immutableBlockPos, -1);
                level.destroyBlock(immutableBlockPos, false);
                return null;
            } else {
                level.destroyBlockProgress(hash, immutableBlockPos, progress);
            }
            return Map.entry(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5), newDestroyProgress);
        });
    }

    private BlockBreakProgressManager() {
        throw new UnsupportedOperationException();
    }
}
