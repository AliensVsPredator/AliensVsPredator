package com.blib.internal.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.api.common.pathfinding.v1.physics.ClimbingMoveControl;
import com.blib.api.common.pathfinding.v1.physics.ClimbingTravelHandler;

/**
 * Replaces vanilla {@code travel()} physics for entities using BLib's {@link ClimbingMoveControl} while they are
 * attached to a climbing surface. All other entities pass through to vanilla behavior unmodified.
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_Travel {

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void blib$onTravel(Vec3 movementInput, CallbackInfo ci) {
        if (!((Object) this instanceof Mob mob)) {
            return;
        }

        if (!(mob.getMoveControl() instanceof ClimbingMoveControl climbingMoveControl)) {
            return;
        }

        if (climbingMoveControl.getActiveSurface() == null) {
            return;
        }

        ClimbingTravelHandler.travel(mob, climbingMoveControl);
        ci.cancel();
    }
}
