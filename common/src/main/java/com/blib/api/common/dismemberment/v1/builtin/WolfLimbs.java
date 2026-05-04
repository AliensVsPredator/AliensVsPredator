package com.blib.api.common.dismemberment.v1.builtin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import com.blib.api.common.dismemberment.v1.LimbCategories;
import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.mod.BLib;

/**
 * Registration helper for the vanilla wolf. WolfModel exposes head/body/4 legs/tail as named children of the model
 * root; resolution goes through the reflective resolver registered in {@code AlienLimbModelResolvers} since the model
 * doesn't keep the root accessible.
 */
public final class WolfLimbs {

    private static final String[] LEG_PART_NAMES = {
        "right_hind_leg",
        "left_hind_leg",
        "right_front_leg",
        "left_front_leg"
    };

    private WolfLimbs() {}

    public static void register() {
        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WOLF);

        LimbDefinitionRegistry.register(typeId, head());
        LimbDefinitionRegistry.register(typeId, body());

        for (var legPartName : LEG_PART_NAMES) {
            LimbDefinitionRegistry.register(typeId, leg(legPartName));
        }

        LimbDefinitionRegistry.register(typeId, tail());
    }

    private static LimbDefinition head() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("wolf_head"),
            "head",
            LimbCategories.HEAD
        )
            .renderOffset(0.0, 0.25, 0.0)
            .spawnAtEyeHeight()
            .fatal()
            .build();
    }

    private static LimbDefinition body() {
        // No torso category yet — re-categorize once one exists in BLib.
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("wolf_body"),
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
            BLib.MOD.resources().createLocation("wolf_" + partName),
            partName,
            LimbCategories.LEG
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.4, 0.0))
            .build();
    }

    private static LimbDefinition tail() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("wolf_tail"),
            "tail",
            LimbCategories.TAIL
        )
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0))
            .build();
    }
}
