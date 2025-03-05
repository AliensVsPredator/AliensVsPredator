package com.avp.core.client.render.entity.parasite.facehugger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.azure.azurelib.rewrite.render.AzLayerRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityModelRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererPipeline;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import com.avp.core.client.render.entity.parasite.EntityHeadData;
import com.avp.core.client.render.entity.parasite.EntityHeadOffsetData;
import com.avp.core.common.entity.living.alien.parasite.facehugger.Facehugger;

public class FacehuggerModelRenderer extends AzEntityModelRenderer<Facehugger> {

    public FacehuggerModelRenderer(AzEntityRendererPipeline<Facehugger> entityRendererPipeline, AzLayerRenderer<Facehugger> layerRenderer) {
        super(entityRendererPipeline, layerRenderer);
    }

    @Override
    protected void applyRotations(
        Facehugger facehugger,
        PoseStack poseStack,
        float ageInTicks,
        float rotationYaw,
        float partialTick,
        float nativeScale
    ) {
        if (!facehugger.attachmentManager().isAttachedToHost()) {
            super.applyRotations(facehugger, poseStack, ageInTicks, rotationYaw, partialTick, 1);
            return;
        }

        var host = (LivingEntity) facehugger.getVehicle();

        if (host == null) {
            return;
        }

        var data = EntityHeadData.ENTITY_HEAD_DATA_BY_TYPE.get(host.getType());

        if (data == null) {
            return;
        }

        applyHuggingRotations(facehugger, poseStack, partialTick, host, data);
    }

    private void applyHuggingRotations(
        Facehugger facehugger,
        PoseStack poseStack,
        float partialTick,
        LivingEntity host,
        EntityHeadData data
    ) {
        var bodyYaw = Mth.rotLerp(partialTick, host.yBodyRotO, host.yBodyRot);
        var headYaw = Mth.rotLerp(partialTick, host.yHeadRotO, host.yHeadRot) - bodyYaw;
        var headPitch = Mth.rotLerp(partialTick, host.getXRot(), host.xRotO);

        var xPivot = data.pivot().x;
        var yPivot = data.pivot().y;
        var zPivot = data.pivot().z;
        var ySize = data.size().y;
        var zSize = data.size().z;

        // translate head-center
        poseStack.mulPose(Axis.YN.rotationDegrees(bodyYaw));

        // yaw
        poseStack.translate(xPivot, yPivot - host.getBbHeight(), -zPivot);
        poseStack.mulPose(Axis.YN.rotationDegrees(headYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(headPitch));
        poseStack.translate(-xPivot, -yPivot + host.getBbHeight(), zPivot);

        var offsetSuppliers = EntityHeadOffsetData.ENTITY_HEAD_OFFSET_DATA_BY_TYPE.get(host.getType());

        if (offsetSuppliers != null) {
            var yOffset = offsetSuppliers.verticalOffsetSupplier().apply(data, facehugger);
            var zOffset = offsetSuppliers.faceOffsetSupplier().apply(data, facehugger);
            poseStack.translate(0, yOffset, zOffset);
        } else {
            poseStack.translate(0, -ySize, zSize);
        }
    }
}
