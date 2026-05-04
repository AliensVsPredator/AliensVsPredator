package com.blib.api.client.render.v1.dismemberment;

import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;

import com.blib.internal.mixin.MixinQuadrupedModel_Accessor;

/**
 * Registers {@link ModelPartResolver}s for the vanilla model base classes BLib supports out of the box. Called once at
 * client init.
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
}
