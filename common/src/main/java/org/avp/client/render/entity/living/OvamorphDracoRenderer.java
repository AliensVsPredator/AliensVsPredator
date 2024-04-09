package org.avp.client.render.entity.living;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import mod.azure.azurelib.common.internal.common.cache.object.BakedGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.OvamorphDracoModel;
import org.avp.common.entity.living.OvamorphDraco;

public class OvamorphDracoRenderer extends GeoEntityRenderer<OvamorphDraco> {

    public OvamorphDracoRenderer(EntityRendererProvider.Context context) {
        super(context, new OvamorphDracoModel());
    }

    @Override
    public void preRender(
        PoseStack poseStack,
        OvamorphDraco animatable,
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
        poseStack.scale(1.75F, 1.75F, 1.75F);
    }
}
