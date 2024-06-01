package org.avp.api.entity.ai.action;

import net.minecraft.util.RandomSource;

public abstract class CooldownAction extends Action {

    private int cooldown;

    private final int cooldownInterval;

    private final RandomSource randomSource;

    protected CooldownAction(int cooldownInterval, RandomSource randomSource) {
        this.cooldownInterval = cooldownInterval;
        this.randomSource = randomSource;
        resetCooldown();
    }

    @Override
    public boolean isValid() {
        return cooldown <= 0;
    }

    @Override
    public int getCost() {
        return cooldown;
    }

    @Override
    public void tick() {
        super.tick();
        if (cooldown > 0) {
            cooldown--;
        }
    }

    @Override
    public void onComplete() {
        super.onComplete();
        resetCooldown();
    }

    protected int getCooldown() {
        return cooldown;
    }

    protected void resetCooldown() {
        cooldown = cooldownInterval + randomSource.nextInt(cooldownInterval);
    }
}
