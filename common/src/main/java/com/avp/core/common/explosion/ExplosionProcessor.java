package com.avp.core.common.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.avp.core.server.ServerScheduler;

public class ExplosionProcessor {

    private final Explosion explosion;

    private final Map<Direction, ExplosionCursor> directionToExplosionCursorMap;

    public ExplosionProcessor(Explosion explosion) {
        this.explosion = explosion;
        this.directionToExplosionCursorMap = new EnumMap<>(
            Arrays.stream(Direction.values())
                .collect(Collectors.toMap(Function.identity(), direction -> new ExplosionCursor(explosion, direction)))
        );
    }

    public void process() {
        var config = explosion.config();
        processBatch(config.blockSampleCountPerCycle(), config.cycleDelayInTicks());
    }

    private void processBatch(int maxBlocksPerCycle, int delayBetweenCycles) {
        var callbacks = explosion.callbacks();

        callbacks.onCycleStart();

        var blocksToSample = sampleBlocks(maxBlocksPerCycle);

        for (var pos : blocksToSample) {
            callbacks.onBlockSample(explosion, pos);
        }

        callbacks.onCycleFinish(blocksToSample);

        if (canAnyWallMoveFurther()) {
            ServerScheduler.schedule(
                () -> processBatch(maxBlocksPerCycle, delayBetweenCycles),
                Duration.ofMillis(delayBetweenCycles * 50L)
            );
        } else {
            callbacks.onExplosionFinish();
        }
    }

    private List<BlockPos> sampleBlocks(int maxBlocksPerCycle) {
        var count = 0;
        var sampledBlocks = new ArrayList<BlockPos>();

        while (count < maxBlocksPerCycle && canAnyWallMoveFurther()) {
            for (var entry : directionToExplosionCursorMap.entrySet()) {
                if (count >= maxBlocksPerCycle) {
                    break;
                }

                if (!entry.getValue().canExpandFurther()) {
                    continue;
                }

                var pos = entry.getValue().next();

                if (explosion.samplerPredicate().test(explosion, pos)) {
                    sampledBlocks.add(pos);
                    count++;
                }
            }
        }
        return sampledBlocks;
    }

    private boolean canAnyWallMoveFurther() {
        return directionToExplosionCursorMap.values().stream().anyMatch(ExplosionCursor::canExpandFurther);
    }
}
