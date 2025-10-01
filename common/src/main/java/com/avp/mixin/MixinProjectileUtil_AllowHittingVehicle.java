package com.avp.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.avp.common.registry.tag.AVPEntityTypeTags;

@Mixin(ProjectileUtil.class)
public class MixinProjectileUtil_AllowHittingVehicle {

    @WrapOperation(
        method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;getRootVehicle()Lnet/minecraft/world/entity/Entity;"
        )
    )
    private static Entity modifyRootVehicleCheck(Entity instance, Operation<Entity> original) {
        // Check if an entity is alien.
        if (avp$isAlien(instance)) {
            return null;
        }

        return instance.getRootVehicle();
    }

    @Unique
    private static boolean avp$isAlien(Entity entity) {
        return entity.getType().is(AVPEntityTypeTags.ALIENS);
    }

}
