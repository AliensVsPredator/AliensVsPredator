package org.avp.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import mod.azure.azurelib.common.internal.common.cache.object.BakedGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import org.avp.client.model.entity.RocketModel;
import org.avp.common.game.entity.Rocket;

public class RocketRenderer extends GeoEntityRenderer<Rocket> {

    public RocketRenderer(EntityRendererProvider.Context context) {
        super(context, new RocketModel());
    }

    @Override
    public void actuallyRender(
        PoseStack poseStack,
        Rocket animatable,
        BakedGeoModel model,
        RenderType renderType,
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
        var level = animatable.level();
        var pos = animatable.position();
        var posX = pos.x;
        var posY = pos.y;
        var eyeHeight = animatable.getEyeHeight();
        var posZ = pos.z;

        level.addAlwaysVisibleParticle(ParticleTypes.SMOKE, posX, posY + eyeHeight, posZ, 0.0D, 0.0D, 0.0D);
        level.addAlwaysVisibleParticle(ParticleTypes.SMALL_FLAME, posX, posY + eyeHeight, posZ, 0.0D, 0.0D, 0.0D);

        poseStack.mulPose(Axis.YP.rotationDegrees(animatable.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(-animatable.getXRot()));
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
