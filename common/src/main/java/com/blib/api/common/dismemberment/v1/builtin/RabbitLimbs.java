package com.blib.api.common.dismemberment.v1.builtin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import com.blib.api.common.dismemberment.v1.LimbCategories;
import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.mod.BLib;

/**
 * Registration helper for the vanilla rabbit. The rabbit's hind legs are split into two top-level pieces — a "haunch"
 * (the upper thigh) and a "hind_foot" (the lower foot) — that sit as siblings under the model root rather than as a
 * single chained leg. We register only the haunches as the back-leg detachables; the feet stay attached as small stumps
 * which reads better visually than detaching one half and orphaning the other.
 */
public final class RabbitLimbs {

    private RabbitLimbs() {}

    public static void register() {
        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.RABBIT);

        LimbDefinitionRegistry.register(typeId, head());
        LimbDefinitionRegistry.register(typeId, body());
        LimbDefinitionRegistry.register(typeId, leg("right_haunch"));
        LimbDefinitionRegistry.register(typeId, leg("left_haunch"));
        LimbDefinitionRegistry.register(typeId, leg("right_front_leg"));
        LimbDefinitionRegistry.register(typeId, leg("left_front_leg"));
        LimbDefinitionRegistry.register(typeId, tail());
    }

    private static LimbDefinition head() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("rabbit_head"),
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
            BLib.MOD.resources().createLocation("rabbit_body"),
            "body",
            LimbCategories.HEAD
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.25, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0))
            .build();
    }

    private static LimbDefinition leg(String partName) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("rabbit_" + partName),
            partName,
            LimbCategories.LEG
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.3, 0.0))
            .build();
    }

    private static LimbDefinition tail() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("rabbit_tail"),
            "tail",
            LimbCategories.TAIL
        )
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0))
            .build();
    }
}
