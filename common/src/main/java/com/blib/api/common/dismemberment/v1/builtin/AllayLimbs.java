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
 * Registration helper for the vanilla allay. Its model has no legs and its parts live nested under
 * {@code root → root → head/body → right_arm/left_arm}, resolved through the custom AllayModel resolver registered in
 * {@link com.blib.api.client.render.v1.dismemberment.BuiltInModelPartResolvers}.
 * <p>
 * Pass a {@link Tweaks} configurer through the one-arg overload to override individual parts.
 */
public final class AllayLimbs {

    private AllayLimbs() {}

    public static void register() {
        register(t -> {});
    }

    public static void register(Consumer<Tweaks> configurer) {
        var t = new Tweaks();
        configurer.accept(t);

        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ALLAY);

        LimbDefinitionRegistry.register(typeId, t.head.build(headBuilder()));
        LimbDefinitionRegistry.register(typeId, t.body.build(bodyBuilder()));
        LimbDefinitionRegistry.register(typeId, t.rightArm.build(armBuilder("right_arm")));
        LimbDefinitionRegistry.register(typeId, t.leftArm.build(armBuilder("left_arm")));
    }

    private static LimbDefinition.Builder headBuilder() {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("allay_head"),
            "head",
            LimbCategories.HEAD
        )
            .spawnAtEyeHeight()
            .fatal();
    }

    private static LimbDefinition.Builder bodyBuilder() {
        // No torso category yet — re-categorize once one exists in BLib.
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("allay_body"),
            "body",
            LimbCategories.HEAD
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.25, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0));
    }

    private static LimbDefinition.Builder armBuilder(String partName) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation("allay_" + partName),
            partName,
            LimbCategories.ARM
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.7, 0.0));
    }

    public static final class Tweaks {

        public final LimbTweakSlot head = new LimbTweakSlot();

        public final LimbTweakSlot body = new LimbTweakSlot();

        public final LimbTweakSlot rightArm = new LimbTweakSlot();

        public final LimbTweakSlot leftArm = new LimbTweakSlot();

        public Tweaks head(UnaryOperator<LimbDefinition.Builder> tweak) {
            head.append(tweak);
            return this;
        }

        public Tweaks body(UnaryOperator<LimbDefinition.Builder> tweak) {
            body.append(tweak);
            return this;
        }

        public Tweaks rightArm(UnaryOperator<LimbDefinition.Builder> tweak) {
            rightArm.append(tweak);
            return this;
        }

        public Tweaks leftArm(UnaryOperator<LimbDefinition.Builder> tweak) {
            leftArm.append(tweak);
            return this;
        }

        /** Apply the same tweak to both arms. */
        public Tweaks arms(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightArm(tweak).leftArm(tweak);
        }
    }
}
