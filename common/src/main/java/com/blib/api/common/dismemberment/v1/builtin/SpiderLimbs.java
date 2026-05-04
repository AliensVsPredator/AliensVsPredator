package com.blib.api.common.dismemberment.v1.builtin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import com.blib.api.common.dismemberment.v1.LimbCategories;
import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.mod.BLib;

/**
 * Registration helper for vanilla spider/cave-spider models. Both share {@code SpiderModel}; their root has the head
 * plus eight named leg parts ({@code right/left + hind/middle_hind/middle_front/front_leg}) as direct children, all
 * resolvable through the default {@code HierarchicalModel} resolver. Leg pivots sit near the body, so the leg limb
 * fragments use the standard quadruped-style render rotation.
 */
public final class SpiderLimbs {

    private static final String[] LEG_PART_NAMES = {
        "right_hind_leg",
        "left_hind_leg",
        "right_middle_hind_leg",
        "left_middle_hind_leg",
        "right_middle_front_leg",
        "left_middle_front_leg",
        "right_front_leg",
        "left_front_leg"
    };

    private SpiderLimbs() {}

    public static void register(EntityType<?> entityType, String idPrefix) {
        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

        LimbDefinitionRegistry.register(typeId, head(idPrefix));

        for (var legPartName : LEG_PART_NAMES) {
            LimbDefinitionRegistry.register(typeId, leg(idPrefix, legPartName));
        }
    }

    private static LimbDefinition head(String prefix) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_head"),
            "head",
            LimbCategories.HEAD
        )
            // Spider's hitbox is wide and short; eye height is roughly the head pivot, so default eye-height anchor
            // is fine.
            .spawnAtEyeHeight()
            .fatal()
            .build();
    }

    private static LimbDefinition leg(String prefix, String partName) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_" + partName),
            partName,
            LimbCategories.LEG
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            // Spider hitbox is short; spawn a touch above the body's vertical center.
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0))
            .build();
    }
}
