package org.avp.client.render.entity.living;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import mod.azure.azurelib.common.internal.common.cache.object.BakedGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.ChestbursterRunnerModel;
import org.avp.common.entity.living.ChestbursterRunner;

public class ChestbursterRunnerRenderer extends GeoEntityRenderer<ChestbursterRunner> {

    public ChestbursterRunnerRenderer(EntityRendererProvider.Context context) {
        super(context, new ChestbursterRunnerModel());
    }

    @Override
    public void preRender(
        PoseStack poseStack,
        ChestbursterRunner animatable,
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
        poseStack.scale(0.5F, 0.5F, 0.5F);
    }
}
