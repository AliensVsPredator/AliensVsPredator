package com.blib.api.client.render.v1.dismemberment;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.dismemberment.v1.LimbVisuals;
import com.blib.api.common.dismemberment.v1.entity.DismemberedLimbEntity;
import com.blib.internal.mixin.MixinEntityRenderDispatcher_Accessor;
import com.blib.internal.mixin.MixinLivingEntityRenderer_Accessor;

/**
 * Re-runs the source mob's {@link ItemInHandLayer} at a {@link DismemberedLimbEntity}'s pose so a held item (bow,
 * sword, crossbow, etc.) follows the dismembered arm rather than vanishing or floating where the source's arm used to
 * be. Companion to {@link com.blib.internal.mixin.MixinItemInHandLayer_Dismemberment}, which suppresses the held-item
 * render on the source body once the arm is detached.
 * <p>
 * Strategy: temporarily mask the source model so only the limb's arm is visible (and zeroed for pose), then call
 * {@code itemInHandLayer.render(...)} on the limb's ghost. Vanilla's layer iterates both arms; the
 * {@code MixinItemInHandLayer_Dismemberment} skip kicks in for the hidden one, so only the limb's arm draws its item.
 */
public final class LimbHeldItemRenderer {

    private LimbHeldItemRenderer() {}

    public static void render(
        DismemberedLimbEntity limb,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight
    ) {
        var visuals = limb.resolveVisuals();

        if (visuals == null) {
            return;
        }

        var renderArm = armForBone(visuals.rootBoneName());

        if (renderArm == null) {
            return;
        }

        var ghost = limb.getOrCreateGhost();

        if (ghost == null) {
            return;
        }

        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        var renderer = ((MixinEntityRenderDispatcher_Accessor) dispatcher).blib$getRenderers().get(ghost.getType());

        if (!(renderer instanceof LivingEntityRenderer<?, ?> living)) {
            return;
        }

        var model = ((MixinLivingEntityRenderer_Accessor) living).blib$getModel();

        if (!(model instanceof ArmedModel)) {
            return;
        }

        var rightArm = ModelPartResolverRegistry.resolve(model, "right_arm");
        var leftArm = ModelPartResolverRegistry.resolve(model, "left_arm");

        if (rightArm == null || leftArm == null) {
            return;
        }

        var itemLayer = findItemInHandLayer(living);

        if (itemLayer == null) {
            return;
        }

        renderItemAtLimbPose(limb, visuals, poseStack, bufferSource, packedLight, ghost, itemLayer, renderArm, rightArm, leftArm);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void renderItemAtLimbPose(
        DismemberedLimbEntity limb,
        LimbVisuals visuals,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        LivingEntity ghost,
        ItemInHandLayer itemLayer,
        HumanoidArm renderArm,
        ModelPart rightArm,
        ModelPart leftArm
    ) {
        var rightSnapshot = ArmSnapshot.capture(rightArm);
        var leftSnapshot = ArmSnapshot.capture(leftArm);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - limb.getYRot()));

        var selectedPose = visuals.poseOrDefault(limb.getPoseId());
        var renderOffset = selectedPose.renderOffset();
        var renderRotation = selectedPose.renderRotation();
        var renderScale = selectedPose.renderScale();
        if (selectedPose.modelerTransform()) {
            var renderPivot = selectedPose.renderPivot();
            poseStack.translate(renderOffset.x, renderOffset.y, renderOffset.z);
            poseStack.translate(renderPivot.x, renderPivot.y, renderPivot.z);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) renderRotation.z));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) renderRotation.y));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) renderRotation.x));
            poseStack.scale((float) renderScale.x, (float) renderScale.y, (float) renderScale.z);
            poseStack.translate(-renderPivot.x, -renderPivot.y, -renderPivot.z);
        } else {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) renderRotation.z));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) renderRotation.y));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) renderRotation.x));
            poseStack.scale((float) renderScale.x, (float) renderScale.y, (float) renderScale.z);
            poseStack.translate(renderOffset.x, renderOffset.y, renderOffset.z);
        }

        // Match LivingEntityRenderer.render's authored-upside-down handling so the item lines up with the arm.
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        try {
            // Hide the OTHER arm so MixinItemInHandLayer_Dismemberment skips its item, leaving only the limb's arm
            // to render its held item.
            if (renderArm == HumanoidArm.RIGHT) {
                leftArm.visible = false;
                rightArm.visible = true;
                zeroPose(rightArm);
            } else {
                rightArm.visible = false;
                leftArm.visible = true;
                zeroPose(leftArm);
            }

            itemLayer.render(poseStack, bufferSource, packedLight, ghost, 0F, 0F, 0F, 0F, 0F, 0F);
        } finally {
            rightSnapshot.restore(rightArm);
            leftSnapshot.restore(leftArm);
            poseStack.popPose();
        }
    }

    private static @Nullable HumanoidArm armForBone(@Nullable String rootBoneName) {
        if ("right_arm".equals(rootBoneName)) {
            return HumanoidArm.RIGHT;
        }

        if ("left_arm".equals(rootBoneName)) {
            return HumanoidArm.LEFT;
        }

        return null;
    }

    private static ItemInHandLayer<?, ?> findItemInHandLayer(LivingEntityRenderer<?, ?> renderer) {
        var layers = ((MixinLivingEntityRenderer_Accessor) renderer).blib$getLayers();

        for (var layer : layers) {
            if (layer instanceof ItemInHandLayer<?, ?> itemLayer) {
                return itemLayer;
            }
        }

        return null;
    }

    private static void zeroPose(ModelPart part) {
        part.x = 0;
        part.y = 0;
        part.z = 0;
        part.xRot = 0;
        part.yRot = 0;
        part.zRot = 0;
    }

    private record ArmSnapshot(
        boolean visible,
        float x,
        float y,
        float z,
        float xRot,
        float yRot,
        float zRot
    ) {

        static ArmSnapshot capture(ModelPart part) {
            return new ArmSnapshot(part.visible, part.x, part.y, part.z, part.xRot, part.yRot, part.zRot);
        }

        void restore(ModelPart part) {
            part.visible = visible;
            part.x = x;
            part.y = y;
            part.z = z;
            part.xRot = xRot;
            part.yRot = yRot;
            part.zRot = zRot;
        }
    }
}
