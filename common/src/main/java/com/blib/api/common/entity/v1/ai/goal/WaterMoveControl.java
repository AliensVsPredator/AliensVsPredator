package com.blib.api.common.entity.v1.ai.goal;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class WaterMoveControl extends MoveControl {

    private final PathfinderMob pathfinderMob;

    public WaterMoveControl(PathfinderMob pathfinderMob) {
        super(pathfinderMob);
        this.pathfinderMob = pathfinderMob;
    }

    @Override
    public void tick() {
        var livingEntity = pathfinderMob.getTarget();
        if (pathfinderMob.isUnderWater()) {
            if (livingEntity != null && livingEntity.getY() > pathfinderMob.getY()) {
                pathfinderMob.setDeltaMovement(pathfinderMob.getDeltaMovement().add(0.0, 0.002, 0.0));
            }

            if (operation != MoveControl.Operation.MOVE_TO || pathfinderMob.getNavigation().isDone()) {
                pathfinderMob.setSpeed(0.0F);
                return;
            }

            var d = wantedX - pathfinderMob.getX();
            var e = wantedY - pathfinderMob.getY();
            var f = wantedZ - pathfinderMob.getZ();
            var g = Math.sqrt(d * d + e * e + f * f);
            e /= g;
            var h = (float) (Mth.atan2(f, d) * 180.0F / (float) Math.PI) - 90.0F;
            pathfinderMob.setYRot(rotlerp(pathfinderMob.getYRot(), h, 90.0F));
            pathfinderMob.yBodyRot = pathfinderMob.getYRot();
            var i = (float) (speedModifier * pathfinderMob.getAttributeValue(Attributes.MOVEMENT_SPEED));
            var j = Mth.lerp(0.125F, pathfinderMob.getSpeed(), i);
            pathfinderMob.setSpeed(j);
            pathfinderMob.setDeltaMovement(
                pathfinderMob.getDeltaMovement().add(j * d * 0.005, j * e * 0.1, j * f * 0.005)
            );
        } else {
            if (!pathfinderMob.onGround()) {
                pathfinderMob.setDeltaMovement(pathfinderMob.getDeltaMovement().add(0.0, -0.008, 0.0));
            }

            super.tick();
        }
    }
}
