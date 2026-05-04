package com.blib.api.client.render.v1.dismemberment;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.dismemberment.v1.entity.DismemberedLimbEntity;
import com.blib.internal.mixin.MixinEntityRenderDispatcher_Accessor;
import com.blib.internal.mixin.MixinLivingEntityRenderer_Accessor;

/**
 * Renders a single {@link ModelPart} of a vanilla mob in isolation, used when a {@link DismemberedLimbEntity}
 * originates from an entity whose visual is a vanilla {@link EntityModel} rather than a BLib geo bake.
 * <p>
 * The source entity's model is looked up via the {@link net.minecraft.client.renderer.entity.EntityRenderDispatcher}
 * cache, the named child of its root part is borrowed, and its local pose is temporarily zeroed so the fragment doesn't
 * inherit any animation state set during the source mob's last frame.
 */
public final class VanillaLimbRenderer {

    private VanillaLimbRenderer() {}

    public static void render(
        DismemberedLimbEntity limb,
        PoseStack poseStack,
        VertexConsumer vertexConsumer,
        int packedLight,
        int packedOverlay
    ) {
        var rootBoneName = limb.getRootBoneName();
        var ghost = limb.getOrCreateGhost();

        if (rootBoneName == null || rootBoneName.isEmpty() || ghost == null) {
            return;
        }

        var part = resolveModelPart(ghost, rootBoneName);

        if (part == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - limb.getYRot()));

        var renderRotation = limb.getLimbRenderRotation();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) renderRotation.z));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) renderRotation.y));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) renderRotation.x));

        var renderOffset = limb.getLimbRenderOffset();
        poseStack.translate(renderOffset.x, renderOffset.y, renderOffset.z);

        // Vanilla entity models are authored upside-down relative to world axes;
        // LivingEntityRenderer applies this scale before rendering the model, so we
        // do the same here when isolating a single part.
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        // Borrow the part: zero its local pose so the fragment doesn't inherit any
        // animation transforms set during the source mob's last setupAnim. Restore
        // afterwards because the model instance is shared across all entities of
        // the source type.
        var savedX = part.x;
        var savedY = part.y;
        var savedZ = part.z;
        var savedXRot = part.xRot;
        var savedYRot = part.yRot;
        var savedZRot = part.zRot;

        part.x = 0;
        part.y = 0;
        part.z = 0;
        part.xRot = 0;
        part.yRot = 0;
        part.zRot = 0;

        try {
            part.render(poseStack, vertexConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
        } finally {
            part.x = savedX;
            part.y = savedY;
            part.z = savedZ;
            part.xRot = savedXRot;
            part.yRot = savedYRot;
            part.zRot = savedZRot;
        }

        poseStack.popPose();
    }

    private static @Nullable ModelPart resolveModelPart(LivingEntity ghost, String partName) {
        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        var renderer = ((MixinEntityRenderDispatcher_Accessor) dispatcher).blib$getRenderers().get(ghost.getType());

        if (!(renderer instanceof LivingEntityRenderer<?, ?>)) {
            return null;
        }

        EntityModel<?> model = ((MixinLivingEntityRenderer_Accessor) renderer).blib$getModel();

        if (model == null) {
            return null;
        }

        return findModelPart(model, partName);
    }

    /**
     * Resolves a named {@link ModelPart} on a vanilla {@link EntityModel} via the {@link ModelPartResolverRegistry}.
     * Callers should pass the {@code LayerDefinition} child name (typically {@code snake_case}, e.g. {@code "head"} /
     * {@code "right_arm"}); the registered resolver for the model's class hierarchy is responsible for translating it
     * into a part reference.
     */
    public static @Nullable ModelPart findModelPart(EntityModel<?> model, String partName) {
        return ModelPartResolverRegistry.resolve(model, partName);
    }
}
