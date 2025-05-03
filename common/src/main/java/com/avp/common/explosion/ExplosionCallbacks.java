package com.avp.common.explosion;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ExplosionCallbacks {

    public static ExplosionCallbacks.Builder builder() {
        return new ExplosionCallbacks.Builder();
    }

    private @Nullable ExplosionBlockSampler onBlockSampleCallback;

    private @Nullable Consumer<List<BlockPos>> onCycleFinishCallback;

    private @Nullable Runnable onCycleStartCallback;

    private @Nullable Runnable onExplosionFinishCallback;

    private @Nullable Runnable onExplosionStartCallback;

    private ExplosionCallbacks() {}

    public void onBlockSample(Explosion explosion, BlockPos pos) {
        if (onBlockSampleCallback != null) {
            onBlockSampleCallback.sample(explosion, pos);
        }
    }

    public void onCycleFinish(List<BlockPos> sampledBlockPositions) {
        if (onCycleFinishCallback != null) {
            onCycleFinishCallback.accept(sampledBlockPositions);
        }
    }

    public void onCycleStart() {
        if (onCycleStartCallback != null) {
            onCycleStartCallback.run();
        }
    }

    public void onExplosionFinish() {
        if (onExplosionFinishCallback != null) {
            onExplosionFinishCallback.run();
        }
    }

    public void onExplosionStart() {
        if (onExplosionStartCallback != null) {
            onExplosionStartCallback.run();
        }
    }

    public static class Builder {

        private final ExplosionCallbacks callbacks;

        private Builder() {
            callbacks = new ExplosionCallbacks();
        }

        public void withOnBlockSampleCallback(ExplosionBlockSampler sampler) {
            callbacks.onBlockSampleCallback = sampler;
        }

        public void withOnCycleFinishCallback(Consumer<List<BlockPos>> callback) {
            callbacks.onCycleFinishCallback = callback;
        }

        public void withOnCycleStartCallback(Runnable callback) {
            callbacks.onCycleStartCallback = callback;
        }

        public void withOnExplosionFinishCallback(Runnable callback) {
            callbacks.onExplosionFinishCallback = callback;
        }

        public void withOnExplosionStartCallback(Runnable callback) {
            callbacks.onExplosionStartCallback = callback;
        }

        public ExplosionCallbacks build() {
            return callbacks;
        }
    }
}
