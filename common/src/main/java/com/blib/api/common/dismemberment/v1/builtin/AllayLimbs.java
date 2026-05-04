package com.blib.api.common.dismemberment.v1.builtin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import com.blib.api.common.dismemberment.v1.LimbCategories;
import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.mod.BLib;

/**
 * Registration helper for the vanilla allay. Its model has no legs and its parts live nested under
 * {@code root → root → head/body → right_arm/left_arm}, so dismemberment relies on the custom resolver registered in
 * {@code AlienLimbModelResolvers} to navigate the tree.
 */
public final class AllayLimbs {

    private AllayLimbs() {}

    public static void register() {
        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ALLAY);

        LimbDefinitionRegistry.register(typeId, head());
        LimbDefinitionRegistry.register(typeId, body());
        LimbDefinitionRegistry.register(typeId, rightArm());
        LimbDefinitionRegistry.register(typeId, leftArm());
    }

    private static LimbDefinition head() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("allay_head"),
            "head",
            LimbCategories.HEAD
        )
            .spawnAtEyeHeight()
            .fatal()
            .build();
    }

    private static LimbDefinition body() {
        // No torso category yet — re-categorize once one exists in BLib.
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("allay_body"),
            "body",
            LimbCategories.HEAD
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.25, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0))
            .build();
    }

    private static LimbDefinition rightArm() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("allay_right_arm"),
            "right_arm",
            LimbCategories.ARM
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.7, 0.0))
            .build();
    }

    private static LimbDefinition leftArm() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("allay_left_arm"),
            "left_arm",
            LimbCategories.ARM
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.7, 0.0))
            .build();
    }
}
