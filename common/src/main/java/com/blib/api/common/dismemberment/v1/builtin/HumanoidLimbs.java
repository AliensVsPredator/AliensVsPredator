package com.blib.api.common.dismemberment.v1.builtin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import com.blib.api.common.dismemberment.v1.LimbCategories;
import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.mod.BLib;

/**
 * Shared registration helper for vanilla mobs whose model is built on {@code HumanoidModel}. Builds the six standard
 * limb definitions (head, body, right/left arm, right/left leg) for the given entity type using a unique id prefix so
 * the registry stays mob-scoped, and applies the same render rotation/offset values that were dialed in on the zombie.
 * Add a per-mob definition class that calls {@link #register} when wiring up a new humanoid mob.
 */
public final class HumanoidLimbs {

    private HumanoidLimbs() {}

    public static void register(EntityType<?> entityType, String idPrefix) {
        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

        LimbDefinitionRegistry.register(typeId, head(idPrefix));
        LimbDefinitionRegistry.register(typeId, body(idPrefix));
        LimbDefinitionRegistry.register(typeId, rightArm(idPrefix));
        LimbDefinitionRegistry.register(typeId, leftArm(idPrefix));
        LimbDefinitionRegistry.register(typeId, rightLeg(idPrefix));
        LimbDefinitionRegistry.register(typeId, leftLeg(idPrefix));
    }

    private static LimbDefinition head(String prefix) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_head"),
            "head",
            LimbCategories.HEAD
        )
            .spawnAtEyeHeight()
            .fatal()
            .build();
    }

    private static LimbDefinition body(String prefix) {
        // No torso category yet — re-categorize once one exists in BLib.
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_body"),
            "body",
            LimbCategories.HEAD
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.25, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0))
            .build();
    }

    private static LimbDefinition rightArm(String prefix) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_right_arm"),
            "right_arm",
            LimbCategories.ARM
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.75, 0.0))
            .build();
    }

    private static LimbDefinition leftArm(String prefix) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_left_arm"),
            "left_arm",
            LimbCategories.ARM
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.75, 0.0))
            .build();
    }

    private static LimbDefinition rightLeg(String prefix) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_right_leg"),
            "right_leg",
            LimbCategories.LEG
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.3, 0.0))
            .build();
    }

    private static LimbDefinition leftLeg(String prefix) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_left_leg"),
            "left_leg",
            LimbCategories.LEG
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.3, 0.0))
            .build();
    }
}
