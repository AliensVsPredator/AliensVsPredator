package com.blib.api.common.dismemberment.v1.builtin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import com.blib.api.common.dismemberment.v1.LimbCategories;
import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.mod.BLib;

/**
 * Registration helper for the vanilla snow golem. Its model has no legs (the lower snow ball is the implicit base) and
 * its torso is named {@code upper_body} rather than {@code body}; arms attach to the root, not the torso. All parts
 * resolve as direct children of the model root.
 */
public final class SnowGolemLimbs {

    private SnowGolemLimbs() {}

    public static void register() {
        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.SNOW_GOLEM);

        LimbDefinitionRegistry.register(typeId, head());
        LimbDefinitionRegistry.register(typeId, upperBody());
        LimbDefinitionRegistry.register(typeId, rightArm());
        LimbDefinitionRegistry.register(typeId, leftArm());
    }

    private static LimbDefinition head() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("snow_golem_head"),
            "head",
            LimbCategories.HEAD
        )
            .spawnAtEyeHeight()
            .fatal()
            .build();
    }

    private static LimbDefinition upperBody() {
        // No torso category yet — re-categorize once one exists in BLib.
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("snow_golem_upper_body"),
            "upper_body",
            LimbCategories.HEAD
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.25, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0))
            .build();
    }

    private static LimbDefinition rightArm() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("snow_golem_right_arm"),
            "right_arm",
            LimbCategories.ARM
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.75, 0.0))
            .build();
    }

    private static LimbDefinition leftArm() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("snow_golem_left_arm"),
            "left_arm",
            LimbCategories.ARM
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.75, 0.0))
            .build();
    }
}
