package com.blib.api.common.dismemberment.v1.builtin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import com.blib.api.common.dismemberment.v1.LimbCategories;
import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.mod.BLib;

/**
 * Shared registration helper for any mob that uses {@code HorseModel} (horse/donkey/mule/skeleton-horse/zombie-horse,
 * via ChestedHorseModel/UndeadHorseModel which both extend HorseModel). The head part is named {@code head_parts} (not
 * {@code head}) in the layer definition; the tail nests under {@code body} but the model still keeps it in a top-level
 * {@code tail} field, so the reflective resolver registered for HorseModel finds it directly.
 */
public final class HorseLimbs {

    private static final String[] LEG_PART_NAMES = {
        "right_hind_leg",
        "left_hind_leg",
        "right_front_leg",
        "left_front_leg"
    };

    private HorseLimbs() {}

    public static void register(EntityType<?> entityType, String idPrefix) {
        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

        LimbDefinitionRegistry.register(typeId, head(idPrefix));
        LimbDefinitionRegistry.register(typeId, body(idPrefix));

        for (var legPartName : LEG_PART_NAMES) {
            LimbDefinitionRegistry.register(typeId, leg(idPrefix, legPartName));
        }

        LimbDefinitionRegistry.register(typeId, tail(idPrefix));
    }

    private static LimbDefinition head(String prefix) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_head"),
            "head_parts",
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

    private static LimbDefinition leg(String prefix, String partName) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_" + partName),
            partName,
            LimbCategories.LEG
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0))
            .build();
    }

    private static LimbDefinition tail(String prefix) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_tail"),
            "tail",
            LimbCategories.TAIL
        )
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0))
            .build();
    }
}
