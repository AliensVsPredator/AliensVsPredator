package com.avp.core.common.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ExplosionBuilder {

    private final ExplosionCallbacks.Builder callbacksBuilder;

    private final Vec3 center;

    private final ServerLevel level;

    private final Map<Direction, Integer> directionToRadiusMap;

    int blockSampleCountPerCycle;

    int cycleDelayInTicks;

    private ExplosionBlockSamplerPredicate samplerPredicate;

    ExplosionBuilder(ServerLevel level, Vec3 center) {
        this.level = level;
        this.callbacksBuilder = ExplosionCallbacks.builder();
        this.center = center;
        this.directionToRadiusMap = new EnumMap<>(Direction.class);
        this.blockSampleCountPerCycle = 65535;
        this.cycleDelayInTicks = 1;
        this.samplerPredicate = ExplosionBlockSamplerPredicate.DEFAULT;
    }

    public ExplosionBuilder onBlockSample(ExplosionBlockSampler sampler) {
        callbacksBuilder.withOnBlockSampleCallback(sampler);
        return this;
    }

    public ExplosionBuilder onCycleFinish(Consumer<List<BlockPos>> callback) {
        callbacksBuilder.withOnCycleFinishCallback(callback);
        return this;
    }

    public ExplosionBuilder onCycleStart(Runnable callback) {
        callbacksBuilder.withOnCycleStartCallback(callback);
        return this;
    }

    public ExplosionBuilder onExplosionFinish(Runnable callback) {
        callbacksBuilder.withOnExplosionFinishCallback(callback);
        return this;
    }

    public ExplosionBuilder onExplosionStart(Runnable callback) {
        callbacksBuilder.withOnExplosionStartCallback(callback);
        return this;
    }

    public ExplosionBuilder withBlockSampleCountPerCycle(int blockSampleCountPerCycle) {
        this.blockSampleCountPerCycle = blockSampleCountPerCycle;
        return this;
    }

    public ExplosionBuilder withCycleDelayInTicks(int cycleDelayInTicks) {
        this.cycleDelayInTicks = cycleDelayInTicks;
        return this;
    }

    public ExplosionBuilder withRadius(int radius) {
        for (var direction : Direction.values()) {
            withRadius(direction, radius);
        }

        return this;
    }

    public ExplosionBuilder withRadius(Direction.Plane plane, int radius) {
        plane.stream().forEach(direction -> withRadius(direction, radius));
        return this;
    }

    public ExplosionBuilder withRadius(Direction.Axis axis, int radius) {
        var direction = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);

        withRadius(direction, radius);
        withRadius(direction.getOpposite(), radius);

        return this;
    }

    public ExplosionBuilder withRadius(Direction direction, int radius) {
        directionToRadiusMap.put(direction, radius);
        return this;
    }

    public ExplosionBuilder withSamplerPredicate(ExplosionBlockSamplerPredicate samplerPredicate) {
        this.samplerPredicate = samplerPredicate;
        return this;
    }

    public Explosion build() {
        var callbacks = callbacksBuilder.build();
        var centerBlockPosition = new BlockPos((int) center.x, (int) center.y, (int) center.z);
        var config = new ExplosionConfig(
            blockSampleCountPerCycle,
            center,
            centerBlockPosition,
            cycleDelayInTicks,
            directionToRadiusMap
        );

        return new Explosion(level, callbacks, config, samplerPredicate);
    }
}
