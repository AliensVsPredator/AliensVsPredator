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
 * Registration helper for the vanilla rabbit. The rabbit's hind legs are split into two top-level pieces — a "haunch"
 * (the upper thigh) and a "hind_foot" (the lower foot) — that sit as siblings under the model root rather than as a
 * single chained leg. We register only the haunches as the back-leg detachables; the feet stay attached as small stumps
 * which reads better visually than detaching one half and orphaning the other.
 * <p>
 * Pass a {@link Tweaks} configurer through the one-arg overload to override individual parts.
 */
public final class RabbitLimbs {

    private RabbitLimbs() {}

    public static void register() {
        register(t -> {});
    }

    public static void register(Consumer<Tweaks> configurer) {
        var t = new Tweaks();
        configurer.accept(t);

        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.RABBIT);

        LimbDefinitionRegistry.register(typeId, t.head.build(headBuilder()));
        LimbDefinitionRegistry.register(typeId, t.body.build(bodyBuilder()));
        LimbDefinitionRegistry.register(typeId, t.rightHaunch.build(legBuilder("right_haunch")));
        LimbDefinitionRegistry.register(typeId, t.leftHaunch.build(legBuilder("left_haunch")));
        LimbDefinitionRegistry.register(typeId, t.rightFrontLeg.build(legBuilder("right_front_leg")));
        LimbDefinitionRegistry.register(typeId, t.leftFrontLeg.build(legBuilder("left_front_leg")));
        LimbDefinitionRegistry.register(typeId, t.tail.build(tailBuilder()));
    }

    private static LimbDefinition.Builder headBuilder() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("rabbit_head"),
            "head",
            LimbCategories.HEAD
        )
            .spawnAtEyeHeight()
            .fatal();
    }

    private static LimbDefinition.Builder bodyBuilder() {
        // No torso category yet — re-categorize once one exists in BLib.
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("rabbit_body"),
            "body",
            LimbCategories.HEAD
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.25, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0));
    }

    private static LimbDefinition.Builder legBuilder(String partName) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("rabbit_" + partName),
            partName,
            LimbCategories.LEG
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.3, 0.0));
    }

    private static LimbDefinition.Builder tailBuilder() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("rabbit_tail"),
            "tail",
            LimbCategories.TAIL
        )
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0));
    }

    public static final class Tweaks {

        public final LimbTweakSlot head = new LimbTweakSlot();

        public final LimbTweakSlot body = new LimbTweakSlot();

        public final LimbTweakSlot rightHaunch = new LimbTweakSlot();

        public final LimbTweakSlot leftHaunch = new LimbTweakSlot();

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

        public Tweaks rightHaunch(UnaryOperator<LimbDefinition.Builder> tweak) {
            rightHaunch.append(tweak);
            return this;
        }

        public Tweaks leftHaunch(UnaryOperator<LimbDefinition.Builder> tweak) {
            leftHaunch.append(tweak);
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

        public Tweaks haunches(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightHaunch(tweak).leftHaunch(tweak);
        }

        public Tweaks frontLegs(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightFrontLeg(tweak).leftFrontLeg(tweak);
        }

        public Tweaks legs(UnaryOperator<LimbDefinition.Builder> tweak) {
            return haunches(tweak).frontLegs(tweak);
        }
    }
}
