package com.blib.internal.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

import com.blib.api.client.render.v1.AzModelRenderer;
import com.blib.api.client.render.v1.AzRendererPipelineContext;
import com.blib.api.client.render.v1.item.pipeline.AzItemRendererPipelineContext;
import com.blib.internal.client.model.AzBone;
import com.blib.internal.client.render.util.RenderUtil;

public class AzItemArmRenderUtil {

    private static final String LEFT_ARM_BONE = "leftArm";

    private static final String RIGHT_ARM_BONE = "rightArm";

    public static boolean isArmBone(AzBone bone) {
        var name = bone.getName();
        return LEFT_ARM_BONE.equals(name) || RIGHT_ARM_BONE.equals(name);
    }

    public static boolean shouldRenderArmsForContext(AzItemRendererPipelineContext context) {
        var transformType = context.getTransformType();
        return transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
            transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
    }

    public static void renderArmForBone(
        AzRendererPipelineContext<UUID, ItemStack> context,
        AzBone bone,
        AzModelRenderer<UUID, ItemStack> modelRenderer
    ) {
        var itemContext = (AzItemRendererPipelineContext) context;

        // Only render if this is a first-person context
        if (!shouldRenderArmsForContext(itemContext)) {
            return;
        }

        // Hide the arm bone but keep children visible
        bone.setHidden(true);
        bone.setChildrenHidden(false);

        var client = Minecraft.getInstance();
        var poseStack = context.poseStack();
        var packedLight = context.packedLight();

        // Get player model and skin
        var playerEntityRenderer = (PlayerRenderer) client.getEntityRenderDispatcher().getRenderer(client.player);
        var playerEntityModel = playerEntityRenderer.getModel();
        var playerSkin = client.player.getSkin().texture();

        poseStack.pushPose();

        // Apply bone transformations
        RenderUtil.translateMatrixToBone(poseStack, bone);
        RenderUtil.translateToPivotPoint(poseStack, bone);
        RenderUtil.rotateMatrixAroundBone(poseStack, bone);
        RenderUtil.scaleMatrixForBone(poseStack, bone);
        RenderUtil.translateAwayFromPivotPoint(poseStack, bone);

        if (LEFT_ARM_BONE.equals(bone.getName())) {
            renderLeftArm(poseStack, bone, playerEntityModel, playerSkin, packedLight, itemContext, modelRenderer);
        } else if (RIGHT_ARM_BONE.equals(bone.getName())) {
            renderRightArm(poseStack, bone, playerEntityModel, playerSkin, packedLight, itemContext, modelRenderer);
        }

        poseStack.popPose();
    }

    private static void renderLeftArm(
        PoseStack poseStack,
        AzBone bone,
        net.minecraft.client.model.PlayerModel<?> playerEntityModel,
        net.minecraft.resources.ResourceLocation playerSkin,
        int packedLight,
        AzItemRendererPipelineContext itemContext,
        AzModelRenderer<UUID, ItemStack> modelRenderer
    ) {
        poseStack.scale(0.67f, 1.33f, 0.67f);
        poseStack.translate(-0.25, -0.43625, 0.1625);

        // Set up and render the left arm
        playerEntityModel.leftArm.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
        playerEntityModel.leftArm.setRotation(0, 0, 0);
        playerEntityModel.leftArm.render(
            poseStack,
            modelRenderer.getOrRefreshBufferRenderType(itemContext, bone, RenderType.entitySolid(playerSkin)),
            packedLight,
            OverlayTexture.NO_OVERLAY
        );

        // Set up and render a left sleeve
        playerEntityModel.leftSleeve.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
        playerEntityModel.leftSleeve.setRotation(0, 0, 0);
        playerEntityModel.leftSleeve.render(
            poseStack,
            modelRenderer.getOrRefreshBufferRenderType(itemContext, bone, RenderType.entityTranslucent(playerSkin)),
            packedLight,
            OverlayTexture.NO_OVERLAY
        );
    }

    private static void renderRightArm(
        PoseStack poseStack,
        AzBone bone,
        net.minecraft.client.model.PlayerModel<?> playerEntityModel,
        net.minecraft.resources.ResourceLocation playerSkin,
        int packedLight,
        AzItemRendererPipelineContext itemContext,
        AzModelRenderer<UUID, ItemStack> modelRenderer
    ) {
        poseStack.scale(0.67f, 1.33f, 0.67f);
        poseStack.translate(0.25, -0.43625, 0.1625);

        // Set up and render right arm
        playerEntityModel.rightArm.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
        playerEntityModel.rightArm.setRotation(0, 0, 0);
        playerEntityModel.rightArm.render(
            poseStack,
            modelRenderer.getOrRefreshBufferRenderType(itemContext, bone, RenderType.entitySolid(playerSkin)),
            packedLight,
            OverlayTexture.NO_OVERLAY
        );

        // Set up and render a right sleeve
        playerEntityModel.rightSleeve.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
        playerEntityModel.rightSleeve.setRotation(0, 0, 0);
        playerEntityModel.rightSleeve.render(
            poseStack,
            modelRenderer.getOrRefreshBufferRenderType(itemContext, bone, RenderType.entityTranslucent(playerSkin)),
            packedLight,
            OverlayTexture.NO_OVERLAY
        );
    }
}
