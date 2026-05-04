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
 * Registration helper for vanilla spider/cave-spider models. Both share {@code SpiderModel}; their root has the head
 * plus eight named leg parts ({@code right/left + hind/middle_hind/middle_front/front_leg}) as direct children, all
 * resolvable through the default {@code HierarchicalModel} resolver. Leg pivots sit near the body, so the leg limb
 * fragments use the standard quadruped-style render rotation.
 * <p>
 * Pass a {@link Tweaks} configurer through the three-arg overload to override individual parts.
 */
public final class SpiderLimbs {

    private SpiderLimbs() {}

    public static void register(EntityType<?> entityType, String idPrefix) {
        register(entityType, idPrefix, t -> {});
    }

    public static void register(EntityType<?> entityType, String idPrefix, Consumer<Tweaks> configurer) {
        var t = new Tweaks();
        configurer.accept(t);

        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

        LimbDefinitionRegistry.register(typeId, t.head.build(headBuilder(idPrefix)));
        LimbDefinitionRegistry.register(typeId, t.rightHindLeg.build(legBuilder(idPrefix, "right_hind_leg")));
        LimbDefinitionRegistry.register(typeId, t.leftHindLeg.build(legBuilder(idPrefix, "left_hind_leg")));
        LimbDefinitionRegistry.register(typeId, t.rightMiddleHindLeg.build(legBuilder(idPrefix, "right_middle_hind_leg")));
        LimbDefinitionRegistry.register(typeId, t.leftMiddleHindLeg.build(legBuilder(idPrefix, "left_middle_hind_leg")));
        LimbDefinitionRegistry.register(typeId, t.rightMiddleFrontLeg.build(legBuilder(idPrefix, "right_middle_front_leg")));
        LimbDefinitionRegistry.register(typeId, t.leftMiddleFrontLeg.build(legBuilder(idPrefix, "left_middle_front_leg")));
        LimbDefinitionRegistry.register(typeId, t.rightFrontLeg.build(legBuilder(idPrefix, "right_front_leg")));
        LimbDefinitionRegistry.register(typeId, t.leftFrontLeg.build(legBuilder(idPrefix, "left_front_leg")));
    }

    private static LimbDefinition.Builder headBuilder(String prefix) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_head"),
            "head",
            LimbCategories.HEAD
        )
            // Spider's hitbox is wide and short; eye height is roughly the head pivot, so default eye-height anchor
            // is fine.
            .spawnAtEyeHeight()
            .fatal();
    }

    private static LimbDefinition.Builder legBuilder(String prefix, String partName) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_" + partName),
            partName,
            LimbCategories.LEG
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            // Spider hitbox is short; spawn a touch above the body's vertical center.
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0));
    }

    public static final class Tweaks {

        public final LimbTweakSlot head = new LimbTweakSlot();

        public final LimbTweakSlot rightHindLeg = new LimbTweakSlot();

        public final LimbTweakSlot leftHindLeg = new LimbTweakSlot();

        public final LimbTweakSlot rightMiddleHindLeg = new LimbTweakSlot();

        public final LimbTweakSlot leftMiddleHindLeg = new LimbTweakSlot();

        public final LimbTweakSlot rightMiddleFrontLeg = new LimbTweakSlot();

        public final LimbTweakSlot leftMiddleFrontLeg = new LimbTweakSlot();

        public final LimbTweakSlot rightFrontLeg = new LimbTweakSlot();

        public final LimbTweakSlot leftFrontLeg = new LimbTweakSlot();

        public Tweaks head(UnaryOperator<LimbDefinition.Builder> tweak) {
            head.append(tweak);
            return this;
        }

        public Tweaks rightHindLeg(UnaryOperator<LimbDefinition.Builder> tweak) {
            rightHindLeg.append(tweak);
            return this;
        }

        public Tweaks leftHindLeg(UnaryOperator<LimbDefinition.Builder> tweak) {
            leftHindLeg.append(tweak);
            return this;
        }

        public Tweaks rightMiddleHindLeg(UnaryOperator<LimbDefinition.Builder> tweak) {
            rightMiddleHindLeg.append(tweak);
            return this;
        }

        public Tweaks leftMiddleHindLeg(UnaryOperator<LimbDefinition.Builder> tweak) {
            leftMiddleHindLeg.append(tweak);
            return this;
        }

        public Tweaks rightMiddleFrontLeg(UnaryOperator<LimbDefinition.Builder> tweak) {
            rightMiddleFrontLeg.append(tweak);
            return this;
        }

        public Tweaks leftMiddleFrontLeg(UnaryOperator<LimbDefinition.Builder> tweak) {
            leftMiddleFrontLeg.append(tweak);
            return this;
        }

        public Tweaks rightFrontLeg(UnaryOperator<LimbDefinition.Builder> tweak) {
            rightFrontLeg.append(tweak);
            return this;
        }

        public Tweaks leftFrontLeg(UnaryOperator<LimbDefinition.Builder> tweak) {
            leftFrontLeg.append(tweak);
            return this;
        }

        /** Apply the same tweak to all eight legs. */
        public Tweaks legs(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightHindLeg(tweak)
                .leftHindLeg(tweak)
                .rightMiddleHindLeg(tweak)
                .leftMiddleHindLeg(tweak)
                .rightMiddleFrontLeg(tweak)
                .leftMiddleFrontLeg(tweak)
                .rightFrontLeg(tweak)
                .leftFrontLeg(tweak);
        }
    }
}
