package org.avp.client.render.entity.living;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import mod.azure.azurelib.common.internal.common.cache.object.BakedGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.DeaconAdultEngineerModel;
import org.avp.common.entity.living.DeaconAdultEngineer;

public class DeaconAdultEngineerRenderer extends GeoEntityRenderer<DeaconAdultEngineer> {

    public DeaconAdultEngineerRenderer(EntityRendererProvider.Context context) {
        super(context, new DeaconAdultEngineerModel());
    }

    @Override
    public void preRender(
        PoseStack poseStack,
        DeaconAdultEngineer animatable,
        BakedGeoModel model,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        float red,
        float green,
        float blue,
        float alpha
    ) {
        super.preRender(
            poseStack,
            animatable,
            model,
            bufferSource,
            buffer,
            isReRender,
            partialTick,
            packedLight,
            packedOverlay,
            red,
            green,
            blue,
            alpha
        );
        poseStack.scale(0.75F, 0.75F, 0.75F);
    }
}
