package com.blib.api.common.dismemberment.v1.builtin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import com.blib.api.common.dismemberment.v1.LimbCategories;
import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.mod.BLib;

/**
 * Shared registration helper for vanilla mobs whose model exposes the {@code QuadrupedModel}-style part naming (head,
 * body, four named hind/front legs). Used for cow/pig (which extend {@code QuadrupedModel}) and creeper (which extends
 * {@code HierarchicalModel} but uses the same {@code LayerDefinition} child names).
 */
public final class QuadrupedLimbs {

    private QuadrupedLimbs() {}

    public static void register(EntityType<?> entityType, String idPrefix) {
        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

        LimbDefinitionRegistry.register(typeId, head(idPrefix));
        LimbDefinitionRegistry.register(typeId, body(idPrefix));
        LimbDefinitionRegistry.register(typeId, leg(idPrefix, "right_hind_leg"));
        LimbDefinitionRegistry.register(typeId, leg(idPrefix, "left_hind_leg"));
        LimbDefinitionRegistry.register(typeId, leg(idPrefix, "right_front_leg"));
        LimbDefinitionRegistry.register(typeId, leg(idPrefix, "left_front_leg"));
    }

    private static LimbDefinition head(String prefix) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_head"),
            "head",
            LimbCategories.HEAD
        )
            .renderOffset(0.0, 0.25, 0.0)
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

    private static LimbDefinition leg(String prefix, String partName) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_" + partName),
            partName,
            LimbCategories.LEG
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.3, 0.0))
            .build();
    }
}
