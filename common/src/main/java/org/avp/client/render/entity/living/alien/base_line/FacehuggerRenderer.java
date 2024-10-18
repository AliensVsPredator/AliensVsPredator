package org.avp.client.render.entity.living.alien.base_line;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import mod.azure.azurelib.common.internal.common.cache.object.BakedGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.entity.living.alien.base_line.Facehugger;

public class FacehuggerRenderer extends GeoEntityRenderer<Facehugger> {

    public FacehuggerRenderer(EntityRendererProvider.Context context) {
        super(context, new AVPGeoModel<>("facehugger", GeoModelType.ENTITY));
    }

    @Override
    public void preRender(
        PoseStack poseStack,
        Facehugger facehugger,
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
            facehugger,
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

        if (!facehugger.isFertile() && facehugger.getVehicle() == null) {
            poseStack.translate(0, animatable.getBbHeight() - 0.05f, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
        }
    }
}
