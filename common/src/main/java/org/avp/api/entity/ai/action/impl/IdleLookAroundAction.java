package org.avp.api.entity.ai.action.impl;

import net.minecraft.world.entity.Mob;
import org.avp.api.entity.ai.action.Action;
import org.avp.api.entity.ai.action.CooldownAction;

public class IdleLookAroundAction extends CooldownAction {

    private int lookTime;

    private double relativeX;

    private double relativeZ;

    private final Mob mob;

    public IdleLookAroundAction(Mob mob) {
        super(120, mob.getRandom());
        this.mob = mob;
    }

    @Override
    public int getCost() {
        if (lookTime > 0) {
            return 0;
        }

        return super.getCost();
    }

    @Override
    public void tick() {
        if (lookTime == 0) {
            super.tick();
        }

        if (getCooldown() == 0 && lookTime <= 0) {
            double randomAngle = 6.283185307179586 * this.mob.getRandom().nextDouble();
            this.relativeX = Math.cos(randomAngle);
            this.relativeZ = Math.sin(randomAngle);
            this.lookTime = 40 + this.mob.getRandom().nextInt(40);
        }
    }

    @Override
    public void execute() {
        if (lookTime > 0) {
            lookTime--;
        }

        if (lookTime <= 0) {
            resetCooldown();
        }

        this.mob.getLookControl().setLookAt(this.mob.getX() + this.relativeX, this.mob.getEyeY(), this.mob.getZ() + this.relativeZ);
    }
}
