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
 * Registration helper for the vanilla wolf. WolfModel exposes head/body/4 legs/tail as named children of the model
 * root, resolved by the {@code WolfModel} resolver in
 * {@link com.blib.api.client.render.v1.dismemberment.BuiltInModelPartResolvers}.
 * <p>
 * Pass a {@link Tweaks} configurer through the one-arg overload to override individual parts.
 */
public final class WolfLimbs {

    private WolfLimbs() {}

    public static void register() {
        register(t -> {});
    }

    public static void register(Consumer<Tweaks> configurer) {
        var t = new Tweaks();
        configurer.accept(t);

        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WOLF);

        LimbDefinitionRegistry.register(typeId, t.head.build(headBuilder()));
        LimbDefinitionRegistry.register(typeId, t.body.build(bodyBuilder()));
        LimbDefinitionRegistry.register(typeId, t.rightHindLeg.build(legBuilder("right_hind_leg")));
        LimbDefinitionRegistry.register(typeId, t.leftHindLeg.build(legBuilder("left_hind_leg")));
        LimbDefinitionRegistry.register(typeId, t.rightFrontLeg.build(legBuilder("right_front_leg")));
        LimbDefinitionRegistry.register(typeId, t.leftFrontLeg.build(legBuilder("left_front_leg")));
        LimbDefinitionRegistry.register(typeId, t.tail.build(tailBuilder()));
    }

    private static LimbDefinition.Builder headBuilder() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("wolf_head"),
            "head",
            LimbCategories.HEAD
        )
            .renderOffset(0.0, 0.25, 0.0)
            .spawnAtEyeHeight()
            .fatal();
    }

    private static LimbDefinition.Builder bodyBuilder() {
        // No torso category yet — re-categorize once one exists in BLib.
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("wolf_body"),
            "body",
            LimbCategories.HEAD
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.25, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0));
    }

    private static LimbDefinition.Builder legBuilder(String partName) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("wolf_" + partName),
            partName,
            LimbCategories.LEG
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.4, 0.0));
    }

    private static LimbDefinition.Builder tailBuilder() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("wolf_tail"),
            "tail",
            LimbCategories.TAIL
        )
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0));
    }

    public static final class Tweaks {

        public final LimbTweakSlot head = new LimbTweakSlot();

        public final LimbTweakSlot body = new LimbTweakSlot();

        public final LimbTweakSlot rightHindLeg = new LimbTweakSlot();

        public final LimbTweakSlot leftHindLeg = new LimbTweakSlot();

        public final LimbTweakSlot rightFrontLeg = new LimbTweakSlot();

        public final LimbTweakSlot leftFrontLeg = new LimbTweakSlot();

        public final LimbTweakSlot tail = new LimbTweakSlot();

        public Tweaks head(UnaryOperator<LimbDefinition.Builder> tweak) {
            head.append(tweak);
            return this;
        }

        public Tweaks body(UnaryOperator<LimbDefinition.Builder> tweak) {
            body.append(tweak);
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

        public Tweaks rightFrontLeg(UnaryOperator<LimbDefinition.Builder> tweak) {
            rightFrontLeg.append(tweak);
            return this;
        }

        public Tweaks leftFrontLeg(UnaryOperator<LimbDefinition.Builder> tweak) {
            leftFrontLeg.append(tweak);
            return this;
        }

        public Tweaks tail(UnaryOperator<LimbDefinition.Builder> tweak) {
            tail.append(tweak);
            return this;
        }

        /** Apply the same tweak to all four legs. */
        public Tweaks legs(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightHindLeg(tweak).leftHindLeg(tweak).rightFrontLeg(tweak).leftFrontLeg(tweak);
        }

        /** Apply the same tweak to both hind legs. */
        public Tweaks hindLegs(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightHindLeg(tweak).leftHindLeg(tweak);
        }

        /** Apply the same tweak to both front legs. */
        public Tweaks frontLegs(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightFrontLeg(tweak).leftFrontLeg(tweak);
        }
    }
}
