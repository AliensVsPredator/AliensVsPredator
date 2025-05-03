package com.avp.common.explosion;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class Explosion {

    public static ExplosionBuilder builder(ServerLevel level, Vec3 center) {
        return new ExplosionBuilder(level, center);
    }

    private final ExplosionCallbacks callbacks;

    private final ExplosionConfig config;

    private final ExplosionProcessor explosionProcessor;

    private final ServerLevel level;

    private final ExplosionBlockSamplerPredicate samplerPredicate;

    Explosion(
        ServerLevel level,
        ExplosionCallbacks callbacks,
        ExplosionConfig config,
        ExplosionBlockSamplerPredicate samplerPredicate
    ) {
        this.callbacks = callbacks;
        this.config = config;
        this.explosionProcessor = new ExplosionProcessor(this);
        this.level = level;
        this.samplerPredicate = samplerPredicate;
    }

    public void explode() {
        callbacks.onExplosionStart();
        explosionProcessor.process();
    }

    public ExplosionCallbacks callbacks() {
        return callbacks;
    }

    public ExplosionConfig config() {
        return config;
    }

    public ServerLevel level() {
        return level;
    }

    public ExplosionBlockSamplerPredicate samplerPredicate() {
        return samplerPredicate;
    }
}
