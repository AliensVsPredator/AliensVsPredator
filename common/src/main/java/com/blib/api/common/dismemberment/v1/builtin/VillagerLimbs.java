package com.blib.api.common.dismemberment.v1.builtin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import com.blib.api.common.dismemberment.v1.LimbCategories;
import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.mod.BLib;

/**
 * Registration helper for mobs whose model extends vanilla {@code VillagerModel} (the villager itself, witches,
 * wandering traders). Unlike {@link HumanoidLimbs}, the villager skeleton is a {@code HierarchicalModel} whose root has
 * a single unified {@code arms} part instead of the Steve-style {@code right_arm}/{@code left_arm} split — registering
 * through {@link HumanoidLimbs} would produce ARM limb defs the client renderer can't resolve. This helper registers an
 * {@code arms} limb that matches the actual model structure, keeping the standard head/body/legs definitions otherwise.
 */
public final class VillagerLimbs {

    private VillagerLimbs() {}

    public static void register(EntityType<?> entityType, String idPrefix) {
        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

        LimbDefinitionRegistry.register(typeId, head(idPrefix));
        LimbDefinitionRegistry.register(typeId, body(idPrefix));
        LimbDefinitionRegistry.register(typeId, arms(idPrefix));
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

    private static LimbDefinition arms(String prefix) {
        // VillagerModel arms live under a single unified "arms" ModelPart (both forearms + shoulder bridge), so a
        // single ARM limb spawns instead of two — matches the geometry the model actually exposes.
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_arms"),
            "arms",
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
