package com.alien.common.gameplay.ai;

import com.alien.common.gameplay.entity.living.alien.EggCarrier;
import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

public class PickUpEggGoal<T extends Xenomorph & EggCarrier> extends Goal {

    private final T eggCarryingXenomorph;

    private Path path;

    public PickUpEggGoal(T eggCarryingXenomorph) {
        this.eggCarryingXenomorph = eggCarryingXenomorph;
    }

    @Override
    public boolean canUse() {
        var targetOvomorph = eggCarryingXenomorph.getEggPickupManager().getTargetOvomorphOrNull();

        return targetOvomorph != null
            && targetOvomorph.wantsPickup
            && !targetOvomorph.isPassenger()
            && eggCarryingXenomorph.getTarget() == null;
    }

    @Override
    public void start() {
        var targetOvomorph = eggCarryingXenomorph.getEggPickupManager().getTargetOvomorphOrNull();

        if (targetOvomorph != null) {
            this.path = eggCarryingXenomorph.getNavigation().createPath(targetOvomorph, 0);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse()
            && path != null
            && !path.isDone()
            && path.canReach();
    }

    @Override
    public void tick() {
        var targetOvomorph = eggCarryingXenomorph.getEggPickupManager().getTargetOvomorphOrNull();

        if (targetOvomorph != null) {
            eggCarryingXenomorph.getNavigation().moveTo(path, 0.5);

            if (eggCarryingXenomorph.distanceToSqr(targetOvomorph) <= 2 * 2) {
                targetOvomorph.startRiding(eggCarryingXenomorph);
                eggCarryingXenomorph.getEggPickupManager()
                    .setTargetOvomorph(null);
            }
        }
    }

    @Override
    public void stop() {
        super.stop();

        this.path = null;

        eggCarryingXenomorph.getEggPickupManager()
            .setTargetOvomorph(null);
    }
}
