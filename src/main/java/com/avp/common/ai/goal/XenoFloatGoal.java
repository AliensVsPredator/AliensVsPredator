package com.avp.common.ai.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;

public class XenoFloatGoal extends FloatGoal {

    private Mob mob;

    public XenoFloatGoal(Mob mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        var blockPos = this.mob.blockPosition();
        var belowPos = blockPos.below();
        var belowState = this.mob.level().getBlockState(belowPos);
        return this.mob.isInWater() && belowState.isSolidRender(this.mob.level(), belowPos);
    }

    @Override
    public void tick() {
        if (this.mob.getRandom().nextFloat() < 0.8F) {
            var deltaMovement = mob.getDeltaMovement().scale(0.2);
            var vectorDifference = mob.getEyePosition()
                .normalize()
                .scale(0.2)
                .add(deltaMovement.x, 0, deltaMovement.z);

            mob.setDeltaMovement(vectorDifference.x, Math.max(0.6, vectorDifference.y), vectorDifference.z);
        }
    }
}
