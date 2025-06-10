package com.avp.common.gameplay.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;

public class XenoFloatGoal extends FloatGoal {

    private Mob mob;

    private BlockPos targetPos;

    public XenoFloatGoal(Mob mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        var blockPos = this.mob.blockPosition();
        var belowPos = blockPos.below();
        var belowState = this.mob.level().getBlockState(belowPos);

        if (this.mob.isAggressive()) {
            return false;
        }

        if (!this.mob.isInWater()) {
            return false;
        }

        if (!belowState.isSolidRender(this.mob.level(), belowPos)) {
            return false;
        }

        if (this.targetPos == null) {
            return false;
        }

        this.targetPos = findNonWaterBlock(blockPos);
        return this.targetPos != null;
    }

    @Override
    public void tick() {
        if (this.targetPos != null) {
            var deltaMovement = mob.getDeltaMovement().scale(0.2);
            var vectorToTarget = mob.position()
                .vectorTo(this.targetPos.getCenter())
                .normalize()
                .scale(0.2)
                .add(deltaMovement.x, 0, deltaMovement.z);

            mob.setDeltaMovement(vectorToTarget.x, Math.max(0.6, vectorToTarget.y), vectorToTarget.z);
        }
    }

    private BlockPos findNonWaterBlock(BlockPos centerPos) {
        var level = this.mob.level();

        for (var x = -5; x <= 5; x++) {
            for (var y = -5; y <= 5; y++) {
                for (var z = -5; z <= 5; z++) {
                    var currentPos = centerPos.offset(x, y, z);
                    var blockState = level.getBlockState(currentPos);

                    if (!blockState.isAir() && blockState.isSolidRender(level, currentPos)) {
                        return currentPos;
                    }
                }
            }
        }
        return null;
    }

}
