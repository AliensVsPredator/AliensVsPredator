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
 * Shared registration helper for vanilla mobs whose model is built on {@code HumanoidModel}. Builds the six standard
 * limb definitions (head, body, right/left arm, right/left leg) for the given entity type using a unique id prefix so
 * the registry stays mob-scoped, and applies the same render rotation/offset values that were dialed in on the zombie.
 * <p>
 * The two-arg {@link #register(EntityType, String)} is the common case. Mobs with off-the-shelf proportions can pass a
 * {@link Tweaks} configurer through the three-arg overload to override individual parts.
 */
public final class HumanoidLimbs {

    private HumanoidLimbs() {}

    public static void register(EntityType<?> entityType, String idPrefix) {
        register(entityType, idPrefix, t -> {});
    }

    public static void register(EntityType<?> entityType, String idPrefix, Consumer<Tweaks> configurer) {
        var t = new Tweaks();
        configurer.accept(t);

        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

        LimbDefinitionRegistry.register(typeId, t.head.build(headBuilder(idPrefix)));
        LimbDefinitionRegistry.register(typeId, t.body.build(bodyBuilder(idPrefix)));
        LimbDefinitionRegistry.register(typeId, t.rightArm.build(armBuilder(idPrefix, "right_arm")));
        LimbDefinitionRegistry.register(typeId, t.leftArm.build(armBuilder(idPrefix, "left_arm")));
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

    private static LimbDefinition.Builder armBuilder(String prefix, String partName) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_" + partName),
            partName,
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

        public final LimbTweakSlot rightArm = new LimbTweakSlot();

        public final LimbTweakSlot leftArm = new LimbTweakSlot();

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

        public Tweaks rightArm(UnaryOperator<LimbDefinition.Builder> tweak) {
            rightArm.append(tweak);
            return this;
        }

        public Tweaks leftArm(UnaryOperator<LimbDefinition.Builder> tweak) {
            leftArm.append(tweak);
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

        /** Apply the same tweak to both arms. */
        public Tweaks arms(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightArm(tweak).leftArm(tweak);
        }

        /** Apply the same tweak to both legs. */
        public Tweaks legs(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightLeg(tweak).leftLeg(tweak);
        }
    }
}
