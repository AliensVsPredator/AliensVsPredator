package com.blib.api.client.render.v1.dismemberment;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.EnumSet;
import java.util.Set;

import com.blib.api.common.dismemberment.v1.LimbVisuals;
import com.blib.api.common.dismemberment.v1.entity.DismemberedLimbEntity;
import com.blib.internal.mixin.MixinEntityRenderDispatcher_Accessor;
import com.blib.internal.mixin.MixinLivingEntityRenderer_Accessor;

/**
 * Re-runs the source mob's {@link HumanoidArmorLayer} at a {@link DismemberedLimbEntity}'s pose so armor pieces follow
 * the body part they were attached to (helmet on a severed head, leggings on a severed leg, etc.).
 * <p>
 * Strategy: temporarily mask the entity model so only the body part matching the limb's {@code rootBoneName} is
 * visible, then call {@code armorLayer.render(...)} on the limb's ghost. Vanilla's armor layer iterates all four armor
 * slots; the visibility mask plus {@code MixinHumanoidArmorLayer_Dismemberment} (which mirrors parent visibility onto
 * the armor model) ensures only the slot whose parts cover the visible bone actually draws.
 */
public final class LimbArmorRenderer {

    private LimbArmorRenderer() {}

    public static void render(
        DismemberedLimbEntity limb,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight
    ) {
        var ghost = limb.getOrCreateGhost();

        if (ghost == null) {
            return;
        }

        var visuals = limb.resolveVisuals();

        if (visuals == null) {
            return;
        }

        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        var renderer = ((MixinEntityRenderDispatcher_Accessor) dispatcher).blib$getRenderers().get(ghost.getType());

        if (!(renderer instanceof LivingEntityRenderer<?, ?> living)) {
            return;
        }

        var model = ((MixinLivingEntityRenderer_Accessor) living).blib$getModel();

        if (!(model instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        var visibleParts = visiblePartsFor(visuals.rootBoneName());

        if (visibleParts.isEmpty()) {
            return;
        }

        var armorLayer = findArmorLayer(living);

        if (armorLayer == null) {
            return;
        }

        renderArmorAtLimbPose(limb, visuals, poseStack, bufferSource, packedLight, ghost, humanoidModel, armorLayer, visibleParts);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void renderArmorAtLimbPose(
        DismemberedLimbEntity limb,
        LimbVisuals visuals,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        LivingEntity ghost,
        HumanoidModel<?> humanoidModel,
        HumanoidArmorLayer armorLayer,
        Set<HumanoidPart> visibleParts
    ) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - limb.getYRot()));

        var renderRotation = visuals.renderRotation();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) renderRotation.z));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) renderRotation.y));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) renderRotation.x));

        var renderScale = visuals.renderScale();
        poseStack.scale((float) renderScale.x, (float) renderScale.y, (float) renderScale.z);

        var renderOffset = visuals.renderOffset();
        poseStack.translate(renderOffset.x, renderOffset.y, renderOffset.z);

        // Match LivingEntityRenderer.render's authored-upside-down handling so armor lines up with the bone.
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        // Snapshot visibility/pose for parts we're going to mutate, mask down to just the limb's part, then call the
        // armor layer once. The Bug 1 mixin ensures the armor model honors parent visibility, so non-matching slots
        // self-suppress.
        var snapshot = HumanoidPartSnapshot.capture(humanoidModel);

        try {
            for (var part : HumanoidPart.values()) {
                part.setVisible(humanoidModel, visibleParts.contains(part));
            }

            // Zero the local pose on visible parts so the limb fragment doesn't inherit residual animation state from
            // the source mob's last render frame.
            for (var part : visibleParts) {
                part.zeroPose(humanoidModel);
            }

            armorLayer.render(poseStack, bufferSource, packedLight, ghost, 0F, 0F, 0F, 0F, 0F, 0F);
        } finally {
            snapshot.restore(humanoidModel);
            poseStack.popPose();
        }
    }

    private static Set<HumanoidPart> visiblePartsFor(String rootBoneName) {
        return switch (rootBoneName) {
            // HEAD covers head + hat so the helmet's outer hat layer renders alongside the head.
            case "head" -> EnumSet.of(HumanoidPart.HEAD, HumanoidPart.HAT);
            case "body" -> EnumSet.of(HumanoidPart.BODY);
            case "right_arm" -> EnumSet.of(HumanoidPart.RIGHT_ARM);
            case "left_arm" -> EnumSet.of(HumanoidPart.LEFT_ARM);
            case "right_leg" -> EnumSet.of(HumanoidPart.RIGHT_LEG);
            case "left_leg" -> EnumSet.of(HumanoidPart.LEFT_LEG);
            default -> Set.of();
        };
    }

    private static HumanoidArmorLayer<?, ?, ?> findArmorLayer(LivingEntityRenderer<?, ?> renderer) {
        var layers = ((MixinLivingEntityRenderer_Accessor) renderer).blib$getLayers();

        for (var layer : layers) {
            if (layer instanceof HumanoidArmorLayer<?, ?, ?> armorLayer) {
                return armorLayer;
            }
        }

        return null;
    }

    private enum HumanoidPart {

        HEAD,
        HAT,
        BODY,
        RIGHT_ARM,
        LEFT_ARM,
        RIGHT_LEG,
        LEFT_LEG;

        void setVisible(HumanoidModel<?> model, boolean visible) {
            switch (this) {
                case HEAD -> model.head.visible = visible;
                case HAT -> model.hat.visible = visible;
                case BODY -> model.body.visible = visible;
                case RIGHT_ARM -> model.rightArm.visible = visible;
                case LEFT_ARM -> model.leftArm.visible = visible;
                case RIGHT_LEG -> model.rightLeg.visible = visible;
                case LEFT_LEG -> model.leftLeg.visible = visible;
            }
        }

        void zeroPose(HumanoidModel<?> model) {
            var part = switch (this) {
                case HEAD -> model.head;
                case HAT -> model.hat;
                case BODY -> model.body;
                case RIGHT_ARM -> model.rightArm;
                case LEFT_ARM -> model.leftArm;
                case RIGHT_LEG -> model.rightLeg;
                case LEFT_LEG -> model.leftLeg;
            };
            part.x = 0;
            part.y = 0;
            part.z = 0;
            part.xRot = 0;
            part.yRot = 0;
            part.zRot = 0;
        }
    }

    private record HumanoidPartSnapshot(
        boolean headVisible,
        boolean hatVisible,
        boolean bodyVisible,
        boolean rightArmVisible,
        boolean leftArmVisible,
        boolean rightLegVisible,
        boolean leftLegVisible,
        float headX,
        float headY,
        float headZ,
        float headXRot,
        float headYRot,
        float headZRot,
        float hatX,
        float hatY,
        float hatZ,
        float hatXRot,
        float hatYRot,
        float hatZRot,
        float bodyX,
        float bodyY,
        float bodyZ,
        float bodyXRot,
        float bodyYRot,
        float bodyZRot,
        float rArmX,
        float rArmY,
        float rArmZ,
        float rArmXRot,
        float rArmYRot,
        float rArmZRot,
        float lArmX,
        float lArmY,
        float lArmZ,
        float lArmXRot,
        float lArmYRot,
        float lArmZRot,
        float rLegX,
        float rLegY,
        float rLegZ,
        float rLegXRot,
        float rLegYRot,
        float rLegZRot,
        float lLegX,
        float lLegY,
        float lLegZ,
        float lLegXRot,
        float lLegYRot,
        float lLegZRot
    ) {

        static HumanoidPartSnapshot capture(HumanoidModel<?> m) {
            return new HumanoidPartSnapshot(
                m.head.visible,
                m.hat.visible,
                m.body.visible,
                m.rightArm.visible,
                m.leftArm.visible,
                m.rightLeg.visible,
                m.leftLeg.visible,
                m.head.x,
                m.head.y,
                m.head.z,
                m.head.xRot,
                m.head.yRot,
                m.head.zRot,
                m.hat.x,
                m.hat.y,
                m.hat.z,
                m.hat.xRot,
                m.hat.yRot,
                m.hat.zRot,
                m.body.x,
                m.body.y,
                m.body.z,
                m.body.xRot,
                m.body.yRot,
                m.body.zRot,
                m.rightArm.x,
                m.rightArm.y,
                m.rightArm.z,
                m.rightArm.xRot,
                m.rightArm.yRot,
                m.rightArm.zRot,
                m.leftArm.x,
                m.leftArm.y,
                m.leftArm.z,
                m.leftArm.xRot,
                m.leftArm.yRot,
                m.leftArm.zRot,
                m.rightLeg.x,
                m.rightLeg.y,
                m.rightLeg.z,
                m.rightLeg.xRot,
                m.rightLeg.yRot,
                m.rightLeg.zRot,
                m.leftLeg.x,
                m.leftLeg.y,
                m.leftLeg.z,
                m.leftLeg.xRot,
                m.leftLeg.yRot,
                m.leftLeg.zRot
            );
        }

        void restore(HumanoidModel<?> m) {
            m.head.visible = headVisible;
            m.hat.visible = hatVisible;
            m.body.visible = bodyVisible;
            m.rightArm.visible = rightArmVisible;
            m.leftArm.visible = leftArmVisible;
            m.rightLeg.visible = rightLegVisible;
            m.leftLeg.visible = leftLegVisible;
            m.head.x = headX;
            m.head.y = headY;
            m.head.z = headZ;
            m.head.xRot = headXRot;
            m.head.yRot = headYRot;
            m.head.zRot = headZRot;
            m.hat.x = hatX;
            m.hat.y = hatY;
            m.hat.z = hatZ;
            m.hat.xRot = hatXRot;
            m.hat.yRot = hatYRot;
            m.hat.zRot = hatZRot;
            m.body.x = bodyX;
            m.body.y = bodyY;
            m.body.z = bodyZ;
            m.body.xRot = bodyXRot;
            m.body.yRot = bodyYRot;
            m.body.zRot = bodyZRot;
            m.rightArm.x = rArmX;
            m.rightArm.y = rArmY;
            m.rightArm.z = rArmZ;
            m.rightArm.xRot = rArmXRot;
            m.rightArm.yRot = rArmYRot;
            m.rightArm.zRot = rArmZRot;
            m.leftArm.x = lArmX;
            m.leftArm.y = lArmY;
            m.leftArm.z = lArmZ;
            m.leftArm.xRot = lArmXRot;
            m.leftArm.yRot = lArmYRot;
            m.leftArm.zRot = lArmZRot;
            m.rightLeg.x = rLegX;
            m.rightLeg.y = rLegY;
            m.rightLeg.z = rLegZ;
            m.rightLeg.xRot = rLegXRot;
            m.rightLeg.yRot = rLegYRot;
            m.rightLeg.zRot = rLegZRot;
            m.leftLeg.x = lLegX;
            m.leftLeg.y = lLegY;
            m.leftLeg.z = lLegZ;
            m.leftLeg.xRot = lLegXRot;
            m.leftLeg.yRot = lLegYRot;
            m.leftLeg.zRot = lLegZRot;
        }
    }
}
