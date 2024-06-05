package org.avp.api.entity.ai.action;

import net.minecraft.util.RandomSource;
import org.avp.api.entity.ai.CostConstraint;

public abstract class CooldownAction extends Action {

    private int cooldown;

    private final int cooldownInterval;

    private final RandomSource randomSource;

    protected CooldownAction(int cooldownInterval, RandomSource randomSource, boolean hasInitialCooldown) {
        this.cooldownInterval = cooldownInterval;
        this.randomSource = randomSource;

        if (hasInitialCooldown) {
            resetCooldown();
        }
    }

    @Override
    public CostConstraint createCostConstraint() {
        return new CostConstraint(0, cooldownInterval * 2);
    }

    @Override
    public boolean isValid() {
        return !isCooldownActive();
    }

    @Override
    public int getCost() {
        return cooldown;
    }

    @Override
    public void tick() {
        super.tick();
        if (isCooldownActive()) {
            cooldown--;
        }
    }

    @Override
    public void onComplete() {
        super.onComplete();
        resetCooldown();
    }

    protected boolean isCooldownActive() {
        return cooldown > 0;
    }

    protected int getCooldown() {
        return cooldown;
    }

    protected void resetCooldown() {
        cooldown = cooldownInterval + randomSource.nextInt(cooldownInterval);
    }
}
