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
 * Registration helper for the vanilla chicken. ChickenModel exposes head/body, two legs, and two wings as named
 * children of the root. Wings are categorized as ARM since they're the chicken's upper-limb analog.
 * <p>
 * Pass a {@link Tweaks} configurer through the one-arg overload to override individual parts.
 */
public final class ChickenLimbs {

    private ChickenLimbs() {}

    public static void register() {
        register(t -> {});
    }

    public static void register(Consumer<Tweaks> configurer) {
        var t = new Tweaks();
        configurer.accept(t);

        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.CHICKEN);

        LimbDefinitionRegistry.register(typeId, t.head.build(headBuilder()));
        LimbDefinitionRegistry.register(typeId, t.body.build(bodyBuilder()));
        LimbDefinitionRegistry.register(typeId, t.rightLeg.build(legBuilder("right_leg")));
        LimbDefinitionRegistry.register(typeId, t.leftLeg.build(legBuilder("left_leg")));
        LimbDefinitionRegistry.register(typeId, t.rightWing.build(wingBuilder("right_wing")));
        LimbDefinitionRegistry.register(typeId, t.leftWing.build(wingBuilder("left_wing")));
    }

    private static LimbDefinition.Builder headBuilder() {
        // ChickenModel keeps the beak and red_thing (wattle) as siblings of `head` rather than children, so they don't
        // ride the head's subtree on their own. Listing them as companions hides them on the corpse and renders them
        // alongside the head limb fragment.
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("chicken_head"),
            "head",
            LimbCategories.HEAD
        )
            .companions("beak", "red_thing")
            .spawnAtEyeHeight()
            .fatal();
    }

    private static LimbDefinition.Builder bodyBuilder() {
        // No torso category yet — re-categorize once one exists in BLib.
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("chicken_body"),
            "body",
            LimbCategories.HEAD
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.25, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0));
    }

    private static LimbDefinition.Builder legBuilder(String partName) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("chicken_" + partName),
            partName,
            LimbCategories.LEG
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.3, 0.0));
    }

    private static LimbDefinition.Builder wingBuilder(String partName) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("chicken_" + partName),
            partName,
            LimbCategories.ARM
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.6, 0.0));
    }

    public static final class Tweaks {

        public final LimbTweakSlot head = new LimbTweakSlot();

        public final LimbTweakSlot body = new LimbTweakSlot();

        public final LimbTweakSlot rightLeg = new LimbTweakSlot();

        public final LimbTweakSlot leftLeg = new LimbTweakSlot();

        public final LimbTweakSlot rightWing = new LimbTweakSlot();

        public final LimbTweakSlot leftWing = new LimbTweakSlot();

        public Tweaks head(UnaryOperator<LimbDefinition.Builder> tweak) {
            head.append(tweak);
            return this;
        }

        public Tweaks body(UnaryOperator<LimbDefinition.Builder> tweak) {
            body.append(tweak);
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

        public Tweaks rightWing(UnaryOperator<LimbDefinition.Builder> tweak) {
            rightWing.append(tweak);
            return this;
        }

        public Tweaks leftWing(UnaryOperator<LimbDefinition.Builder> tweak) {
            leftWing.append(tweak);
            return this;
        }

        public Tweaks legs(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightLeg(tweak).leftLeg(tweak);
        }

        public Tweaks wings(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightWing(tweak).leftWing(tweak);
        }
    }
}
