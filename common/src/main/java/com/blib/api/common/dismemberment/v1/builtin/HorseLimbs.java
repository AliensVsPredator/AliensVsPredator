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
 * Shared registration helper for any mob that uses {@code HorseModel} (horse/donkey/mule/skeleton-horse/zombie-horse,
 * via ChestedHorseModel/UndeadHorseModel which both extend HorseModel). The head part is named {@code head_parts} (not
 * {@code head}) in the layer definition; the tail nests under {@code body} but the model still keeps it in a top-level
 * {@code tail} field.
 * <p>
 * Pass a {@link Tweaks} configurer through the three-arg overload to override individual parts.
 */
public final class HorseLimbs {

    private HorseLimbs() {}

    public static void register(EntityType<?> entityType, String idPrefix) {
        register(entityType, idPrefix, t -> {});
    }

    public static void register(EntityType<?> entityType, String idPrefix, Consumer<Tweaks> configurer) {
        var t = new Tweaks();
        configurer.accept(t);

        var typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

        LimbDefinitionRegistry.register(typeId, t.head.build(headBuilder(idPrefix)));
        LimbDefinitionRegistry.register(typeId, t.body.build(bodyBuilder(idPrefix)));
        LimbDefinitionRegistry.register(typeId, t.rightHindLeg.build(legBuilder(idPrefix, "right_hind_leg")));
        LimbDefinitionRegistry.register(typeId, t.leftHindLeg.build(legBuilder(idPrefix, "left_hind_leg")));
        LimbDefinitionRegistry.register(typeId, t.rightFrontLeg.build(legBuilder(idPrefix, "right_front_leg")));
        LimbDefinitionRegistry.register(typeId, t.leftFrontLeg.build(legBuilder(idPrefix, "left_front_leg")));
        LimbDefinitionRegistry.register(typeId, t.tail.build(tailBuilder(idPrefix)));
    }

    private static LimbDefinition.Builder headBuilder(String prefix) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_head"),
            "head_parts",
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

    private static LimbDefinition.Builder legBuilder(String prefix, String partName) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_" + partName),
            partName,
            LimbCategories.LEG
        )
            .renderRotation(90.0, 0.0, 0.0)
            .renderOffset(0.0, 0.125, 0.0)
            .spawnOffset(entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0));
    }

    private static LimbDefinition.Builder tailBuilder(String prefix) {
        return LimbDefinition.builder(
            BLib.MOD.resources().createLocation(prefix + "_tail"),
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

        public Tweaks legs(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightHindLeg(tweak).leftHindLeg(tweak).rightFrontLeg(tweak).leftFrontLeg(tweak);
        }

        public Tweaks hindLegs(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightHindLeg(tweak).leftHindLeg(tweak);
        }

        public Tweaks frontLegs(UnaryOperator<LimbDefinition.Builder> tweak) {
            return rightFrontLeg(tweak).leftFrontLeg(tweak);
        }
    }
}
