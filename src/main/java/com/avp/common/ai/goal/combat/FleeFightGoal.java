package com.avp.common.ai.goal.combat;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class FleeFightGoal extends Goal {

    protected final PathfinderMob mob;

    public FleeFightGoal(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (this.mob.isVehicle()) {
            return false;
        }

        return this.mob.getHealth() < (this.mob.getMaxHealth() / 2);
    }

    @Override
    public void start() {
        this.mob.setAggressive(false);
    }

    @Override
    public void stop() {}

    @Override
    public void tick() {
        if (this.mob.getNavigation().isDone() && this.mob.getLastHurtByMob() != null) {
            var lastAttacker = this.mob.getLastHurtByMob();
            var attackerPos = lastAttacker.position();
            var randomDirection = this.getRandomDirectionAwayFrom(attackerPos).normalize().scale(20.0);
            var mobPosition = this.mob.position();
            var targetPos = mobPosition.add(randomDirection);

            this.mob.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, 1.0);
        }
    }

    private Vec3 getRandomDirectionAwayFrom(Vec3 attackerPos) {
        var mobPosition = this.mob.position();
        var directionAwayFromAttacker = mobPosition.subtract(attackerPos);
        var randomOffsetX = this.mob.getRandom().nextDouble() - 0.5;
        var randomOffsetZ = this.mob.getRandom().nextDouble() - 0.5;
        var randomizedDirection = directionAwayFromAttacker.add(randomOffsetX, 0, randomOffsetZ);

        return randomizedDirection;
    }
}
