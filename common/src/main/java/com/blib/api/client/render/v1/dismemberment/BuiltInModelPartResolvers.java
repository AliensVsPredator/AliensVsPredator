package com.blib.api.client.render.v1.dismemberment;

import net.minecraft.client.model.AllayModel;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;

import com.blib.internal.mixin.MixinQuadrupedModel_Accessor;

/**
 * Registers {@link ModelPartResolver}s for the vanilla model base classes BLib supports out of the box. Called once at
 * client init.
 * <p>
 * The base-class resolvers handle whole model families ({@code HumanoidModel}, {@code QuadrupedModel}, etc.); a few
 * specific subclasses have part hierarchies that need bespoke navigation:
 * <ul>
 * <li>{@code WardenModel} / {@code AllayModel} — head/body/limbs nested deeper than direct children of root.</li>
 * <li>{@code WolfModel} / {@code HorseModel} / {@code FoxModel} / {@code ChickenModel} / {@code RabbitModel} — custom
 * skeletons with named parts kept in private fields. The fields are exposed through BLib's access widener / access
 * transformer so the resolvers below read them directly.</li>
 * </ul>
 * The resolver registry's class-hierarchy lookup means subclasses inherit registered resolvers — registering against
 * {@code HorseModel} also covers {@code ChestedHorseModel} (donkey/mule) and {@code UndeadHorseModel}.
 */
public final class BuiltInModelPartResolvers {

    private BuiltInModelPartResolvers() {}

    public static void register() {
        // HumanoidModel: explicit name → field map for head/hat/body/arms/legs.
        ModelPartResolverRegistry.register(HumanoidModel.class, BuiltInModelPartResolvers::resolveHumanoid);

        // QuadrupedModel: head/body/4 legs. Body/legs go through a mixin accessor since
        // the underlying fields are protected; head goes through HeadedModel.getHead().
        ModelPartResolverRegistry.register(QuadrupedModel.class, BuiltInModelPartResolvers::resolveQuadruped);

        // HierarchicalModel: walk the root tree by child name (matches LayerDefinition).
        ModelPartResolverRegistry.register(HierarchicalModel.class, BuiltInModelPartResolvers::resolveHierarchical);

        // HeadedModel: fallback for any model exposing a head via the interface but not covered above.
        ModelPartResolverRegistry.register(HeadedModel.class, BuiltInModelPartResolvers::resolveHeaded);

        // Specific subclasses with nested children that the HierarchicalModel resolver can't reach.
        ModelPartResolverRegistry.register(WardenModel.class, BuiltInModelPartResolvers::resolveWarden);
        ModelPartResolverRegistry.register(AllayModel.class, BuiltInModelPartResolvers::resolveAllay);

        // Models whose named parts live in private fields exposed by BLib's access widener / access transformer.
        ModelPartResolverRegistry.register(WolfModel.class, BuiltInModelPartResolvers::resolveWolf);
        ModelPartResolverRegistry.register(HorseModel.class, BuiltInModelPartResolvers::resolveHorse);
        ModelPartResolverRegistry.register(FoxModel.class, BuiltInModelPartResolvers::resolveFox);
        ModelPartResolverRegistry.register(ChickenModel.class, BuiltInModelPartResolvers::resolveChicken);
        ModelPartResolverRegistry.register(RabbitModel.class, BuiltInModelPartResolvers::resolveRabbit);
    }

    private static @Nullable ModelPart resolveHumanoid(HumanoidModel<?> model, String partName) {
        return switch (partName) {
            case "head" -> model.head;
            case "hat" -> model.hat;
            case "body" -> model.body;
            case "right_arm" -> model.rightArm;
            case "left_arm" -> model.leftArm;
            case "right_leg" -> model.rightLeg;
            case "left_leg" -> model.leftLeg;
            default -> null;
        };
    }

    private static @Nullable ModelPart resolveQuadruped(QuadrupedModel<?> model, String partName) {
        // QuadrupedModel itself doesn't implement HeadedModel (only some of its subclasses do, e.g. CowModel adds a
        // getHead() method but never declares the interface, and PigModel doesn't even add the method). Read every part
        // straight off the protected fields via the accessor so vanilla quadrupeds dismember consistently.
        var accessor = (MixinQuadrupedModel_Accessor) model;

        return switch (partName) {
            case "head" -> accessor.blib$getHead();
            case "body" -> accessor.blib$getBody();
            case "right_hind_leg" -> accessor.blib$getRightHindLeg();
            case "left_hind_leg" -> accessor.blib$getLeftHindLeg();
            case "right_front_leg" -> accessor.blib$getRightFrontLeg();
            case "left_front_leg" -> accessor.blib$getLeftFrontLeg();
            default -> null;
        };
    }

    private static @Nullable ModelPart resolveHierarchical(HierarchicalModel<?> model, String partName) {
        var root = model.root();
        return root.hasChild(partName) ? root.getChild(partName) : null;
    }

    private static @Nullable ModelPart resolveHeaded(HeadedModel model, String partName) {
        return "head".equals(partName) ? model.getHead() : null;
    }

    private static @Nullable ModelPart resolveWarden(WardenModel<?> model, String partName) {
        // root → bone → body → head/right_arm/left_arm; bone → right_leg/left_leg.
        var bone = model.root().getChild("bone");
        var body = bone.getChild("body");

        return switch (partName) {
            case "head" -> body.getChild("head");
            case "body" -> body;
            case "right_arm" -> body.getChild("right_arm");
            case "left_arm" -> body.getChild("left_arm");
            case "right_leg" -> bone.getChild("right_leg");
            case "left_leg" -> bone.getChild("left_leg");
            default -> null;
        };
    }

    private static @Nullable ModelPart resolveAllay(AllayModel model, String partName) {
        // root → root → head/body; body → right_arm/left_arm.
        var allayRoot = model.root().getChild("root");
        var body = allayRoot.getChild("body");

        return switch (partName) {
            case "head" -> allayRoot.getChild("head");
            case "body" -> body;
            case "right_arm" -> body.getChild("right_arm");
            case "left_arm" -> body.getChild("left_arm");
            default -> null;
        };
    }

    private static @Nullable ModelPart resolveWolf(WolfModel<?> model, String partName) {
        return switch (partName) {
            case "head" -> model.head;
            case "body" -> model.body;
            case "right_hind_leg" -> model.rightHindLeg;
            case "left_hind_leg" -> model.leftHindLeg;
            case "right_front_leg" -> model.rightFrontLeg;
            case "left_front_leg" -> model.leftFrontLeg;
            case "tail" -> model.tail;
            default -> null;
        };
    }

    private static @Nullable ModelPart resolveHorse(HorseModel<?> model, String partName) {
        // The head bone is named `head_parts` in HorseModel's LayerDefinition; the corresponding field is `headParts`.
        // Tail is technically a child of body but HorseModel keeps a top-level reference to it, which is what we use.
        return switch (partName) {
            case "head_parts" -> model.headParts;
            case "body" -> model.body;
            case "right_hind_leg" -> model.rightHindLeg;
            case "left_hind_leg" -> model.leftHindLeg;
            case "right_front_leg" -> model.rightFrontLeg;
            case "left_front_leg" -> model.leftFrontLeg;
            case "tail" -> model.tail;
            default -> null;
        };
    }

    private static @Nullable ModelPart resolveFox(FoxModel<?> model, String partName) {
        return switch (partName) {
            case "head" -> model.head;
            case "body" -> model.body;
            case "right_hind_leg" -> model.rightHindLeg;
            case "left_hind_leg" -> model.leftHindLeg;
            case "right_front_leg" -> model.rightFrontLeg;
            case "left_front_leg" -> model.leftFrontLeg;
            case "tail" -> model.tail;
            default -> null;
        };
    }

    private static @Nullable ModelPart resolveChicken(ChickenModel<?> model, String partName) {
        return switch (partName) {
            case "head" -> model.head;
            case "body" -> model.body;
            case "right_leg" -> model.rightLeg;
            case "left_leg" -> model.leftLeg;
            case "right_wing" -> model.rightWing;
            case "left_wing" -> model.leftWing;
            // Companions of the head limb (chicken keeps the beak/wattle as siblings of head).
            case "beak" -> model.beak;
            case "red_thing" -> model.redThing;
            default -> null;
        };
    }

    private static @Nullable ModelPart resolveRabbit(RabbitModel<?> model, String partName) {
        // We register only the "haunches" as the back-leg detachables (see RabbitLimbs); hind feet ride on the body
        // limb. If you ever want hind feet as their own limbs, add them to the access widener and switch entries here.
        return switch (partName) {
            case "head" -> model.head;
            case "body" -> model.body;
            case "right_haunch" -> model.rightHaunch;
            case "left_haunch" -> model.leftHaunch;
            case "right_front_leg" -> model.rightFrontLeg;
            case "left_front_leg" -> model.leftFrontLeg;
            case "tail" -> model.tail;
            default -> null;
        };
    }
}
