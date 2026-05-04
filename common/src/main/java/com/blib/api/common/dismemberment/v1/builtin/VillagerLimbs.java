package com.blib.api.common.dismemberment.v1.builtin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.blib.api.common.dismemberment.v1.LimbCategories;
import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.mod.BLib;

/**
 * Registration helper for mobs whose model extends vanilla {@code VillagerModel} (the villager itself, witches,
 * wandering traders). VillagerModel skeletons are {@code HierarchicalModel}s with a single unified {@code arms} part
 * instead of the Steve-style {@code right_arm}/{@code left_arm} split — registering through {@link HumanoidLimbs} would
 * produce ARM limb defs the client renderer can't resolve. This helper registers an {@code arms} limb that matches the
 * actual model structure, keeping the standard head/body/legs definitions otherwise.
 * <p>
 * Pass a {@link Tweaks} configurer through the three-arg overload to override individual parts.
 */
public final class VillagerLimbs {

    private VillagerLimbs() {}

    public static void register(EntityType<?> entityType, String idPrefix) {
        register(entityType, idPrefix, t -> {});
    }

    public static void register(EntityType<?> entityType, String idPrefix, Consumer<Tweaks> configurer) {
        var t = new Tweaks();
        configurer.accept(t);

        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

        LimbDefinitionRegistry.register(typeId, t.head.build(headBuilder(idPrefix)));
        LimbDefinitionRegistry.register(typeId, t.body.build(bodyBuilder(idPrefix)));
        LimbDefinitionRegistry.register(typeId, t.arms.build(armsBuilder(idPrefix)));
        LimbDefinitionRegistry.register(typeId, t.rightLeg.build(legBuilder(idPrefix, "right_leg")));
        LimbDefinitionRegistry.register(typeId, t.leftLeg.build(legBuilder(idPrefix, "left_leg")));
    }

    private static LimbDefinition.Builder headBuilder(String prefix) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_head"),
            "head",
            LimbCategories.HEAD
        )
            .spawnAtEyeHeight()
            .fatal();
    }

    private static LimbDefinition.Builder bodyBuilder(String prefix) {
        // No torso category yet — re-categorize once one exists in BLib.
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_body"),
            "body",
            LimbCategories.HEAD
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.25, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0));
    }

    private static LimbDefinition.Builder armsBuilder(String prefix) {
        // VillagerModel arms live under a single unified "arms" ModelPart (both forearms + shoulder bridge), so a
        // single ARM limb spawns instead of two — matches the geometry the model actually exposes.
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_arms"),
            "arms",
            LimbCategories.ARM
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.75, 0.0));
    }

    private static LimbDefinition.Builder legBuilder(String prefix, String partName) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_" + partName),
            partName,
            LimbCategories.LEG
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.3, 0.0));
    }

    public static final class Tweaks {

        public final LimbTweakSlot head = new LimbTweakSlot();

        public final LimbTweakSlot body = new LimbTweakSlot();

        public final LimbTweakSlot arms = new LimbTweakSlot();

        public final LimbTweakSlot rightLeg = new LimbTweakSlot();

        public final LimbTweakSlot leftLeg = new LimbTweakSlot();

        public Tweaks head(UnaryOperator<LimbDefinition.Builder> tweak) {
            head.append(tweak);
            return this;
        }

        public Tweaks body(UnaryOperator<LimbDefinition.Builder> tweak) {
            body.append(tweak);
            return this;
        }

        public Tweaks arms(UnaryOperator<LimbDefinition.Builder> tweak) {
            arms.append(tweak);
            return this;
        }

        public Tweaks rightLeg(UnaryOperator<LimbDefinition.Builder> tweak) {
            rightLeg.append(tweak);
            return this;
        }

        public Tweaks leftLeg(UnaryOperator<LimbDefinition.Builder> tweak) {
            leftLeg.append(tweak);
            return this;
        }

        /** Apply the same tweak to both legs. */
        public Tweaks legs(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightLeg(tweak).leftLeg(tweak);
        }
    }
}
