package org.avp.api.entity.ai.action.impl;

import net.minecraft.world.entity.Mob;
import org.avp.api.entity.ai.action.Action;

public class IdleLookAroundAction extends Action {

    private int lookTime;

    private double relativeX;

    private double relativeZ;

    private final Mob mob;

    public IdleLookAroundAction(Mob mob) {
        this.mob = mob;
    }

    @Override
    public int getCost() {
        if (lookTime > 0) {
            lookTime--;
        }
        return lookTime;
    }

    @Override
    public void execute() {
        if (lookTime <= 0) {
            double randomAngle = 6.283185307179586 * this.mob.getRandom().nextDouble();
            this.relativeX = Math.cos(randomAngle);
            this.relativeZ = Math.sin(randomAngle);
            this.lookTime = 20 + this.mob.getRandom().nextInt(20);
        }

        this.mob.getLookControl().setLookAt(this.mob.getX() + this.relativeX, this.mob.getEyeY(), this.mob.getZ() + this.relativeZ);
    }
}
