package com.lib.common.data;

import java.time.Duration;

public class Cooldown {

    public static Cooldown withCooldownTime(Duration cooldownTime) {
        return withCooldownTimeInTicks(cooldownTime.toSeconds() * 20);
    }

    public static Cooldown withCooldownTimeInTicks(long maxCooldownInTicks) {
        return new Cooldown(maxCooldownInTicks);
    }

    private final long maxCooldownInTicks;

    private long cooldownInTicks;

    private Cooldown(long maxCooldownInTicks) {
        this.maxCooldownInTicks = maxCooldownInTicks;
    }

    public void tick() {
        cooldownInTicks = Math.max(cooldownInTicks - 1, 0);
    }

    public boolean isActive() {
        return cooldownInTicks > 0;
    }

    public void reset() {
        this.cooldownInTicks = maxCooldownInTicks;
    }
}
